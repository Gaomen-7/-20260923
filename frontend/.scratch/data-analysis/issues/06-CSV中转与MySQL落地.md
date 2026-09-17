# 工单 06：CSV 中转 + MySQL 落地程序

**状态**：待执行
**依赖**：工单 02-05（Hive ADS 表有数据）
**预计时间**：30 分钟
**执行席**：atomcode CLI（后端项目）

## 交付物

### 1. Hive 导出脚本 `phase2_export_csv.hql`

位置：`E:\space\Project\test\mall-sys\phase2_export_csv.hql`

4 条 INSERT OVERWRITE DIRECTORY 语句，将 ADS 表导出为 CSV：
```sql
INSERT OVERWRITE DIRECTORY '/tmp/phase2/ads_user_behavior'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
SELECT * FROM ads_user_behavior;
-- 同理导出其他 3 张表
```

### 2. Java 落地程序 `MysqlLoader.java`

位置：`com.gec.loader.MysqlLoader`（新建 `loader` 包）

功能：
- 读取本地 CSV 文件（从 HDFS 导出到本地的路径，或直接读 Hive 导出目录）
- JDBC 批量写入 MySQL（先 TRUNCATE 表，再批量 INSERT）
- 4 张表依次处理，每张表打印导入行数和耗时
- 数据库连接复用第一阶段的 `application.yml` 配置

**核心方法**：
```java
public static void loadTable(String csvPath, String tableName, String[] columns)
// 1. TRUNCATE TABLE tableName
// 2. 逐行读取 CSV，组装 INSERT 语句
// 3. 每 1000 行执行一次批量 INSERT（addBatch + executeBatch）
// 4. 打印：表名 + 导入行数 + 耗时毫秒
```

### 3. 一键执行脚本 `run-etl.bat`

位置：`E:\space\Project\test\mall-sys\run-etl.bat`

按顺序执行：
1. `hive -f phase2_behavior_etl.hql`（4 个 ETL 脚本依次执行）
2. `hive -f phase2_export_csv.hql`
3. `java -cp target/classes com.gec.loader.MysqlLoader`

> 注意：手动分步执行，bat 仅作为参考脚本，答辩时可逐步手动执行展示每步输出。

## 验收标准

1. `mvn compile` 通过
2. MysqlLoader 运行后，MySQL 4 张 ads_ 表有数据，行数与 Hive ADS 表一致
3. 中文数据无乱码（JDBC URL 加 `useUnicode=true&characterEncoding=utf8`）
4. 批量插入每 1000 行提交一次，内存不溢出
5. 控制台打印每张表的导入行数和耗时

## 给执行席的指令要点

- 用 `java.io.BufferedReader` 读 CSV，不要用第三方 CSV 解析库
- CSV 字段可能含逗号（用双引号包裹），需简单处理双引号字段
- JDBC 用 `PreparedStatement` + `addBatch`
- 数据库连接参数从 `application.yml` 读取，或直接硬编码（课程作业可接受）
