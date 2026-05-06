package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.petpaw.common.vo.DashboardDataVO;
import com.petpaw.entity.*;
import com.petpaw.mapper.*;
import com.petpaw.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现类
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CrmCustomerMapper customerMapper;
    private final FinanceOrderMapper financeOrderMapper;
    private final AppointmentOrderMapper appointmentOrderMapper;
    private final ScmProductMapper productMapper;
    private final AppointmentServiceItemMapper serviceItemMapper;
    private final CrmPetMapper petMapper;

    @Override
    public DashboardDataVO getDashboardData() {
        DashboardDataVO vo = new DashboardDataVO();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDateTime todayStart = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(today, LocalTime.MAX);
        LocalDateTime yesterdayStart = LocalDateTime.of(yesterday, LocalTime.MIN);
        LocalDateTime yesterdayEnd = LocalDateTime.of(yesterday, LocalTime.MAX);

        // 1. 统计数据
        // 今日新增客户
        Long todayNewCustomers = customerMapper.selectCount(new LambdaQueryWrapper<CrmCustomer>()
                .ge(CrmCustomer::getCreateTime, todayStart)
                .le(CrmCustomer::getCreateTime, todayEnd));
        Long yesterdayNewCustomers = customerMapper.selectCount(new LambdaQueryWrapper<CrmCustomer>()
                .ge(CrmCustomer::getCreateTime, yesterdayStart)
                .le(CrmCustomer::getCreateTime, yesterdayEnd));
        vo.setTodayCustomers(todayNewCustomers.intValue());
        vo.setCustomerGrowth(calculateGrowth(todayNewCustomers, yesterdayNewCustomers));

        // 今日营收
        List<FinanceOrder> todayOrders = financeOrderMapper.selectList(new LambdaQueryWrapper<FinanceOrder>()
                .eq(FinanceOrder::getPaymentStatus, "已支付")
                .ge(FinanceOrder::getPaymentTime, todayStart)
                .le(FinanceOrder::getPaymentTime, todayEnd));
        BigDecimal todayRevenue = todayOrders.stream()
                .map(FinanceOrder::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<FinanceOrder> yesterdayOrders = financeOrderMapper.selectList(new LambdaQueryWrapper<FinanceOrder>()
                .eq(FinanceOrder::getPaymentStatus, "已支付")
                .ge(FinanceOrder::getPaymentTime, yesterdayStart)
                .le(FinanceOrder::getPaymentTime, yesterdayEnd));
        BigDecimal yesterdayRevenue = yesterdayOrders.stream()
                .map(FinanceOrder::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        vo.setTodayRevenue(todayRevenue);
        vo.setRevenueGrowth(calculateGrowth(todayRevenue, yesterdayRevenue));

        // 今日预约
        Long todayAppts = appointmentOrderMapper.selectCount(new LambdaQueryWrapper<AppointmentOrder>()
                .eq(AppointmentOrder::getAppointmentDate, today));
        Long yesterdayAppts = appointmentOrderMapper.selectCount(new LambdaQueryWrapper<AppointmentOrder>()
                .eq(AppointmentOrder::getAppointmentDate, yesterday));
        vo.setTodayAppointments(todayAppts.intValue());
        vo.setAppointmentGrowth(calculateGrowth(todayAppts, yesterdayAppts));

        // 库存预警
        Long lowStock = productMapper.selectCount(new QueryWrapper<ScmProduct>()
                .lt("stock_quantity", "min_stock_quantity")
                .eq("deleted", 0));
        vo.setLowStockItems(lowStock.intValue());

        // 2. 图表数据
        vo.setRevenueChartData(getRevenueChartData());
        vo.setServiceChartData(getServiceChartData());
        vo.setCustomerSourceData(getCustomerSourceData());
        vo.setInventoryData(getInventoryData());

        // 3. 近期活动
        vo.setTodayAppointmentsList(getTodayAppointmentsList(today));
        vo.setLowStockItemsList(getLowStockItemsList());

        return vo;
    }

    private Double calculateGrowth(Number current, Number previous) {
        double curr = current.doubleValue();
        double prev = previous.doubleValue();
        if (prev == 0) return curr > 0 ? 100.0 : 0.0;
        return ((curr - prev) / prev) * 100;
    }

    private Map<String, Object> getRevenueChartData() {
        Map<String, Object> data = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<BigDecimal> revenues = new ArrayList<>();
        List<BigDecimal> targets = new ArrayList<>(); // 模拟目标

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            
            List<FinanceOrder> orders = financeOrderMapper.selectList(new LambdaQueryWrapper<FinanceOrder>()
                    .eq(FinanceOrder::getPaymentStatus, "已支付")
                    .ge(FinanceOrder::getPaymentTime, start)
                    .le(FinanceOrder::getPaymentTime, end));
            
            BigDecimal revenue = orders.stream()
                    .map(FinanceOrder::getPaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenues.add(revenue);
            targets.add(new BigDecimal("1000")); // 模拟每日目标
        }
        data.put("dates", dates);
        data.put("revenues", revenues);
        data.put("targets", targets);
        return data;
    }

    private Map<String, Object> getServiceChartData() {
        QueryWrapper<AppointmentOrder> wrapper = new QueryWrapper<>();
        wrapper.select("service_item_id", "count(*) as count")
               .groupBy("service_item_id")
               .orderByDesc("count")
               .last("LIMIT 10");
        List<Map<String, Object>> list = appointmentOrderMapper.selectMaps(wrapper);
        
        List<String> names = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        
        for (Map<String, Object> map : list) {
            Long serviceId = (Long) map.get("service_item_id");
            Long count = (Long) map.get("count");
            AppointmentServiceItem item = serviceItemMapper.selectById(serviceId);
            if (item != null) {
                names.add(item.getServiceName());
                counts.add(count);
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("names", names);
        data.put("counts", counts);
        return data;
    }

    private List<Map<String, Object>> getCustomerSourceData() {
        QueryWrapper<CrmCustomer> wrapper = new QueryWrapper<>();
        wrapper.select("source", "count(*) as count")
               .groupBy("source")
               .isNotNull("source");
        List<Map<String, Object>> list = customerMapper.selectMaps(wrapper);
        
        return list.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", m.get("source") != null ? m.get("source") : "未知");
            map.put("value", m.get("count"));
            return map;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getInventoryData() {
        Long normal = productMapper.selectCount(new QueryWrapper<ScmProduct>()
                .ge("stock_quantity", "min_stock_quantity")
                .gt("stock_quantity", 0));
        Long low = productMapper.selectCount(new QueryWrapper<ScmProduct>()
                .lt("stock_quantity", "min_stock_quantity")
                .gt("stock_quantity", 0));
        Long out = productMapper.selectCount(new LambdaQueryWrapper<ScmProduct>()
                .eq(ScmProduct::getStockQuantity, 0));
        
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> normalMap = new HashMap<>();
        normalMap.put("name", "正常库存");
        normalMap.put("value", normal);
        data.add(normalMap);
        
        Map<String, Object> lowMap = new HashMap<>();
        lowMap.put("name", "低库存预警");
        lowMap.put("value", low);
        data.add(lowMap);
        
        Map<String, Object> outMap = new HashMap<>();
        outMap.put("name", "缺货");
        outMap.put("value", out);
        data.add(outMap);
        
        return data;
    }

    private List<Map<String, Object>> getTodayAppointmentsList(LocalDate today) {
        List<AppointmentOrder> orders = appointmentOrderMapper.selectList(new LambdaQueryWrapper<AppointmentOrder>()
                .eq(AppointmentOrder::getAppointmentDate, today)
                .orderByAsc(AppointmentOrder::getAppointmentTime));
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (AppointmentOrder order : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("time", order.getAppointmentTime().toString());
            map.put("status", order.getStatus());
            
            AppointmentServiceItem item = serviceItemMapper.selectById(order.getServiceItemId());
            map.put("serviceName", item != null ? item.getServiceName() : "未知服务");
            
            CrmCustomer customer = customerMapper.selectById(order.getCustomerId());
            map.put("customerName", customer != null ? customer.getCustomerName() : "未知客户");
            
            CrmPet pet = petMapper.selectById(order.getPetId());
            map.put("petName", pet != null ? pet.getPetName() : "未知宠物");
            
            result.add(map);
        }
        return result;
    }
    
    private List<Map<String, Object>> getLowStockItemsList() {
        // 使用 QueryWrapper 处理字段间比较
        List<ScmProduct> products = productMapper.selectList(new QueryWrapper<ScmProduct>()
                .lt("stock_quantity", "min_stock_quantity")
                .eq("deleted", 0)
                .last("LIMIT 10"));
        
        return products.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("productName", p.getProductName());
            map.put("currentStock", p.getStockQuantity());
            map.put("minStock", p.getMinStockQuantity());
            map.put("unit", p.getUnit());
            return map;
        }).collect(Collectors.toList());
    }
}
