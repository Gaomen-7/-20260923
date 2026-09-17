package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "tbl_order_info")
public class OrderInfo implements java.io.Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String orderNo;          // 订单编号
    private Integer userId;          // 会员ID
    private String userName;         // 会员昵称
    private String userPhone;        // 会员手机号
    private String receiverName;     // 收货人
    private String receiverPhone;    // 收货电话
    private String receiverAddress;  // 收货地址
    private BigDecimal totalAmount;  // 订单总金额
    private BigDecimal discountAmount; // 折扣金额
    private BigDecimal actualAmount; // 实收款
    private Integer couponId;        // 使用的优惠券ID
    private BigDecimal freight;      // 运费
    private Integer payStatus;       // 付款状态：0=未付款，1=已付款
    private Integer orderStatus;     // 订单状态：0=待付款，1=待发货，2=已发货，3=已完成，4=已取消
    private String remark;           // 备注
    private String createTime;       // 创建时间
    private String updateTime;       // 修改时间
}
