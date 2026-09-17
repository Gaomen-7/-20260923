# 工单 04：Hive 清洗 — 用户评价数据分析

**状态**：待执行
**依赖**：工单 01
**预计时间**：30 分钟
**执行席**：atomcode CLI

## 交付物

文件：`E:\space\Project\test\mall-sys\phase2_review_etl.hql`

## 清洗规则（对应需求文档三(三)1）

1. **无效过滤**：剔除 review_content 为空、重复 review_id、系统默认好评（rating=5 且 content 含"默认好评"）
2. **等级规整**：rating 4-5=好评，3=中评，1-2=差评，新增 `review_level` 字段
3. **关键词提取**：用 Hive `LIKE` 匹配预设关键词词典（质量/物流/服务/性价比/外观/尺码/包装/售后），统计各维度提及次数
4. **维度补全**：关联品类（生成器已含 product_id→category_id 映射）
5. **追评整合**：is_append=是 的记录单独统计追评率/追评好评率

## ADS 宽表 `ads_review_analysis` 设计

| 字段 | 类型 | 说明 |
|---|---|---|
| stat_date | STRING | 统计日期 |
| dimension_type | STRING | 维度：overview/category/keyword/product/rating_level/append |
| dimension_value | STRING | 维度值 |
| total_reviews | BIGINT | 总评价数 |
| good_reviews | BIGINT | 好评数 |
| mid_reviews | BIGINT | 中评数 |
| bad_reviews | BIGINT | 差评数 |
| good_rate | DECIMAL(5,4) | 好评率 |
| avg_rating | DECIMAL(3,2) | 平均评分 |
| keyword_count | BIGINT | 关键词提及次数 |
| append_count | BIGINT | 追评数 |
| append_rate | DECIMAL(5,4) | 追评率 |

## 需要产出的查询结果

1. **评价总览**：按 stat_date 分组，总评价数/好评数/中评数/差评数/好评率/平均评分
2. **等级分布**：按 review_level 分组，各等级评价数/占比
3. **品类口碑**：按 category_id 分组，各品类好评率/平均评分
4. **关键词分析**：好评关键词 TOP30 + 差评关键词 TOP30（用 LIKE 匹配词典，统计提及次数）
5. **维度评价占比**：按关键词维度（质量/物流/服务/性价比/外观）分组，各维度在好评/差评中的提及占比
6. **商品口碑榜**：按 product_id 分组，平均评分 TOP20（高口碑）/ 平均评分 BOTTOM20（低分）
7. **追评分析**：追评数/追评率/追评好评率
8. **口碑趋势**：按 stat_date 分组，每日好评率变化趋势

## 验收标准

1. HQL 语法正确
2. 关键词匹配用 `CASE WHEN review_content LIKE '%质量%' THEN 1 ELSE 0 END` 模式
3. 好评率 = 好评数 / 总评价数，除零保护
4. 平均评分 = AVG(rating)
5. 执行后行数合理（预计 100-200 行）
