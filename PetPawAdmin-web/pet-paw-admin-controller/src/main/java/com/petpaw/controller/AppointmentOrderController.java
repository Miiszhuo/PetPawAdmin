package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.AppointmentOrder;
import com.petpaw.service.AppointmentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 预约订单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/appointment/orders")
@RequiredArgsConstructor
public class AppointmentOrderController {

    private final AppointmentOrderService orderService;

    /**
     * 分页查询预约订单
     */
    @GetMapping
    public Result listOrders(PageRequest pageRequest,
                            @RequestParam(required = false) String orderNumber,
                            @RequestParam(required = false) Long customerId,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String customerName,
                            @RequestParam(required = false) String serviceType,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate) {
        IPage<AppointmentOrder> page = orderService.listOrders(pageRequest, orderNumber, customerId, status, customerName, serviceType, startDate, endDate);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取预约订单
     */
    @GetMapping("/{id}")
    public Result getOrderById(@PathVariable Long id) {
        AppointmentOrder order = orderService.getById(id);
        return order != null ? Result.success(order) : Result.error("预约订单不存在");
    }

    /**
     * 创建预约订单
     */
    @PostMapping
    public Result createOrder(@RequestBody AppointmentOrder order) {
        boolean success = orderService.saveOrder(order);
        return success ? Result.success("预约订单创建成功") : Result.error("预约订单创建失败");
    }

    /**
     * 更新预约订单
     */
    @PutMapping("/{id}")
    public Result updateOrder(@PathVariable Long id, @RequestBody AppointmentOrder order) {
        order.setId(id);
        boolean success = orderService.updateOrder(order);
        return success ? Result.success("预约订单更新成功") : Result.error("预约订单更新失败");
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    public Result updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = orderService.updateOrderStatus(id, status);
        return success ? Result.success("订单状态更新成功") : Result.error("订单状态更新失败");
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result cancelOrder(@PathVariable Long id) {
        boolean success = orderService.cancelOrder(id);
        return success ? Result.success("订单取消成功") : Result.error("订单取消失败");
    }
}
