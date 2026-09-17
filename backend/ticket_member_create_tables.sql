-- 【会员管理模块】建表 SQL
-- 库: project20206

USE project20206;

CREATE TABLE IF NOT EXISTS tbl_member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL COMMENT '会员昵称',
    avatar VARCHAR(200) COMMENT '头像',
    phone VARCHAR(20) COMMENT '手机号',
    member_type TINYINT DEFAULT 1 COMMENT '会员类型：1=普通会员，2=VIP会员，3=黄金会员',
    source VARCHAR(20) COMMENT '来源：APP/小程序/PC/H5',
    balance DECIMAL(10,2) DEFAULT 0 COMMENT '会员余额',
    status TINYINT DEFAULT 1 COMMENT '状态：1=正常，0=黑名单',
    register_time VARCHAR(50) COMMENT '注册时间',
    create_time VARCHAR(50),
    update_time VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 测试数据（10条）
INSERT INTO tbl_member (nickname, avatar, phone, member_type, source, balance, status, register_time) VALUES
('宫崎骏', '', '18112341234', 1, 'APP', 234.00, 1, '2026-02-14 02:14:00'),
('宫崎骏', '', '18112341234', 2, '小程序', 234.00, 0, '2026-02-14 02:14:00'),
('宫崎骏', '', '18112341234', 3, 'PC', 234.00, 1, '2026-02-14 02:14:00'),
('宫崎骏', '', '18112341234', 1, 'H5', 234.00, 1, '2026-02-14 02:14:00'),
('宫崎骏', '', '18112341234', 1, 'APP', 234.00, 1, '2026-02-14 02:14:00'),
('宫崎骏', '', '18112341234', 1, '小程序', 234.00, 1, '2026-02-14 02:14:00'),
('宫崎骏', '', '18112341234', 1, 'PC', 234.00, 1, '2026-02-14 02:14:00'),
('宫崎骏', '', '18112341234', 1, 'H5', 234.00, 1, '2026-02-14 02:14:00'),
('宫崎骏', '', '18112341234', 1, 'APP', 234.00, 1, '2026-02-14 02:14:00'),
('宫崎骏', '', '18112341234', 1, 'APP', 234.00, 1, '2026-02-14 02:14:00');
