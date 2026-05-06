package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.AppointmentStaffSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约员工排班Mapper接口
 */
@Mapper
public interface AppointmentStaffScheduleMapper extends BaseMapper<AppointmentStaffSchedule> {
}
