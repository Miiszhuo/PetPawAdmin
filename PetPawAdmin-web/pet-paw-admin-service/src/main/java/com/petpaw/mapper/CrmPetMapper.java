package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.CrmPet;
import org.apache.ibatis.annotations.Mapper;

/**
 * CRM宠物Mapper接口
 */
@Mapper
public interface CrmPetMapper extends BaseMapper<CrmPet> {
}
