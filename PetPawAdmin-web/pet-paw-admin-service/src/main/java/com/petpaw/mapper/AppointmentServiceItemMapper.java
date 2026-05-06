package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.AppointmentServiceItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约服务项目Mapper接口
 */
@Mapper
public interface AppointmentServiceItemMapper extends BaseMapper<AppointmentServiceItem> {
}
