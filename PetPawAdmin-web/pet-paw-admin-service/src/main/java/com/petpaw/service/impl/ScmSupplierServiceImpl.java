package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.ScmSupplier;
import com.petpaw.mapper.ScmSupplierMapper;
import com.petpaw.service.ScmSupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * SCM供应商服务实现类
 */
@Service
@RequiredArgsConstructor
public class ScmSupplierServiceImpl extends ServiceImpl<ScmSupplierMapper, ScmSupplier> implements ScmSupplierService {

    private static final String SUPPLIER_CODE_PREFIX = "SUP";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public IPage<ScmSupplier> listSuppliers(PageRequest pageRequest, String supplierName, Integer cooperationStatus, String contact, String level) {
        Page<ScmSupplier> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<ScmSupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(supplierName), ScmSupplier::getSupplierName, supplierName)
                .like(StringUtils.hasText(contact), ScmSupplier::getContactPerson, contact)
                .eq(StringUtils.hasText(level), ScmSupplier::getLevel, level)
                .eq(cooperationStatus != null, ScmSupplier::getCooperationStatus, cooperationStatus);
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean saveSupplier(ScmSupplier supplier) {
        // 后端自动生成编码，格式：SUP + yyyyMMddHHmmss + 4位随机字符
        supplier.setCoding(generateSupplierCode());
        supplier.setCreateTime(java.time.LocalDateTime.now());
        supplier.setUpdateTime(java.time.LocalDateTime.now());
        return save(supplier);
    }

    /**
     * 生成供应商编码
     * 格式：SUP + yyyyMMddHHmmss + UUID的前4位
     */
    private String generateSupplierCode() {
        String timestamp = java.time.LocalDateTime.now().format(DATE_FORMATTER);
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return SUPPLIER_CODE_PREFIX + timestamp + random;
    }

    @Override
    @Transactional
    public boolean updateSupplier(ScmSupplier supplier) {
        supplier.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(supplier);
    }

    @Override
    @Transactional
    public boolean deleteSupplier(Long id) {
        ScmSupplier supplier = new ScmSupplier();
        supplier.setId(id);
        supplier.setDeleted(1);
        supplier.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(supplier);
    }
}
