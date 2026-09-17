# 02 — 后端 SKU 支撑接口 + 销售属性测试数据

**What to build:** 后端提供SKU生成所需的4个Redis读写接口，数据库中有可用于测试的销售属性数据。前端SKU页可以调用这些接口完成数据加载与保存。

**Blocked by:** 01 — 数据层奠基（实体层就绪；Redis操作本身不强依赖，但统一在奠基后开始）

**Status:** ready-for-agent

- [ ] 数据库插入 category_id=11, attr_type=2 的销售属性测试记录（如：机身颜色[多值:黑色;白色;蓝色]、存储容量[多值:128GB;256GB;512GB]、版本[单值:标准版;Pro版]）
- [ ] IGoodsDetailService 新增 getSaleAttrCache(pubKey) 方法，从 Redis 读取 {pubKey}-sale-attr 的选中值列表
- [ ] IGoodsDetailService 新增 getBaseInfoCache(pubKey) 方法，从 Redis 读取 {pubKey}-base 的基本信息（含图集文件名列表）
- [ ] IGoodsDetailService 新增 saveSkuCache(pubKey, skuData) 方法，写入 Redis key={pubKey}-sku
- [ ] PublishGoodsController 新增 GET /PublishGoods/getSaleAttr?pubKey= 端点，返回销售属性选中值列表
- [ ] PublishGoodsController 新增 GET /PublishGoods/getAlbumList?pubKey= 端点，从base缓存中提取图集文件名列表返回
- [ ] PublishGoodsController 新增 POST /PublishGoods/generateSku 端点，接收销售属性选中值列表，返回笛卡尔积生成的SKU行骨架（每行含属性值组合+默认空字段）
- [ ] PublishGoodsController 新增 POST /PublishGoods/saveSkuCache 端点，接收 pubKey + SKU数据列表，写入 Redis
- [ ] Postman 验证：generateSku 输入3个属性各3个值 → 返回27行SKU骨架；saveSkuCache 后 Redis 中存在 {pubKey}-sku 键
