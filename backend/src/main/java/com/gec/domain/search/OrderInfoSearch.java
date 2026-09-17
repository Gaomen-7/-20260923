package com.gec.domain.search;

import lombok.Data;

@Data
public class OrderInfoSearch {
    private String orderNo;       // 订单编号
    private String goodsName;     // 商品名称（模糊匹配明细）
    private String keyword;       // 会员昵称/手机号/收货人（合并模糊筛选）
    private String receiverName;  // 收货人（独立筛选）
    private String receiverPhone; // 收货手机号（独立筛选）
    private String userName;      // 会员昵称（独立筛选）
    private String userPhone;     // 会员手机号（独立筛选）
    private Integer orderStatus;  // 订单状态
    private String startTime;     // 创建时间起
    private String endTime;       // 创建时间止
}
