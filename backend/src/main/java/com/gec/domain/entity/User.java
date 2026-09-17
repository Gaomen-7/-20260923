package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tbl_user")
public class User extends BaseEntity {
    @TableId(type=IdType.AUTO)
    private Integer id;
    private String account;
    private String nickName;
    private String phone;
    private String sex;
    private String no;
    private String email;
    private Integer deptId;
    private String password;

}
