-- 【工单04】发布商品五张表建表 SQL
-- 库: project20206

USE project20206;

-- 1. 商品主表
CREATE TABLE IF NOT EXISTS tbl_goods_info (
    id INT AUTO_INCREMENT PRIMARY KEY,
    goods_name VARCHAR(255),
    goods_description VARCHAR(500),
    category_id INT,
    brand_id INT,
    main_image VARCHAR(255),
    weight VARCHAR(50),
    publish_status TINYINT DEFAULT 0,
    create_time VARCHAR(50),
    update_time VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 商品规格参数表
CREATE TABLE IF NOT EXISTS tbl_spu_goods_attr (
    id INT AUTO_INCREMENT PRIMARY KEY,
    goods_id INT,
    attr_id INT,
    attr_name VARCHAR(100),
    attr_value VARCHAR(500),
    attr_sort INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. SKU信息表
CREATE TABLE IF NOT EXISTS tbl_sku_info (
    sku_id INT AUTO_INCREMENT PRIMARY KEY,
    goods_id INT,
    sku_name VARCHAR(255),
    sku_desc VARCHAR(500),
    category_id INT,
    brand_id INT,
    sku_title VARCHAR(255),
    sku_subtitle VARCHAR(255),
    price DECIMAL(10,2),
    sale_count INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. SKU图片集表
CREATE TABLE IF NOT EXISTS tbl_sku_album (
    id INT AUTO_INCREMENT PRIMARY KEY,
    goods_id INT,
    sku_id INT,
    images TEXT,
    default_image VARCHAR(255),
    create_time VARCHAR(50),
    update_time VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. SKU销售属性值表
CREATE TABLE IF NOT EXISTS tbl_sku_sale_attr_value (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sku_id INT,
    attr_id INT,
    attr_name VARCHAR(100),
    attr_value VARCHAR(255),
    attr_sort INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
