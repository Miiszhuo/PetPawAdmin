package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.ScmSupplier;

/**
 * SCM供应商服务接口
 */
public interface ScmSupplierService extends IService<ScmSupplier> {

    /**
     * 分页查询供应商
     */
    IPage<ScmSupplier> listSuppliers(PageRequest pageRequest, String supplierName, Integer cooperationStatus, String contact, String level);

    /**
     * 创建供应商
     */
    boolean saveSupplier(ScmSupplier supplier);

    /**
     * 更新供应商
     */
    boolean updateSupplier(ScmSupplier supplier);

    /**
     * 删除供应商
     */
    boolean deleteSupplier(Long id);
}
