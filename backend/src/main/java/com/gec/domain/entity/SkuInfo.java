package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value="tbl_sku_info")
public class SkuInfo implements java.io.Serializable {
    @TableId(value="sku_id", type= IdType.AUTO)
    private Integer skuId;         //sku_id
    private Integer goodsId;       //goods_id
    private String  skuName;       //sku_name
    private String  skuDesc;       //sku_desc
    private Integer categoryId;    //category_id
    private Integer brandId;       //brand_id
    private String  skuTitle;      //sku_title
    private String  skuSubtitle;   //sku_subtitle
    private BigDecimal price;      //price
    private Integer stock;         //库存
    private Integer saleCount;     //sale_count

    @TableField(exist = false)
    private String defaultImage;   //默认图片（关联 tbl_sku_album，非数据库字段）
}
