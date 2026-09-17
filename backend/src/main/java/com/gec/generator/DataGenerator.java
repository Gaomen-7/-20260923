package com.gec.generator;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 第二阶段数据分析 - 模拟数据生成器
 * 生成 4 类原始数据文件到 D:/haida/space/phase2-data/：
 *   user_behavior.log  行为日志      100,000 行（Tab 分隔）
 *   order_data.csv     订单数据       10,000 行（逗号分隔）
 *   review_data.csv    评价数据        5,000 行（逗号分隔）
 *   chat_data.csv      交流数据        2,000 行（逗号分隔）
 * 仅使用 JDK 自带类，UTF-8 编码输出。
 */
public class DataGenerator {

    private static final String OUTPUT_DIR = "D:/haida/space/phase2-data/";
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final Random RAND = new Random(20260915L);

    /* ============ 公共基础数据 ============ */

    /** 商品价格映射表 */
    private static final Map<String, BigDecimal> PRODUCT_PRICE = new LinkedHashMap<>();
    static {
        String[][] prices = {
            {"P10001","299"},{"P10002","1299"},{"P10003","199"},{"P10004","599"},{"P10005","89.9"},
            {"P10006","129"},{"P10007","459"},{"P10008","59.9"},{"P10009","899"},{"P10010","349"},
            {"P10011","1599"},{"P10012","79"},{"P10013","259"},{"P10014","1299"},{"P10015","1999"},
            {"P10016","159"},{"P10017","399"},{"P10018","89"},{"P10019","699"},{"P10020","249"}
        };
        for (String[] p : prices) {
            PRODUCT_PRICE.put(p[0], new BigDecimal(p[1]));
        }
    }

    private static final String[] CITIES = {"北京","上海","广州","深圳","杭州","成都","武汉","西安"};
    private static final String[] DEVICES = {"Android","iOS","Web","平板"};
    private static final String[] SEARCH_KEYWORDS = {
        "无线蓝牙耳机","运动鞋","手机壳","保温杯","笔记本电脑","充电宝","数据线","耳机","手表","背包"
    };
    private static final String[] SOURCES = {
        "首页推荐","站内搜索","搜索页","详情页","购物车","直播带货","广告落地页","商品详情分享","分类页","活动页"
    };

    /* ============ 主入口 ============ */

    public static void main(String[] args) throws IOException {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long start = System.currentTimeMillis();
        int n1 = generateUserBehavior(OUTPUT_DIR + "user_behavior.log", 100000);
        int n2 = generateOrderData(OUTPUT_DIR + "order_data.csv", 10000);
        int n3 = generateReviewData(OUTPUT_DIR + "review_data.csv", 5000);
        int n4 = generateChatData(OUTPUT_DIR + "chat_data.csv", 2000);
        System.out.println("生成完成，耗时 " + (System.currentTimeMillis() - start) + " ms");
        System.out.println(OUTPUT_DIR + "user_behavior.log : " + n1 + " 行");
        System.out.println(OUTPUT_DIR + "order_data.csv    : " + n2 + " 行");
        System.out.println(OUTPUT_DIR + "review_data.csv   : " + n3 + " 行");
        System.out.println(OUTPUT_DIR + "chat_data.csv     : " + n4 + " 行");
    }

    /* ============ 通用工具方法 ============ */

    private static String randUser() {
        return "U" + (10001 + RAND.nextInt(500));
    }

    private static String randProduct() {
        return "P" + (10001 + RAND.nextInt(20));
    }

    private static BigDecimal productPrice(String productId) {
        return PRODUCT_PRICE.get(productId);
    }

    /** 按权重随机选取：weights 与 options 等长，权重之和不必为 100 */
    private static <T> T weightedPick(T[] options, int[] weights) {
        int total = 0;
        for (int w : weights) {
            total += w;
        }
        int r = RAND.nextInt(total);
        for (int i = 0; i < options.length; i++) {
            r -= weights[i];
            if (r < 0) {
                return options[i];
            }
        }
        return options[options.length - 1];
    }

