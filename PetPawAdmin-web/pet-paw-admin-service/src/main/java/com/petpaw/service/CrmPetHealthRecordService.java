package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.CrmPetHealthRecord;

/**
 * CRM宠物健康记录服务接口
 */
public interface CrmPetHealthRecordService extends IService<CrmPetHealthRecord> {

    /**
     * 分页查询健康记录
     */
    IPage<CrmPetHealthRecord> listHealthRecords(PageRequest pageRequest, Long petId, String recordType);

    /**
     * 创建健康记录
     */
    boolean saveHealthRecord(CrmPetHealthRecord record);

    /**
     * 更新健康记录
     */
    boolean updateHealthRecord(CrmPetHealthRecord record);

    /**
     * 删除健康记录
     */
    boolean deleteHealthRecord(Long id);
}
