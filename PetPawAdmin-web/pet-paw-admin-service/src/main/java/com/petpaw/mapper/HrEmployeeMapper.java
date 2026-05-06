package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petpaw.entity.HrEmployee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 员工Mapper接口
 */
@Mapper
public interface HrEmployeeMapper extends BaseMapper<HrEmployee> {

    @Select("SELECT e.*, p.position_name FROM hr_employee e LEFT JOIN hr_position p ON e.position_id = p.id ${ew.customSqlSegment}")
    IPage<HrEmployee> selectPageWithPosition(Page<HrEmployee> page, @Param("ew") com.baomidou.mybatisplus.core.conditions.Wrapper<HrEmployee> wrapper);
}
