package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.CrmPet;
import com.petpaw.mapper.CrmPetMapper;
import com.petpaw.service.CrmPetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * CRM宠物服务实现类
 */
@Service
@RequiredArgsConstructor
public class CrmPetServiceImpl extends ServiceImpl<CrmPetMapper, CrmPet> implements CrmPetService {

    @Override
    public IPage<CrmPet> listPets(PageRequest pageRequest, Long customerId, String petName, String petType, String breed, String ownerName, String status) {
        Page<CrmPet> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<CrmPet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(customerId != null, CrmPet::getCustomerId, customerId)
                .like(StringUtils.hasText(petName), CrmPet::getPetName, petName)
                .like(StringUtils.hasText(petType), CrmPet::getPetType, petType)
                .like(StringUtils.hasText(breed), CrmPet::getBreed, breed)
                .eq(StringUtils.hasText(status), CrmPet::getStatus, status);
        
        if (StringUtils.hasText(ownerName)) {
            queryWrapper.inSql(CrmPet::getCustomerId, "SELECT id FROM crm_customer WHERE customer_name LIKE '%" + ownerName + "%'");
        }
        
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean savePet(CrmPet pet) {
        pet.setCreateTime(java.time.LocalDateTime.now());
        pet.setUpdateTime(java.time.LocalDateTime.now());
        if (pet.getStatus() == null) {
            pet.setStatus("active");
        }
        return save(pet);
    }

    @Override
    @Transactional
    public boolean updatePet(CrmPet pet) {
        pet.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(pet);
    }

    @Override
    @Transactional
    public boolean deletePet(Long id) {
        CrmPet pet = new CrmPet();
        pet.setId(id);
        pet.setDeleted(1);
        pet.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(pet);
    }
}
