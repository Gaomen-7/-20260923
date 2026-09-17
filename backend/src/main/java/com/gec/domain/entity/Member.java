package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "tbl_member")
public class Member implements java.io.Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String nickname;       // 会员昵称
    private String avatar;         // 头像
    private String phone;          // 手机号
    private Integer memberType;    // 会员类型：1=普通，2=VIP，3=黄金
    private String source;         // 来源：APP/小程序/PC/H5
    private BigDecimal balance;    // 余额
    private Integer points;         // 会员积分
    private Integer status;        // 状态：1=正常，0=黑名单
    private String registerTime;   // 注册时间
    private String createTime;
    private String updateTime;
}
