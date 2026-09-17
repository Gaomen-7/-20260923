package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName(value="tbl_goods_attr_group")
@EqualsAndHashCode(callSuper = false)
public class GoodsAttrGroup extends BaseEntity {
    @TableId(type= IdType.AUTO)
    private Integer id;            //id [自增长]
    private String groupName;      //group_name
    private Integer categoryId;    //category_id
    private String descript;
    private String icons;
    private Integer sort;

    //create_date [In BaseEntity]
    //update_date [In BaseEntity]
}
