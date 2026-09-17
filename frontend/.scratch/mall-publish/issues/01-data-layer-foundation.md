# 01 — 数据层奠基：5张核心表实体+Mapper+命名统一修复

**What to build:** 商品发布流程所需的5张核心数据库表的实体类与Mapper接口全部就绪，现有实体的表名错误得到修复。后续所有持久化操作都基于这一层。

**Blocked by:** None — can start immediately

**Status:** ready-for-agent

- [ ] 新建 GoodsInfo 实体（@TableName="tbl_goods_info"），字段：id/goodsName/goodsDescription/categoryId/brandId/mainImage/weight/publishStatus/createTime/updateTime
- [ ] 新建 SpuGoodsAttr 实体（@TableName="tbl_spu_goods_attr"），字段：id/goodsId/attrId/attrName/attrValue/attrSort
- [ ] 新建 SkuInfo 实体（@TableName="tbl_sku_info"），字段：skuId/goodsId/skuName/skuDesc/categoryId/brandId/skuTitle/skuSubtitle/price/saleCount
- [ ] 新建 SkuAlbum 实体（@TableName="tbl_sku_album"），字段：id/goodsId/skuId/images/defaultImage/createTime/updateTime
- [ ] 新建 SkuSaleAttrValue 实体（@TableName="tbl_sku_sale_attr_value"），字段：id/skuId/attrId/attrName/attrValue/attrSort
- [ ] 为以上5个实体各创建对应的 Mapper 接口（继承 BaseMapper）
- [ ] 修复现有 SpuAlbum 实体的 @TableName 错误（当前指向 "tbl_point_rule"，应改为 "tbl_sku_album"），补充 defaultImage 字段
- [ ] 现有 GoodsDetail 实体补充 publishStatus 字段（Integer，0=下架/1=上架）
- [ ] 后端 Maven 编译通过，所有 Mapper 可被 Spring 注入
