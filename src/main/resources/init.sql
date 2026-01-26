-- 创建数据库
CREATE DATABASE IF NOT EXISTS auth_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE auth_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录账号）',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    enabled INT NOT NULL DEFAULT 1 COMMENT '账号状态：0-禁用，1-启用',
    locked INT NOT NULL DEFAULT 0 COMMENT '账号锁定：0-未锁定，1-已锁定',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试用户
-- 密码使用 {noop} 前缀表示不加密，生产环境应使用 BCrypt 加密
INSERT INTO sys_user (username, password, real_name, email, phone, enabled, locked) VALUES
('admin', '{noop}admin123', '系统管理员', 'admin@example.com', '13800138000', 1, 0),
('user', '{noop}user123', '普通用户', 'user@example.com', '13800138001', 1, 0),
('test', '{noop}test123', '测试用户', 'test@example.com', '13800138002', 1, 0)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    real_name = VALUES(real_name),
    email = VALUES(email);

-- 查询用户表验证
SELECT id, username, real_name, email, phone, enabled, locked, create_time, update_time
FROM sys_user;
