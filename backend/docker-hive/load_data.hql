-- 加载用户行为日志（Tab分隔）
LOAD DATA LOCAL INPATH '/tmp/phase2-data/user_behavior.log' OVERWRITE INTO TABLE ods_user_behavior;

-- 加载订单数据（逗号分隔）
LOAD DATA LOCAL INPATH '/tmp/phase2-data/order_data.csv' OVERWRITE INTO TABLE ods_order_data;

-- 加载评价数据（逗号分隔）
LOAD DATA LOCAL INPATH '/tmp/phase2-data/review_data.csv' OVERWRITE INTO TABLE ods_review_data;

-- 加载交流数据（逗号分隔）
LOAD DATA LOCAL INPATH '/tmp/phase2-data/chat_data.csv' OVERWRITE INTO TABLE ods_chat_data;
