# 工单 07：SpringBoot 分析接口

**状态**：待执行
**依赖**：工单 06（MySQL 表有数据）
**预计时间**：40 分钟
**执行席**：atomcode CLI（后端项目 `E:\space\Project\test\mall-sys`）

## 交付物

### 1. 实体类（4 个）

位置：`com.gec.domain.entity`（或新建 `domain/analysis` 包）
- `UserBehaviorStat.java` → 对应 `ads_user_behavior`
- `OrderAnalysisStat.java` → 对应 `ads_order_analysis`
- `ReviewAnalysisStat.java` → 对应 `ads_review_analysis`
- `ChatAnalysisStat.java` → 对应 `ads_chat_analysis`

### 2. Mapper 接口 + XML（4 个）

位置：`com.gec.dao` + `resources/mapper/`
- 每个 Mapper 提供：按 dimension_type 查询、按日期范围查询、TOP N 查询
- XML 写在 `resources/mapper/analysis/` 下

### 3. Controller（4 个）

位置：`com.gec.controller.analysis`（新建包）

**BehaviorAnalysisController**（`/analysis/behavior`）：
- `GET /source` — 流量入口饼图+柱状图数据
- `GET /trend` — 行为趋势折线图（按日期+行为类型）
- `GET /keywords` — 热搜关键词榜单
- `GET /funnel` — 转化漏斗数据
- `GET /heatmap` — 时段热力图
- `GET /topProducts` — 热门商品榜单

**OrderAnalysisController**（`/analysis/order`）：
- `GET /overview` — 销售指标卡片
- `GET /trend` — 销售趋势图
- `GET /category` — 品类销售结构饼图
- `GET /return` — 退换货分析看板
- `GET /priceRange` — 客单价直方图
- `GET /hourly` — 时段销售

**ReviewAnalysisController**（`/analysis/review`）：
- `GET /overview` — 评价指标卡片
- `GET /levelDist` — 评价等级饼图
- `GET /keywords` — 关键词词云数据（好评/差评分开）
- `GET /categoryCompare` — 品类口碑对比柱状图
- `GET /trend` — 口碑趋势折线图
- `GET /topProducts` — 商品口碑榜

**ChatAnalysisController**（`/analysis/chat`）：
- `GET /overview` — 会话指标卡片
- `GET /typeDist` — 咨询类型饼图
- `GET /heatmap` — 咨询时段热力图
- `GET /conversion` — 咨询转化柱状图
- `GET /topProducts` — 热门咨询商品榜

### 4. 统一响应

所有接口返回 `R`（复用第一阶段的 R 封装），数据放在 `data` 字段。

## 验收标准

1. `mvn compile` 通过
2. 每个 Controller 至少 5 个端点，共 20+ 端点
3. 接口支持 `startDate` / `endDate` 可选参数（默认查全部）
4. 用 Postman 或浏览器测试，返回 JSON 格式正确，中文无乱码
5. 榜单接口支持 `limit` 参数（默认 10/20）

## 给执行席的指令要点

- Controller 只做参数转发，查询逻辑放 Mapper XML
- 不新建 Service 层（课程作业级，Controller 直接调 Mapper，与第一阶段部分 Controller 风格一致）
- 实体类字段与 MySQL 表字段一一对应，用 MyBatis-Plus 注解
- 日期参数用 `@RequestParam(required = false)` 接收
- 所有接口路径前缀 `/analysis/`，与第一阶段业务接口区分
