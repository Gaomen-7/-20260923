-- ============================================================
-- 常规商品种子数据：8 个 SPU × 4 SKU（颜色2 × 存储2）
-- 数据库：project20206
-- 说明：图片已落盘 D:/haida/space/upload/goods/p01~p08.jpg 与 album/p01~p08.jpg
-- 用法：mysql -uroot project20206 < seed_real_products.sql
-- ============================================================
USE project20206;
START TRANSACTION;

-- ============ SPU 1：华为 Mate 70 Pro ============
INSERT INTO tbl_goods_info(goods_sn,goods_name,goods_description,category_id,brand_id,main_image,weight,price,stock,sale_count,publish_status,is_recommend,create_time,update_time)
VALUES('SP000103','华为 Mate 70 Pro 16GB+512GB','麒麟9020，高亮钛玄武架构，红枫原色影像',11,1,'p01.jpg','221g',5499.00,390,86,1,1,NOW(),NOW());
SET @g1=LAST_INSERT_ID();
INSERT INTO tbl_sku_info(goods_id,sku_name,sku_desc,category_id,brand_id,sku_title,sku_subtitle,price,sale_count,stock) VALUES
(@g1,'Mate 70 Pro 曜石黑 128GB 标准版','玄武架构 鸿蒙AI影像',11,1,'华为 Mate 70 Pro','曜石黑 128GB',5499.00,20,120),
(@g1,'Mate 70 Pro 曜石黑 256GB 标准版','玄武架构 鸿蒙AI影像',11,1,'华为 Mate 70 Pro','曜石黑 256GB',5999.00,15,90),
(@g1,'Mate 70 Pro 月光白 128GB 标准版','玄武架构 鸿蒙AI影像',11,1,'华为 Mate 70 Pro','月光白 128GB',5499.00,10,100),
(@g1,'Mate 70 Pro 月光白 256GB 标准版','玄武架构 鸿蒙AI影像',11,1,'华为 Mate 70 Pro','月光白 256GB',5999.00,8,80);
SET @s1=LAST_INSERT_ID();
INSERT INTO tbl_sku_sale_attr_value(sku_id,attr_id,attr_name,attr_value,attr_sort) VALUES
(@s1,25,'机身颜色','曜石黑',1),(@s1,26,'存储容量','128GB',4),(@s1,27,'版本','标准版',5),
(@s1+1,25,'机身颜色','曜石黑',1),(@s1+1,26,'存储容量','256GB',4),(@s1+1,27,'版本','标准版',5),
(@s1+2,25,'机身颜色','月光白',1),(@s1+2,26,'存储容量','128GB',4),(@s1+2,27,'版本','标准版',5),
(@s1+3,25,'机身颜色','月光白',1),(@s1+3,26,'存储容量','256GB',4),(@s1+3,27,'版本','标准版',5);
INSERT INTO tbl_sku_album(goods_id,sku_id,images,default_image,create_time,update_time) VALUES
(@g1,@s1,'p01.jpg','p01.jpg',NOW(),NOW()),
(@g1,@s1+1,'p01.jpg','p01.jpg',NOW(),NOW()),
(@g1,@s1+2,'p01.jpg','p01.jpg',NOW(),NOW()),
(@g1,@s1+3,'p01.jpg','p01.jpg',NOW(),NOW());

