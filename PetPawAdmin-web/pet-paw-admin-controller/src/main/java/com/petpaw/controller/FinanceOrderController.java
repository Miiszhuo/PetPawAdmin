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

/**
 * 财务订单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/finance/orders")
@RequiredArgsConstructor
public class FinanceOrderController {

    private final FinanceOrderService orderService;

    /**
     * 分页查询订单
     */
    @GetMapping
    public Result listOrders(PageRequest pageRequest,
                            @RequestParam(required = false) String orderNumber,
                            @RequestParam(required = false) Long customerId,
                            @RequestParam(required = false) String orderStatus) {
        IPage<FinanceOrder> page = orderService.listOrders(pageRequest, orderNumber, customerId, orderStatus);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取订单
     */
    @GetMapping("/{id}")
    public Result getOrderById(@PathVariable Long id) {
        FinanceOrder order = orderService.getById(id);
        return order != null ? Result.success(order) : Result.error("订单不存在");
    }

    /**
     * 创建订单
     */
    @PostMapping
    public Result createOrder(@RequestBody FinanceOrder order) {
        boolean success = orderService.saveOrder(order);
        return success ? Result.success("订单创建成功") : Result.error("订单创建失败");
    }

    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    public Result updateOrder(@PathVariable Long id, @RequestBody FinanceOrder order) {
        order.setId(id);
        boolean success = orderService.updateOrder(order);
        return success ? Result.success("订单更新成功") : Result.error("订单更新失败");
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    public Result updateOrderStatus(@PathVariable Long id,
                                   @RequestParam String orderStatus,
                                   @RequestParam String paymentStatus) {
        boolean success = orderService.updateOrderStatus(id, orderStatus, paymentStatus);
        return success ? Result.success("订单状态更新成功") : Result.error("订单状态更新失败");
    }

    /**
     * 订单支付
     */
    @PutMapping("/{id}/pay")
    public Result payOrder(@PathVariable Long id,
                          @RequestParam String paymentMethod,
                          @RequestParam Long cashierId) {
        boolean success = orderService.payOrder(id, paymentMethod, cashierId);
        return success ? Result.success("订单支付成功") : Result.error("订单支付失败");
    }
}
