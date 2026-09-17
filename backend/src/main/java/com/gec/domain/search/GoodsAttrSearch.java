package com.gec.domain.search;

import com.gec.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
*  att.category_id=#{param.categoryId}
*  att.attr_type=#{param.attrType}
*
*  categoryId
*  attrGroupId
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsAttrSearch {
    private String  attrName;     //1.属性名称
    private Integer categoryId;
    private Integer attrType;
    private Integer attrGroupId;  //2.属性分组ID
}
