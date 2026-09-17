# 12 — Dashboard 组件

**What to build:** 系统首页 Dashboard 显示商城核心数据概览，包括商品总数、SKU总数、上架商品数、今日发布数等统计卡片，以及简单的图表（商品分类分布饼图、最近7天发布趋势折线图）。路由图片中曾出现 Dashboard 但未创建。

**Blocked by:** 05 — SPU 商品管理页（需要商品数据统计接口）、06 — SKU管理页（需要SKU数据统计接口）

**Status:** ready-for-agent

- [ ] 新建 DashboardController，@RequestMapping("/Dashboard")
- [ ] DashboardController 新增 GET /Dashboard/statistics 接口：返回统计数据——商品总数、上架商品数、下架商品数、SKU总数、今日新增商品数、今日新增SKU数
- [ ] DashboardController 新增 GET /Dashboard/categoryDist 接口：按分类统计商品数量，返回分类名称+数量（用于饼图）
- [ ] DashboardController 新增 GET /Dashboard/publishTrend 接口：最近7天每天的商品发布数量，返回日期+数量（用于折线图）
- [ ] 新建 pms_dashboard.js API 文件：getStatistics()、getCategoryDist()、getPublishTrend()
- [ ] 新建 Dashboard.vue 组件：顶部4个统计卡片（el-card，商品总数/上架商品/SKU总数/今日新增）+ 下方两栏布局（左侧分类分布饼图 + 右侧最近7天发布趋势折线图）
- [ ] 图表使用 ECharts（需确认项目是否已安装echarts依赖；若未安装，npm install echarts --save，或使用vue-echarts封装）
- [ ] router/index.js 注册路由：path='/', name='dashboard', component=Dashboard（作为首页默认路由）
- [ ] 验证：启动后访问根路径 → 显示Dashboard → 统计卡片数据与数据库一致 → 饼图显示各分类商品占比 → 折线图显示最近7天发布趋势
