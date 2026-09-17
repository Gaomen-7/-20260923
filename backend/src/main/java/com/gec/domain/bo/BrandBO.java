package com.gec.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BrandBO {
    private Integer id;          //id
    private String brandName;    //brand_name
    private String logoName;     //logo_url
    private String brandDesc;    //brand_desc
    private Integer showStatus;  //show_status

}
