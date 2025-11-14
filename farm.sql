-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        8.4.5 - MySQL Community Server - GPL
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- farm 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `farm` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `farm`;

-- 테이블 farm.all_users 구조 내보내기
CREATE TABLE IF NOT EXISTS `all_users` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '모든유저 ID',
  `user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '관리자,강사,일반유저',
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '아이디',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '비밀번호',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '실명',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '닉네임',
  `birth` date NOT NULL COMMENT '생년월일',
  `email` varchar(50) NOT NULL COMMENT '이메일',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `nickName` (`nickname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='모든 사용자(관리자, 강사, 일반유저)';

-- 테이블 데이터 farm.all_users:~0 rows (대략적) 내보내기

-- 테이블 farm.category 구조 내보내기
CREATE TABLE IF NOT EXISTS `category` (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '카테고리 ID',
  `category_type` varchar(50) NOT NULL COMMENT '카테고리 타입(퀴즈,강의,공지사항 등)',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '강의 시 강의 이름/퀴즈 시 퀴즈 제목',
  `category_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '카테고리에 쓰일 이미지',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='카테고리';

-- 테이블 데이터 farm.category:~0 rows (대략적) 내보내기

-- 테이블 farm.coupon 구조 내보내기
CREATE TABLE IF NOT EXISTS `coupon` (
  `coupon_id` int NOT NULL AUTO_INCREMENT COMMENT '쿠폰 ID',
  `coupon_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '쿠폰 코드',
  `coupon_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '쿠폰 이름',
  `dc_value` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '할인율(0.10=10%)',
  `valid_until` timestamp NOT NULL COMMENT '쿠폰만료 시간',
  PRIMARY KEY (`coupon_id`),
  CONSTRAINT `chk_dc_value_range` CHECK (((`dc_value` >= 0.00) and (`dc_value` <= 1.00)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='쿠폰';

-- 테이블 데이터 farm.coupon:~0 rows (대략적) 내보내기

-- 테이블 farm.free 구조 내보내기
CREATE TABLE IF NOT EXISTS `free` (
  `free_id` int NOT NULL AUTO_INCREMENT COMMENT '자유게시판 ID',
  `user_id` int NOT NULL COMMENT '모든 유저 ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '게시글 제목',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '게시글 내용',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
  PRIMARY KEY (`free_id`),
  KEY `FK_all_users` (`user_id`) USING BTREE,
  CONSTRAINT `FK__all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='자유게시판';

-- 테이블 데이터 farm.free:~0 rows (대략적) 내보내기

-- 테이블 farm.free_cmt 구조 내보내기
CREATE TABLE IF NOT EXISTS `free_cmt` (
  `cmt_id` int NOT NULL AUTO_INCREMENT COMMENT '자유게시판 댓글 ID',
  `free_id` int NOT NULL COMMENT '자유게시판 ID',
  `user_id` int NOT NULL COMMENT '모든 유저 ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '댓글 내용',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
  PRIMARY KEY (`cmt_id`) USING BTREE,
  KEY `FK_free` (`free_id`) USING BTREE,
  KEY `FK_free_cmt_all_users` (`user_id`) USING BTREE,
  CONSTRAINT `FK__free_board` FOREIGN KEY (`free_id`) REFERENCES `free` (`free_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_free_board_cmt_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='자유게시판의 댓글';

-- 테이블 데이터 farm.free_cmt:~0 rows (대략적) 내보내기

-- 테이블 farm.free_reply 구조 내보내기
CREATE TABLE IF NOT EXISTS `free_reply` (
  `reply_id` int NOT NULL AUTO_INCREMENT COMMENT '자유게시판 답글 ID',
  `cmt_id` int DEFAULT NULL COMMENT '답글의 상위 댓글 ID',
  `user_id` int DEFAULT NULL COMMENT '모든 유저 ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '답글 내용',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
  PRIMARY KEY (`reply_id`),
  KEY `FK_free_reply_all_users` (`user_id`),
  KEY `FK_free_reply_free_cmt` (`cmt_id`),
  CONSTRAINT `FK_free_reply_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_free_reply_free_cmt` FOREIGN KEY (`cmt_id`) REFERENCES `free_cmt` (`cmt_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='자유게시판 답글';

-- 테이블 데이터 farm.free_reply:~0 rows (대략적) 내보내기

-- 테이블 farm.game_item 구조 내보내기
CREATE TABLE IF NOT EXISTS `game_item` (
  `item_id` int NOT NULL AUTO_INCREMENT COMMENT '아이템 ID',
  `item_type` varchar(50) NOT NULL COMMENT '아이템 타입',
  `item_img` varchar(255) DEFAULT NULL COMMENT '아이템 이미지',
  `item_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '아이템 이름',
  `item_desc` text COMMENT '아이템 설명',
  `item_price` int NOT NULL COMMENT '아이템 가격(포인트)',
  `is_available` enum('구입가능','판매됨') DEFAULT '구입가능' COMMENT '구입가능 여부',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '등록시간',
  PRIMARY KEY (`item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게임내 아이템';

-- 테이블 데이터 farm.game_item:~0 rows (대략적) 내보내기

-- 테이블 farm.game_record 구조 내보내기
CREATE TABLE IF NOT EXISTS `game_record` (
  `game_id` int NOT NULL AUTO_INCREMENT COMMENT '게임기록 ID',
  `user_id` int NOT NULL COMMENT '일반유저 ID',
  `point_id` int NOT NULL COMMENT '포인트 ID',
  `crop_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '작물 이름',
  `crop_hp` int DEFAULT '100' COMMENT '작물 체력',
  `day` int DEFAULT '0' COMMENT '경과 일수',
  `game_status` enum('진행중','클리어','게임오버') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '진행중' COMMENT '게임 상태',
  `event_type` varchar(50) DEFAULT NULL COMMENT '이벤트 이름',
  `started_at` timestamp NULL DEFAULT (now()) COMMENT '시작시간',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '완료시간',
  PRIMARY KEY (`game_id`),
  KEY `FK_game_record_all_users` (`user_id`),
  KEY `FK_game_record_point` (`point_id`),
  CONSTRAINT `FK_game_record_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_game_record_point` FOREIGN KEY (`point_id`) REFERENCES `point` (`point_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게임 기록';

-- 테이블 데이터 farm.game_record:~0 rows (대략적) 내보내기

-- 테이블 farm.lecture 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture` (
  `lecture_id` int NOT NULL AUTO_INCREMENT COMMENT '강의 ID',
  `category_id` int NOT NULL COMMENT '강의 카테고리 ID',
  `user_id` int NOT NULL COMMENT '강사 ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '제목',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '강의 요약',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '강의 영상',
  `price` int DEFAULT NULL COMMENT '구독료',
  `subs_count` int DEFAULT '0' COMMENT '구독자수(직접기입X)',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
  PRIMARY KEY (`lecture_id`),
  KEY `FK_lecture_all_users` (`user_id`) USING BTREE,
  KEY `FK_lecture_lecture_category` (`category_id`) USING BTREE,
  CONSTRAINT `FK_lecture_board_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_board_lecture_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_price_positive` CHECK ((`price` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의게시판';

-- 테이블 데이터 farm.lecture:~0 rows (대략적) 내보내기

-- 테이블 farm.lecture_pay 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_pay` (
  `pay_id` int NOT NULL AUTO_INCREMENT COMMENT '강의결제 ID',
  `lecture_id` int NOT NULL COMMENT '강의 ID',
  `user_id` int NOT NULL COMMENT '일반유저 ID',
  `uc_id` int DEFAULT NULL COMMENT '획득 쿠폰 ID',
  `total_price` int NOT NULL COMMENT '최종결제금액(직접기입X)',
  `paid_at` timestamp NOT NULL DEFAULT (now()) COMMENT '결재시간',
  `valid_until` timestamp NOT NULL COMMENT '강의 만료 시간',
  PRIMARY KEY (`pay_id`),
  UNIQUE KEY `lecture_id_user_id` (`lecture_id`,`user_id`),
  KEY `lecture_id` (`lecture_id`),
  KEY `user_id` (`user_id`),
  KEY `uc_id` (`uc_id`),
  CONSTRAINT `FK_lecture_pay_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_pay_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_pay_user_coupon` FOREIGN KEY (`uc_id`) REFERENCES `user_coupon` (`uc_id`) ON DELETE SET NULL,
  CONSTRAINT `chk_total_price_positive` CHECK ((`total_price` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 결제';

-- 테이블 데이터 farm.lecture_pay:~0 rows (대략적) 내보내기

-- 테이블 farm.lecture_progress 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_progress` (
  `progress_id` int NOT NULL AUTO_INCREMENT COMMENT '강의 진도 ID',
  `lecture_id` int NOT NULL COMMENT '강의 ID',
  `user_id` int NOT NULL COMMENT '일반유저 ID',
  `progress` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '진도율(0.00~1.00)',
  `earned_point` int NOT NULL DEFAULT '0' COMMENT '획득 포인트',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 시청시간',
  PRIMARY KEY (`progress_id`) USING BTREE,
  UNIQUE KEY `lecture_id_user_id` (`lecture_id`,`user_id`),
  KEY `FK_lecture_progress_all_users` (`user_id`),
  KEY `lecture_id` (`lecture_id`),
  CONSTRAINT `FK_lecture_progress_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_progress_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_progress_range` CHECK (((`progress` >= 0.00) and (`progress` <= 1.00)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 진도';

-- 테이블 데이터 farm.lecture_progress:~0 rows (대략적) 내보내기

-- 테이블 farm.lecture_qna 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_qna` (
  `qna_id` int NOT NULL AUTO_INCREMENT COMMENT '질문/답변 ID',
  `lecture_id` int NOT NULL COMMENT '강의게시판 ID',
  `user_id` int NOT NULL COMMENT '일반유저(질문)/강사(답변) ID',
  `p_qna_id` int DEFAULT NULL COMMENT '질문은 null/답변은 질문 ID',
  `content` text COMMENT '질문,답변 내용',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
  PRIMARY KEY (`qna_id`),
  KEY `FK_lecture_qna_lecture_board` (`lecture_id`),
  KEY `FK_lecture_qna_all_users` (`user_id`),
  KEY `FK_lecture_qna_lecture_qna` (`p_qna_id`),
  CONSTRAINT `FK_lecture_qna_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_qna_lecture_board` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_qna_lecture_qna` FOREIGN KEY (`p_qna_id`) REFERENCES `lecture_qna` (`qna_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 QnA';

-- 테이블 데이터 farm.lecture_qna:~0 rows (대략적) 내보내기

-- 테이블 farm.notice 구조 내보내기
CREATE TABLE IF NOT EXISTS `notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '공지사항 ID',
  `user_id` int NOT NULL COMMENT '관리자 ID',
  `title` varchar(50) NOT NULL COMMENT '공지사항 제목',
  `content` text NOT NULL COMMENT '공지사항 내용',
  `view_count` int DEFAULT '0' COMMENT '조회수',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
  PRIMARY KEY (`notice_id`),
  KEY `FK_all_users` (`user_id`),
  CONSTRAINT `FK_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='공지사항';

-- 테이블 데이터 farm.notice:~0 rows (대략적) 내보내기

-- 테이블 farm.point 구조 내보내기
CREATE TABLE IF NOT EXISTS `point` (
  `point_id` int NOT NULL AUTO_INCREMENT COMMENT '포인트 ID',
  `user_id` int NOT NULL COMMENT '일반유저 ID',
  `progress_id` int DEFAULT NULL COMMENT '강의 진도 ID(강의진도로 포인트를 얻는게 아니면 null)',
  `score_id` int DEFAULT NULL COMMENT '퀴즈 총점 ID(퀴즈총점로 포인트를 얻는게 아니면 null)',
  `item_id` int DEFAULT NULL COMMENT '아이템 ID(아이템으로 포인트를 잃는게 아니면 null)',
  `point_change` int NOT NULL COMMENT '포인트 변동량(강의 진도, 퀴즈 총점에는 정수/아이템에는 음수)',
  `total_point` int NOT NULL COMMENT '총 포인트량',
  `created_at` timestamp NULL DEFAULT (now()),
  PRIMARY KEY (`point_id`),
  KEY `FK_point_all_users` (`user_id`),
  KEY `FK_point_lecture_progress` (`progress_id`),
  KEY `FK_point_quiz_score` (`score_id`),
  KEY `FK_point_game_item` (`item_id`),
  CONSTRAINT `FK_point_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_point_game_item` FOREIGN KEY (`item_id`) REFERENCES `game_item` (`item_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_point_lecture_progress` FOREIGN KEY (`progress_id`) REFERENCES `lecture_progress` (`progress_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_point_quiz_score` FOREIGN KEY (`score_id`) REFERENCES `quiz_score` (`score_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트';

-- 테이블 데이터 farm.point:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quiz_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 ID',
  `category_id` int NOT NULL COMMENT '카테고리 ID',
  `quiz_number` int NOT NULL COMMENT '퀴즈번호',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '퀴즈에 쓰일 이미지',
  `question` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '퀴즈 질문',
  `item1` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '선택지 1',
  `item2` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '선택지 2',
  `item3` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '선택지 3',
  `item4` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '선택지 4',
  `answer` int NOT NULL COMMENT '정답',
  `quiz_score` int NOT NULL COMMENT '해당 문제 점수',
  PRIMARY KEY (`quiz_id`),
  KEY `FK_quiz_category` (`category_id`),
  CONSTRAINT `FK_quiz_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈';

-- 테이블 데이터 farm.quiz:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz_attempt 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_attempt` (
  `attempt_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈참여 ID',
  `user_id` int NOT NULL COMMENT '일반유저 ID',
  `category_id` int NOT NULL COMMENT '퀴즈 카테고리 ID',
  `quiz_id` int NOT NULL COMMENT '퀴즈 ID',
  `selected` int NOT NULL COMMENT '유저의 선택지',
  `earned_score` int NOT NULL COMMENT '얻은 점수(직접기입X)',
  `attempt_at` datetime DEFAULT NULL COMMENT '참여시간',
  PRIMARY KEY (`attempt_id`),
  KEY `FK_quiz_attempt_all_users` (`user_id`),
  KEY `FK_quiz_attempt_category` (`category_id`),
  KEY `FK_quiz_attempt_quiz` (`quiz_id`),
  CONSTRAINT `FK_quiz_attempt_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_attempt_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_attempt_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`quiz_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 참여기록';

-- 테이블 데이터 farm.quiz_attempt:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz_score 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_score` (
  `score_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 총점 ID',
  `category_id` int NOT NULL COMMENT '퀴즈 카테고리 ID',
  `user_id` int NOT NULL COMMENT '일반유저 ID',
  `total_score` int NOT NULL COMMENT '총점(직접기입X)',
  `earned_point` int NOT NULL DEFAULT '0' COMMENT '획득 포인트',
  `pass` enum('합격','불합격') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '합격/불합격(직접기입X)',
  PRIMARY KEY (`score_id`),
  UNIQUE KEY `category_id_user_id` (`category_id`,`user_id`),
  KEY `FK_quiz_score_all_users` (`user_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `FK_quiz_score_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_score_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 총점/합격 및 불합격';

-- 테이블 데이터 farm.quiz_score:~0 rows (대략적) 내보내기

-- 프로시저 farm.sp_register_user 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_register_user`(
	IN `p_user_type` VARCHAR(50),
	IN `p_id` VARCHAR(50),
	IN `p_password` VARCHAR(255),
	IN `p_name` VARCHAR(50),
	IN `p_nickname` VARCHAR(50),
	IN `p_birth` DATE
)
    COMMENT '회원가입 프로시저'
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '사용자 등록 실패';
    END;
    
    START TRANSACTION;
    
    -- 사용자 등록
    INSERT INTO all_users (user_type, id, password, name, nickname, birth)
    VALUES (p_user_type, p_id, p_password, p_name, p_nickname, p_birth);
    
    -- 일반 유저인 경우 초기 포인트 레코드 생성
    IF p_user_type = '일반유저' THEN
        INSERT INTO point (user_id, point_change, total_point)
        VALUES (LAST_INSERT_ID(), 0, 0);
    END IF;
    
    COMMIT;
END//
DELIMITER ;

-- 테이블 farm.user_coupon 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_coupon` (
  `uc_id` int NOT NULL AUTO_INCREMENT COMMENT '획득 쿠폰 ID',
  `user_id` int NOT NULL COMMENT '일반유저 ID',
  `coupon_id` int NOT NULL COMMENT '쿠폰 ID',
  `is_used` tinyint NOT NULL DEFAULT '1' COMMENT '쿠폰 보유중 = 1, 소비함 = 0(직접기입X)',
  `received_at` timestamp NULL DEFAULT (now()) COMMENT '쿠폰 획득일',
  PRIMARY KEY (`uc_id`),
  KEY `FK_user_coupon_all_users` (`user_id`),
  KEY `FK_user_coupon_coupon` (`coupon_id`),
  CONSTRAINT `FK_user_coupon_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_user_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`coupon_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='유저 소유 쿠폰';

-- 테이블 데이터 farm.user_coupon:~0 rows (대략적) 내보내기

-- 테이블 farm.user_item 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_item` (
  `user_item_id` int NOT NULL AUTO_INCREMENT COMMENT '보유 아이템 ID',
  `user_id` int NOT NULL COMMENT '일반유저 ID',
  `item_id` int NOT NULL COMMENT '아이템 ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '아이템 보유량',
  `bought_at` timestamp NULL DEFAULT (now()) COMMENT '구매시간',
  `used_at` timestamp NULL DEFAULT NULL COMMENT '사용시간',
  PRIMARY KEY (`user_item_id`),
  KEY `FK_user_item_all_users` (`user_id`),
  KEY `FK_user_item_game_item` (`item_id`),
  CONSTRAINT `FK_user_item_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_item_game_item` FOREIGN KEY (`item_id`) REFERENCES `game_item` (`item_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='유저가 보유한 아이템';

-- 테이블 데이터 farm.user_item:~0 rows (대략적) 내보내기

-- 뷰 farm.v_active_subscriptions 구조 내보내기
-- VIEW 종속성 오류를 극복하기 위해 임시 테이블을 생성합니다.
CREATE TABLE `v_active_subscriptions` (
	`pay_id` INT NOT NULL COMMENT '강의결제 ID',
	`lecture_id` INT NOT NULL COMMENT '강의 ID',
	`user_id` INT NOT NULL COMMENT '일반유저 ID',
	`uc_id` INT NULL COMMENT '획득 쿠폰 ID',
	`total_price` INT NOT NULL COMMENT '최종결제금액(직접기입X)',
	`paid_at` TIMESTAMP NOT NULL COMMENT '결재시간',
	`valid_until` TIMESTAMP NOT NULL COMMENT '강의 만료 시간'
);

-- 뷰 farm.v_lecture_subscribers 구조 내보내기
-- VIEW 종속성 오류를 극복하기 위해 임시 테이블을 생성합니다.
CREATE TABLE `v_lecture_subscribers` (
	`lecture_id` INT NOT NULL COMMENT '강의 ID',
	`title` VARCHAR(1) NOT NULL COMMENT '제목' COLLATE 'utf8mb4_0900_ai_ci',
	`subs_count` BIGINT NOT NULL,
	`price` INT NULL COMMENT '구독료',
	`created_at` TIMESTAMP NULL COMMENT '최초 작성시간'
);

-- 트리거 farm.trg_lecture_pay_after_delete 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_lecture_pay_after_delete` AFTER DELETE ON `lecture_pay` FOR EACH ROW BEGIN
	 -- 강의 만료시 구독자수 - 1 (0보다 작아지지 않음)
    UPDATE lecture
    SET subs_count = GREATEST(subs_count - 1, 0)
    WHERE lecture_id = OLD.lecture_id;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_lecture_pay_after_insert 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_lecture_pay_after_insert` AFTER INSERT ON `lecture_pay` FOR EACH ROW BEGIN
	 -- 강의 결재시 구독자수 + 1
    UPDATE lecture
    SET subs_count = subs_count + 1
    WHERE lecture_id = NEW.lecture_id;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_lecture_pay_before_insert 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_lecture_pay_before_insert` BEFORE INSERT ON `lecture_pay` FOR EACH ROW BEGIN
	 -- 쿠폰 사용 혹은 미사용을 가정하고 전체 가격 계산
    DECLARE original_price INT;
    DECLARE discount_rate DECIMAL(3,2);
    
    -- 강의의 원래 가격 조회
    SELECT price INTO original_price
    FROM lecture
    WHERE lecture_id = NEW.lecture_id;
    
    -- 쿠폰이 사용된 경우
    IF NEW.uc_id IS NOT NULL THEN
        -- 쿠폰의 할인율 조회
        SELECT c.dc_value INTO discount_rate
        FROM user_coupon uc
        JOIN coupon c ON uc.coupon_id = c.coupon_id
        WHERE uc.uc_id = NEW.uc_id
        AND c.valid_until > NOW();
        
        -- 할인율이 있으면 적용, 없으면 원가
        IF discount_rate IS NOT NULL THEN
            SET NEW.total_price = FLOOR(original_price * (1 - discount_rate));
        ELSE
            SET NEW.total_price = original_price;
        END IF;
    ELSE
        -- 쿠폰 미사용 시 원가
        SET NEW.total_price = original_price;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_lecture_pay_mark_coupon_used 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_lecture_pay_mark_coupon_used` AFTER INSERT ON `lecture_pay` FOR EACH ROW BEGIN
    -- 쿠폰이 사용된 경우 is_used를 0(소비함)으로 변경
    IF NEW.uc_id IS NOT NULL THEN
        UPDATE user_coupon
        SET is_used = 0
        WHERE uc_id = NEW.uc_id;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_lecture_pay_prevent_duplicate_coupon 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_lecture_pay_prevent_duplicate_coupon` BEFORE INSERT ON `lecture_pay` FOR EACH ROW BEGIN
    -- 쿠폰 중복사용 방지 트리거
    DECLARE coupon_status TINYINT;
    DECLARE coupon_expiry TIMESTAMP;
    
    -- 쿠폰이 사용된 경우에만 검증
    IF NEW.uc_id IS NOT NULL THEN
        
        -- 쿠폰 사용 여부 및 만료일 확인
        SELECT uc.is_used, c.valid_until
        INTO coupon_status, coupon_expiry
        FROM user_coupon uc
        JOIN coupon c ON uc.coupon_id = c.coupon_id
        WHERE uc.uc_id = NEW.uc_id;
        
        -- 쿠폰이 이미 사용된 경우 (is_used = 0)
        IF coupon_status = 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '이미 사용된 쿠폰입니다.';
        END IF;
        
        -- 쿠폰이 만료된 경우
        IF coupon_expiry < NOW() THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '만료된 쿠폰입니다.';
        END IF;
        
        -- 사용자가 해당 쿠폰을 소유하고 있는지 확인
        IF NOT EXISTS (
            SELECT 1 FROM user_coupon 
            WHERE uc_id = NEW.uc_id 
            AND user_id = NEW.user_id
        ) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '해당 쿠폰을 사용할 권한이 없습니다.';
        END IF;
        
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_lecture_pay_set_valid_until 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_lecture_pay_set_valid_until` BEFORE INSERT ON `lecture_pay` FOR EACH ROW BEGIN
	 -- 강의 수강가능 시간 설정(30일)
    IF NEW.valid_until IS NULL THEN
        SET NEW.valid_until = DATE_ADD(NOW(), INTERVAL 30 DAY);
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_quiz_attempt_before_insert 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_quiz_attempt_before_insert` BEFORE INSERT ON `quiz_attempt` FOR EACH ROW BEGIN
    DECLARE quiz_answer INT;
    DECLARE quiz_score_value INT;
    
    -- quiz 테이블에서 해당 quiz_id의 answer를 조회
    SELECT answer, quiz_score
    INTO quiz_answer, quiz_score_value
    FROM quiz 
    WHERE quiz_id = NEW.quiz_id;
    
    -- selected와 answer가 같으면 quiz_score 값을, 다르면 0점을 earned_score에 설정
    IF NEW.selected = quiz_answer THEN
        SET NEW.earned_score = quiz_score_value;
    ELSE
        SET NEW.earned_score = 0;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_quiz_score_management 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_quiz_score_management` AFTER INSERT ON `quiz_attempt` FOR EACH ROW BEGIN
	-- 퀴즈 총점 및 합격/불합격 기입
    INSERT INTO quiz_score (category_id, user_id, total_score, earned_point, pass)
    SELECT 
        NEW.category_id,
        NEW.user_id,
        COALESCE(SUM(earned_score), 0),
        COALESCE(SUM(earned_score), 0) * 1,
        CASE WHEN COALESCE(SUM(earned_score), 0) >= 60 THEN '합격' ELSE '불합격' END
    FROM quiz_attempt
    WHERE user_id = NEW.user_id AND category_id = NEW.category_id
    ON DUPLICATE KEY UPDATE
        total_score = VALUES(total_score),
        earned_point = VALUES(earned_point),
        pass = VALUES(pass);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 임시 테이블을 제거하고 최종 VIEW 구조를 생성
DROP TABLE IF EXISTS `v_active_subscriptions`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_active_subscriptions` AS select `lecture_pay`.`pay_id` AS `pay_id`,`lecture_pay`.`lecture_id` AS `lecture_id`,`lecture_pay`.`user_id` AS `user_id`,`lecture_pay`.`uc_id` AS `uc_id`,`lecture_pay`.`total_price` AS `total_price`,`lecture_pay`.`paid_at` AS `paid_at`,`lecture_pay`.`valid_until` AS `valid_until` from `lecture_pay` where (`lecture_pay`.`valid_until` > now())
;

-- 임시 테이블을 제거하고 최종 VIEW 구조를 생성
DROP TABLE IF EXISTS `v_lecture_subscribers`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_lecture_subscribers` AS select `l`.`lecture_id` AS `lecture_id`,`l`.`title` AS `title`,count(`lp`.`pay_id`) AS `subs_count`,`l`.`price` AS `price`,`l`.`created_at` AS `created_at` from (`lecture` `l` left join `lecture_pay` `lp` on(((`l`.`lecture_id` = `lp`.`lecture_id`) and (`lp`.`valid_until` > now())))) group by `l`.`lecture_id`
;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

