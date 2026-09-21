-- ============================================================
-- 工单04：用户评价数据清洗与ADS宽表加载
-- 输入：ods_review_data（5000行，13列，逗号分隔）
-- 输出：ads_review_analysis（12列宽表）
-- 说明：
--   - 查询3品类口碑：ODS无category_id字段，跳过（由product榜覆盖）
--   - 查询8口碑趋势：与查询1字段重复，合并跳过
--   - 成交订单式的相关子查询分母统一用 tmp_daily_total JOIN 实现
-- ============================================================

-- 防OOM（工单03踩坑：HS2与LocalJobRunner共JVM）
SET io.sort.mb=32;

-- 0. 清空目标表
TRUNCATE TABLE ads_review_analysis;

-- 1. 统一清洗：去重(review_id) / 过滤空内容与系统默认好评 / 等级规整 / 关键词标记
DROP TABLE IF EXISTS tmp_clean_review;

CREATE TABLE tmp_clean_review AS
SELECT
  review_id,
  order_id,
  user_id,
  product_id,
  CAST(rating AS INT) AS rating,
  review_content,
  review_time,
  is_append,
  append_content,
  CAST(image_count AS INT) AS image_count,
  CAST(praise_num AS INT) AS praise_num,
  merchant_reply,
  TO_DATE(review_time) AS stat_date,
  CASE
    WHEN CAST(rating AS INT) >= 4 THEN '好评'
    WHEN CAST(rating AS INT) = 3 THEN '中评'
    ELSE '差评'
  END AS review_level,
  CASE WHEN review_content LIKE '%质量%' THEN 1 ELSE 0 END AS kw_quality,
  CASE WHEN review_content LIKE '%物流%' THEN 1 ELSE 0 END AS kw_logistics,
  CASE WHEN review_content LIKE '%服务%' THEN 1 ELSE 0 END AS kw_service,
  CASE WHEN review_content LIKE '%性价比%' OR review_content LIKE '%便宜%' OR review_content LIKE '%划算%' THEN 1 ELSE 0 END AS kw_price,
  CASE WHEN review_content LIKE '%外观%' OR review_content LIKE '%好看%' OR review_content LIKE '%漂亮%' THEN 1 ELSE 0 END AS kw_appearance,
  CASE WHEN review_content LIKE '%尺码%' OR review_content LIKE '%大小%' THEN 1 ELSE 0 END AS kw_size,
  CASE WHEN review_content LIKE '%包装%' THEN 1 ELSE 0 END AS kw_packaging,
  CASE WHEN review_content LIKE '%售后%' OR review_content LIKE '%退货%' OR review_content LIKE '%退款%' THEN 1 ELSE 0 END AS kw_aftersale
FROM (
  SELECT *,
         ROW_NUMBER() OVER (PARTITION BY review_id ORDER BY review_time) AS rn
  FROM ods_review_data
  WHERE review_id IS NOT NULL AND review_id != ''
    AND rating IS NOT NULL AND rating != ''
    AND review_content IS NOT NULL AND review_content != ''
    AND NOT (CAST(rating AS INT) = 5 AND review_content LIKE '%系统默认好评%')
) t
WHERE rn = 1;

-- 1b. 每日评价总数（占比分母，规避 Hive 相关子查询限制）
DROP TABLE IF EXISTS tmp_daily_total;

CREATE TABLE tmp_daily_total AS
SELECT stat_date, COUNT(*) AS total_cnt
FROM tmp_clean_review
GROUP BY stat_date;

