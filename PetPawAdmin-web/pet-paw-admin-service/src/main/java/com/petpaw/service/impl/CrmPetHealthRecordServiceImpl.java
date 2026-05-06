package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.CrmPetHealthRecord;
import com.petpaw.mapper.CrmPetHealthRecordMapper;
import com.petpaw.service.CrmPetHealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * CRM宠物健康记录服务实现类
 */
@Service
@RequiredArgsConstructor
public class CrmPetHealthRecordServiceImpl extends ServiceImpl<CrmPetHealthRecordMapper, CrmPetHealthRecord> implements CrmPetHealthRecordService {

    @Override
    public IPage<CrmPetHealthRecord> listHealthRecords(PageRequest pageRequest, Long petId, String recordType) {
        Page<CrmPetHealthRecord> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<CrmPetHealthRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(petId != null, CrmPetHealthRecord::getPetId, petId)
                .like(StringUtils.hasText(recordType), CrmPetHealthRecord::getRecordType, recordType)
                .orderByDesc(CrmPetHealthRecord::getRecordDate);
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean saveHealthRecord(CrmPetHealthRecord record) {
        record.setCreateTime(java.time.LocalDateTime.now());
        record.setUpdateTime(java.time.LocalDateTime.now());
        return save(record);
    }

    @Override
    @Transactional
    public boolean updateHealthRecord(CrmPetHealthRecord record) {
        record.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(record);
    }

    @Override
    @Transactional
    public boolean deleteHealthRecord(Long id) {
        CrmPetHealthRecord record = new CrmPetHealthRecord();
        record.setId(id);
        record.setDeleted(1);
        record.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(record);
    }
}
