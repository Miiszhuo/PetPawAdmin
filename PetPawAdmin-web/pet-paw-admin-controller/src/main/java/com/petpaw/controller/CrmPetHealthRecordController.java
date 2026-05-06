package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.CrmPetHealthRecord;
import com.petpaw.service.CrmPetHealthRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * CRM宠物健康记录管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/crm/health-records")
@RequiredArgsConstructor
public class CrmPetHealthRecordController {

    private final CrmPetHealthRecordService healthRecordService;

    /**
     * 分页查询健康记录
     */
    @GetMapping
    public Result listHealthRecords(PageRequest pageRequest,
                                   @RequestParam(required = false) Long petId,
                                   @RequestParam(required = false) String recordType) {
        IPage<CrmPetHealthRecord> page = healthRecordService.listHealthRecords(pageRequest, petId, recordType);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取健康记录
     */
    @GetMapping("/{id}")
    public Result getHealthRecordById(@PathVariable Long id) {
        CrmPetHealthRecord record = healthRecordService.getById(id);
        return record != null ? Result.success(record) : Result.error("健康记录不存在");
    }

    /**
     * 创建健康记录
     */
    @PostMapping
    public Result createHealthRecord(@RequestBody CrmPetHealthRecord record) {
        boolean success = healthRecordService.saveHealthRecord(record);
        return success ? Result.success("健康记录创建成功") : Result.error("健康记录创建失败");
    }

    /**
     * 更新健康记录
     */
    @PutMapping("/{id}")
    public Result updateHealthRecord(@PathVariable Long id, @RequestBody CrmPetHealthRecord record) {
        record.setId(id);
        boolean success = healthRecordService.updateHealthRecord(record);
        return success ? Result.success("健康记录更新成功") : Result.error("健康记录更新失败");
    }

    /**
     * 删除健康记录
     */
    @DeleteMapping("/{id}")
    public Result deleteHealthRecord(@PathVariable Long id) {
        boolean success = healthRecordService.deleteHealthRecord(id);
        return success ? Result.success("健康记录删除成功") : Result.error("健康记录删除失败");
    }
}
