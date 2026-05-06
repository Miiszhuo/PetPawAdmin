package com.petpaw.controller;

import com.petpaw.common.result.Result;
import com.petpaw.entity.AppointmentStaffSchedule;
import com.petpaw.service.AppointmentStaffScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预约员工排班管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/appointment/schedules")
@RequiredArgsConstructor
public class AppointmentStaffScheduleController {

    private final AppointmentStaffScheduleService scheduleService;

    /**
     * 获取排班看板数据
     */
    @GetMapping("/board")
    public Result getScheduleBoard(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Map<String, Object>> boardData = scheduleService.getScheduleBoard(date);
            return Result.success(boardData);
        } catch (Exception e) {
            log.error("获取排班看板失败", e);
            return Result.error("获取排班看板失败: " + e.getMessage());
        }
    }

    /**
     * 保存排班
     */
    @PostMapping
    public Result saveSchedule(@RequestBody AppointmentStaffSchedule schedule) {
        boolean success = scheduleService.saveSchedule(schedule);
        return success ? Result.success("排班保存成功") : Result.error("排班保存失败");
    }

    /**
     * 删除排班
     */
    @DeleteMapping("/{id}")
    public Result deleteSchedule(@PathVariable Long id) {
        boolean success = scheduleService.removeById(id);
        return success ? Result.success("排班删除成功") : Result.error("排班删除失败");
    }

    /**
     * 获取指定日期的排班列表
     */
    @GetMapping
    public Result listSchedules(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentStaffSchedule> list = scheduleService.lambdaQuery()
                .eq(AppointmentStaffSchedule::getWorkDate, date)
                .list();
        return Result.success(list);
    }
}
