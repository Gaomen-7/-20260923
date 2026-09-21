-- ============================================================
-- 工单03：订单数据清洗与ADS宽表加载
-- 输入：ods_order_data（1万行，19列，逗号分隔）
-- 输出：ads_order_analysis（12列宽表，预计150-300行）
-- 注意：Hive 的 WITH CTE 只对紧随的一条语句有效，
--       故用临时表 tmp_clean_order 承载清洗结果。
-- 成交订单定义：order_status IN ('已支付','已发货','已完成')
-- ============================================================

-- 降低 LocalJobRunner 堆内存压力（容器内 HS2 堆有限，防 OOM）
SET io.sort.mb=32;

-- 0. 清空目标表（避免重复运行数据重复）
TRUNCATE TABLE ads_order_analysis;

DROP TABLE IF EXISTS tmp_clean_order;

-- 1. 统一清洗：去重(order_id+product_id) / 异常过滤(actual_amount>0) / 状态规整
CREATE TABLE tmp_clean_order AS
SELECT
  order_id,
  user_id,
  product_id,
  category_id,
  CASE order_status
    WHEN '待支付' THEN '待支付'
    WHEN '已支付' THEN '已支付'
    WHEN '已发货' THEN '已发货'
    WHEN '已完成' THEN '已完成'
    WHEN '已取消' THEN '已取消'
    WHEN '已退款' THEN '已退款'
    ELSE '其他'
  END AS order_status,
  pay_status,
  CAST(order_amount AS DECIMAL(12,2)) AS order_amount,
  CAST(discount_amount AS DECIMAL(12,2)) AS discount_amount,
  CAST(actual_amount AS DECIMAL(12,2)) AS actual_amount,
  pay_method,
  order_source,
  create_time,
  pay_time,
  ship_time,
  finish_time,
  CAST(is_return AS INT) AS is_return,
  return_status,
  return_reason,
  return_time,
  TO_DATE(create_time) AS stat_date,
  HOUR(create_time) AS create_hour
FROM (
  SELECT *,
         ROW_NUMBER() OVER (PARTITION BY order_id, product_id ORDER BY create_time) AS rn
  FROM ods_order_data
  WHERE order_id IS NOT NULL AND order_id != ''
    AND actual_amount IS NOT NULL
    AND CAST(actual_amount AS DECIMAL(12,2)) > 0
    AND create_time IS NOT NULL AND create_time != ''
    AND create_time RLIKE '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$'
) t
WHERE rn = 1;

-- 1b. 每日成交订单数（退换货率分母，规避 Hive 相关子查询限制）
DROP TABLE IF EXISTS tmp_daily_paid;

CREATE TABLE tmp_daily_paid AS
SELECT
  stat_date,
  COUNT(DISTINCT CASE WHEN order_status IN ('已支付','已发货','已完成') THEN order_id END) AS paid_order_count
FROM tmp_clean_order
GROUP BY stat_date;

-- ============================================================
-- 查询1：成交总览（dimension_type='overview'）
-- ============================================================
INSERT INTO TABLE ads_order_analysis
SELECT
  stat_date,
  'overview' AS dimension_type,
  'all' AS dimension_value,
  COUNT(DISTINCT order_id) AS order_count,
  COUNT(DISTINCT CASE WHEN order_status IN ('已支付','已发货','已完成') THEN order_id END) AS paid_order_count,
  CAST(SUM(CASE WHEN order_status IN ('已支付','已发货','已完成') THEN actual_amount ELSE 0 END) AS DECIMAL(12,2)) AS total_amount,
  CAST(SUM(CASE WHEN order_status IN ('已支付','已发货','已完成') THEN actual_amount ELSE 0 END)
    / NULLIF(COUNT(DISTINCT CASE WHEN order_status IN ('已支付','已发货','已完成') THEN order_id END), 0) AS DECIMAL(10,2)) AS avg_order_value,
  CAST(COUNT(DISTINCT CASE WHEN order_status IN ('已支付','已发货','已完成') THEN order_id END) AS DECIMAL(10,4))
    / NULLIF(COUNT(DISTINCT order_id), 0) AS pay_conversion_rate,
  COUNT(DISTINCT CASE WHEN order_status = '已取消' THEN order_id END) AS cancel_count,
  COUNT(DISTINCT CASE WHEN is_return = 1 THEN order_id END) AS return_count,
  CAST(COUNT(DISTINCT CASE WHEN is_return = 1 THEN order_id END) AS DECIMAL(10,4))
    / NULLIF(COUNT(DISTINCT CASE WHEN order_status IN ('已支付','已发货','已完成') THEN order_id END), 0) AS return_rate,
  CAST(SUM(CASE WHEN is_return = 1 THEN actual_amount ELSE 0 END) AS DECIMAL(12,2)) AS return_amount
