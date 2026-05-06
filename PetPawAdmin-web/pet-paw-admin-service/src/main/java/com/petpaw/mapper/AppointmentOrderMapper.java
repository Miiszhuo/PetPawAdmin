package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.AppointmentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约订单Mapper接口
 */
@Mapper
public interface AppointmentOrderMapper extends BaseMapper<AppointmentOrder> {
}
