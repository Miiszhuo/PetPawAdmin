package com.petpaw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.entity.AppointmentStaffSchedule;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预约员工排班服务接口
 */
public interface AppointmentStaffScheduleService extends IService<AppointmentStaffSchedule> {

    /**
     * 获取指定日期的排班看板数据
     * @param date 日期
     * @return 看板数据
     */
    List<Map<String, Object>> getScheduleBoard(LocalDate date);

    /**
     * 保存排班
     * @param schedule 排班信息
     * @return 是否成功
     */
    boolean saveSchedule(AppointmentStaffSchedule schedule);
}
