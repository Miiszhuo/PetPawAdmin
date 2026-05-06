package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.ScmSupplier;
import com.petpaw.service.ScmSupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * SCM供应商管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/scm/suppliers")
@RequiredArgsConstructor
public class ScmSupplierController {

    private final ScmSupplierService supplierService;

    /**
     * 分页查询供应商
     */
    @GetMapping
    public Result listSuppliers(PageRequest pageRequest,
                               @RequestParam(required = false) String supplierName,
                               @RequestParam(required = false) Integer cooperationStatus,
                               @RequestParam(required = false) String contact,
                               @RequestParam(required = false) String level) {
        IPage<ScmSupplier> page = supplierService.listSuppliers(pageRequest, supplierName, cooperationStatus, contact, level);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取供应商
     */
    @GetMapping("/{id}")
    public Result getSupplierById(@PathVariable Long id) {
        ScmSupplier supplier = supplierService.getById(id);
        return supplier != null ? Result.success(supplier) : Result.error("供应商不存在");
    }

    /**
     * 创建供应商
     */
    @PostMapping
    public Result createSupplier(@RequestBody ScmSupplier supplier) {
        boolean success = supplierService.saveSupplier(supplier);
        return success ? Result.success("供应商创建成功") : Result.error("供应商创建失败");
    }

    /**
     * 更新供应商
     */
    @PutMapping("/{id}")
    public Result updateSupplier(@PathVariable Long id, @RequestBody ScmSupplier supplier) {
        supplier.setId(id);
        boolean success = supplierService.updateSupplier(supplier);
        return success ? Result.success("供应商更新成功") : Result.error("供应商更新失败");
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/{id}")
    public Result deleteSupplier(@PathVariable Long id) {
        boolean success = supplierService.deleteSupplier(id);
        return success ? Result.success("供应商删除成功") : Result.error("供应商删除失败");
    }
}
