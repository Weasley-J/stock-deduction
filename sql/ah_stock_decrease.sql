/*
 Navicat Premium Data Transfer

 Source Server         : vm-132-docker
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : 192.168.40.132:33306
 Source Schema         : ah_stock_decrease

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 19/02/2021 23:25:39
*/
CREATE DATABASE `ah_stock_decrease` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_product_stock
-- ----------------------------
DROP TABLE IF EXISTS `tb_product_stock`;
CREATE TABLE `tb_product_stock`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类id',
  `brand_id` bigint NULL DEFAULT NULL COMMENT '品牌id',
  `purchase_price` decimal(10, 0) NULL DEFAULT NULL COMMENT '进货价格',
  `price_unit` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '分' COMMENT '价格单位 (默认: 分  )',
  `stock_quantity` bigint NULL DEFAULT NULL COMMENT '库存存量',
  `stock_unit` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '个' COMMENT '库存单位 ( 默认: 个 )',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '删除状态（0：未删，1：已删，默认：未删除）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_cid`(`category_id`) USING BTREE COMMENT '分类id索引',
  INDEX `idx_bid`(`brand_id`) USING BTREE COMMENT '品牌id索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_product_stock
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
