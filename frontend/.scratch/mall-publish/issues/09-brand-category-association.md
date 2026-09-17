# 09 — 品牌-分类关联表

**What to build:** 商品发布第一步中，选择三级分类后，品牌下拉框动态显示与该分类关联的品牌列表，而非显示全部品牌。需要新建品牌-分类关联表并实现关联查询。

**Blocked by:** 01 — 数据层奠基（需要新建关联表实体）

**Status:** ready-for-agent

- [ ] 新建 BrandCategory 实体（@TableName="tbl_brand_category"），字段：id/brandId/catelogId/brandName/catelogName
- [ ] 新建 BrandCategoryMapper 接口
- [ ] 数据库创建 tbl_brand_category 表并插入测试数据（如 category_id=11 关联品牌：Apple/华为/小米）
- [ ] BrandController 新增 GET /Brand/listByCategory/{categoryId} 接口：通过 tbl_brand_category 联表查询该分类下的品牌列表，返回 id+brandName
- [ ] 前端 PublishBaseInfo.vue：分类级联选择器选中三级分类后，调用 listByCategory(categoryId) 动态加载品牌下拉选项，而非页面加载时加载全部品牌
- [ ] 品牌下拉框在未选择分类时禁用并提示"请先选择分类"；分类变更时清空已选品牌
- [ ] pms_brand.js 中已有 listByCategory 方法（交接文档确认存在），确认其调用路径与后端接口一致
- [ ] 验证：进入发布商品第一步 → 不选分类时品牌下拉禁用 → 选择分类后品牌下拉显示该分类关联的品牌 → 切换分类后品牌列表更新且已选值清空
