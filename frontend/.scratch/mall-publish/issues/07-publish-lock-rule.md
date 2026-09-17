# 07 — 上架锁定业务规则

**What to build:** 已上架的商品信息被锁定，不可修改基本信息和规格参数；下架后的商品允许二次上架（课程项目简化，无需审核流程）。前端在商品管理页对上架状态的商品禁用编辑入口。

**Blocked by:** 05 — SPU 商品管理页

**Status:** ready-for-agent

- [ ] 后端 GoodsController 的更新类接口（如后续新增 updateBaseInfo/updateAttr）增加校验：查询 goodsId 对应的 publishStatus，若为1（上架）则抛出 RuntimeException("上架状态的商品不可修改")
- [ ] 若当前尚无更新接口，在 GoodsController 中预留 updateGoodsBaseInfo 接口骨架并加上架校验，供后续编辑功能使用
- [ ] 下架后二次上架：updateStatus 接口允许 publishStatus 从0→1（二次上架），无需额外审核，保留原商品数据不变
- [ ] 前端 GoodsManage.vue：上架状态（publishStatus=1）的行，操作列中的"编辑"类按钮（如有）设置为 disabled 并添加 el-tooltip 提示"上架状态的商品不可修改，请先下架"
- [ ] 前端 GoodsManage.vue：上架状态的行，"规格"按钮改为只读查看（不可编辑），弹窗标题标注"规格参数（只读）"
- [ ] 验证：将商品上架 → 尝试调用更新接口返回错误信息 → 前端按钮置灰且tooltip可见 → 下架后按钮恢复可用 → 再次上架成功
