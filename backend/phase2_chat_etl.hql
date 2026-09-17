-- ============================================================
-- 工单05：用户与商家交流数据清洗与ADS宽表加载
-- 输入：ods_chat_data（2000行，14列，逗号分隔）
-- 输出：ads_chat_analysis（12列宽表）
-- 说明：
--   - 无效过滤：空session_id / 用户0消息 / 时长<5秒秒退 / 单条消息且客服未回复
--   - first_response_sec 异常上限 3600 秒，AVG 中以 CASE WHEN 置 NULL 排除
--   - resolution/conversion/invalid 三个率的分母均为"分组自身会话数"（工单§3.3），
--     宽表12列无"占当日总会话比"字段，故未创建§四中的 tmp_daily_total（无消费方）
--   - 除法统一 NULLIF 防除零，率类 DECIMAL(5,4)、时长类 DECIMAL(10,2)、次数类 DECIMAL(5,2)
-- ============================================================

-- 防OOM（工单03踩坑：HS2与LocalJobRunner共JVM）
SET io.sort.mb=32;

-- 0. 清空目标表
TRUNCATE TABLE ads_chat_analysis;

-- 1. 统一清洗 + 字段规整
DROP TABLE IF EXISTS tmp_clean_chat;

CREATE TEMPORARY TABLE tmp_clean_chat AS
SELECT
  chat_id,
  user_id,
  product_id,
  category_id,
  session_id,
  chat_type,
  CAST(user_msg_count AS INT) AS user_msg_count,
  CAST(merchant_reply_count AS INT) AS merchant_reply_count,
  CAST(first_response_sec AS INT) AS first_response_sec,
  CAST(session_duration_sec AS INT) AS session_duration_sec,
  CAST(is_converted AS INT) AS is_converted,
  convert_action,
  chat_start_time,
  TO_DATE(chat_start_time) AS stat_date,
  HOUR(chat_start_time) AS chat_hour
FROM ods_chat_data
WHERE session_id IS NOT NULL AND session_id != ''
  AND CAST(user_msg_count AS INT) > 0
  AND CAST(session_duration_sec AS INT) >= 5
  AND NOT (CAST(user_msg_count AS INT) = 1 AND CAST(merchant_reply_count AS INT) = 0);

