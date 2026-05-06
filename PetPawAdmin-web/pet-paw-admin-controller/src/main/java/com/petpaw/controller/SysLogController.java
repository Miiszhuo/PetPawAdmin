package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.SysLog;
import com.petpaw.service.SysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 系统日志管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/system/logs")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLogService logService;

    /**
     * 分页查询系统日志
     */
    @GetMapping
    public Result listLogs(PageRequest pageRequest,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) String operationType,
                          @RequestParam(required = false) String operationModule,
                          @RequestParam(required = false) String startTime,
                          @RequestParam(required = false) String endTime) {
        IPage<SysLog> page = logService.listLogs(pageRequest, username, operationType,
                operationModule, startTime, endTime);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取日志详情
     */
    @GetMapping("/{id}")
    public Result getLogById(@PathVariable Long id) {
        SysLog log = logService.getById(id);
        return log != null ? Result.success(log) : Result.error("日志不存在");
    }

    /**
     * 记录系统日志
     */
    @PostMapping
    public Result saveLog(@RequestBody SysLog log) {
        boolean success = logService.saveLog(log);
        return success ? Result.success("日志记录成功") : Result.error("日志记录失败");
    }

    /**
     * 清理过期日志
     */
    @DeleteMapping("/expired/{days}")
    public Result cleanExpiredLogs(@PathVariable Integer days) {
        boolean success = logService.cleanExpiredLogs(days);
        return success ? Result.success("过期日志清理成功") : Result.error("过期日志清理失败");
    }

    /**
     * 导出日志数据
     */
    @GetMapping("/export")
    public Result exportLogs(@RequestParam(required = false) String username,
                            @RequestParam(required = false) String operationType,
                            @RequestParam(required = false) String operationModule,
                            @RequestParam(required = false) String startTime,
                            @RequestParam(required = false) String endTime) {
        var logs = logService.exportLogs(username, operationType, operationModule, startTime, endTime);
        return Result.success(logs);
    }

    /**
     * 删除日志
     */
    @DeleteMapping("/{id}")
    public Result deleteLog(@PathVariable Long id) {
        boolean success = logService.removeById(id);
        return success ? Result.success("日志删除成功") : Result.error("日志删除失败");
    }
}
