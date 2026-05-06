/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80011 (8.0.11)
 Source Host           : localhost:3306
 Source Schema         : petpaw_admin

 Target Server Type    : MySQL
 Target Server Version : 80011 (8.0.11)
 File Encoding         : 65001

 Date: 07/02/2026 20:22:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointment_order
-- ----------------------------
DROP TABLE IF EXISTS `appointment_order`;
CREATE TABLE `appointment_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `customer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户姓名',
  `pet_id` bigint(20) NOT NULL COMMENT '宠物ID',
  `pet_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宠物姓名',
  `service_item_id` bigint(20) NOT NULL COMMENT '服务项目ID',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务项目名称',
  `service_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务类型',
  `staff_id` bigint(20) NOT NULL COMMENT '服务人员ID',
  `staff_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务人员姓名',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `appointment_time` time NOT NULL COMMENT '预约时间',
  `duration` int(11) NOT NULL COMMENT '预约时长(分钟)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待确认' COMMENT '状态：待确认、已预约、已签到、服务中、待结算、已完成、已取消',
  `actual_start_time` datetime NULL DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime NULL DEFAULT NULL COMMENT '实际结束时间',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '未支付' COMMENT '支付状态：未支付、部分支付、已支付',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_number`(`order_number` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_pet_id`(`pet_id` ASC) USING BTREE,
  INDEX `idx_staff_id`(`staff_id` ASC) USING BTREE,
  INDEX `idx_appointment_date`(`appointment_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_payment_status`(`payment_status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `service_item_id`(`service_item_id` ASC) USING BTREE,
  CONSTRAINT `appointment_order_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `crm_customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `appointment_order_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `crm_pet` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `appointment_order_ibfk_3` FOREIGN KEY (`service_item_id`) REFERENCES `appointment_service_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of appointment_order
-- ----------------------------
INSERT INTO `appointment_order` VALUES (1, 'APT001', 1, NULL, 1, NULL, 1, NULL, NULL, 1, NULL, '2025-12-26', '10:00:00', 60, '已取消', NULL, NULL, 80.00, 0.00, 80.00, '未支付', NULL, '2025-12-26 15:42:59', '2025-12-27 23:11:08', NULL, 'system', 0);
INSERT INTO `appointment_order` VALUES (2, 'APT002', 2, NULL, 2, NULL, 2, NULL, NULL, 1, NULL, '2025-12-26', '14:00:00', 90, '已取消', NULL, NULL, 150.00, 0.00, 150.00, '未支付', NULL, '2025-12-26 15:42:59', '2025-12-27 23:50:14', NULL, 'system', 0);
INSERT INTO `appointment_order` VALUES (3, 'AP202512272310174972', 1, '王二', 1, 'kkk', 1, '宠物洗澡', '基础服务', 3, 'admin', '2025-12-28', '23:15:00', 60, '已取消', NULL, NULL, 80.00, 0.00, 80.00, '未支付', NULL, '2025-12-27 23:10:18', '2025-12-27 23:50:04', 'system', 'system', 0);
INSERT INTO `appointment_order` VALUES (4, 'APT1766849871817', 1, '王二', 1, 'kkk', 2, '宠物美容', '美容服务', 1, '张三丰', '2025-12-27', '12:00:00', 90, '已取消', NULL, NULL, 150.00, 0.00, 150.00, '未支付', '', '2025-12-27 23:37:52', '2025-12-28 17:26:42', 'system', 'system', 0);
INSERT INTO `appointment_order` VALUES (5, 'APT1766906420883', 1, '王二', 1, 'kkk', 2, '宠物美容', '美容服务', 1, '张三丰', '2025-12-29', '09:00:00', 90, '已完成', NULL, '2025-12-28 18:31:52', 150.00, 0.00, 150.00, '未支付', '', '2025-12-28 15:20:21', '2025-12-28 18:31:52', 'system', 'system', 0);
INSERT INTO `appointment_order` VALUES (6, 'AP202512281733479138', 2, 'tiki', 2, '大大', 2, '宠物美容', '美容服务', 6, '黄三', '2025-12-28', '18:33:00', 90, '已完成', NULL, '2025-12-28 18:30:08', 150.00, 0.00, 150.00, '未支付', NULL, '2025-12-28 17:33:48', '2025-12-28 18:30:08', 'system', 'system', 0);

-- ----------------------------
-- Table structure for appointment_service_item
-- ----------------------------
DROP TABLE IF EXISTS `appointment_service_item`;
CREATE TABLE `appointment_service_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务名称',
  `service_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务编码',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务分类',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务描述',
  `standard_duration` int(11) NULL DEFAULT 60 COMMENT '标准时长(分钟)',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `skill_level_required` int(11) NULL DEFAULT 1 COMMENT '所需技能等级：1-初级，2-中级，3-高级',
  `required_skills` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所需技能',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_service_code`(`service_code` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约服务项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of appointment_service_item
-- ----------------------------
INSERT INTO `appointment_service_item` VALUES (1, '宠物洗澡', 'WASH', '基础服务', '宠物全身清洁加护理。', 60, 80.00, 1, '洗澡', 1, NULL, '2025-12-25 22:38:06', '2026-02-07 18:29:12', NULL, 'system', 0);
INSERT INTO `appointment_service_item` VALUES (2, '宠物美容', 'GROOMING', '美容服务', '宠物毛发修剪造型。。。。。。。', 90, 150.00, 1, '美容', 1, NULL, '2025-12-25 22:38:06', '2026-02-07 18:29:18', NULL, 'system', 0);
INSERT INTO `appointment_service_item` VALUES (3, '宠物驱虫', 'DEWORMING', '医疗服务', '宠物体内外驱虫治疗。。。。', 30, 120.00, 1, '诊断', 1, NULL, '2025-12-25 22:38:06', '2026-02-07 18:29:28', NULL, 'system', 0);
INSERT INTO `appointment_service_item` VALUES (4, '宠物疫苗', 'VACCINE', '医疗服务', '宠物疫苗注射。。。。。。。', 15, 100.00, 1, '疫苗', 1, NULL, '2025-12-25 22:38:06', '2026-02-07 18:29:31', NULL, 'system', 0);
INSERT INTO `appointment_service_item` VALUES (5, '阿斯顿撒打算', NULL, 'grooming', '达瓦达瓦达瓦达瓦达瓦大屋顶', 60, 8.00, 1, NULL, 1, NULL, '2025-12-27 23:12:24', '2026-02-05 15:53:07', 'system', 'system', 1);
INSERT INTO `appointment_service_item` VALUES (6, 'sadass', NULL, 'grooming', 'dwaddawdawdadaw', 60, 3.00, 1, NULL, 1, NULL, '2025-12-28 20:41:08', '2026-02-05 15:40:40', 'system', 'system', 1);
INSERT INTO `appointment_service_item` VALUES (7, '啊大大', '2121', 'grooming', 'dadsadsadasda', 60, 1.00, 1, NULL, 1, NULL, '2026-02-05 15:52:42', '2026-02-05 15:53:03', 'system', 'system', 1);
INSERT INTO `appointment_service_item` VALUES (8, '古典风格', '3233414', 'grooming', 'dadsdsadsadasdas', 60, 1.00, 1, NULL, 1, NULL, '2026-02-07 16:37:32', '2026-02-07 18:06:51', 'system', 'system', 1);

-- ----------------------------
-- Table structure for appointment_staff_schedule
-- ----------------------------
DROP TABLE IF EXISTS `appointment_staff_schedule`;
CREATE TABLE `appointment_staff_schedule`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `staff_id` bigint(20) NOT NULL COMMENT '员工ID',
  `work_date` date NOT NULL COMMENT '工作日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '正常' COMMENT '状态：正常、请假、调休',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_staff_date`(`staff_id` ASC, `work_date` ASC) USING BTREE,
  INDEX `idx_staff_id`(`staff_id` ASC) USING BTREE,
  INDEX `idx_work_date`(`work_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约员工排班表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of appointment_staff_schedule
-- ----------------------------

-- ----------------------------
-- Table structure for crm_customer
-- ----------------------------
DROP TABLE IF EXISTS `crm_customer`;
CREATE TABLE `crm_customer`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
  `customer_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户类型：普通、高消费、VIP等',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户来源',
  `total_consumption` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '累计消费金额',
  `last_visit_time` date NULL DEFAULT NULL COMMENT '最后到店时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_customer_type`(`customer_type` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'CRM客户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_customer
-- ----------------------------
INSERT INTO `crm_customer` VALUES (1, '王二', '17776767623', NULL, '香港北角渣华道 123 号北角社区会堂', '2025-12-09', 2, 'diamond', '线上预约', 0.00, NULL, '', '2025-12-26 10:38:58', '2025-12-30 13:53:47', 'system', 'system', 0, '1');
INSERT INTO `crm_customer` VALUES (2, 'tiki', '17776767623', NULL, '中国', '2025-12-14', 2, 'vip', '门店介绍', 0.00, NULL, '', '2025-12-26 13:45:37', '2025-12-30 13:53:55', 'system', 'system', 0, '1');
INSERT INTO `crm_customer` VALUES (3, '张三', '13424324434', NULL, '北京', '2025-12-26', 2, 'vip', NULL, 0.00, NULL, '', '2025-12-26 15:44:11', '2025-12-26 15:52:50', NULL, 'system', 0, '1');

-- ----------------------------
-- Table structure for crm_pet
-- ----------------------------
DROP TABLE IF EXISTS `crm_pet`;
CREATE TABLE `crm_pet`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint(20) NOT NULL COMMENT '所属客户ID',
  `pet_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '宠物姓名',
  `pet_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宠物类型：狗、猫等',
  `breed` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品种',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别：0-未知，1-雄，2-雌',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '毛色',
  `sterilization` tinyint(4) NULL DEFAULT NULL COMMENT '绝育：0-未绝育，1-已绝育',
  `blood_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '血型',
  `allergy_source` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '过敏源',
  `chip_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '芯片号',
  `health_status` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '健康状况',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_pet_name`(`pet_name` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  CONSTRAINT `crm_pet_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `crm_customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'CRM宠物档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_pet
-- ----------------------------
INSERT INTO `crm_pet` VALUES (1, 1, 'kkk', '金毛犬', '金毛犬', 1, '2025-12-01', 4.00, NULL, 0, NULL, NULL, NULL, '无', 'active', '', '2025-12-26 13:20:48', '2025-12-26 13:50:03', 'system', 'system', 0);
INSERT INTO `crm_pet` VALUES (2, 2, '大大', '泰迪犬', '泰迪犬', 1, '2025-12-01', 3.00, NULL, 0, NULL, NULL, NULL, '', 'active', '', '2025-12-26 13:50:17', '2025-12-26 13:50:17', 'system', 'system', 0);

-- ----------------------------
-- Table structure for crm_pet_health_record
-- ----------------------------
DROP TABLE IF EXISTS `crm_pet_health_record`;
CREATE TABLE `crm_pet_health_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_id` bigint(20) NOT NULL COMMENT '宠物ID',
  `record_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录类型：疫苗、驱虫、手术、洗护等',
  `record_date` date NOT NULL COMMENT '记录日期',
  `record_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录内容',
  `next_reminder_date` date NULL DEFAULT NULL COMMENT '下次提醒日期',
  `doctor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '医生姓名',
  `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '费用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pet_id`(`pet_id` ASC) USING BTREE,
  INDEX `idx_record_type`(`record_type` ASC) USING BTREE,
  INDEX `idx_record_date`(`record_date` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  CONSTRAINT `crm_pet_health_record_ibfk_1` FOREIGN KEY (`pet_id`) REFERENCES `crm_pet` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'CRM宠物健康记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of crm_pet_health_record
-- ----------------------------

-- ----------------------------
-- Table structure for finance_member_card
-- ----------------------------
DROP TABLE IF EXISTS `finance_member_card`;
CREATE TABLE `finance_member_card`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `card_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '卡号',
  `card_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '储值卡' COMMENT '卡类型：储值卡、折扣卡、积分卡',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '余额',
  `total_recharge` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '累计充值',
  `total_consumption` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '累计消费',
  `points` int(11) NULL DEFAULT 0 COMMENT '积分',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
  `expire_time` date NULL DEFAULT NULL COMMENT '到期时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_card_number`(`card_number` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_card_type`(`card_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  CONSTRAINT `finance_member_card_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `crm_customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务会员卡表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of finance_member_card
-- ----------------------------
INSERT INTO `finance_member_card` VALUES (1, 3, '21321321312', 'diamond', 10.00, 0.00, 0.00, 0, 1, NULL, NULL, '2025-12-26 18:37:48', '2025-12-26 19:07:48', 'system', 'system', 1);
INSERT INTO `finance_member_card` VALUES (2, 1, '2313', 'diamond', 0.00, 0.00, 0.00, 0, 1, NULL, NULL, '2025-12-26 18:39:47', '2025-12-26 19:07:45', 'system', 'system', 1);
INSERT INTO `finance_member_card` VALUES (3, 1, '', 'diamond', 500.00, 0.00, 0.00, 1000, 1, NULL, NULL, '2025-12-26 19:07:58', '2025-12-26 19:22:03', 'system', 'system', 0);

-- ----------------------------
-- Table structure for finance_order
-- ----------------------------
DROP TABLE IF EXISTS `finance_order`;
CREATE TABLE `finance_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单编号',
  `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型：商品、服务、混合',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `payment_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '未支付' COMMENT '支付状态：未支付、部分支付、已支付',
  `order_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待处理' COMMENT '订单状态：待处理、处理中、已完成、已取消',
  `cashier_id` bigint(20) NULL DEFAULT NULL COMMENT '收银员ID',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_number`(`order_number` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_order_type`(`order_type` ASC) USING BTREE,
  INDEX `idx_payment_status`(`payment_status` ASC) USING BTREE,
  INDEX `idx_order_status`(`order_status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `cashier_id`(`cashier_id` ASC) USING BTREE,
  CONSTRAINT `finance_order_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `crm_customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `finance_order_ibfk_2` FOREIGN KEY (`cashier_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of finance_order
-- ----------------------------
INSERT INTO `finance_order` VALUES (10, 'TEST004', '服务', 1, 100.00, 0.00, 100.00, '现金', '已支付', '已完成', NULL, '2025-12-25 15:42:58', NULL, '2025-12-25 15:42:58', '2025-12-25 15:42:58', NULL, NULL, 0);
INSERT INTO `finance_order` VALUES (11, 'TEST001', '服务', 1, 150.00, 0.00, 150.00, '微信', '已支付', '已完成', NULL, '2025-12-26 15:44:50', NULL, '2025-12-26 15:44:50', '2025-12-26 15:44:50', NULL, NULL, 0);
INSERT INTO `finance_order` VALUES (12, 'TEST002', '商品', 2, 380.00, 0.00, 380.00, '支付宝', '已支付', '已完成', NULL, '2025-12-26 15:44:50', NULL, '2025-12-26 15:44:50', '2025-12-26 15:44:50', NULL, NULL, 0);
INSERT INTO `finance_order` VALUES (13, 'TEST003', '混合', 3, 520.00, 0.00, 500.00, '微信', '已支付', '已完成', NULL, '2025-12-26 15:44:50', NULL, '2025-12-26 15:44:50', '2025-12-26 15:44:50', NULL, NULL, 0);
INSERT INTO `finance_order` VALUES (15, 'ORD1766911072630', '商品', 1, 605.00, 0.00, 605.00, 'cash', '未支付', '待处理', NULL, NULL, NULL, '2025-12-28 16:37:53', '2025-12-28 16:37:53', 'system', 'system', 0);
INSERT INTO `finance_order` VALUES (16, 'ORD1766913482078', '商品', 1, 560.00, 0.00, 560.00, 'alipay', '已支付', '已完成', NULL, NULL, NULL, '2025-12-28 17:18:02', '2026-02-07 17:50:55', 'system', 'system', 0);
INSERT INTO `finance_order` VALUES (17, 'ORD1770454651916', '商品', 1, 80.00, 0.00, 80.00, 'alipay', '已支付', '已完成', 3, NULL, NULL, '2026-02-07 16:57:32', '2026-02-07 17:50:57', 'system', 'system', 0);
INSERT INTO `finance_order` VALUES (18, 'ORD1770457031296', '商品', 1, 540.00, 0.00, 540.00, 'cash', '已支付', '已完成', NULL, NULL, NULL, '2026-02-07 17:37:12', '2026-02-07 17:51:00', 'system', 'system', 0);
INSERT INTO `finance_order` VALUES (19, 'ORD1770458140314', '商品', 1, 45.00, 0.00, 45.00, 'cash', '已支付', '已完成', NULL, '2026-02-07 17:55:41', '收银台结算', '2026-02-07 17:55:41', '2026-02-07 17:55:41', 'system', 'system', 0);

-- ----------------------------
-- Table structure for finance_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `finance_order_detail`;
CREATE TABLE `finance_order_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '明细类型：商品、服务',
  `item_id` bigint(20) NOT NULL COMMENT '商品/服务ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品/服务名称',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_item_type`(`item_type` ASC) USING BTREE,
  INDEX `idx_item_id`(`item_id` ASC) USING BTREE,
  CONSTRAINT `finance_order_detail_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `finance_order` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务订单明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of finance_order_detail
-- ----------------------------
INSERT INTO `finance_order_detail` VALUES (1, 19, '商品', 3, '乐享泡泡浴', 1, 45.00, 45.00, 0.00, NULL, '2026-02-07 17:55:41');

-- ----------------------------
-- Table structure for finance_recharge_record
-- ----------------------------
DROP TABLE IF EXISTS `finance_recharge_record`;
CREATE TABLE `finance_recharge_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_card_id` bigint(20) NOT NULL COMMENT '会员卡ID',
  `recharge_amount` decimal(10, 2) NOT NULL COMMENT '充值金额',
  `gift_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '赠送金额',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式',
  `operator_id` bigint(20) NULL DEFAULT NULL COMMENT '操作员ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_card_id`(`member_card_id` ASC) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `finance_recharge_record_ibfk_1` FOREIGN KEY (`member_card_id`) REFERENCES `finance_member_card` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `finance_recharge_record_ibfk_2` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务充值记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of finance_recharge_record
-- ----------------------------

-- ----------------------------
-- Table structure for hr_employee
-- ----------------------------
DROP TABLE IF EXISTS `hr_employee`;
CREATE TABLE `hr_employee`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` tinyint(4) NULL DEFAULT 1 COMMENT '性别 1:男 2:女 0:未知',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '员工头像URL',
  `position_id` bigint(20) NULL DEFAULT NULL COMMENT '职位ID',
  `entry_date` date NULL DEFAULT NULL COMMENT '入职日期',
  `sys_user_id` bigint(20) NULL DEFAULT NULL COMMENT '关联系统用户ID',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1:在职 2:离职 3:休假',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_employee_no`(`employee_no` ASC) USING BTREE,
  INDEX `idx_position_id`(`position_id` ASC) USING BTREE,
  INDEX `idx_sys_user_id`(`sys_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '员工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of hr_employee
-- ----------------------------
INSERT INTO `hr_employee` VALUES (1, 'EMP001', '张三丰', 1, '13800138001', NULL, NULL, 3, NULL, NULL, 1, '2025-12-26 17:52:44', '2025-12-26 17:52:44', NULL, NULL, 0);
INSERT INTO `hr_employee` VALUES (2, 'EMP002', '李小龙', 1, '13800138002', NULL, NULL, 3, NULL, NULL, 1, '2025-12-26 17:52:44', '2025-12-26 17:52:44', NULL, NULL, 0);
INSERT INTO `hr_employee` VALUES (3, 'EMP003', '赵敏', 2, '13800138003', NULL, NULL, 2, NULL, NULL, 1, '2025-12-26 17:52:44', '2025-12-26 17:52:44', NULL, NULL, 0);
INSERT INTO `hr_employee` VALUES (6, 'EMP004', '黄三', 1, '17778992343', '', NULL, 5, '2025-12-24', NULL, 1, '2025-12-26 19:30:58', '2025-12-26 19:30:58', 'system', 'system', 0);

-- ----------------------------
-- Table structure for hr_position
-- ----------------------------
DROP TABLE IF EXISTS `hr_position`;
CREATE TABLE `hr_position`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `position_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '职位名称',
  `position_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '职位编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_position_code`(`position_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '职位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of hr_position
-- ----------------------------
INSERT INTO `hr_position` VALUES (1, '店长', 'STORE_MANAGER', '负责门店整体运营', 1, '2025-12-26 17:52:44', '2025-12-26 17:52:44', NULL, NULL, 0);
INSERT INTO `hr_position` VALUES (2, '前台', 'RECEPTIONIST', '负责接待和收银', 1, '2025-12-26 17:52:44', '2025-12-26 17:52:44', NULL, NULL, 0);
INSERT INTO `hr_position` VALUES (3, '美容师', 'GROOMER', '负责宠物美容服务', 1, '2025-12-26 17:52:44', '2025-12-26 17:52:44', NULL, NULL, 0);
INSERT INTO `hr_position` VALUES (4, '助理美容师', 'ASSISTANT_GROOMER', '协助美容师', 1, '2025-12-26 17:52:44', '2025-12-26 17:52:44', NULL, NULL, 0);
INSERT INTO `hr_position` VALUES (5, '兽医', 'VET', '负责宠物医疗', 1, '2025-12-26 17:52:44', '2025-12-26 17:52:44', NULL, NULL, 0);
INSERT INTO `hr_position` VALUES (6, '清洁工', 'CLEANER', '负责卫生清洁', 1, '2025-12-26 17:52:44', '2025-12-26 17:52:44', NULL, NULL, 0);

-- ----------------------------
-- Table structure for scm_inventory_record
-- ----------------------------
DROP TABLE IF EXISTS `scm_inventory_record`;
CREATE TABLE `scm_inventory_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `record_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录类型：入库、出库、盘点、调拨',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `before_quantity` int(11) NOT NULL COMMENT '操作前数量',
  `after_quantity` int(11) NOT NULL COMMENT '操作后数量',
  `supplier_id` bigint(20) NULL DEFAULT NULL COMMENT '供应商ID',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '关联订单ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人',
  `deleted` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_record_type`(`record_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `supplier_id`(`supplier_id` ASC) USING BTREE,
  CONSTRAINT `scm_inventory_record_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `scm_product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `scm_inventory_record_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `scm_supplier` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'SCM库存记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of scm_inventory_record
-- ----------------------------
INSERT INTO `scm_inventory_record` VALUES (1, 1, 'INBOUND', 4, 5, 9, 1, NULL, NULL, '2025-12-27 15:55:17', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (2, 3, 'OUTBOUND', 1, 2, 1, NULL, NULL, NULL, '2025-12-27 22:28:49', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (3, 1, 'ADJUST', 5, 0, 5, NULL, NULL, '五', '2025-12-28 18:37:15', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (4, 1, 'STOCKTAKING', 95, 5, 100, NULL, NULL, '定期盘点', '2025-12-28 18:37:53', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (5, 1, 'STOCKTAKING', 0, 100, 100, NULL, NULL, '定期盘点', '2025-12-28 18:37:54', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (6, 1, 'STOCKTAKING', 0, 100, 100, NULL, NULL, '定期盘点', '2025-12-28 18:37:55', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (7, 1, 'INBOUND', 6, 100, 106, 3, NULL, NULL, '2025-12-28 18:42:00', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (8, 1, 'STOCKTAKING', -6, 106, 100, NULL, NULL, '定期盘点', '2025-12-28 18:42:21', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (9, 1, 'STOCKTAKING', 0, 100, 100, NULL, NULL, '定期盘点', '2025-12-28 18:42:29', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (10, 1, 'OUTBOUND', 3, 100, 97, NULL, NULL, NULL, '2025-12-28 18:46:33', 'admin', '0');
INSERT INTO `scm_inventory_record` VALUES (11, 2, 'OUTBOUND', 1, 30, 29, NULL, NULL, NULL, '2025-12-28 18:46:45', 'admin', '0');

-- ----------------------------
-- Table structure for scm_product
-- ----------------------------
DROP TABLE IF EXISTS `scm_product`;
CREATE TABLE `scm_product`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品编码',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类',
  `brand` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `specification` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `unit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `purchase_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '采购价',
  `sale_price` decimal(10, 2) NOT NULL COMMENT '销售价',
  `stock_quantity` int(11) NULL DEFAULT 0 COMMENT '库存数量',
  `min_stock_quantity` int(11) NULL DEFAULT 0 COMMENT '最低库存预警',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `supplier_id` bigint(20) NULL DEFAULT NULL COMMENT '供应商ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_product_code`(`product_code` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_brand`(`brand` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'SCM商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of scm_product
-- ----------------------------
INSERT INTO `scm_product` VALUES (1, '皇家狗粮成犬', 'ROYAL_ADULT', '宠物食品', '皇家', '15kg', '袋', 0.00, 380.00, 97, 10, 0, 0, NULL, NULL, '2025-12-25 22:38:06', '2026-02-05 15:25:57', NULL, 'system', 0);
INSERT INTO `scm_product` VALUES (2, '渴望猫粮', 'ORI_ADULT', '宠物食品', '渴望', '2kg', '袋', 0.00, 180.00, 29, 5, 1, 0, NULL, NULL, '2025-12-25 22:38:06', '2026-02-05 15:26:49', NULL, 'system', 0);
INSERT INTO `scm_product` VALUES (3, '乐享泡泡浴', 'SHOWER_GEL', '宠物用品', '自有品牌', '500ml', '瓶', 0.00, 45.00, 1, 20, 1, 0, NULL, NULL, '2025-12-25 22:38:06', '2026-02-05 15:28:38', NULL, 'system', 0);
INSERT INTO `scm_product` VALUES (4, '宠物牙刷', 'TOOTHBRUSH', '宠物用品', '自有品牌', '中号', '个', 0.00, 15.00, 200, 50, 1, 0, NULL, NULL, '2025-12-25 22:38:06', '2026-02-05 15:29:45', NULL, 'system', 0);
INSERT INTO `scm_product` VALUES (5, '大三', '323231', 'snack', NULL, NULL, '袋', 0.00, 5.00, 6, 10, 0, 0, NULL, NULL, '2025-12-26 13:57:19', '2026-02-05 15:40:14', 'system', 'system', 1);
INSERT INTO `scm_product` VALUES (6, 'dasda', '21312', 'supplies', NULL, NULL, '盒', 0.00, 0.00, 6, 10, 0, NULL, NULL, NULL, '2025-12-27 22:28:26', '2026-02-05 15:40:18', 'system', 'system', 1);
INSERT INTO `scm_product` VALUES (7, 'wqeqweq', '321231', 'snack', '比瑞吉', '', '袋', 0.00, 5.00, 5, 10, 1, 2, NULL, NULL, '2025-12-28 20:57:20', '2026-02-05 15:40:19', 'system', 'system', 1);
INSERT INTO `scm_product` VALUES (8, '猫粮', '011', 'main_food', '皇家', '', '袋', 0.00, 93.00, 10, 10, 1, 1, NULL, NULL, '2026-02-05 13:28:18', '2026-02-05 15:40:22', 'system', 'system', 1);

-- ----------------------------
-- Table structure for scm_supplier
-- ----------------------------
DROP TABLE IF EXISTS `scm_supplier`;
CREATE TABLE `scm_supplier`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `supplier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '银行账号',
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开户银行',
  `tax_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '税号',
  `level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商等级',
  `cooperation_status` tinyint(4) NULL DEFAULT 1 COMMENT '合作状态：0-终止，1-合作中',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  `coding` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '编码',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_supplier_name`(`supplier_name` ASC) USING BTREE,
  INDEX `idx_cooperation_status`(`cooperation_status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'SCM供应商表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of scm_supplier
-- ----------------------------
INSERT INTO `scm_supplier` VALUES (1, '啊阿达', '咋说的', '13423434211', '', '香港北角渣华道 123 号北角社区会堂', NULL, NULL, NULL, NULL, 1, NULL, '2025-12-26 14:06:32', '2025-12-26 14:06:40', 'system', 'system', 0, NULL);
INSERT INTO `scm_supplier` VALUES (2, '大苏打', '133333', '13423434211', '', '香港北角渣华道 123 号北角社区会堂', NULL, NULL, NULL, NULL, 1, NULL, '2025-12-26 14:12:33', '2025-12-26 14:12:33', 'system', 'system', 0, NULL);
INSERT INTO `scm_supplier` VALUES (3, '阿斯顿撒', '广东省', '15367678193', '', '香港北角渣华道 123 号北角社区会堂', NULL, NULL, NULL, NULL, 1, NULL, '2025-12-26 15:01:24', '2025-12-26 15:01:24', 'system', 'system', 0, 'SUP20251226150124AE49');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `last_login_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (3, 'admin', '7w5u5x7q/jtBwJPNdiO71A==', '系统管理员', NULL, NULL, NULL, NULL, 1, NULL, NULL, '2025-12-25 23:36:40', '2025-12-25 23:36:40', NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (4, 'mmm', 'g40pEweqCGK1ehYJwv+qpw==', '测试员', NULL, NULL, NULL, NULL, 1, NULL, NULL, '2025-12-25 23:36:40', '2025-12-25 23:36:40', NULL, NULL, 0);

-- ----------------------------
-- Table structure for wj_file
-- ----------------------------
DROP TABLE IF EXISTS `wj_file`;
CREATE TABLE `wj_file`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `business_id` bigint(20) NULL DEFAULT NULL COMMENT '业务ID',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件路径',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business`(`business_id` ASC, `business_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wj_file
-- ----------------------------
INSERT INTO `wj_file` VALUES (1, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2025/12/28/e5948f05f65444f89cc80ecabe9efb25.png', '2025-12-28 20:34:31');
INSERT INTO `wj_file` VALUES (2, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2025/12/28/1affd483fe854f1facbe7da83b39f6c4.png', '2025-12-28 20:34:37');
INSERT INTO `wj_file` VALUES (3, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2025/12/28/763b16831a8843b2b403c38e23525bf8.png', '2025-12-28 20:34:43');
INSERT INTO `wj_file` VALUES (4, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2025/12/28/e7ef782cdbb94e798aeb081698144fce.png', '2025-12-28 20:38:03');
INSERT INTO `wj_file` VALUES (5, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2025/12/28/02ded4ff6dc2439798b164feac65a5f6.png', '2025-12-28 20:40:07');
INSERT INTO `wj_file` VALUES (6, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2025/12/28/0a0e9dfe41d242f0ba12cf30ff695dce.png', '2025-12-28 20:40:48');
INSERT INTO `wj_file` VALUES (7, NULL, 'PRODUCT', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2025/12/28/2b0a274be52846a4b7ce42c5ac9e676a.png', '2025-12-28 20:57:18');
INSERT INTO `wj_file` VALUES (8, NULL, 'PRODUCT', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2025/12/28/da4b913e43664bfea03decf4db14ef9b.png', '2025-12-28 21:35:53');
INSERT INTO `wj_file` VALUES (9, 0, 'PRODUCT', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/05/260962fd7fd94ba085d6926d24dcc54d.png', '2026-02-05 13:28:14');
INSERT INTO `wj_file` VALUES (11, 1, 'PRODUCT', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/05/7d3fecbf314c4bbda7d4349bfafff26a.jpg', '2026-02-05 15:25:56');
INSERT INTO `wj_file` VALUES (12, 2, 'PRODUCT', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/05/4155518b468144b6b454d1398c549069.png', '2026-02-05 15:26:48');
INSERT INTO `wj_file` VALUES (13, 3, 'PRODUCT', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/05/e62fab8ae91e4daabc9cbdee21d69b5c.png', '2026-02-05 15:28:36');
INSERT INTO `wj_file` VALUES (14, 4, 'PRODUCT', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/05/dc8ddcfaf83b452a9f96149de80e666c.jpg', '2026-02-05 15:29:44');
INSERT INTO `wj_file` VALUES (15, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/05/cbcb302cd538487c999d20b72dd6e101.png', '2026-02-05 15:43:14');
INSERT INTO `wj_file` VALUES (16, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/05/3b3ef0b5ae11496d9e1458d05ff6c11c.png', '2026-02-05 15:51:34');
INSERT INTO `wj_file` VALUES (17, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/05/e15c2ed6513c41088eaeb59ccf9f7644.png', '2026-02-05 15:52:41');
INSERT INTO `wj_file` VALUES (18, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/05/ad74f4d7ea604d6c89ee6e7d59e5c0f1.png', '2026-02-05 16:35:43');
INSERT INTO `wj_file` VALUES (19, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/07/9eecbde737ae4b898253bf73eeaee2e3.png', '2026-02-07 16:36:41');
INSERT INTO `wj_file` VALUES (20, NULL, NULL, 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/07/db8586368b3f4434abd1c0d1032fae00.png', '2026-02-07 16:37:20');
INSERT INTO `wj_file` VALUES (31, 1, 'SERVICE', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/07/12b7c1ccedea45029cc895550317274f.png', '2026-02-07 18:29:12');
INSERT INTO `wj_file` VALUES (32, 2, 'SERVICE', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/07/9e8ed554684c40db94567e7433f12013.jpg', '2026-02-07 18:29:18');
INSERT INTO `wj_file` VALUES (33, 3, 'SERVICE', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/07/3f3d548ba9ac4241b8cfd0b4eab04ed6.jpg', '2026-02-07 18:29:28');
INSERT INTO `wj_file` VALUES (34, 4, 'SERVICE', 'https://image-tww.oss-cn-hangzhou.aliyuncs.com/2026/02/07/5c72009f20c845188f891c02d154da8d.jpg', '2026-02-07 18:29:31');

SET FOREIGN_KEY_CHECKS = 1;
