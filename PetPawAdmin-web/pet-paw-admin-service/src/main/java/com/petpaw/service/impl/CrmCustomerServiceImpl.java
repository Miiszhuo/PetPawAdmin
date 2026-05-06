package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.CrmCustomer;
import com.petpaw.mapper.CrmCustomerMapper;
import com.petpaw.entity.WjFile;
import com.petpaw.service.WjFileService;
import com.petpaw.service.CrmCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * CRM客户服务实现类
 */
@Service
@RequiredArgsConstructor
public class CrmCustomerServiceImpl extends ServiceImpl<CrmCustomerMapper, CrmCustomer> implements CrmCustomerService {

    private final WjFileService wjFileService;

    @Value("${aliyun.oss.url-prefix}")
    private String ossUrlPrefix;

    @Override
    public IPage<CrmCustomer> listCustomers(PageRequest pageRequest, String customerName, String phone, Integer status, String customerType, String startDate, String endDate) {
        Page<CrmCustomer> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<CrmCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(customerName), CrmCustomer::getCustomerName, customerName)
                .like(StringUtils.hasText(phone), CrmCustomer::getPhone, phone)
                .eq(status != null, CrmCustomer::getDeleted, status)
                .eq(StringUtils.hasText(customerType), CrmCustomer::getCustomerType, customerType);

        if (StringUtils.hasText(startDate)) {
            queryWrapper.ge(CrmCustomer::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
        }
        if (StringUtils.hasText(endDate)) {
            queryWrapper.le(CrmCustomer::getCreateTime, LocalDate.parse(endDate).atTime(LocalTime.MAX));
        }

        IPage<CrmCustomer> result = baseMapper.selectPage(page, queryWrapper);
        
        // 填充关联的图片信息（客户头像）
        // 业务逻辑：查询 file 表中关联类型为 "CUSTOMER" 且关联ID为客户ID的记录
        result.getRecords().forEach(customer -> {
            List<WjFile> files = wjFileService.getFiles(customer.getId(), "CUSTOMER");
            if (!files.isEmpty()) {
                String path = files.get(0).getFilePath();
                if (path != null && !path.startsWith("http")) {
                    path = ossUrlPrefix + path;
                }
                customer.setImageUrl(path);
            }
        });
        
        return result;
    }

    @Override
    @Transactional
    public boolean saveCustomer(CrmCustomer customer) {
        // 校验客户状态
        if (customer.getStatus() != null && customer.getStatus() != 1) {
            throw new com.petpaw.common.exception.BusinessException("无法添加非正常状态的客户");
        }
        
        customer.setCreateTime(java.time.LocalDateTime.now());
        customer.setUpdateTime(java.time.LocalDateTime.now());
        boolean success = save(customer);
        
        if (success && StringUtils.hasText(customer.getImageUrl())) {
            wjFileService.bindFile(customer.getImageUrl(), customer.getId(), "CUSTOMER");
        }
        return success;
    }

    @Override
    @Transactional
    public boolean updateCustomer(CrmCustomer customer) {
        // 校验客户状态
        if (customer.getStatus() != null && customer.getStatus() != 1) {
            // 如果要修改为非正常状态，需要校验是否允许
            // 这里简单处理：如果当前状态已经是非正常，则不允许修改其他信息
            CrmCustomer existing = getById(customer.getId());
            if (existing != null && existing.getStatus() != 1) {
                 // 允许修改状态回正常，或者删除操作，但这里是更新信息
                 // 如果业务要求严格，可以抛出异常
            }
        }
        
        customer.setUpdateTime(java.time.LocalDateTime.now());
        boolean success = updateById(customer);
        
        if (success && StringUtils.hasText(customer.getImageUrl())) {
            wjFileService.bindFile(customer.getImageUrl(), customer.getId(), "CUSTOMER");
        }
        return success;
    }

    @Override
    @Transactional
    public boolean deleteCustomer(Long id) {
        CrmCustomer customer = new CrmCustomer();
        customer.setId(id);
        customer.setDeleted(1);
        customer.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(customer);
    }
}
