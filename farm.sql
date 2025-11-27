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

-- 테이블 farm.ai_planner 구조 내보내기
CREATE TABLE IF NOT EXISTS `ai_planner` (
  `planner_id` int NOT NULL AUTO_INCREMENT COMMENT '플래너 ID',
  `user_id` int NOT NULL COMMENT '유저 ID',
  `planner_name` varchar(100) NOT NULL COMMENT '플래너 제목',
  `planner_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'AI가 생성한 HTML 결과물',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '생성일시',
  PRIMARY KEY (`planner_id`),
  KEY `FK_ai_planner_all_users` (`user_id`),
  CONSTRAINT `FK_ai_planner_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI 플래너';

-- 테이블 데이터 farm.ai_planner:~5 rows (대략적) 내보내기
INSERT INTO `ai_planner` (`planner_id`, `user_id`, `planner_name`, `planner_content`, `created_at`) VALUES
	(4, 16, '베란다', '<p>안녕하세요! 주말농장 성공 플래너 AI입니다. 고객님의 소중한 정보를 바탕으로 가장 성공 가능성이 높고 흥미로운 텃밭 설계 및 초기 재배 계획을 세워드릴게요. 베란다에서 나만의 신선한 채소와 열매를 수확하는 즐거움을 곧 경험하실 수 있을 거예요!</p>\n<h2>1. 최적 텃밭 모델: 콤팩트 베란다 홈팜</h2>\n<p>고객님께서는 아파트 베란다 공간과 3~6시간의 일조량을 가지고 계시네요. 예산과 경험 수준을 고려했을 때, 공간 효율성을 극대화하고 다양한 작물을 키울 수 있는 <strong>\'콤팩트 베란다 홈팜\'</strong> 모델을 추천합니다.</p>\n<ul>\n<li><strong>다단 화분 스탠드 (수직형):</strong> 좁은 공간에서도 여러 종류의 쌈채소를 층층이 키울 수 있어 매우 효율적입니다. 햇빛을 골고루 받을 수 있도록 회전형이나 이동식 바퀴가 달린 제품을 선택하면 더욱 편리해요.</li>\n<li><strong>개별 대형 플랜터 (열매 채소용):</strong> 방울토마토와 같은 열매 채소는 뿌리가 깊게 뻗고 많은 영양분을 필요로 하므로, 충분한 크기의 개별 화분에 심는 것이 좋습니다. 햇빛이 가장 잘 드는 곳에 배치해 주세요.</li>\n<li><strong>배치 아이디어:</strong> 베란다 창가에 다단 화분 스탠드를 두고, 그 옆이나 앞쪽에 개별 플랜터를 두어 햇빛을 최대한 활용하는 구조를 추천합니다.</li>\n</ul>\n<p><strong>성공 포인트:</strong> 콤팩트 홈팜은 작은 공간에서도 다양한 작물을 키우는 기쁨을 선사하며, 이동이 쉬워 햇빛 방향에 따라 조절하기도 용이합니다.</p>\n<h2>2. 추천 작물: 베란다 초보 농부 맞춤 쌈채소 &amp; 방울토마토</h2>\n<p>고객님의 텃밭 목적(식용 쌈채소/열매)과 초보 경험, 그리고 베란다 일조량을 고려하여 쉽고 빠르게 수확의 기쁨을 느낄 수 있는 작물들을 선정했습니다.</p>\n<ul>\n<li><strong>상추 (쌈채소):</strong>\n<ul>\n<li><strong>특징:</strong> 가장 대표적인 쌈채소로, 씨앗 발아율이 좋고 병충해에 강해 초보자도 쉽게 키울 수 있습니다. 겉잎을 따서 수확하는 방식으로 꾸준히 신선한 쌈채소를 즐길 수 있어요.</li>\n<li><strong>재배 난이도:</strong> ★☆☆☆☆ (매우 쉬움)</li>\n<li><strong>수확 속도:</strong> 20~30일 후 첫 수확 가능</li>\n</ul>\n</li>\n<li><strong>청경채 (쌈채소):</strong>\n<ul>\n<li><strong>특징:</strong> 아삭한 식감과 부드러운 맛으로 샐러드나 볶음 요리에 활용하기 좋습니다. 상추와 마찬가지로 키우기 쉽고 비교적 빠르게 자랍니다.</li>\n<li><strong>재배 난이도:</strong> ★☆☆☆☆ (매우 쉬움)</li>\n<li><strong>수확 속도:</strong> 25~35일 후 첫 수확 가능</li>\n</ul>\n</li>\n<li><strong>방울토마토 (열매 채소):</strong>\n<ul>\n<li><strong>특징:</strong> 베란다 텃밭에서 열매 수확의 즐거움을 느끼기에 가장 좋은 작물 중 하나입니다. 햇빛만 충분하면 튼튼하게 자라 달콤한 열매를 선사합니다. 모종으로 시작하면 더욱 쉽게 키울 수 있어요.</li>\n<li><strong>재배 난이도:</strong> ★★☆☆☆ (쉬움)</li>\n<li><strong>수확 속도:</strong> 모종 심은 후 60~80일 후 첫 수확 가능</li>\n</ul>\n</li>\n</ul>\n<p><strong>성공 포인트:</strong> 이 작물들은 베란다 환경에 잘 적응하고, 비교적 적은 햇빛에도 잘 자라며, 초보자도 실패 없이 키울 확률이 매우 높습니다.</p>\n<h2>3. 초기 투자 재료 리스트: 5만원 이하 알뜰 꾸러미</h2>\n<p>고객님의 예산(5만원 이하)을 준수하면서도 성공적인 텃밭을 위한 필수 재료들을 엄선했습니다.</p>\n<table>\n<thead>\n<tr><th align="left">품목 분류</th><th align="left">추천 품목</th><th align="left">예상 가격대 (원)</th><th align="left">비고</th></tr>\n</thead>\n<tbody>\n<tr><td align="left"><strong>용기</strong></td><td align="left">다단 화분 스탠드 (3단)</td><td align="left">15,000 ~ 20,000</td><td align="left">쌈채소용, 공간 효율성 최고!</td></tr>\n<tr><td align="left"> </td><td align="left">대형 플랜터/화분 (지름 25cm 이상) 1개</td><td align="left">5,000 ~ 8,000</td><td align="left">방울토마토용, 뿌리 성장에 충분한 공간 확보</td></tr>\n<tr><td align="left"><strong>흙/비료</strong></td><td align="left">베란다 텃밭용 상토 (10L)</td><td align="left">5,000 ~ 7,000</td><td align="left">배수와 보수성이 좋은 전용 흙</td></tr>\n<tr><td align="left"> </td><td align="left">유기농 액비 (소용량)</td><td align="left">3,000 ~ 5,000</td><td align="left">작물 성장 촉진, 한 달에 1~2회 사용</td></tr>\n<tr><td align="left"><strong>씨앗/모종</strong></td><td align="left">상추 씨앗 1봉</td><td align="left">1,000 ~ 2,000</td><td align="left">다양한 품종 중 1가지 선택</td></tr>\n<tr><td align="left"> </td><td align="left">청경채 씨앗 1봉</td><td align="left">1,000 ~ 2,000</td><td align="left"> </td></tr>\n<tr><td align="left"> </td><td align="left">방울토마토 모종 1개</td><td align="left">3,000 ~ 5,000</td><td align="left">씨앗보다 모종이 초보에게 훨씬 유리</td></tr>\n<tr><td align="left"><strong>필수 도구</strong></td><td align="left">미니 모종삽 &amp; 갈퀴 세트</td><td align="left">5,000 ~ 8,000</td><td align="left">흙을 다루고 모종 심을 때 필수</td></tr>\n<tr><td align="left"> </td><td align="left">물뿌리개 (1~2L)</td><td align="left">3,000 ~ 5,000</td><td align="left">섬세한 물 주기 가능</td></tr>\n<tr><td align="left"> </td><td align="left">면장갑</td><td align="left">1,000 ~ 2,000</td><td align="left">흙으로부터 손 보호</td></tr>\n<tr><td align="left"><strong>합계</strong></td><td align="left"> </td><td align="left"><strong>약 42,000 ~ 59,000원</strong></td><td align="left">(예산 범위 내에서 선택)</td></tr>\n</tbody>\n</table>\n<p><strong>성공 포인트:</strong> 위 리스트는 최소한의 투자로 최대의 효과를 볼 수 있도록 구성되었습니다. 필요한 품목들을 한 번에 구매하여 배송비를 절약하는 것도 좋은 방법입니다.</p>\n<h2>4. LMS 추천 강의: 초보 농부를 위한 성공 가이드</h2>\n<p>고객님의 성공적인 첫 텃밭 경험을 위해 주말농장 LMS의 맞춤형 강의들을 추천해 드립니다!</p>\n<ul>\n<li><strong>[무료] 처음 시작하는 베란다 텃밭 가이드:</strong>\n<ul>\n<li>내용: 베란다 텃밭의 기본 개념, 공간 활용법, 작물 선택 노하우 등 초보자가 알아야 할 모든 것을 담았습니다.</li>\n<li>추천 이유: 텃밭을 시작하기 전 전체적인 그림을 그리는 데 큰 도움이 됩니다.</li>\n</ul>\n</li>\n<li><strong>[챌린지] 초보도 성공하는 쌈채소 재배법 30일 챌린지:</strong>\n<ul>\n<li>내용: 상추, 청경채 등 쌈채소 씨앗 파종부터 수확까지 일자별 미션과 전문가 피드백을 제공합니다.</li>\n<li>추천 이유: 추천 작물인 쌈채소를 직접 키우며 실전 경험을 쌓고, 꾸준히 동기를 부여받을 수 있습니다.</li>\n</ul>\n</li>\n<li><strong>[유료] 우리 집 방울토마토 달콤하게 키우기 (모종 편):</strong>\n<ul>\n<li>내용: 방울토마토 모종 심기, 물 주기, 영양 관리, 지지대 세우기, 병충해 예방 등 열매 수확을 위한 핵심 노하우를 상세히 알려드립니다.</li>\n<li>추천 이유: 베란다에서 방울토마토를 성공적으로 키우기 위한 전문적인 지식을 습득할 수 있습니다.</li>\n</ul>\n</li>\n<li><strong>[무료] 흙이 건강해야 작물도 건강! 상토 선택 및 관리법:</strong>\n<ul>\n<li>내용: 텃밭 흙의 중요성, 베란다 텃밭에 적합한 상토 선택법, 영양분 관리법 등 건강한 흙 만들기에 대한 기초 지식입니다.</li>\n<li>추천 이유: 작물 성장의 가장 기본이 되는 흙 관리에 대한 이해를 높여줍니다.</li>\n</ul>\n</li>\n</ul>\n<p><strong>성공 포인트:</strong> 이 강의들을 통해 텃밭 가꾸기의 기본기를 다지고, 작물별 맞춤 관리법을 익히면 분명 멋진 수확의 기쁨을 누리실 수 있을 거예요!</p>\n<p>고객님의 베란다 텃밭 성공을 진심으로 응원합니다! 궁금한 점이 있다면 언제든지 다시 찾아주세요.</p>\n', '2025-11-25 08:08:14'),
	(6, 27, '123234234', '<h2>안녕하세요! 주말농장 성공 플래너 AI입니다. 고객님의 베란다 텃밭 성공을 위해 특별히 맞춤형 계획을 설계해 드릴게요! 🏡💚</h2>\n<p>아파트 베란다에서 쌈채소를 직접 키우는 즐거움은 정말 특별하답니다. 초보자도 쉽게 시작하고 맛있는 수확의 기쁨을 누릴 수 있도록 도와드릴게요!</p>\n<h2>1. 최적 텃밭 모델: \'미니 컨테이너 베란다 텃밭\'</h2>\n<p>고객님께서는 아파트 베란다에서 하루 3~6시간의 일조량을 가지고 계시며, 식용 쌈채소를 목표로 하시는 초보자이시네요. 이러한 조건에서는 **\'미니 컨테이너 베란다 텃밭\'**이 가장 적합합니다.</p>\n<ul>\n<li><strong>모델 특징:</strong>\n<ul>\n<li><strong>공간 효율성:</strong> 좁은 베란다 공간을 최대한 활용할 수 있는 소형 화분이나 재배 용기를 이용합니다.</li>\n<li><strong>이동 용이성:</strong> 일조량 변화에 따라 화분의 위치를 쉽게 옮길 수 있어 작물 생장에 유리합니다.</li>\n<li><strong>쉬운 관리:</strong> 물 주기, 흙 갈기 등 초기 관리가 비교적 간단하여 초보자에게 적합합니다.</li>\n<li><strong>비용 효율성:</strong> 5만원 이하 예산으로도 충분히 시작할 수 있습니다.</li>\n</ul>\n</li>\n<li><strong>추천 배치:</strong> 베란다 난간 가까이 햇빛이 가장 잘 드는 곳에 1~3개 정도의 화분을 배치하는 것을 추천합니다.</li>\n</ul>\n<h2>2. 추천 작물: \'초보자도 성공하는 쌈채소 3총사\'</h2>\n<p>고객님의 텃밭 목적(식용 쌈채소)과 초보자 경험 수준, 그리고 베란다 일조량을 고려하여 빠르고 쉽게 수확의 기쁨을 느낄 수 있는 작물들을 추천합니다.</p>\n<ul>\n<li><strong>상추 (로메인 상추, 청상추 등):</strong>\n<ul>\n<li><strong>재배 난이도:</strong> ★☆☆☆☆ (매우 쉬움)</li>\n<li><strong>특징:</strong> 씨앗이나 모종으로 쉽게 키울 수 있으며, 비교적 짧은 시간에 수확이 가능합니다. 한 번 심으면 여러 번 잎을 따서 먹을 수 있어 가성비가 좋습니다.</li>\n<li><strong>일조량:</strong> 햇빛을 좋아하지만 반그늘에서도 잘 자라 베란다 환경에 적합합니다.</li>\n</ul>\n</li>\n<li><strong>쑥갓:</strong>\n<ul>\n<li><strong>재배 난이도:</strong> ★★☆☆☆ (쉬움)</li>\n<li><strong>특징:</strong> 독특한 향과 맛으로 요리에 활용도가 높습니다. 생장 속도가 빨라 초보자도 성장의 재미를 느낄 수 있습니다.</li>\n<li><strong>일조량:</strong> 상추와 비슷하게 반그늘에서도 잘 자라며, 너무 강한 햇빛보다는 적당한 햇빛이 좋습니다.</li>\n</ul>\n</li>\n<li><strong>치커리 (적치커리, 쌈치커리):</strong>\n<ul>\n<li><strong>재배 난이도:</strong> ★★☆☆☆ (쉬움)</li>\n<li><strong>특징:</strong> 쌉쌀한 맛이 일품인 쌈채소로, 상추와 함께 곁들이면 맛의 균형을 이룹니다. 병충해에 강한 편이라 키우기 수월합니다.</li>\n<li><strong>일조량:</strong> 역시 반그늘에서도 잘 자라며, 베란다 환경에서 무난하게 키울 수 있습니다.</li>\n</ul>\n</li>\n</ul>\n<h2>3. 초기 투자 재료 리스트 (예산 5만원 이하)</h2>\n<p>고객님의 5만원 이하 예산에 맞춰 꼭 필요한 필수 재료들로 리스트를 구성했습니다. 최소한의 투자로 최대의 효과를 내보세요!</p>\n<table>\n<thead>\n<tr><th align="left">카테고리</th><th align="left">품목</th><th align="left">예상 가격 (원)</th><th align="left">비고</th></tr>\n</thead>\n<tbody>\n<tr><td align="left"><strong>필수 도구</strong></td><td align="left">미니 모종삽/손삽</td><td align="left">5,000</td><td align="left">흙을 다루고 모종을 심을 때 사용</td></tr>\n<tr><td align="left"> </td><td align="left">작은 물뿌리개</td><td align="left">7,000</td><td align="left">섬세한 물 주기 및 영양제 희석용</td></tr>\n<tr><td align="left"><strong>흙/비료</strong></td><td align="left">베란다용 상토 (10L)</td><td align="left">10,000</td><td align="left">작물 생장에 필수적인 영양분이 포함된 흙</td></tr>\n<tr><td align="left"><strong>씨앗/모종</strong></td><td align="left">상추, 쑥갓, 치커리 씨앗 또는 모종</td><td align="left">15,000</td><td align="left">씨앗은 더 저렴하지만, 모종은 바로 키울 수 있어 초보에게 유리</td></tr>\n<tr><td align="left"><strong>용기</strong></td><td align="left">플라스틱 화분 또는 재배 용기 (3개)</td><td align="left">10,000</td><td align="left">배수 구멍이 있는 가벼운 용기 선택</td></tr>\n<tr><td align="left"><strong>총 예상 비용</strong></td><td align="left"><strong>47,000</strong></td><td align="left"> </td><td align="left">예산 범위 내에서 충분히 시작 가능</td></tr>\n</tbody>\n</table>\n<ul>\n<li><strong>팁:</strong> 씨앗 대신 작은 모종을 구입하면 초기 실패율을 줄이고 빠르게 수확을 경험할 수 있습니다. 인터넷 쇼핑몰이나 가까운 화원, 종묘사에서 저렴하게 구매할 수 있습니다.</li>\n</ul>\n<h2>4. LMS 추천 강의</h2>\n<p>고객님의 \'미니 컨테이너 베란다 텃밭\'과 \'초보 가드닝\'에 딱 맞는 강의들을 LMS에서 찾아봤어요. 이 강의들을 통해 더욱 쉽고 재미있게 텃밭 생활을 시작해 보세요!</p>\n<h2>4. LMS 추천 강의</h2>\n<p>고객님의 \'미니 컨테이너 베란다 텃밭\'과 \'초보 가드닝\'에 딱 맞는 강의들을 LMS에서 찾아봤어요. 이 강의들을 통해 더욱 쉽고 재미있게 텃밭 생활을 시작해 보세요!</p>\n<p>아쉽게도 현재 \'텃밭 기초\'에 대한 직접적인 강의는 없지만, 베란다 텃밭 시작에 가장 중요한 \'씨앗 파종\'에 대한 유료 강의가 있어 추천해 드립니다.</p>\n<ul>\n<li><strong><a href="javascript:void(0);" onclick="handleCourseClick(31, \'미니 텃밭 씨앗 파종 기초: 발아율 100% 도전 보러가기\', false); return false;">미니 텃밭 씨앗 파종 기초: 발아율 100% 도전 보러가기</a></strong>\n<ul>\n<li><strong>강사:</strong> 베란다정원사</li>\n<li><strong>내용:</strong> 베란다 텃밭의 첫걸음인 씨앗 파종에 대한 모든 것을 알려주는 강의입니다. 씨앗을 심고 싹을 틔우는 기초 방법을 상세히 배울 수 있어 초보자에게 큰 도움이 될 거예요!</li>\n</ul>\n</li>\n</ul>\n<p>추후 \'베란다 텃밭 관리\'나 \'초보 쌈채소 재배\'와 관련된 무료 또는 챌린지 강의도 업데이트될 예정이니, LMS 웹사이트를 자주 방문하여 확인해 주세요! 지금부터 시작하는 고객님의 텃밭 여정을 응원합니다! 😊</p>\n', '2025-11-26 06:47:42'),
	(7, 27, '3463463456', '<p>안녕하세요! 주말농장 성공 플래너 AI입니다. 고객님의 소중한 텃밭 꿈을 응원하며, 성공적인 베란다 텃밭을 위한 맞춤형 플래너를 제안해 드릴게요!</p>\n<h2>1. 최적 텃밭 모델</h2>\n<p>고객님께서는 아파트 베란다/발코니에서 오전/오후 3~6시간의 일조량을 가지고 계시고, 5만원 이하의 예산으로 초보자로서 식용 쌈채소/열매를 재배하고 싶어 하십니다. 이러한 조건을 고려했을 때, <strong>\'미니 컨테이너 텃밭\'</strong> 모델이 가장 적합합니다!</p>\n<ul>\n<li><strong>모델 특징:</strong> 공간 활용도가 높고, 이동이 쉬워 햇볕을 따라 놓아줄 수 있습니다. 또한, 초기 비용 부담이 적어 초보자분들도 부담 없이 시작하기 좋습니다.</li>\n<li><strong>추천 용기:</strong> 플라스틱 화분, 재활용 스티로폼 박스, 다단형 플랜터 등을 활용하여 좁은 공간에서도 다양한 작물을 키울 수 있습니다.</li>\n<li><strong>배치 팁:</strong> 햇볕이 가장 잘 드는 시간대에 맞춰 용기를 옮겨주면 작물 성장에 더욱 도움이 됩니다.</li>\n</ul>\n<h2>2. 추천 작물</h2>\n<p>고객님의 텃밭 목적(식용 쌈채소/열매), 일조량, 초보 경험 수준 및 예산을 고려하여, 텃밭 성공률을 높여줄 작물을 추천합니다.</p>\n<ul>\n<li><strong>[필수 추천] 잎채소:</strong>\n<ul>\n<li><strong>상추:</strong> 키우기 매우 쉽고 수확량이 풍부하여 초보자에게 가장 인기 있는 작물입니다. 약 30일이면 첫 수확이 가능하며, 잎을 따내도 계속 자라 오랫동안 즐길 수 있습니다.</li>\n<li><strong>깻잎:</strong> 특유의 향으로 다양한 요리에 활용되며, 상추와 마찬가지로 키우기 쉬워 초보자도 쉽게 성공할 수 있습니다.</li>\n</ul>\n</li>\n<li><strong>[선택 추천] 열매채소:</strong>\n<ul>\n<li><strong>방울토마토:</strong> 3~6시간의 일조량이라면 재배가 가능합니다. 초보자도 열매를 수확하는 기쁨을 느낄 수 있으며, 성장하는 모습을 관찰하는 재미가 큽니다. (단, 햇볕이 부족하면 열매가 잘 맺히지 않을 수 있으니 주의 깊은 관리가 필요합니다.)</li>\n</ul>\n</li>\n</ul>\n<h2>3. 초기 투자 재료 리스트</h2>\n<p>5만원 이하의 예산으로 효율적인 베란다 텃밭을 시작하실 수 있도록 최소한의 필수 재료 리스트를 제안합니다.</p>\n<ul>\n<li><strong>필수 도구 (약 1만원):</strong>\n<ul>\n<li>작은 모종삽 1개</li>\n<li>물뿌리개 1개 (소형)</li>\n</ul>\n</li>\n<li><strong>흙/비료 (약 2만원):</strong>\n<ul>\n<li>친환경 배양토(상토) 10L: 약 1만 5천원</li>\n<li>유기질 비료 또는 지렁이 분변토 (소량): 약 5천원</li>\n</ul>\n</li>\n<li><strong>씨앗/모종 (약 1만원):</strong>\n<ul>\n<li>상추 씨앗 또는 모종 2~3개: 약 3천원</li>\n<li>깻잎 씨앗 또는 모종 2~3개: 약 3천원</li>\n<li>방울토마토 모종 1개 (선택): 약 4천원</li>\n</ul>\n</li>\n<li><strong>재배 용기 (약 1만원, 재활용 시 0원):</strong>\n<ul>\n<li>플라스틱 화분 (중형 3~4개) 또는 재활용 스티로폼 박스/페트병 등을 활용하면 비용 절감에 큰 도움이 됩니다.</li>\n</ul>\n</li>\n</ul>\n<p>총 예상 비용: 약 4~5만원 (용기 재활용 여부에 따라 변동 가능)</p>\n<h2>4. LMS 추천 강의</h2>\n<p>고객님의 베란다 컨테이너 텃밭 성공을 위해 관련 강의를 추천해 드립니다!</p>\n<ul>\n<li>\n<p><strong>베란다 텃밭 첫걸음을 위한 기초 강의:</strong></p>\n<ul>\n<li><a href="javascript:void(0);" onclick="handleCourseClick(31, \'미니 텃밭 씨앗 파종 기초: 발아율 100% 도전 보러가기\', false); return false;">미니 텃밭 씨앗 파종 기초: 발아율 100% 도전 보러가기</a> - 씨앗부터 건강하게 싹 틔우는 노하우를 배울 수 있습니다.</li>\n<li><a href="javascript:void(0);" onclick="handleCourseClick(1, \'도시 농부를 위한 모종 심기 마스터 클래스 보러가기\', false); return false;">도시 농부를 위한 모종 심기 마스터 클래스 보러가기</a> - 모종을 심는 가장 기본적인 방법을 알려주어 초보자에게 안성맞춤입니다.</li>\n<li><a href="javascript:void(0);" onclick="handleCourseClick(4, \'저예산으로 시작하는 쌈채소 수직 텃밭 설계 보러가기\', false); return false;">저예산으로 시작하는 쌈채소 수직 텃밭 설계 보러가기</a> - 저예산으로 쌈채소 텃밭을 구성하는 아이디어를 얻을 수 있습니다.</li>\n</ul>\n</li>\n<li>\n<p><strong>추천 작물 재배 및 관리 강의:</strong></p>\n<ul>\n<li><a href="javascript:void(0);" onclick="handleCourseClick(11, \'베란다 쌈채소의 신선한 수확과 장기 저장법 보러가기\', false); return false;">베란다 쌈채소의 신선한 수확과 장기 저장법 보러가기</a> - 수확한 쌈채소를 더욱 신선하게 즐기는 방법을 배울 수 있습니다.</li>\n<li><a href="javascript:void(0);" onclick="handleCourseClick(2, \'아이와 함께 하는 상추 &amp; 바질 모종 심기 보러가기\', false); return false;">아이와 함께 하는 상추 &amp; 바질 모종 심기 보러가기</a> - 상추 모종 심기에 대한 실질적인 도움을 받을 수 있습니다.</li>\n<li><a href="javascript:void(0);" onclick="handleCourseClick(6, \'베란다 토마토 재배: 곁순 제거와 열매 관리 심화 보러가기\', false); return false;">베란다 토마토 재배: 곁순 제거와 열매 관리 심화 보러가기</a> - 방울토마토 재배 시 중요한 곁순 제거와 열매 관리 방법을 익힐 수 있습니다.</li>\n</ul>\n</li>\n</ul>\n<p>이 플래너와 함께 고객님의 베란다 텃밭이 풍성한 수확의 기쁨으로 가득 차기를 바랍니다! 궁금한 점이 있다면 언제든지 다시 물어봐 주세요!</p>\n', '2025-11-26 07:04:40');

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
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='모든 사용자(관리자, 강사, 일반유저)';

-- 테이블 데이터 farm.all_users:~8 rows (대략적) 내보내기
INSERT INTO `all_users` (`user_id`, `user_type`, `id`, `password`, `name`, `nickname`, `birth`, `email`, `intro`) VALUES
	(1, 'admin', 'admin', '1111', '관리자', '관리자', '2025-11-13', 'admin@admin.com', '나야 관리자.'),
	(2, 'teacher', '1234', '1234', 'AI더미강사', '미래농부', '2025-11-18', 'dummy.ai@email.com', 'AI 플래너 연동 테스트를 위한 더미 강사 계정입니다.'),
	(16, 'student', 'qkrehwo123', '1234', '박도재', '박도토리', '1996-12-02', 'qkrehwo123@naver.com', '안녕하세요'),
	(25, 'student', 'hello', 'dlalsdn1', '이민우', '이황우', '2025-11-10', 'mw@naver.com', '민우님이다 음하하'),
	(27, 'student', '123', '123', '강건호', '강건호아님', '2025-11-01', 'rkdrjsgh123@naver.com', NULL),
	(35, 'student', 'jmj10338', 'jmj691107', '정민주', '초보농부', '1998-10-07', 'jmj10338@gmail.com', NULL),
	(37, 'student', 'tkdduq', '1234', '김상엽', '김상엽', '2025-11-25', 'tkdduq@naver.com', NULL),
	(39, 'teacher', 'imteacher', 'dlalsdn1', '이선생', '난티처', '2025-10-29', 'mw@naver.com', NULL),
	(41, 'teacher', 'garden_min', 'pass1234', '김민지', '베란다정원사', '1992-05-15', 'garden.min@email.com', '아파트 베란다를 활용한 미니 텃밭 설계 및 실내 작물 관리 전문가입니다. 초보자를 위한 쉽고 재미있는 강의를 제공합니다.'),
	(42, 'teacher', 'field_pro', 'pass1234', '박현우', '토양마스터', '1985-11-20', 'field.park@email.com', '노지 재배 토양 개량 및 친환경 퇴비 제작 전문가입니다. 흙의 중요성을 강조하며 건강한 농사를 돕습니다.'),
	(43, 'teacher', 'pest_care', 'pass1234', '이수진', '자연방제', '1978-08-03', 'pest.lee@email.com', '농약 없는 친환경 병충해 방제법과 작물 생육 관리 노하우를 전문적으로 강의합니다. 중급자 이상에게 인기가 높습니다.'),
	(44, 'teacher', 'smart_farm', 'pass1234', '최영철', '스마트하우스', '1970-02-28', 'smart.choi@email.com', '하우스 시설 운영 및 스마트팜 환경 제어 시스템 구축 전문가입니다. 높은 수확량을 위한 기술 집약적 강의를 제공합니다.'),
	(45, 'teacher', 'harvest_tip', 'pass1234', '정은아', '수확의여왕', '1988-07-10', 'harvest.jung@email.com', '작물별 최적 수확 시기 판단 및 수확물 가공(잼, 피클 등) 및 유통 전략 전문가입니다. 수확의 즐거움을 극대화하세요!');

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
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='채팅방 사용자';

-- 테이블 데이터 farm.chat_member:~7 rows (대략적) 내보내기
INSERT INTO `chat_member` (`member_id`, `room_id`, `user_id`, `joined_at`) VALUES
	(51, 13, 16, '2025-11-18 05:16:15'),
	(52, 13, 25, '2025-11-18 05:16:27'),
	(56, 14, 35, '2025-11-24 00:44:28'),
	(57, 14, 16, '2025-11-24 02:18:32'),
	(59, 13, 1, '2025-11-24 06:18:34'),
	(61, 14, 25, '2025-11-25 00:45:25'),
	(62, 15, 25, '2025-11-25 03:42:58'),
	(63, 15, 2, '2025-11-26 02:40:55');

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
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='채팅 기록';

-- 테이블 데이터 farm.chat_message:~16 rows (대략적) 내보내기
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
	(139, 14, 16, '안녕하세요', '2025-11-24 03:16:57'),
	(140, 14, 2, '네 안녕', '2025-11-24 06:13:27'),
	(141, 14, 2, '?', '2025-11-24 06:13:28'),
	(142, 14, 35, '반갑습니다!', '2025-11-24 06:33:13'),
	(143, 15, 25, 'ㅎㅇㅎㅇ', '2025-11-25 03:43:03'),
	(144, 15, 2, 'ㅎㅇㅎㅇ는 반말이고', '2025-11-26 02:41:04');

-- 테이블 farm.chat_room 구조 내보내기
CREATE TABLE IF NOT EXISTS `chat_room` (
  `room_id` int NOT NULL AUTO_INCREMENT COMMENT '실시간 채팅 ID',
  `user_id` int NOT NULL COMMENT 'admin ID',
  `room_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '채팅방 제목',
  `created_at` timestamp NULL DEFAULT (now()) COMMENT '생성시간',
  PRIMARY KEY (`room_id`) USING BTREE,
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK_chat_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='실시간 채팅방';

-- 테이블 데이터 farm.chat_room:~1 rows (대략적) 내보내기
INSERT INTO `chat_room` (`room_id`, `user_id`, `room_name`, `created_at`) VALUES
	(13, 16, '새로 만든 방', '2025-11-18 05:16:15'),
	(14, 35, '문외한도 쉽게 이해하는 기초! 토양 관리부터 수확까지!', '2025-11-24 00:44:28'),
	(15, 25, '반갑다 음하하', '2025-11-25 03:42:58');

-- 함수 farm.fn_calculate_keyword_score 구조 내보내기
DELIMITER //
CREATE FUNCTION `fn_calculate_keyword_score`(
	`p_answer` TEXT,
	`p_model_answer` TEXT
) RETURNS int
    DETERMINISTIC
BEGIN
    DECLARE v_score INT DEFAULT 0;
    DECLARE v_answer_length INT;
    DECLARE v_model_length INT;
    
    SET v_answer_length = CHAR_LENGTH(TRIM(p_answer));
    SET v_model_length = CHAR_LENGTH(TRIM(p_model_answer));
    
    -- 기본 점수 계산 (단순 길이 비율)
    IF v_model_length > 0 THEN
        SET v_score = LEAST(100, (v_answer_length * 100) / v_model_length);
    END IF;
    
    -- 키워드 매칭 보너스 (간단한 버전)
    IF INSTR(LOWER(p_answer), LOWER(p_model_answer)) > 0 THEN
        SET v_score = LEAST(100, v_score + 30);
    END IF;
    
    RETURN v_score;
END//
DELIMITER ;

-- 테이블 farm.game 구조 내보내기
CREATE TABLE IF NOT EXISTS `game` (
  `session_id` int NOT NULL AUTO_INCREMENT COMMENT '게임 세션 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `player_hp` int DEFAULT '100' COMMENT '플레이어 체력',
  `game_day` int DEFAULT '0' COMMENT '현재 경과 일수',
  `growth_rate` decimal(5,2) DEFAULT '0.00' COMMENT '작물 성장률',
  `weather` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '날씨(맑음/흐림 등)',
  `daily_action` int DEFAULT '2' COMMENT '행동 횟수',
  `action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '첫번째 행동유형',
  `mini_result` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '행동 결과',
  `action_score` decimal(5,2) DEFAULT '0.00' COMMENT '점수(미니게임 단위)',
  `game_grade` enum('S','A','B','C','D') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'D' COMMENT '현재 등급(S/A/B/C/D)',
  PRIMARY KEY (`session_id`) USING BTREE,
  KEY `FK_game_record_all_users` (`user_id`),
  CONSTRAINT `FK_game_record_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_game_day_range` CHECK (((`game_day` >= 0) and (`game_day` <= 30))),
  CONSTRAINT `chk_growth_rate` CHECK (((`growth_rate` >= 0.00) and (`growth_rate` <= 100.00)))
) ENGINE=InnoDB AUTO_INCREMENT=144 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게임';

-- 테이블 데이터 farm.game:~2 rows (대략적) 내보내기

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
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의게시판';

-- 테이블 데이터 farm.lecture:~8 rows (대략적) 내보내기
INSERT INTO `lecture` (`lecture_id`, `user_id`, `category`, `sub_category`, `img_url`, `title`, `content`, `subs_count`, `created_at`) VALUES
	(1, 41, 'gardening', 'seed', 'https://storage.googleapis.com/dwproject2/18-Farmer-Resize.jpg', '도시 농부를 위한 모종 심기 마스터 클래스', '"첫 텃밭, 실패 없이 시작하는 비결! 초보 농부도 10단계만 따라 하면 튼튼하고 건강한 모종을 심을 수 있습니다.\r\n모종 고르기부터 성공적인 정식 후 관리까지, 도시 텃밭 가꾸기의 기본기를 확실하게 다지세요."\r\n\r\n이 시리즈는 텃밭 가꾸기의 첫 단계인 모종(苗種) 심기에 초점을 맞춥니다.\r\n모종을 고르는 안목을 기르고, 흙 만들기, 정식(定植, 옮겨 심기), 뿌리 활착 유도, 초기 관리 및 병충해 예방까지 체계적인 과정을 10개의 짧은 비디오로 구성했습니다.\r\n실습 위주의 콘텐츠로, 바로 텃밭에 적용 가능한 노하우를 제공합니다.', 16, '2025-11-13 06:19:32'),
	(2, 41, 'gardening', 'seed', '[img_url_채우기]', '아이와 함께 하는 상추 & 바질 모종 심기', '자녀 교육 목적에 특화된 강의입니다. 아이들과 함께 쉽게 다룰 수 있는 상추와 허브(바질)의 모종을 베란다 화분에 심는 과정과 관찰 일지 작성법을 배웁니다.', 0, '2025-11-26 05:23:18'),
	(3, 41, 'gardening', 'seed', '[img_url_채우기]', '교육용 텃밭: 씨앗 준비편', '자녀 교육 목적으로 텃밭을 시작하는 부모님을 위한 강의. 아이들이 쉽게 관찰할 수 있는 작물(강낭콩, 해바라기 등)의 씨앗 준비 및 심기 방법을 다룹니다.', 0, '2025-11-26 05:23:18'),
	(4, 41, 'gardening', 'seed', '[img_url_채우기]', '저예산으로 시작하는 쌈채소 수직 텃밭 설계', '5만원 이하의 예산으로 페트병, 목재 등을 활용하여 베란다에 설치하는 수직 텃밭 모델 설계법과 쌈채소 모종 심기 노하우를 제공합니다.', 0, '2025-11-26 05:23:18'),
	(5, 41, 'gardening', 'seed', '[img_url_채우기]', '분갈이 흙 믹스 비율: 화분 속 토양 개량 기초', '성공적인 텃밭을 위한 흙 만들기가 핵심입니다. 마사토, 피트모스, 상토의 최적 비율을 제시하고, 텃밭 목적에 맞는 흙을 만드는 방법을 배웁니다.', 0, '2025-11-26 05:23:18'),
	(6, 43, 'gardening', 'grow', '[img_url_채우기]', '베란다 토마토 재배: 곁순 제거와 열매 관리 심화', '중급 재배자를 위한 열매채소 관리 강의. 방울토마토, 고추 등의 곁순 제거(순지르기) 타이밍과 열매가 맺힌 후의 비료 관리법을 심도 있게 다룹니다.', 0, '2025-11-26 05:23:18'),
	(7, 41, 'gardening', 'grow', '[img_url_채우기]', '실내 허브 수확 후 영양제 및 물주기 노하우', '관상용/치유 목적의 허브(로즈마리, 민트 등)를 오랫동안 건강하게 키우는 관리법입니다. 수확 후 관리, 액체 비료 사용법, 겨울철 실내 월동 준비를 배웁니다.', 0, '2025-11-26 05:23:18'),
	(8, 43, 'gardening', 'grow', '[img_url_채우기]', '친환경 천연 살충제 제조 및 사용법', '농약을 사용하지 않고 베이킹소다, 식초 등을 활용하여 텃밭의 흔한 병충해(진딧물, 흰가루병)를 예방하고 방제하는 중급 기술 강의입니다.', 0, '2025-11-26 05:23:18'),
	(9, 43, 'gardening', 'grow', '[img_url_채우기]', '수직 텃밭 자동 관수 시스템 DIY (중급)', '베란다/옥상의 수직 텃밭 효율화를 위한 자가 제작 자동 관수 장치 설치 강의입니다. 시간당 급수량 계산법과 타이머 설정법을 실습합니다.', 0, '2025-11-26 05:23:18'),
	(10, 43, 'gardening', 'grow', '[img_url_채우기]', '화분 속 뿌리 활착 최적화 비료 사용 가이드', '작물이 잘 자라지 않을 때 필요한 맞춤 영양제 사용법입니다. 질소, 인산, 칼륨의 역할을 이해하고 작물 생육 단계별로 필요한 비료를 투입하는 방법을 배웁니다.', 0, '2025-11-26 05:23:18'),
	(11, 45, 'gardening', 'ship', '[img_url_채우기]', '베란다 쌈채소의 신선한 수확과 장기 저장법', '식용 목적 텃밭에서 쌈채소(상추, 깻잎)를 가장 맛있을 때 수확하는 타이밍과, 냉장 보관 시 신선도를 최대화하는 비법을 공개합니다.', 0, '2025-11-26 05:23:18'),
	(12, 45, 'gardening', 'ship', '[img_url_채우기]', '텃밭 작물로 만드는 홈메이드 피클 & 잼 레시피', '수확한 오이, 토마토, 베리류 등을 활용하여 건강하고 맛있는 피클과 잼을 만드는 가공 레시피 강의입니다.', 0, '2025-11-26 05:23:18'),
	(13, 45, 'gardening', 'ship', '[img_url_채우기]', '허브 건조 및 오일 추출 활용법 (관상용/치유)', '수확한 허브(로즈마리, 라벤더)를 건조하여 포푸리나 허브 오일을 추출하는 방법을 배웁니다. 힐링 및 관상용 목적에 적합합니다.', 0, '2025-11-26 05:23:18'),
	(14, 45, 'gardening', 'ship', '[img_url_채우기]', '작은 텃밭 생산물의 이웃 나눔을 위한 포장 디자인', '이웃이나 지인에게 수확물을 선물할 때, 신선도를 유지하면서 예쁘게 포장하는 디자인 및 포장재 선택 노하우를 다룹니다.', 0, '2025-11-26 05:23:18'),
	(15, 45, 'gardening', 'ship', '[img_url_채우기]', '텃밭 폐기물을 활용한 친환경 퇴비 만들기', '작물 찌꺼기, 잡초, 채소 껍질 등을 활용하여 다음 재배기를 위한 퇴비를 만드는 순환 농법의 기초를 배웁니다.', 0, '2025-11-26 05:23:18'),
	(16, 42, 'field', 'seed', '[img_url_채우기]', '공동 텃밭 최적 구획 설계 및 작물 배치 전략', '넓은 노지 공동 텃밭을 효율적으로 나누어 사용하는 방법을 배웁니다. 연작 장애 방지를 위한 작물 배치 계획 수립에 중점을 둡니다.', 0, '2025-11-26 05:23:18'),
	(17, 39, 'gardening', 'grow', NULL, '재밌는 농사 짓기', 'ㄵ', 0, '2025-11-26 05:21:50'),
	(18, 42, 'field', 'seed', '[img_url_채우기]', '고추, 가지 등 대형 작물 모종의 노지 정식 시기', '노지 재배의 핵심인 모종 정식 시기를 결정하는 방법(늦서리 위험 분석 포함)과, 뿌리가 잘 내리도록 심는 기술을 배웁니다.', 0, '2025-11-26 05:23:18'),
	(19, 42, 'field', 'seed', '[img_url_채우기]', '밭 주변 풀(잡초) 관리를 위한 멀칭(Mulching) 심화', '잡초를 막고 토양의 수분과 온도를 유지하는 멀칭 작업의 중요성과, 작물별로 적합한 멀칭 재료(비닐, 짚)를 선택하는 방법을 배웁니다.', 0, '2025-11-26 05:23:18'),
	(20, 42, 'field', 'seed', '[img_url_채우기]', '태양열 소독을 활용한 노지 밭 병충해 예방', '노지 밭의 초기 단계에서 토양 속 병원균과 해충을 예방하기 위해 태양열 소독(피복)을 실시하는 방법을 상세히 다룹니다.', 0, '2025-11-26 05:23:18'),
	(21, 43, 'field', 'ship', '[img_url_채우기]', '김장용 배추 대량 수확 및 품질 검사 노하우', '노지에서 재배한 배추를 김장철에 맞춰 대량 수확하는 적정 시기 판별법과, 상품성 확보를 위한 품질 검사 기준을 배웁니다.', 0, '2025-11-26 05:23:18'),
	(22, 2, 'field', 'ship', '[img_url_채우기]', '노지 작물의 유통: 산지 직거래 포장 및 운송 전략', '직접 재배한 농산물을 소비자에게 직거래하거나 소규모 마켓에 판매하기 위한 포장 기술 및 운송 중 신선도 유지 방법을 숙련자 관점에서 다룹니다.', 0, '2025-11-26 05:23:18'),
	(23, 43, 'field', 'ship', '[img_url_채우기]', '저장고 없이 작물 보관하는 땅속 저장 기술', '감자, 고구마, 무 등 뿌리채소를 장기간 신선하게 저장하기 위한 전통적인 땅속 저장 기술(움 만들기)과 현대적인 보관법을 비교 분석합니다.', 0, '2025-11-26 05:23:18'),
	(24, 43, 'field', 'ship', '[img_url_채우기]', '노지 열매채소(고추/가지)의 최적 수확 시기와 후속 관리', '고추, 가지 등 열매채소의 품질과 수확량을 극대화하는 수확 타이밍과, 수확 후 다음 수확까지 작물 관리를 이어가는 방법을 배웁니다.', 0, '2025-11-26 05:23:18'),
	(25, 43, 'field', 'ship', '[img_url_채우기]', '농업 부산물을 활용한 퇴비 생산 및 판매', '수확 후 남은 농업 부산물을 고품질 퇴비로 만들어 재판매하거나 다음 해 농사에 활용하는 자원 순환 노하우를 다룹니다.', 0, '2025-11-26 05:23:18'),
	(26, 44, 'house', 'grow', '[img_url_채우기]', '스마트 하우스 환경: 온도, 습도, CO2 정밀 제어 심화', '전문적인 하우스 재배자를 위한 강의. 난방, 환기, 가습 시스템을 통합하여 작물 생육 최적의 조건을 유지하는 환경 제어 기술을 다룹니다.', 0, '2025-11-26 05:23:18'),
	(27, 44, 'house', 'grow', '[img_url_채우기]', '수경재배 기초: NFT(박막 수경) 시스템 설치 및 관리', '하우스 또는 베란다 시설 재배를 위한 수경재배 시스템 설치 강의입니다. NFT(Nutrient Film Technique) 원리를 이해하고 양액 관리법을 배웁니다.', 0, '2025-11-26 05:23:18'),
	(28, 44, 'house', 'grow', '[img_url_채우기]', '하우스 내 특수 작물(버섯, 새싹삼) 재배 기초', '일반적인 채소 외에 버섯, 새싹삼 등 높은 투자와 기술을 요하는 특수 작물을 하우스에서 재배하는 환경 조성 및 초기 관리법을 배웁니다.', 0, '2025-11-26 05:23:18'),
	(29, 44, 'house', 'ship', '[img_url_채우기]', '하우스 고품질 딸기의 계절 외 출하 전략', '딸기 등 고수익 작물을 계절과 무관하게 재배하여 안정적으로 출하하는 기술입니다. 품종 선택부터 포장 및 판매 전략을 숙련자 관점에서 다룹니다.', 0, '2025-11-26 05:23:18'),
	(30, 44, 'house', 'ship', '[img_url_채우기]', '하우스 자동화 수확 시스템 효율 분석 및 도입 가이드', '대규모 하우스 운영자를 위한 강의. 자동화된 수확 및 선별 장비의 도입 비용, 효율, 유지보수 노하우 등을 비교 분석합니다.', 0, '2025-11-26 05:23:18'),
	(31, 41, 'gardening', 'seed', '[img_url_채우기]', '미니 텃밭 씨앗 파종 기초: 발아율 100% 도전', '아파트 베란다 텃밭을 위한 씨앗 파종의 정석. 어떤 흙을 써야 하는지, 물주기 타이밍, 발아 온도 관리 등 기초 중의 기초를 초보자 맞춤으로 설명합니다.', 0, '2025-11-26 05:23:18'),
	(32, 42, 'field', 'seed', '[img_url_채우기]', '퇴비와 석회를 이용한 노지 밭 토양 개량 실습', '노지 밭을 처음 시작하는 분들을 위한 필수 강의입니다. 토양 산도(pH) 측정법과, 퇴비 및 석회를 활용하여 토양을 개량하는 실제 노하우를 다룹니다.', 0, '2025-11-26 05:23:18');

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
  KEY `lecture_id` (`lecture_id`),
  KEY `user_id_progress` (`user_id`,`progress`),
  CONSTRAINT `FK_lecture_progress_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_progress_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_progress_range` CHECK (((`progress` >= 0.00) and (`progress` <= 1.00)))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='구독 강의 진도';

-- 테이블 데이터 farm.lecture_progress:~4 rows (대략적) 내보내기
INSERT INTO `lecture_progress` (`progress_id`, `lecture_id`, `user_id`, `valid_until`, `progress`, `updated_at`) VALUES
	(7, 1, 27, NULL, 1.00, '2025-11-26 08:40:52'),
	(8, 1, 16, NULL, 0.20, '2025-11-25 05:23:58'),
	(9, 1, 1, NULL, 0.10, '2025-11-24 06:11:35'),
	(10, 1, 2, NULL, 0.10, '2025-11-25 01:17:53'),
	(11, 1, 25, NULL, 0.20, '2025-11-26 08:02:20');

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
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 QnA';

-- 테이블 데이터 farm.lecture_qna:~7 rows (대략적) 내보내기
INSERT INTO `lecture_qna` (`qna_id`, `lecture_id`, `user_id`, `p_qna_id`, `content`, `created_at`) VALUES
	(1, 1, 1, NULL, '질문입니다', '2025-11-18 03:24:11'),
	(39, 1, 25, NULL, '질문있다. 답변 가능하냐?', '2025-11-25 03:40:06'),
	(40, 1, 2, 39, '반말 ㄴ ', '2025-11-25 03:43:40'),
	(49, 1, 16, NULL, '강의가 진짜 재미없네여;;', '2025-11-25 06:09:53'),
	(53, 1, 2, 49, 'ㄹㅇ?', '2025-11-26 01:26:40'),
	(57, 1, 2, 49, '많이 재미없나요?', '2025-11-26 01:30:23'),
	(60, 1, 25, NULL, '아녕하쎼요 방갑고', '2025-11-26 03:31:40'),
	(61, 1, 2, 60, '방가워요 민우군', '2025-11-26 03:32:58'),
	(62, 1, 2, 1, '뭔데요', '2025-11-26 03:33:21'),
	(64, 17, 25, NULL, '와 재밌다 하하하', '2025-11-26 05:22:04');

-- 테이블 farm.lecture_sub 구조 내보내기
CREATE TABLE IF NOT EXISTS `lecture_sub` (
  `sub_id` int NOT NULL AUTO_INCREMENT COMMENT '강의 구독 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `lecture_id` int NOT NULL COMMENT '강의 ID',
  PRIMARY KEY (`sub_id`),
  KEY `FK_lecture_sub_all_users` (`user_id`),
  KEY `FK_lecture_sub_lecture` (`lecture_id`),
  CONSTRAINT `FK_lecture_sub_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lecture_sub_lecture` FOREIGN KEY (`lecture_id`) REFERENCES `lecture` (`lecture_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='강의 구독';

-- 테이블 데이터 farm.lecture_sub:~9 rows (대략적) 내보내기
INSERT INTO `lecture_sub` (`sub_id`, `user_id`, `lecture_id`) VALUES
	(1, 27, 1),
	(2, 2, 1),
	(3, 16, 1),
	(4, 25, 1),
	(13, 25, 17),
	(14, 16, 32),
	(16, 2, 22),
	(17, 2, 30),
	(24, 41, 19),
	(25, 41, 1);

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
  `user_id` int NOT NULL COMMENT 'student ID',
  `watched_time` int DEFAULT '0' COMMENT '시청시간(초)',
  `last_position` int DEFAULT '0' COMMENT '마지막 시청위치(초)',
  `watched_at` timestamp NULL DEFAULT NULL COMMENT '마지막 시청 시간',
  `progress` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '진도율(0.00~1.00)',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '진도율 100% 달성 시간',
  PRIMARY KEY (`video_progress_id`),
  UNIQUE KEY `unique_progress_video_user` (`user_id`,`progress_id`,`video_id`) USING BTREE,
  KEY `FK_video_progress_video` (`video_id`),
  KEY `progress_id_user_id` (`progress_id`,`user_id`),
  CONSTRAINT `FK_lecture_video_progress_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_video_progress_lecture_progress` FOREIGN KEY (`progress_id`) REFERENCES `lecture_progress` (`progress_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_video_progress_video` FOREIGN KEY (`video_id`) REFERENCES `lecture_video` (`video_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_video_progress_range` CHECK (((`progress` >= 0.00) and (`progress` <= 1.00)))
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='개별 비디오 시청 진도';

-- 테이블 데이터 farm.lecture_video_progress:~6 rows (대략적) 내보내기
INSERT INTO `lecture_video_progress` (`video_progress_id`, `progress_id`, `video_id`, `user_id`, `watched_time`, `last_position`, `watched_at`, `progress`, `completed_at`) VALUES
	(10, 7, 1, 27, 21, 21, '2025-11-24 05:02:19', 0.62, NULL),
	(11, 7, 2, 27, 12, 12, '2025-11-24 05:19:55', 1.00, '2025-11-25 04:59:38'),
	(12, 8, 1, 16, 34, 34, '2025-11-24 05:28:13', 1.00, '2025-11-25 05:23:40'),
	(13, 9, 1, 1, 34, 34, '2025-11-24 06:11:12', 1.00, '2025-11-24 06:11:41'),
	(14, 10, 2, 2, 12, 12, '2025-11-25 01:17:40', 1.00, '2025-11-25 01:17:54'),
	(15, 8, 2, 16, 12, 12, '2025-11-25 05:23:50', 1.00, '2025-11-25 05:23:59'),
	(16, 11, 1, 25, 34, 34, '2025-11-26 07:39:11', 1.00, '2025-11-26 07:39:43'),
	(17, 11, 2, 25, 12, 12, '2025-11-26 08:02:15', 1.00, '2025-11-26 08:02:21');

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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='공지사항';

-- 테이블 데이터 farm.notice:~3 rows (대략적) 내보내기
INSERT INTO `notice` (`notice_id`, `user_id`, `title`, `content`, `img_url`, `view_count`, `created_at`, `updated_at`, `is_pinned`) VALUES
	(1, 1, 'd', 'xzzxc', '/uploads/notice/84478180ca6f492e8d1ff235d7024f9c.PNG', 141, '2025-11-17 00:13:22', '2025-11-26 08:33:51', 1),
	(7, 1, '충격, 강백호 한화 방출', 'ㄹㅇ', '/uploads/notice/784b23b5dd614e28bea786cdce3e9513.jpg', 39, '2025-11-24 03:29:05', '2025-11-26 03:32:18', 0),
	(11, 1, '김서현, "mlb 진출 고려중" 포부 밝혀...', 'ㄹㅇ', '/uploads/notice/93bf6c54458b436ba43d7e5e1fc37cc6.png', 16, '2025-11-24 05:20:09', '2025-11-26 03:33:37', 0),
	(16, 1, 'UrbanGreen 박도재 대표, 소매치기범으로 오해받아..', 'ㄹㅇ', NULL, 5, '2025-11-26 03:35:26', '2025-11-26 23:55:33', 0);

-- 테이블 farm.quiz 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz` (
  `quiz_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 ID',
  `video_id` int DEFAULT NULL COMMENT '강의 영상 ID',
  `quiz_number` int DEFAULT NULL COMMENT '퀴즈 번호',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '퀴즈에 쓰일 이미지',
  `question` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '퀴즈 질문',
  `model_answer` text COMMENT '모범 답안',
  `pass_score` int NOT NULL DEFAULT '60' COMMENT '합격 점수',
  PRIMARY KEY (`quiz_id`),
  UNIQUE KEY `video_id_quiz_number` (`video_id`,`quiz_number`),
  KEY `FK_quiz_lecture_video` (`video_id`) USING BTREE,
  CONSTRAINT `FK_quiz_lecture_video` FOREIGN KEY (`video_id`) REFERENCES `lecture_video` (`video_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈';

-- 테이블 데이터 farm.quiz:~4 rows (대략적) 내보내기
INSERT INTO `quiz` (`quiz_id`, `video_id`, `quiz_number`, `img_url`, `question`, `model_answer`, `pass_score`) VALUES
	(7, 1, 1, NULL, '모종이 중요한 이유는?', '중요하니까', 60),
	(8, 1, 2, NULL, '모종의 뜻은?', '답이 모죵?', 60),
	(9, 1, 3, NULL, '모종의 형의 이름은?', '오종', 60),
	(10, 1, 4, NULL, '내 나이는?', '28', 60),
	(11, 1, 5, NULL, '내 몸무게는?', '2000', 60),
	(12, 2, 1, NULL, '퀴즈입니다', '답', 60),
	(13, 2, 2, NULL, '퀴즈2', '답2', 60);

-- 테이블 farm.quiz_attempt 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_attempt` (
  `attempt_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈참여 ID',
  `user_id` int NOT NULL COMMENT 'student ID',
  `quiz_id` int NOT NULL COMMENT '퀴즈 ID',
  `answer_text` text NOT NULL COMMENT '유저의 주관식 답변',
  `earned_score` int NOT NULL DEFAULT '0' COMMENT '획득 점수',
  `attempted_at` timestamp NULL DEFAULT (now()) COMMENT '등록 시간',
  PRIMARY KEY (`attempt_id`),
  KEY `FK_quiz_attempt_quiz` (`quiz_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `FK_quiz_attempt_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_attempt_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`quiz_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 참여기록';

-- 테이블 데이터 farm.quiz_attempt:~48 rows (대략적) 내보내기
INSERT INTO `quiz_attempt` (`attempt_id`, `user_id`, `quiz_id`, `answer_text`, `earned_score`, `attempted_at`) VALUES
	(67, 25, 7, '중요하니까', 20, '2025-11-26 07:45:07'),
	(68, 25, 8, '답이 모죵', 0, '2025-11-26 07:45:07'),
	(69, 25, 9, '오종', 20, '2025-11-26 07:45:07'),
	(70, 25, 10, '28', 20, '2025-11-26 07:45:07'),
	(71, 25, 11, '2000', 20, '2025-11-26 07:45:07'),
	(72, 16, 7, 'z', 0, '2025-11-26 07:55:33'),
	(73, 16, 8, 'z', 0, '2025-11-26 07:55:33'),
	(74, 16, 9, 'z', 0, '2025-11-26 07:55:33'),
	(75, 16, 10, 'z', 0, '2025-11-26 07:55:33'),
	(76, 16, 11, 'z', 0, '2025-11-26 07:55:33'),
	(77, 25, 12, 'd', 0, '2025-11-26 08:02:28'),
	(78, 25, 13, 'd', 0, '2025-11-26 08:02:28'),
	(79, 25, 12, '답', 20, '2025-11-26 08:02:55'),
	(80, 25, 13, '답2', 20, '2025-11-26 08:02:55'),
	(81, 25, 7, '중요해서', 0, '2025-11-26 08:05:10'),
	(82, 25, 8, '답이모죵', 0, '2025-11-26 08:05:10'),
	(83, 25, 9, '오종', 20, '2025-11-26 08:05:10'),
	(84, 25, 10, '28', 20, '2025-11-26 08:05:10'),
	(85, 25, 11, '2000', 20, '2025-11-26 08:05:10');

-- 테이블 farm.quiz_score 구조 내보내기
CREATE TABLE IF NOT EXISTS `quiz_score` (
  `score_id` int NOT NULL AUTO_INCREMENT COMMENT '퀴즈 총점 ID',
  `video_id` int DEFAULT NULL COMMENT '강의 영상 ID',
  `user_id` int DEFAULT NULL COMMENT 'student ID',
  `total_score` int NOT NULL DEFAULT '0' COMMENT '총점',
  `pass` enum('pass','fail') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'fail' COMMENT '합격/불합격',
  PRIMARY KEY (`score_id`),
  UNIQUE KEY `video_id_user_id` (`video_id`,`user_id`),
  KEY `FK_quiz_score_all_users` (`user_id`),
  KEY `video_id` (`video_id`),
  CONSTRAINT `FK_quiz_score_all_users` FOREIGN KEY (`user_id`) REFERENCES `all_users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quiz_score_lecture_video` FOREIGN KEY (`video_id`) REFERENCES `lecture_video` (`video_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='퀴즈 총점';

-- 테이블 데이터 farm.quiz_score:~1 rows (대략적) 내보내기
INSERT INTO `quiz_score` (`score_id`, `video_id`, `user_id`, `total_score`, `pass`) VALUES
	(1, 2, 27, 60, 'pass'),
	(12, 2, 16, 0, 'fail'),
	(13, 1, 25, 60, 'pass'),
	(17, 1, 16, 0, 'fail'),
	(18, 2, 25, 40, 'fail');

-- 프로시저 farm.sp_manual_grade_quiz 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_manual_grade_quiz`(
	IN `p_attempt_id` INT,
	IN `p_teacher_id` INT,
	IN `p_manual_score` INT
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
	OUT `p_auto_score` INT,
	OUT `p_pass_status` VARCHAR(10)
)
BEGIN
DECLARE v_model_answer TEXT;
    DECLARE v_pass_score INT;
    DECLARE v_keyword_score INT DEFAULT 0;
    DECLARE v_video_id INT;
    
    -- 퀴즈 정보 조회
    SELECT q.model_answer, q.pass_score, q.video_id
    INTO v_model_answer, v_pass_score, v_video_id
    FROM quiz q
    WHERE q.quiz_id = p_quiz_id;
    
    -- 자동 채점
    SET v_keyword_score = fn_calculate_keyword_score(p_answer_text, v_model_answer);
    
    -- 합격/불합격 판정
    IF v_keyword_score >= v_pass_score THEN
        SET p_pass_status = 'pass';
    ELSE
        SET p_pass_status = 'fail';
    END IF;
    
    -- 답변 저장
    INSERT INTO quiz_attempt (
        user_id,
        quiz_id,
        answer_text,
        earned_score,
        attempted_at
    ) VALUES (
        p_user_id,
        p_quiz_id,
        p_answer_text,
        v_keyword_score,
        NOW()
    );
    
    SET p_attempt_id = LAST_INSERT_ID();
    SET p_auto_score = v_keyword_score;
    
    -- 비디오별 전체 퀴즈 점수 업데이트
    CALL sp_update_quiz_score(p_user_id, v_video_id);
END//
DELIMITER ;

-- 프로시저 farm.sp_unsubscribe_lecture 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_unsubscribe_lecture`(
	IN `p_user_id` INT,
	IN `p_lecture_id` INT
)
BEGIN
    DECLARE v_progress_id INT;
    
    -- progress_id 조회
    SELECT progress_id INTO v_progress_id
    FROM lecture_progress
    WHERE user_id = p_user_id AND lecture_id = p_lecture_id;
    
    -- lecture_progress 삭제 (트리거가 자동으로 subs_count 감소)
    DELETE FROM lecture_progress
    WHERE progress_id = v_progress_id;
    
    -- lecture_sub 삭제
    DELETE FROM lecture_sub
    WHERE user_id = p_user_id AND lecture_id = p_lecture_id;
    
    SELECT '구독이 취소되었습니다.' AS message;
END//
DELIMITER ;

-- 프로시저 farm.sp_update_quiz_score 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_update_quiz_score`(
    IN `p_user_id` INT,
    IN `p_video_id` INT
)
BEGIN
    DECLARE v_total_quizzes INT;
    DECLARE v_total_score INT;
    DECLARE v_avg_score DECIMAL(5,2);
    DECLARE v_all_passed INT;
    DECLARE v_pass_status ENUM('pass', 'fail');
    
    -- 해당 비디오의 전체 퀴즈 수
    SELECT COUNT(*) INTO v_total_quizzes
    FROM quiz
    WHERE video_id = p_video_id;
    
    -- 사용자가 응시한 퀴즈들의 평균 점수 계산
    SELECT 
        COALESCE(AVG(qa.earned_score), 0),
        SUM(qa.earned_score)
    INTO v_avg_score, v_total_score
    FROM quiz_attempt qa
    INNER JOIN quiz q ON qa.quiz_id = q.quiz_id
    WHERE qa.user_id = p_user_id 
    AND q.video_id = p_video_id
    AND qa.attempt_id IN (
        -- 각 퀴즈의 최신 시도만 선택
        SELECT MAX(qa2.attempt_id)
        FROM quiz_attempt qa2
        INNER JOIN quiz q2 ON qa2.quiz_id = q2.quiz_id
        WHERE qa2.user_id = p_user_id
        AND q2.video_id = p_video_id
        GROUP BY qa2.quiz_id
    );
    
    -- 모든 퀴즈를 통과했는지 확인
    SELECT COUNT(*) INTO v_all_passed
    FROM (
        SELECT qa.quiz_id
        FROM quiz_attempt qa
        INNER JOIN quiz q ON qa.quiz_id = q.quiz_id
        WHERE qa.user_id = p_user_id 
        AND q.video_id = p_video_id
        AND qa.earned_score >= q.pass_score
        AND qa.attempt_id IN (
            SELECT MAX(qa2.attempt_id)
            FROM quiz_attempt qa2
            WHERE qa2.user_id = p_user_id
            AND qa2.quiz_id = qa.quiz_id
        )
    ) passed_quizzes;
    
    -- 모든 퀴즈를 통과하고, 평균 60점 이상이면 pass
    IF v_all_passed = v_total_quizzes AND v_avg_score >= 60 THEN
        SET v_pass_status = 'pass';
    ELSE
        SET v_pass_status = 'fail';
    END IF;
    
    -- quiz_score 테이블 업데이트 (INSERT ... ON DUPLICATE KEY UPDATE)
    INSERT INTO quiz_score (
        video_id,
        user_id,
        total_score,
        pass
    ) VALUES (
        p_video_id,
        p_user_id,
        ROUND(v_avg_score),
        v_pass_status
    )
    ON DUPLICATE KEY UPDATE
        total_score = ROUND(v_avg_score),
        pass = v_pass_status;
END//
DELIMITER ;

-- 프로시저 farm.sp_update_video_progress 구조 내보내기
DELIMITER //
CREATE PROCEDURE `sp_update_video_progress`(
	IN `p_progress_id` INT,
	IN `p_video_id` INT,
	IN `p_user_id` INT,
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
        user_id,
        watched_time, 
        last_position,
        watched_at
    )
    VALUES (
        p_progress_id, 
        p_video_id, 
        p_user_id,
        LEAST(p_current_position, v_video_time),
        p_current_position,
        NOW()
    )
    ON DUPLICATE KEY UPDATE
        watched_time = GREATEST(watched_time, LEAST(p_current_position, v_video_time)),
        last_position = p_current_position,
		  watched_at = NOW();
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
CREATE TABLE `v_lecture_subscribers` (
	`lecture_id` INT NOT NULL COMMENT '강의 ID',
	`title` VARCHAR(1) NOT NULL COMMENT '제목' COLLATE 'utf8mb4_0900_ai_ci',
	`created_at` TIMESTAMP NULL COMMENT '최초 작성시간',
	`subs_count` INT NOT NULL COMMENT '구독자 수(직접기입X)'
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
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_lecture_subscribers` AS select `l`.`lecture_id` AS `lecture_id`,`l`.`title` AS `title`,`l`.`created_at` AS `created_at`,`l`.`subs_count` AS `subs_count` from `lecture` `l`
;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
