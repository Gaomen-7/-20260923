package com.gec.domain.bo;

import com.gec.domain.entity.OrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderInfoBO {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private String userName;
    private String userPhone;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private Integer couponId;
    private String couponName;   // 关联查询用
    private BigDecimal freight;
    private Integer payStatus;
    private Integer orderStatus;
    private String remark;
    private String createTime;
    private String updateTime;

    /* 列表页展示用：第一条商品明细（图片+名称） */
    private String goodsName;
    private String goodsImage;
    private BigDecimal price;
    private Integer quantity;
    private Integer itemCount;  // 明细商品种类数

    /* 详情页用：全部明细 */
    private List<OrderItem> itemList;

    /* 状态文字 */
    public String getOrderStatusName() {
        if (orderStatus == null) return "";
        switch (orderStatus) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }

    public String getPayStatusName() {
        if (payStatus == null) return "";
        return payStatus == 1 ? "已付款" : "未付款";
    }
}
