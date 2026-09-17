# 工单 03：Hive 清洗 — 用户订单数据分析

**状态**：待执行
**依赖**：工单 01
**预计时间**：30 分钟
**执行席**：atomcode CLI

## 交付物

文件：`E:\space\Project\test\mall-sys\phase2_order_etl.hql`

## 清洗规则（对应需求文档三(二)1）

1. **异常过滤**：剔除 actual_amount=0、order_id 重复、测试订单
2. **状态规整**：order_status 统一为 6 类（待支付/已支付/已发货/已完成/已取消/已退款），return_status 统一为 4 类
3. **去重补全**：去除重复退换货记录，补全品类维度
4. **订单拆分**：多商品订单已在生成器中拆为单商品明细（每行一个 product_id）
5. **退换货关联**：is_return=1 的记录关联 return_reason/return_amount/return_time

## ADS 宽表 `ads_order_analysis` 设计

| 字段 | 类型 | 说明 |
|---|---|---|
| stat_date | STRING | 统计日期 |
| dimension_type | STRING | 维度：overview/category/source/pay_method/hour/return_reason/price_range |
| dimension_value | STRING | 维度值 |
| order_count | BIGINT | 订单总数 |
| paid_order_count | BIGINT | 成交订单数 |
| total_amount | DECIMAL(12,2) | 成交金额 |
| avg_order_value | DECIMAL(10,2) | 客单价 |
| pay_conversion_rate | DECIMAL(5,4) | 支付转化率 |
| cancel_count | BIGINT | 取消订单数 |
| return_count | BIGINT | 退换货订单数 |
| return_rate | DECIMAL(5,4) | 退换货率 |
| return_amount | DECIMAL(12,2) | 退款金额 |

## 需要产出的查询结果

1. **成交总览**：按 stat_date 分组，订单总数/成交数/成交金额/客单价/支付转化率/取消数/退换货数
2. **销售结构**：按 category_id 分组，各品类销售额/销量占比
3. **渠道分析**：按 order_source 分组，各渠道成交占比
4. **支付方式**：按 pay_method 分组，各支付方式使用率
5. **订单流失**：按 order_status=已取消 分组，取消订单数/流失金额/超时取消占比
6. **退换货分析**：按 category_id 分组退换货率；按 return_reason 分组退换货原因占比；退换货处理时长（finish_time - return_time）
7. **客单价分布**：按 actual_amount 分桶（0-50/50-100/100-200/200-500/500+），统计各区间订单数
8. **时段销售**：按 HOUR(create_time) 分组，每小时成交订单数/成交金额

## 验收标准

1. HQL 语法正确
2. 客单价分桶用 `CASE WHEN` 实现
3. 退换货率 = 退换货订单数 / 成交订单数，除零保护
4. 支付转化率 = 已支付订单数 / 订单总数
5. 执行后行数合理（预计 150-300 行）