-- ============================================================
-- 查询1：评价总览（dimension_type='overview'，含口碑趋势）
-- ============================================================
INSERT INTO TABLE ads_review_analysis
SELECT
  stat_date,
  'overview' AS dimension_type,
  'all' AS dimension_value,
  COUNT(*) AS total_reviews,
  SUM(CASE WHEN review_level = '好评' THEN 1 ELSE 0 END) AS good_reviews,
  SUM(CASE WHEN review_level = '中评' THEN 1 ELSE 0 END) AS mid_reviews,
  SUM(CASE WHEN review_level = '差评' THEN 1 ELSE 0 END) AS bad_reviews,
  CAST(SUM(CASE WHEN review_level = '好评' THEN 1 ELSE 0 END) AS DECIMAL(10,4))
    / NULLIF(COUNT(*), 0) AS good_rate,
  CAST(AVG(rating) AS DECIMAL(3,2)) AS avg_rating,
  NULL AS keyword_count,
  SUM(CASE WHEN is_append = '是' THEN 1 ELSE 0 END) AS append_count,
  CAST(SUM(CASE WHEN is_append = '是' THEN 1 ELSE 0 END) AS DECIMAL(10,4))
    / NULLIF(COUNT(*), 0) AS append_rate
FROM tmp_clean_review
GROUP BY stat_date;

-- ============================================================
-- 查询2：等级分布（dimension_type='rating_level'）
-- good_rate 列复用为该等级当日占比
-- ============================================================
INSERT INTO TABLE ads_review_analysis
SELECT
  t1.stat_date,
  'rating_level' AS dimension_type,
  t1.review_level AS dimension_value,
  COUNT(*) AS total_reviews,
  CASE WHEN t1.review_level = '好评' THEN COUNT(*) ELSE 0 END AS good_reviews,
  CASE WHEN t1.review_level = '中评' THEN COUNT(*) ELSE 0 END AS mid_reviews,
  CASE WHEN t1.review_level = '差评' THEN COUNT(*) ELSE 0 END AS bad_reviews,
  CAST(CAST(COUNT(*) AS DECIMAL(10,4)) / NULLIF(t2.total_cnt, 0) AS DECIMAL(5,4)) AS good_rate,
  CAST(AVG(t1.rating) AS DECIMAL(3,2)) AS avg_rating,
  NULL AS keyword_count,
  SUM(CASE WHEN t1.is_append = '是' THEN 1 ELSE 0 END) AS append_count,
  NULL AS append_rate
FROM tmp_clean_review t1
LEFT JOIN tmp_daily_total t2 ON t1.stat_date = t2.stat_date
GROUP BY t1.stat_date, t1.review_level, t2.total_cnt;

-- ============================================================
-- 查询3：品类口碑 —— 跳过（ods_review_data 无 category_id）
-- ============================================================

-- ============================================================
-- 查询4：关键词分析（keyword_good TOP30 / keyword_bad TOP30）
-- ============================================================
INSERT INTO TABLE ads_review_analysis
SELECT * FROM (
  SELECT
    stat_date,
    'keyword_good' AS dimension_type,
    keyword_name AS dimension_value,
    cnt AS total_reviews,
    cnt AS good_reviews,
    0 AS mid_reviews,
    0 AS bad_reviews,
    NULL AS good_rate,
    NULL AS avg_rating,
    cnt AS keyword_count,
    NULL AS append_count,
    NULL AS append_rate
  FROM (
    SELECT stat_date, '质量' AS keyword_name, SUM(kw_quality) AS cnt
      FROM tmp_clean_review WHERE review_level = '好评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '物流', SUM(kw_logistics)
      FROM tmp_clean_review WHERE review_level = '好评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '服务', SUM(kw_service)
      FROM tmp_clean_review WHERE review_level = '好评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '性价比', SUM(kw_price)
      FROM tmp_clean_review WHERE review_level = '好评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '外观', SUM(kw_appearance)
      FROM tmp_clean_review WHERE review_level = '好评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '尺码', SUM(kw_size)
      FROM tmp_clean_review WHERE review_level = '好评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '包装', SUM(kw_packaging)
      FROM tmp_clean_review WHERE review_level = '好评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '售后', SUM(kw_aftersale)
      FROM tmp_clean_review WHERE review_level = '好评' GROUP BY stat_date
  ) kw
  WHERE cnt > 0
  ORDER BY keyword_count DESC
  LIMIT 30
) g;

