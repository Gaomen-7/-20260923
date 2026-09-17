# 工单 05：Hive 清洗 — 用户与商家交流数据分析

**状态**：✅ 已完成（2026-09-16，689行，7维度，验收7/8通过）
**依赖**：工单 01
**预计时间**：25 分钟
**执行席**：atomcode CLI
**执行日志**：`E:\space\Project\test\mall-sys\ticket05_run.log`
**抽查日志**：`E:\space\Project\test\mall-sys\ticket05_spotcheck.log`

## 交付物

文件：`E:\space\Project\test\mall-sys\phase2_chat_etl.hql`

## 清洗规则（对应需求文档三(四)1）

1. **无效过滤**：剔除 session_id 为空、user_msg_count=0、秒退会话（session_duration_sec < 5）
2. **内容分类**：chat_type 已在生成器中按关键词归类为 6 类（商品咨询/价格活动/物流查询/售后问题/投诉建议/其他）
3. **字段规整**：会话时长/首次响应时间统一为秒
4. **低效过滤**：过滤 user_msg_count=1 且 merchant_reply_count=0 的纯表情包/无有效内容会话
5. **维度关联**：关联品类和用户层级

## ADS 宽表 `ads_chat_analysis` 设计

| 字段 | 类型 | 说明 |
|---|---|---|
| stat_date | STRING | 统计日期 |
| dimension_type | STRING | 维度：overview/chat_type/hour/product/category |
| dimension_value | STRING | 维度值 |
| session_count | BIGINT | 有效会话数 |
| user_count | BIGINT | 咨询用户数 |
| avg_duration | DECIMAL(10,2) | 平均会话时长（秒） |
| avg_first_response | DECIMAL(10,2) | 平均首次响应时长（秒） |
| avg_reply_count | DECIMAL(5,2) | 平均客服回复次数 |
| resolution_rate | DECIMAL(5,4) | 问题解决率（is_converted 或有明确回复） |
| conversion_count | BIGINT | 咨询后转化数 |
| conversion_rate | DECIMAL(5,4) | 咨询后转化率 |
| invalid_rate | DECIMAL(5,4) | 无效咨询占比 |

## 需要产出的查询结果

1. **会话总览**：按 stat_date 分组，有效会话数/咨询用户数/平均时长/平均首次响应/解决率
2. **咨询类型分布**：按 chat_type 分组，各类会话数/占比/平均时长/转化率
3. **时段热力**：按 HOUR(chat_start_time) 分组，每小时会话数/咨询用户数
4. **咨询转化**：按 chat_type 分组，各类咨询后加购率/下单成交率
5. **热门咨询商品**：按 product_id 分组，咨询次数 TOP20
6. **服务质量**：整体无效咨询占比、平均回复次数
7. **品类咨询分布**：按 category_id 分组，各品类咨询量/转化率

## 验收标准

1. HQL 语法正确
2. 问题解决率 = (会话中 merchant_reply_count >= 1 的数量) / 总会话数
3. 咨询转化率 = is_converted=1 的会话数 / 总会话数
4. 首次响应时长过滤异常值（>3600 秒的视为缺失，不计入平均）
5. 执行后行数合理（预计 80-150 行）
