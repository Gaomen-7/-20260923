# 工单 02：Hive 清洗 — 用户全链路行为分析

**状态**：待执行
**依赖**：工单 01（ODS 表已建，数据已生成）
**预计时间**：30 分钟
**执行席**：atomcode CLI（写 HQL 文件）

## 交付物

文件：`E:\space\Project\test\mall-sys\phase2_behavior_etl.hql`

## 清洗规则（对应需求文档三(一)1）

1. **脏数据过滤**：剔除 user_id 为空、product_id 为空、behavior_time 异常、重复 log_id 的记录
2. **字段标准化**：时间格式统一，device_type 统一归类（手机=Android+iOS，电脑=Web，平板=Pad）
3. **行为归类**：7 类核心行为（浏览/搜索/收藏/加购/下单/支付/退出）
4. **维度补全**：关联商品品类（用 product_id 映射 category_id，生成器已含则直接用）
5. **链路拼接**：基于 session_id 拼接用户访问链路

## ADS 宽表 `ads_user_behavior` 设计

按 `stat_date`（统计日期）+ `dimension_type`（维度类型）+ `dimension_value`（维度值）组织，一行一个指标组合：

| 字段 | 类型 | 说明 |
|---|---|---|
| stat_date | STRING | 统计日期 yyyy-MM-dd |
| dimension_type | STRING | 指标维度：source/behavior/keyword/hour/funnel |
| dimension_value | STRING | 维度值 |
| pv | BIGINT | 浏览量 |
| uv | BIGINT | 独立用户数 |
| session_count | BIGINT | 会话数 |
| conversion_count | BIGINT | 转化人数（下单/支付） |
| conversion_rate | DECIMAL(5,4) | 转化率 |
| bounce_rate | DECIMAL(5,4) | 跳出率 |
| avg_duration | DECIMAL(10,2) | 平均时长（秒） |

## 需要产出的查询结果（INSERT INTO ads_user_behavior）

1. **流量入口指标**：按 source 分组，计算各入口 PV/UV/跳出率/转化人数/转化率
2. **行为趋势指标**：按 stat_date + behavior_type 分组，计算每日各行为 PV/UV
3. **热搜关键词**：behavior_type=搜索，按 search_keyword 分组 TOP50，计算搜索次数/搜索人数/搜索后加购率
4. **转化漏斗**：全局漏斗，浏览→搜索→加购→下单→支付，每环节人数/转化率/流失率
5. **时段热力**：按 HOUR(behavior_time) 分组，计算每小时活跃用户数/下单数
6. **热门商品**：按 product_id 分组，浏览量 TOP100 / 收藏量 TOP20 / 加购量 TOP20

## 验收标准

1. HQL 语法正确，可在 Hive 中执行
2. 每条 INSERT 语句有注释说明对应需求文档的哪个指标
3. 漏斗查询用子查询逐步收敛，每环节人数递减
4. 转化率用 `SUM(CASE WHEN...) / COUNT(*)` 计算，除零保护（NULLIF 或 COALESCE）
5. 执行后 `SELECT COUNT(*) FROM ads_user_behavior` 返回合理行数（预计 200-500 行）
