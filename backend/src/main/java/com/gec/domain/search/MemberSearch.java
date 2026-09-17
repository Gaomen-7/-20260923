package com.gec.domain.search;

import lombok.Data;

@Data
public class MemberSearch {
    private Integer memberType;  // 会员类型
    private Integer status;      // 状态
    private String source;       // 来源
    private String phone;        // 手机号（模糊）
}
