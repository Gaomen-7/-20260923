# 04 — 第五步保存完成：Redis→DB 持久化 + 缓存清理

**What to build:** 用户在SKU设置页点击"保存并下一步"后，系统从Redis读取全部四步数据，在一个事务中写入5张数据库表，成功后清除该商品的所有Redis缓存，页面显示发布成功（含商品ID）。这是商品发布流程的闭环。

**Blocked by:** 01 — 数据层奠基（需要实体/Mapper）、03 — 前端SKU设置页（SKU数据格式已确定）

**Status:** ready-for-agent

- [ ] IGoodsDetailService 新增 persistAll(pubKey) 方法：从Redis读取 {pubKey}-base / {pubKey}-goods-attr / {pubKey}-sale-attr / {pubKey}-sku 四步数据
- [ ] persistAll 方法内使用 @Transactional 事务：① 写入 tbl_goods_info（从base缓存取GoodsDetail，publishStatus默认0），获取自增goodsId；② 批量写入 tbl_spu_goods_attr（规格参数，goodsId回填）；③ 批量写入 tbl_sku_info（仅启用的SKU，goodsId回填，skuId自增）；④ 批量写入 tbl_sku_album（每个SKU的图片集，skuId回填）；⑤ 批量写入 tbl_sku_sale_attr_value（每个SKU的销售属性值，skuId回填）
- [ ] IGoodsDetailService 新增 clearCache(pubKey) 方法：删除 {pubKey}-base / {pubKey}-goods-attr / {pubKey}-sale-attr / {pubKey}-sku 四个Redis键
- [ ] PublishGoodsController 新增 POST /PublishGoods/saveComplete?pubKey= 端点：调用 persistAll → 成功后调用 clearCache → 返回 goodsId
- [ ] 新建 PublishComplete.vue 组件，步骤条 active=4（第五步高亮），显示发布成功图标+商品ID+商品名称+"查看商品列表"按钮（跳转商品管理页，路由占位）
- [ ] router/index.js 注册路由：path='/publishComplete/:pubKey', name='publishComplete', component=PublishComplete
- [ ] SetSku.vue 保存成功后跳转 $router.push({name:'publishComplete', params:{pubKey}})
- [ ] 端到端验证：完整走完五步 → 数据库5张表有对应记录（goodsId/skuId正确关联）→ Redis中该pubKey的4个键全部清除 → 页面显示成功
- [ ] 异常处理：持久化失败时事务回滚，Redis缓存不清除，前端显示错误提示并允许重试
