-- 【广告设置模块】建表 SQL
-- 库: project20206

USE project20206;

CREATE TABLE IF NOT EXISTS tbl_advert (
    id INT AUTO_INCREMENT PRIMARY KEY,
    advert_name VARCHAR(100) NOT NULL COMMENT '广告名称',
    advert_type TINYINT DEFAULT 1 COMMENT '广告形式：1=图片，2=视频，3=GIF',
    position VARCHAR(50) COMMENT '投放位置：首页轮播/分类页/商品详情页',
    image_url VARCHAR(255) COMMENT '广告图片',
    link_url VARCHAR(255) COMMENT '跳转链接',
    billing_type TINYINT DEFAULT 1 COMMENT '计费方式：1=CPM，2=CPD',
    price DECIMAL(10,2) DEFAULT 0 COMMENT '单价',
    total_views INT DEFAULT 0 COMMENT '售卖量（总展现量）',
    current_views INT DEFAULT 0 COMMENT '已展现量',
    start_time VARCHAR(50) COMMENT '投放开始时间',
    end_time VARCHAR(50) COMMENT '投放结束时间',
    weight INT DEFAULT 1 COMMENT '投放权重',
    is_first TINYINT DEFAULT 0 COMMENT '是否首刷：0=否，1=是',
    status TINYINT DEFAULT 1 COMMENT '状态：0=已下线，1=投放中',
    create_time VARCHAR(50),
    update_time VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 测试数据
INSERT INTO tbl_advert (advert_name, advert_type, position, image_url, link_url, billing_type, price, total_views, current_views, start_time, end_time, weight, is_first, status, create_time) VALUES
('双11大促横幅', 1, '首页轮播', 'banner_1111.jpg', 'https://example.com/1111', 1, 50.00, 10000, 3500, '2026-10-20', '2026-11-11', 5, 1, 1, '2026-09-01 10:00:00'),
('新品上市推荐', 1, '分类页', 'new_product.jpg', 'https://example.com/new', 2, 200.00, 0, 1200, '2026-09-01', '2026-09-30', 3, 0, 1, '2026-09-01 11:00:00'),
('会员专享活动', 3, '首页轮播', 'vip_gift.gif', 'https://example.com/vip', 1, 30.00, 5000, 5000, '2026-08-01', '2026-08-31', 2, 0, 0, '2026-08-01 09:00:00'),
('品牌宣传视频', 2, '商品详情页', 'brand_video.mp4', 'https://example.com/brand', 2, 500.00, 0, 800, '2026-09-15', '2026-10-15', 1, 0, 1, '2026-09-10 14:00:00'),
('限时秒杀', 1, '首页轮播', 'flash_sale.jpg', 'https://example.com/flash', 1, 80.00, 8000, 2000, '2026-09-20', '2026-09-25', 4, 1, 1, '2026-09-12 16:00:00');
