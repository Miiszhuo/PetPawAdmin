package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.CrmPet;

/**
 * CRM宠物服务接口
 */
public interface CrmPetService extends IService<CrmPet> {

    /**
     * 分页查询宠物
     *
     * @param pageRequest 分页参数
     * @param customerId  客户ID
     * @param petName     宠物名称
     * @param petType     宠物类型
     * @param breed       品种
     * @param ownerName   主人姓名
     * @param status      状态
     * @return 分页结果
     */
    IPage<CrmPet> listPets(PageRequest pageRequest, Long customerId, String petName, String petType, String breed, String ownerName, String status);

    /**
     * 创建宠物
     */
    boolean savePet(CrmPet pet);

    /**
     * 更新宠物
     */
    boolean updatePet(CrmPet pet);

    /**
     * 删除宠物
     */
    boolean deletePet(Long id);
}
