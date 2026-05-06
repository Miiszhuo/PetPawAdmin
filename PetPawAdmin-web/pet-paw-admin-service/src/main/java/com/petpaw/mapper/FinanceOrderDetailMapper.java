package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.FinanceOrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 财务订单明细Mapper接口
 */
@Mapper
public interface FinanceOrderDetailMapper extends BaseMapper<FinanceOrderDetail> {
}
