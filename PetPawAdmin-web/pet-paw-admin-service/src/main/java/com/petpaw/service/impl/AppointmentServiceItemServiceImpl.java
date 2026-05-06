package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.AppointmentServiceItem;
import com.petpaw.entity.WjFile;
import com.petpaw.mapper.AppointmentServiceItemMapper;
import com.petpaw.service.AppointmentServiceItemService;
import com.petpaw.service.WjFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 预约服务项目服务实现类
 */
@Service
@RequiredArgsConstructor
public class AppointmentServiceItemServiceImpl extends ServiceImpl<AppointmentServiceItemMapper, AppointmentServiceItem> implements AppointmentServiceItemService {

    private final WjFileService wjFileService;

    @Value("${aliyun.oss.url-prefix}")
    private String ossUrlPrefix;

    @Override
    public IPage<AppointmentServiceItem> listServiceItems(PageRequest pageRequest, String serviceName, String category, Integer status, String requiredSkill) {
        Page<AppointmentServiceItem> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<AppointmentServiceItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AppointmentServiceItem::getDeleted, 0)
                .like(StringUtils.hasText(serviceName), AppointmentServiceItem::getServiceName, serviceName)
                .like(StringUtils.hasText(category), AppointmentServiceItem::getCategory, category)
                .like(StringUtils.hasText(requiredSkill), AppointmentServiceItem::getRequiredSkills, requiredSkill)
                .eq(status != null, AppointmentServiceItem::getStatus, status);
        
        IPage<AppointmentServiceItem> result = baseMapper.selectPage(page, queryWrapper);
        
        // 填充图片信息
        result.getRecords().forEach(item -> {
            List<WjFile> files = wjFileService.getFiles(item.getId(), "SERVICE");
            if (!files.isEmpty()) {
                String path = files.get(0).getFilePath();
                if (path != null && !path.startsWith("http")) {
                    path = ossUrlPrefix + path;
                }
                item.setImageUrl(path);
            }
        });
        
        return result;
    }

    @Override
    @Transactional
    public boolean saveServiceItem(AppointmentServiceItem serviceItem) {
        serviceItem.setCreateTime(java.time.LocalDateTime.now());
        serviceItem.setUpdateTime(java.time.LocalDateTime.now());
        if (serviceItem.getStandardDuration() == null) {
            serviceItem.setStandardDuration(60);
        }
        boolean success = save(serviceItem);
        
        if (success && StringUtils.hasText(serviceItem.getImageUrl())) {
            wjFileService.bindFile(serviceItem.getImageUrl(), serviceItem.getId(), "SERVICE");
        }
        return success;
    }

    @Override
    @Transactional
    public boolean updateServiceItem(AppointmentServiceItem serviceItem) {
        serviceItem.setUpdateTime(java.time.LocalDateTime.now());
        boolean success = updateById(serviceItem);
        
        if (success && StringUtils.hasText(serviceItem.getImageUrl())) {
            wjFileService.bindFile(serviceItem.getImageUrl(), serviceItem.getId(), "SERVICE");
        }
        return success;
    }

    @Override
    @Transactional
    public boolean deleteServiceItem(Long id) {
        boolean success = removeById(id);
        if (success) {
            wjFileService.deleteFiles(id, "SERVICE");
        }
        return success;
    }
}
