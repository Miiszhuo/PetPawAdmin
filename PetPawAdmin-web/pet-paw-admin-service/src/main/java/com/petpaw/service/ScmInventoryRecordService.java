package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.ScmInventoryRecord;

/**
 * 库存记录服务接口
 */
public interface ScmInventoryRecordService extends IService<ScmInventoryRecord> {

    /**
     * 分页查询库存记录
     */
    IPage<ScmInventoryRecord> listInventoryRecords(PageRequest pageRequest, Long productId,
                                                  String recordType, String startTime, String endTime);

    /**
     * 入库操作
     */
    boolean inbound(Long productId, Integer quantity, Long supplierId, String remark, String createBy);

    /**
     * 出库操作
     */
    boolean outbound(Long productId, Integer quantity, Long orderId, String remark, String createBy);

    /**
     * 库存调整
     */
    boolean adjustStock(Long productId, Integer newQuantity, String remark, String createBy);

    /**
     * 盘点操作
     */
    boolean stocktaking(Long productId, Integer actualQuantity, String remark, String createBy);

    /**
     * 获取商品当前库存
     */
    Integer getCurrentStock(Long productId);

    /**
     * 获取库存预警商品列表
     */
    java.util.List<com.petpaw.entity.ScmProduct> getLowStockProducts();
}