INSERT INTO TABLE ads_review_analysis
SELECT * FROM (
  SELECT
    stat_date,
    'keyword_bad' AS dimension_type,
    keyword_name AS dimension_value,
    cnt AS total_reviews,
    0 AS good_reviews,
    0 AS mid_reviews,
    cnt AS bad_reviews,
    NULL AS good_rate,
    NULL AS avg_rating,
    cnt AS keyword_count,
    NULL AS append_count,
    NULL AS append_rate
  FROM (
    SELECT stat_date, '质量' AS keyword_name, SUM(kw_quality) AS cnt
      FROM tmp_clean_review WHERE review_level = '差评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '物流', SUM(kw_logistics)
      FROM tmp_clean_review WHERE review_level = '差评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '服务', SUM(kw_service)
      FROM tmp_clean_review WHERE review_level = '差评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '性价比', SUM(kw_price)
      FROM tmp_clean_review WHERE review_level = '差评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '外观', SUM(kw_appearance)
      FROM tmp_clean_review WHERE review_level = '差评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '尺码', SUM(kw_size)
      FROM tmp_clean_review WHERE review_level = '差评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '包装', SUM(kw_packaging)
      FROM tmp_clean_review WHERE review_level = '差评' GROUP BY stat_date
    UNION ALL
    SELECT stat_date, '售后', SUM(kw_aftersale)
      FROM tmp_clean_review WHERE review_level = '差评' GROUP BY stat_date
  ) kw
  WHERE cnt > 0
  ORDER BY keyword_count DESC
  LIMIT 30
) b;

-- ============================================================
-- 查询5：维度评价占比（dimension_type='keyword_dim'，5核心维度）
-- good_rate = 好评中该维度提及率；bad_reviews/total_reviews = 差评中提及占比
-- ============================================================
INSERT INTO TABLE ads_review_analysis
SELECT
  stat_date,
  'keyword_dim' AS dimension_type,
  keyword_dim AS dimension_value,
  SUM(has_kw) AS total_reviews,
  SUM(CASE WHEN review_level = '好评' THEN has_kw ELSE 0 END) AS good_reviews,
  NULL AS mid_reviews,
  SUM(CASE WHEN review_level = '差评' THEN has_kw ELSE 0 END) AS bad_reviews,
  CAST(SUM(CASE WHEN review_level = '好评' THEN has_kw ELSE 0 END) AS DECIMAL(10,4))
    / NULLIF(SUM(CASE WHEN review_level = '好评' THEN 1 ELSE 0 END), 0) AS good_rate,
  NULL AS avg_rating,
  SUM(has_kw) AS keyword_count,
  NULL AS append_count,
  NULL AS append_rate
FROM (
  SELECT stat_date, review_level, '质量' AS keyword_dim, kw_quality AS has_kw FROM tmp_clean_review
  UNION ALL
  SELECT stat_date, review_level, '物流', kw_logistics FROM tmp_clean_review
  UNION ALL
  SELECT stat_date, review_level, '服务', kw_service FROM tmp_clean_review
  UNION ALL
  SELECT stat_date, review_level, '性价比', kw_price FROM tmp_clean_review
  UNION ALL
  SELECT stat_date, review_level, '外观', kw_appearance FROM tmp_clean_review
) t
GROUP BY stat_date, keyword_dim;

