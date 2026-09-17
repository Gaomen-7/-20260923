package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value="tbl_spu_detail")
public class GoodsDetail
    implements java.io.Serializable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String goodsName;
    private String goodsDetails;
    private Integer categoryId;
    private Integer brandId;
    private String mainImage;
    private String weight;
    private String createDate;
    private String updateDate;
    private Integer publishStatus;   //publish_status 0=下架 1=上架

}
