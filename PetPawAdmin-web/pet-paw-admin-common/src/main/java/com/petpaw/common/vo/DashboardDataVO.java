package com.petpaw.common.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘数据VO
 */
@Data
public class DashboardDataVO {
    // 统计数据
    private Integer todayCustomers;
    private Double customerGrowth;
    private BigDecimal todayRevenue;
    private Double revenueGrowth;
    private Integer todayAppointments;
    private Double appointmentGrowth;
    private Integer lowStockItems;

    // 图表数据
    private Map<String, Object> revenueChartData;
    private Map<String, Object> serviceChartData;
    private List<Map<String, Object>> customerSourceData;
    private List<Map<String, Object>> inventoryData;
    
    // 近期活动
    private List<Map<String, Object>> todayAppointmentsList;
    private List<Map<String, Object>> lowStockItemsList;
}
