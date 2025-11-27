package com.lms.urbangreen.urbangreenproject.teacher.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TeacherLectureCommentsDto {

    /** 내가 작성한 댓글(q.qna_id) */
    private int id;

    /** 내가 작성한 댓글의 작성자 이름(= 나) */
    private String authorName;

    /** 내가 작성한 댓글 내용 */
    private String content;

    /** 내가 작성한 댓글 작성 시간 */
    private LocalDateTime createdAt;

    /** 부모 댓글 ID (있으면: 어떤 댓글에 답글 달았는지 표시용) */
    private Integer parentId;

    /** 부모 댓글 작성자 이름 (학생 등) */
    private String parentAuthorName;

    /** 부모 댓글 내용 */
    private String parentContent;

    /** 부모 댓글 작성 시간 */
    private LocalDateTime parentCreatedAt;

    /** 내 댓글에 달린 추가 답글 목록 */
    private List<TeacherReplyDto> replies = new ArrayList<>();
}
