package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.CrmPetHealthRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * CRM宠物健康记录Mapper接口
 */
@Mapper
public interface CrmPetHealthRecordMapper extends BaseMapper<CrmPetHealthRecord> {
}
