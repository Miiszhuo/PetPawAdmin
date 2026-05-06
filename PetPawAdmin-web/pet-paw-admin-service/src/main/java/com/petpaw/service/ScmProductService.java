package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.ScmProduct;

/**
 * SCM商品服务接口
 */
public interface ScmProductService extends IService<ScmProduct> {

    /**
     * 分页查询商品
     */
    IPage<ScmProduct> listProducts(PageRequest pageRequest, String productName, String category, Integer status, Long supplierId, String stockStatus, String brand);

    /**
     * 创建商品
     */
    boolean saveProduct(ScmProduct product);

    /**
     * 更新商品
     */
    boolean updateProduct(ScmProduct product);

    /**
     * 删除商品
     */
    boolean deleteProduct(Long id);

    /**
     * 更新库存
     */
    boolean updateStock(Long id, Integer quantity);
}
