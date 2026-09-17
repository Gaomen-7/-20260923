package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName(value="tbl_role")
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseEntity {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String roleName;
    private String descript;

}
