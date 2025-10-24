CREATE TABLE account (
    account_id INT AUTO_INCREMENT COMMENT '账户ID',
    account_name VARCHAR(50) COMMENT '账户名称',
    password VARCHAR(50) COMMENT '账户密码',
    email VARCHAR(100) NOT NULL COMMENT '账户email',
    account_status INT NOT NULL DEFAULT 0 COMMENT '账户状态：0-正常 1-冻结 2-注销',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '账户创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '账户更新时间',
    PRIMARY KEY (account_id),
    UNIQUE KEY uk_email (email), -- 保留email的唯一索引（确保非空且唯一）
    KEY idx_email_accountname (email, account_name) -- 复合索引：email在前，account_name在后
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';