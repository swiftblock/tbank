DROP TABLE IF EXISTS `offer`;
CREATE TABLE `offer` (
  `offer_no` varchar(64) NOT NULL COMMENT '报价编号',
  `status` varchar(10) DEFAULT NULL COMMENT '状态码',
  `price_trx` decimal(48,10) DEFAULT NULL COMMENT 'TRX单价',
  `price_vena` decimal(48,10) DEFAULT NULL COMMENT 'VENA单价',
  `start_time` datetime(3) DEFAULT NULL COMMENT '报价起始时间',
  `end_time` datetime(3) DEFAULT NULL COMMENT '报价截止时间',
  PRIMARY KEY (`offer_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `order_no` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `user_address` varchar(128) DEFAULT NULL COMMENT '用户地址',
  `resource_type` varchar(10) DEFAULT NULL COMMENT '购买能源类型',
  `resource_amount` decimal(32,10) DEFAULT NULL COMMENT '购买能源数量',
  `freeze_amount` decimal(48,10) DEFAULT NULL,
  `freeze_interval` bigint(20) DEFAULT NULL COMMENT '冻结时间(按天记录)',
  `freeze_msg` varchar(256) DEFAULT NULL,
  `fee` decimal(48,10) DEFAULT NULL,
  `fee_rate` decimal(48,10) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL COMMENT '订单状态',
  `tx_hash` varchar(64) NOT NULL COMMENT '交易hash',
  `raw_tx` text COMMENT '交易内容',
  `currency` varchar(10) DEFAULT NULL COMMENT '支付币种',
  `pay_amount` decimal(32,10) DEFAULT NULL COMMENT '支付数量',
  `pay_block_time` datetime DEFAULT NULL COMMENT '支付的区块时间',
  `offer_no` varchar(64) DEFAULT NULL COMMENT '报价编号',
  `resource_tx_hash` varchar(64) DEFAULT NULL COMMENT '抵押交易hash',
  `freeze_id` varchar(64) DEFAULT NULL,
  `refund_tx_hash` varchar(64) DEFAULT NULL COMMENT '退款交易hash',
  `gmt_create` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`order_no`),
  UNIQUE KEY `INDEX_ORDER_TX_HASH` (`tx_hash`) USING BTREE,
  UNIQUE KEY `INDEX_ORDER_FREEZE_ID` (`freeze_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `combo`;
CREATE TABLE `combo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `freeze_amount` bigint(20) DEFAULT NULL COMMENT '租借数量',
  `freeze_interval` bigint(20) DEFAULT NULL COMMENT '冻结天数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
