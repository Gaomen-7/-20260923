# 工单 06 Code Review 方案 — CSV 中转 + MySQL 落地

> 本文档为检查席审查方案，基于双轴审查法（Standards + Spec）。
> 审查范围：工单06的3项交付物
> 预计审查时间：15 分钟

---

## 一、审查范围

| # | 交付物 | 路径 | 行数 |
|---|---|---|---|
| 1 | Java 落地程序 | `src/main/java/com/gec/loader/MysqlLoader.java` | 193 |
| 2 | Hive 导出脚本 | `phase2_export_csv.hql` | 41 |
| 3 | 一键执行脚本 | `run-etl.bat` | 49 |

**前置条件**：检查席需确认 `mvn compile` 已通过，4张MySQL表已创建，MysqlLoader已运行且4张表有数据。

---

## 二、Standards 轴（代码质量与工程规范）

### 2.1 高优先级（必须修复）

| # | 检查项 | 检查方法 | 通过标准 |
|---|---|---|---|
| S1 | **SQL 注入风险** | 检查 `loadTable` 中 `tableName` 和 `columns` 是否来自外部输入 | tableName/columns 均来自硬编码 `TABLES` 常量，无外部拼接风险。`TRUNCATE TABLE ` + tableName 可接受（常量来源） |
| S2 | **资源泄漏** | 检查 Connection / Statement / PreparedStatement / BufferedReader 是否全部在 try-with-resources 中 | Connection（第86行）、Statement（第90行）、BufferedReader+PreparedStatement（第109-111行）均使用 try-with-resources，无泄漏 |
| S3 | **事务一致性** | 检查 TRUNCATE 和批量 INSERT 是否在同一事务中 | conn.setAutoCommit(false)（第87行），TRUNCATE 在事务内（第91行），全部 INSERT 后 conn.commit()（第146行）。注意：MySQL 的 TRUNCATE TABLE 是隐式提交，会中断当前事务——需确认是否影响数据一致性 |
| S4 | **批量执行异常处理** | 检查 executeBatch 失败时是否回滚 | executeBatch 在 try 块内，异常会向上抛出，Connection 的 try-with-resources 会关闭连接（未显式 rollback，但连接关闭时未提交的事务会回滚）。可接受但建议显式 rollback |

### 2.2 中优先级（建议修复）

| # | 检查项 | 检查方法 | 通过标准 |
|---|---|---|---|
| S5 | **驱动类名已废弃** | 第59行 `Class.forName("com.mysql.jdbc.Driver")` | `com.mysql.jdbc.Driver` 已废弃，建议改为 `com.mysql.cj.jdbc.Driver`。但当前可用，且有 catch 降级到 SPI 自动发现，可接受 |
| S6 | **硬编码配置** | 检查 CSV_DIR / DB_URL / DB_USER / DB_PASS 是否硬编码 | 全部硬编码（第22-27行）。课程作业可接受，但建议从 application.yml 或命令行参数读取。标注为"已知限制"即可 |
| S7 | **空密码安全** | DB_PASS = "" | 本地开发环境无密码，课程作业可接受。不阻断 |
| S8 | **Object[][] 类型安全** | 第31行 `TABLES` 用 `Object[][]` 存储 String 和 String[] | 运行时强制转换（第68行），无编译期类型安全。建议用静态内部类或 record。课程作业级别可接受 |
| S9 | **parseCsvLine 边界情况** | 检查未闭合的双引号、行尾逗号 | 未闭合引号：inQuotes 一直为 true，最后一个字段会包含剩余全部内容，不会崩溃。行尾逗号：最后一个空字段会被正确加入（第190行）。可接受 |
| S10 | **sum() 方法 SUCCESS_NO_INFO 处理** | 第152-158行 | `v >= 0 ? v : 1` 将 SUCCESS_NO_INFO(-2) 和 EXECUTE_FAILED(-3) 都按1行计。EXECUTE_FAILED 应按0计或抛出异常。但 executeBatch 失败会抛 BatchUpdateException，不会走到这里。可接受 |

### 2.3 低优先级（可选优化）

| # | 检查项 | 说明 |
|---|---|---|
| S11 | **System.out 日志** | 全部用 System.out.println，无日志框架。独立工具类可接受 |
| S12 | **魔法数字** | BATCH_SIZE=1000 已提取常量（第28行），无其他魔法数字 |
| S13 | **CSV_DIR 路径分隔符** | 硬编码 Windows 路径 `E:\\...`，跨平台不可移植。课程作业固定 Windows 环境，可接受 |
| S14 | **缺少文件编码 BOM 处理** | Hive 导出的 CSV 无 BOM，UTF-8 读取正确。无需处理 |

---

## 三、Spec 轴（与工单06需求规格一致性）

### 3.1 交付物完整性

