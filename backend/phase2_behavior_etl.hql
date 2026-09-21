-- ============================================================
-- 工单02：用户行为数据清洗与ADS宽表加载
-- 输入：ods_user_behavior（10万行）
-- 输出：ads_user_behavior（宽表）
-- 注意：Hive 的 WITH CTE 只对紧随的一条语句有效，
--       故用中间表 tmp_clean_behavior 承载清洗结果（持久表，便于答辩现场核对）。
-- ============================================================

-- 0. 清空目标表（避免重复运行数据重复）
TRUNCATE TABLE ads_user_behavior;

DROP TABLE IF EXISTS tmp_clean_behavior;

-- 1. 统一清洗：去重 / 脏数据过滤 / 字段标准化
CREATE TABLE tmp_clean_behavior AS
SELECT
  log_id,
  user_id,
  product_id,
  CASE behavior_type
    WHEN '浏览' THEN '浏览'
    WHEN '搜索' THEN '搜索'
    WHEN '收藏' THEN '收藏'
    WHEN '加入购物车' THEN '加购'
    WHEN '加购' THEN '加购'
    WHEN '购买' THEN '下单'
    WHEN '下单' THEN '下单'
    WHEN '支付' THEN '支付'
    WHEN '退出' THEN '退出'
    ELSE '其他'
  END AS behavior_type,
  source,
  search_keyword,
  session_id,
  CASE device_type
    WHEN 'Android' THEN '手机'
    WHEN 'iOS' THEN '手机'
    WHEN 'Web' THEN '电脑'
    WHEN 'PC' THEN '电脑'
    WHEN 'Pad' THEN '平板'
    WHEN 'Tablet' THEN '平板'
    WHEN '平板' THEN '平板'
    ELSE '其他'
  END AS device_type,
  ip_city,
  CAST(product_price AS DECIMAL(10,2)) AS product_price,
  CAST(quantity AS INT) AS quantity,
  behavior_time,
  TO_DATE(behavior_time) AS stat_date,
  HOUR(behavior_time) AS behavior_hour
FROM (
  SELECT *,
         ROW_NUMBER() OVER (PARTITION BY log_id ORDER BY behavior_time) AS rn
  FROM ods_user_behavior
  WHERE user_id IS NOT NULL AND user_id != ''
    AND product_id IS NOT NULL AND product_id != ''
    AND behavior_time IS NOT NULL AND behavior_time != ''
    AND behavior_time RLIKE '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$'
) t
WHERE rn = 1;

-- ============================================================
-- 查询1：流量入口指标（dimension_type='source'）
-- 跳出率 = 该来源下只有1条行为的session数 / 总session数
-- ============================================================
INSERT INTO TABLE ads_user_behavior
SELECT
  stat_date,
  'source' AS dimension_type,
  source AS dimension_value,
  COUNT(*) AS pv,
  COUNT(DISTINCT user_id) AS uv,
  COUNT(DISTINCT session_id) AS session_count,
  COUNT(DISTINCT CASE WHEN behavior_type IN ('下单','支付') THEN user_id END) AS conversion_count,
  CAST(COUNT(DISTINCT CASE WHEN behavior_type IN ('下单','支付') THEN user_id END) AS DECIMAL(10,4))
    / NULLIF(COUNT(DISTINCT user_id), 0) AS conversion_rate,
  CAST(COALESCE(b.bounce_sessions, 0) AS DECIMAL(10,4)) / NULLIF(COUNT(DISTINCT session_id), 0) AS bounce_rate,
  NULL AS avg_duration
FROM tmp_clean_behavior c
LEFT JOIN (
  SELECT source, COUNT(DISTINCT session_id) AS bounce_sessions
  FROM (
    SELECT source, session_id, COUNT(*) AS cnt
    FROM tmp_clean_behavior
    GROUP BY source, session_id
    HAVING COUNT(*) = 1
  ) s
  GROUP BY source
) b ON c.source = b.source
GROUP BY stat_date, c.source, b.bounce_sessions;