-- ============================================================
-- 查询1：会话总览（dimension_type='overview'）
-- ============================================================
INSERT INTO TABLE ads_chat_analysis
SELECT
  stat_date,
  'overview' AS dimension_type,
  'all' AS dimension_value,
  COUNT(*) AS session_count,
  COUNT(DISTINCT user_id) AS user_count,
  CAST(AVG(session_duration_sec) AS DECIMAL(10,2)) AS avg_duration,
  CAST(AVG(CASE WHEN first_response_sec > 0 AND first_response_sec <= 3600
                THEN first_response_sec END) AS DECIMAL(10,2)) AS avg_first_response,
  CAST(AVG(merchant_reply_count) AS DECIMAL(5,2)) AS avg_reply_count,
  CAST(CAST(SUM(CASE WHEN merchant_reply_count >= 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS resolution_rate,
  SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS conversion_count,
  CAST(CAST(SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS conversion_rate,
  CAST(CAST(SUM(CASE WHEN merchant_reply_count = 0 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS invalid_rate
FROM tmp_clean_chat
GROUP BY stat_date;

-- ============================================================
-- 查询2：咨询类型分布（dimension_type='chat_type'）
-- ============================================================
INSERT INTO TABLE ads_chat_analysis
SELECT
  stat_date,
  'chat_type' AS dimension_type,
  chat_type AS dimension_value,
  COUNT(*) AS session_count,
  COUNT(DISTINCT user_id) AS user_count,
  CAST(AVG(session_duration_sec) AS DECIMAL(10,2)) AS avg_duration,
  CAST(AVG(CASE WHEN first_response_sec > 0 AND first_response_sec <= 3600
                THEN first_response_sec END) AS DECIMAL(10,2)) AS avg_first_response,
  CAST(AVG(merchant_reply_count) AS DECIMAL(5,2)) AS avg_reply_count,
  CAST(CAST(SUM(CASE WHEN merchant_reply_count >= 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS resolution_rate,
  SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS conversion_count,
  CAST(CAST(SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS conversion_rate,
  CAST(CAST(SUM(CASE WHEN merchant_reply_count = 0 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS invalid_rate
FROM tmp_clean_chat
GROUP BY stat_date, chat_type;

-- ============================================================
-- 查询3：时段热力（dimension_type='hour'，仅会话数/用户数/平均时长）
-- ============================================================
INSERT INTO TABLE ads_chat_analysis
SELECT
  stat_date,
  'hour' AS dimension_type,
  CAST(chat_hour AS STRING) AS dimension_value,
  COUNT(*) AS session_count,
  COUNT(DISTINCT user_id) AS user_count,
  CAST(AVG(session_duration_sec) AS DECIMAL(10,2)) AS avg_duration,
  CAST(NULL AS DECIMAL(10,2)) AS avg_first_response,
  CAST(NULL AS DECIMAL(5,2)) AS avg_reply_count,
  CAST(NULL AS DECIMAL(5,4)) AS resolution_rate,
  CAST(NULL AS BIGINT) AS conversion_count,
  CAST(NULL AS DECIMAL(5,4)) AS conversion_rate,
  CAST(NULL AS DECIMAL(5,4)) AS invalid_rate
FROM tmp_clean_chat
GROUP BY stat_date, chat_hour;

-- ============================================================
-- 查询4：咨询转化（dimension_type='conversion'）
-- 列复用：avg_first_response=加购数，avg_reply_count=下单数
-- ============================================================
INSERT INTO TABLE ads_chat_analysis
SELECT
  stat_date,
  'conversion' AS dimension_type,
  chat_type AS dimension_value,
  COUNT(*) AS session_count,
  COUNT(DISTINCT user_id) AS user_count,
  CAST(NULL AS DECIMAL(10,2)) AS avg_duration,
  CAST(SUM(CASE WHEN convert_action = '加购' THEN 1 ELSE 0 END) AS DECIMAL(10,2)) AS avg_first_response,
  CAST(SUM(CASE WHEN convert_action = '下单' THEN 1 ELSE 0 END) AS DECIMAL(5,2)) AS avg_reply_count,
  CAST(NULL AS DECIMAL(5,4)) AS resolution_rate,
  SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS conversion_count,
  CAST(CAST(SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS conversion_rate,
  CAST(NULL AS DECIMAL(5,4)) AS invalid_rate
FROM tmp_clean_chat
GROUP BY stat_date, chat_type;

-- ============================================================
-- 查询5：热门咨询商品（dimension_type='product'，全局 TOP20）
-- ============================================================
INSERT INTO TABLE ads_chat_analysis
SELECT * FROM (
  SELECT
    stat_date,
    'product' AS dimension_type,
    product_id AS dimension_value,
    COUNT(*) AS session_count,
    COUNT(DISTINCT user_id) AS user_count,
    CAST(AVG(session_duration_sec) AS DECIMAL(10,2)) AS avg_duration,
    CAST(AVG(CASE WHEN first_response_sec > 0 AND first_response_sec <= 3600
                  THEN first_response_sec END) AS DECIMAL(10,2)) AS avg_first_response,
    CAST(AVG(merchant_reply_count) AS DECIMAL(5,2)) AS avg_reply_count,
    CAST(CAST(SUM(CASE WHEN merchant_reply_count >= 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
      / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS resolution_rate,
    SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS conversion_count,
    CAST(CAST(SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
      / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS conversion_rate,
    CAST(CAST(SUM(CASE WHEN merchant_reply_count = 0 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
      / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS invalid_rate
  FROM tmp_clean_chat
  GROUP BY stat_date, product_id
  ORDER BY session_count DESC
  LIMIT 20
) p;

-- ============================================================
-- 查询6：服务质量（dimension_type='service'）
-- ============================================================
INSERT INTO TABLE ads_chat_analysis
SELECT
  stat_date,
  'service' AS dimension_type,
  'all' AS dimension_value,
  COUNT(*) AS session_count,
  COUNT(DISTINCT user_id) AS user_count,
  CAST(AVG(session_duration_sec) AS DECIMAL(10,2)) AS avg_duration,
  CAST(AVG(CASE WHEN first_response_sec > 0 AND first_response_sec <= 3600
                THEN first_response_sec END) AS DECIMAL(10,2)) AS avg_first_response,
  CAST(AVG(merchant_reply_count) AS DECIMAL(5,2)) AS avg_reply_count,
  CAST(CAST(SUM(CASE WHEN merchant_reply_count >= 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS resolution_rate,
  SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS conversion_count,
  CAST(CAST(SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS conversion_rate,
  CAST(CAST(SUM(CASE WHEN merchant_reply_count = 0 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS invalid_rate
FROM tmp_clean_chat
GROUP BY stat_date;

-- ============================================================
-- 查询7：品类咨询分布（dimension_type='category'）
-- ============================================================
INSERT INTO TABLE ads_chat_analysis
SELECT
  stat_date,
  'category' AS dimension_type,
  category_id AS dimension_value,
  COUNT(*) AS session_count,
  COUNT(DISTINCT user_id) AS user_count,
  CAST(AVG(session_duration_sec) AS DECIMAL(10,2)) AS avg_duration,
  CAST(AVG(CASE WHEN first_response_sec > 0 AND first_response_sec <= 3600
                THEN first_response_sec END) AS DECIMAL(10,2)) AS avg_first_response,
  CAST(AVG(merchant_reply_count) AS DECIMAL(5,2)) AS avg_reply_count,
  CAST(CAST(SUM(CASE WHEN merchant_reply_count >= 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS resolution_rate,
  SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS conversion_count,
  CAST(CAST(SUM(CASE WHEN is_converted = 1 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS conversion_rate,
  CAST(CAST(SUM(CASE WHEN merchant_reply_count = 0 THEN 1 ELSE 0 END) AS DECIMAL(12,4))
    / NULLIF(COUNT(*), 0) AS DECIMAL(5,4)) AS invalid_rate
FROM tmp_clean_chat
GROUP BY stat_date, category_id;

-- ============================================================
-- 验证
-- ============================================================
SELECT COUNT(*) AS valid_sessions, COUNT(DISTINCT user_id) AS valid_users
FROM tmp_clean_chat;

SELECT dimension_type, COUNT(*) AS rows_cnt
FROM ads_chat_analysis
GROUP BY dimension_type;

SELECT COUNT(*) AS total_rows FROM ads_chat_analysis;

SELECT MIN(resolution_rate) min_res, MAX(resolution_rate) max_res,
       MIN(conversion_rate) min_conv, MAX(conversion_rate) max_conv,
       MIN(avg_first_response) min_fr, MAX(avg_first_response) max_fr,
       SUM(CASE WHEN avg_duration > 3600 THEN 1 ELSE 0 END) bad_dur
FROM ads_chat_analysis
WHERE dimension_type = 'overview';
