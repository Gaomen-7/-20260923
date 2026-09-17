package com.gec.controller.analysis;

import com.gec.controller.BaseController;
import com.gec.controller.R;
import com.gec.dao.OrderAnalysisStatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 订单分析接口（数据源：ads_order_analysis）
 */
@RestController
@RequestMapping("/analysis/order")
public class OrderAnalysisController extends BaseController {

    @Autowired
    private OrderAnalysisStatMapper orderAnalysisStatMapper;

    /* 1. 销售指标卡片（订单数/总额/客单价/支付转化率） */
    @GetMapping("/overview")
    public R overview(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", orderAnalysisStatMapper.selectByDimensionType("overview", startDate, endDate));
    }

    /* 2. 销售趋势折线图（按日期） */
    @GetMapping("/trend")
    public R trend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", orderAnalysisStatMapper.selectByDimensionType("overview", startDate, endDate));
    }

    /* 3. 品类销售结构饼图 */
    @GetMapping("/category")
    public R category(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", orderAnalysisStatMapper.selectByDimensionType("category", startDate, endDate));
    }

    /* 4. 退换货看板（原因分布+取消数，合并 return_reason + cancel） */
    @GetMapping("/return")
    public R returnBoard(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data",
                orderAnalysisStatMapper.selectByDimensionTypes(Arrays.asList("return_reason", "cancel"), startDate, endDate));
    }

    /* 5. 客单价直方图 */
    @GetMapping("/priceRange")
    public R priceRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", orderAnalysisStatMapper.selectByDimensionType("price_range", startDate, endDate));
    }

    /* 6. 时段销售柱状图（按小时汇总） */
    @GetMapping("/hourly")
    public R hourly(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", orderAnalysisStatMapper.selectHourlySales(startDate, endDate));
    }

    @ExceptionHandler(Exception.class)
    public void exceptionFallback(Exception e, HttpServletResponse resp) {
        e.printStackTrace();
        R.err(e).write(resp);
    }

    @Override
    protected com.gec.components.FileTemplate getFileTemplate() { return null; }
}