-- ============ SPU 2：小米 15 Ultra ============
INSERT INTO tbl_goods_info(goods_sn,goods_name,goods_description,category_id,brand_id,main_image,weight,price,stock,sale_count,publish_status,is_recommend,create_time,update_time)
VALUES('SP000104','小米 15 Ultra 16GB+512GB','骁龙8至尊版，徕卡光学，2亿像素超长焦',11,4,'p02.jpg','226g',6499.00,260,64,1,1,NOW(),NOW());
SET @g2=LAST_INSERT_ID();
INSERT INTO tbl_sku_info(goods_id,sku_name,sku_desc,category_id,brand_id,sku_title,sku_subtitle,price,sale_count,stock) VALUES
(@g2,'小米 15 Ultra 曜石黑 128GB 标准版','徕卡四摄 影像旗舰',11,4,'小米 15 Ultra','曜石黑 128GB',6499.00,18,80),
(@g2,'小米 15 Ultra 曜石黑 256GB 标准版','徕卡四摄 影像旗舰',11,4,'小米 15 Ultra','曜石黑 256GB',6999.00,14,70),
(@g2,'小米 15 Ultra 月光白 128GB 标准版','徕卡四摄 影像旗舰',11,4,'小米 15 Ultra','月光白 128GB',6499.00,10,60),
(@g2,'小米 15 Ultra 月光白 256GB 标准版','徕卡四摄 影像旗舰',11,4,'小米 15 Ultra','月光白 256GB',6999.00,8,50);
SET @s2=LAST_INSERT_ID();
INSERT INTO tbl_sku_sale_attr_value(sku_id,attr_id,attr_name,attr_value,attr_sort) VALUES
(@s2,25,'机身颜色','曜石黑',1),(@s2,26,'存储容量','128GB',4),(@s2,27,'版本','标准版',5),
(@s2+1,25,'机身颜色','曜石黑',1),(@s2+1,26,'存储容量','256GB',4),(@s2+1,27,'版本','标准版',5),
(@s2+2,25,'机身颜色','月光白',1),(@s2+2,26,'存储容量','128GB',4),(@s2+2,27,'版本','标准版',5),
(@s2+3,25,'机身颜色','月光白',1),(@s2+3,26,'存储容量','256GB',4),(@s2+3,27,'版本','标准版',5);
INSERT INTO tbl_sku_album(goods_id,sku_id,images,default_image,create_time,update_time) VALUES
(@g2,@s2,'p02.jpg','p02.jpg',NOW(),NOW()),
(@g2,@s2+1,'p02.jpg','p02.jpg',NOW(),NOW()),
(@g2,@s2+2,'p02.jpg','p02.jpg',NOW(),NOW()),
(@g2,@s2+3,'p02.jpg','p02.jpg',NOW(),NOW());

-- ============ SPU 3：Redmi K80 ============
INSERT INTO tbl_goods_info(goods_sn,goods_name,goods_description,category_id,brand_id,main_image,weight,price,stock,sale_count,publish_status,is_recommend,create_time,update_time)
VALUES('SP000105','Redmi K80 16GB+512GB','第三代骁龙8，2K新国屏，6550mAh大电池',11,5,'p03.jpg','210g',2199.00,650,152,1,1,NOW(),NOW());
SET @g3=LAST_INSERT_ID();
INSERT INTO tbl_sku_info(goods_id,sku_name,sku_desc,category_id,brand_id,sku_title,sku_subtitle,price,sale_count,stock) VALUES
(@g3,'Redmi K80 曜石黑 128GB 标准版','骁龙8 Gen3 IP68',11,5,'Redmi K80','曜石黑 128GB',2199.00,45,200),
(@g3,'Redmi K80 曜石黑 256GB 标准版','骁龙8 Gen3 IP68',11,5,'Redmi K80','曜石黑 256GB',2699.00,38,150),
(@g3,'Redmi K80 月光白 128GB 标准版','骁龙8 Gen3 IP68',11,5,'Redmi K80','月光白 128GB',2199.00,35,180),
(@g3,'Redmi K80 月光白 256GB 标准版','骁龙8 Gen3 IP68',11,5,'Redmi K80','月光白 256GB',2699.00,28,120);
SET @s3=LAST_INSERT_ID();
INSERT INTO tbl_sku_sale_attr_value(sku_id,attr_id,attr_name,attr_value,attr_sort) VALUES
(@s3,25,'机身颜色','曜石黑',1),(@s3,26,'存储容量','128GB',4),(@s3,27,'版本','标准版',5),
(@s3+1,25,'机身颜色','曜石黑',1),(@s3+1,26,'存储容量','256GB',4),(@s3+1,27,'版本','标准版',5),
(@s3+2,25,'机身颜色','月光白',1),(@s3+2,26,'存储容量','128GB',4),(@s3+2,27,'版本','标准版',5),
(@s3+3,25,'机身颜色','月光白',1),(@s3+3,26,'存储容量','256GB',4),(@s3+3,27,'版本','标准版',5);
INSERT INTO tbl_sku_album(goods_id,sku_id,images,default_image,create_time,update_time) VALUES
(@g3,@s3,'p03.jpg','p03.jpg',NOW(),NOW()),
(@g3,@s3+1,'p03.jpg','p03.jpg',NOW(),NOW()),
(@g3,@s3+2,'p03.jpg','p03.jpg',NOW(),NOW()),
(@g3,@s3+3,'p03.jpg','p03.jpg',NOW(),NOW());

