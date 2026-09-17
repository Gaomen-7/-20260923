# 电商项目（后端+前端）

> 课程作业级电商后台管理系统 + 电商用户行为与销售数据分析项目
> 交接日期：2026-09-23

## 项目结构

```
.
├── backend/          # SpringBoot 后端（mall-sys）
├── frontend/         # Vue2 前端（mypro5）
├── docs/             # 交接文档
│   ├── 商城项目交接文档.md
│   └── 第二阶段-电商数据分析交接文档.md
└── README.md
```

## 一、项目概览

### 第一阶段：商城后台管理系统
- **前端**：Vue2 + ElementUI + vue-router + axios + webpack3
- **后端**：SpringBoot 2.3.7 + MyBatis-Plus 3.4.2 + MySQL8 + Redis
- **功能模块**：用户管理、部门管理、品牌管理、类别管理、商品发布（五步流程）、SPU/SKU管理、规格参数管理、销售属性管理、订单管理、优惠券管理、会员管理、库存管理、广告设置

### 第二阶段：电商用户行为与销售数据分析
- **技术栈**：Hadoop + Hive + MySQL + SpringBoot + Vue + ECharts
- **数据流**：Java生成器 → Hive ods层 → ads层聚合 → CSV中转 → MySQL落地 → SpringBoot接口 → Vue+ECharts可视化
- **四大分析模块**：用户全链路行为、用户订单数据、用户评价数据、用户与商家交流

---

## 二、环境配置清单

### 2.1 基础运行环境

| 软件 | 版本要求 | 用途 | 验证命令 |
|------|---------|------|---------|
| JDK | 1.8（必须） | 后端编译运行 | `java -version` |
| Maven | 3.6+ | 后端依赖管理 | `mvn -version` |
| Node.js | 10.x ~ 12.x（webpack3兼容） | 前端构建 | `node -v` |
| npm | 6.x | 前端包管理 | `npm -v` |
| MySQL | 8.0 | 业务数据库 + 分析结果落地 | `mysql --version` |
| Redis | 5.0+ | 缓存（发布流程中间态） | `redis-cli ping` |
| Docker Desktop | 最新稳定版 | Hadoop/Hive 容器化部署 | `docker --version` |
| Docker Compose | v2（Docker Desktop自带） | 多容器编排 | `docker compose version` |

### 2.2 后端配置（backend/）

**端口与路径**
- 服务端口：`8090`
- context-path：`/mall-sys`
- 完整基址：`http://localhost:8090/mall-sys`

**数据库连接**
- 地址：`127.0.0.1:3306`
- 数据库名：`project20206`
- 用户名：`root`
- 密码：（空）

**Redis 连接**
- 地址：`localhost:6379`
- 密码：（空）

**文件上传目录**
- 主图/图集/品牌上传路径：`D:/haida/space/upload/`
- 需提前创建该目录

**启动方式**
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

### 2.3 前端配置（frontend/）

**端口与路径**
- dev server 端口：`8081`
- 访问地址：`http://localhost:8081`

**API 基址**（`src/utils/request.js`）
- `http://localhost:8090/mall-sys`

**启动方式**
```bash
cd frontend
npm install
npm run dev
```

**构建生产版本**
```bash
npm run build
# 输出到 dist/ 目录
```

### 2.4 数据分析环境（第二阶段）

**Docker Hive 环境**
- 部署目录：`backend/docker-hive/`
- 容器清单（5个核心容器）：
  - namenode（HDFS NameNode，端口9870/9000）
  - datanode（HDFS DataNode，端口9864）
  - resourcemanager（YARN，端口8088）
  - nodemanager（YARN NodeManager，端口8042）
  - hive-server（HiveServer2，端口10000/JDBC，10002/Web）
- metastore：Derby 内嵌数据库（已从PostgreSQL迁移）

**启动命令**
```bash
cd backend/docker-hive
docker compose up -d namenode datanode resourcemanager nodemanager hive-server
```

**Hive 连接**
```bash
docker exec -it hive-server beeline -u "jdbc:hive2://localhost:10000/;auth=noSasl" -n root
```

**数据目录**
- 原始数据文件：`D:/haida/space/phase2-data/`
- 挂载到容器：`/tmp/phase2-data`

**Web UI**
- NameNode: http://localhost:9870
- ResourceManager: http://localhost:8088
- HiveServer2: http://localhost:10002

### 2.5 数据库初始化

**第一阶段业务表**
- 建表脚本位于 `backend/` 根目录：
  - `ticket04_create_tables.sql`（商品核心表）
  - `ticket_order_create_tables.sql`（订单表）
  - `ticket_coupon_create_tables.sql`（优惠券表）
  - `ticket_member_create_tables.sql`（会员表）
  - `ticket_advert_create_tables.sql`（广告表）

**第二阶段分析表**
- Hive 建表：`backend/phase2_create_tables.hql`（ODS 4表 + ADS 4表）
- MySQL 建表：`backend/phase2_mysql_tables.sql`（4张宽表）

---

## 三、快速启动顺序

1. **启动 MySQL**，创建数据库 `project20206`，执行建表脚本
2. **启动 Redis**
3. **启动后端**：`cd backend && mvn spring-boot:run`
4. **启动前端**：`cd frontend && npm install && npm run dev`
5. 浏览器访问 `http://localhost:8081`

**数据分析额外步骤**：
6. 启动 Docker Hive 环境（见2.4）
7. 运行 Java 数据生成器生成原始数据
8. Hive 建表 + 加载数据 + ETL 清洗
9. CSV 导出 + MySQL 落地
10. 访问前端分析看板页面

---

## 四、注意事项

1. **JDK 必须为 1.8**：数据生成器使用了 JDK8 兼容写法，高版本 JDK 可能编译失败
2. **Node.js 版本**：webpack3 不兼容 Node 17+，建议使用 Node 10~12
3. **Docker 镜像加速**：国内访问 Docker Hub 需配置镜像加速器，可用 `https://docker.1panel.live`
4. **禁止 `docker compose down -v`**：会删除 Derby metastore 数据和 HDFS 数据，需重新建表
5. **上传目录**：需提前创建 `D:/haida/space/upload/`，否则图片上传失败
6. **MySQL 密码为空**：生产环境部署前务必修改密码

---

## 五、交接文档

详细交接文档见 `docs/` 目录：
- `商城项目交接文档.md` — 第一阶段完整交接（模块、数据库、流程、架构决策）
- `第二阶段-电商数据分析交接文档.md` — 第二阶段完整交接（架构、工单、Docker部署、踩坑记录）
