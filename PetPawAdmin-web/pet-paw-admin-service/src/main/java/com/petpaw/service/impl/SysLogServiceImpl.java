package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.SysLog;
import com.petpaw.mapper.SysLogMapper;
import com.petpaw.service.SysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 系统日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    private final SysLogMapper logMapper;

    @Override
    public IPage<SysLog> listLogs(PageRequest pageRequest, String username, String operationType,
                                  String operationModule, String startTime, String endTime) {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (StringUtils.hasText(username)) {
            wrapper.like(SysLog::getUsername, username);
        }
        if (StringUtils.hasText(operationType)) {
            wrapper.eq(SysLog::getOperationType, operationType);
        }
        if (StringUtils.hasText(operationModule)) {
            wrapper.eq(SysLog::getOperationModule, operationModule);
        }
        if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
            wrapper.between(SysLog::getCreateTime, startTime, endTime);
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(SysLog::getCreateTime);

        // 分页查询
        Page<SysLog> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        return this.page(page, wrapper);
    }

    @Override
    @Transactional
    public boolean saveLog(SysLog log) {
        try {
            return this.save(log);
        } catch (Exception e) {
            // 这里不能使用log变量，因为和参数名冲突
            System.err.println("保存系统日志失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean cleanExpiredLogs(Integer days) {
        try {
            LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
            String expireTimeStr = expireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.lt(SysLog::getCreateTime, expireTimeStr);

            return this.remove(wrapper);
        } catch (Exception e) {
            log.error("清理过期日志失败", e);
            return false;
        }
    }

    @Override
    public List<SysLog> exportLogs(String username, String operationType, String operationModule,
                                   String startTime, String endTime) {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件（同listLogs）
        if (StringUtils.hasText(username)) {
            wrapper.like(SysLog::getUsername, username);
        }
        if (StringUtils.hasText(operationType)) {
            wrapper.eq(SysLog::getOperationType, operationType);
        }
        if (StringUtils.hasText(operationModule)) {
            wrapper.eq(SysLog::getOperationModule, operationModule);
        }
        if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
            wrapper.between(SysLog::getCreateTime, startTime, endTime);
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(SysLog::getCreateTime);

        return this.list(wrapper);
    }
}
