package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.CrmCustomer;
import org.apache.ibatis.annotations.Mapper;

/**
 * CRM客户Mapper接口
 */
@Mapper
public interface CrmCustomerMapper extends BaseMapper<CrmCustomer> {
}
