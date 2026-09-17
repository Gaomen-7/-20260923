-- ============================================================
-- 第二阶段数据分析 - Hive 建表脚本
-- 包含：ODS 层 4 张外部表 + ADS 层 4 张宽表
-- 数据源：D:/haida/space/phase2-data/ 下的 4 个生成文件
-- ============================================================

-- ============================================================
-- 一、ODS 层（4 张外部表，全部字段 STRING，TEXTFILE）
-- ============================================================

-- 1. 用户行为日志（Tab 分隔，12 列）
CREATE EXTERNAL TABLE IF NOT EXISTS ods_user_behavior (
  log_id          STRING COMMENT '日志ID',
  user_id         STRING COMMENT '用户ID',
  product_id      STRING COMMENT '商品ID',
  behavior_type   STRING COMMENT '行为类型(浏览/搜索/收藏/加入购物车/购买/支付/退出)',
  source          STRING COMMENT '来源渠道',
  search_keyword  STRING COMMENT '搜索关键词(仅搜索行为)',
  session_id      STRING COMMENT '会话ID',
  device_type     STRING COMMENT '设备类型',
  ip_city         STRING COMMENT 'IP归属城市',
  product_price   STRING COMMENT '商品价格',
  quantity        STRING COMMENT '数量(加购/购买时有效)',
  behavior_time   STRING COMMENT '行为时间 yyyy-MM-dd HH:mm:ss'
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '/warehouse/mall/ods/ods_user_behavior';

-- 2. 订单数据（逗号分隔，19 列）
CREATE EXTERNAL TABLE IF NOT EXISTS ods_order_data (
  order_id         STRING COMMENT '订单ID',
  user_id          STRING COMMENT '用户ID',
  product_id       STRING COMMENT '商品ID',
  category_id      STRING COMMENT '品类ID',
  order_status     STRING COMMENT '订单状态(待支付/已支付/已发货/已完成/已取消/已退款)',
  pay_status       STRING COMMENT '支付状态(未支付/已支付/已退款)',
  order_amount     STRING COMMENT '订单金额',
  discount_amount  STRING COMMENT '优惠金额',
  actual_amount    STRING COMMENT '实付金额',
  pay_method       STRING COMMENT '支付方式',
  order_source     STRING COMMENT '下单渠道',
  create_time      STRING COMMENT '创建时间',
  pay_time         STRING COMMENT '支付时间',
  ship_time        STRING COMMENT '发货时间',
  finish_time      STRING COMMENT '完成时间',
  is_return        STRING COMMENT '是否退货 0/1',
  return_status    STRING COMMENT '退货状态',
  return_reason    STRING COMMENT '退货原因',
  return_time      STRING COMMENT '退货时间'
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/warehouse/mall/ods/ods_order_data';

-- 3. 评价数据（逗号分隔，13 列）
CREATE EXTERNAL TABLE IF NOT EXISTS ods_review_data (
  review_id           STRING COMMENT '评价ID',
  order_id            STRING COMMENT '订单ID',
  user_id             STRING COMMENT '用户ID',
  product_id          STRING COMMENT '商品ID',
  rating              STRING COMMENT '评分 1-5',
  review_content      STRING COMMENT '评价内容',
  review_time         STRING COMMENT '评价时间',
  is_append           STRING COMMENT '是否追评 是/否',
  append_content      STRING COMMENT '追评内容',
  image_count         STRING COMMENT '图片数',
  praise_num          STRING COMMENT '点赞数',
  merchant_reply      STRING COMMENT '商家回复',
  merchant_reply_time STRING COMMENT '商家回复时间'
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/warehouse/mall/ods/ods_review_data';

-- 4. 交流数据（逗号分隔，14 列）
CREATE EXTERNAL TABLE IF NOT EXISTS ods_chat_data (
  chat_id              STRING COMMENT '会话ID',
  user_id              STRING COMMENT '用户ID',
  product_id           STRING COMMENT '商品ID',
  category_id          STRING COMMENT '品类ID',
  session_id           STRING COMMENT '客服会话ID',
  chat_type            STRING COMMENT '咨询类型',
  user_msg_count       STRING COMMENT '用户消息数',
  merchant_reply_count STRING COMMENT '商家回复数',
  first_response_sec   STRING COMMENT '首次响应秒数',
  session_duration_sec STRING COMMENT '会话时长秒数',
  is_converted         STRING COMMENT '是否转化 0/1',
  convert_action       STRING COMMENT '转化动作(加购/下单)',
  chat_start_time      STRING COMMENT '会话开始时间',
  chat_end_time        STRING COMMENT '会话结束时间'
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/warehouse/mall/ods/ods_chat_data';

-- ============================================================
-- 二、ADS 层（4 张分析宽表，TEXTFILE）
-- ============================================================

-- 1. 用户行为分析宽表
CREATE TABLE IF NOT EXISTS ads_user_behavior (
  stat_date        STRING        COMMENT '统计日期',
  dimension_type   STRING        COMMENT '维度类型(如 date/city/device/source/keyword)',
  dimension_value  STRING        COMMENT '维度值',
  pv               BIGINT        COMMENT '页面访问量',
  uv               BIGINT        COMMENT '独立访客数',
  session_count    BIGINT        COMMENT '会话数',
  conversion_count BIGINT        COMMENT '转化行为数(支付)',
  conversion_rate  DECIMAL(5,4)  COMMENT '转化率',
  bounce_rate      DECIMAL(5,4)  COMMENT '跳出率',
  avg_duration     DECIMAL(10,2) COMMENT '平均停留时长(秒)'
)
STORED AS TEXTFILE;

-- 2. 订单分析宽表
CREATE TABLE IF NOT EXISTS ads_order_analysis (
  stat_date           STRING        COMMENT '统计日期',
  dimension_type      STRING        COMMENT '维度类型(如 date/category/source/pay_method)',
  dimension_value     STRING        COMMENT '维度值',
  order_count         BIGINT        COMMENT '订单数',
  paid_order_count    BIGINT        COMMENT '支付订单数',
  total_amount        DECIMAL(12,2) COMMENT '总金额',
  avg_order_value     DECIMAL(10,2) COMMENT '客单价',
  pay_conversion_rate DECIMAL(5,4)  COMMENT '支付转化率',
  cancel_count        BIGINT        COMMENT '取消订单数',
  return_count        BIGINT        COMMENT '退货订单数',
  return_rate         DECIMAL(5,4)  COMMENT '退货率',
  return_amount       DECIMAL(12,2) COMMENT '退货金额'
)
STORED AS TEXTFILE;

-- 3. 评价分析宽表
CREATE TABLE IF NOT EXISTS ads_review_analysis (
  stat_date      STRING       COMMENT '统计日期',
  dimension_type STRING       COMMENT '维度类型(如 date/product/rating)',
  dimension_value STRING      COMMENT '维度值',
  total_reviews  BIGINT       COMMENT '评价总数',
  good_reviews   BIGINT       COMMENT '好评数(4-5星)',
  mid_reviews    BIGINT       COMMENT '中评数(3星)',
  bad_reviews    BIGINT       COMMENT '差评数(1-2星)',
  good_rate      DECIMAL(5,4) COMMENT '好评率',
  avg_rating     DECIMAL(3,2) COMMENT '平均评分',
  keyword_count  BIGINT       COMMENT '关键词提及数',
  append_count   BIGINT       COMMENT '追评数',
  append_rate    DECIMAL(5,4) COMMENT '追评率'
)
STORED AS TEXTFILE;

-- 4. 交流分析宽表
CREATE TABLE IF NOT EXISTS ads_chat_analysis (
  stat_date         STRING        COMMENT '统计日期',
  dimension_type    STRING        COMMENT '维度类型(如 date/category/chat_type)',
  dimension_value   STRING        COMMENT '维度值',
  session_count     BIGINT        COMMENT '会话数',
  user_count        BIGINT        COMMENT '用户数',
  avg_duration      DECIMAL(10,2) COMMENT '平均会话时长(秒)',
  avg_first_response DECIMAL(10,2) COMMENT '平均首次响应时长(秒)',
  avg_reply_count   DECIMAL(5,2)  COMMENT '平均回复数',
  resolution_rate   DECIMAL(5,4)  COMMENT '解决率',
  conversion_count  BIGINT        COMMENT '转化会话数',
  conversion_rate   DECIMAL(5,4)  COMMENT '转化率',
  invalid_rate      DECIMAL(5,4)  COMMENT '无响应会话占比'
)
STORED AS TEXTFILE;
