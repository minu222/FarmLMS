package com.lms.urbangreen.urbangreenproject.teacher.service;

import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.xml.sax.helpers.DefaultHandler;
import com.lms.urbangreen.urbangreenproject.lecture.service.GcsService;
import com.lms.urbangreen.urbangreenproject.lecture.video.entity.Video;
import com.lms.urbangreen.urbangreenproject.teacher.domain.LectureVideo;
import com.lms.urbangreen.urbangreenproject.teacher.quiz.dto.VideoQuizSummary;
import com.lms.urbangreen.urbangreenproject.teacher.repository.LectureVideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class LectureVideoService {

    private record VideoData(String title, MultipartFile file) {
    }

    private final LectureVideoRepository lectureVideoRepository;
    private final GcsService gcsService;

    public LectureVideoService(LectureVideoRepository lectureVideoRepository, GcsService gcsService) {
        this.lectureVideoRepository = lectureVideoRepository;
        this.gcsService = gcsService;
    }

    public List<VideoQuizSummary> getVideoSummariesForLecture(Long lectureId) {
        return lectureVideoRepository.findVideoSummariesByLectureId(lectureId);
    }

    public LectureVideo getVideo(Long videoId) {
        return lectureVideoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("영상 정보를 찾을 수 없습니다. videoId=" + videoId));
    }

    public void uploadVideos(int lectureId, List<String> titles, List<MultipartFile> files) throws IOException {

        // 1. 초기 null 체크 및 유효성 확인
        if (files == null || files.isEmpty()) {
            // 파일을 받지 못했거나 빈 리스트인 경우 (이 경우 400은 아닐 수 있지만 안전하게 처리)
            throw new IllegalArgumentException("업로드할 유효한 영상 파일이 없습니다.");
        }

        // 2. 제목 리스트 초기화 (null 방지)
        List<String> videoTitles = (titles != null) ? titles : List.of();

        // 3. 인덱스를 사용하여 유효한 (제목, 파일) 쌍만 필터링
        List<VideoData> validData = new java.util.ArrayList<>();
        int maxIndex = files.size();

        for (int i = 0; i < maxIndex; i++) {
            MultipartFile file = files.get(i);
            String title = (i < videoTitles.size() && videoTitles.get(i) != null)
                    ? videoTitles.get(i).trim()
                    : "";

            // 파일이 존재하고(null 아님), 내용이 있으며(isEmpty 아님), 제목도 비어있지 않은 경우에만 처리
            if (file != null && !file.isEmpty() && !title.isEmpty()) {
                validData.add(new VideoData(title, file));
            }
        }

        // 4. 처리할 유효한 데이터가 있는지 최종 확인
        if (validData.isEmpty()) {
            throw new IllegalArgumentException("업로드할 영상과 제목이 모두 유효한 쌍이 없습니다.");
        }

        // 5. 유효한 데이터만 GCS 업로드 및 DB 저장
        for (VideoData data : validData) {

            File tempVideoFile = null; // 루프 내에서 임시 파일 선언

            try {
                // 1. MultipartFile을 로컬 임시 파일로 저장
                tempVideoFile = convertMultipartFileToFile(data.file());

                // 2. ✅ 비디오 길이 추출 로직 추가
                int videoDuration = getVideoDurationInSeconds(tempVideoFile);

                // 3. GCS에 비디오 업로드 (tempVideoFile 대신 원본 data.file()을 사용해도 무방)
                String videoUrl = gcsService.uploadFile(data.file(), "videos");

                // 4. 엔티티 생성 및 DB 저장
                Video video = new Video();
                video.setLecture_id(lectureId);
                video.setVideo_title(data.title());
                video.setVideo_url(videoUrl);

                // 👇 [핵심] 추출된 비디오 길이를 저장
                video.setVideo_time(videoDuration);

                lectureVideoRepository.save(video);
                log.info("Video saved: lectureId={}, title={}, duration={}s", lectureId, data.title(), videoDuration);

            } finally {
                // 5. [핵심] try-finally 구문을 사용하여 임시 파일 삭제를 보장
                if (tempVideoFile != null) {
                    tempVideoFile.delete();
                }
            }
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        // 임시 파일 경로: 시스템의 기본 임시 디렉토리 사용
        Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + "_" + file.getOriginalFilename());

        // MultipartFile의 내용을 임시 파일로 복사
        Files.copy(file.getInputStream(), tempPath);
        return tempPath.toFile();
    }

    /**
     * JCodec을 사용하여 비디오 파일의 총 재생 시간을 초 단위로 계산합니다.
     */
    public int getVideoDurationInSeconds(File videoFile) {
        FileChannelWrapper ch = null;
        try {
            // File 객체를 사용하여 채널 열기
            ch = NIOUtils.readableFileChannel(videoFile.getAbsolutePath());
            MP4Demuxer demuxer = MP4Demuxer.createMP4Demuxer(ch);

            MovieBox moov = demuxer.getMovie();

            if (moov != null) {
                long duration = moov.getDuration();
                long timeScale = moov.getTimescale();

                if (timeScale > 0) {
                    return (int) (duration / timeScale);
                }
            }
        } catch (IOException e) {
            log.error("JCodec 비디오 길이 측정 중 오류 발생: {}", e.getMessage());
        } finally {
            NIOUtils.closeQuietly(ch);
        }
        return 0;
    }

    // 강의 ID로 모든 비디오 목록 조회
    public List<Video> getVideosByLectureId(int lectureId) {
        return lectureVideoRepository.findByLectureId(lectureId);
    }

    // 비디오 수정, 추가, 삭제를 한 번에 처리하는 통합 로직
    public void updateVideos(int lectureId, List<Long> videoIds, List<String> videoTitles,
                             List<MultipartFile> videoFiles, List<String> existingUrls) throws IOException {

        // 1. 기존 비디오 목록을 조회하여 Map으로 변환 (빠른 비교를 위해)
        List<Video> existingVideos = lectureVideoRepository.findByLectureId(lectureId);
        java.util.Set<Long> videosToKeep = new java.util.HashSet<>();

        // 2. 입력된 데이터 순회하며 처리
        int inputCount = videoTitles != null ? videoTitles.size() : 0;

        for (int i = 0; i < inputCount; i++) {
            Long videoId = (videoIds != null && i < videoIds.size()) ? videoIds.get(i) : null;
            String title = (videoTitles != null && i < videoTitles.size()) ? videoTitles.get(i) : null;
            MultipartFile file = (videoFiles != null && i < videoFiles.size()) ? videoFiles.get(i) : null;
            String existingUrl = (existingUrls != null && i < existingUrls.size()) ? existingUrls.get(i) : null;

            // 제목이 없으면 (폼에서 삭제된 것으로 간주) 건너뜁니다.
            if (title == null || title.trim().isEmpty()) {
                continue;
            }

            // 기존 비디오 (수정)
            if (videoId != null && videoId > 0) {
                videosToKeep.add(videoId);

                Video videoToUpdate = existingVideos.stream()
                        .filter(v -> v.getVideo_id() == videoId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 비디오 ID: " + videoId));

                // 제목 수정
                videoToUpdate.setVideo_title(title);

                // 파일 교체 로직
                if (file != null && !file.isEmpty()) {
                    // 1. 기존 GCS 파일 삭제
                    if (videoToUpdate.getVideo_url() != null) {
                        gcsService.deleteFile(videoToUpdate.getVideo_url());
                    }

                    File tempVideoFile = null;
                    try {
                        // 2. 새 파일 길이 측정
                        tempVideoFile = convertMultipartFileToFile(file);
                        int duration = getVideoDurationInSeconds(tempVideoFile);

                        // 3. 새 파일 GCS 업로드 및 DB 업데이트
                        String newVideoUrl = gcsService.uploadFile(file, "videos");

                        videoToUpdate.setVideo_url(newVideoUrl);
                        videoToUpdate.setVideo_time(duration);

                    } finally {
                        if (tempVideoFile != null) tempVideoFile.delete();
                    }
                } else {
                    // 파일 교체 없이 기존 URL 유지 (Hidden 필드 값 사용)
                    videoToUpdate.setVideo_url(existingUrl);
                    // video_time은 변경되지 않음
                }

                lectureVideoRepository.update(videoToUpdate);

            } else {
                // 새로운 비디오 (추가) - 등록 로직과 동일
                if (file != null && !file.isEmpty()) {
                    File tempVideoFile = null;
                    try {
                        tempVideoFile = convertMultipartFileToFile(file);
                        int duration = getVideoDurationInSeconds(tempVideoFile);

                        String videoUrl = gcsService.uploadFile(file, "videos");

                        Video newVideo = new Video();
                        newVideo.setLecture_id(lectureId);
                        newVideo.setVideo_title(title);
                        newVideo.setVideo_url(videoUrl);
                        newVideo.setVideo_time(duration);

                        lectureVideoRepository.save(newVideo);
                    } finally {
                        if (tempVideoFile != null) tempVideoFile.delete();
                    }
                }
                // 새로운 비디오인데 파일이 없으면 무시
            }
        }

        // 3. 기존 비디오 중 유지 목록에 없는 항목 삭제 (DB 및 GCS 파일 삭제)
        for (Video video : existingVideos) {
            if (!videosToKeep.contains(video.getVideo_id())) {
                log.info("Deleting video: ID={}, URL={}", video.getVideo_id(), video.getVideo_url());
                // GCS 파일 삭제
                if (video.getVideo_url() != null) {
                    gcsService.deleteFile(video.getVideo_url());
                }
                // DB 레코드 삭제
                lectureVideoRepository.deleteById((long) video.getVideo_id());
            }
        }
    }
}