package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value="tbl_goods_info")
public class GoodsInfo implements java.io.Serializable {
    @TableId(type= IdType.AUTO)
    private Integer id;               //id
    private String  goodsSn;          //商品编号
    private String  goodsName;        //goods_name
    private String  goodsDescription; //goods_description
    private Integer categoryId;       //category_id
    private Integer brandId;          //brand_id
    private String  mainImage;        //main_image
    private String  weight;           //weight
    private BigDecimal price;         //商品价格
    private Integer stock;            //库存
    private Integer saleCount;        //销量
    private Integer publishStatus;    //publish_status 0=下架 1=上架
    private Integer isRecommend;      //是否推荐 0=否 1=是
    private String  createTime;       //create_time
    private String  updateTime;       //update_time
}
