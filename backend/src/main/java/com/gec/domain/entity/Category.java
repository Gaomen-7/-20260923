package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gec.domain.entity.BaseEntity;
import com.gec.domain.entity.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName(value="tbl_goods_category")
@EqualsAndHashCode(callSuper = false)
public class Category extends BaseEntity implements Node {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String categoryName;  //category_name
    private Integer parentId;     //parent_id
    private String pIds;          //
    private Integer showStatus;
    private Integer sort;

    //{1}设置级别的属性(设置它与数据库无关)
    //@TableField(exist=false)
    //private Integer level;
}
