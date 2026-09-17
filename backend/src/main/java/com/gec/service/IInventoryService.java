package com.gec.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gec.domain.entity.InventoryLog;

public interface IInventoryService {

    /* 库存统计：商品总数、上架数、库存总量、低库存数、库存总价值 */
    Map<String, Object> getStatistics();

    /* 分页查询商品库存列表（含SKU数量，无N+1） */
    Map<String, Object> pageInventory(int page, int limit, Map<String, Object> param);

    /* 某商品的SKU库存明细 */
    List<com.gec.domain.entity.SkuInfo> getSkuDetail(Integer goodsId);

    /* 调整商品库存 */
    void adjustStock(Integer goodsId, Integer stock);

    /* 调整SKU库存 */
    void adjustSkuStock(Integer skuId, Integer stock);

    /* 按订单扣减库存（发货时调用），遍历订单明细扣减SKU和商品库存 */
    void deductByOrder(Integer orderId, Integer changeType, String operator);

    /* 按订单回滚库存（取消/退货时调用） */
    void rollbackByOrder(Integer orderId, Integer changeType, String operator);

    /* 库存流水分页查询 */
    IPage<InventoryLog> pageInventoryLog(int page, int limit, Integer goodsId, Integer skuId, Integer changeType);

    /* 库存盘点：传入实际盘点数量，系统计算差异并调整 */
    void stocktake(Integer goodsId, Integer skuId, Integer actualStock, String operator, String remark);
}
