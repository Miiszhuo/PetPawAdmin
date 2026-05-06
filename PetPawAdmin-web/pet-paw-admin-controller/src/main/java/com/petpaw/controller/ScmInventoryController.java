package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.ScmInventoryRecord;
import com.petpaw.entity.ScmProduct;
import com.petpaw.service.ScmInventoryRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/scm/inventory")
@RequiredArgsConstructor
public class ScmInventoryController {

    private final ScmInventoryRecordService inventoryService;

    /**
     * 分页查询库存记录
     */
    @GetMapping("/records")
    public Result listInventoryRecords(PageRequest pageRequest,
                                      @RequestParam(required = false) Long productId,
                                      @RequestParam(required = false) String recordType,
                                      @RequestParam(required = false) String startTime,
                                      @RequestParam(required = false) String endTime) {
        IPage<ScmInventoryRecord> page = inventoryService.listInventoryRecords(pageRequest,
                productId, recordType, startTime, endTime);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 获取商品当前库存
     */
    @GetMapping("/stock/{productId}")
    public Result getCurrentStock(@PathVariable Long productId) {
        Integer stock = inventoryService.getCurrentStock(productId);
        return Result.success(stock);
    }

    /**
     * 获取库存预警商品
     */
    @GetMapping("/warning")
    public Result getLowStockWarningProducts() {
        List<ScmProduct> products = inventoryService.getLowStockProducts();
        return Result.success(products);
    }

    /**
     * 获取低库存商品列表
     */
    @GetMapping("/low-stock-products")
    public Result getLowStockProducts() {
        List<ScmProduct> products = inventoryService.getLowStockProducts();
        return Result.success(products);
    }

    /**
     * 入库操作
     */
    @PostMapping("/inbound")
    public Result inbound(@RequestParam Long productId,
                         @RequestParam Integer quantity,
                         @RequestParam(required = false) Long supplierId,
                         @RequestParam(required = false) String remark,
                         @RequestParam String createBy) {
        boolean success = inventoryService.inbound(productId, quantity, supplierId, remark, createBy);
        return success ? Result.success("入库成功") : Result.error("入库失败");
    }

    /**
     * 出库操作
     */
    @PostMapping("/outbound")
    public Result outbound(@RequestParam Long productId,
                          @RequestParam Integer quantity,
                          @RequestParam(required = false) Long orderId,
                          @RequestParam(required = false) String remark,
                          @RequestParam String createBy) {
        boolean success = inventoryService.outbound(productId, quantity, orderId, remark, createBy);
        return success ? Result.success("出库成功") : Result.error("出库失败");
    }

    /**
     * 库存调整
     */
    @PostMapping("/adjust")
    public Result adjustStock(@RequestParam Long productId,
                             @RequestParam Integer newQuantity,
                             @RequestParam(required = false) String remark,
                             @RequestParam String createBy) {
        boolean success = inventoryService.adjustStock(productId, newQuantity, remark, createBy);
        return success ? Result.success("库存调整成功") : Result.error("库存调整失败");
    }

    /**
     * 盘点操作
     */
    @PostMapping("/stocktaking")
    public Result stocktaking(@RequestParam Long productId,
                             @RequestParam Integer actualQuantity,
                             @RequestParam(required = false) String remark,
                             @RequestParam String createBy) {
        boolean success = inventoryService.stocktaking(productId, actualQuantity, remark, createBy);
        return success ? Result.success("盘点成功") : Result.error("盘点失败");
    }
}
