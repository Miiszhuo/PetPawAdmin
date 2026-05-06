package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.FinanceOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 财务订单Mapper接口
 */
@Mapper
public interface FinanceOrderMapper extends BaseMapper<FinanceOrder> {
}
