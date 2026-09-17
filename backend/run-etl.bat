@echo off
chcp 65001 >nul
rem ============================================================
rem 工单06：第二阶段 ETL 一键执行参考脚本
rem 注意：答辩建议手动分步执行以展示每步输出。
rem Docker 不在 PATH 时，先执行：
rem   set PATH=%PATH%;C:\Program Files\Docker\Docker\resources\bin
rem ============================================================
set PATH=%PATH%;C:\Program Files\Docker\Docker\resources\bin
cd /d E:\space\Project\test\mall-sys

echo === 第二阶段 ETL 全流程 ===
echo.

echo [1/5] 执行 Hive ETL 清洗（4张表）...
docker exec hive-server beeline -u "jdbc:hive2://localhost:10000/;auth=noSasl" -n root -f /tmp/phase2_behavior_etl.hql
docker exec hive-server beeline -u "jdbc:hive2://localhost:10000/;auth=noSasl" -n root -f /tmp/phase2_order_etl.hql
docker exec hive-server beeline -u "jdbc:hive2://localhost:10000/;auth=noSasl" -n root -f /tmp/phase2_review_etl.hql
docker exec hive-server beeline -u "jdbc:hive2://localhost:10000/;auth=noSasl" -n root -f /tmp/phase2_chat_etl.hql
echo ETL 清洗完成。
echo.

echo [2/5] 导出 Hive ADS 表为 CSV...
docker exec hive-server beeline -u "jdbc:hive2://localhost:10000/;auth=noSasl" -n root -f /tmp/phase2_export_csv.hql
echo 导出完成。
echo.

echo [3/5] 重命名并从容器拷贝 CSV 到宿主机...
if not exist phase2-csv mkdir phase2-csv
docker exec hive-server bash -c "for t in ads_user_behavior ads_order_analysis ads_review_analysis ads_chat_analysis; do cp /tmp/phase2-csv/$t/000000_0 /tmp/phase2-csv/$t.csv; done"
docker cp hive-server:/tmp/phase2-csv/ads_user_behavior.csv phase2-csv\
docker cp hive-server:/tmp/phase2-csv/ads_order_analysis.csv phase2-csv\
docker cp hive-server:/tmp/phase2-csv/ads_review_analysis.csv phase2-csv\
docker cp hive-server:/tmp/phase2-csv/ads_chat_analysis.csv phase2-csv\
echo 拷贝完成。
echo.

echo [4/5] 编译项目...
call mvn compile -q
echo 编译完成。
echo.

echo [5/5] 执行 MySQL 落地（更简单的方式：在 IDEA 中直接运行 com.gec.loader.MysqlLoader 的 main 方法）...
java -cp "target/classes;%USERPROFILE%\.m2\repository\mysql\mysql-connector-java\*\mysql-connector-java-*.jar" com.gec.loader.MysqlLoader
echo 落地完成。
echo.
echo === 全流程结束 ===
pause
