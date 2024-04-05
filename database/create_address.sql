USE cafe_user;
DROP TABLE IF EXISTS `address`;

CREATE TABLE `address` (
                           `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                           `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                           `default_status` int(1) DEFAULT NULL COMMENT '是否默认收货地址：0->否；1->是',
                           `receiver_name` varchar(64) DEFAULT NULL COMMENT '收发货人姓名',
                           `phone` varchar(64) DEFAULT NULL COMMENT '收货人电话',
                           `state` varchar(64) DEFAULT NULL COMMENT '省/直辖市',
                           `city` varchar(64) DEFAULT NULL COMMENT '市',
                           `district` varchar(64) DEFAULT NULL COMMENT '区',
                           `detailed_address` varchar(200) DEFAULT NULL COMMENT '详细地址',
                           `create_at` datetime DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;

INSERT INTO `address` (`id`, `user_id`, `default_status`, `receiver_name`, `phone`, `state`, `city`, `district`, `detailed_address`, `create_at`)
VALUES
    (39,3,0,'小滴课堂-隔壁老王','12321312321','广东省','广州市','天河区','运营中心-老王隔壁1号','2021-02-05 10:48:45'),
    (40,3,0,'小滴课堂-隔壁老王','12321312321','广东省','广州市','天河区','运营中心-老王隔壁2号','2021-02-05 10:49:32'),
    (43,9,0,'小滴课堂-隔壁大哥','12321312321','广东省','广州市','天河区','运营中心-老王隔壁1号','2021-02-05 10:48:45'),
    (44,10,0,'小滴课堂-隔壁小文','12321312321','广东省','广州市','天河区','运营中心-老王隔壁1号','2021-02-05 10:48:45'),
    (45,36,0,'小滴课堂-隔壁老王','12321312321','广东省','广州市','天河区','运营中心-老王隔壁5号','2021-03-01 17:36:21'),
    (46,36,0,'小滴课堂-隔壁老王','12321312321','广东省','广州市','天河区','运营中心-老王隔壁88号','2021-03-01 17:36:29'),
    (47,36,0,'小滴课堂-隔壁老王','12321312321','广东省','广州市','天河区','运营中心-老王隔壁99号','2021-03-01 17:36:34'),
    (48,36,1,'小滴课堂-隔壁老王','12321312321','广东省','广州市','天河区','运营中心-老王隔壁5号','2021-03-07 16:20:34'),
    (49,37,1,'小滴课堂-隔壁老王','12321312321','广东省','广州市','天河区','运营中心-老王隔壁5号-555','2021-03-13 11:34:41'),
    (50,40,0,'小滴课堂-隔壁老王','12321312321','广东省','广州市','天河区','运营中心-老王隔壁53333号','2021-03-14 10:52:52'),
    (51,40,1,'小滴课堂-隔壁老王','12321312321','广东省','广州市','天河区','运营中心-老王隔壁533311113号','2021-03-14 10:52:58');

/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;
