package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.entity.HrEmployee;

import java.util.List;

/**
 * 员工服务接口
 */
public interface HrEmployeeService extends IService<HrEmployee> {
    
    /**
     * 分页查询员工（包含职位名称）
     */
    IPage<HrEmployee> pageWithPosition(Page<HrEmployee> page, HrEmployee query);

    /**
     * 获取所有在职技师/美容师
     */
    List<HrEmployee> getActiveTechnicians();
}
