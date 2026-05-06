package com.petpaw.controller;

import com.petpaw.common.result.Result;
import com.petpaw.common.vo.DashboardDataVO;
import com.petpaw.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/data")
    public Result<DashboardDataVO> getDashboardData() {
        return Result.success(dashboardService.getDashboardData());
    }
}