    /** [min, max] 随机整数 */
    private static int randInt(int min, int max) {
        return min + RAND.nextInt(max - min + 1);
    }

    /** 在 startDate ~ endDate 之间随机一个 LocalDateTime */
    private static LocalDateTime randDateTime(LocalDate start, LocalDate end) {
        long days = end.toEpochDay() - start.toEpochDay();
        // JDK8 的 Random 没有 nextLong(bound)，拆成两个nextInt拼long
        long offset = ((long) RAND.nextInt(1 << 20) << 20) | RAND.nextInt(1 << 20);
        LocalDate d = start.plusDays(offset % (days + 1));
        int hour = RAND.nextInt(24);
        int minute = RAND.nextInt(60);
        int second = RAND.nextInt(60);
        return d.atTime(hour, minute, second);
    }

    /** 在指定日期段内随机时间，且命中高峰时段小时集合时权重加倍 */
    private static LocalDateTime randPeakDateTime(LocalDate start, LocalDate end, int[] peakHours) {
        while (true) {
            LocalDateTime t = randDateTime(start, end);
            for (int h : peakHours) {
                if (t.getHour() == h) {
                    if (RAND.nextInt(2) == 0) {
                        return t; // 高峰时段 50% 概率直接返回（等效权重加倍）
                    }
                    break;
                }
            }
            if (RAND.nextInt(2) == 0) {
                return t; // 非高峰时段 50% 概率返回
            }
        }
    }

    /** CSV 字段：含中文逗号则用双引号包裹，内部双引号转义为两个双引号 */
    private static String csv(String field) {
        if (field != null && field.contains("，")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field == null ? "" : field;
    }

    /** 生成用户与日期关联的 session 序号缓存：user -> date -> 已用序号数 */
    private static final Map<String, Map<String, Integer>> SESSION_SEQ = new HashMap<>();

    /** 同一用户同一天共享 1-3 个 session，返回 SESS + yyyyMMdd + 3位序号 */
    private static String nextSessionId(String userId, LocalDate date) {
        String day = date.format(DF);
        Map<String, Integer> dayMap = SESSION_SEQ.get(userId);
        if (dayMap == null) {
            dayMap = new HashMap<>();
            SESSION_SEQ.put(userId, dayMap);
        }
        Integer used = dayMap.get(day);
        if (used == null) {
            used = 0;
            // 预分配该用户该天共 1-3 个 session
            dayMap.put(day, 0);
            dayMap.put(day + "#total", randInt(1, 3));
        }
        int total = dayMap.get(day + "#total");
        int seq = Math.min(used, total - 1);
        dayMap.put(day, used + 1);
        return "SESS" + day + String.format("%03d", seq + 1);
    }

    /* ============ 1. 行为日志 user_behavior.log（Tab 分隔，12 列） ============ */

    private static final String[] BEHAVIOR_TYPES = {"浏览","搜索","收藏","加入购物车","购买","支付","退出"};
    private static final int[] BEHAVIOR_WEIGHTS = {40, 15, 10, 15, 10, 5, 5};

    private static int generateUserBehavior(String path, int rows) throws IOException {
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 14);
        int[] peakHours = {10, 11, 20, 21, 22, 23};
        int logId = 100001;
        int count = 0;
        try (Writer w = new OutputStreamWriter(new FileOutputStream(path), "UTF-8")) {
            for (int i = 0; i < rows; i++) {
                String userId = randUser();
                String productId = randProduct();
                String behavior = weightedPick(BEHAVIOR_TYPES, BEHAVIOR_WEIGHTS);
                String source = SOURCES[RAND.nextInt(SOURCES.length)];
                String keyword = "搜索".equals(behavior)
                        ? SEARCH_KEYWORDS[RAND.nextInt(SEARCH_KEYWORDS.length)] : "";
                LocalDateTime time = randPeakDateTime(start, end, peakHours);
                String sessionId = nextSessionId(userId, time.toLocalDate());
                String device = weightedPick(DEVICES, new int[]{45, 35, 15, 5});
                String city = CITIES[RAND.nextInt(CITIES.length)];
                int quantity = ("加入购物车".equals(behavior) || "购买".equals(behavior))
                        ? randInt(1, 3) : 0;
                StringBuilder sb = new StringBuilder();
                sb.append(logId++).append('\t')
                  .append(userId).append('\t')
                  .append(productId).append('\t')
                  .append(behavior).append('\t')
                  .append(source).append('\t')
                  .append(keyword).append('\t')
                  .append(sessionId).append('\t')
                  .append(device).append('\t')
                  .append(city).append('\t')
                  .append(productPrice(productId)).append('\t')
                  .append(quantity).append('\t')
                  .append(time.format(DT));
                w.write(sb.toString());
                w.write("\n");
                count++;
            }
        }
        return count;
    }

