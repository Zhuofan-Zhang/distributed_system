DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
                        `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                        `name` varchar(128) DEFAULT NULL COMMENT '昵称',
                        `pwd` varchar(124) DEFAULT NULL COMMENT '密码',
                        `avatar` varchar(524) DEFAULT NULL COMMENT '头像',
                        `slogan` varchar(524) DEFAULT NULL COMMENT '用户签名',
                        `gender` tinyint(2) DEFAULT '1' COMMENT '0表示女，1表示男',
                        `points` int(10) DEFAULT '0' COMMENT '积分',
                        `created_at` datetime DEFAULT NULL,
                        `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
                        `secret` varchar(12) DEFAULT NULL COMMENT '盐，用于个人敏感信息处理',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `mail_idx` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO `user` (`id`, `name`, `pwd`, `avatar`, `slogan`, `gender`, `points`, `created_at`, `email`, `secret`)
VALUES
    (3,'Anna小姐姐','$1$V908ssVg$HIp7IF/MbiUAsyQ.ZwXAb/','https://xdclass-1024shop-img.oss-cn-shenzhen.aliyuncs.com/user/2021/02/03/39473aa1029a430298ac2620dd819962.jpeg','人生需要动态规划，学习需要贪心算法',1,0,'2021-02-04 10:31:07','7946669181@qq.com','$1$V908ssVg'),
    (4,'老王','$1$3znxHOtF$u1yNaVN6XIA/MLqB5RYgP0','https://xdclass-1024shop-img.oss-cn-shenzhen.aliyuncs.com/user/2021/02/03/39473aa1029a430298ac2620dd819962.jpeg','人生需要动态规划，学习需要贪心算法',1,0,'2021-02-23 18:17:20','7946669218@qq.com','$1$3znxHOtF'),
    (7,'小d','$1$MqBfDGFj$LYVccjLN7TZNfOWESgw4e.','https://xdclass-1024shop-img.oss-cn-shenzhen.aliyuncs.com/user/2021/02/03/39473aa1029a430298ac2620dd819962.jpeg','人生需要动态规划，学习需要贪心算法',1,0,'2021-02-23 22:55:15','794666911118@qq.com','$1$MqBfDGFj'),
    (36,'二当家小D-分布式事务---','$1$XDlvKGlo$7ZTOKwOOFwV1YmNjON6iL1','https://xdclass-1024shop-img.oss-cn-shenzhen.aliyuncs.com/user/2021/02/03/39473aa1029a430298ac2620dd819962.jpeg','人生需要动态规划，学习需要贪心算法',1,0,'2021-02-24 21:52:48','7946663918@qq.com','$1$XDlvKGlo'),
    (37,'Rancher测试','$1$m6onNPfr$.L8IIen/XfpN2V8FS3S0B/','https://xdclass-1024shop-img.oss-cn-shenzhen.aliyuncs.com/user/2021/03/13/e4f463d20e424f7cbe766d4d02206b66.jpeg','人生需要动态规划，学习需要贪心算法',1,0,'2021-03-13 11:33:25','794662426918@qq.com','$1$m6onNPfr'),
    (40,'小滴课堂','$1$0DnHF7Hd$MTv.7QuEQ.oxSrWh7cxM6.','https://xdclass-1024shop-img.oss-cn-shenzhen.aliyuncs.com/user/2021/02/03/39473aa1029a430298ac2620dd819962.jpeg','人生需要动态规划，学习需要贪心算法',1,0,'2021-03-14 10:51:47','794666918@qq.com','$1$0DnHF7Hd');

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
