package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.entity.AppointmentOrder;
import com.petpaw.entity.AppointmentServiceItem;
import com.petpaw.entity.AppointmentStaffSchedule;
import com.petpaw.entity.CrmCustomer;
import com.petpaw.entity.CrmPet;
import com.petpaw.entity.HrEmployee;
import com.petpaw.mapper.AppointmentStaffScheduleMapper;
import com.petpaw.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预约员工排班服务实现类
 */
@Service
@RequiredArgsConstructor
public class AppointmentStaffScheduleServiceImpl extends ServiceImpl<AppointmentStaffScheduleMapper, AppointmentStaffSchedule> implements AppointmentStaffScheduleService {

    private final HrEmployeeService employeeService;
    private final AppointmentOrderService orderService;
    private final CrmCustomerService customerService;
    private final CrmPetService petService;
    private final AppointmentServiceItemService serviceItemService;

    @Override
    public List<Map<String, Object>> getScheduleBoard(LocalDate date) {
        // 1. 获取所有活跃的技师/美容师
        List<HrEmployee> staffList = employeeService.getActiveTechnicians();

        // 2. 获取该日期的所有排班
        List<AppointmentStaffSchedule> schedules = this.list(new LambdaQueryWrapper<AppointmentStaffSchedule>()
                .eq(AppointmentStaffSchedule::getWorkDate, date));
        Map<Long, List<AppointmentStaffSchedule>> scheduleMap = schedules.stream()
                .collect(Collectors.groupingBy(AppointmentStaffSchedule::getStaffId));

        // 3. 获取该日期的所有预约订单
        List<AppointmentOrder> orders = orderService.list(new LambdaQueryWrapper<AppointmentOrder>()
                .eq(AppointmentOrder::getAppointmentDate, date)
                .ne(AppointmentOrder::getStatus, "已取消"));
        
        // 3.1 填充订单关联信息 (客户名、宠物名、服务名)
        if (!orders.isEmpty()) {
            Set<Long> customerIds = orders.stream().map(AppointmentOrder::getCustomerId).collect(Collectors.toSet());
            Set<Long> petIds = orders.stream().map(AppointmentOrder::getPetId).collect(Collectors.toSet());
            Set<Long> serviceIds = orders.stream().map(AppointmentOrder::getServiceItemId).collect(Collectors.toSet());

            Map<Long, String> customerMap = customerIds.isEmpty() ? new HashMap<>() : 
                    customerService.listByIds(customerIds).stream()
                    .collect(Collectors.toMap(CrmCustomer::getId, CrmCustomer::getCustomerName));
            
            Map<Long, String> petMap = petIds.isEmpty() ? new HashMap<>() :
                    petService.listByIds(petIds).stream()
                    .collect(Collectors.toMap(CrmPet::getId, CrmPet::getPetName));

            Map<Long, String> serviceMap = serviceIds.isEmpty() ? new HashMap<>() :
                    serviceItemService.listByIds(serviceIds).stream()
                    .collect(Collectors.toMap(AppointmentServiceItem::getId, AppointmentServiceItem::getServiceName));

            for (AppointmentOrder order : orders) {
                order.setCustomerName(customerMap.getOrDefault(order.getCustomerId(), "未知客户"));
                order.setPetName(petMap.getOrDefault(order.getPetId(), "未知宠物"));
                order.setServiceName(serviceMap.getOrDefault(order.getServiceItemId(), "未知服务"));
            }
        }

        Map<Long, List<AppointmentOrder>> orderMap = orders.stream()
                .collect(Collectors.groupingBy(AppointmentOrder::getStaffId));

        // 4. 组装看板数据
        List<Map<String, Object>> board = new ArrayList<>();
        for (HrEmployee staff : staffList) {
            Map<String, Object> staffData = new HashMap<>();
            staffData.put("staffId", staff.getId());
            staffData.put("staffName", staff.getRealName());
            staffData.put("avatar", staff.getAvatar());
            staffData.put("positionName", staff.getPositionName());
            staffData.put("schedules", scheduleMap.getOrDefault(staff.getId(), new ArrayList<>()));
            staffData.put("appointments", orderMap.getOrDefault(staff.getId(), new ArrayList<>()));
            board.add(staffData);
        }

        return board;
    }

    @Override
    @Transactional
    public boolean saveSchedule(AppointmentStaffSchedule schedule) {
        schedule.setCreateTime(java.time.LocalDateTime.now());
        schedule.setUpdateTime(java.time.LocalDateTime.now());
        return this.save(schedule);
    }
}
