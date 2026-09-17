package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tbl_coupon_goods_relation")
public class CouponGoodsRelation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer couponId;
    private Integer goodsId;
    private Integer skuId;   // NULL=绑定整个SPU
    private String createTime;
}
