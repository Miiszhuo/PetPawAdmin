package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.FinanceOrder;
import com.petpaw.entity.FinanceOrderDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 财务订单服务接口
 */
public interface FinanceOrderService extends IService<FinanceOrder> {

    /**
     * 分页查询订单
     */
    IPage<FinanceOrder> listOrders(PageRequest pageRequest, String orderNumber, Long customerId, String orderStatus);

    /**
     * 创建订单
     */
    boolean saveOrder(FinanceOrder order);

    /**
     * 更新订单
     */
    boolean updateOrder(FinanceOrder order);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long id, String orderStatus, String paymentStatus);

    /**
     * 订单支付
     */
    boolean payOrder(Long id, String paymentMethod, Long cashierId);

    /**
     * 获取每日汇总数据
     */
    Map<String, Object> getDailySummary(LocalDate date);

    /**
     * 获取每日订单列表
     */
    IPage<FinanceOrder> getDailyOrders(PageRequest pageRequest, LocalDate date, String orderStatus, String paymentStatus);

    /**
     * 创建并支付订单
     */
    boolean createAndPayOrder(FinanceOrder order, List<FinanceOrderDetail> orderDetails);

    /**
     * 订单退款
     */
    boolean refundOrder(Long orderId, BigDecimal refundAmount, String refundReason, Long cashierId);

    /**
     * 获取挂单列表
     */
    List<FinanceOrder> getPendingOrders();

    /**
     * 恢复挂单
     */
    boolean resumePendingOrder(Long orderId, Long cashierId);

    /**
     * 取消挂单
     */
    boolean cancelPendingOrder(Long orderId, String cancelReason, Long cashierId);

    /**
     * 获取交班报表
     */
    Map<String, Object> getShiftReport(Long cashierId, String startTime, String endTime);
}
