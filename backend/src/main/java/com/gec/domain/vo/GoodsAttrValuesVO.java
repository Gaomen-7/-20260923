package com.gec.domain.vo;

import com.gec.domain.entity.BaseEntity;
import com.gec.domain.entity.GoodsAttrValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsAttrValuesVO extends BaseEntity {
    private String pubKey;
    private List<GoodsAttrValue> attrList;

}