-- ============ SPU 4：三星 Galaxy S25 ============
INSERT INTO tbl_goods_info(goods_sn,goods_name,goods_description,category_id,brand_id,main_image,weight,price,stock,sale_count,publish_status,is_recommend,create_time,update_time)
VALUES('SP000106','三星 Galaxy S25 12GB+256GB','骁龙8至尊版，Dynamic AMOLED 2X，Galaxy AI',11,7,'p04.jpg','162g',5299.00,270,45,1,0,NOW(),NOW());
SET @g4=LAST_INSERT_ID();
INSERT INTO tbl_sku_info(goods_id,sku_name,sku_desc,category_id,brand_id,sku_title,sku_subtitle,price,sale_count,stock) VALUES
(@g4,'Galaxy S25 曜石黑 128GB 标准版','超轻薄 AI手机',11,7,'三星 Galaxy S25','曜石黑 128GB',5299.00,15,90),
(@g4,'Galaxy S25 曜石黑 256GB 标准版','超轻薄 AI手机',11,7,'三星 Galaxy S25','曜石黑 256GB',5799.00,12,60),
(@g4,'Galaxy S25 冰羽蓝 128GB 标准版','超轻薄 AI手机',11,7,'三星 Galaxy S25','冰羽蓝 128GB',5299.00,10,70),
(@g4,'Galaxy S25 冰羽蓝 256GB 标准版','超轻薄 AI手机',11,7,'三星 Galaxy S25','冰羽蓝 256GB',5799.00,8,50);
SET @s4=LAST_INSERT_ID();
INSERT INTO tbl_sku_sale_attr_value(sku_id,attr_id,attr_name,attr_value,attr_sort) VALUES
(@s4,25,'机身颜色','曜石黑',1),(@s4,26,'存储容量','128GB',4),(@s4,27,'版本','标准版',5),
(@s4+1,25,'机身颜色','曜石黑',1),(@s4+1,26,'存储容量','256GB',4),(@s4+1,27,'版本','标准版',5),
(@s4+2,25,'机身颜色','冰羽蓝',1),(@s4+2,26,'存储容量','128GB',4),(@s4+2,27,'版本','标准版',5),
(@s4+3,25,'机身颜色','冰羽蓝',1),(@s4+3,26,'存储容量','256GB',4),(@s4+3,27,'版本','标准版',5);
INSERT INTO tbl_sku_album(goods_id,sku_id,images,default_image,create_time,update_time) VALUES
(@g4,@s4,'p04.jpg','p04.jpg',NOW(),NOW()),
(@g4,@s4+1,'p04.jpg','p04.jpg',NOW(),NOW()),
(@g4,@s4+2,'p04.jpg','p04.jpg',NOW(),NOW()),
(@g4,@s4+3,'p04.jpg','p04.jpg',NOW(),NOW());

