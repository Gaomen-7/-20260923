-- ============================================================
-- 工单06：Hive ADS 表导出为 CSV（LOCAL DIRECTORY = hive-server 容器本地）
-- 导出后需 docker cp 到宿主机 E:\space\Project\test\mall-sys\phase2-csv\
-- ============================================================

-- 1. 用户行为分析表（834行，11列，不含id）
INSERT OVERWRITE LOCAL DIRECTORY '/tmp/phase2-csv/ads_user_behavior'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
SELECT
  stat_date, dimension_type, dimension_value,
  pv, uv, session_count, conversion_count,
  conversion_rate, bounce_rate, avg_duration
FROM ads_user_behavior;

-- 2. 订单分析表（766行，13列，不含id）
INSERT OVERWRITE LOCAL DIRECTORY '/tmp/phase2-csv/ads_order_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
SELECT
  stat_date, dimension_type, dimension_value,
  order_count, paid_order_count, total_amount, avg_order_value,
  pay_conversion_rate, cancel_count, return_count, return_rate, return_amount
FROM ads_order_analysis;

-- 3. 评价分析表（200行，12列，不含id）
INSERT OVERWRITE LOCAL DIRECTORY '/tmp/phase2-csv/ads_review_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
SELECT
  stat_date, dimension_type, dimension_value,
  total_reviews, good_reviews, mid_reviews, bad_reviews,
  good_rate, avg_rating, keyword_count, append_count, append_rate
FROM ads_review_analysis;

-- 4. 交流分析表（689行，13列，不含id）
INSERT OVERWRITE LOCAL DIRECTORY '/tmp/phase2-csv/ads_chat_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
SELECT
  stat_date, dimension_type, dimension_value,
  session_count, user_count, avg_duration, avg_first_response,
  avg_reply_count, resolution_rate, conversion_count, conversion_rate, invalid_rate
FROM ads_chat_analysis;
