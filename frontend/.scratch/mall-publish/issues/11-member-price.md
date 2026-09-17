# 11 — 会员价（黄金/钻石会员价）

**What to build:** SKU设置页折叠面板中可设置黄金会员价和钻石会员价，持久化到新增的SKU会员价表。需要用户/会员微服务提供会员等级信息（课程项目中可简化为本地用户表扩展字段或独立会员表）。

**Blocked by:** 04 — 第五步保存完成（持久化流程）、10 — SKU折扣/满减持久化（折叠面板扩展模式已确立）

**Status:** ready-for-agent

- [ ] 新建 SkuMemberPrice 实体（@TableName="tbl_sku_member_price"），字段：id/skuId/memberLevel（如"gold"/"diamond"）/memberPrice/createTime
- [ ] 新建 SkuMemberPriceMapper 接口
- [ ] 数据库创建 tbl_sku_member_price 表
- [ ] 会员等级来源：确认当前用户表（tbl_user）是否有会员等级字段；若无，在用户表扩展 member_level 字段（varchar，默认普通用户），或新建简化的会员等级字典。课程项目不要求独立微服务，本地实现即可
- [ ] SetSku.vue 折叠面板中新增"会员价"区域：黄金会员价输入框 + 钻石会员价输入框（decimal，可留空表示不设置）
- [ ] 会员价数据随SKU数据一起存入Redis的 {pubKey}-sku
- [ ] persistAll 方法扩展：写入tbl_sku_info后，遍历每个SKU的会员价批量写入tbl_sku_member_price（仅写入非空值），skuId回填
- [ ] GoodsController 新增 GET /Goods/skuMemberPrice/{skuId} 接口：查询某SKU的各等级会员价
- [ ] 验证：在SKU设置页为某SKU设置黄金价=8999、钻石价=8599 → 完成发布 → 数据库tbl_sku_member_price有2条记录 → 接口可查询到
