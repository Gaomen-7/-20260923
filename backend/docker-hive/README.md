# Docker Hive 部署指南

Hadoop 3.2.1 + Hive 2.3.2 伪分布式，7 个容器。

## 前置条件

- Docker Desktop 已安装并启动（鲸鱼图标变绿）
- 至少 8GB 内存（建议 Docker 分配 4GB+）
- 数据文件已生成：`D:/haida/space/phase2-data/`（4个文件）

## 启动

```bash
cd E:\space\Project\test\mall-sys\docker-hive
docker compose up -d
```

首次启动会拉取 7 个镜像（约 2GB），需 10-20 分钟（取决于网络）。

## 验证启动

```bash
# 查看容器状态，全部 healthy 或 running
docker compose ps

# 查看 Hive Server 日志，出现 "Started HiveServer2" 表示就绪
docker logs hive-server --tail 20
```

Web UI：
- NameNode: http://localhost:9870
- ResourceManager: http://localhost:8088
- HiveServer2: http://localhost:10002

## 连接 Hive（Beeline）

```bash
docker exec -it hive-server beeline -u "jdbc:hive2://localhost:10000" -n root
```

## 执行建表脚本

```bash
# 把建表脚本拷进容器
docker cp E:\space\Project\test\mall-sys\phase2_create_tables.hql hive-server:/tmp/

# 在容器内执行
docker exec -it hive-server beeline -u "jdbc:hive2://localhost:10000" -n root -f /tmp/phase2_create_tables.hql
```

## 加载数据到 Hive

数据文件已通过 volume 映射到容器内 `/tmp/phase2-data/`：

```sql
-- 在 beeline 中执行
LOAD DATA LOCAL INPATH '/tmp/phase2-data/user_behavior.log' OVERWRITE INTO TABLE ods_user_behavior;
LOAD DATA LOCAL INPATH '/tmp/phase2-data/order_data.csv' OVERWRITE INTO TABLE ods_order_data;
LOAD DATA LOCAL INPATH '/tmp/phase2-data/review_data.csv' OVERWRITE INTO TABLE ods_review_data;
LOAD DATA LOCAL INPATH '/tmp/phase2-data/chat_data.csv' OVERWRITE INTO TABLE ods_chat_data;

-- 验证
SELECT COUNT(*) FROM ods_user_behavior;  -- 应返回 100000
```

## 执行 ETL 清洗脚本

```bash
docker cp E:\space\Project\test\mall-sys\phase2_behavior_etl.hql hive-server:/tmp/
docker exec -it hive-server beeline -u "jdbc:hive2://localhost:10000" -n root -f /tmp/phase2_behavior_etl.hql
-- 同理执行 order/review/chat 的 ETL 脚本
```

## 导出 ADS 结果为 CSV

```sql
INSERT OVERWRITE LOCAL DIRECTORY '/tmp/phase2-export/ads_user_behavior'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
SELECT * FROM ads_user_behavior;
```

然后从容器拷出：
```bash
docker cp hive-server:/tmp/phase2-export/ .
```

## 常用命令

```bash
docker compose down          # 停止并删除容器（数据卷保留）
docker compose down -v       # 停止并删除容器和数据卷（清空所有数据）
docker compose logs -f       # 查看所有容器日志
docker exec -it namenode bash   # 进入 namenode 容器
```

## 注意事项

1. 首次启动 hive-metastore 会自动初始化 PostgreSQL 中的 schema，需等 1-2 分钟
2. hive-server 依赖 hive-metastore，会等 metastore 就绪后才启动
3. 数据文件映射是只读的（volume 默认读写，但建议不要在容器内改源文件）
4. 如果 beeline 连接报 "Peer indicated failure"，等 hive-server 完全启动后重试
5. Windows 路径映射用正斜杠 `D:/haida/space/phase2-data`，不要用反斜杠
