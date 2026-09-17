package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value="tbl_sku_album")
public class SpuAlbum {
    @TableId(type= IdType.AUTO)
    private String id;
    private String goodsId;
    private String skuId;
    private String images;
    private String defaultImage;
    private String createDate;
    private String updateDate;

}
