package com.gec.domain.search;

import lombok.Data;

@Data
public class AdvertSearch {
    private String advertName;   // 广告名称（模糊）
    private String position;     // 投放位置
    private Integer status;      // 状态
}
