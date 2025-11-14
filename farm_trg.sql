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

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
