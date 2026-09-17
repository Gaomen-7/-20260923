package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tbl_inventory_log")
public class InventoryLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer goodsId;
    private Integer skuId;
    private Integer changeType;   // 1=下单锁定 2=发货扣减 3=取消回滚 4=退货回滚 5=手动调整 6=库存盘点
    private Integer changeQty;   // 正=增加，负=减少
    private Integer beforeStock;
    private Integer afterStock;
    private String orderNo;
    private String operator;
    private String remark;
    private String createTime;
}
