use cafe_product;
DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
                           `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                           `title` varchar(128) DEFAULT NULL COMMENT '标题',
                           `cover_img` varchar(128) DEFAULT NULL COMMENT '封面图',
                           `detail` varchar(256) DEFAULT '' COMMENT '详情',
                           `old_amount` decimal(16,2) DEFAULT NULL COMMENT '老价格',
                           `amount` decimal(16,2) DEFAULT NULL COMMENT '新价格',
                           `stock` int(11) DEFAULT NULL COMMENT '库存',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `lock_stock` int(11) DEFAULT '0' COMMENT '锁定库存',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;

INSERT INTO `product` (`id`, `title`, `cover_img`, `detail`, `old_amount`, `amount`, `stock`, `create_time`, `lock_stock`)
VALUES
    (1,'pillow','https://file.xdclass.net/video/2020/alibabacloud/zt-alibabacloud.png','https://file.xdclass.net/video/2021/60-MLS/summary.jpeg',32.00,213.00,100,'2021-09-12 00:00:00',86),
    (2,'cup1','https://file.xdclass.net/video/2020/alibabacloud/zt-alibabacloud.png','https://file.xdclass.net/video/2021/59-Postman/summary.jpeg',432.00,42.00,100,'2021-03-12 00:00:00',86),
    (3,'cup2','https://file.xdclass.net/video/2020/alibabacloud/zt-alibabacloud.png','https://file.xdclass.net/video/2021/60-MLS/summary.jpeg',35.00,12.00,20,'2022-09-22 00:00:00',15),
    (4,'cup3','https://file.xdclass.net/video/2020/alibabacloud/zt-alibabacloud.png','https://file.xdclass.net/video/2021/60-MLS/summary.jpeg',12.00,14.00,20,'2022-11-12 00:00:00',2);

UNLOCK TABLES;

DROP TABLE IF EXISTS `product_task`;

CREATE TABLE `product_task` (
                                `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                                `product_id` bigint(11) DEFAULT NULL COMMENT '商品id',
                                `buy_num` int(11) DEFAULT NULL COMMENT '购买数量',
                                `product_name` varchar(128) DEFAULT NULL COMMENT '商品标题',
                                `lock_state` varchar(32) DEFAULT NULL COMMENT '锁定状态锁定LOCK  完成FINISH-取消CANCEL',
                                `out_trade_no` varchar(32) DEFAULT NULL,
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `product_task` WRITE;

