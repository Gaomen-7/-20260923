package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value="tbl_sku_album")
public class SkuAlbum implements java.io.Serializable {
    @TableId(type= IdType.AUTO)
    private Integer id;             //id
    private Integer goodsId;        //goods_id
    private Integer skuId;          //sku_id
    private String  images;         //images
    private String  defaultImage;   //default_image
    private String  createTime;     //create_time
    private String  updateTime;     //update_time
}
