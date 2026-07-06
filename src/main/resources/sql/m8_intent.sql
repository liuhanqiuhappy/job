ALTER TABLE `user` ADD COLUMN `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;

CREATE TABLE `intent` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `from_user_id` BIGINT NOT NULL COMMENT '发起方用户ID（个人或企业）',
  `to_user_id` BIGINT NOT NULL COMMENT '接收方用户ID',
  `resume_id` BIGINT DEFAULT NULL COMMENT '关联的简历ID（个人发起时必填）',
  `job_id` BIGINT DEFAULT NULL COMMENT '关联的职位ID（企业发起时必填）',
  `type` TINYINT NOT NULL COMMENT '意向类型：0=个人向企业发送意向书，1=企业向个人发送邀约',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待回应，1=已接受，2=已拒绝，3=已过期',
  `message` VARCHAR(500) DEFAULT NULL COMMENT '附言（自荐语或邀约语）',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间（默认7天后）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_from_user` (`from_user_id`),
  KEY `idx_to_user` (`to_user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意向沟通表';