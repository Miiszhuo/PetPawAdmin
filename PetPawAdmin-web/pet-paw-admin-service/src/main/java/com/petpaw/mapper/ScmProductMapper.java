package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.ScmProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * SCM商品Mapper接口
 */
@Mapper
public interface ScmProductMapper extends BaseMapper<ScmProduct> {
}
