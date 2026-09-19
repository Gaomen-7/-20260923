package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "tbl_permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 权限标识，格式 模块:操作，如 user:list */
    private String permission;
    /** 路由路径（预留，暂不使用） */
    private String path;
}
