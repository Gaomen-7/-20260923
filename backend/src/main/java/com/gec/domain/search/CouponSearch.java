package com.gec.domain.search;

import lombok.Data;

@Data
public class CouponSearch {
    private String couponName;   // 优惠券名称（模糊）
    private Integer status;      // 是否有效
    private String startTime;    // 发布时间起
    private String endTime;      // 发布时间止
}