-- ============ SPU 5：华为 Pura 80 Ultra ============
INSERT INTO tbl_goods_info(goods_sn,goods_name,goods_description,category_id,brand_id,main_image,weight,price,stock,sale_count,publish_status,is_recommend,create_time,update_time)
VALUES('SP000107','华为 Pura 80 Ultra 16GB+512GB','一英寸主摄，双潜望长焦，麒麟9020',11,1,'p05.jpg','233g',8499.00,180,38,1,1,NOW(),NOW());
SET @g5=LAST_INSERT_ID();
INSERT INTO tbl_sku_info(goods_id,sku_name,sku_desc,category_id,brand_id,sku_title,sku_subtitle,price,sale_count,stock) VALUES
(@g5,'Pura 80 Ultra 鎏光黑 128GB 标准版','超微距双长焦',11,1,'华为 Pura 80 Ultra','鎏光黑 128GB',8499.00,12,60),
(@g5,'Pura 80 Ultra 鎏光黑 256GB 标准版','超微距双长焦',11,1,'华为 Pura 80 Ultra','鎏光黑 256GB',8999.00,10,50),
(@g5,'Pura 80 Ultra 鎏光金 128GB 标准版','超微距双长焦',11,1,'华为 Pura 80 Ultra','鎏光金 128GB',8499.00,9,40),
(@g5,'Pura 80 Ultra 鎏光金 256GB 标准版','超微距双长焦',11,1,'华为 Pura 80 Ultra','鎏光金 256GB',8999.00,7,30);
SET @s5=LAST_INSERT_ID();
INSERT INTO tbl_sku_sale_attr_value(sku_id,attr_id,attr_name,attr_value,attr_sort) VALUES
(@s5,25,'机身颜色','鎏光黑',1),(@s5,26,'存储容量','128GB',4),(@s5,27,'版本','标准版',5),
(@s5+1,25,'机身颜色','鎏光黑',1),(@s5+1,26,'存储容量','256GB',4),(@s5+1,27,'版本','标准版',5),
(@s5+2,25,'机身颜色','鎏光金',1),(@s5+2,26,'存储容量','128GB',4),(@s5+2,27,'版本','标准版',5),
(@s5+3,25,'机身颜色','鎏光金',1),(@s5+3,26,'存储容量','256GB',4),(@s5+3,27,'版本','标准版',5);
INSERT INTO tbl_sku_album(goods_id,sku_id,images,default_image,create_time,update_time) VALUES
(@g5,@s5,'p05.jpg','p05.jpg',NOW(),NOW()),
(@g5,@s5+1,'p05.jpg','p05.jpg',NOW(),NOW()),
(@g5,@s5+2,'p05.jpg','p05.jpg',NOW(),NOW()),
(@g5,@s5+3,'p05.jpg','p05.jpg',NOW(),NOW());

