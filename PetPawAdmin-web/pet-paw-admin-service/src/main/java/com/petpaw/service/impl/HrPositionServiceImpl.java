package com.petpaw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.entity.HrPosition;
import com.petpaw.mapper.HrPositionMapper;
import com.petpaw.service.HrPositionService;
import org.springframework.stereotype.Service;

/**
 * 职位服务实现类
 */
@Service
public class HrPositionServiceImpl extends ServiceImpl<HrPositionMapper, HrPosition> implements HrPositionService {
}
