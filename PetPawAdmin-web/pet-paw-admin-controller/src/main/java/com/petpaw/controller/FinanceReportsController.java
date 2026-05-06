package com.petpaw.controller;

import com.petpaw.common.result.Result;
import com.petpaw.service.FinanceReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 财务报表控制器
 */
@RestController
@RequestMapping("/api/finance/reports")
@RequiredArgsConstructor
public class FinanceReportsController {

    private final FinanceReportsService reportsService;

    /**
     * 获取财务统计数据
     */
    @GetMapping("/stats")
    public Result getStats(@RequestParam String startDate, @RequestParam String endDate) {
        Map<String, Object> stats = reportsService.getStats(startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 获取营收趋势
     */
    @GetMapping("/revenue-trend")
    public Result getRevenueTrend(@RequestParam String startDate, 
                                 @RequestParam String endDate,
                                 @RequestParam(defaultValue = "day") String type) {
        Map<String, Object> trend = reportsService.getRevenueTrend(startDate, endDate, type);
        return Result.success(trend);
    }

    /**
     * 获取收入构成
     */
    @GetMapping("/income-composition")
    public Result getIncomeComposition(@RequestParam String startDate, @RequestParam String endDate) {
        List<Map<String, Object>> composition = reportsService.getIncomeComposition(startDate, endDate);
        return Result.success(composition);
    }

    /**
     * 获取会员等级消费统计
     */
    @GetMapping("/member-consumption")
    public Result getMemberConsumptionStats(@RequestParam String startDate, @RequestParam String endDate) {
        Map<String, Object> stats = reportsService.getMemberConsumptionStats(startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 获取商品销售排行
     */
    @GetMapping("/product-sales-ranking")
    public Result getProductSalesRanking(@RequestParam String startDate, @RequestParam String endDate) {
        Map<String, Object> ranking = reportsService.getProductSalesRanking(startDate, endDate);
        return Result.success(ranking);
    }

    /**
     * 获取日营收明细列表
     */
    @GetMapping("/daily-revenue-list")
    public Result getDailyRevenueList(@RequestParam String startDate, @RequestParam String endDate) {
        List<Map<String, Object>> list = reportsService.getDailyRevenueList(startDate, endDate);
        return Result.success(list);
    }
}