-- ============ SPU 6：中兴 Axon 60 Ultra ============
INSERT INTO tbl_goods_info(goods_sn,goods_name,goods_description,category_id,brand_id,main_image,weight,price,stock,sale_count,publish_status,is_recommend,create_time,update_time)
VALUES('SP000108','中兴 Axon 60 Ultra 12GB+512GB','骁龙8 Gen2，6000mAh，双卫星通信',11,2,'p06.jpg','204g',3999.00,220,22,1,0,NOW(),NOW());
SET @g6=LAST_INSERT_ID();
INSERT INTO tbl_sku_info(goods_id,sku_name,sku_desc,category_id,brand_id,sku_title,sku_subtitle,price,sale_count,stock) VALUES
(@g6,'Axon 60 Ultra 曜石黑 128GB 标准版','原石纹理机身',11,2,'中兴 Axon 60 Ultra','曜石黑 128GB',3999.00,8,70),
(@g6,'Axon 60 Ultra 曜石黑 256GB 标准版','原石纹理机身',11,2,'中兴 Axon 60 Ultra','曜石黑 256GB',4499.00,6,50),
(@g6,'Axon 60 Ultra 月光白 128GB 标准版','原石纹理机身',11,2,'中兴 Axon 60 Ultra','月光白 128GB',3999.00,5,60),
(@g6,'Axon 60 Ultra 月光白 256GB 标准版','原石纹理机身',11,2,'中兴 Axon 60 Ultra','月光白 256GB',4499.00,3,40);
SET @s6=LAST_INSERT_ID();
INSERT INTO tbl_sku_sale_attr_value(sku_id,attr_id,attr_name,attr_value,attr_sort) VALUES
(@s6,25,'机身颜色','曜石黑',1),(@s6,26,'存储容量','128GB',4),(@s6,27,'版本','标准版',5),
(@s6+1,25,'机身颜色','曜石黑',1),(@s6+1,26,'存储容量','256GB',4),(@s6+1,27,'版本','标准版',5),
(@s6+2,25,'机身颜色','月光白',1),(@s6+2,26,'存储容量','128GB',4),(@s6+2,27,'版本','标准版',5),
(@s6+3,25,'机身颜色','月光白',1),(@s6+3,26,'存储容量','256GB',4),(@s6+3,27,'版本','标准版',5);
INSERT INTO tbl_sku_album(goods_id,sku_id,images,default_image,create_time,update_time) VALUES
(@g6,@s6,'p06.jpg','p06.jpg',NOW(),NOW()),
(@g6,@s6+1,'p06.jpg','p06.jpg',NOW(),NOW()),
(@g6,@s6+2,'p06.jpg','p06.jpg',NOW(),NOW()),
(@g6,@s6+3,'p06.jpg','p06.jpg',NOW(),NOW());

-- ============ SPU 7：Redmi Note 14 Pro+ ============
INSERT INTO tbl_goods_info(goods_sn,goods_name,goods_description,category_id,brand_id,main_image,weight,price,stock,sale_count,publish_status,is_recommend,create_time,update_time)
VALUES('SP000109','Redmi Note 14 Pro+ 12GB+256GB','第三代骁龙7s，2亿像素，90W快充',11,5,'p07.jpg','198g',1699.00,800,210,1,1,NOW(),NOW());
SET @g7=LAST_INSERT_ID();
INSERT INTO tbl_sku_info(goods_id,sku_name,sku_desc,category_id,brand_id,sku_title,sku_subtitle,price,sale_count,stock) VALUES
(@g7,'Note 14 Pro+ 镜瓷白 128GB 标准版','2亿像素主摄 6200mAh',11,5,'Redmi Note 14 Pro+','镜瓷白 128GB',1699.00,60,250),
(@g7,'Note 14 Pro+ 镜瓷白 256GB 标准版','2亿像素主摄 6200mAh',11,5,'Redmi Note 14 Pro+','镜瓷白 256GB',2199.00,50,180),
(@g7,'Note 14 Pro+ 曜石黑 128GB 标准版','2亿像素主摄 6200mAh',11,5,'Redmi Note 14 Pro+','曜石黑 128GB',1699.00,45,220),
(@g7,'Note 14 Pro+ 曜石黑 256GB 标准版','2亿像素主摄 6200mAh',11,5,'Redmi Note 14 Pro+','曜石黑 256GB',2199.00,40,150);
SET @s7=LAST_INSERT_ID();
INSERT INTO tbl_sku_sale_attr_value(sku_id,attr_id,attr_name,attr_value,attr_sort) VALUES
(@s7,25,'机身颜色','镜瓷白',1),(@s7,26,'存储容量','128GB',4),(@s7,27,'版本','标准版',5),
(@s7+1,25,'机身颜色','镜瓷白',1),(@s7+1,26,'存储容量','256GB',4),(@s7+1,27,'版本','标准版',5),
(@s7+2,25,'机身颜色','曜石黑',1),(@s7+2,26,'存储容量','128GB',4),(@s7+2,27,'版本','标准版',5),
(@s7+3,25,'机身颜色','曜石黑',1),(@s7+3,26,'存储容量','256GB',4),(@s7+3,27,'版本','标准版',5);
INSERT INTO tbl_sku_album(goods_id,sku_id,images,default_image,create_time,update_time) VALUES
(@g7,@s7,'p07.jpg','p07.jpg',NOW(),NOW()),
(@g7,@s7+1,'p07.jpg','p07.jpg',NOW(),NOW()),
(@g7,@s7+2,'p07.jpg','p07.jpg',NOW(),NOW()),
(@g7,@s7+3,'p07.jpg','p07.jpg',NOW(),NOW());

