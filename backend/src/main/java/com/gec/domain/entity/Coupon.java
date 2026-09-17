package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "tbl_coupon")
public class Coupon implements java.io.Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String couponName;      // 优惠券名称
    private Integer couponType;     // 类型：1=满减，2=折扣
    private BigDecimal discountAmount; // 优惠金额（满减时用）
    private BigDecimal discountRate;   // 折扣率（折扣时用，如0.90=9折）
    private BigDecimal minAmount;      // 使用门槛
    private String startTime;       // 开始时间
    private String endTime;         // 结束时间
    private Integer totalCount;     // 发放数量
    private Integer receivedCount;  // 已领取数量
    private Integer status;         // 是否有效：0=无效，1=有效
    private String publisher;       // 发布者
    private String createTime;
    private String updateTime;
}
