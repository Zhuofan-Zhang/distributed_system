DROP TABLE IF EXISTS `coupon`;

CREATE TABLE `coupon` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                          `category` varchar(11) DEFAULT NULL COMMENT '优惠卷类型[NEW_USER注册赠券，TASK任务卷，PROMOTION促销劵]',
                          `publish` varchar(11) DEFAULT NULL COMMENT '发布状态, PUBLISH发布，DRAFT草稿，OFFLINE下线',
                          `coupon_img` varchar(524) DEFAULT NULL COMMENT '优惠券图片',
                          `coupon_title` varchar(128) DEFAULT NULL COMMENT '优惠券标题',
                          `price_deducted` decimal(16,2) DEFAULT NULL COMMENT '抵扣价格',
                          `user_limit` int(11) DEFAULT NULL COMMENT '每人限制张数',
                          `valid_from` datetime DEFAULT NULL COMMENT '优惠券开始有效时间',
                          `valid_until` datetime DEFAULT NULL COMMENT '优惠券失效时间',
                          `inventory` int(11) DEFAULT NULL COMMENT '优惠券总量',
                          `remaining` int(11) DEFAULT '0' COMMENT '库存',
                          `created_at` datetime DEFAULT NULL,
                          `condition_price` decimal(16,2) DEFAULT NULL COMMENT '满多少才可以使用',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `coupon` WRITE;

INSERT INTO `coupon` (`id`, `category`, `publish`, `coupon_img`, `coupon_title`, `price_deducted`, `user_limit`, `valid_from`, `valid_until`, `inventory`, `remaining`, `created_at`, `condition_price`)
VALUES
    (18,'NEW_USER','PUBLISH','https://file.xdclass.net/video/2020/alibabacloud/zt-alibabacloud.png','永久有效-新人注册-0元满减-5元抵扣劵-限领取2张-不可叠加使用',5.00,1,'2000-01-01 00:00:00','2099-01-29 00:00:00',100000000,99998997,'2020-12-26 16:33:02',0.00),
    (19,'PROMOTION','PUBLISH','https://file.xdclass.net/video/2020/alibabacloud/zt-alibabacloud.png','有效中-21年1月到25年1月-20元满减-5元抵扣劵-限领取2张-不可叠加使用',5.00,10,'2000-01-29 00:00:00','2025-01-29 00:00:00',200,0,'2020-12-26 16:33:03',20.00),
    (20,'PROMOTION','PUBLISH','https://file.xdclass.net/video/2020/alibabacloud/zt-alibabacloud.png','有效中-20年8月到21年9月-商品id1-8.8元抵扣劵-限领取2张-不可叠加使用',8.80,2,'2020-08-01 00:00:00','2021-09-29 00:00:00',100,96,'2020-12-26 16:33:03',0.00),
    (21,'PROMOTION','PUBLISH','https://file.xdclass.net/video/2020/alibabacloud/zt-alibabacloud.png','有效中-20年8月到21年9月-商品id2-9.9元抵扣劵-限领取2张-可叠加使用',8.80,2,'2020-08-01 00:00:00','2021-09-29 00:00:00',100,94,'2020-12-26 16:33:03',0.00),
    (22,'PROMOTION','PUBLISH','https://file.xdclass.net/video/2020/alibabacloud/zt-alibabacloud.png','过期-20年8月到20年9月-商品id3-6元抵扣劵-限领取1张-可叠加使用',6.00,1,'2020-08-01 00:00:00','2020-09-29 00:00:00',100,100,'2020-12-26 16:33:03',0.00);

UNLOCK TABLES;

DROP TABLE IF EXISTS `coupon_record`;

