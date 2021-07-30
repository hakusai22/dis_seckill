# ------------------------------------------------------------------------
#
#   创建秒杀场景下的表, 包含以下几个表：
#
#   1. 秒杀用户表        seckill_user
#   2. 商品表            goods
#   3. 参与秒杀的商品表   seckill_goods
#   4. 秒杀订单表        seckill_order
#   5. 订单表            order_info
#
# ------------------------------------------------------------------------

CREATE DATABASE  IF NOT EXISTS seckill;

USE seckill;

# ************************************************************************
# 秒杀用户表
DROP TABLE IF EXISTS `seckill_user`;
CREATE TABLE `seckill_user` (
  UUID            INT          AUTO_INCREMENT   COMMENT '主键编号,用户id',
  phone           BIGINT(20)   NOT NULL         COMMENT '手机号码',
  nickname        VARCHAR(255) NOT NULL         COMMENT '昵称',
  password        VARCHAR(32)  DEFAULT NULL     COMMENT 'MD5(MD5(password明文+固定salt) + salt)',
  salt            VARCHAR(10)  DEFAULT NULL     COMMENT '盐值',
  head            VARCHAR(128) DEFAULT NULL     COMMENT '头像，云存储的id',
  register_date   DATETIME     DEFAULT NULL     COMMENT '注册时间',
  last_login_date DATETIME     DEFAULT NULL     COMMENT '上次登录时间',
  login_count     INT(11)      DEFAULT 0        COMMENT '登录次数',
  PRIMARY KEY (UUID)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COMMENT '秒杀用户表';

# 插入一条记录（未经过MD5的密码为000000, 两次MD5后的密码为记录中的密码，两次MD5的salt一样）
INSERT INTO seckill_user (phone, nickname, password, salt)
VALUES (13111897391, 'xizizzz', '5e7b3a9754c2777f96174d4ccb980d23', '1a2b3c4d');


# ************************************************************************
# 创建商品表
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT   COMMENT '商品ID',
  `goods_name`   VARCHAR(16)         DEFAULT NULL     COMMENT '商品名称',
  `goods_title`  VARCHAR(64)         DEFAULT NULL     COMMENT '商品标题',
  `goods_img`    VARCHAR(64)         DEFAULT NULL     COMMENT '商品的图片',
  `goods_detail` LONGTEXT            DEFAULT NULL     COMMENT '商品的详情介绍',
  `goods_price`  DECIMAL(10, 2)      DEFAULT '0.00'   COMMENT '商品单价',
  `goods_stock`  INT(11)             DEFAULT '0'      COMMENT '商品库存，-1表示没有限制',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4;

# 在商品表中添加4条记录
INSERT INTO `goods` VALUES
  (1, 'iphoneX', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', '/img/iphonex.png','Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', 8765.00, 1000),
  (2, '华为Meta9', '华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '/img/meta10.png','华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', 3212.00, 1000),
  (3, 'iphone8', 'Apple iPhone 8 (A1865) 64GB 银色 移动联通电信4G手机', '/img/iphone8.png','Apple iPhone 8 (A1865) 64GB 银色 移动联通电信4G手机', 5589.00, 1000),
  (4, '小米6', '小米6 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '/img/mi6.png', '小米6 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', 3212.00, 1000);


# ************************************************************************
# 秒杀商品表
DROP TABLE IF EXISTS `seckill_goods`;
CREATE TABLE `seckill_goods` (
  `id`            BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT '秒杀的商品表',
  `goods_id`      BIGINT(20)          DEFAULT NULL    COMMENT '商品Id',
  `seckill_price` DECIMAL(10, 2)      DEFAULT '0.00'  COMMENT '秒杀价',
  `stock_count`   INT(11)             DEFAULT NULL    COMMENT '库存数量',
  `start_date`    DATETIME            DEFAULT NULL    COMMENT '秒杀开始时间',
  `end_date`      DATETIME            DEFAULT NULL    COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`),
  KEY `goods_id` (`goods_id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4;

# 插入两条记录
INSERT INTO `seckill_goods` VALUES
  (1, 1, 0.01, 10, '2021-05-25 17:17:53', '2021-05-26 17:17:53'),
  (2, 2, 0.01, 10, '2021-05-25 17:17:53', '2021-05-26 17:17:53'),
  (3, 3, 0.01, 10, '2021-05-25 17:17:53', '2021-05-26 17:17:53'),
  (4, 4, 0.01, 10, '2021-05-25 17:17:53', '2021-05-26 17:17:53');

# ************************************************************************
# 秒杀订单表
DROP TABLE IF EXISTS `seckill_order`;
CREATE TABLE `seckill_order` (
  `id`       BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id`  BIGINT(20)          DEFAULT NULL     COMMENT '用户ID',
  `order_id` BIGINT(20)          DEFAULT NULL     COMMENT '订单ID',
 `goods_id`  BIGINT(20)          DEFAULT NULL     COMMENT '商品ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_uid_gid` (`user_id`, `goods_id`) USING BTREE COMMENT '定义联合索引'
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1551
  DEFAULT CHARSET = utf8mb4;


# ************************************************************************
# 订单信息表
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id`               BIGINT(20)          NOT NULL AUTO_INCREMENT,
  `user_id`          BIGINT(20)          DEFAULT NULL   COMMENT '用户ID',
  `goods_id`         BIGINT(20)          DEFAULT NULL   COMMENT '商品ID',
  `delivery_addr_id` BIGINT(20)          DEFAULT NULL   COMMENT '收获地址ID',
  `goods_name`       VARCHAR(16)         DEFAULT NULL   COMMENT '冗余过来的商品名称',
  `goods_count`      INT(11)             DEFAULT '0'    COMMENT '商品数量',
  `goods_price`      DECIMAL(10, 2)      DEFAULT '0.00' COMMENT '商品单价',
  `order_channel`    TINYINT(4)          DEFAULT '0'    COMMENT '1-pc，2-android，3-ios',
  `status`           TINYINT(4)          DEFAULT '0'    COMMENT '订单状态，0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
  `create_date`      DATETIME            DEFAULT NULL   COMMENT '订单的创建时间',
  `pay_date`         DATETIME            DEFAULT NULL   COMMENT '支付时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1565
  DEFAULT CHARSET = utf8mb4;



