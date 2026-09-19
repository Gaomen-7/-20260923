package com.gec.controller.analysis;

import com.gec.controller.BaseController;
import com.gec.controller.R;
import com.gec.service.IAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 评价分析接口（数据源：ads_review_analysis）
 * 注意：review 表无 category 维度，/categoryCompare 用商品口碑TOP（product_top）代替。
 */
@RestController
@RequestMapping("/analysis/review")
public class ReviewAnalysisController extends BaseController {

    @Autowired
    private IAnalysisService analysisService;

    /* 1. 评价指标卡片（总数/好评率/平均分/追评率） */
    @GetMapping("/overview")
    public R overview(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", analysisService.reviewByDimension("overview", startDate, endDate));
    }

    /* 2. 评价等级饼图（好/中/差） */
    @GetMapping("/levelDist")
    public R levelDist(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", analysisService.reviewByDimension("rating_level", startDate, endDate));
    }

    /* 3. 关键词词云（好评+差评合并，前端按 dimension_type 区分） */
    @GetMapping("/keywords")
    public R keywords(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data",
                analysisService.reviewByDimensions(Arrays.asList("keyword_good", "keyword_bad"), startDate, endDate));
    }

    /* 4. 商品口碑对比（review无category维度，返回商品口碑TOP数据） */
    @GetMapping("/categoryCompare")
    public R categoryCompare(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok()
                .put("note", "review表无category维度，返回商品口碑TOP数据")
                .put("data", analysisService.reviewByDimension("product_top", startDate, endDate));
    }

    /* 5. 口碑趋势折线图（好评率按日期） */
    @GetMapping("/trend")
    public R trend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", analysisService.reviewByDimension("overview", startDate, endDate));
    }

    /* 6. 商品口碑榜（好评TOP+差评榜合并，按好评率排序） */
    @GetMapping("/topProducts")
    public R topProducts(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok().put("data", analysisService.reviewProductWordOfMouth(limit, startDate, endDate));
    }

    @ExceptionHandler(Exception.class)
    public void exceptionFallback(Exception e, HttpServletResponse resp) {
        e.printStackTrace();
        R.err(e).write(resp);
    }

    @Override
    protected com.gec.components.FileTemplate getFileTemplate() { return null; }
}