| # | 需求项 | 交付状态 | 验证方法 |
|---|---|---|---|
| P1 | `phase2_export_csv.hql` 存在 | ✅ | 文件存在，4条 INSERT OVERWRITE LOCAL DIRECTORY |
| P2 | `MysqlLoader.java` 存在且在 `com.gec.loader` 包 | ✅ | 第1行 package 声明正确 |
| P3 | `run-etl.bat` 存在 | ✅ | 文件存在，5步流程 |
| P4 | MySQL 建表脚本已执行 | ⚠️ 需确认 | 检查席需验证 `project20206` 库中4张 ads_ 表存在 |

### 3.2 Hive 导出脚本（phase2_export_csv.hql）

| # | 检查项 | 通过标准 | 实际状态 |
|---|---|---|---|
| P5 | 4张表全部导出 | ads_user_behavior / ads_order_analysis / ads_review_analysis / ads_chat_analysis | ✅ 4条语句 |
| P6 | 导出格式为 CSV 逗号分隔 | `ROW FORMAT DELIMITED FIELDS TERMINATED BY ','` | ✅ |
| P7 | 导出列为业务列（不含 id） | SELECT 列与 Hive ADS 表业务列一致，不含自增 id | ✅ |
| P8 | 列顺序与 MySQL 表一致 | SELECT 列顺序 = MysqlLoader TABLES 中 columns 顺序 = MySQL 表字段顺序（除id） | 需逐表核对（见下方核对表） |
| P9 | 使用 LOCAL DIRECTORY | 导出到 hive-server 容器本地文件系统 | ✅ `INSERT OVERWRITE LOCAL DIRECTORY` |
| P10 | 导出路径统一 | `/tmp/phase2-csv/{表名}` | ✅ |

**列顺序核对表**：

| 表 | Hive SELECT 列数 | MysqlLoader columns 列数 | MySQL 表业务列数 | 一致？ |
|---|---|---|---|---|
| ads_user_behavior | 10（stat_date, dimension_type, dimension_value, pv, uv, session_count, conversion_count, conversion_rate, bounce_rate, avg_duration） | 10 | 10 | ✅ |
| ads_order_analysis | 12 | 12 | 12 | ✅ |
| ads_review_analysis | 11 | 11 | 11 | ✅ |
| ads_chat_analysis | 12 | 12 | 12 | ✅ |

> 注意：hql 注释中写"11列"/"13列"/"12列"/"13列"（含id的总数），实际 SELECT 业务列为 10/12/11/12。注释与实际列数的表述差异不影响功能，但建议统一注释口径。

### 3.3 Java 落地程序（MysqlLoader.java）

| # | 检查项 | 通过标准 | 实际状态 |
|---|---|---|---|
| P11 | 独立运行（main 方法） | `public static void main(String[] args)`，纯 JDBC，不依赖 Spring | ✅ 第57行 |
| P12 | 先 TRUNCATE 再 INSERT | 每张表先清空再全量写入 | ✅ 第91行 TRUNCATE，第95-105行构建 INSERT |
| P13 | 批量插入每1000行提交 | `BATCH_SIZE = 1000`，`addBatch` + `executeBatch` | ✅ 第28行常量，第132-138行批量逻辑 |
| P14 | 4张表依次处理 | main 方法遍历 TABLES 数组调用 loadTable | ✅ 第66-74行 |
| P15 | 打印导入行数和耗时 | 每张表完成后打印 `[OK] 表名 导入 N 行, 耗时 M ms` | ✅ 第148行 |
| P16 | 中文无乱码 | JDBC URL 含 `useUnicode=true&characterEncoding=utf-8`；CSV 读取指定 UTF-8 | ✅ 第24行 URL，第110行 StandardCharsets.UTF_8 |
| P17 | 空值处理 | CSV 空字符串 / `\N` → SQL NULL | ✅ 第126-127行 `ps.setNull` |
| P18 | CSV 双引号解析 | 含逗号的双引号字段正确解析 | ✅ 第164-192行 parseCsvLine 状态机 |
| P19 | 字段数校验 | CSV 行字段数与 columns 长度不一致时抛出明确异常 | ✅ 第120-123行 |
| P20 | 单表失败不影响其他表 | main 方法 try-catch 包裹 loadTable，失败计数继续 | ✅ 第67-73行 |
| P21 | 数据库连接参数 | URL/user/pass 与 application.yml 一致（localhost:3306/project20206/root/空密码） | ✅ 第23-27行 |
| P22 | 幂等性 | 重复运行结果一致（TRUNCATE + 全量 INSERT） | ✅ 设计上幂等 |

### 3.4 一键执行脚本（run-etl.bat）

| # | 检查项 | 通过标准 | 实际状态 |
|---|---|---|---|
| P23 | 5步流程完整 | ETL清洗 → 导出CSV → 拷贝 → 编译 → 落地 | ✅ 第15-45行 |
| P24 | Docker PATH 处理 | 脚本开头加 Docker bin 到 PATH | ✅ 第9行 |
| P25 | 工作目录切换 | `cd /d E:\space\Project\test\mall-sys` | ✅ 第10行 |
| P26 | phase2-csv 目录自动创建 | `if not exist phase2-csv mkdir phase2-csv` | ✅ 第29行 |
| P27 | 容器内重命名 000000_0 | bash -c for 循环拷贝重命名 | ✅ 第30行 |
| P28 | java -cp 驱动路径 | 含 mysql-connector-java jar 通配符路径 | ⚠️ 第44行用 `*\mysql-connector-java-*.jar`，Windows cmd 不支持通配符展开，可能找不到驱动。但脚本注释建议"在 IDEA 中直接运行 main 方法"，此路径仅作参考 |
| P29 | 答辩手动执行提示 | 脚本头部注释说明建议手动分步执行 | ✅ 第5行 |

