package com.petpaw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.SysPermission;
import com.petpaw.mapper.SysPermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionMapper permissionMapper;

    /**
     * 分页查询权限列表
     */
    @GetMapping
    public Result listPermissions(PageRequest pageRequest,
                                                            @RequestParam(required = false) String permissionName,
                                                            @RequestParam(required = false) Integer permissionType,
                                                            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(permissionName != null, SysPermission::getPermissionName, permissionName)
               .eq(permissionType != null, SysPermission::getPermissionType, permissionType)
               .eq(status != null, SysPermission::getStatus, status)
               .eq(SysPermission::getDeleted, 0)
               .orderByAsc(SysPermission::getSort)
               .orderByDesc(SysPermission::getCreateTime);

        Page<SysPermission> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        IPage<SysPermission> result = permissionMapper.selectPage(page, wrapper);

        PageResult<SysPermission> pageResult = new PageResult<>(result.getRecords(), result.getTotal(),
                result.getSize(), result.getCurrent());

        return Result.success(pageResult);
    }

    /**
     * 获取权限详情
     */
    @GetMapping("/{id}")
    public Result getPermission(@PathVariable Long id) {
        SysPermission permission = permissionMapper.selectById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    /**
     * 创建权限
     */
    @PostMapping
    public Result createPermission(@RequestBody SysPermission permission) {
        int count = permissionMapper.insert(permission);
        return count > 0 ? Result.success("创建权限成功") : Result.error("创建权限失败");
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    public Result updatePermission(@PathVariable Long id, @RequestBody SysPermission permission) {
        permission.setId(id);
        int count = permissionMapper.updateById(permission);
        return count > 0 ? Result.success("更新权限成功") : Result.error("更新权限失败");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result deletePermission(@PathVariable Long id) {
        int count = permissionMapper.deleteById(id);
        return count > 0 ? Result.success("删除权限成功") : Result.error("删除权限失败");
    }

    /**
     * 获取权限树形结构
     */
    @GetMapping("/tree")
    public Result getPermissionTree() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getDeleted, 0)
               .orderByAsc(SysPermission::getSort);
        List<SysPermission> permissions = permissionMapper.selectList(wrapper);
        return Result.success(permissions);
    }
}