-- ============================================================
-- 查询2：行为趋势指标（dimension_type='behavior'）
-- ============================================================
INSERT INTO TABLE ads_user_behavior
SELECT
  stat_date,
  'behavior' AS dimension_type,
  behavior_type AS dimension_value,
  COUNT(*) AS pv,
  COUNT(DISTINCT user_id) AS uv,
  COUNT(DISTINCT session_id) AS session_count,
  NULL AS conversion_count,
  NULL AS conversion_rate,
  NULL AS bounce_rate,
  NULL AS avg_duration
FROM tmp_clean_behavior
GROUP BY stat_date, behavior_type;

-- ============================================================
-- 查询3：热搜关键词（dimension_type='keyword'）TOP50
-- 搜索后加购率 = 搜索后同session有加购的session数 / 搜索session数
-- ============================================================
INSERT INTO TABLE ads_user_behavior
SELECT
  stat_date,
  'keyword' AS dimension_type,
  search_keyword AS dimension_value,
  pv,
  uv,
  session_count,
  addcart_sessions AS conversion_count,
  CAST(addcart_sessions AS DECIMAL(10,4)) / NULLIF(session_count, 0) AS conversion_rate,
  NULL AS bounce_rate,
  NULL AS avg_duration
FROM (
  SELECT
    stat_date,
    search_keyword,
    COUNT(*) AS pv,
    COUNT(DISTINCT user_id) AS uv,
    COUNT(DISTINCT session_id) AS session_count,
    COUNT(DISTINCT CASE WHEN has_addcart = 1 THEN session_id END) AS addcart_sessions
  FROM (
    SELECT
      s.stat_date, s.user_id, s.search_keyword, s.session_id,
      CASE WHEN a.session_id IS NOT NULL THEN 1 ELSE 0 END AS has_addcart
    FROM (SELECT * FROM tmp_clean_behavior
          WHERE behavior_type = '搜索'
            AND search_keyword IS NOT NULL AND search_keyword != '') s
    LEFT JOIN (
      SELECT DISTINCT session_id FROM tmp_clean_behavior WHERE behavior_type = '加购'
    ) a ON s.session_id = a.session_id
  ) t
  GROUP BY stat_date, search_keyword
) agg
ORDER BY pv DESC
LIMIT 50;

-- ============================================================
-- 查询4：转化漏斗（dimension_type='funnel'）
-- 先算各环节去重人数，再JOIN上一步人数，避免Hive相关子查询限制
-- ============================================================
DROP TABLE IF EXISTS tmp_funnel_step;

CREATE TABLE tmp_funnel_step AS
SELECT
  stat_date,
  CASE behavior_type
    WHEN '浏览' THEN '浏览'
    WHEN '搜索' THEN '搜索'
    WHEN '加购' THEN '加购'
    WHEN '下单' THEN '下单'
    WHEN '支付' THEN '支付'
  END AS funnel_step,
  COUNT(DISTINCT user_id) AS user_count
FROM tmp_clean_behavior
WHERE behavior_type IN ('浏览','搜索','加购','下单','支付')
GROUP BY stat_date,
  CASE behavior_type
    WHEN '浏览' THEN '浏览'
    WHEN '搜索' THEN '搜索'
    WHEN '加购' THEN '加购'
    WHEN '下单' THEN '下单'
    WHEN '支付' THEN '支付'
  END;

INSERT INTO TABLE ads_user_behavior
SELECT
  cur.stat_date,
  'funnel' AS dimension_type,
  cur.funnel_step AS dimension_value,
  cur.user_count AS pv,
  NULL AS uv,
  NULL AS session_count,
  NULL AS conversion_count,
  CASE
    WHEN cur.funnel_step = '浏览' THEN CAST(1.0 AS DECIMAL(5,4))
    ELSE CAST(cur.user_count AS DECIMAL(10,4)) / NULLIF(prev.user_count, 0)
  END AS conversion_rate,
  CASE
    WHEN cur.funnel_step = '浏览' THEN CAST(0.0 AS DECIMAL(5,4))
    ELSE CAST(1 - CAST(cur.user_count AS DECIMAL(10,4)) / NULLIF(prev.user_count, 0) AS DECIMAL(5,4))
  END AS bounce_rate,
  NULL AS avg_duration
