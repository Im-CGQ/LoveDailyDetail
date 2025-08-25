-- MySQL dump 10.13  Distrib 8.0.27, for Linux (x86_64)
--
-- Host: localhost    Database: love_diary
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chat_records`
--

DROP TABLE IF EXISTS `chat_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chat_type` varchar(50) NOT NULL COMMENT 'èŠå¤©ç±»åž‹ï¼šå¾®ä¿¡è¯­éŸ³ã€å¾®ä¿¡èŠå¤©ã€å°çº¢ä¹¦èŠå¤©ã€è‡ªå®šä¹‰ç±»åž‹',
  `duration_minutes` int NOT NULL COMMENT 'èŠå¤©æ—¶é•¿ï¼ˆåˆ†é’Ÿï¼‰',
  `date` date NOT NULL COMMENT 'èŠå¤©æ—¥æœŸ',
  `description` text COMMENT 'èŠå¤©æè¿°æˆ–å¤‡æ³¨',
  `custom_type` varchar(100) DEFAULT NULL COMMENT 'è‡ªå®šä¹‰èŠå¤©ç±»åž‹',
  `user_id` bigint NOT NULL,
  `partner_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_chat_records_user_id` (`user_id`),
  KEY `idx_chat_records_partner_id` (`partner_id`),
  KEY `idx_chat_records_user_partner` (`user_id`,`partner_id`),
  KEY `idx_chat_records_date` (`date`),
  KEY `idx_chat_records_chat_type` (`chat_type`),
  CONSTRAINT `chat_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chat_records_ibfk_2` FOREIGN KEY (`partner_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_records`
--

LOCK TABLES `chat_records` WRITE;
/*!40000 ALTER TABLE `chat_records` DISABLE KEYS */;
INSERT INTO `chat_records` VALUES (1,'微信语音',94,'2025-08-24','男的女的',NULL,3,NULL,'2025-08-25 18:50:08','2025-08-25 18:50:08');
/*!40000 ALTER TABLE `chat_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diaries`
--

DROP TABLE IF EXISTS `diaries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diaries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `description` text,
  `background_music_url` varchar(500) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `partner_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_diaries_date` (`date`),
  KEY `idx_diaries_partner_id` (`partner_id`),
  KEY `idx_diaries_user_partner` (`user_id`,`partner_id`),
  CONSTRAINT `diaries_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `diaries_ibfk_2` FOREIGN KEY (`partner_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diaries`
--

LOCK TABLES `diaries` WRITE;
/*!40000 ALTER TABLE `diaries` DISABLE KEYS */;
INSERT INTO `diaries` VALUES (1,'测试','2025-08-25','测试',NULL,3,NULL,'2025-08-25 18:56:04','2025-08-25 18:56:04'),(2,'测试一下','2025-08-25','测试',NULL,3,NULL,'2025-08-25 18:57:45','2025-08-25 18:57:45');
/*!40000 ALTER TABLE `diaries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diary_images`
--

DROP TABLE IF EXISTS `diary_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diary_images` (
  `diary_id` bigint NOT NULL,
  `image_url` varchar(500) NOT NULL,
  KEY `idx_diary_images_diary_id` (`diary_id`),
  CONSTRAINT `diary_images_ibfk_1` FOREIGN KEY (`diary_id`) REFERENCES `diaries` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diary_images`
--