-- ============================================================
-- 查询6：商品口碑榜（product_top TOP20 / product_bottom BOTTOM20）
-- 至少3条评价才上榜
-- ============================================================
INSERT INTO TABLE ads_review_analysis
SELECT * FROM (
  SELECT
    stat_date,
    'product_top' AS dimension_type,
    product_id AS dimension_value,
    COUNT(*) AS total_reviews,
    SUM(CASE WHEN review_level = '好评' THEN 1 ELSE 0 END) AS good_reviews,
    SUM(CASE WHEN review_level = '中评' THEN 1 ELSE 0 END) AS mid_reviews,
    SUM(CASE WHEN review_level = '差评' THEN 1 ELSE 0 END) AS bad_reviews,
    CAST(SUM(CASE WHEN review_level = '好评' THEN 1 ELSE 0 END) AS DECIMAL(10,4))
      / NULLIF(COUNT(*), 0) AS good_rate,
    CAST(AVG(rating) AS DECIMAL(3,2)) AS avg_rating,
    NULL AS keyword_count,
    NULL AS append_count,
    NULL AS append_rate
  FROM tmp_clean_review
  GROUP BY stat_date, product_id
  HAVING COUNT(*) >= 3
  ORDER BY avg_rating DESC, total_reviews DESC
  LIMIT 20
) t;

INSERT INTO TABLE ads_review_analysis
SELECT * FROM (
  SELECT
    stat_date,
    'product_bottom' AS dimension_type,
    product_id AS dimension_value,
    COUNT(*) AS total_reviews,
    SUM(CASE WHEN review_level = '好评' THEN 1 ELSE 0 END) AS good_reviews,
    SUM(CASE WHEN review_level = '中评' THEN 1 ELSE 0 END) AS mid_reviews,
    SUM(CASE WHEN review_level = '差评' THEN 1 ELSE 0 END) AS bad_reviews,
    CAST(SUM(CASE WHEN review_level = '好评' THEN 1 ELSE 0 END) AS DECIMAL(10,4))
      / NULLIF(COUNT(*), 0) AS good_rate,
    CAST(AVG(rating) AS DECIMAL(3,2)) AS avg_rating,
    NULL AS keyword_count,
    NULL AS append_count,
    NULL AS append_rate
  FROM tmp_clean_review
  GROUP BY stat_date, product_id
  HAVING COUNT(*) >= 3
  ORDER BY avg_rating ASC, total_reviews DESC
  LIMIT 20
) t;

-- ============================================================
-- 查询7：追评分析（dimension_type='append'）
-- append_rate = 当日追评数 / 当日评价总数（JOIN tmp_daily_total）
-- ============================================================
INSERT INTO TABLE ads_review_analysis
SELECT
  r.stat_date,
  'append' AS dimension_type,
  'with_append' AS dimension_value,
  r.append_cnt AS total_reviews,
  r.good_cnt AS good_reviews,
  r.mid_cnt AS mid_reviews,
  r.bad_cnt AS bad_reviews,
  CAST(CAST(r.good_cnt AS DECIMAL(10,4)) / NULLIF(r.append_cnt, 0) AS DECIMAL(5,4)) AS good_rate,
  r.avg_r AS avg_rating,
  NULL AS keyword_count,
  r.append_cnt AS append_count,
  CAST(CAST(r.append_cnt AS DECIMAL(10,4)) / NULLIF(d.total_cnt, 0) AS DECIMAL(5,4)) AS append_rate
FROM (
  SELECT
    stat_date,
    COUNT(*) AS append_cnt,
    SUM(CASE WHEN review_level = '好评' THEN 1 ELSE 0 END) AS good_cnt,
    SUM(CASE WHEN review_level = '中评' THEN 1 ELSE 0 END) AS mid_cnt,
    SUM(CASE WHEN review_level = '差评' THEN 1 ELSE 0 END) AS bad_cnt,
    CAST(AVG(rating) AS DECIMAL(3,2)) AS avg_r
  FROM tmp_clean_review
  WHERE is_append = '是'
  GROUP BY stat_date
) r
LEFT JOIN tmp_daily_total d ON r.stat_date = d.stat_date;

-- 查询8：口碑趋势 —— 与查询1重复，合并跳过

-- ============================================================
-- 验证
-- ============================================================
SELECT dimension_type, COUNT(*) AS rows_cnt
FROM ads_review_analysis
GROUP BY dimension_type;

SELECT COUNT(*) AS total_rows FROM ads_review_analysis;
