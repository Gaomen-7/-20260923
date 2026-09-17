package com.gec.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserBO {
    private Integer id;
    private String account;
    private String nickName;
    private String phone;
    private String sex;
    private String no;
    private Integer deptId;
    private String deptName;
    private Integer roleId;
    private String roleName;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date createDate;
}