---

## 四、数据验证清单（检查席必跑）

### 4.1 行数一致性验证

```sql
-- 在 MySQL 中执行，对比 Hive ADS 表行数
SELECT 'ads_user_behavior' AS tbl, COUNT(*) AS cnt FROM ads_user_behavior
UNION ALL SELECT 'ads_order_analysis', COUNT(*) FROM ads_order_analysis
UNION ALL SELECT 'ads_review_analysis', COUNT(*) FROM ads_review_analysis
UNION ALL SELECT 'ads_chat_analysis', COUNT(*) FROM ads_chat_analysis;
```

**预期结果**：

| 表 | Hive 行数 | MySQL 预期 | 偏差容忍 |
|---|---|---|---|
| ads_user_behavior | 834 | 834 | 0 |
| ads_order_analysis | 766 | 766 | 0 |
| ads_review_analysis | 200 | 200 | 0 |
| ads_chat_analysis | 689 | 689 | 0 |

### 4.2 中文无乱码验证

```sql
-- 抽查各表 dimension_value 中的中文值
SELECT DISTINCT dimension_type, dimension_value FROM ads_user_behavior WHERE dimension_value LIKE '%览%' LIMIT 5;
SELECT DISTINCT dimension_value FROM ads_order_analysis WHERE dimension_value LIKE '%支付%' LIMIT 5;
SELECT DISTINCT dimension_value FROM ads_review_analysis WHERE dimension_value LIKE '%评%' LIMIT 5;
SELECT DISTINCT dimension_value FROM ads_chat_analysis WHERE dimension_value LIKE '%咨询%' LIMIT 5;
```

**通过标准**：中文正常显示，无 `???` 或乱码字符。

### 4.3 DECIMAL 精度验证

```sql
-- 抽查率类字段精度（应为4位小数）
SELECT stat_date, conversion_rate, bounce_rate FROM ads_user_behavior WHERE dimension_type='overview' LIMIT 3;
SELECT stat_date, good_rate, append_rate FROM ads_review_analysis WHERE dimension_type='overview' LIMIT 3;
SELECT stat_date, resolution_rate, conversion_rate, invalid_rate FROM ads_chat_analysis WHERE dimension_type='overview' LIMIT 3;
```

**通过标准**：率类字段保留4位小数，无科学计数法，值在 [0,1] 范围内。

### 4.4 空值验证

```sql
-- 确认空字符串已转为 NULL（而非空字符串）
SELECT COUNT(*) FROM ads_user_behavior WHERE pv IS NULL;
SELECT COUNT(*) FROM ads_order_analysis WHERE total_amount IS NULL;
-- Hive 导出的 DECIMAL NULL 在 CSV 中为空字符串，应转为 SQL NULL
```

### 4.5 幂等性验证

```bash
# 重复运行 MysqlLoader，确认行数不变
# 运行前记录行数 → 运行后再次查询 → 应完全一致
```

---

## 五、审查结论模板

检查席完成审查后，按以下格式输出结论：

```
## 工单06 Code Review 结论

### Standards 轴
- 高优先级：X 项（通过 X / 需修复 X）
- 中优先级：X 项（通过 X / 建议修复 X）
- 低优先级：X 项（可选优化）

### Spec 轴
- 交付物完整性：4/4 ✅
- Hive 导出脚本：P5-P10 共 6 项，通过 X
- Java 落地程序：P11-P22 共 12 项，通过 X
- 一键执行脚本：P23-P29 共 7 项，通过 X
- 数据验证：行数一致 / 中文无乱码 / 精度正确 / 空值正确 / 幂等

### 需修复项（如有）
1. [高/中] 问题描述 → 修复建议

### 结论
✅ 通过 / ⚠️ 有条件通过（需修复X项） / ❌ 不通过
```

---

## 六、已知限制（不阻断审查）

1. **TRUNCATE 隐式提交**：MySQL 的 TRUNCATE TABLE 是 DDL，会隐式提交当前事务。这意味着 TRUNCATE 后如果 INSERT 失败，表已被清空无法回滚。当前场景为全量覆盖重跑，可接受。
2. **run-etl.bat java -cp 通配符**：Windows cmd 不支持 jar 路径通配符展开，命令行直接运行可能找不到驱动。脚本已注释建议用 IDEA 运行 main 方法。
3. **硬编码配置**：CSV 路径和数据库连接参数硬编码，课程作业可接受。
4. **com.mysql.jdbc.Driver 已废弃**：当前可用，建议后续改为 cj 驱动。
