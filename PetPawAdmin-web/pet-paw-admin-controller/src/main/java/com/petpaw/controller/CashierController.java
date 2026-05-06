package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.FinanceOrder;
import com.petpaw.service.FinanceOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收银管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/cashier")
@RequiredArgsConstructor
public class CashierController {

    private final FinanceOrderService orderService;

    /**
     * 获取今日营业数据
     */
    @GetMapping("/today/summary")
    public Result getTodaySummary() {
        LocalDate today = LocalDate.now();
        Map<String, Object> summary = orderService.getDailySummary(today);
        return Result.success(summary);
    }

    /**
     * 获取今日订单列表
     */
    @GetMapping("/today/orders")
    public Result getTodayOrders(PageRequest pageRequest,
                                @RequestParam(required = false) String orderStatus,
                                @RequestParam(required = false) String paymentStatus) {
        LocalDate today = LocalDate.now();
        IPage<FinanceOrder> page = orderService.getDailyOrders(pageRequest, today, orderStatus, paymentStatus);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 快速收银（创建并支付订单）
     */
    @PostMapping("/quick-checkout")
    public Result quickCheckout(@RequestBody QuickCheckoutRequest request) {
        try {
            FinanceOrder order = new FinanceOrder();
            order.setCustomerId(request.getCustomerId());
            order.setTotalAmount(request.getTotalAmount());
            order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
            order.setPaymentAmount(request.getActualAmount());
            order.setPaymentMethod(request.getPaymentMethod());
            order.setCashierId(request.getCashierId());
            order.setRemark(request.getRemarks());

            // 转换OrderDetail为FinanceOrderDetail
            List<com.petpaw.entity.FinanceOrderDetail> orderDetails = request.getOrderDetails().stream()
                .map(detail -> {
                    com.petpaw.entity.FinanceOrderDetail orderDetail = new com.petpaw.entity.FinanceOrderDetail();
                    orderDetail.setItemId(detail.getItemId());
                    orderDetail.setItemType(detail.getItemType());
                    orderDetail.setItemName(detail.getItemName());
                    orderDetail.setQuantity(detail.getQuantity());
                    orderDetail.setUnitPrice(detail.getUnitPrice());
                    orderDetail.setTotalAmount(detail.getTotalPrice());
                    return orderDetail;
                })
                .collect(Collectors.toList());

            boolean success = orderService.createAndPayOrder(order, orderDetails);
            return success ? Result.success("收银成功") : Result.error("收银失败");
        } catch (Exception e) {
            log.error("快速收银失败", e);
            return Result.error("收银失败: " + e.getMessage());
        }
    }

    /**
     * 订单退款
     */
    @PostMapping("/orders/{orderId}/refund")
    public Result refundOrder(@PathVariable Long orderId,
                             @RequestParam BigDecimal refundAmount,
                             @RequestParam String refundReason,
                             @RequestParam Long cashierId) {
        boolean success = orderService.refundOrder(orderId, refundAmount, refundReason, cashierId);
        return success ? Result.success("退款成功") : Result.error("退款失败");
    }

    /**
     * 挂单列表
     */
    @GetMapping("/pending-orders")
    public Result getPendingOrders() {
        var orders = orderService.getPendingOrders();
        return Result.success(orders);
    }

    /**
     * 恢复挂单
     */
    @PostMapping("/pending-orders/{orderId}/resume")
    public Result resumePendingOrder(@PathVariable Long orderId,
                                    @RequestParam Long cashierId) {
        boolean success = orderService.resumePendingOrder(orderId, cashierId);
        return success ? Result.success("挂单恢复成功") : Result.error("挂单恢复失败");
    }

    /**
     * 取消挂单
     */
    @PostMapping("/pending-orders/{orderId}/cancel")
    public Result cancelPendingOrder(@PathVariable Long orderId,
                                    @RequestParam String cancelReason,
                                    @RequestParam Long cashierId) {
        boolean success = orderService.cancelPendingOrder(orderId, cancelReason, cashierId);
        return success ? Result.success("挂单取消成功") : Result.error("挂单取消失败");
    }

    /**
     * 交班报表
     */
    @GetMapping("/shift-report")
    public Result getShiftReport(@RequestParam Long cashierId,
                                @RequestParam String startTime,
                                @RequestParam String endTime) {
        Map<String, Object> report = orderService.getShiftReport(cashierId, startTime, endTime);
        return Result.success(report);
    }

    /**
     * 快速收银请求类
     */
    public static class QuickCheckoutRequest {
        private Long customerId;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal actualAmount;
        private String paymentMethod;
        private Long cashierId;
        private String remarks;
        private java.util.List<OrderDetail> orderDetails;

        // Getters and Setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

        public BigDecimal getActualAmount() { return actualAmount; }
        public void setActualAmount(BigDecimal actualAmount) { this.actualAmount = actualAmount; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public Long getCashierId() { return cashierId; }
        public void setCashierId(Long cashierId) { this.cashierId = cashierId; }

        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }

        public java.util.List<OrderDetail> getOrderDetails() { return orderDetails; }
        public void setOrderDetails(java.util.List<OrderDetail> orderDetails) { this.orderDetails = orderDetails; }
    }

    /**
     * 订单明细类
     */
    public static class OrderDetail {
        private String itemType; // 商品 or 服务
        private Long itemId;
        private String itemName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private BigDecimal discountAmount;

        // Getters and Setters
        public String getItemType() { return itemType; }
        public void setItemType(String itemType) { this.itemType = itemType; }

        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    }
}
