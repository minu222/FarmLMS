package com.lms.urbangreen.urbangreenproject.lecture.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class GcsService {

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    @Value("${gcp.storage.project-id}")
    private String projectId;

    @Value("${gcp.storage.key-file}")
    private String keyFilePath;
    private Storage storage;

    // 서비스 계정 키 파일을 사용하여 GCS Storage 객체 초기화
    @PostConstruct
    public void init() throws IOException {
        log.info("GCS Storage initialization started.");

        // ResourceLoader를 통해 키 파일 로드 (클래스패스에서 읽어옴)
        java.io.InputStream keyFileStream = getClass().getClassLoader().getResourceAsStream(keyFilePath.replace("classpath:", ""));

        if (keyFileStream == null) {
            log.error("GCS key file not found at: {}", keyFilePath);
            throw new IOException("GCS key file not found");
        }

        // 키 파일 스트림을 사용하여 Storage 객체 생성
        this.storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(keyFileStream))
                .build()
                .getService();

        log.info("GCS Storage initialized successfully. Bucket: {}", bucketName);
    }

    /**
     * 파일을 GCS에 업로드하고 공개 URL을 반환합니다.
     * @param file 업로드할 파일
     * @param folderName 버킷 내의 폴더 경로 (예: "videos", "thumbnails")
     * @return 파일의 공개 URL
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String storedFileName = folderName + "/" + UUID.randomUUID() + "-" + originalFileName;

        BlobInfo blobInfo = storage.create(
                BlobInfo.newBuilder(bucketName, storedFileName)
                        .setContentType(file.getContentType())
                        .build(),
                file.getInputStream()
        );

        // 공개 URL 형식: https://storage.googleapis.com/BUCKET_NAME/OBJECT_NAME
        return "https://storage.googleapis.com/" + bucketName + "/" + storedFileName;
    }

    /**
     * GCS에서 파일을 삭제합니다.
     * @param fileUrl 삭제할 파일의 전체 URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            // URL에서 버킷 이름과 객체 이름(파일 경로) 추출
            String path = fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);

            boolean deleted = storage.delete(bucketName, path);
            if (deleted) {
                log.info("GCS file deleted successfully: {}", path);
            } else {
                log.warn("GCS file not found or deletion failed: {}", path);
            }
        } catch (Exception e) {
            log.error("Error deleting file from GCS: {}", fileUrl, e);
        }
    }
}
