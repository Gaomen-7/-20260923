package com.gec.domain.vo;

import com.gec.domain.entity.Brand;
import lombok.Data;

import java.util.List;

@Data
public class CategoryBrandVO {
    Integer categoryId;
    String  categoryName;
    /*
    * Brand 包含以下属性:
    * {1}Integer id;
    * {2}String brandName;
    */
    private List<Brand> brands;
    private List<Integer> deleteIds;
}
