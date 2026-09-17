package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.domain.bo.OrderInfoBO;
import com.gec.domain.entity.OrderInfo;
import com.gec.domain.search.OrderInfoSearch;
import org.apache.ibatis.annotations.Param;

public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /* 分页查询订单列表（支持按商品名称模糊匹配明细） */
    Page<OrderInfoBO> getOrderList(
        Page page,
        @Param("param") OrderInfoSearch search);
}
