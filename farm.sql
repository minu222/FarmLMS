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
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='모든 사용자(관리자, 강사, 일반유저)';

-- 테이블 데이터 farm.all_users:~8 rows (대략적) 내보내기
INSERT INTO `all_users` (`user_id`, `user_type`, `id`, `password`, `name`, `nickname`, `birth`, `email`, `intro`) VALUES
	(1, 'admin', 'admin', '1111', '관리자', '관리자', '2025-11-13', 'admin@admin.com', '나야 관리자.'),
	(2, 'teacher', '1234', '1234', '강사', '강사13', '2025-11-18', 'asd@asd.asd', '테스트용 강사'),
	(16, 'student', 'qkrehwo123', '1234', '박도재', '박도토리', '1996-12-02', 'qkrehwo123@naver.com', '안녕하세요'),
	(25, 'student', 'hello', 'dlalsdn1', '이민우', '이황우', '2025-11-10', 'mw@naver.com', '민우님이다 음하하'),
	(27, 'student', 'qwer1234', 'rkdrjsgh1234', '강건호', '강건호아님', '2025-11-01', 'rkdrjsgh123@naver.com', NULL),
	(28, 'teacher', 'dkssud123', 'dlalsdn1', '앜', '뭘봐', '2025-10-31', 'dd@naver.com', NULL),
	(35, 'student', 'jmj10338', 'jmj691107', '정민주', '초보농부', '1998-10-07', 'jmj10338@gmail.com', NULL);