FROM tmp_funnel_step cur
LEFT JOIN (
  SELECT stat_date, funnel_step, user_count
  FROM tmp_funnel_step
) prev
ON cur.stat_date = prev.stat_date
AND prev.funnel_step = CASE cur.funnel_step
    WHEN '搜索' THEN '浏览'
    WHEN '加购' THEN '搜索'
    WHEN '下单' THEN '加购'
    WHEN '支付' THEN '下单'
  END;

-- ============================================================
-- 查询5：时段热力（dimension_type='hour'）
-- ============================================================
INSERT INTO TABLE ads_user_behavior
SELECT
  stat_date,
  'hour' AS dimension_type,
  LPAD(CAST(behavior_hour AS STRING), 2, '0') AS dimension_value,
  COUNT(*) AS pv,
  COUNT(DISTINCT user_id) AS uv,
  COUNT(DISTINCT session_id) AS session_count,
  COUNT(DISTINCT CASE WHEN behavior_type IN ('下单','支付') THEN user_id END) AS conversion_count,
  NULL AS conversion_rate,
  NULL AS bounce_rate,
  NULL AS avg_duration
FROM tmp_clean_behavior
GROUP BY stat_date, behavior_hour;

-- ============================================================
-- 查询6：热门商品
-- 6a: 浏览量 TOP100 / 6b: 收藏量 TOP20 / 6c: 加购量 TOP20
-- ============================================================
INSERT INTO TABLE ads_user_behavior
SELECT stat_date, dimension_type, dimension_value, pv, uv,
       session_count, conversion_count, conversion_rate, bounce_rate, avg_duration
FROM (
  SELECT
    stat_date,
    'product_view' AS dimension_type,
    product_id AS dimension_value,
    COUNT(*) AS pv,
    COUNT(DISTINCT user_id) AS uv,
    NULL AS session_count,
    NULL AS conversion_count,
    NULL AS conversion_rate,
    NULL AS bounce_rate,
    NULL AS avg_duration
  FROM tmp_clean_behavior
  WHERE behavior_type = '浏览'
  GROUP BY stat_date, product_id
  ORDER BY pv DESC
  LIMIT 100
) v;

INSERT INTO TABLE ads_user_behavior
SELECT stat_date, dimension_type, dimension_value, pv, uv,
       session_count, conversion_count, conversion_rate, bounce_rate, avg_duration
FROM (
  SELECT
    stat_date,
    'product_fav' AS dimension_type,
    product_id AS dimension_value,
    COUNT(*) AS pv,
    COUNT(DISTINCT user_id) AS uv,
    NULL AS session_count,
    NULL AS conversion_count,
    NULL AS conversion_rate,
    NULL AS bounce_rate,
    NULL AS avg_duration
  FROM tmp_clean_behavior
  WHERE behavior_type = '收藏'
  GROUP BY stat_date, product_id
  ORDER BY pv DESC
  LIMIT 20
) f;

INSERT INTO TABLE ads_user_behavior
SELECT stat_date, dimension_type, dimension_value, pv, uv,
       session_count, conversion_count, conversion_rate, bounce_rate, avg_duration
FROM (
  SELECT
    stat_date,
    'product_cart' AS dimension_type,
    product_id AS dimension_value,
    COUNT(*) AS pv,
    COUNT(DISTINCT user_id) AS uv,
    NULL AS session_count,
    NULL AS conversion_count,
    NULL AS conversion_rate,
    NULL AS bounce_rate,
    NULL AS avg_duration
  FROM tmp_clean_behavior
  WHERE behavior_type = '加购'
  GROUP BY stat_date, product_id
  ORDER BY pv DESC
  LIMIT 20
) ca;

-- ============================================================
-- 验证
-- ============================================================
SELECT dimension_type, COUNT(*) AS rows_cnt
FROM ads_user_behavior
GROUP BY dimension_type;

SELECT COUNT(*) AS total_rows FROM ads_user_behavior;
