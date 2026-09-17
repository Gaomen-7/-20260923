package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseEntity implements Serializable {

    //{1}JsonInclude: 这个是设置当这项数据不为空时, 生成 Json.
    //   fill=FieldFill.INSERT: 当插入时才需要填充日期。
    @TableField(value="create_date",fill=FieldFill.INSERT)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime createDate;

    @TableField(value="update_date",fill=FieldFill.INSERT_UPDATE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime updateDate;

}