-- 테이블 farm.attachment 구조 내보내기
CREATE TABLE IF NOT EXISTS `attachment` (
  `attachment_id` int NOT NULL AUTO_INCREMENT COMMENT '첨부파일 ID',
  `reference_type` enum('lecture','notice') NOT NULL COMMENT '참조 테이블 타입',
  `reference_id` int NOT NULL COMMENT '참조 테이블의 ID',
  `user_id` int NOT NULL COMMENT 'admin, teacher ID',
  `original_filename` varchar(255) NOT NULL COMMENT '원본 파일명',
  `stored_filename` varchar(255) NOT NULL COMMENT '저장된 파일명',
  `file_path` varchar(500) NOT NULL COMMENT '파일 저장 경로',
  `file_size` bigint NOT NULL COMMENT '파일 크기(bytes)',
  `uploaded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '업로드 시간',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_reference` (`reference_type`,`reference_id`),
  KEY `FK_attachments_user` (`user_id`),
  CONSTRAINT `FK_attachments_user` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='통합 첨부파일 테이블';

-- 테이블 데이터 farm.attachment:~0 rows (대략적) 내보내기

-- 테이블 farm.chat_member 구조 내보내기
CREATE TABLE IF NOT EXISTS `chat_member` (
  `member_id` int NOT NULL AUTO_INCREMENT COMMENT '채팅방 사용자 ID',
  `room_id` int NOT NULL COMMENT '실시간 채팅 ID',
  `user_id` int DEFAULT NULL COMMENT '모든 유저 ID',
  `joined_at` timestamp NULL DEFAULT (now()) COMMENT '채팅방 입장 시간',
  PRIMARY KEY (`member_id`),
  KEY `room_id` (`room_id`),
  KEY `FK_chat_member_all_users` (`user_id`),
  CONSTRAINT `FK_chat_member_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_chat_room` FOREIGN KEY (`room_id`) REFERENCES `chat_room` (`room_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='채팅방 사용자';

-- 테이블 데이터 farm.chat_member:~2 rows (대략적) 내보내기
INSERT INTO `chat_member` (`member_id`, `room_id`, `user_id`, `joined_at`) VALUES
	(51, 13, 16, '2025-11-18 05:16:15'),
	(52, 13, 25, '2025-11-18 05:16:27'),
	(53, 13, 1, '2025-11-18 05:51:07'),
	(54, 13, 2, '2025-11-24 00:01:39'),
	(55, 13, 35, '2025-11-24 00:39:15'),
	(56, 14, 35, '2025-11-24 00:44:28'),
	(57, 14, 16, '2025-11-24 02:18:32');

-- 테이블 farm.chat_message 구조 내보내기
CREATE TABLE IF NOT EXISTS `chat_message` (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '채팅 기록 ID',
  `room_id` int NOT NULL COMMENT '실시간 채팅 ID',
  `user_id` int DEFAULT NULL COMMENT '모든 유저 ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '채팅 내용',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '채팅 기록 시간',
  PRIMARY KEY (`message_id`),
  KEY `FK_chat_message_all_users` (`user_id`),
  KEY `idx_room_created` (`room_id`,`created_at`),
  CONSTRAINT `FK__chat_room` FOREIGN KEY (`room_id`) REFERENCES `chat_room` (`room_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_chat_message_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='채팅 기록';

-- 테이블 데이터 farm.chat_message:~8 rows (대략적) 내보내기
INSERT INTO `chat_message` (`message_id`, `room_id`, `user_id`, `content`, `created_at`) VALUES
	(126, 13, 16, 'ㅎㅇㅎㅇ', '2025-11-18 05:16:30'),
	(127, 13, 25, '안녕하세여 ㅎㅎ', '2025-11-18 05:16:35'),
	(128, 13, 25, '안녕하세여', '2025-11-18 05:17:19'),
	(129, 13, 25, 'ㅎㅎ', '2025-11-18 05:17:20'),
	(130, 13, 25, '안녕하시냐구여', '2025-11-18 05:17:33'),
	(131, 13, 25, '저기요', '2025-11-18 05:17:40'),
	(132, 13, 25, '여보세여', '2025-11-18 05:17:42'),
	(133, 13, 1, '나야', '2025-11-18 05:51:14'),
	(134, 13, 1, '거기 잘 지내니', '2025-11-18 05:51:16'),
	(135, 13, 16, '?', '2025-11-18 06:25:15'),
	(136, 13, 35, '캡본 따려했는데... 방을 새로 파야겠군여', '2025-11-24 00:42:49'),
	(137, 13, 35, 'ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ', '2025-11-24 00:42:50'),
	(138, 14, 35, '안녕하세요. 이제 막 농업에 종사하게 된 초보 농부입니다.\n문외한이라 너무 걱정되는데 농사 선배님들의 농사팁 좀 많이 배워가겠습니다!', '2025-11-24 00:44:37'),
	(139, 14, 16, '안녕하세요', '2025-11-24 03:16:57');

-- 테이블 farm.chat_room 구조 내보내기
CREATE TABLE IF NOT EXISTS `chat_room` (
  `room_id` int NOT NULL AUTO_INCREMENT COMMENT '실시간 채팅 ID',
  `user_id` int NOT NULL COMMENT 'admin ID',
  `room_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '채팅방 제목',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '생성시간',
  PRIMARY KEY (`room_id`) USING BTREE,
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK_chat_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='실시간 채팅방';

-- 테이블 데이터 farm.chat_room:~1 rows (대략적) 내보내기
INSERT INTO `chat_room` (`room_id`, `user_id`, `room_name`, `created_at`) VALUES
	(13, 16, '새로 만든 방', '2025-11-18 05:16:15'),
	(14, 35, '문외한도 쉽게 이해하는 기초! 토양 관리부터 수확까지!', '2025-11-24 00:44:28');

-- 테이블 farm.game 구조 내보내기
CREATE TABLE IF NOT EXISTS `game` (
  `session_id` int NOT NULL AUTO_INCREMENT COMMENT '게임 세션 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `player_hp` int DEFAULT '100' COMMENT '플레이어 체력',
  `game_day` int DEFAULT '0' COMMENT '현재 경과 일수',
  `growth_rate` decimal(5,2) DEFAULT '0.00' COMMENT '작물 성장률',
  `weather` enum('clear','hot','rain','cloudy','storm') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'clear' COMMENT '날씨(맑음/흐림 등)',
  `daily_action` int DEFAULT '2' COMMENT '행동 횟수',
  `action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '첫번째 행동유형',
  `mini_result` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '행동 결과',
  `action_score` decimal(5,2) DEFAULT '0.00' COMMENT '점수(미니게임 단위)',
  `game_grade` enum('S','A','B','C','D') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'D' COMMENT '현재 등급(S/A/B/C/D)',
  PRIMARY KEY (`session_id`) USING BTREE,
  KEY `FK_game_record_all_users` (`user_id`),
  CONSTRAINT `FK_game_record_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_action_score` CHECK (((`action_score` >= 0.00) and (`action_score` <= 100.00))),
  CONSTRAINT `chk_game_day_range` CHECK (((`game_day` >= 0) and (`game_day` <= 30))),
  CONSTRAINT `chk_growth_rate` CHECK (((`growth_rate` >= 0.00) and (`growth_rate` <= 100.00))),
  CONSTRAINT `chk_player_hp_max` CHECK (((`player_hp` <= 100) and (`player_hp` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게임';

-- 테이블 데이터 farm.game:~3 rows (대략적) 내보내기
INSERT INTO `game` (`session_id`, `user_id`, `player_hp`, `game_day`, `growth_rate`, `weather`, `daily_action`, `action_type`, `mini_result`, `action_score`, `game_grade`) VALUES
	(16, 1, 80, 1, 0.00, 'cloudy', 1, 'Water', 'Perfect', 2.35, 'D'),
	(17, 1, 60, 1, 0.00, 'cloudy', 0, 'Fertilize', 'Good', 1.00, 'D'),
	(18, 1, 60, 2, 3.35, 'storm', 2, 'none', '', 0.00, 'D'),
	(19, 1, 40, 2, 3.35, 'storm', 1, 'Water', 'Perfect', 2.35, 'D'),
	(20, 1, 20, 2, 3.35, 'storm', 0, 'Pest', 'Good', 1.00, 'D'),
	(21, 1, 20, 3, 6.70, 'cloudy', 2, 'none', '', 0.00, 'D'),
	(22, 1, 80, 1, 0.00, 'storm', 1, 'Water', 'Perfect', 2.35, 'D'),
	(23, 1, 60, 1, 0.00, 'storm', 0, 'Fertilize', 'Good', 1.00, 'D'),
	(24, 1, 60, 2, 3.35, 'cloudy', 2, 'none', '', 0.00, 'D'),
	(25, 1, 60, 2, 1.00, 'rain', 2, 'none', '', 0.00, 'D'),
	(26, 1, 80, 1, 0.00, 'cloudy', 1, 'Water', 'Perfect', 2.35, 'D'),
	(27, 1, 60, 1, 0.00, 'cloudy', 0, 'Fertilize', 'Good', 1.00, 'D'),
	(28, 1, 60, 2, 3.35, 'rain', 2, 'none', '', 0.00, 'D'),
	(29, 1, 40, 2, 3.35, 'rain', 1, 'Pest', 'Good', 1.00, 'D'),
	(30, 1, 20, 2, 3.35, 'rain', 0, 'Weed', 'Perfect', 2.35, 'D'),
	(31, 1, 80, 1, 0.00, 'hot', 1, 'Water', 'Bad', 0.00, 'D'),
	(32, 1, 60, 1, 0.00, 'hot', 0, 'Fertilize', 'Good', 1.00, 'D'),
	(33, 1, 60, 2, 3.35, 'cloudy', 2, 'none', '', 0.00, 'D'),
	(34, 1, 80, 1, 0.00, 'cloudy', 1, 'Water', 'Bad', 0.00, 'D'),
	(35, 1, 60, 1, 0.00, 'cloudy', 0, 'Pest', 'Good', 1.00, 'D'),
	(36, 1, 60, 2, 1.00, 'storm', 2, 'none', '', 0.00, 'D'),
	(37, 1, 80, 1, 0.00, 'rain', 1, 'Water', 'Good', 1.00, 'D'),
	(38, 1, 60, 1, 0.00, 'rain', 0, 'Pest', 'Good', 1.00, 'D'),
	(39, 1, 60, 2, 2.00, 'cloudy', 2, 'none', '', 0.00, 'D'),
	(40, 1, 40, 2, 2.00, 'cloudy', 1, 'Water', 'Good', 1.00, 'D'),
	(41, 1, 20, 2, 2.00, 'cloudy', 0, 'Fertilize', 'Good', 1.00, 'D'),
	(42, 1, 20, 3, 4.00, 'rain', 2, 'none', '', 0.00, 'D'),
	(43, 1, 0, 3, 4.00, 'rain', 1, 'Water', 'Good', 1.00, 'D'),
	(44, 1, 80, 1, 0.00, 'rain', 1, 'Water', 'Good', 1.00, 'D'),
	(45, 1, 60, 1, 0.00, 'rain', 0, 'Pest', 'Good', 1.00, 'D'),
	(46, 1, 80, 1, 0.00, 'hot', 1, 'Water', 'Perfect', 2.35, 'D'),
	(47, 1, 60, 1, 0.00, 'hot', 0, 'Fertilize', 'Good', 1.00, 'D'),
	(48, 1, 60, 2, 3.35, 'hot', 2, 'none', '', 0.00, 'D'),
	(49, 1, 60, 2, 3.35, 'cloudy', 2, 'none', '', 0.00, 'D'),
	(50, 1, 20, 2, 3.35, 'cloudy', 0, 'Fertilize', 'Good', 1.00, 'D'),
	(51, 1, 20, 3, 3.15, 'hot', 2, 'none', '', 0.00, 'D');

-- 테이블 farm.lecture 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture` (
  `lecture_id` int NOT NULL AUTO_INCREMENT COMMENT '강의 ID',
  `user_id` int NOT NULL COMMENT 'teacher ID',
  `category` enum('gardening','field','house') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '카테고리 이름(텃밭/노지/하우스)',
  `sub_category` enum('seed','grow','ship') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '서브 카테고리 이름(모종/재배/출하)',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '강의에 쓰일 이미지(썸네일 등)',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '제목',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '강의 요약',
  `subs_count` int NOT NULL DEFAULT '0' COMMENT '구독자 수(직접기입X)',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  PRIMARY KEY (`lecture_id`),
  KEY `FK_lecture_all_users` (`user_id`) USING BTREE,
  KEY `category` (`category`),
  KEY `sub_category` (`sub_category`),
  CONSTRAINT `FK_lecture_board_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의게시판';

-- 테이블 데이터 farm.lecture:~7 rows (대략적) 내보내기
INSERT INTO `lecture` (`lecture_id`, `user_id`, `category`, `sub_category`, `img_url`, `title`, `content`, `subs_count`, `created_at`) VALUES
	(1, 2, 'gardening', 'seed', 'https://storage.googleapis.com/dwproject2/18-Farmer-Resize.jpg', '도시 농부를 위한 모종 심기 마스터 클래스', '"첫 텃밭, 실패 없이 시작하는 비결! 초보 농부도 10단계만 따라 하면 튼튼하고 건강한 모종을 심을 수 있습니다.\r\n모종 고르기부터 성공적인 정식 후 관리까지, 도시 텃밭 가꾸기의 기본기를 확실하게 다지세요."\r\n\r\n이 시리즈는 텃밭 가꾸기의 첫 단계인 모종(苗種) 심기에 초점을 맞춥니다.\r\n모종을 고르는 안목을 기르고, 흙 만들기, 정식(定植, 옮겨 심기), 뿌리 활착 유도, 초기 관리 및 병충해 예방까지 체계적인 과정을 10개의 짧은 비디오로 구성했습니다.\r\n실습 위주의 콘텐츠로, 바로 텃밭에 적용 가능한 노하우를 제공합니다.', 11, '2025-11-13 06:19:32'),
	(2, 2, 'field', 'grow', NULL, '1', 'asd', 2, '2025-11-20 06:54:20'),
	(4, 27, 'field', 'grow', NULL, '3', '345345', 2, '2025-11-20 06:54:46'),
	(6, 1, 'field', 'grow', NULL, '5', '6', 3, '2025-11-21 00:44:13');

-- 테이블 farm.lecture_progress 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_progress` (
  `progress_id` int NOT NULL AUTO_INCREMENT COMMENT '강의 진도 ID',
  `lecture_id` int NOT NULL COMMENT '강의 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `valid_until` timestamp NULL DEFAULT NULL COMMENT '강의 만료 시간',
  `progress` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '진도율(0.00~1.00)',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '강의 진도율 수정 시간',
  PRIMARY KEY (`progress_id`) USING BTREE,
  UNIQUE KEY `lecture_id_user_id` (`lecture_id`,`user_id`),
  KEY `FK_lecture_progress_all_users` (`user_id`),
  KEY `lecture_id` (`lecture_id`),
  CONSTRAINT `FK_lecture_progress_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_progress_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_progress_range` CHECK (((`progress` >= 0.00) and (`progress` <= 1.00)))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='구독 강의 진도';

-- 테이블 데이터 farm.lecture_progress:~1 rows (대략적) 내보내기

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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 QnA';

-- 테이블 데이터 farm.lecture_qna:~3 rows (대략적) 내보내기
INSERT INTO `lecture_qna` (`qna_id`, `lecture_id`, `user_id`, `p_qna_id`, `content`, `created_at`) VALUES
	(1, 1, 1, NULL, '질문입니다', '2025-11-18 03:24:11'),
	(24, 1, 2, 1, '흠', '2025-11-20 07:50:13'),
	(25, 1, 2, NULL, 'ㅇㅇ', '2025-11-20 07:50:34');

-- 테이블 farm.lecture_video 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_video` (
  `video_id` int NOT NULL AUTO_INCREMENT COMMENT '강의 영상 ID',
  `lecture_id` int DEFAULT NULL COMMENT '강의 진도 ID',
  `video_title` varchar(50) DEFAULT NULL COMMENT '강의 영상 제목',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '강의 영상 링크',
  `video_time` int DEFAULT NULL COMMENT '영상 시간(초)',
  PRIMARY KEY (`video_id`),
  KEY `FK_lecture_video_lecture` (`lecture_id`) USING BTREE,
  CONSTRAINT `FK_lecture_video_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 영상';

-- 테이블 데이터 farm.lecture_video:~10 rows (대략적) 내보내기
INSERT INTO `lecture_video` (`video_id`, `lecture_id`, `video_title`, `video_url`, `video_time`) VALUES
	(1, 1, '모종, 왜 중요할까요?', 'https://storage.googleapis.com/dwproject2/2758322-uhd_3840_2160_30fps.mp4', 34),
	(2, 1, '튼튼한 모종 고르는 5가지 기준', 'https://storage.googleapis.com/dwproject2/3195351-uhd_3840_2160_25fps.mp4', 12),
	(3, 1, '모종 심기 최적의 시기 및 환경 조건', NULL, 0),
	(4, 1, '모종 심기 전, 흙 준비 마법: 상토/퇴비 배합', NULL, NULL),
	(5, 1, '뿌리 활착을 돕는 \'물주기\' 기술', NULL, NULL),
	(6, 1, '모종 포트 분리 & 정식 (뿌리 스트레스 최소화)', NULL, NULL),
	(7, 1, '작물별 적정 \'심는 간격\' & 지지대 설치', NULL, NULL),
	(8, 1, '정식 후 첫 3일: 초기 관리 골든 타임', NULL, NULL),
	(9, 1, '웃거름 주기: 모종을 폭풍 성장시키는 영양 비법', NULL, NULL),
	(10, 1, '모종 병충해 예방 및 초기 대처법', NULL, NULL);

-- 테이블 farm.lecture_video_progress 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_video_progress` (
  `video_progress_id` int NOT NULL AUTO_INCREMENT COMMENT '비디오 진도 ID',
  `progress_id` int NOT NULL COMMENT '강의 진도 ID',
  `video_id` int NOT NULL COMMENT '강의 영상 ID',
  `user_id` int NOT NULL,
  `watched_time` int DEFAULT '0' COMMENT '시청시간(초)',
  `last_position` int DEFAULT '0' COMMENT '마지막 시청위치(초)',
  `watched_at` timestamp NULL DEFAULT NULL COMMENT '마지막 시청 시간',
  `progress` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '진도율(0.00~1.00)',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '진도율 100% 달성 시간',
  PRIMARY KEY (`video_progress_id`),
  UNIQUE KEY `unique_progress_video_user` (`user_id`,`progress_id`,`video_id`) USING BTREE,
  KEY `FK_video_progress_lecture_progress` (`progress_id`),
  KEY `FK_video_progress_video` (`video_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK_lecture_video_progress_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_video_progress_lecture_progress` FOREIGN KEY (`progress_id`) REFERENCES `lecture_progress` (`progress_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_video_progress_video` FOREIGN KEY (`video_id`) REFERENCES `lecture_video` (`video_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_video_progress_range` CHECK (((`progress` >= 0.00) and (`progress` <= 1.00)))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='개별 비디오 시청 진도';

-- 테이블 데이터 farm.lecture_video_progress:~1 rows (대략적) 내보내기

-- 테이블 farm.notice 구조 내보내기
CREATE TABLE IF NOT EXISTS `notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '공지사항 ID',
  `user_id` int NOT NULL COMMENT 'admin ID',
  `title` varchar(50) NOT NULL COMMENT '공지사항 제목',
  `content` text NOT NULL COMMENT '공지사항 내용',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '공지에 쓰일 이미지',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '조회수',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
  `is_pinned` tinyint NOT NULL DEFAULT '0' COMMENT '고정여부(고정하면 1)',
  PRIMARY KEY (`notice_id`),
  KEY `FK_all_users` (`user_id`),
  CONSTRAINT `FK_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='공지사항';

-- 테이블 데이터 farm.notice:~1 rows (대략적) 내보내기
INSERT INTO `notice` (`notice_id`, `user_id`, `title`, `content`, `img_url`, `view_count`, `created_at`, `updated_at`, `is_pinned`) VALUES
	(1, 1, 'd', 'xzzxc', NULL, 125, '2025-11-17 00:13:22', '2025-11-24 04:11:15', 1),
	(6, 1, '노시환 롯데 5년 120억 계약', '구란데 ㅋ', NULL, 16, '2025-11-24 02:08:35', '2025-11-24 03:56:03', 0),
	(7, 1, '충격, 강백호 한화 방출', 'ㄹㅇ', NULL, 10, '2025-11-24 03:29:05', '2025-11-24 04:32:12', 0);

-- 테이블 farm.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quiz_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 ID',
  `lecture_id` int DEFAULT NULL COMMENT '강의 ID',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '퀴즈에 쓰일 이미지',
  `question` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '퀴즈 질문',
  `model_answer` text COMMENT '모범 답안',
  PRIMARY KEY (`quiz_id`),
  KEY `FK_quiz_lecture` (`lecture_id`),
  CONSTRAINT `FK_quiz_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈';

-- 테이블 데이터 farm.quiz:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz_attempt 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_attempt` (
  `attempt_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈참여 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `quiz_id` int NOT NULL COMMENT '퀴즈 ID',
  `answer_text` text NOT NULL COMMENT '유저의 주관식 답변',
  `total_score` int NOT NULL DEFAULT '0' COMMENT '총점',
  `pass` enum('pass','fail') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'fail' COMMENT '합격/불합격',
  PRIMARY KEY (`attempt_id`),
  KEY `FK_quiz_attempt_quiz` (`quiz_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK_quiz_attempt_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_attempt_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`quiz_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 참여기록';

-- 테이블 데이터 farm.quiz_attempt:~0 rows (대략적) 내보내기

-- 프로시저 farm.sp_manual_grade_quiz 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_manual_grade_quiz`(
	IN `p_attempt_id` INT,
	IN `p_teacher_id` INT,
	IN `p_manual_score` INT,
	IN `p_feedback` TEXT
)
BEGIN
	 -- 수동채점 프로시저
    DECLARE v_pass_status ENUM('pass', 'fail');
    DECLARE v_user_type ENUM('admin','teacher','student');
    
    -- 채점자가 강사 또는 관리자인지 확인
    SELECT user_type INTO v_user_type
    FROM all_users
    WHERE user_id = p_teacher_id;
    
    IF v_user_type NOT IN ('teacher', 'admin') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '채점 권한이 없습니다.';
    END IF;
    
    -- 점수 유효성 검사
    IF p_manual_score < 0 OR p_manual_score > 100 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '점수는 0~100 사이의 값이어야 합니다.';
    END IF;
    
    -- 합격/불합격 판정
    IF p_manual_score >= 60 THEN
        SET v_pass_status = 'pass';
    ELSE
        SET v_pass_status = 'fail';
    END IF;
    
    -- 채점 결과 업데이트
    UPDATE quiz_attempt
    SET total_score = p_manual_score,
        pass = v_pass_status
    WHERE attempt_id = p_attempt_id;
    
    -- 채점 완료 메시지
    SELECT CONCAT('채점이 완료되었습니다. 점수: ', p_manual_score, '점, 결과: ', v_pass_status) AS result;
END//
DELIMITER ;

-- 프로시저 farm.sp_submit_quiz_answer 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_submit_quiz_answer`(
	IN `p_user_id` INT,
	IN `p_quiz_id` INT,
	IN `p_answer_text` TEXT,
	OUT `p_attempt_id` INT,
	OUT `p_auto_score` INT
)
BEGIN
	 -- 퀴즈답변 제출 및 자동 채점 프로시저
    DECLARE v_model_answer TEXT;
    DECLARE v_keyword_score INT DEFAULT 0;
    DECLARE v_pass_status ENUM('pass', 'fail');
    
    -- 모범 답안 조회
    SELECT model_answer INTO v_model_answer
    FROM quiz
    WHERE quiz_id = p_quiz_id;
    
    -- 기본 키워드 기반 자동 채점 (간단한 버전)
    -- 실제로는 더 정교한 로직이나 AI API 호출로 대체 가능
    SET v_keyword_score = fn_calculate_keyword_score(p_answer_text, v_model_answer);
    
    -- 합격/불합격 판정 (60점 기준)
    IF v_keyword_score >= 60 THEN
        SET v_pass_status = 'pass';
    ELSE
        SET v_pass_status = 'fail';
    END IF;
    
    -- 답변 저장
    INSERT INTO quiz_attempt (
        user_id,
        quiz_id,
        answer_text,
        total_score,
        pass
    ) VALUES (
        p_user_id,
        p_quiz_id,
        p_answer_text,
        v_keyword_score,
        v_pass_status
    );
    
    -- 생성된 attempt_id 반환
    SET p_attempt_id = LAST_INSERT_ID();
    SET p_auto_score = v_keyword_score;
END//
DELIMITER ;

-- 프로시저 farm.sp_update_video_progress 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_update_video_progress`(
	IN `p_progress_id` INT,
	IN `p_video_id` INT,
	IN `p_current_position` INT
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

-- 뷰 farm.user_summary 구조 내보내기
-- VIEW 종속성 오류를 극복하기 위해 임시 테이블을 생성합니다.
CREATE TABLE `user_summary` (
	`이름` LONGTEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`닉네임` VARCHAR(1) NOT NULL COMMENT '닉네임' COLLATE 'utf8mb4_0900_ai_ci',
	`아이디` LONGTEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`이메일` LONGTEXT NULL COLLATE 'utf8mb4_0900_ai_ci'
);

-- 뷰 farm.v_chat_messages 구조 내보내기
-- VIEW 종속성 오류를 극복하기 위해 임시 테이블을 생성합니다.
CREATE TABLE `v_chat_messages` (
	`message_id` INT NOT NULL COMMENT '채팅 기록 ID',
	`room_id` INT NOT NULL COMMENT '실시간 채팅 ID',
	`user_id` INT NULL COMMENT '모든 유저 ID',
	`nickname` VARCHAR(1) NOT NULL COMMENT '닉네임' COLLATE 'utf8mb4_0900_ai_ci',
	`content` TEXT NULL COMMENT '채팅 내용' COLLATE 'utf8mb4_0900_ai_ci',
	`created_at` TIMESTAMP NULL COMMENT '채팅 기록 시간'
);

-- 뷰 farm.v_lecture_subscribers 구조 내보내기
-- VIEW 종속성 오류를 극복하기 위해 임시 테이블을 생성합니다.
CREATE TABLE `v_lecture_subscribers` 
);

-- 트리거 farm.trg_decrement_subs_count 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_decrement_subs_count` AFTER DELETE ON `lecture_progress` FOR EACH ROW BEGIN
    -- lecture_progress에서 항목이 삭제되면
    -- 해당 강의의 구독자 수를 1 감소 (단, 0 이하로는 내려가지 않음)
    UPDATE lecture
    SET subs_count = GREATEST(0, subs_count - 1)
    WHERE lecture_id = OLD.lecture_id;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_increment_subs_count 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_increment_subs_count` AFTER INSERT ON `lecture_progress` FOR EACH ROW BEGIN
    -- lecture_progress에 새로운 항목이 추가되면
    -- 해당 강의의 구독자 수를 1 증가
    UPDATE lecture
    SET subs_count = subs_count + 1
    WHERE lecture_id = NEW.lecture_id;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_update_lecture_progress_on_video 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_update_lecture_progress_on_video` AFTER INSERT ON `lecture_video_progress` FOR EACH ROW BEGIN
	 -- 비디오 진도가 업데이트될 때 강의 전체 진도를 자동으로 계산하는 트리거
    DECLARE v_total_videos INT;
    DECLARE v_completed_videos INT;
    DECLARE v_new_progress DECIMAL(3,2);
    DECLARE v_lecture_id INT;
    
    -- 해당 진도의 강의 ID 조회
    SELECT lp.lecture_id INTO v_lecture_id
    FROM lecture_progress lp
    WHERE lp.progress_id = NEW.progress_id;
    
    -- 해당 강의의 전체 비디오 수 조회
    SELECT COUNT(*) INTO v_total_videos
    FROM lecture_video
    WHERE lecture_id = v_lecture_id;
    
    -- 해당 사용자가 완료한 비디오 수 조회 (진도율 100% 기준)
    SELECT COUNT(*) INTO v_completed_videos
    FROM lecture_video_progress lvp
    WHERE lvp.progress_id = NEW.progress_id
    AND lvp.progress >= 1.00;
    
    -- 전체 진도율 계산 (완료한 비디오 수 / 전체 비디오 수)
    IF v_total_videos > 0 THEN
        SET v_new_progress = v_completed_videos / v_total_videos;
        
        -- 강의 진도율 업데이트
        UPDATE lecture_progress
        SET progress = v_new_progress,
            updated_at = NOW()
        WHERE progress_id = NEW.progress_id;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_update_lecture_progress_on_video_delete 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_update_lecture_progress_on_video_delete` AFTER DELETE ON `lecture_video_progress` FOR EACH ROW BEGIN
	 -- 비디오 진도가 삭제될 때도 강의 진도를 업데이트하는 트리거
    DECLARE v_total_videos INT;
    DECLARE v_completed_videos INT;
    DECLARE v_new_progress DECIMAL(3,2);
    DECLARE v_lecture_id INT;
    
    -- 해당 진도의 강의 ID 조회
    SELECT lp.lecture_id INTO v_lecture_id
    FROM lecture_progress lp
    WHERE lp.progress_id = OLD.progress_id;
    
    -- 해당 강의의 전체 비디오 수 조회
    SELECT COUNT(*) INTO v_total_videos
    FROM lecture_video
    WHERE lecture_id = v_lecture_id;
    
    -- 해당 사용자가 완료한 비디오 수 조회
    SELECT COUNT(*) INTO v_completed_videos
    FROM lecture_video_progress lvp
    WHERE lvp.progress_id = OLD.progress_id
    AND lvp.progress >= 1.00;
    
    -- 전체 진도율 계산
    IF v_total_videos > 0 THEN
        SET v_new_progress = v_completed_videos / v_total_videos;
        
        -- 강의 진도율 업데이트
        UPDATE lecture_progress
        SET progress = v_new_progress,
            updated_at = NOW()
        WHERE progress_id = OLD.progress_id;
    ELSE
        -- 비디오가 없으면 진도율 0으로 설정
        UPDATE lecture_progress
        SET progress = 0.00,
            updated_at = NOW()
        WHERE progress_id = OLD.progress_id;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 farm.trg_update_lecture_progress_on_video_update 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `trg_update_lecture_progress_on_video_update` AFTER UPDATE ON `lecture_video_progress` FOR EACH ROW BEGIN
	 -- 비디오 진도가 수정될 때도 강의 진도를 업데이트하는 트리거
    DECLARE v_total_videos INT;
    DECLARE v_completed_videos INT;
    DECLARE v_new_progress DECIMAL(3,2);
    DECLARE v_lecture_id INT;
    
    -- 진도율이 변경된 경우에만 실행
    IF OLD.progress != NEW.progress THEN
        -- 해당 진도의 강의 ID 조회
        SELECT lp.lecture_id INTO v_lecture_id
        FROM lecture_progress lp
        WHERE lp.progress_id = NEW.progress_id;
        
        -- 해당 강의의 전체 비디오 수 조회
        SELECT COUNT(*) INTO v_total_videos
        FROM lecture_video
        WHERE lecture_id = v_lecture_id;
        
        -- 해당 사용자가 완료한 비디오 수 조회 (진도율 100% 기준)
        SELECT COUNT(*) INTO v_completed_videos
        FROM lecture_video_progress lvp
        WHERE lvp.progress_id = NEW.progress_id
        AND lvp.progress >= 1.00;
        
        -- 전체 진도율 계산
        IF v_total_videos > 0 THEN
            SET v_new_progress = v_completed_videos / v_total_videos;
            
            -- 강의 진도율 업데이트
            UPDATE lecture_progress
            SET progress = v_new_progress,
                updated_at = NOW()
            WHERE progress_id = NEW.progress_id;
        END IF;
    END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 임시 테이블을 제거하고 최종 VIEW 구조를 생성
DROP TABLE IF EXISTS `user_summary`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `user_summary` AS select concat(left(`all_users`.`name`,1),repeat('*',(char_length(`all_users`.`name`) - 1))) AS `이름`,`all_users`.`nickname` AS `닉네임`,concat(left(`all_users`.`id`,3),repeat('*',(char_length(`all_users`.`id`) - 3))) AS `아이디`,concat(repeat('*',(locate('@',`all_users`.`email`) - 1)),substr(`all_users`.`email`,locate('@',`all_users`.`email`))) AS `이메일` from `all_users`
;

-- 임시 테이블을 제거하고 최종 VIEW 구조를 생성
DROP TABLE IF EXISTS `v_chat_messages`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_chat_messages` AS select `cm`.`message_id` AS `message_id`,`cm`.`room_id` AS `room_id`,`cm`.`user_id` AS `user_id`,`au`.`nickname` AS `nickname`,`cm`.`content` AS `content`,`cm`.`created_at` AS `created_at` from (`chat_message` `cm` join `all_users` `au` on((`cm`.`user_id` = `au`.`user_id`)))
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