FROM tmp_clean_order
GROUP BY stat_date;

-- ============================================================
-- 查询2：销售结构（dimension_type='category'）
-- 仅统计成交订单（已支付/已发货/已完成）
-- ============================================================
INSERT INTO TABLE ads_order_analysis
SELECT
  stat_date,
  'category' AS dimension_type,
  category_id AS dimension_value,
  COUNT(DISTINCT order_id) AS order_count,
  COUNT(DISTINCT order_id) AS paid_order_count,
  CAST(SUM(actual_amount) AS DECIMAL(12,2)) AS total_amount,
  CAST(SUM(actual_amount) / NULLIF(COUNT(DISTINCT order_id), 0) AS DECIMAL(10,2)) AS avg_order_value,
  NULL AS pay_conversion_rate,
  NULL AS cancel_count,
  NULL AS return_count,
  NULL AS return_rate,
  NULL AS return_amount
FROM tmp_clean_order
WHERE order_status IN ('已支付','已发货','已完成')
GROUP BY stat_date, category_id;

-- ============================================================
-- 查询3：渠道分析（dimension_type='source'）
-- ============================================================
INSERT INTO TABLE ads_order_analysis
SELECT
  stat_date,
  'source' AS dimension_type,
  order_source AS dimension_value,
  COUNT(DISTINCT order_id) AS order_count,
  COUNT(DISTINCT CASE WHEN order_status IN ('已支付','已发货','已完成') THEN order_id END) AS paid_order_count,
  CAST(SUM(CASE WHEN order_status IN ('已支付','已发货','已完成') THEN actual_amount ELSE 0 END) AS DECIMAL(12,2)) AS total_amount,
  CAST(SUM(CASE WHEN order_status IN ('已支付','已发货','已完成') THEN actual_amount ELSE 0 END)
    / NULLIF(COUNT(DISTINCT CASE WHEN order_status IN ('已支付','已发货','已完成') THEN order_id END), 0) AS DECIMAL(10,2)) AS avg_order_value,
  CAST(COUNT(DISTINCT CASE WHEN order_status IN ('已支付','已发货','已完成') THEN order_id END) AS DECIMAL(10,4))
    / NULLIF(COUNT(DISTINCT order_id), 0) AS pay_conversion_rate,
  NULL AS cancel_count,
  NULL AS return_count,
  NULL AS return_rate,
  NULL AS return_amount
FROM tmp_clean_order
GROUP BY stat_date, order_source;

-- ============================================================
-- 查询4：支付方式（dimension_type='pay_method'）
-- 仅统计成交订单
-- ============================================================
INSERT INTO TABLE ads_order_analysis
SELECT
  stat_date,
  'pay_method' AS dimension_type,
  pay_method AS dimension_value,
  COUNT(DISTINCT order_id) AS order_count,
  COUNT(DISTINCT order_id) AS paid_order_count,
  CAST(SUM(actual_amount) AS DECIMAL(12,2)) AS total_amount,
  CAST(SUM(actual_amount) / NULLIF(COUNT(DISTINCT order_id), 0) AS DECIMAL(10,2)) AS avg_order_value,
  NULL AS pay_conversion_rate,
  NULL AS cancel_count,
  NULL AS return_count,
  NULL AS return_rate,
  NULL AS return_amount
FROM tmp_clean_order
WHERE order_status IN ('已支付','已发货','已完成')
  AND pay_method IS NOT NULL AND pay_method != ''
GROUP BY stat_date, pay_method;

-- ============================================================
-- 查询5：订单流失（dimension_type='cancel'）
-- 流失金额用 order_amount（下单金额，未实付）
-- ============================================================
INSERT INTO TABLE ads_order_analysis
SELECT
  stat_date,
  'cancel' AS dimension_type,
  'cancelled' AS dimension_value,
  COUNT(DISTINCT order_id) AS order_count,
  NULL AS paid_order_count,
  CAST(SUM(order_amount) AS DECIMAL(12,2)) AS total_amount,
  NULL AS avg_order_value,
  NULL AS pay_conversion_rate,
  COUNT(DISTINCT order_id) AS cancel_count,
  NULL AS return_count,
  NULL AS return_rate,
  NULL AS return_amount
