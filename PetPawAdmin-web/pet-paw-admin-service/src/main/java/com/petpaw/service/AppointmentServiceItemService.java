package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.AppointmentServiceItem;

/**
 * 预约服务项目服务接口
 */
public interface AppointmentServiceItemService extends IService<AppointmentServiceItem> {

    /**
     * 分页查询服务项目
     */
    IPage<AppointmentServiceItem> listServiceItems(PageRequest pageRequest, String serviceName, String category, Integer status, String requiredSkill);

    /**
     * 创建服务项目
     */
    boolean saveServiceItem(AppointmentServiceItem serviceItem);

    /**
     * 更新服务项目
     */
    boolean updateServiceItem(AppointmentServiceItem serviceItem);

    /**
     * 删除服务项目
     */
    boolean deleteServiceItem(Long id);
}