LOCK TABLES `diary_images` WRITE;
/*!40000 ALTER TABLE `diary_images` DISABLE KEYS */;
INSERT INTO `diary_images` VALUES (1,'https://love-diary-detail.oss-cn-hangzhou.aliyuncs.com//image/1756119355074_d918bd4f6bcd468fb67e615f8ba098d0.jpg'),(2,'https://love-diary-detail.oss-cn-hangzhou.aliyuncs.com//image/1756119379050_3c3f5ebc8a7641f5b3a252e6f0d02c73.jpg'),(2,'https://love-diary-detail.oss-cn-hangzhou.aliyuncs.com//image/1756119384488_5aa12f1bebb1481aa4df9054f2766103.jpg');
/*!40000 ALTER TABLE `diary_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diary_videos`
--

DROP TABLE IF EXISTS `diary_videos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diary_videos` (
  `diary_id` bigint NOT NULL,
  `video_url` varchar(500) NOT NULL,
  KEY `idx_diary_videos_diary_id` (`diary_id`),
  CONSTRAINT `diary_videos_ibfk_1` FOREIGN KEY (`diary_id`) REFERENCES `diaries` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diary_videos`
--

LOCK TABLES `diary_videos` WRITE;
/*!40000 ALTER TABLE `diary_videos` DISABLE KEYS */;
INSERT INTO `diary_videos` VALUES (2,'https://love-diary-detail.oss-cn-hangzhou.aliyuncs.com//video/1756119411367_fe607b8ff98a4547a10b353c8f6f957c.mp4'),(2,'https://love-diary-detail.oss-cn-hangzhou.aliyuncs.com//video/1756119420808_ec066b0195544b8487250f05199ff06a.mp4');
/*!40000 ALTER TABLE `diary_videos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `letters`
--

DROP TABLE IF EXISTS `letters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `letters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `unlock_time` timestamp NOT NULL,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_letters_sender_id` (`sender_id`),
  KEY `idx_letters_receiver_id` (`receiver_id`),
  KEY `idx_letters_unlock_time` (`unlock_time`),
  KEY `idx_letters_is_read` (`is_read`),
  CONSTRAINT `letters_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `letters_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `letters`
--

LOCK TABLES `letters` WRITE;
/*!40000 ALTER TABLE `letters` DISABLE KEYS */;
INSERT INTO `letters` VALUES (1,'写信给自己','和喜欢的人睡觉的好处\n1.免疫力增加\n和自己真心喜欢的人一同安卧，安全感会如被子般将你环绕。\n这种安稳的感觉能让身心全然放松，睡眠质量大幅提升，\n免疫力也会悄然增强，仿佛为身体增添了一层防护屏障\n2.焦虑缓解\n安全感会把皮质醇（压力激素）压制到最低水平，那些繁杂的\n负面情绪，似乎会随着呼吸一点点被排出体外。清晨醒来,\n头脑格外清醒，就像被重新启动了一样\n3.代谢加速\n当两人的呼吸和心跳逐渐同步时，身体的代谢似乎也会被带动起来，水肿和体内的代谢废物会在夜间被快速清除，晨起时脸部会显得格外轻快\n和喜欢的人睡觉的好处\n4.催产素释放\n共同入眠时，身体会悄悄分泌催产素--也就是那种能让\n人内心感到甜蜜的“拥抱激素”。压力会悄然下降，整个人就像\n浸泡在温水中一样放松，睡眠深沉而香甜\n5.睡眠质量提升\n一旦安全感充足，松果体就会高效工作，褪黑素大量分泌。据说深度睡眠时间会比平时增加47分钟，这相当于为身体补充了大量的能量\n6.皮肤改善\n在冬季与心爱之人相拥而眠时，皮肤的微循环会加快，\n就如同用37°C的恒温在慢慢抚平细纹，法令纹也似乎有所\n淡化，比使用美容仪还要方便有效\n\n和喜欢的人睡觉的好处\n7.压力释放\n许多身体的不适，实际上是由负面情绪积压所致。与喜欢\n的人同睡，即便不言语交流仅仅是彼此的陪伴，烦躁和压力也\n会逐渐消散，身心会像被温柔梳理过一样舒适\n8.体温调节\n手脚容易冰凉的人一定深有体会！投入喜欢的人的怀抱，\n两人的体温相互交融，仿佛能温暖肝经胆经，整夜都不会\n感到寒冷，清晨醒来时浑身都暖洋洋的\n9.美梦相伴\n快速眼动期会延长36%，做噩梦的概率大大降低,\n甚至醒来时嘴角都可能带着笑意。原来，和喜欢的人\n一起入睡，连梦境都会变得甜蜜起来\n\n','2025-08-26 12:00:00',3,3,0,'2025-08-25 18:38:12','2025-08-25 18:38:12'),(2,'换行测试','哈哈哈\n换行','2025-08-26 12:00:00',3,3,0,'2025-08-25 18:46:11','2025-08-25 18:46:11');
/*!40000 ALTER TABLE `letters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partner_invitations`
--

DROP TABLE IF EXISTS `partner_invitations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `partner_invitations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_user_id` bigint NOT NULL,
  `to_user_id` bigint NOT NULL,
  `status` varchar(20) DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_invitation` (`from_user_id`,`to_user_id`),
  KEY `idx_partner_invitations_from_user` (`from_user_id`),
  KEY `idx_partner_invitations_to_user` (`to_user_id`),
  KEY `idx_partner_invitations_status` (`status`),
  CONSTRAINT `partner_invitations_ibfk_1` FOREIGN KEY (`from_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `partner_invitations_ibfk_2` FOREIGN KEY (`to_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partner_invitations`
--

LOCK TABLES `partner_invitations` WRITE;
/*!40000 ALTER TABLE `partner_invitations` DISABLE KEYS */;
/*!40000 ALTER TABLE `partner_invitations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_configs`
--

DROP TABLE IF EXISTS `system_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) NOT NULL COMMENT 'é…ç½®é”®',
  `config_value` text COMMENT 'é…ç½®å€¼',
  `config_type` varchar(20) DEFAULT 'STRING' COMMENT 'é…ç½®ç±»åž‹ï¼šSTRING, NUMBER, BOOLEAN, JSON',
  `description` varchar(255) DEFAULT NULL COMMENT 'é…ç½®æè¿°',
  `user_id` bigint DEFAULT NULL COMMENT 'ç”¨æˆ·IDï¼ŒNULLè¡¨ç¤ºå…¨å±€é…ç½®',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_key` (`config_key`),
  KEY `idx_system_configs_key` (`config_key`),
  KEY `idx_system_configs_user_id` (`user_id`),
  CONSTRAINT `system_configs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_configs`
--

LOCK TABLES `system_configs` WRITE;
/*!40000 ALTER TABLE `system_configs` DISABLE KEYS */;
INSERT INTO `system_configs` VALUES (1,'together_date','2025-05-30','STRING','åœ¨ä¸€èµ·çš„æ—¶é—´',NULL,'2025-08-25 07:03:19','2025-08-25 07:03:19'),(2,'background_music_autoplay','true','BOOLEAN','èƒŒæ™¯éŸ³ä¹æ˜¯å¦è‡ªåŠ¨æ’­æ”¾',NULL,'2025-08-25 07:03:19','2025-08-25 07:03:19');
/*!40000 ALTER TABLE `system_configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_table`
--

DROP TABLE IF EXISTS `test_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_table` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_table`
--

LOCK TABLES `test_table` WRITE;
/*!40000 ALTER TABLE `test_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `test_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `display_name` varchar(100) DEFAULT NULL,
  `role` varchar(10) DEFAULT 'ADMIN',
  `partner_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `partner_id` (`partner_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`partner_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$nMge6m9p.BkgV/hJdJV/WOZuhqWqcvzbETRztW0f.PiOz1md3r6Py','管理员','ADMIN',NULL,'2025-08-25 07:03:19'),(2,'zhen','$2a$10$5W5xw5O0edaeFt1XT..sf.uxOjP4NOwGIBqkM/RyfplS.WCAYTRlm','zhen','USER',NULL,'2025-08-25 15:08:06'),(3,'chen','$2a$10$BTBYpzdQDYI2K01zAcBEYOPSi4rnwBXTrLF4a0gk4PdjsR7Dwdtnm','chen','USER',NULL,'2025-08-25 15:08:26');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-25 17:42:16
