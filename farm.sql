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
  `user_type` enum('admin','teacher','student') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'student' COMMENT 'admin,teacher,student',
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '아이디',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '비밀번호',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '실명',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '닉네임',
  `birth` date NOT NULL COMMENT '생년월일',
  `email` varchar(50) NOT NULL COMMENT '이메일',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '자기소개문',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `nickName` (`nickname`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='모든 사용자(관리자, 강사, 일반유저)';

-- 테이블 데이터 farm.all_users:~3 rows (대략적) 내보내기
INSERT INTO `all_users` (`user_id`, `user_type`, `id`, `password`, `name`, `nickname`, `birth`, `email`, `intro`) VALUES
	(1, 'admin', 'admin', '1111', '관리자', '관리자', '2025-11-13', 'admin@admin.com', NULL),
	(11, 'student', 'hello', 'dlalsdn123', '이민우', '킹', '2025-11-04', 'w@naver.com', '반갑다 하하하'),
	(12, 'student', 'minu123', 'dlalsdn1', '이민구', '캉쿠쿠', '2025-10-27', 'd@nave.rocm', NULL),
	(13, 'student', 'hihi', 'dlalsdn1', '이민', 'dlalsdn', '2025-10-27', 'd@naver.com', NULL),
	(14, 'teacher', 'teacher', '1234', '강삼', '강사', '2025-11-13', 'rkddh@a.d', NULL),
	(16, 'student', 'qkrehwo123', 'pdj868312', '박도재', '박도토리', '1996-12-02', 'qkrehwo123@naver.com', NULL);

-- 테이블 farm.chat_member 구조 내보내기
CREATE TABLE IF NOT EXISTS `chat_member` (
  `member_id` int NOT NULL AUTO_INCREMENT COMMENT '채팅방 사용자 ID',
  `room_id` int NOT NULL COMMENT '실시간 채팅 ID',
  `user_id` int NOT NULL COMMENT '모든 유저 ID',
  `joined_at` timestamp NULL DEFAULT (now()) COMMENT '채팅방 입장 시간',
  PRIMARY KEY (`member_id`),
  KEY `room_id` (`room_id`),
  KEY `FK_chat_member_all_users` (`user_id`),
  CONSTRAINT `FK_chat_member_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_chat_room` FOREIGN KEY (`room_id`) REFERENCES `chat_room` (`room_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='채팅방 사용자';

-- 테이블 데이터 farm.chat_member:~0 rows (대략적) 내보내기
INSERT INTO `chat_member` (`member_id`, `room_id`, `user_id`, `joined_at`) VALUES
	(1, 1, 11, '2025-11-13 06:35:05');

-- 테이블 farm.chat_message 구조 내보내기
CREATE TABLE IF NOT EXISTS `chat_message` (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '채팅 기록 ID',
  `room_id` int NOT NULL COMMENT '실시간 채팅 ID',
  `user_id` int NOT NULL COMMENT '모든 유저 ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '채팅 내용',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '채팅 기록 시간',
  PRIMARY KEY (`message_id`),
  KEY `FK__chat_room` (`room_id`),
  KEY `FK__all_users` (`user_id`),
  CONSTRAINT `FK__all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK__chat_room` FOREIGN KEY (`room_id`) REFERENCES `chat_room` (`room_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='채팅 기록';

-- 테이블 데이터 farm.chat_message:~0 rows (대략적) 내보내기

-- 테이블 farm.chat_room 구조 내보내기
CREATE TABLE IF NOT EXISTS `chat_room` (
  `room_id` int NOT NULL AUTO_INCREMENT COMMENT '실시간 채팅 ID',
  `user_id` int NOT NULL COMMENT 'admin ID',
  `room_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '채팅방 제목',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '생성시간',
  PRIMARY KEY (`room_id`) USING BTREE,
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK_chat_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='실시간 채팅방';

-- 테이블 데이터 farm.chat_room:~0 rows (대략적) 내보내기
INSERT INTO `chat_room` (`room_id`, `user_id`, `room_name`, `created_at`) VALUES
	(1, 11, '나는천재', '2025-11-13 06:35:05');

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

-- 테이블 farm.game_action 구조 내보내기
CREATE TABLE IF NOT EXISTS `game_action` (
  `action_id` int NOT NULL AUTO_INCREMENT COMMENT '게임 행동 ID',
  `session_id` int NOT NULL DEFAULT '0' COMMENT '게임 세션 ID',
  `action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '미니게임 유형(물,비료주기/잡초,해충제거)',
  `is_reco` tinyint(1) DEFAULT '0' COMMENT '추천행동 여부(하면 1)',
  `is_avoid` tinyint(1) DEFAULT '0' COMMENT '회피행동 여부(하면 1)',
  `action_score` int DEFAULT '0' COMMENT '행동 점수',
  `mini_result` enum('perfect','good','bad','fail') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'fail' COMMENT '미니게임 판정',
  `mini_score` int DEFAULT '0' COMMENT '미니게임 점수',
  `growth_change` decimal(3,2) DEFAULT '0.00' COMMENT '행동 및 미니게임으로 인한 성장률 변화',
  `hp_cost` int DEFAULT '20' COMMENT '체력 소모량',
  PRIMARY KEY (`action_id`),
  KEY `session_id` (`session_id`),
  CONSTRAINT `FK_game_action_game_session` FOREIGN KEY (`session_id`) REFERENCES `game_session` (`session_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게임 내 행동';

-- 테이블 데이터 farm.game_action:~0 rows (대략적) 내보내기

-- 테이블 farm.game_session 구조 내보내기
CREATE TABLE IF NOT EXISTS `game_session` (
  `session_id` int NOT NULL AUTO_INCREMENT COMMENT '게임 세션 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `point_id` int NOT NULL COMMENT '포인트 ID',
  `game_status` enum('진행중','클리어','게임오버') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '진행중' COMMENT '게임 상태',
  `player_hp` int DEFAULT '100' COMMENT '플레이어 체력',
  `game_day` int DEFAULT '0' COMMENT '현재 경과 일수',
  `growth_rate` decimal(3,2) DEFAULT '0.00' COMMENT '작물 성장률(0.00~1.00)',
  `weather` enum('맑음','더움','비','흐림','폭풍') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '맑음' COMMENT '날씨(맑음/흐림 등)',
  `game_grade` enum('S','A','B','C','D') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'D' COMMENT '현재 등급(S/A/B/C/D)',
  `daily_action` int DEFAULT '0' COMMENT '행동 횟수',
  `started_at` timestamp NULL DEFAULT (now()) COMMENT '시작시간',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '수정시간(과정중/완료시)',
  PRIMARY KEY (`session_id`) USING BTREE,
  KEY `FK_game_record_all_users` (`user_id`),
  KEY `FK_game_record_point` (`point_id`),
  CONSTRAINT `FK_game_record_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_game_record_point` FOREIGN KEY (`point_id`) REFERENCES `point` (`point_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_daily_action` CHECK (((`daily_action` >= 0) and (`daily_action` <= 2))),
  CONSTRAINT `chk_game_day_range` CHECK (((`game_day` >= 0) and (`game_day` <= 30))),
  CONSTRAINT `chk_growth_rate` CHECK (((`growth_rate` >= 0.00) and (`growth_rate` <= 1.00))),
  CONSTRAINT `chk_player_hp_max` CHECK (((`player_hp` <= 100) and (`player_hp` >= 0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게임 세션';

-- 테이블 데이터 farm.game_session:~0 rows (대략적) 내보내기

-- 테이블 farm.lecture 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture` (
  `lecture_id` int NOT NULL AUTO_INCREMENT COMMENT '강의 ID',
  `user_id` int NOT NULL COMMENT 'teacher ID',
  `category` enum('seed','grow','ship') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '카테고리 이름(모종/재배/출하)',
  `difficulty` enum('beginner','intermediate','advanced') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '난이도(초급/중급/고급)',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '강의에 쓰일 이미지(썸네일 등)',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '제목',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '강의 요약',
  `price` int NOT NULL DEFAULT '0' COMMENT '구독료',
  `subs_count` int NOT NULL DEFAULT '0' COMMENT '구독자 수',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  PRIMARY KEY (`lecture_id`),
  KEY `FK_lecture_all_users` (`user_id`) USING BTREE,
  CONSTRAINT `FK_lecture_board_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_price_positive` CHECK ((`price` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의게시판';

-- 테이블 데이터 farm.lecture:~0 rows (대략적) 내보내기
INSERT INTO `lecture` (`lecture_id`, `user_id`, `category`, `difficulty`, `img_url`, `title`, `content`, `price`, `subs_count`, `created_at`) VALUES
	(1, 14, 'seed', 'beginner', NULL, '테스트', '1번\r\n1\r\n2번\r\n2\r\n3번\r\n3', 10000, 2, '2025-11-13 06:19:32');

-- 테이블 farm.lecture_pay 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_pay` (
  `pay_id` int NOT NULL AUTO_INCREMENT COMMENT '강의결제 ID',
  `lecture_id` int NOT NULL COMMENT '강의 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `uc_id` int DEFAULT NULL COMMENT '획득 쿠폰 ID',
  `total_price` int NOT NULL COMMENT '최종결제금액(직접기입X)',
  `paid_at` timestamp NOT NULL DEFAULT (now()) COMMENT '결재시간',
  `valid_until` timestamp NOT NULL COMMENT '강의 만료 시간',
  PRIMARY KEY (`pay_id`),
  KEY `lecture_id` (`lecture_id`),
  KEY `user_id` (`user_id`),
  KEY `uc_id` (`uc_id`),
  CONSTRAINT `FK_lecture_pay_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_pay_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_pay_user_coupon` FOREIGN KEY (`uc_id`) REFERENCES `user_coupon` (`uc_id`) ON DELETE SET NULL,
  CONSTRAINT `chk_total_price_positive` CHECK ((`total_price` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 결제';

-- 테이블 데이터 farm.lecture_pay:~0 rows (대략적) 내보내기

-- 테이블 farm.lecture_progress 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_progress` (
  `progress_id` int NOT NULL AUTO_INCREMENT COMMENT '강의 진도 ID',
  `lecture_id` int NOT NULL COMMENT '강의 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `progress` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '진도율(0.00~1.00)',
  `earned_point` int NOT NULL DEFAULT '0' COMMENT '획득 포인트',
  `valid_until` timestamp NOT NULL COMMENT '강의 만료 시간',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 시청시간',
  PRIMARY KEY (`progress_id`) USING BTREE,
  UNIQUE KEY `lecture_id_user_id` (`lecture_id`,`user_id`),
  KEY `FK_lecture_progress_all_users` (`user_id`),
  KEY `lecture_id` (`lecture_id`),
  CONSTRAINT `FK_lecture_progress_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_progress_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_progress_range` CHECK (((`progress` >= 0.00) and (`progress` <= 1.00)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='구독 강의 진도';

-- 테이블 데이터 farm.lecture_progress:~0 rows (대략적) 내보내기

-- 테이블 farm.lecture_qna 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_qna` (
  `qna_id` int NOT NULL AUTO_INCREMENT COMMENT '질문/답변 ID',
  `lecture_id` int NOT NULL COMMENT '강의게시판 ID',
  `user_id` int NOT NULL COMMENT 'student(질문)/teacher(답변) ID',
  `p_qna_id` int DEFAULT NULL COMMENT '질문은 null/답변은 질문 ID',
  `content` text COMMENT '질문,답변 내용',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  PRIMARY KEY (`qna_id`),
  KEY `FK_lecture_qna_lecture_board` (`lecture_id`),
  KEY `FK_lecture_qna_all_users` (`user_id`),
  KEY `FK_lecture_qna_lecture_qna` (`p_qna_id`),
  CONSTRAINT `FK_lecture_qna_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_qna_lecture_board` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_qna_lecture_qna` FOREIGN KEY (`p_qna_id`) REFERENCES `lecture_qna` (`qna_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 QnA';

-- 테이블 데이터 farm.lecture_qna:~0 rows (대략적) 내보내기

-- 테이블 farm.lecture_video 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_video` (
  `video_id` int NOT NULL AUTO_INCREMENT COMMENT '강의 영상 ID',
  `lecture_id` int DEFAULT NULL COMMENT '강의 ID',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '강의 영상 링크',
  `video_time` int DEFAULT NULL COMMENT '영상 시간(초)',
  PRIMARY KEY (`video_id`),
  KEY `FK_lecture_video_lecture` (`lecture_id`),
  CONSTRAINT `FK_lecture_video_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 영상';

-- 테이블 데이터 farm.lecture_video:~0 rows (대략적) 내보내기
INSERT INTO `lecture_video` (`video_id`, `lecture_id`, `video_url`, `video_time`) VALUES
	(1, 1, NULL, 520),
	(2, 1, NULL, 480),
	(3, 1, NULL, 330);

-- 테이블 farm.lecture_video_progress 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_video_progress` (
  `video_progress_id` int NOT NULL AUTO_INCREMENT COMMENT '비디오 진도 ID',
  `progress_id` int NOT NULL COMMENT '강의 진도 ID',
  `video_id` int NOT NULL COMMENT '강의 영상 ID',
  `progress` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '진도율(0.00~1.00)',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '완료 시간',
  PRIMARY KEY (`video_progress_id`),
  UNIQUE KEY `unique_progress_video` (`progress_id`,`video_id`),
  KEY `FK_video_progress_lecture_progress` (`progress_id`),
  KEY `FK_video_progress_video` (`video_id`),
  CONSTRAINT `FK_video_progress_lecture_progress` FOREIGN KEY (`progress_id`) REFERENCES `lecture_progress` (`progress_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_video_progress_video` FOREIGN KEY (`video_id`) REFERENCES `lecture_video` (`video_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='개별 비디오 시청 진도';

-- 테이블 데이터 farm.lecture_video_progress:~0 rows (대략적) 내보내기

-- 테이블 farm.notice 구조 내보내기
CREATE TABLE IF NOT EXISTS `notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '공지사항 ID',
  `user_id` int NOT NULL COMMENT 'admin ID',
  `title` varchar(50) NOT NULL COMMENT '공지사항 제목',
  `content` text NOT NULL COMMENT '공지사항 내용',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '조회수',
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
  `user_id` int NOT NULL COMMENT 'student ID',
  `progress_id` int DEFAULT NULL COMMENT '강의 진도 ID(강의진도로 포인트를 얻는게 아니면 null)',
  `score_id` int DEFAULT NULL COMMENT '퀴즈 총점 ID(퀴즈총점로 포인트를 얻는게 아니면 null)',
  `point_change` int NOT NULL COMMENT '포인트 변동량(강의 진도, 퀴즈 총점에는 정수/아이템에는 음수)',
  `total_point` int NOT NULL COMMENT '총 포인트량',
  `created_at` timestamp NULL DEFAULT (now()),
  PRIMARY KEY (`point_id`),
  KEY `FK_point_all_users` (`user_id`),
  KEY `FK_point_lecture_progress` (`progress_id`),
  KEY `FK_point_quiz_score` (`score_id`),
  CONSTRAINT `FK_point_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_point_lecture_progress` FOREIGN KEY (`progress_id`) REFERENCES `lecture_progress` (`progress_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_point_quiz_score` FOREIGN KEY (`score_id`) REFERENCES `quiz_score` (`score_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='포인트';

-- 테이블 데이터 farm.point:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quiz_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 ID',
  `quiz_category_id` int DEFAULT NULL COMMENT '퀴즈 카테고리 ID',
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
  KEY `FK_quiz_quiz_category` (`quiz_category_id`),
  CONSTRAINT `FK_quiz_quiz_category` FOREIGN KEY (`quiz_category_id`) REFERENCES `quiz_category` (`quiz_category_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈';

-- 테이블 데이터 farm.quiz:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz_attempt 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_attempt` (
  `attempt_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈참여 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `quiz_id` int NOT NULL COMMENT '퀴즈 ID',
  `selected` int NOT NULL COMMENT '유저의 선택지',
  `earned_score` int NOT NULL COMMENT '얻은 점수(직접기입X)',
  `attempt_at` datetime DEFAULT NULL COMMENT '참여시간',
  PRIMARY KEY (`attempt_id`),
  UNIQUE KEY `user_id_quiz_id` (`user_id`,`quiz_id`),
  KEY `FK_quiz_attempt_quiz` (`quiz_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK_quiz_attempt_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_attempt_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`quiz_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 참여기록';

-- 테이블 데이터 farm.quiz_attempt:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz_category 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_category` (
  `quiz_category_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 카테고리 ID',
  `category_name` varchar(50) NOT NULL COMMENT '카테고리 이름',
  `category_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '카테고리에 쓰일 이미지',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '카테고리 설명',
  `difficulty` enum('easy','normal','hard') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'easy' COMMENT '난이도',
  `pass_score` int DEFAULT '60' COMMENT '합격점수',
  `display_order` int DEFAULT '0' COMMENT '카테고리 표시순서(낮을수록 위에 위치)',
  `is_active` tinyint DEFAULT '1' COMMENT '카테고리 활성화 여부(1은 활성화)',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '작성시간',
  PRIMARY KEY (`quiz_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 카테고리';

-- 테이블 데이터 farm.quiz_category:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz_score 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_score` (
  `score_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 총점 ID',
  `quiz_category_id` int NOT NULL COMMENT '퀴즈 카테고리 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `total_score` int NOT NULL COMMENT '총점(직접기입X)',
  `earned_point` int NOT NULL DEFAULT '0' COMMENT '획득 포인트',
  `pass` enum('합격','불합격') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '합격/불합격(직접기입X)',
  PRIMARY KEY (`score_id`),
  UNIQUE KEY `category_id_user_id` (`quiz_category_id`,`user_id`) USING BTREE,
  KEY `FK_quiz_score_all_users` (`user_id`),
  KEY `category_id` (`quiz_category_id`) USING BTREE,
  CONSTRAINT `FK_quiz_score_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_score_quiz_category` FOREIGN KEY (`quiz_category_id`) REFERENCES `quiz_category` (`quiz_category_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 총점/합격 및 불합격';

-- 테이블 데이터 farm.quiz_score:~0 rows (대략적) 내보내기

-- 프로시저 farm.sp_update_video_progress 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_update_video_progress`(
    IN p_progress_id INT,
    IN p_video_id INT,
    IN p_current_position INT
)
BEGIN
    DECLARE v_video_time INT;
    
    -- 비디오 총 시간 조회
    SELECT video_time INTO v_video_time
    FROM lecture_video
    WHERE video_id = p_video_id;
    
    -- 시청 기록 INSERT 또는 UPDATE
    INSERT INTO lecture_video_progress (
        progress_id, 
        video_id, 
        watched_time, 
        last_position,
        watched_at
    )
    VALUES (
        p_progress_id, 
        p_video_id, 
        LEAST(p_current_position, v_video_time),
        p_current_position,
        NOW()
    )
    ON DUPLICATE KEY UPDATE
        watched_time = GREATEST(watched_time, LEAST(p_current_position, v_video_time)),
        last_position = p_current_position;
END//
DELIMITER ;

-- 테이블 farm.user_coupon 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_coupon` (
  `uc_id` int NOT NULL AUTO_INCREMENT COMMENT '획득 쿠폰 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `session_id` int DEFAULT NULL COMMENT '게임 세션 ID(게임 보상으로 얻은 것이 아니면 기입X)',
  `coupon_id` int NOT NULL COMMENT '쿠폰 ID',
  `is_used` tinyint NOT NULL DEFAULT '1' COMMENT '쿠폰 보유중 = 1, 소비함 = 0(직접기입X)',
  `received_at` timestamp NULL DEFAULT (now()) COMMENT '쿠폰 획득일',
  PRIMARY KEY (`uc_id`),
  KEY `FK_user_coupon_all_users` (`user_id`),
  KEY `FK_user_coupon_coupon` (`coupon_id`),
  KEY `FK_user_coupon_game_session` (`session_id`),
  CONSTRAINT `FK_user_coupon_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_user_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`coupon_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_user_coupon_game_session` FOREIGN KEY (`session_id`) REFERENCES `game_session` (`session_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='유저 소유 쿠폰';

-- 테이블 데이터 farm.user_coupon:~0 rows (대략적) 내보내기

-- 뷰 farm.user_summary 구조 내보내기
-- VIEW 종속성 오류를 극복하기 위해 임시 테이블을 생성합니다.
CREATE TABLE `user_summary` (
	`이름` LONGTEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`닉네임` VARCHAR(1) NOT NULL COMMENT '닉네임' COLLATE 'utf8mb4_0900_ai_ci',
	`아이디` LONGTEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`이메일` LONGTEXT NULL COLLATE 'utf8mb4_0900_ai_ci'
);

-- 뷰 farm.v_active_subscriptions 구조 내보내기
-- VIEW 종속성 오류를 극복하기 위해 임시 테이블을 생성합니다.
CREATE TABLE `v_active_subscriptions` (
	`pay_id` INT NOT NULL COMMENT '강의결제 ID',
	`lecture_id` INT NOT NULL COMMENT '강의 ID',
	`user_id` INT NOT NULL COMMENT 'student ID',
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
	`price` INT NOT NULL COMMENT '구독료',
	`created_at` TIMESTAMP NULL COMMENT '최초 작성시간',
	`subs_count` BIGINT NOT NULL,
	`total_revenue` DECIMAL(32,0) NULL,
	`last_subscription_date` TIMESTAMP NULL COMMENT '결재시간'
);

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

-- 트리거 farm.trg_lecture_pay_create_user_lecture 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_lecture_pay_create_user_lecture` AFTER INSERT ON `lecture_pay` FOR EACH ROW BEGIN
	 -- lecture_pay 등록 시 lecture_progress 테이블에 강의 진도 레코드 생성
    -- 중복 항목: lecture_id, user_id, valid_until
    -- 디폴트/NULL 항목: progress(0.00), earned_point(0), updated_at(NULL)
    
    INSERT INTO lecture_progress (
        lecture_id,
        user_id,
        progress,
        earned_point,
        valid_until,
        updated_at
    )
    VALUES (
        NEW.lecture_id,      -- lecture_pay의 lecture_id 그대로 사용
        NEW.user_id,         -- lecture_pay의 user_id 그대로 사용
        0.00,                -- 진도율 초기값 (0%)
        0,                   -- 획득 포인트 초기값
        NEW.valid_until,     -- lecture_pay의 valid_until 그대로 사용
        NULL                 -- 아직 시청하지 않았으므로 NULL
    )
    ON DUPLICATE KEY UPDATE
        -- 재구독 시: 기간만 연장, 진도는 유지
        valid_until = GREATEST(valid_until, NEW.valid_until),
        -- 만료 후 재구독이면 진도 초기화
        progress = IF(valid_until < NOW(), 0.00, progress),
        earned_point = IF(valid_until < NOW(), 0, earned_point),
        updated_at = IF(valid_until < NOW(), NULL, updated_at);
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

-- 트리거 farm.trg_quiz_attempt_after_insert 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_quiz_attempt_after_insert` AFTER INSERT ON `quiz_attempt` FOR EACH ROW BEGIN
    DECLARE category INT;
    DECLARE current_total INT;
    DECLARE pass_threshold INT;
    DECLARE pass_status ENUM('합격', '불합격');
    
    -- 퀴즈가 속한 카테고리 찾기
    SELECT qc.quiz_category_id, qc.pass_score
    INTO category, pass_threshold
    FROM quiz q
    JOIN quiz_category qc ON q.quiz_id = NEW.quiz_id
    LIMIT 1;
    
    -- 해당 카테고리의 현재까지 획득한 총점 계산
    SELECT IFNULL(SUM(qa.earned_score), 0)
    INTO current_total
    FROM quiz_attempt qa
    JOIN quiz q ON qa.quiz_id = q.quiz_id
    WHERE qa.user_id = NEW.user_id;
    
    -- 합격 여부 판정
    IF current_total >= pass_threshold THEN
        SET pass_status = '합격';
    ELSE
        SET pass_status = '불합격';
    END IF;
    
    -- quiz_score 테이블에 INSERT 또는 UPDATE
    INSERT INTO quiz_score (quiz_category_id, user_id, total_score, earned_point, pass)
    VALUES (quiz_category_id, NEW.user_id, current_total, 0, pass_status)
    ON DUPLICATE KEY UPDATE 
        total_score = current_total,
        pass = pass_status;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_quiz_attempt_before_insert 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_quiz_attempt_before_insert` BEFORE INSERT ON `quiz_attempt` FOR EACH ROW BEGIN
    DECLARE quiz_answer INT;
    DECLARE quiz_score_value INT;
    
    -- quiz 테이블에서 해당 quiz_id의 answer와 quiz_score 조회
    SELECT answer, quiz_score
    INTO quiz_answer, quiz_score_value
    FROM quiz 
    WHERE quiz_id = NEW.quiz_id;
    
    -- 퀴즈가 존재하지 않는 경우 에러 발생
    IF quiz_answer IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '존재하지 않는 퀴즈입니다.';
    END IF;
    
    -- selected 값이 1~4 범위인지 검증
    IF NEW.selected NOT IN (1, 2, 3, 4) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '선택지는 1~4 사이의 값이어야 합니다.';
    END IF;
    
    -- selected와 answer가 같으면 quiz_score 값을, 다르면 0점을 earned_score에 설정
    IF NEW.selected = quiz_answer THEN
        SET NEW.earned_score = quiz_score_value;
    ELSE
        SET NEW.earned_score = 0;
    END IF;
    
    -- attempt_at이 NULL이면 현재 시간으로 설정
    IF NEW.attempt_at IS NULL THEN
        SET NEW.attempt_at = NOW();
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_quiz_score_after_update 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_quiz_score_after_update` AFTER UPDATE ON `quiz_score` FOR EACH ROW BEGIN
    DECLARE point_reward INT;
    DECLARE current_total_point INT;
    
    -- 불합격에서 합격으로 변경된 경우에만 포인트 지급
    IF OLD.pass = '불합격' AND NEW.pass = '합격' AND OLD.earned_point = 0 THEN
        
        -- 난이도에 따른 포인트 계산
        SELECT CASE qc.difficulty
            WHEN 'easy' THEN 100
            WHEN 'normal' THEN 200
            WHEN 'hard' THEN 300
            ELSE 100
        END INTO point_reward
        FROM quiz_category qc
        WHERE qc.quiz_category_id = NEW.quiz_category_id;
        
        -- earned_point 업데이트
        UPDATE quiz_score
        SET earned_point = point_reward
        WHERE score_id = NEW.score_id;
        
        -- 현재 총 포인트 조회
        SELECT IFNULL(total_point, 0) INTO current_total_point
        FROM point
        WHERE user_id = NEW.user_id
        ORDER BY created_at DESC
        LIMIT 1;
        
        -- point 테이블에 포인트 변동 기록
        INSERT INTO point (user_id, progress_id, score_id, item_id, point_change, total_point)
        VALUES (NEW.user_id, NULL, NEW.score_id, NULL, point_reward, current_total_point + point_reward);
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 임시 테이블을 제거하고 최종 VIEW 구조를 생성
DROP TABLE IF EXISTS `user_summary`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `user_summary` AS select concat(left(`all_users`.`name`,1),repeat('*',(char_length(`all_users`.`name`) - 1))) AS `이름`,`all_users`.`nickname` AS `닉네임`,concat(left(`all_users`.`id`,3),repeat('*',(char_length(`all_users`.`id`) - 3))) AS `아이디`,concat(repeat('*',(locate('@',`all_users`.`email`) - 1)),substr(`all_users`.`email`,locate('@',`all_users`.`email`))) AS `이메일` from `all_users`
;

-- 임시 테이블을 제거하고 최종 VIEW 구조를 생성
DROP TABLE IF EXISTS `v_active_subscriptions`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_active_subscriptions` AS select `lecture_pay`.`pay_id` AS `pay_id`,`lecture_pay`.`lecture_id` AS `lecture_id`,`lecture_pay`.`user_id` AS `user_id`,`lecture_pay`.`uc_id` AS `uc_id`,`lecture_pay`.`total_price` AS `total_price`,`lecture_pay`.`paid_at` AS `paid_at`,`lecture_pay`.`valid_until` AS `valid_until` from `lecture_pay` where (`lecture_pay`.`valid_until` > now())
;

-- 임시 테이블을 제거하고 최종 VIEW 구조를 생성
DROP TABLE IF EXISTS `v_lecture_subscribers`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_lecture_subscribers` AS select `l`.`lecture_id` AS `lecture_id`,`l`.`title` AS `title`,`l`.`price` AS `price`,`l`.`created_at` AS `created_at`,count(`lp`.`pay_id`) AS `subs_count`,sum(`lp`.`total_price`) AS `total_revenue`,max(`lp`.`paid_at`) AS `last_subscription_date` from (`lecture` `l` left join `lecture_pay` `lp` on(((`l`.`lecture_id` = `lp`.`lecture_id`) and (`lp`.`valid_until` > now())))) group by `l`.`lecture_id`,`l`.`title`,`l`.`price`,`l`.`created_at`
;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
