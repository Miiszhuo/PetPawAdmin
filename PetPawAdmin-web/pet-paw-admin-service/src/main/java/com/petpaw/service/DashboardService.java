package com.petpaw.service;

import com.petpaw.common.vo.DashboardDataVO;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {
    /**
     * 获取仪表盘数据
     * @return 仪表盘数据VO
     */
    DashboardDataVO getDashboardData();
}
