package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.AppointmentOrder;

/**
 * 预约订单服务接口
 */
public interface AppointmentOrderService extends IService<AppointmentOrder> {

    /**
     * 分页查询预约订单
     */
    IPage<AppointmentOrder> listOrders(PageRequest pageRequest, String orderNumber, Long customerId, String status, String customerName, String serviceType, String startDate, String endDate);

    /**
     * 创建预约订单
     */
    boolean saveOrder(AppointmentOrder order);

    /**
     * 更新预约订单
     */
    boolean updateOrder(AppointmentOrder order);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long id, String status);

    /**
     * 取消订单
     */
    boolean cancelOrder(Long id);
}
