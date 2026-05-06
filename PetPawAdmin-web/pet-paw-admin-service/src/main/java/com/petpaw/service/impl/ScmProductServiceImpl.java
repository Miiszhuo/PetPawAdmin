package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.exception.BusinessException;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.ScmProduct;
import com.petpaw.mapper.ScmProductMapper;
import com.petpaw.entity.WjFile;
import com.petpaw.service.WjFileService;
import com.petpaw.service.ScmProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * SCM商品服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScmProductServiceImpl extends ServiceImpl<ScmProductMapper, ScmProduct> implements ScmProductService {

    private final WjFileService wjFileService;

    @Value("${aliyun.oss.url-prefix}")
    private String ossUrlPrefix;

    @Override
    public IPage<ScmProduct> listProducts(PageRequest pageRequest, String productName, String category, Integer status, Long supplierId, String stockStatus, String brand) {
        Page<ScmProduct> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<ScmProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScmProduct::getDeleted, 0)
                .like(StringUtils.hasText(productName), ScmProduct::getProductName, productName)
                .like(StringUtils.hasText(category), ScmProduct::getCategory, category)
                .like(StringUtils.hasText(brand), ScmProduct::getBrand, brand)
                .eq(status != null, ScmProduct::getStatus, status)
                .eq(supplierId != null, ScmProduct::getSupplierId, supplierId);

        if (StringUtils.hasText(stockStatus)) {
            if ("low".equals(stockStatus)) {
                queryWrapper.apply("stock_quantity <= min_stock_quantity");
            } else if ("out".equals(stockStatus)) {
                queryWrapper.le(ScmProduct::getStockQuantity, 0);
            } else if ("normal".equals(stockStatus)) {
                queryWrapper.apply("stock_quantity > min_stock_quantity");
            }
        }
        IPage<ScmProduct> result = baseMapper.selectPage(page, queryWrapper);
        
        // 填充图片信息
        result.getRecords().forEach(product -> {
            List<WjFile> files = wjFileService.getFiles(product.getId(), "PRODUCT");
            log.info("Product ID: {}, Files found: {}", product.getId(), files.size());
            if (!files.isEmpty()) {
                String path = files.get(0).getFilePath();
                log.info("Original Path: {}", path);
                if (path != null && !path.startsWith("http")) {
                    path = ossUrlPrefix + path;
                }
                product.setImageUrl(path);
                log.info("Final Path: {}", path);
            }
        });
        
        return result;
    }

    @Override
    @Transactional
    public boolean saveProduct(ScmProduct product) {
        product.setCreateTime(java.time.LocalDateTime.now());
        product.setUpdateTime(java.time.LocalDateTime.now());
        boolean success = save(product);
        
        if (success && StringUtils.hasText(product.getImageUrl())) {
            wjFileService.bindFile(product.getImageUrl(), product.getId(), "PRODUCT");
        }
        return success;
    }

    @Override
    @Transactional
    public boolean updateProduct(ScmProduct product) {
        product.setUpdateTime(java.time.LocalDateTime.now());
        boolean success = updateById(product);
        
        if (success && StringUtils.hasText(product.getImageUrl())) {
            wjFileService.bindFile(product.getImageUrl(), product.getId(), "PRODUCT");
        }
        return success;
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        boolean success = removeById(id);
        if (success) {
            wjFileService.deleteFiles(id, "PRODUCT");
        }
        return success;
    }

    @Override
    @Transactional
    public boolean updateStock(Long id, Integer quantity) {
        ScmProduct product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStockQuantity(quantity);
        product.setUpdateTime(java.time.LocalDateTime.now());
        return updateById(product);
    }
}
