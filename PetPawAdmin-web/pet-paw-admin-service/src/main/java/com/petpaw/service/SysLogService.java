package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.SysLog;

/**
 * 系统日志服务接口
 */
public interface SysLogService extends IService<SysLog> {

    /**
     * 分页查询系统日志
     */
    IPage<SysLog> listLogs(PageRequest pageRequest, String username, String operationType,
                          String operationModule, String startTime, String endTime);

    /**
     * 记录系统日志
     */
    boolean saveLog(SysLog log);

    /**
     * 清理过期日志
     */
    boolean cleanExpiredLogs(Integer days);

    /**
     * 导出日志数据
     */
    java.util.List<SysLog> exportLogs(String username, String operationType,
                                     String operationModule, String startTime, String endTime);
}