CREATE TABLE `coupon_record` (
                                 `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                                 `coupon_id` bigint(11) DEFAULT NULL COMMENT '优惠券id',
                                 `create_at` datetime DEFAULT NULL COMMENT '创建时间获得时间',
                                 `usage_state` varchar(32) DEFAULT NULL COMMENT '使用状态  可用 NEW,已使用USED,过期 EXPIRED;',
                                 `user_id` bigint(11) DEFAULT NULL COMMENT '用户id',
                                 `user_name` varchar(128) DEFAULT NULL COMMENT '用户昵称',
                                 `coupon_title` varchar(128) DEFAULT NULL COMMENT '优惠券标题',
                                 `valid_from` datetime DEFAULT NULL COMMENT '开始时间',
                                 `valid_until` datetime DEFAULT NULL COMMENT '结束时间',
                                 `order_id` bigint(11) DEFAULT NULL COMMENT '订单id',
                                 `price_deducted` decimal(16,2) DEFAULT NULL COMMENT '抵扣价格',
                                 `condition_price` decimal(16,2) DEFAULT NULL COMMENT '满多少才可以使用',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `coupon_record` WRITE;

INSERT INTO `coupon_record` (`id`, `coupon_id`, `create_at`, `usage_state`, `user_id`, `user_name`, `coupon_title`, `valid_from`, `valid_until`, `order_id`, `price_deducted`, `condition_price`)
VALUES
    (1,19,'2021-02-18 10:37:42','NEW',3,'Anna小姐姐','有效中-21年1月到25年1月-20元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-29 00:00:00','2025-01-29 00:00:00',NULL,5.00,20.00),
    (7,19,'2021-02-18 10:37:44','NEW',3,'Anna小姐姐','有效中-21年1月到25年1月-20元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-29 00:00:00','2025-01-29 00:00:00',NULL,5.00,20.00),
    (8,19,'2021-02-18 10:37:45','NEW',3,'Anna小姐姐','有效中-21年1月到25年1月-20元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-29 00:00:00','2025-01-29 00:00:00',NULL,5.00,20.00),
    (9,21,'2021-02-18 10:38:40','NEW',3,'Anna小姐姐','有效中-20年8月到21年9月-商品id2-9.9元抵扣劵-限领取2张-可叠加使用','2020-08-01 00:00:00','2021-09-29 00:00:00',NULL,8.80,0.00),
    (10,21,'2021-02-18 10:38:41','NEW',3,'Anna小姐姐','有效中-20年8月到21年9月-商品id2-9.9元抵扣劵-限领取2张-可叠加使用','2020-08-01 00:00:00','2021-09-29 00:00:00',NULL,8.80,0.00),
    (14,18,'2021-02-18 13:58:50','NEW',3,'Anna小姐姐','永久有效-新人注册-0元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-01 00:00:00','2099-01-29 00:00:00',NULL,5.00,0.00),
    (17,18,'2021-02-18 14:40:17','NEW',3,'jack','永久有效-新人注册-0元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-01 00:00:00','2099-01-29 00:00:00',NULL,5.00,0.00),
    (18,18,'2021-02-23 18:17:45','NEW',4,'老王','永久有效-新人注册-0元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-01 00:00:00','2099-01-29 00:00:00',NULL,5.00,0.00),
    (48,18,'2021-02-24 21:44:09','NEW',12,'二当家小D-普通事务','永久有效-新人注册-0元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-01 00:00:00','2099-01-29 00:00:00',NULL,5.00,0.00),
    (50,18,'2021-02-24 21:52:49','USED',36,'二当家小D-分布式事务---','永久有效-新人注册-0元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-01 00:00:00','2099-01-29 00:00:00',NULL,5.00,0.00),
    (53,19,'2021-03-08 15:47:16','NEW',36,'二当家小D-分布式事务---','有效中-21年1月到25年1月-20元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-29 00:00:00','2025-01-29 00:00:00',NULL,5.00,20.00),
    (54,18,'2021-03-13 11:33:26','USED',37,'Rancher测试','永久有效-新人注册-0元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-01 00:00:00','2099-01-29 00:00:00',NULL,5.00,0.00),
    (55,18,'2021-03-14 10:47:31','NEW',444,'jack','永久有效-新人注册-0元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-01 00:00:00','2099-01-29 00:00:00',NULL,5.00,0.00),
    (56,18,'2021-03-14 10:51:47','USED',40,'小滴课堂','永久有效-新人注册-0元满减-5元抵扣劵-限领取2张-不可叠加使用','2000-01-01 00:00:00','2099-01-29 00:00:00',NULL,5.00,0.00);

UNLOCK TABLES;

DROP TABLE IF EXISTS `coupon_task`;

CREATE TABLE `coupon_task` (
                               `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                               `coupon_record_id` bigint(11) DEFAULT NULL COMMENT '优惠券记录id',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `out_trade_no` varchar(64) DEFAULT NULL COMMENT '订单号',
                               `lock_state` varchar(32) DEFAULT NULL COMMENT '锁定状态 锁定LOCK-完成FINISH 取消CANCEL',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `coupon_task` WRITE;

INSERT INTO `coupon_task` (`id`, `coupon_record_id`, `create_time`, `out_trade_no`, `lock_state`)
VALUES
    (7,50,'2021-03-06 11:57:49','9uIakxXHAgr0X33YMIyUmCotbKADcSQ6','FINISH'),
    (8,54,'2021-03-13 11:36:34','fVYsNXN9ZQzUC4j1D70wA7QFc8tqLrYe','FINISH'),
    (9,56,'2021-03-14 10:58:10','el2CL9c2R4ZgUNIvY21YK4gwC1LCNE1S','FINISH');

UNLOCK TABLES;

DROP TABLE IF EXISTS `undo_log`;

CREATE TABLE `undo_log` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `branch_id` bigint(20) NOT NULL,
                            `xid` varchar(100) NOT NULL,
                            `context` varchar(128) NOT NULL,
                            `rollback_info` longblob NOT NULL,
                            `log_status` int(11) NOT NULL,
                            `log_created` datetime NOT NULL,
                            `log_modified` datetime NOT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
