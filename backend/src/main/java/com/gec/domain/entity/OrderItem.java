package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "tbl_order_item")
public class OrderItem implements java.io.Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer orderId;     // 关联订单主表ID
    private String orderNo;      // 订单编号（冗余）
    private Integer goodsId;     // 商品ID
    private String goodsName;    // 商品名称
    private String goodsImage;   // 商品图片
    private Integer skuId;       // SKU ID
    private BigDecimal price;    // 单价
    private Integer quantity;    // 数量
    private BigDecimal subtotal; // 小计
}