INSERT INTO `product_task` (`id`, `product_id`, `buy_num`, `product_name`, `lock_state`, `out_trade_no`, `create_time`)
VALUES
    (27,2,2,'cup1','LOCK','ngnDfcrExq4udzmUMFarN4piiAcxCtPh','2021-03-06 11:39:26'),
    (28,1,2,'pillow','LOCK','ngnDfcrExq4udzmUMFarN4piiAcxCtPh','2021-03-06 11:39:26'),
    (29,2,2,'cup1','FINISH','9uIakxXHAgr0X33YMIyUmCotbKADcSQ6','2021-03-06 11:57:49'),
    (30,1,2,'pillow','FINISH','9uIakxXHAgr0X33YMIyUmCotbKADcSQ6','2021-03-06 11:57:49'),
    (31,1,2,'pillow','CANCEL','9wnSzlQQ9KnCtyCJmsxLcMyZcs6MFmbW','2021-03-06 12:06:53'),
    (32,2,2,'cup1','CANCEL','9wnSzlQQ9KnCtyCJmsxLcMyZcs6MFmbW','2021-03-06 12:06:53'),
    (33,1,2,'pillow','FINISH','aHEuu6vnDIfJ9gMUeHkO3LwiCkgoLxmQ','2021-03-07 11:13:09'),
    (34,2,2,'cup1','FINISH','aHEuu6vnDIfJ9gMUeHkO3LwiCkgoLxmQ','2021-03-07 11:13:09'),
    (35,2,2,'cup1','LOCK','6eBBzmFJsmhWYezusEPb2s8VGbkj44SF','2021-03-07 11:17:12'),
    (36,1,2,'pillow','LOCK','6eBBzmFJsmhWYezusEPb2s8VGbkj44SF','2021-03-07 11:17:12'),
    (37,1,2,'pillow','CANCEL','1DkCt7ub8SM6DhuocbP6lDQ9O74Vpjsg','2021-03-07 11:29:30'),
    (38,2,2,'cup1','CANCEL','1DkCt7ub8SM6DhuocbP6lDQ9O74Vpjsg','2021-03-07 11:29:30'),
    (39,2,2,'cup1','CANCEL','WCiI6yrEVNGc5xlSfyOT5yoISNFvNh0F','2021-03-07 11:29:49'),
    (40,1,2,'pillow','CANCEL','WCiI6yrEVNGc5xlSfyOT5yoISNFvNh0F','2021-03-07 11:29:49'),
    (41,1,2,'pillow','FINISH','9TMSQAdlYmV8isUGHq5e3czaxT0yZzN3','2021-03-07 11:34:45'),
    (42,2,2,'cup1','LOCK','9TMSQAdlYmV8isUGHq5e3czaxT0yZzN3','2021-03-07 11:34:45'),
    (43,2,2,'cup1','LOCK','iVbHc46YeACQnF8AUjQGQZtLPzYPKYji','2021-03-07 11:35:11'),
    (44,1,2,'pillow','LOCK','iVbHc46YeACQnF8AUjQGQZtLPzYPKYji','2021-03-07 11:35:11'),
    (45,1,2,'pillow','LOCK','qq8rNpVUGP8nmfJefGVrWvy2qmHFomu6','2021-03-07 11:39:31'),
    (46,2,2,'cup1','LOCK','qq8rNpVUGP8nmfJefGVrWvy2qmHFomu6','2021-03-07 11:39:31'),
    (47,2,2,'cup1','LOCK','bCweaVkaaHtsxCRKRCKIDYlLr1FZczeo','2021-03-07 11:39:57'),
    (48,1,2,'pillow','LOCK','bCweaVkaaHtsxCRKRCKIDYlLr1FZczeo','2021-03-07 11:39:57'),
    (49,1,2,'pillow','FINISH','YEgyASJXWg7NUm0ftc3OLFL1L3aWmf0J','2021-03-07 11:48:11'),
    (50,2,2,'cup1','FINISH','YEgyASJXWg7NUm0ftc3OLFL1L3aWmf0J','2021-03-07 11:48:11'),
    (51,2,2,'cup1','CANCEL','bmPgKITSuqWoEWl9wccm0E4Bw1adoyHM','2021-03-07 11:48:27'),
    (52,1,2,'pillow','CANCEL','bmPgKITSuqWoEWl9wccm0E4Bw1adoyHM','2021-03-07 11:48:27'),
    (53,1,2,'pillow','LOCK','SahcCrnFuRTDa2UaiiGVXenP2CL6rO3I','2021-03-07 12:14:10'),
    (54,2,2,'cup1','LOCK','SahcCrnFuRTDa2UaiiGVXenP2CL6rO3I','2021-03-07 12:14:10'),
    (55,2,2,'cup1','FINISH','k1VB63mwJ31MTHkCECXOtOxs1JcytfMp','2021-03-07 12:22:40'),
    (56,1,2,'pillow','FINISH','k1VB63mwJ31MTHkCECXOtOxs1JcytfMp','2021-03-07 12:22:40'),
    (57,1,2,'pillow','CANCEL','fxnIMMIS8E0GOrYrMKbfVudD8b1xgoHG','2021-03-07 12:23:00'),
    (58,2,2,'cup1','CANCEL','fxnIMMIS8E0GOrYrMKbfVudD8b1xgoHG','2021-03-07 12:23:00'),
    (59,1,2,'pillow','FINISH','BuVNrPTMH2daY8mOYPJavESS27XmJcbe','2021-03-07 16:30:11'),
    (60,2,2,'cup1','FINISH','BuVNrPTMH2daY8mOYPJavESS27XmJcbe','2021-03-07 16:30:11'),
    (61,2,2,'cup1','FINISH','fVYsNXN9ZQzUC4j1D70wA7QFc8tqLrYe','2021-03-13 03:36:34'),
    (62,1,2,'pillow','FINISH','fVYsNXN9ZQzUC4j1D70wA7QFc8tqLrYe','2021-03-13 03:36:34'),
    (63,3,2,'cup2','FINISH','el2CL9c2R4ZgUNIvY21YK4gwC1LCNE1S','2021-03-14 02:58:09'),
    (64,1,2,'pillow','FINISH','el2CL9c2R4ZgUNIvY21YK4gwC1LCNE1S','2021-03-14 02:58:09'),
    (65,2,2,'cup1','FINISH','el2CL9c2R4ZgUNIvY21YK4gwC1LCNE1S','2021-03-14 02:58:09');

UNLOCK TABLES;
