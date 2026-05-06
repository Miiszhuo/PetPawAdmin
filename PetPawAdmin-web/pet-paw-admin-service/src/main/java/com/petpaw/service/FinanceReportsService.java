package com.petpaw.service;

import java.util.List;
import java.util.Map;

/**
 * 财务报表服务接口
 */
public interface FinanceReportsService {

    /**
     * 获取财务统计数据
     */
    Map<String, Object> getStats(String startDate, String endDate);

    /**
     * 获取营收趋势
     */
    Map<String, Object> getRevenueTrend(String startDate, String endDate, String type);

    /**
     * 获取收入构成
     */
    List<Map<String, Object>> getIncomeComposition(String startDate, String endDate);

    /**
     * 获取会员等级消费统计
     */
    Map<String, Object> getMemberConsumptionStats(String startDate, String endDate);

    /**
     * 获取商品销售排行
     */
    Map<String, Object> getProductSalesRanking(String startDate, String endDate);

    /**
     * 获取日营收明细列表
     */
    List<Map<String, Object>> getDailyRevenueList(String startDate, String endDate);
}
