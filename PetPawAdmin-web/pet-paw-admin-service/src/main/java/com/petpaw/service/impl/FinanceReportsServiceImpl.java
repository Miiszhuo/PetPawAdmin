package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.petpaw.entity.FinanceOrder;
import com.petpaw.entity.FinanceOrderDetail;
import com.petpaw.entity.FinanceMemberCard;
import com.petpaw.mapper.FinanceOrderMapper;
import com.petpaw.mapper.FinanceOrderDetailMapper;
import com.petpaw.mapper.FinanceMemberCardMapper;
import com.petpaw.service.FinanceReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 财务报表服务实现类
 */
@Service
@RequiredArgsConstructor
public class FinanceReportsServiceImpl implements FinanceReportsService {

    private final FinanceOrderMapper orderMapper;
    private final FinanceMemberCardMapper memberCardMapper;
    private final FinanceOrderDetailMapper orderDetailMapper;

    @Override
    public Map<String, Object> getStats(String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();

        // 总营收
        BigDecimal totalRevenue = orderMapper.selectList(new QueryWrapper<FinanceOrder>()
                .ge("create_time", startDate)
                .le("create_time", endDate)
                .eq("payment_status", "已支付"))
                .stream()
                .map(FinanceOrder::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);
        stats.put("revenueGrowth", 12.5); // 模拟增长率

        // 订单总数
        Long totalOrders = orderMapper.selectCount(new QueryWrapper<FinanceOrder>()
                .ge("create_time", startDate)
                .le("create_time", endDate));
        stats.put("totalOrders", totalOrders);
        stats.put("orderGrowth", 8.3); // 模拟增长率

        // 平均客单价
        BigDecimal avgOrderValue = totalOrders > 0 ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        stats.put("avgOrderValue", avgOrderValue);
        stats.put("avgOrderGrowth", -2.1); // 模拟增长率

        // 会员消费
        BigDecimal memberRevenue = orderMapper.selectList(new QueryWrapper<FinanceOrder>()
                .ge("create_time", startDate)
                .le("create_time", endDate)
                .eq("payment_status", "已支付")
                .isNotNull("customer_id")) // 假设有customer_id即为会员关联订单
                .stream()
                .map(FinanceOrder::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("memberRevenue", memberRevenue);
        stats.put("memberRevenueGrowth", 15.7); // 模拟增长率

        return stats;
    }

    @Override
    public Map<String, Object> getRevenueTrend(String startDate, String endDate, String type) {
        Map<String, Object> result = new HashMap<>();
        
        List<String> dates = new ArrayList<>();
        List<BigDecimal> revenue = new ArrayList<>();
        List<BigDecimal> target = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        // 限制最多展示30个点
        if (start.plusDays(30).isBefore(end)) {
            start = end.minusDays(30);
        }

        // 查询数据库中的每日营收
        List<Map<String, Object>> dailyRevenue = orderMapper.selectMaps(new QueryWrapper<FinanceOrder>()
                .select("DATE_FORMAT(create_time, '%Y-%m-%d') as date", "SUM(payment_amount) as total")
                .ge("create_time", startDate)
                .le("create_time", endDate)
                .eq("payment_status", "已支付")
                .groupBy("DATE_FORMAT(create_time, '%Y-%m-%d')"));

        Map<String, BigDecimal> revenueMap = dailyRevenue.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("date"),
                        m -> (BigDecimal) m.get("total")
                ));

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dateStr = date.format(DateTimeFormatter.ISO_DATE);
            dates.add(dateStr);
            revenue.add(revenueMap.getOrDefault(dateStr, BigDecimal.ZERO));
            target.add(new BigDecimal("6000")); // 模拟每日目标
        }

        result.put("dates", dates);
        result.put("revenue", revenue);
        result.put("target", target);

        return result;
    }

    @Override
    public List<Map<String, Object>> getIncomeComposition(String startDate, String endDate) {
        // 获取所有已支付订单ID
        List<FinanceOrder> orders = orderMapper.selectList(new QueryWrapper<FinanceOrder>()
                .select("id")
                .ge("create_time", startDate)
                .le("create_time", endDate)
                .eq("payment_status", "已支付"));
        
        if (orders.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> orderIds = orders.stream().map(FinanceOrder::getId).collect(Collectors.toList());
        
        // 查询订单详情并分组统计
        List<Map<String, Object>> list = orderDetailMapper.selectMaps(new QueryWrapper<FinanceOrderDetail>()
                .select("item_name as name", "SUM(total_amount) as value")
                .in("order_id", orderIds)
                .groupBy("item_name")
                .orderByDesc("value"));

        return list;
    }

    @Override
    public Map<String, Object> getMemberConsumptionStats(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查找日期范围内的会员订单
        List<FinanceOrder> orders = orderMapper.selectList(new QueryWrapper<FinanceOrder>()
                .ge("create_time", startDate)
                .le("create_time", endDate)
                .eq("payment_status", "已支付")
                .isNotNull("customer_id")); // 假设有customer_id即为会员
        
        if (orders.isEmpty()) {
            result.put("categories", Arrays.asList("钻石会员", "VIP会员", "普通会员"));
            result.put("data", Arrays.asList(0, 0, 0));
            return result;
        }

        // 2. 获取涉及的会员ID
        Set<Long> customerIds = orders.stream()
                .map(FinanceOrder::getCustomerId)
                .collect(Collectors.toSet());
        
        // 3. 查询会员卡等级
        // 注意：这里假设customer_id对应的是会员表的ID，需要关联查询会员卡信息
        // 简化处理：假设FinanceOrder里没有存card_type，需要查表
        // 如果customerIds很大，这里会有性能问题，但作为演示可以接受
        List<FinanceMemberCard> cards = memberCardMapper.selectList(new QueryWrapper<FinanceMemberCard>()
                .in("customer_id", customerIds)); // 假设customer_id是外键
        
        Map<Long, String> customerLevelMap = cards.stream()
                .collect(Collectors.toMap(FinanceMemberCard::getCustomerId, FinanceMemberCard::getCardType, (v1, v2) -> v1));
        
        // 4. 统计各等级消费
        Map<String, BigDecimal> levelStats = new HashMap<>();
        levelStats.put("钻石会员", BigDecimal.ZERO);
        levelStats.put("VIP会员", BigDecimal.ZERO);
        levelStats.put("普通会员", BigDecimal.ZERO);

        for (FinanceOrder order : orders) {
            String level = customerLevelMap.getOrDefault(order.getCustomerId(), "普通会员");
            // 简单映射，如果数据库里的类型名称不一致
            if (level.contains("钻石")) level = "钻石会员";
            else if (level.contains("VIP") || level.contains("金")) level = "VIP会员";
            else level = "普通会员";

            levelStats.put(level, levelStats.get(level).add(order.getPaymentAmount()));
        }

        result.put("categories", new ArrayList<>(levelStats.keySet()));
        result.put("data", new ArrayList<>(levelStats.values()));
        
        return result;
    }

    @Override
    public Map<String, Object> getProductSalesRanking(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取所有已支付订单ID
        List<FinanceOrder> orders = orderMapper.selectList(new QueryWrapper<FinanceOrder>()
                .select("id")
                .ge("create_time", startDate)
                .le("create_time", endDate)
                .eq("payment_status", "已支付"));
        
        if (orders.isEmpty()) {
            result.put("categories", new ArrayList<>());
            result.put("data", new ArrayList<>());
            return result;
        }
        
        List<Long> orderIds = orders.stream().map(FinanceOrder::getId).collect(Collectors.toList());
        
        // 查询商品销量排行
        List<Map<String, Object>> list = orderDetailMapper.selectMaps(new QueryWrapper<FinanceOrderDetail>()
                .select("item_name as name", "SUM(quantity) as value")
                .in("order_id", orderIds)
                .eq("item_type", "商品") // 假设区分商品和服务
                .groupBy("item_name")
                .orderByDesc("value")
                .last("LIMIT 10")); // Top 10

        List<String> categories = list.stream().map(m -> (String) m.get("name")).collect(Collectors.toList());
        List<BigDecimal> data = list.stream().map(m -> (BigDecimal) m.get("value")).collect(Collectors.toList());

        result.put("categories", categories);
        result.put("data", data);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getDailyRevenueList(String startDate, String endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<FinanceOrder> orders = orderMapper.selectList(new QueryWrapper<FinanceOrder>()
                .ge("create_time", startDate)
                .le("create_time", endDate)
                .eq("payment_status", "已支付")
                .orderByDesc("create_time"));

        // 按日期分组
        Map<String, List<FinanceOrder>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        // 排序日期
        List<String> sortedDates = new ArrayList<>(groupedByDate.keySet());
        sortedDates.sort(Collections.reverseOrder());

        for (String date : sortedDates) {
            List<FinanceOrder> dailyOrders = groupedByDate.get(date);
            
            BigDecimal revenue = dailyOrders.stream().map(FinanceOrder::getPaymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            int count = dailyOrders.size();
            BigDecimal avg = count > 0 ? revenue.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
            
            BigDecimal cash = dailyOrders.stream()
                    .filter(o -> "现金".equals(o.getPaymentMethod()))
                    .map(FinanceOrder::getPaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal card = dailyOrders.stream()
                    .filter(o -> "会员卡".equals(o.getPaymentMethod()))
                    .map(FinanceOrder::getPaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> item = new HashMap<>();
            item.put("date", date);
            item.put("revenue", revenue);
            item.put("orderCount", count);
            item.put("avgOrderValue", avg);
            item.put("cashPayment", cash);
            item.put("cardPayment", card);
            
            result.add(item);
        }
        
        return result;
    }
}
