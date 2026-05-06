package com.petpaw.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petpaw.common.result.Result;
import com.petpaw.entity.HrPosition;
import com.petpaw.service.HrPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 职位管理控制器
 */
@RestController
@RequestMapping("/api/hr/positions")
@RequiredArgsConstructor
public class HrPositionController {

    private final HrPositionService positionService;

    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       HrPosition query) {
        Page<HrPosition> page = new Page<>(current, size);
        QueryWrapper<HrPosition> wrapper = new QueryWrapper<>();
        if (query.getPositionName() != null && !query.getPositionName().isEmpty()) {
            wrapper.like("position_name", query.getPositionName());
        }
        return Result.success(positionService.page(page, wrapper));
    }
    
    @GetMapping("/list")
    public Result list() {
        return Result.success(positionService.list(new QueryWrapper<HrPosition>().eq("status", 1)));
    }

    @PostMapping
    public Result save(@RequestBody HrPosition position) {
        positionService.save(position);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody HrPosition position) {
        position.setId(id);
        positionService.updateById(position);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        positionService.removeById(id);
        return Result.success();
    }
}
