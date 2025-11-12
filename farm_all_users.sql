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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='모든 사용자(관리자, 강사, 일반유저)';

-- 테이블 데이터 farm.all_users:~2 rows (대략적) 내보내기
INSERT INTO `all_users` (`user_id`, `user_type`, `id`, `password`, `name`, `nickname`, `birth`, `email`, `intro`) VALUES
	(9, 'student', 'student01', 'password123', '김학생', '초보농부', '2000-01-15', 'student01@example.com', '농업을 배우고 싶습니다.'),
	(10, 'student', 'student02', 'password123', '이학생', '고수농부', '2000-01-15', 'student02@example.com', '농업을 배우고 싶습니다.');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