    /* ============ 2. 订单数据 order_data.csv（逗号分隔，19 列） ============ */

    private static final String[] ORDER_STATUS = {"待支付","已支付","已发货","已完成","已取消","已退款"};
    private static final int[] ORDER_STATUS_WEIGHTS = {10, 15, 20, 40, 10, 5};
    private static final String[] PAY_METHODS = {"微信","支付宝","银行卡","货到付款"};
    private static final int[] PAY_WEIGHTS = {40, 35, 15, 10};
    private static final String[] ORDER_SOURCES = {"APP","小程序","PC","H5"};
    private static final int[] ORDER_SOURCE_WEIGHTS = {40, 25, 20, 15};
    private static final String[] RETURN_REASONS = {"质量问题","尺寸不符","不想要了","物流损坏","描述不符","其他"};
    private static final String[] RETURN_STATUS = {"申请中","审核通过","已退换","驳回"};
    private static final int[] RETURN_STATUS_WEIGHTS = {20, 30, 40, 10};

    private static int generateOrderData(String path, int rows) throws IOException {
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 14);
        int count = 0;
        try (Writer w = new OutputStreamWriter(new FileOutputStream(path), "UTF-8")) {
            for (int i = 0; i < rows; i++) {
                String orderId = "O202609" + String.format("%06d", i + 1);
                String userId = randUser();
                String productId = randProduct();
                String categoryId = String.valueOf(randInt(1, 10));
                String status = weightedPick(ORDER_STATUS, ORDER_STATUS_WEIGHTS);
                String payStatus;
                if ("待支付".equals(status) || "已取消".equals(status)) {
                    payStatus = "未支付";
                } else if ("已退款".equals(status)) {
                    payStatus = "已退款";
                } else {
                    payStatus = "已支付";
                }
                int quantity = randInt(1, 3);
                BigDecimal amount = productPrice(productId)
                        .multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal discount = amount.multiply(new BigDecimal(RAND.nextInt(21) / 100.0))
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal actual = amount.subtract(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                String payMethod = weightedPick(PAY_METHODS, PAY_WEIGHTS);
                String orderSource = weightedPick(ORDER_SOURCES, ORDER_SOURCE_WEIGHTS);
                LocalDateTime createTime = randDateTime(start, end);
                String payTime = "", shipTime = "", finishTime = "";
                if (!"待支付".equals(status) && !"已取消".equals(status)) {
                    payTime = createTime.plusMinutes(randInt(1, 30)).format(DT);
                    if ("已发货".equals(status) || "已完成".equals(status)) {
                        LocalDateTime pay = createTime.plusMinutes(randInt(1, 30));
                        shipTime = pay.plusHours(randInt(1, 24)).format(DT);
                        if ("已完成".equals(status)) {
                            finishTime = pay.plusHours(randInt(1, 24)).plusDays(randInt(1, 3)).format(DT);
                        }
                    }
                }
                int isReturn = RAND.nextInt(100) < 15 ? 1 : 0;
                String returnStatus = "", returnReason = "", returnTime = "";
                if (isReturn == 1) {
                    returnStatus = weightedPick(RETURN_STATUS, RETURN_STATUS_WEIGHTS);
                    returnReason = RETURN_REASONS[RAND.nextInt(RETURN_REASONS.length)];
                    // 退货时间基于完成时间推算 1-7 天（无完成时间则基于创建时间）
                    LocalDateTime base = finishTime.isEmpty() ? createTime : LocalDateTime.parse(finishTime, DT);
                    returnTime = base.plusDays(randInt(1, 7)).format(DT);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(csv(orderId)).append(',')
                  .append(csv(userId)).append(',')
                  .append(csv(productId)).append(',')
                  .append(csv(categoryId)).append(',')
                  .append(csv(status)).append(',')
                  .append(csv(payStatus)).append(',')
                  .append(csv(amount.toPlainString())).append(',')
                  .append(csv(discount.toPlainString())).append(',')
                  .append(csv(actual.toPlainString())).append(',')
                  .append(csv(payMethod)).append(',')
                  .append(csv(orderSource)).append(',')
                  .append(csv(createTime.format(DT))).append(',')
                  .append(csv(payTime)).append(',')
                  .append(csv(shipTime)).append(',')
                  .append(csv(finishTime)).append(',')
                  .append(csv(String.valueOf(isReturn))).append(',')
                  .append(csv(returnStatus)).append(',')
                  .append(csv(returnReason)).append(',')
                  .append(csv(returnTime));
                w.write(sb.toString());
                w.write("\n");
                count++;
            }
        }
        return count;
    }

    /* ============ 3. 评价数据 review_data.csv（逗号分隔，13 列） ============ */

    private static final String[] GOOD_REVIEWS = {
        "保温效果很好外观也漂亮物流很快","手机性价比超高续航一天没问题推荐购买","绘本内容不错孩子很喜欢",
        "质量很好做工精细值得购买","物流超快第二天就到了包装完好","客服态度好有问必答点赞",
        "颜色正没有色差穿着舒服","价格实惠比实体店便宜很多"
    };
    private static final String[] MID_REVIEWS = {
        "东西还行就是发货慢了点","一般般吧没有想象中好","质量凑合对得起这个价格","包装有点简陋东西没问题"
    };
    private static final String[] BAD_REVIEWS = {
        "鞋子偏小一码穿了半天脚疼","用了两天就坏了质量太差","物流慢得要死等了一周",
        "跟描述完全不符要求退货","客服态度恶劣不理人","收到货发现有瑕疵要求换货"
    };
    private static final String[] APPEND_CONTENTS = {
        "用了一段时间来追评确实不错","追评：发现了一些问题希望改进","售后处理很快满意","追评：质量比预期好"
    };
    private static final String[] MERCHANT_REPLIES = {
        "感谢您的认可欢迎下次光临","非常抱歉给您带来不好的体验我们会改进",
        "感谢您的理解祝您生活愉快","已收到您的反馈我们会尽快处理"
    };

    /** 4-5星70%，3星15%，1-2星15% */
    private static int randRating() {
        int r = RAND.nextInt(100);
        if (r < 70) {
            return randInt(4, 5);
        } else if (r < 85) {
            return 3;
        } else {
            return randInt(1, 2);
        }
    }

    private static int generateReviewData(String path, int rows) throws IOException {
        LocalDate start = LocalDate.of(2026, 9, 5);
        LocalDate end = LocalDate.of(2026, 9, 14);
        int count = 0;
        try (Writer w = new OutputStreamWriter(new FileOutputStream(path), "UTF-8")) {
            for (int i = 0; i < rows; i++) {
                String reviewId = "R202609" + String.format("%06d", i + 1);
                String orderId = "O202609" + String.format("%06d", RAND.nextInt(10000) + 1);
                String userId = randUser();
                String productId = randProduct();
                int rating = randRating();
                String content;
                if (rating >= 4) {
                    content = GOOD_REVIEWS[RAND.nextInt(GOOD_REVIEWS.length)];
                } else if (rating == 3) {
                    content = MID_REVIEWS[RAND.nextInt(MID_REVIEWS.length)];
                } else {
                    content = BAD_REVIEWS[RAND.nextInt(BAD_REVIEWS.length)];
                }
                LocalDateTime reviewTime = randDateTime(start, end);
                boolean isAppend = RAND.nextInt(100) < 20;
                String appendContent = isAppend
                        ? APPEND_CONTENTS[RAND.nextInt(APPEND_CONTENTS.length)] : "";
                int imageCount = randInt(0, 3);
                int praiseNum = randInt(0, 50);
                boolean hasReply = RAND.nextInt(100) < 70;
                String reply = hasReply ? MERCHANT_REPLIES[RAND.nextInt(MERCHANT_REPLIES.length)] : "";
                String replyTime = hasReply
                        ? reviewTime.plusHours(randInt(1, 5)).format(DT) : "";
                StringBuilder sb = new StringBuilder();
                sb.append(csv(reviewId)).append(',')
                  .append(csv(orderId)).append(',')
                  .append(csv(userId)).append(',')
                  .append(csv(productId)).append(',')
                  .append(csv(String.valueOf(rating))).append(',')
                  .append(csv(content)).append(',')
                  .append(csv(reviewTime.format(DT))).append(',')
                  .append(csv(isAppend ? "是" : "否")).append(',')
                  .append(csv(appendContent)).append(',')
                  .append(csv(String.valueOf(imageCount))).append(',')
                  .append(csv(String.valueOf(praiseNum))).append(',')
                  .append(csv(reply)).append(',')
                  .append(csv(replyTime));
                w.write(sb.toString());
                w.write("\n");
                count++;
            }
        }
        return count;
    }

    /* ============ 4. 交流数据 chat_data.csv（逗号分隔，14 列） ============ */

    private static final String[] CHAT_TYPES = {"商品咨询","价格活动","物流查询","售后问题","投诉建议","其他"};
    private static final int[] CHAT_TYPE_WEIGHTS = {35, 15, 20, 15, 5, 10};

    private static int generateChatData(String path, int rows) throws IOException {
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 14);
        int[] peakHours = {9, 10, 11, 14, 15, 16, 17, 20, 21, 22};
        int count = 0;
        try (Writer w = new OutputStreamWriter(new FileOutputStream(path), "UTF-8")) {
            for (int i = 0; i < rows; i++) {
                String chatId = "C202609" + String.format("%06d", i + 1);
                String userId = randUser();
                String productId = randProduct();
                String categoryId = String.valueOf(randInt(1, 10));
                String sessionId = "CHATSESS" + String.format("%06d", RAND.nextInt(900000) + 100000);
                String chatType = weightedPick(CHAT_TYPES, CHAT_TYPE_WEIGHTS);
                int userMsgCount = randInt(1, 8);
                // 80% 概率商家有回复（回复数 ≥1），否则 0
                int merchantReplyCount;
                if (RAND.nextInt(100) < 80) {
                    merchantReplyCount = randInt(1, userMsgCount);
                } else {
                    merchantReplyCount = 0;
                }
                int firstResponseSec = merchantReplyCount >= 1 ? randInt(5, 300) : 0;
                int durationSec = randInt(30, 1800);
                int isConverted = RAND.nextInt(100) < 40 ? 1 : 0;
                String convertAction = isConverted == 1
                        ? (RAND.nextInt(100) < 60 ? "加购" : "下单") : "";
                LocalDateTime chatStart = randPeakDateTime(start, end, peakHours);
                LocalDateTime chatEnd = chatStart.plusSeconds(durationSec);
                StringBuilder sb = new StringBuilder();
                sb.append(csv(chatId)).append(',')
                  .append(csv(userId)).append(',')
                  .append(csv(productId)).append(',')
                  .append(csv(categoryId)).append(',')
                  .append(csv(sessionId)).append(',')
                  .append(csv(chatType)).append(',')
                  .append(csv(String.valueOf(userMsgCount))).append(',')
                  .append(csv(String.valueOf(merchantReplyCount))).append(',')
                  .append(csv(String.valueOf(firstResponseSec))).append(',')
                  .append(csv(String.valueOf(durationSec))).append(',')
                  .append(csv(String.valueOf(isConverted))).append(',')
                  .append(csv(convertAction)).append(',')
                  .append(csv(chatStart.format(DT))).append(',')
                  .append(csv(chatEnd.format(DT)));
                w.write(sb.toString());
                w.write("\n");
                count++;
            }
        }
        return count;
    }
}
