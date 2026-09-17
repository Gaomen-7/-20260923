-- 【订单管理模块】建表 SQL
-- 库: project20206

USE project20206;

-- 1. 订单主表
CREATE TABLE IF NOT EXISTS tbl_order_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL COMMENT '订单编号',
    user_id INT COMMENT '会员ID',
    user_name VARCHAR(64) COMMENT '会员昵称',
    user_phone VARCHAR(20) COMMENT '会员手机号',
    receiver_name VARCHAR(64) COMMENT '收货人',
    receiver_phone VARCHAR(20) COMMENT '收货电话',
    receiver_address VARCHAR(255) COMMENT '收货地址',
    total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '订单总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '折扣金额',
    actual_amount DECIMAL(10,2) DEFAULT 0 COMMENT '实收款',
    freight DECIMAL(10,2) DEFAULT 0 COMMENT '运费',
    pay_status TINYINT DEFAULT 0 COMMENT '付款状态：0=未付款，1=已付款',
    order_status TINYINT DEFAULT 0 COMMENT '订单状态：0=待付款，1=待发货，2=已发货，3=已完成，4=已取消',
    remark VARCHAR(255) COMMENT '备注',
    create_time VARCHAR(50) COMMENT '创建时间',
    update_time VARCHAR(50) COMMENT '修改时间',
    UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 订单明细表
CREATE TABLE IF NOT EXISTS tbl_order_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL COMMENT '关联订单主表ID',
    order_no VARCHAR(64) COMMENT '订单编号（冗余）',
    goods_id INT COMMENT '商品ID',
    goods_name VARCHAR(255) COMMENT '商品名称',
    goods_image VARCHAR(255) COMMENT '商品图片',
    sku_id INT COMMENT 'SKU ID',
    price DECIMAL(10,2) DEFAULT 0 COMMENT '单价',
    quantity INT DEFAULT 1 COMMENT '数量',
    subtotal DECIMAL(10,2) DEFAULT 0 COMMENT '小计'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 插入测试数据
INSERT INTO tbl_order_info (order_no, user_id, user_name, user_phone, receiver_name, receiver_phone, receiver_address, total_amount, discount_amount, actual_amount, freight, pay_status, order_status, remark, create_time) VALUES
('202609150001', 1, 'sakura', '12345678901', '张三', '13800138001', '北京市朝阳区xxx路1号', 195.00, 9.75, 185.25, 0.00, 1, 1, '请尽快发货', '2026-09-15 10:30:00'),
('202609150002', 2, 'lisa', '12345678902', '李四', '13800138002', '上海市浦东新区xxx路2号', 396.00, 0.00, 396.00, 10.00, 1, 2, '', '2026-09-14 15:20:00'),
('202609150003', 1, 'sakura', '12345678901', '张三', '13800138001', '北京市朝阳区xxx路1号', 128.00, 0.00, 128.00, 0.00, 0, 0, '', '2026-09-15 09:15:00'),
('202609150004', 3, 'tom', '12345678903', '王五', '13800138003', '广州市天河区xxx路3号', 256.00, 20.00, 236.00, 8.00, 1, 3, '已签收', '2026-09-10 14:00:00'),
('202609150005', 2, 'lisa', '12345678902', '李四', '13800138002', '上海市浦东新区xxx路2号', 88.00, 0.00, 88.00, 0.00, 0, 4, '用户取消', '2026-09-13 11:45:00');

INSERT INTO tbl_order_item (order_id, order_no, goods_id, goods_name, goods_image, sku_id, price, quantity, subtotal) VALUES
(1, '202609150001', 1, 'Unofficial系列休闲直筒裤', '', 101, 195.00, 1, 195.00),
(2, '202609150002', 2, '【硬核守护】12星座守护项链 s925纯银 白羊座', '', 102, 168.00, 2, 336.00),
(2, '202609150002', 3, '纯棉T恤', '', 103, 60.00, 1, 60.00),
(3, '202609150003', 4, '蓝牙耳机', '', 104, 128.00, 1, 128.00),
(4, '202609150004', 5, '机械键盘', '', 105, 256.00, 1, 256.00),
(5, '202609150005', 6, '鼠标垫', '', 106, 88.00, 1, 88.00);
