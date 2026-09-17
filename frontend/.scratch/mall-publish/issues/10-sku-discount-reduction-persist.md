# 10 — SKU 折扣/满减持久化

**What to build:** SKU设置页折叠面板中设置的折扣和满减规则，在第五步持久化时写入新增的SKU折扣表和满减表，而非仅存在Redis中。商品详情页/订单系统后续可读取这些规则。

**Blocked by:** 04 — 第五步保存完成（持久化流程已建立）、03 — 前端SKU设置页（折扣/满减UI已存在）

**Status:** ready-for-agent

- [ ] 新建 SkuDiscount 实体（@TableName="tbl_sku_discount"），字段：id/skuId/minQuantity/discountRate（如0.8表示8折）/createTime
- [ ] 新建 SkuReduction 实体（@TableName="tbl_sku_reduction"），字段：id/skuId/fullPrice/reducePrice/createTime
- [ ] 新建对应 Mapper 接口
- [ ] 数据库创建两张表
- [ ] 03中SetSku.vue折叠面板的折扣/满减数据结构确认：折扣为数组[{minQuantity, discountRate}]，满减为数组[{fullPrice, reducePrice}]，随SKU数据一起存入Redis的 {pubKey}-sku
- [ ] 04中persistAll方法扩展：写入tbl_sku_info后，遍历每个SKU的折扣规则批量写入tbl_sku_discount，遍历满减规则批量写入tbl_sku_reduction，skuId回填
- [ ] clearCache 无需变更（Redis键不变）
- [ ] GoodsController 新增 GET /Goods/skuPromotion/{skuId} 接口：查询某SKU的折扣列表+满减列表，供后续展示使用
- [ ] 验证：在SKU设置页为某SKU设置"满2件打8折"和"满1000减100" → 完成发布 → 数据库tbl_sku_discount和tbl_sku_reduction中有对应记录 → skuPromotion接口可查询到