FROM tmp_clean_order
WHERE order_status = '已取消'
GROUP BY stat_date;

-- ============================================================
-- 查询6：退换货分析（dimension_type='return_reason'）
-- 退换货率 = 该原因退换货订单数 / 当日成交订单数（JOIN tmp_daily_paid）
-- ============================================================
INSERT INTO TABLE ads_order_analysis
SELECT
  r.stat_date,
  'return_reason' AS dimension_type,
  r.reason AS dimension_value,
  r.return_orders AS order_count,
  NULL AS paid_order_count,
  CAST(r.return_amt AS DECIMAL(12,2)) AS total_amount,
  NULL AS avg_order_value,
  NULL AS pay_conversion_rate,
  NULL AS cancel_count,
  r.return_orders AS return_count,
  CAST(CAST(r.return_orders AS DECIMAL(10,4)) / NULLIF(d.paid_order_count, 0) AS DECIMAL(5,4)) AS return_rate,
  CAST(r.return_amt AS DECIMAL(12,2)) AS return_amount
FROM (
  SELECT
    stat_date,
    -- COALESCE/NULLIF 在 MR 下触发 LazyString→Text 异常(HIVE-16205)，用 CASE WHEN 替代
    CASE WHEN return_reason IS NULL OR return_reason = '' THEN '未填写'
         ELSE return_reason END AS reason,
    COUNT(DISTINCT order_id) AS return_orders,
    SUM(actual_amount) AS return_amt
  FROM tmp_clean_order
  WHERE is_return = 1
  GROUP BY stat_date,
    CASE WHEN return_reason IS NULL OR return_reason = '' THEN '未填写'
         ELSE return_reason END
) r
LEFT JOIN tmp_daily_paid d ON r.stat_date = d.stat_date;

-- ============================================================
-- 查询7：客单价分布（dimension_type='price_range'）
-- 分桶：0-50/50-100/100-200/200-500/500+，仅统计成交订单
-- ============================================================
INSERT INTO TABLE ads_order_analysis
SELECT
  stat_date,
  'price_range' AS dimension_type,
  CASE
    WHEN actual_amount < 50 THEN '0-50'
    WHEN actual_amount < 100 THEN '50-100'
    WHEN actual_amount < 200 THEN '100-200'
    WHEN actual_amount < 500 THEN '200-500'
    ELSE '500+'
  END AS dimension_value,
  COUNT(DISTINCT order_id) AS order_count,
  COUNT(DISTINCT order_id) AS paid_order_count,
  CAST(SUM(actual_amount) AS DECIMAL(12,2)) AS total_amount,
  CAST(SUM(actual_amount) / NULLIF(COUNT(DISTINCT order_id), 0) AS DECIMAL(10,2)) AS avg_order_value,
  NULL AS pay_conversion_rate,
  NULL AS cancel_count,
  NULL AS return_count,
  NULL AS return_rate,
  NULL AS return_amount
FROM tmp_clean_order
WHERE order_status IN ('已支付','已发货','已完成')
GROUP BY stat_date,
  CASE
    WHEN actual_amount < 50 THEN '0-50'
    WHEN actual_amount < 100 THEN '50-100'
    WHEN actual_amount < 200 THEN '100-200'
    WHEN actual_amount < 500 THEN '200-500'
    ELSE '500+'
  END;

-- ============================================================
-- 查询8：时段销售（dimension_type='hour'）
-- ============================================================
INSERT INTO TABLE ads_order_analysis
SELECT
  stat_date,
  'hour' AS dimension_type,
  LPAD(CAST(create_hour AS STRING), 2, '0') AS dimension_value,
  COUNT(DISTINCT order_id) AS order_count,
  COUNT(DISTINCT CASE WHEN order_status IN ('已支付','已发货','已完成') THEN order_id END) AS paid_order_count,
  CAST(SUM(CASE WHEN order_status IN ('已支付','已发货','已完成') THEN actual_amount ELSE 0 END) AS DECIMAL(12,2)) AS total_amount,
  NULL AS avg_order_value,
  NULL AS pay_conversion_rate,
  NULL AS cancel_count,
  NULL AS return_count,
  NULL AS return_rate,
  NULL AS return_amount
FROM tmp_clean_order
GROUP BY stat_date, create_hour;

-- ============================================================
-- 验证
-- ============================================================
SELECT dimension_type, COUNT(*) AS rows_cnt
FROM ads_order_analysis
GROUP BY dimension_type;

SELECT COUNT(*) AS total_rows FROM ads_order_analysis;
