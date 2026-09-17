-- ============================================================
-- 第二阶段数据分析 - MySQL 结果宽表建表脚本
-- 目标库：project20206
-- 表名与 Hive ADS 层一致，类型映射：STRING→VARCHAR(255)，BIGINT→INT，DECIMAL 保持精度
-- 引擎 InnoDB，字符集 utf8mb4
-- ============================================================

-- 1. 用户行为分析结果表
CREATE TABLE IF NOT EXISTS ads_user_behavior (
  id               INT           NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  stat_date        VARCHAR(255)  DEFAULT NULL COMMENT '统计日期',
  dimension_type   VARCHAR(255)  DEFAULT NULL COMMENT '维度类型(如 date/city/device/source/keyword)',
  dimension_value  VARCHAR(255)  DEFAULT NULL COMMENT '维度值',
  pv               INT           DEFAULT NULL COMMENT '页面访问量',
  uv               INT           DEFAULT NULL COMMENT '独立访客数',
  session_count    INT           DEFAULT NULL COMMENT '会话数',
  conversion_count INT           DEFAULT NULL COMMENT '转化行为数(支付)',
  conversion_rate  DECIMAL(5,4)  DEFAULT NULL COMMENT '转化率',
  bounce_rate      DECIMAL(5,4)  DEFAULT NULL COMMENT '跳出率',
  avg_duration     DECIMAL(10,2) DEFAULT NULL COMMENT '平均停留时长(秒)',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户行为分析结果表';

-- 2. 订单分析结果表
CREATE TABLE IF NOT EXISTS ads_order_analysis (
  id                  INT           NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  stat_date           VARCHAR(255)  DEFAULT NULL COMMENT '统计日期',
  dimension_type      VARCHAR(255)  DEFAULT NULL COMMENT '维度类型(如 date/category/source/pay_method)',
  dimension_value     VARCHAR(255)  DEFAULT NULL COMMENT '维度值',
  order_count         INT           DEFAULT NULL COMMENT '订单数',
  paid_order_count    INT           DEFAULT NULL COMMENT '支付订单数',
  total_amount        DECIMAL(12,2) DEFAULT NULL COMMENT '总金额',
  avg_order_value     DECIMAL(10,2) DEFAULT NULL COMMENT '客单价',
  pay_conversion_rate DECIMAL(5,4)  DEFAULT NULL COMMENT '支付转化率',
  cancel_count        INT           DEFAULT NULL COMMENT '取消订单数',
  return_count        INT           DEFAULT NULL COMMENT '退货订单数',
  return_rate         DECIMAL(5,4)  DEFAULT NULL COMMENT '退货率',
  return_amount       DECIMAL(12,2) DEFAULT NULL COMMENT '退货金额',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单分析结果表';

-- 3. 评价分析结果表
CREATE TABLE IF NOT EXISTS ads_review_analysis (
  id             INT           NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  stat_date      VARCHAR(255)  DEFAULT NULL COMMENT '统计日期',
  dimension_type VARCHAR(255)  DEFAULT NULL COMMENT '维度类型(如 date/product/rating)',
  dimension_value VARCHAR(255) DEFAULT NULL COMMENT '维度值',
  total_reviews  INT           DEFAULT NULL COMMENT '评价总数',
  good_reviews   INT           DEFAULT NULL COMMENT '好评数(4-5星)',
  mid_reviews    INT           DEFAULT NULL COMMENT '中评数(3星)',
  bad_reviews    INT           DEFAULT NULL COMMENT '差评数(1-2星)',
  good_rate      DECIMAL(5,4)  DEFAULT NULL COMMENT '好评率',
  avg_rating     DECIMAL(3,2)  DEFAULT NULL COMMENT '平均评分',
  keyword_count  INT           DEFAULT NULL COMMENT '关键词提及数',
  append_count   INT           DEFAULT NULL COMMENT '追评数',
  append_rate    DECIMAL(5,4)  DEFAULT NULL COMMENT '追评率',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='评价分析结果表';

-- 4. 交流分析结果表
CREATE TABLE IF NOT EXISTS ads_chat_analysis (
  id                 INT           NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  stat_date          VARCHAR(255)  DEFAULT NULL COMMENT '统计日期',
  dimension_type     VARCHAR(255)  DEFAULT NULL COMMENT '维度类型(如 date/category/chat_type)',
  dimension_value    VARCHAR(255)  DEFAULT NULL COMMENT '维度值',
  session_count      INT           DEFAULT NULL COMMENT '会话数',
  user_count         INT           DEFAULT NULL COMMENT '用户数',
  avg_duration       DECIMAL(10,2) DEFAULT NULL COMMENT '平均会话时长(秒)',
  avg_first_response DECIMAL(10,2) DEFAULT NULL COMMENT '平均首次响应时长(秒)',
  avg_reply_count    DECIMAL(5,2)  DEFAULT NULL COMMENT '平均回复数',
  resolution_rate    DECIMAL(5,4)  DEFAULT NULL COMMENT '解决率',
  conversion_count   INT           DEFAULT NULL COMMENT '转化会话数',
  conversion_rate    DECIMAL(5,4)  DEFAULT NULL COMMENT '转化率',
  invalid_rate       DECIMAL(5,4)  DEFAULT NULL COMMENT '无响应会话占比',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='交流分析结果表';
