package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.exception.BusinessException;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.FinanceOrder;
import com.petpaw.entity.FinanceOrderDetail;
import com.petpaw.mapper.FinanceOrderDetailMapper;
import com.petpaw.mapper.FinanceOrderMapper;
import com.petpaw.service.FinanceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务订单服务实现类
 */
@Service
@RequiredArgsConstructor
public class FinanceOrderServiceImpl extends ServiceImpl<FinanceOrderMapper, FinanceOrder> implements FinanceOrderService {

    private final FinanceOrderDetailMapper orderDetailMapper;

    @Override
    public IPage<FinanceOrder> listOrders(PageRequest pageRequest, String orderNumber, Long customerId, String orderStatus) {
        Page<FinanceOrder> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<FinanceOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(orderNumber), FinanceOrder::getOrderNumber, orderNumber)
                .eq(customerId != null, FinanceOrder::getCustomerId, customerId)
                .eq(StringUtils.hasText(orderStatus), FinanceOrder::getOrderStatus, orderStatus)
                .orderByDesc(FinanceOrder::getCreateTime);
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean saveOrder(FinanceOrder order) {
        // 生成订单编号
        if (!StringUtils.hasText(order.getOrderNumber())) {
            order.setOrderNumber("FO" + System.currentTimeMillis());
        }
        order.setCreateTime(java.time.LocalDateTime.now());
        order.setUpdateTime(java.time.LocalDateTime.now());
        return save(order);
    }

    @Override
    @Transactional
    public boolean updateOrder(FinanceOrder order) {
        order.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long id, String orderStatus, String paymentStatus) {
        FinanceOrder order = new FinanceOrder();
        order.setId(id);
        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);
        order.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean payOrder(Long id, String paymentMethod, Long cashierId) {
        FinanceOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if ("已支付".equals(order.getPaymentStatus())) {
            throw new BusinessException("订单已支付");
        }

        order.setPaymentMethod(paymentMethod);
        order.setCashierId(cashierId);
        order.setPaymentStatus("已支付");
        order.setPaymentTime(java.time.LocalDateTime.now());
        order.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(order);
    }

    @Override
    public Map<String, Object> getDailySummary(LocalDate date) {
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

        LambdaQueryWrapper<FinanceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(FinanceOrder::getCreateTime, startOfDay, endOfDay)
               .eq(FinanceOrder::getDeleted, 0);

        List<FinanceOrder> orders = baseMapper.selectList(wrapper);

        Map<String, Object> summary = new HashMap<>();
        summary.put("date", date.toString());
        summary.put("totalOrders", orders.size());
        summary.put("paidOrders", orders.stream().filter(o -> "已支付".equals(o.getPaymentStatus())).count());
        summary.put("totalAmount", orders.stream().map(FinanceOrder::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("paidAmount", orders.stream().filter(o -> "已支付".equals(o.getPaymentStatus()))
                                        .map(FinanceOrder::getPaymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        return summary;
    }

    @Override
    public IPage<FinanceOrder> getDailyOrders(PageRequest pageRequest, LocalDate date, String orderStatus, String paymentStatus) {
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

        Page<FinanceOrder> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<FinanceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(FinanceOrder::getCreateTime, startOfDay, endOfDay)
               .eq(StringUtils.hasText(orderStatus), FinanceOrder::getOrderStatus, orderStatus)
               .eq(StringUtils.hasText(paymentStatus), FinanceOrder::getPaymentStatus, paymentStatus)
               .orderByDesc(FinanceOrder::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public boolean createAndPayOrder(FinanceOrder order, List<FinanceOrderDetail> orderDetails) {
        try {
            // 保存订单
            if (!saveOrder(order)) {
                return false;
            }

            // 保存订单明细
            if (orderDetails != null && !orderDetails.isEmpty()) {
                for (FinanceOrderDetail detail : orderDetails) {
                    detail.setOrderId(order.getId());
                    detail.setCreateTime(LocalDateTime.now());
                    orderDetailMapper.insert(detail);
                }
            }

            // 支付订单
            return payOrder(order.getId(), order.getPaymentMethod(), order.getCashierId());
        } catch (Exception e) {
            throw new BusinessException("创建并支付订单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean refundOrder(Long orderId, BigDecimal refundAmount, String refundReason, Long cashierId) {
        FinanceOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"已支付".equals(order.getPaymentStatus())) {
            throw new BusinessException("只有已支付的订单才能退款");
        }
        if (refundAmount.compareTo(order.getPaymentAmount()) > 0) {
            throw new BusinessException("退款金额不能超过订单金额");
        }

        // 这里应该有实际的退款逻辑，比如调用支付接口
        // 暂时只更新订单状态
        order.setOrderStatus("已退款");
        order.setPaymentStatus("已退款");
        order.setRemark((order.getRemark() != null ? order.getRemark() : "") + " [退款原因: " + refundReason + "]");
        order.setUpdateTime(LocalDateTime.now());

        return updateById(order);
    }

    @Override
    public List<FinanceOrder> getPendingOrders() {
        LambdaQueryWrapper<FinanceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceOrder::getOrderStatus, "挂单")
               .eq(FinanceOrder::getDeleted, 0)
               .orderByDesc(FinanceOrder::getCreateTime);

        return baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public boolean resumePendingOrder(Long orderId, Long cashierId) {
        FinanceOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"挂单".equals(order.getOrderStatus())) {
            throw new BusinessException("只有挂单状态的订单才能恢复");
        }

        order.setOrderStatus("进行中");
        order.setCashierId(cashierId);
        order.setUpdateTime(LocalDateTime.now());

        return updateById(order);
    }

    @Override
    @Transactional
    public boolean cancelPendingOrder(Long orderId, String cancelReason, Long cashierId) {
        FinanceOrder order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"挂单".equals(order.getOrderStatus())) {
            throw new BusinessException("只有挂单状态的订单才能取消");
        }

        order.setOrderStatus("已取消");
        order.setRemark((order.getRemark() != null ? order.getRemark() : "") + " [取消原因: " + cancelReason + "]");
        order.setUpdateTime(LocalDateTime.now());

        return updateById(order);
    }

    @Override
    public Map<String, Object> getShiftReport(Long cashierId, String startTime, String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LambdaQueryWrapper<FinanceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(FinanceOrder::getCreateTime, start, end)
               .eq(FinanceOrder::getCashierId, cashierId)
               .eq(FinanceOrder::getDeleted, 0);

        List<FinanceOrder> orders = baseMapper.selectList(wrapper);

        Map<String, Object> report = new HashMap<>();
        report.put("cashierId", cashierId);
        report.put("startTime", startTime);
        report.put("endTime", endTime);
        report.put("totalOrders", orders.size());
        report.put("paidOrders", orders.stream().filter(o -> "已支付".equals(o.getPaymentStatus())).count());
        report.put("totalAmount", orders.stream().map(FinanceOrder::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        report.put("paidAmount", orders.stream().filter(o -> "已支付".equals(o.getPaymentStatus()))
                                        .map(FinanceOrder::getPaymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        return report;
    }
}
