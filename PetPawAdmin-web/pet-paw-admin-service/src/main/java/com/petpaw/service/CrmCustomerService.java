package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.CrmCustomer;

/**
 * CRM客户服务接口
 */
public interface CrmCustomerService extends IService<CrmCustomer> {

    /**
     * 分页查询客户
     */
    IPage<CrmCustomer> listCustomers(PageRequest pageRequest, String customerName, String phone, Integer status, String customerType, String startDate, String endDate);

    /**
     * 创建客户
     */
    boolean saveCustomer(CrmCustomer customer);

    /**
     * 更新客户
     */
    boolean updateCustomer(CrmCustomer customer);

    /**
     * 删除客户
     */
    boolean deleteCustomer(Long id);
}
