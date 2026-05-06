package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.exception.BusinessException;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.ScmInventoryRecord;
import com.petpaw.entity.ScmProduct;
import com.petpaw.mapper.ScmInventoryRecordMapper;
import com.petpaw.mapper.ScmProductMapper;
import com.petpaw.service.ScmInventoryRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScmInventoryRecordServiceImpl extends ServiceImpl<ScmInventoryRecordMapper, ScmInventoryRecord>
        implements ScmInventoryRecordService {

    private final ScmInventoryRecordMapper inventoryRecordMapper;
    private final ScmProductMapper productMapper;

    @Override
    public IPage<ScmInventoryRecord> listInventoryRecords(PageRequest pageRequest, Long productId,
                                                          String recordType, String startTime, String endTime) {
        LambdaQueryWrapper<ScmInventoryRecord> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (productId != null) {
            wrapper.eq(ScmInventoryRecord::getProductId, productId);
        }
        if (StringUtils.hasText(recordType)) {
            wrapper.eq(ScmInventoryRecord::getRecordType, recordType);
        }
        if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
            wrapper.between(ScmInventoryRecord::getCreateTime, startTime, endTime);
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(ScmInventoryRecord::getCreateTime);

        // 分页查询
        Page<ScmInventoryRecord> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        return this.page(page, wrapper);
    }

    @Override
    @Transactional
    public boolean inbound(Long productId, Integer quantity, Long supplierId, String remark, String createBy) {
        try {
            // 获取当前库存
            Integer currentStock = getCurrentStock(productId);
            if (currentStock == null) currentStock = 0;

            // 创建库存记录
            ScmInventoryRecord record = new ScmInventoryRecord();
            record.setProductId(productId);
            record.setRecordType("INBOUND");
            record.setQuantity(quantity);
            record.setBeforeQuantity(currentStock);
            record.setAfterQuantity(currentStock + quantity);
            record.setSupplierId(supplierId);
            record.setRemark(remark);
            record.setCreateBy(createBy);
            record.setCreateTime(LocalDateTime.now());

            // 保存记录
            this.save(record);

            // 更新商品库存
            updateProductStock(productId, currentStock + quantity);

            return true;
        } catch (Exception e) {
            log.error("入库操作失败", e);
            throw new BusinessException("入库操作失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean outbound(Long productId, Integer quantity, Long orderId, String remark, String createBy) {
        try {
            // 获取当前库存
            Integer currentStock = getCurrentStock(productId);
            if (currentStock == null || currentStock < quantity) {
                throw new BusinessException("库存不足，无法出库");
            }

            // 创建库存记录
            ScmInventoryRecord record = new ScmInventoryRecord();
            record.setProductId(productId);
            record.setRecordType("OUTBOUND");
            record.setQuantity(quantity);
            record.setBeforeQuantity(currentStock);
            record.setAfterQuantity(currentStock - quantity);
            record.setOrderId(orderId);
            record.setRemark(remark);
            record.setCreateBy(createBy);
            record.setCreateTime(LocalDateTime.now());

            // 保存记录
            this.save(record);

            // 更新商品库存
            updateProductStock(productId, currentStock - quantity);

            return true;
        } catch (Exception e) {
            log.error("出库操作失败", e);
            throw new BusinessException("出库操作失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean adjustStock(Long productId, Integer newQuantity, String remark, String createBy) {
        try {
            // 获取当前库存
            Integer currentStock = getCurrentStock(productId);
            if (currentStock == null) currentStock = 0;

            // 创建库存记录
            ScmInventoryRecord record = new ScmInventoryRecord();
            record.setProductId(productId);
            record.setRecordType("ADJUST");
            record.setQuantity(newQuantity - currentStock);
            record.setBeforeQuantity(currentStock);
            record.setAfterQuantity(newQuantity);
            record.setRemark(remark);
            record.setCreateBy(createBy);
            record.setCreateTime(LocalDateTime.now());

            // 保存记录
            this.save(record);

            // 更新商品库存
            updateProductStock(productId, newQuantity);

            return true;
        } catch (Exception e) {
            log.error("库存调整失败", e);
            throw new BusinessException("库存调整失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean stocktaking(Long productId, Integer actualQuantity, String remark, String createBy) {
        try {
            // 获取当前库存
            Integer currentStock = getCurrentStock(productId);
            if (currentStock == null) currentStock = 0;

            // 创建库存记录
            ScmInventoryRecord record = new ScmInventoryRecord();
            record.setProductId(productId);
            record.setRecordType("STOCKTAKING");
            record.setQuantity(actualQuantity - currentStock);
            record.setBeforeQuantity(currentStock);
            record.setAfterQuantity(actualQuantity);
            record.setRemark(remark);
            record.setCreateBy(createBy);
            record.setCreateTime(LocalDateTime.now());

            // 保存记录
            this.save(record);

            // 更新商品库存
            updateProductStock(productId, actualQuantity);

            return true;
        } catch (Exception e) {
            log.error("盘点操作失败", e);
            throw new BusinessException("盘点操作失败: " + e.getMessage());
        }
    }

    @Override
    public Integer getCurrentStock(Long productId) {
        ScmProduct product = productMapper.selectById(productId);
        return product != null ? product.getStockQuantity() : 0;
    }

    @Override
    public List<ScmProduct> getLowStockProducts() {
        // 使用原生SQL查询
        return productMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ScmProduct>()
                .apply("stock_quantity < min_stock_quantity")
                .eq("status", 1)
                .eq("deleted", 0)
        );
    }

    /**
     * 更新商品库存
     */
    private void updateProductStock(Long productId, Integer newStock) {
        ScmProduct product = new ScmProduct();
        product.setId(productId);
        product.setStockQuantity(newStock);
        productMapper.updateById(product);
    }
}
