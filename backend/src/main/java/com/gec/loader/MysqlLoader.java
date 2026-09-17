package com.gec.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单06：将宿主机 phase2-csv 目录下的 4 个 CSV 文件批量写入 MySQL project20206 库。
 * 独立运行（main 方法），纯 JDBC，不依赖 Spring。
 * 流程：TRUNCATE -> 批量 INSERT（每 1000 行 executeBatch），全量覆盖，幂等。
 */
public class MysqlLoader {

    private static final String CSV_DIR = "E:\\space\\Project\\test\\mall-sys\\phase2-csv\\";
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/project20206?useUnicode=true&characterEncoding=utf-8"
                    + "&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final int BATCH_SIZE = 1000;

    // 表配置数组：{CSV文件名, 表名, 列名数组}
    private static final Object[][] TABLES = {
        {
            "ads_user_behavior.csv", "ads_user_behavior",
            new String[]{"stat_date", "dimension_type", "dimension_value", "pv", "uv",
                    "session_count", "conversion_count", "conversion_rate", "bounce_rate", "avg_duration"}
        },
        {
            "ads_order_analysis.csv", "ads_order_analysis",
            new String[]{"stat_date", "dimension_type", "dimension_value", "order_count", "paid_order_count",
                    "total_amount", "avg_order_value", "pay_conversion_rate", "cancel_count",
                    "return_count", "return_rate", "return_amount"}
        },
        {
            "ads_review_analysis.csv", "ads_review_analysis",
            new String[]{"stat_date", "dimension_type", "dimension_value", "total_reviews", "good_reviews",
                    "mid_reviews", "bad_reviews", "good_rate", "avg_rating",
                    "keyword_count", "append_count", "append_rate"}
        },
        {
            "ads_chat_analysis.csv", "ads_chat_analysis",
            new String[]{"stat_date", "dimension_type", "dimension_value", "session_count", "user_count",
                    "avg_duration", "avg_first_response", "avg_reply_count", "resolution_rate",
                    "conversion_count", "conversion_rate", "invalid_rate"}
        }
    };

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // JDBC 4.0 可自动发现驱动，此处仅提示
            System.out.println("[warn] 未显式找到 com.mysql.jdbc.Driver，尝试 SPI 自动发现: " + e.getMessage());
        }

        int fail = 0;
        for (Object[] conf : TABLES) {
            try {
                loadTable((String) conf[0], (String) conf[1], (String[]) conf[2]);
            } catch (Exception e) {
                fail++;
                System.out.println("[ERROR] " + conf[1] + " 导入失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println(fail == 0 ? "=== 全部 4 张表导入完成 ===" : "=== 完成，但有 " + fail + " 张表失败 ===");
    }

    public static void loadTable(String csvFile, String tableName, String[] columns) throws Exception {
        long start = System.currentTimeMillis();

        File file = new File(CSV_DIR + csvFile);
        if (!file.exists()) {
            throw new IllegalStateException("CSV 文件不存在: " + file.getAbsolutePath());
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);

            // 1. 先清后写
            try (Statement st = conn.createStatement()) {
                st.execute("TRUNCATE TABLE " + tableName);
            }

            // 2. 构建 INSERT SQL
            StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
            StringBuilder marks = new StringBuilder();
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    sql.append(',');
                    marks.append(',');
                }
                sql.append(columns[i]);
                marks.append('?');
            }
            sql.append(") VALUES (").append(marks).append(')');

            // 3. 逐行读取并批量插入
            int total = 0;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                 PreparedStatement ps = conn.prepareStatement(sql.toString())) {

                String line;
                List<String> batch = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] fields = parseCsvLine(line);
                    if (fields.length != columns.length) {
                        throw new IllegalStateException(tableName + " 第 " + (total + 1)
                                + " 行字段数不符: 期望 " + columns.length + " 实际 " + fields.length + " -> " + line);
                    }
                    for (int i = 0; i < columns.length; i++) {
                        String v = fields[i];
                        if (v == null || v.isEmpty() || "\\N".equals(v)) {
                            ps.setNull(i + 1, java.sql.Types.NULL);
                        } else {
                            ps.setObject(i + 1, v);
                        }
                    }
                    ps.addBatch();
                    batch.add(line);
                    if (batch.size() >= BATCH_SIZE) {
                        int[] r = ps.executeBatch();
                        total += sum(r);
                        batch.clear();
                    }
                }
                if (!batch.isEmpty()) {
                    int[] r = ps.executeBatch();
                    total += sum(r);
                }
            }

            conn.commit();
            long cost = System.currentTimeMillis() - start;
            System.out.println("[OK] " + tableName + " 导入 " + total + " 行, 耗时 " + cost + " ms");
        }
    }

    private static int sum(int[] arr) {
        int s = 0;
        for (int v : arr) {
            s += (v >= 0 ? v : 1); // SUCCESS_NO_INFO(-2) 等按 1 行计
        }
        return s;
    }

    /**
     * CSV 行解析：双引号内的逗号不作为分隔符，"" 表示转义的双引号字符。
     * Hive 导出的普通字段（无引号）也兼容。
     */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields.toArray(new String[0]);
    }
}
