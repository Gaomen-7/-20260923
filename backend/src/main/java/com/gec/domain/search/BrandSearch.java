package com.gec.domain.search;

import lombok.Data;
import lombok.EqualsAndHashCode;

/*
*  关键字(KEY WORD)
*  [1]id
*  [2]brandName
*/
@Data
@EqualsAndHashCode(callSuper = false)
public class BrandSearch {
    private Integer id;
    private String brandName;
    private Integer categoryId;
}