-- ============ SPU 8：moto edge 50 Pro ============
INSERT INTO tbl_goods_info(goods_sn,goods_name,goods_description,category_id,brand_id,main_image,weight,price,stock,sale_count,publish_status,is_recommend,create_time,update_time)
VALUES('SP000110','moto edge 50 Pro 12GB+256GB','骁龙7 Gen3，1.5K 144Hz pOLED，125W快充',11,3,'p08.jpg','186g',3199.00,330,33,1,0,NOW(),NOW());
SET @g8=LAST_INSERT_ID();
INSERT INTO tbl_sku_info(goods_id,sku_name,sku_desc,category_id,brand_id,sku_title,sku_subtitle,price,sale_count,stock) VALUES
(@g8,'edge 50 Pro 月光白 128GB 标准版','真皮背板 AI影像',11,3,'moto edge 50 Pro','月光白 128GB',3199.00,12,100),
(@g8,'edge 50 Pro 月光白 256GB 标准版','真皮背板 AI影像',11,3,'moto edge 50 Pro','月光白 256GB',3699.00,9,80),
(@g8,'edge 50 Pro 月夜黑 128GB 标准版','真皮背板 AI影像',11,3,'moto edge 50 Pro','月夜黑 128GB',3199.00,7,90),
(@g8,'edge 50 Pro 月夜黑 256GB 标准版','真皮背板 AI影像',11,3,'moto edge 50 Pro','月夜黑 256GB',3699.00,5,60);
SET @s8=LAST_INSERT_ID();
INSERT INTO tbl_sku_sale_attr_value(sku_id,attr_id,attr_name,attr_value,attr_sort) VALUES
(@s8,25,'机身颜色','月光白',1),(@s8,26,'存储容量','128GB',4),(@s8,27,'版本','标准版',5),
(@s8+1,25,'机身颜色','月光白',1),(@s8+1,26,'存储容量','256GB',4),(@s8+1,27,'版本','标准版',5),
(@s8+2,25,'机身颜色','月夜黑',1),(@s8+2,26,'存储容量','128GB',4),(@s8+2,27,'版本','标准版',5),
(@s8+3,25,'机身颜色','月夜黑',1),(@s8+3,26,'存储容量','256GB',4),(@s8+3,27,'版本','标准版',5);
INSERT INTO tbl_sku_album(goods_id,sku_id,images,default_image,create_time,update_time) VALUES
(@g8,@s8,'p08.jpg','p08.jpg',NOW(),NOW()),
(@g8,@s8+1,'p08.jpg','p08.jpg',NOW(),NOW()),
(@g8,@s8+2,'p08.jpg','p08.jpg',NOW(),NOW()),
(@g8,@s8+3,'p08.jpg','p08.jpg',NOW(),NOW());

COMMIT;

-- ============ 验证查询 ============
SELECT id,goods_name,brand_id,price,stock,sale_count,publish_status,is_recommend,main_image FROM tbl_goods_info WHERE id>=3;
SELECT COUNT(*) AS sku_total FROM tbl_sku_info WHERE goods_id>=3;
SELECT COUNT(*) AS saleattr_total FROM tbl_sku_sale_attr_value WHERE sku_id>=6;
SELECT COUNT(*) AS album_total FROM tbl_sku_album WHERE goods_id>=3;
