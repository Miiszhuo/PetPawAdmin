package com.petpaw.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petpaw.common.result.Result;
import com.petpaw.entity.HrEmployee;
import com.petpaw.service.HrEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理控制器
 */
@RestController
@RequestMapping("/api/hr/employees")
@RequiredArgsConstructor
public class HrEmployeeController {

    private final HrEmployeeService employeeService;

    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       HrEmployee query) {
        Page<HrEmployee> page = new Page<>(current, size);
        return Result.success(employeeService.pageWithPosition(page, query));
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(employeeService.getById(id));
    }

    @PostMapping
    public Result save(@RequestBody HrEmployee employee) {
        employeeService.save(employee);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody HrEmployee employee) {
        employee.setId(id);
        employeeService.updateById(employee);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        employeeService.removeById(id);
        return Result.success();
    }
    
    @GetMapping("/technicians")
    public Result getTechnicians() {
        return Result.success(employeeService.getActiveTechnicians());
    }
}
