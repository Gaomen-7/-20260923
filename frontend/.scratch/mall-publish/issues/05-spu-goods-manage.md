# 05 — SPU 商品管理页（列表+筛选+上架）

**What to build:** 管理员可在商品管理页查看所有已发布的商品（SPU），按分类/品牌/状态/关键词筛选，可对商品执行上架/下架操作，可查看商品的规格参数。

**Blocked by:** 04 — 第五步保存完成（需要持久化数据才能端到端演示）

**Status:** ready-for-agent

- [ ] 新建 GoodsController，@RequestMapping("/Goods")
- [ ] GoodsController 新增 GET /Goods/list 分页查询接口：支持 categoryId/brandId/publishStatus/keyword 筛选参数，使用 MyBatis-Plus Page + QueryWrapper，返回 IPage<GoodsInfo>
- [ ] GoodsController 新增 POST /Goods/updateStatus 接口：接收 goodsId + publishStatus，更新上架状态
- [ ] GoodsController 新增 GET /Goods/detail/{goodsId} 接口：联表查询商品基本信息 + 规格参数列表（tbl_spu_goods_attr），用于规格查看弹窗
- [ ] 新建 pms_goods.js API 文件：listGoods(params)、updateStatus(data)、getGoodsDetail(goodsId)
- [ ] 新建 GoodsManage.vue 组件：筛选栏（分类下拉/品牌下拉/状态下拉/关键词输入框/搜索按钮/重置按钮）+ el-table（复选框列/id/商品名称/描述/分类名称/品牌名称/重量/上架状态标签/创建时间/修改时间/操作列）
- [ ] 操作列：上架/下架按钮（根据当前状态切换，调用 updateStatus 后刷新列表）、规格按钮（弹窗显示该商品的规格参数列表）
- [ ] 分页：el-pagination，10条/页，支持页码切换和每页条数
- [ ] router/index.js 注册路由：path='/goodsManage', name='goodsManage', component=GoodsManage
- [ ] PublishComplete.vue 的"查看商品列表"按钮跳转 /goodsManage
- [ ] 验证：发布至少1个商品后进入商品管理页 → 列表显示该商品 → 筛选功能生效 → 点击上架后状态变为"上架"标签
