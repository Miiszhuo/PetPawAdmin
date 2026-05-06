package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.entity.HrEmployee;
import com.petpaw.entity.HrPosition;
import com.petpaw.mapper.HrEmployeeMapper;
import com.petpaw.service.HrEmployeeService;
import com.petpaw.service.HrPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工服务实现类
 */
@Service
@RequiredArgsConstructor
public class HrEmployeeServiceImpl extends ServiceImpl<HrEmployeeMapper, HrEmployee> implements HrEmployeeService {

    private final HrPositionService positionService;

    @Override
    public IPage<HrEmployee> pageWithPosition(Page<HrEmployee> page, HrEmployee query) {
        QueryWrapper<HrEmployee> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(query.getRealName())) {
            wrapper.like("e.real_name", query.getRealName());
        }
        if (StringUtils.hasText(query.getEmployeeNo())) {
            wrapper.like("e.employee_no", query.getEmployeeNo());
        }
        if (query.getStatus() != null) {
            wrapper.eq("e.status", query.getStatus());
        }
        if (query.getPositionId() != null) {
            wrapper.eq("e.position_id", query.getPositionId());
        }
        wrapper.orderByDesc("e.create_time");
        return baseMapper.selectPageWithPosition(page, wrapper);
    }

    @Override
    public List<HrEmployee> getActiveTechnicians() {
        // 1. 查找技术类职位 (美容师、助理美容师、兽医)
        List<Long> techPositionIds = positionService.list(new QueryWrapper<HrPosition>()
                .in("position_name", "美容师", "助理美容师", "兽医")
                .eq("status", 1))
                .stream()
                .map(HrPosition::getId)
                .collect(Collectors.toList());

        if (techPositionIds.isEmpty()) {
            return List.of();
        }

        // 2. 查找这些职位的在职员工
        return list(new QueryWrapper<HrEmployee>()
                .in("position_id", techPositionIds)
                .eq("status", 1)); // 1: 在职
    }
}
