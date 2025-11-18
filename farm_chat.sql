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

-- 테이블 데이터 farm.chat_member:~2 rows (대략적) 내보내기
INSERT INTO `chat_member` (`member_id`, `room_id`, `user_id`, `joined_at`) VALUES
	(40, 12, 25, '2025-11-18 03:03:51'),
	(42, 12, 1, '2025-11-18 03:05:19');

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
	(82, 12, 25, 'ㅋㅋㅋ', '2025-11-18 03:03:55'),
	(83, 12, 1, 'ㅏㅏㅏㅏㅏ', '2025-11-18 03:04:22'),
	(84, 12, 25, 'ㄴㅇㄹㄴㅇㅁ', '2025-11-18 03:04:25'),
	(85, 12, 25, '후후훗', '2025-11-18 03:04:36'),
	(86, 12, 1, '내말 안들으면 처형이다', '2025-11-18 03:04:41'),
	(87, 12, 25, 'ㅋㅋ', '2025-11-18 03:05:46'),
	(88, 12, 25, 'ㅋㅋ', '2025-11-18 03:05:46'),
	(89, 12, 25, 'ㅋㅋ', '2025-11-18 03:05:47'),
	(90, 12, 25, 'ㅋㅋ', '2025-11-18 03:05:47'),
	(91, 12, 25, 'ㅋㅋ', '2025-11-18 03:05:48');

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
	(12, 25, '코딩고수모임', '2025-11-18 03:03:51');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
