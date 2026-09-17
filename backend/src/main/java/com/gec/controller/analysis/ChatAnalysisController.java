package com.gec.controller.analysis;

import com.gec.controller.BaseController;
import com.gec.controller.R;
import com.gec.dao.ChatAnalysisStatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 交流分析接口（数据源：ads_chat_analysis）
 */
@RestController
@RequestMapping("/analysis/chat")
public class ChatAnalysisController extends BaseController {

    @Autowired
    private ChatAnalysisStatMapper chatAnalysisStatMapper;

    /* 1. 会话指标卡片（会话数/用户数/平均时长/解决率/转化率） */
    @GetMapping("/overview")
    public R overview(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", chatAnalysisStatMapper.selectByDimensionType("overview", startDate, endDate));
    }

    /* 2. 咨询类型饼图 */
    @GetMapping("/typeDist")
    public R typeDist(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", chatAnalysisStatMapper.selectByDimensionType("chat_type", startDate, endDate));
    }

    /* 3. 咨询时段热力图 */
    @GetMapping("/heatmap")
    public R heatmap(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", chatAnalysisStatMapper.selectByDimensionType("hour", startDate, endDate));
    }

    /* 4. 咨询转化柱状图（按咨询类型） */
    @GetMapping("/conversion")
    public R conversion(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", chatAnalysisStatMapper.selectByDimensionType("conversion", startDate, endDate));
    }

    /* 5. 热门咨询商品榜 */
    @GetMapping("/topProducts")
    public R topProducts(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", chatAnalysisStatMapper.selectTopConsultedProducts(limit, startDate, endDate));
    }

    @ExceptionHandler(Exception.class)
    public void exceptionFallback(Exception e, HttpServletResponse resp) {
        e.printStackTrace();
        R.err(e).write(resp);
    }

    @Override
    protected com.gec.components.FileTemplate getFileTemplate() { return null; }
}
