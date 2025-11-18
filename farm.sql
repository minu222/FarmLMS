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
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='모든 사용자(관리자, 강사, 일반유저)';

-- 테이블 데이터 farm.all_users:~13 rows (대략적) 내보내기
INSERT INTO `all_users` (`user_id`, `user_type`, `id`, `password`, `name`, `nickname`, `birth`, `email`, `intro`) VALUES
	(1, 'admin', 'admin', '1111', '관리자', '관리자', '2025-11-13', 'admin@admin.com', NULL),
	(11, 'student', 'hello', 'dlalsdn123', '이민우', '킹', '2025-11-04', 'w@naver.com', '반갑다 하하하'),
	(12, 'student', 'minu123', 'dlalsdn1', '이민구', '캉쿠쿠', '2025-10-27', 'd@nave.rocm', NULL),
	(13, 'student', 'hihi', 'dlalsdn1', '이민', 'dlalsdn', '2025-10-27', 'd@naver.com', NULL),
	(14, 'teacher', 'teacher', '1234', '강삼', '강사1', '2025-11-13', 'rkddh@a.d', NULL),
	(16, 'student', 'qkrehwo123', 'pdj868312', '박도재', '박도토리', '1996-12-02', 'qkrehwo123@naver.com', NULL),
	(17, 'student', '1234', '1234', 'test', '학생1', '2025-11-14', 'asd@123d.d', NULL),
	(18, 'student', 'mwl21', 'dlalsdn1', '이왕우후', 'minwoo', '2025-10-26', 'dd@naver.com', NULL),
	(19, 'student', 'ohsunjin', 'dlalsdn1', '강백호', 'zzef', '2025-10-30', 'mw@naver.com', NULL),
	(20, 'student', 'tiger', 'dlalsdn1', '김호랑', '어흥', '2025-10-30', 'mw@naver.com', NULL),
	(21, 'student', 'hahahaz', 'dlalsdn1', '이장쿤', '오오옼', '2025-10-30', 'sdci@NAVE.cim', NULL),
	(22, 'student', 'hello123', 'dlalsdn1', 'fsda', '니카', '2025-10-27', 'dd@ad.com', NULL),
	(23, 'student', 'kingz', 'dlalsdn1', 'dd', 'adminz', '2025-10-30', 'dd@naver.com', NULL);

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
  CONSTRAINT `FK_chat_room` FOREIGN KEY (`room_id`) REFERENCES `chat_room` (`room_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='채팅방 사용자';

-- 테이블 데이터 farm.chat_member:~1 rows (대략적) 내보내기
INSERT INTO `chat_member` (`member_id`, `room_id`, `user_id`, `joined_at`) VALUES
	(30, 4, 16, '2025-11-17 08:09:34'),
	(34, 3, 16, '2025-11-17 08:26:19');

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
  CONSTRAINT `FK__chat_room` FOREIGN KEY (`room_id`) REFERENCES `chat_room` (`room_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_chat_message_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='채팅 기록';

-- 테이블 데이터 farm.chat_message:~2 rows (대략적) 내보내기
INSERT INTO `chat_message` (`message_id`, `room_id`, `user_id`, `content`, `created_at`) VALUES
	(45, 4, 16, 'ㅋㅋㅋ', '2025-11-17 08:17:10'),
	(46, 4, 16, 'ㅋㅋㅋ', '2025-11-17 08:55:00');

-- 테이블 farm.chat_room 구조 내보내기
CREATE TABLE IF NOT EXISTS `chat_room` (
  `room_id` int NOT NULL AUTO_INCREMENT COMMENT '실시간 채팅 ID',
  `user_id` int NOT NULL COMMENT 'admin ID',
  `room_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '채팅방 제목',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '생성시간',
  PRIMARY KEY (`room_id`) USING BTREE,
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK_chat_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='실시간 채팅방';

-- 테이블 데이터 farm.chat_room:~4 rows (대략적) 내보내기
INSERT INTO `chat_room` (`room_id`, `user_id`, `room_name`, `created_at`) VALUES
	(1, 11, '나는천재', '2025-11-13 06:35:05'),
	(2, 11, '반갑다 음하하', '2025-11-17 02:41:24'),
	(3, 11, 'ㅋㅋ', '2025-11-17 02:49:35'),
	(4, 11, '어머나', '2025-11-17 02:54:42');

-- 테이블 farm.game_score 구조 내보내기
CREATE TABLE IF NOT EXISTS `game_score` (
  `game_score_id` int NOT NULL AUTO_INCREMENT COMMENT '게임 스코어 ID',
  `user_id` int DEFAULT NULL COMMENT 'student ID',
  `session_id` int DEFAULT NULL COMMENT '게임 세션 ID',
  `game_grade` enum('S','A','B','C','D') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'D' COMMENT '현재 등급(S/A/B/C/D)',
  `total_game_score` int DEFAULT NULL COMMENT '게임누적 총점',
  PRIMARY KEY (`game_score_id`),
  KEY `FK_game_score_all_users` (`user_id`),
  KEY `FK_game_score_game_session` (`session_id`),
  CONSTRAINT `FK_game_score_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_game_score_game_session` FOREIGN KEY (`session_id`) REFERENCES `game_session` (`session_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게임 총점';

-- 테이블 데이터 farm.game_score:~0 rows (대략적) 내보내기

-- 테이블 farm.game_session 구조 내보내기
CREATE TABLE IF NOT EXISTS `game_session` (
  `session_id` int NOT NULL AUTO_INCREMENT COMMENT '게임 세션 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `game_status` enum('progress','clear') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'progress' COMMENT '게임 상태',
  `player_hp` int DEFAULT '100' COMMENT '플레이어 체력',
  `game_day` int DEFAULT '0' COMMENT '현재 경과 일수',
  `growth_rate` int DEFAULT NULL COMMENT '작물 성장률',
  `weather` enum('clear','hot','rain','cloudy','storm') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'clear' COMMENT '날씨(맑음/흐림 등)',
  `daily_action` int DEFAULT '2' COMMENT '행동 횟수',
  `f_action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '첫번째 행동유형',
  `s_action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '두번째 행동유형',
  `daily_score` int DEFAULT NULL COMMENT '점수(하루단위)',
  PRIMARY KEY (`session_id`) USING BTREE,
  KEY `FK_game_record_all_users` (`user_id`),
  CONSTRAINT `FK_game_record_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_game_day_range` CHECK (((`game_day` >= 0) and (`game_day` <= 30))),
  CONSTRAINT `chk_growth_rate` CHECK (((`growth_rate` >= 0.00) and (`growth_rate` <= 1.00))),
  CONSTRAINT `chk_player_hp_max` CHECK (((`player_hp` <= 100) and (`player_hp` >= 0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게임 세션(하루 단위)';

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
  `subs_count` int NOT NULL DEFAULT '0' COMMENT '구독자 수(직접기입X)',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '최초 작성시간',
  PRIMARY KEY (`lecture_id`),
  KEY `FK_lecture_all_users` (`user_id`) USING BTREE,
  KEY `idx_category_difficulty` (`category`,`difficulty`),
  CONSTRAINT `FK_lecture_board_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의게시판';

-- 테이블 데이터 farm.lecture:~2 rows (대략적) 내보내기
INSERT INTO `lecture` (`lecture_id`, `user_id`, `category`, `difficulty`, `img_url`, `title`, `content`, `subs_count`, `created_at`) VALUES
	(1, 1, 'seed', 'beginner', NULL, '테스트', '1번\r\n1\r\n2번\r\n2\r\n3번\r\n3', 2, '2025-11-13 06:19:32'),
	(2, 14, 'grow', 'intermediate', NULL, '테스트2', '### 강의 소개\r\n\r\n안녕하세요, 도시 농부 여러분!\r\n\r\n땅이 없어도, 경험이 없어도 괜찮습니다. 이 강의는 초보자도 쉽게 따라 할 수 있는 벼농사 입문 과정입니다. 작은 베란다나 옥상, 텃밭 등 어떤 공간에서도 건강한 쌀을 직접 수확하는 기쁨을 누릴 수 있도록 파종부터 수확 후 관리까지 벼농사의 모든 과정을 체계적으로 알려드립니다.\r\n\r\n이 강의를 통해 직접 키운 쌀로 지은 따뜻한 밥 한 그릇의 가치를 경험해 보세요!\r\n\r\n---\r\n\r\n### 학습 목표\r\n\r\n- 벼 재배에 필요한 기본적인 도구와 환경을 이해하고 준비할 수 있다.\r\n- 물 관리, 병충해 예방, 비료 시비 등 단계별 핵심 관리 방법을 습득한다.\r\n- 파종부터 모내기, 이삭 패기, 성공적인 수확까지 모든 재배 과정을 자신 있게 진행한다.\r\n\r\n---\r\n\r\n### 챕터 구성 (예시)\r\n\r\n1. 벼농사 기본 이해: 벼의 생애 주기, 필요한 환경 (물, 햇빛, 온도), 준비물 목록\r\n2. 파종과 모 기르기: 적절한 종자 선택, 씨앗 소독, 모판 준비 및 관리 방법\r\n3. 모내기 (이앙): 모내기 적정 시기, 작은 공간에서의 모내기 요령\r\n4. 성장기 관리: 효과적인 물 관리 기술, 비료 주는 시기와 방법 (웃거름)\r\n5. 병충해와 잡초 관리: 벼에 흔한 병충해 종류 및 친환경적 예방/방제법\r\n6. 수확과 탈곡: 벼가 익는 시기 판단, 수확 방법 및 탈곡, 건조, 저장 기술\r\n\r\n---\r\n\r\n### 이런 분들에게 추천합니다\r\n\r\n- 도시에서 나만의 쌀을 직접 키워보고 싶은 초보 농부\r\n- 아이들과 함께 자연을 체험하고 싶은 가족\r\n- 작은 공간을 활용해 새로운 취미를 찾고 싶은 분', 1456, '2025-11-14 00:15:12');

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 QnA';

-- 테이블 데이터 farm.lecture_qna:~4 rows (대략적) 내보내기
INSERT INTO `lecture_qna` (`qna_id`, `lecture_id`, `user_id`, `p_qna_id`, `content`, `created_at`) VALUES
	(1, 2, 11, NULL, '질문입니다', '2025-11-14 07:03:52'),
	(2, 2, 14, 1, '답변이요', '2025-11-14 07:04:06'),
	(7, 2, 17, NULL, 'ㅌㅅㅌ', '2025-11-18 00:44:10'),
	(8, 2, 14, NULL, 'xtx', '2025-11-18 00:44:39');

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 영상';

-- 테이블 데이터 farm.lecture_video:~5 rows (대략적) 내보내기
INSERT INTO `lecture_video` (`video_id`, `lecture_id`, `video_title`, `video_url`, `video_time`) VALUES
	(1, 1, '1강  인', 'https://storage.googleapis.com/dwproject2/2758322-uhd_3840_2160_30fps.mp4', 480),
	(2, 1, '2강  인', NULL, 550),
	(3, 1, '3강  인', NULL, 633),
	(4, 2, '2424124', 'https://youtu.be/2G9pXiZ8av8?si=77i2EgKAZ8QGQVmR', 1234),
	(5, 2, '12434', NULL, 5346);

-- 테이블 farm.lecture_video_progress 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_video_progress` (
  `video_progress_id` int NOT NULL AUTO_INCREMENT COMMENT '비디오 진도 ID',
  `progress_id` int NOT NULL COMMENT '강의 진도 ID',
  `video_id` int NOT NULL COMMENT '강의 영상 ID',
  `watched_time` int DEFAULT '0' COMMENT '시청시간(초)',
  `last_position` int DEFAULT '0' COMMENT '마지막 시청위치(초)',
  `watched_at` timestamp NULL DEFAULT NULL COMMENT '마지막 시청 시간',
  `progress` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '진도율(0.00~1.00)',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '진도율 100% 달성 시간',
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
  `is_pinned` tinyint DEFAULT '0' COMMENT '고정여부(고정하면 1)',
  PRIMARY KEY (`notice_id`),
  KEY `FK_all_users` (`user_id`),
  CONSTRAINT `FK_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='공지사항';

-- 테이블 데이터 farm.notice:~1 rows (대략적) 내보내기
INSERT INTO `notice` (`notice_id`, `user_id`, `title`, `content`, `view_count`, `created_at`, `updated_at`, `is_pinned`) VALUES
	(1, 1, 'd', 'xzzxc', 41, '2025-11-17 00:13:22', '2025-11-17 06:51:28', 1);

-- 테이블 farm.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quiz_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 ID',
  `quiz_category_id` int DEFAULT NULL COMMENT '퀴즈 카테고리 ID',
  `quiz_number` int NOT NULL COMMENT '퀴즈번호',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '퀴즈에 쓰일 이미지',
  `question` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '퀴즈 질문',
  `model_answer` text COMMENT '모범 답안',
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
  `attempt_number` int NOT NULL DEFAULT '1' COMMENT '응시 횟수',
  `answer_text` text NOT NULL COMMENT '유저의 주관식 답변',
  `attempt_at` timestamp NOT NULL DEFAULT (now()) COMMENT '참여시간',
  `graded_by` int DEFAULT NULL COMMENT 'teacher ID',
  `grading_status` enum('pending','graded') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '채점 상태',
  `earned_score` int NOT NULL DEFAULT '0' COMMENT '채점 점수',
  `graded_at` timestamp NULL DEFAULT NULL COMMENT '채점 시간',
  PRIMARY KEY (`attempt_id`),
  UNIQUE KEY `user_id_quiz_id_attempt_number` (`user_id`,`quiz_id`,`attempt_number`) USING BTREE,
  KEY `FK_quiz_attempt_quiz` (`quiz_id`),
  KEY `user_id` (`user_id`),
  KEY `FK_quiz_attempt_all_users_2` (`graded_by`),
  KEY `idx_grading_status` (`grading_status`),
  CONSTRAINT `FK_quiz_attempt_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_attempt_all_users_2` FOREIGN KEY (`graded_by`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_attempt_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`quiz_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 참여기록';

-- 테이블 데이터 farm.quiz_attempt:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz_category 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_category` (
  `quiz_category_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 카테고리 ID',
  `category_name` varchar(50) NOT NULL COMMENT '카테고리 이름',
  `category_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '카테고리에 쓰일 이미지',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '카테고리 설명',
  `difficulty` enum('beginner','intermediate','advanced') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'beginner' COMMENT '난이도',
  `total_quiz` int DEFAULT NULL COMMENT '총 문제 수',
  `total_quiz_score` int DEFAULT NULL COMMENT '만점',
  `pass_score` int DEFAULT '60' COMMENT '합격점수',
  `time_limit` int DEFAULT NULL COMMENT '제한 시간(분)',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '작성시간',
  PRIMARY KEY (`quiz_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 카테고리';

-- 테이블 데이터 farm.quiz_category:~0 rows (대략적) 내보내기

-- 테이블 farm.quiz_score 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_score` (
  `score_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 총점 ID',
  `quiz_category_id` int NOT NULL COMMENT '퀴즈 카테고리 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `total_score` int NOT NULL COMMENT '총점',
  `pass` enum('pass','fail') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'fail' COMMENT '합격/불합격',
  PRIMARY KEY (`score_id`),
  UNIQUE KEY `category_id_user_id` (`quiz_category_id`,`user_id`) USING BTREE,
  KEY `FK_quiz_score_all_users` (`user_id`),
  KEY `category_id` (`quiz_category_id`) USING BTREE,
  CONSTRAINT `FK_quiz_score_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_score_quiz_category` FOREIGN KEY (`quiz_category_id`) REFERENCES `quiz_category` (`quiz_category_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 총점/합격 및 불합격';

-- 테이블 데이터 farm.quiz_score:~0 rows (대략적) 내보내기

-- 프로시저 farm.sp_grade_quiz_attempt 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_grade_quiz_attempt`(
	IN `p_attempt_id` INT,
	IN `p_graded_by` INT,
	IN `p_earned_score` INT
)
BEGIN
    DECLARE v_user_id INT;
    DECLARE v_quiz_id INT;
    DECLARE v_quiz_category_id INT;
    DECLARE v_quiz_score INT;
    DECLARE v_current_total INT;
    DECLARE v_pass_score INT;
    DECLARE v_is_pass ENUM('합격', '불합격');
    DECLARE v_existing_score_id INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '채점 처리 중 오류가 발생했습니다.';
    END;
    
    START TRANSACTION;
    
    -- 1. 시도 정보 조회
    SELECT user_id, quiz_id
    INTO v_user_id, v_quiz_id
    FROM quiz_attempt
    WHERE attempt_id = p_attempt_id
    AND grading_status = '채점대기';
    
    IF v_user_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '채점 대기 중인 시도를 찾을 수 없습니다.';
    END IF;
    
    -- 2. 퀴즈 정보 조회
    SELECT quiz_category_id, quiz_score
    INTO v_quiz_category_id, v_quiz_score
    FROM quiz
    WHERE quiz_id = v_quiz_id;
    
    -- 3. 점수 유효성 검증
    IF p_earned_score < 0 OR p_earned_score > v_quiz_score THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '유효하지 않은 점수입니다.';
    END IF;
    
    -- 4. 채점 정보 업데이트
    UPDATE quiz_attempt
    SET 
        earned_score = p_earned_score,
        graded_by = p_graded_by,
        grading_status = '채점완료',
        graded_at = NOW()
    WHERE attempt_id = p_attempt_id;
    
    -- 5. 해당 카테고리의 총점 계산
    SELECT 
        COALESCE(SUM(qa.earned_score), 0),
        qc.pass_score
    INTO v_current_total, v_pass_score
    FROM quiz_attempt qa
    INNER JOIN quiz q ON qa.quiz_id = q.quiz_id
    INNER JOIN quiz_category qc ON q.quiz_category_id = qc.quiz_category_id
    WHERE qa.user_id = v_user_id
    AND q.quiz_category_id = v_quiz_category_id
    AND qa.grading_status = '채점완료'
    GROUP BY qc.pass_score;
    
    -- 6. 합격 여부 판단
    IF v_current_total >= v_pass_score THEN
        SET v_is_pass = '합격';
    ELSE
        SET v_is_pass = '불합격';
    END IF;
    
    -- 7. quiz_score 테이블 업데이트 또는 삽입
    SELECT score_id INTO v_existing_score_id
    FROM quiz_score
    WHERE quiz_category_id = v_quiz_category_id
    AND user_id = v_user_id;
    
    IF v_existing_score_id IS NOT NULL THEN
        -- 기존 레코드 업데이트
        UPDATE quiz_score
        SET 
            total_score = v_current_total,
            pass = v_is_pass
        WHERE score_id = v_existing_score_id;
    ELSE
        -- 새 레코드 삽입
        INSERT INTO quiz_score (
            quiz_category_id,
            user_id,
            total_score,
            pass
        ) VALUES (
            v_quiz_category_id,
            v_user_id,
            v_current_total,
            v_is_pass
        );
        
        SET v_existing_score_id = LAST_INSERT_ID();
    END IF;
    
    COMMIT;
    
    -- 결과 반환
    SELECT 
        p_attempt_id AS attempt_id,
        v_user_id AS user_id,
        v_quiz_id AS quiz_id,
        p_earned_score AS earned_score,
        v_current_total AS total_score,
        v_is_pass AS pass_status,
        '채점이 완료되었습니다.' AS message;
        
        
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
