# 06 — SKU 管理页（列表+筛选+预览）

**What to build:** 管理员可在SKU管理页查看所有SKU，按分类/品牌/价格区间/关键词筛选，可预览SKU详情，行展开查看SKU的图片集和销售属性。

**Blocked by:** 04 — 第五步保存完成（需要持久化的SKU数据）

**Status:** ready-for-agent

- [ ] GoodsController 新增 GET /Goods/skuList 分页查询接口：支持 categoryId/brandId/minPrice/maxPrice/keyword 筛选，联表 tbl_sku_info + tbl_sku_album（取default_image），返回 SKU 分页列表（含默认图片URL）
- [ ] GoodsController 新增 GET /Goods/skuDetail/{skuId} 接口：联表查询 SKU 基本信息 + 图片集（tbl_sku_album）+ 销售属性值（tbl_sku_sale_attr_value），用于预览弹窗和行展开
- [ ] pms_goods.js 扩展：listSku(params)、getSkuDetail(skuId)
- [ ] 新建 SkuManage.vue 组件：筛选栏（分类下拉/品牌下拉/价格区间[min~max两个数字输入框]/关键词/搜索/重置）+ el-table
- [ ] 表格列：展开箭头（el-table type="expand"）、复选框、skuId、SKU名称、默认图片（el-image 缩略图，点击可预览大图）、价格、销量、操作列
- [ ] 操作列：预览按钮（弹窗显示SKU详情：图片轮播+销售属性+价格+销量）、更多按钮（下拉菜单：查看商品/编辑[占位]）
- [ ] 行展开区域：显示该SKU的全部图片（el-image 缩略图列表）+ 销售属性键值对列表
- [ ] 分页：el-pagination，10条/页
- [ ] router/index.js 注册路由：path='/skuManage', name='skuManage', component=SkuManage
- [ ] 验证：发布含多个SKU的商品后进入SKU管理页 → 列表显示所有SKU → 价格区间筛选生效 → 点击展开显示图片和属性 → 预览弹窗显示完整详情
