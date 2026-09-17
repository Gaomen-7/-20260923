-- 【活动管理模块】优惠券建表 SQL
-- 库: project20206

USE project20206;

CREATE TABLE IF NOT EXISTS tbl_coupon (
    id INT AUTO_INCREMENT PRIMARY KEY,
    coupon_name VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    coupon_type TINYINT DEFAULT 1 COMMENT '类型：1=满减，2=折扣',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额（满减时用）',
    discount_rate DECIMAL(3,2) DEFAULT 0 COMMENT '折扣率（折扣时用，如0.90=9折）',
    min_amount DECIMAL(10,2) DEFAULT 0 COMMENT '使用门槛（满多少可用）',
    start_time VARCHAR(50) COMMENT '开始时间',
    end_time VARCHAR(50) COMMENT '结束时间',
    total_count INT DEFAULT 0 COMMENT '发放数量',
    received_count INT DEFAULT 0 COMMENT '已领取数量',
    status TINYINT DEFAULT 1 COMMENT '是否有效：0=无效，1=有效',
    publisher VARCHAR(50) COMMENT '发布者',
    create_time VARCHAR(50) COMMENT '创建时间',
    update_time VARCHAR(50) COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 测试数据
INSERT INTO tbl_coupon (coupon_name, coupon_type, discount_amount, discount_rate, min_amount, start_time, end_time, total_count, received_count, status, publisher, create_time) VALUES
('满减优惠券', 1, 50.00, 0, 200.00, '2026-09-01', '2026-09-30', 100, 50, 1, 'admin', '2026-09-01 09:00:00'),
('周年庆优惠券', 2, 0, 0.90, 100.00, '2026-09-01', '2026-09-30', 100, 50, 1, 'admin', '2026-09-01 09:00:00'),
('新人专享券', 1, 20.00, 0, 50.00, '2026-09-10', '2026-10-10', 200, 30, 1, 'admin', '2026-09-10 10:00:00'),
('双11预热券', 2, 0, 0.85, 300.00, '2026-10-20', '2026-11-11', 500, 0, 1, 'admin', '2026-09-12 14:00:00'),
('过期优惠券', 1, 100.00, 0, 500.00, '2026-01-01', '2026-01-31', 100, 100, 0, 'admin', '2026-01-01 08:00:00');
