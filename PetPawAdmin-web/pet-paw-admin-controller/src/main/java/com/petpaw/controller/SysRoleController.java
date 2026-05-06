package com.petpaw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.SysRole;
import com.petpaw.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping
    public Result listRoles(PageRequest pageRequest,
                                                 @RequestParam(required = false) String roleName,
                                                 @RequestParam(required = false) String roleCode,
                                                 @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(roleName != null, SysRole::getRoleName, roleName)
               .like(roleCode != null, SysRole::getRoleCode, roleCode)
               .eq(status != null, SysRole::getStatus, status)
               .eq(SysRole::getDeleted, 0)
               .orderByDesc(SysRole::getCreateTime);

        Page<SysRole> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        IPage<SysRole> result = roleService.page(page, wrapper);

        PageResult<SysRole> pageResult = new PageResult<>(result.getRecords(), result.getTotal(),
                result.getSize(), result.getCurrent());

        return Result.success(pageResult);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public Result getRole(@PathVariable Long id) {
        SysRole role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result createRole(@RequestBody SysRole role) {
        boolean success = roleService.createRole(role);
        return success ? Result.success("创建角色成功") : Result.error("创建角色失败");
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result updateRole(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        boolean success = roleService.updateRole(role);
        return success ? Result.success("更新角色成功") : Result.error("更新角色失败");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result deleteRole(@PathVariable Long id) {
        boolean success = roleService.removeById(id);
        return success ? Result.success("删除角色成功") : Result.error("删除角色失败");
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assign/{userId}")
    public Result assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        boolean success = roleService.assignRolesToUser(userId, roleIds);
        return success ? Result.success("分配角色成功") : Result.error("分配角色失败");
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/user/{userId}")
    public Result getUserRoles(@PathVariable Long userId) {
        List<SysRole> roles = roleService.findRolesByUserId(userId);
        return Result.success(roles);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/permission/{roleId}")
    public Result assignPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        boolean success = roleService.assignPermissionsToRole(roleId, permissionIds);
        return success ? Result.success("分配权限成功") : Result.error("分配权限失败");
    }
}
