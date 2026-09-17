# 03 — 前端第四步：SKU 设置页（SetSku.vue）端到端

**What to build:** 用户从销售属性页点击"下一步"进入SKU设置页，页面自动根据选中的销售属性生成笛卡尔积SKU表格，用户可编辑每个SKU的名称/标题/价格，可在折叠面板中为每个SKU选择图片、设置折扣和满减，保存后数据进入Redis缓存。

**Blocked by:** 02 — 后端 SKU 支撑接口

**Status:** ready-for-agent

- [ ] pms_publish.js 扩展4个API方法：getSaleAttr(pubKey)、getAlbumList(pubKey)、generateSku(data)、saveSkuCache(data)
- [ ] router/index.js 注册路由：path='/setSku/:pubKey', name='setSku', component=SetSku
- [ ] 新建 SetSku.vue 组件，步骤条 el-steps active=3（第四步高亮）
- [ ] 页面 mounted 时：从路由参数取 pubKey → 调用 getSaleAttr 读取销售属性选中值 → 调用 generateSku 生成SKU行 → 渲染表格
- [ ] SKU表格列：启用复选框（el-checkbox，可取消个别SKU）、动态属性列（根据销售属性名动态生成，如"颜色"/"容量"）、SKU名称（可编辑输入框，默认自动拼接属性值）、标题（可编辑）、副标题（可编辑）、价格（可编辑数字输入框）、展开按钮（>）
- [ ] 折叠面板使用 ElementUI el-collapse 手风琴模式，每行展开后显示：SKU图片选择（从getAlbumList返回的图集中多选，可设默认图，排除主图）、折扣设置（满X件打Y折，可添加多条）、满减设置（满X元减Y元，可添加多条）
- [ ] 页面底部"上一步"按钮返回销售属性页，"保存并下一步"按钮调用 saveSkuCache 保存到 Redis，成功后跳转第五步（路由占位，待04实现）
- [ ] SetSaleAttr.vue 的 saveSaleAttrData 方法中，保存成功后的 console.log 替换为 $router.push({name:'setSku', params:{pubKey}})
- [ ] 端到端验证：走完前三步 → 进入SKU页 → 自动生成SKU行 → 编辑价格 → 展开某行选图片 → 保存 → Redis中 {pubKey}-sku 有完整数据
