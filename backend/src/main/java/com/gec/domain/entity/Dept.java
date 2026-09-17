package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gec.domain.entity.Node;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Date;

@Data
@TableName(value="tbl_dept")
@EqualsAndHashCode(callSuper = false)
public class Dept extends BaseEntity implements Node {
    @TableId(type=IdType.AUTO)
    private Integer id;
    private String deptName;     //dept_name
    private String deptDesc;     //dept_desc
    private Integer parentId;    //parent_id

    @TableField(value="p_ids")
    private String pIds;         //p_ids

}
