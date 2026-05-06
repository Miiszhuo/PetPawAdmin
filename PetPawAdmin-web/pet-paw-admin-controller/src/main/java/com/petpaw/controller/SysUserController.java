package com.petpaw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.SysUser;
import com.petpaw.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Result listUsers(PageRequest pageRequest,
                                                 @RequestParam(required = false) String username,
                                                 @RequestParam(required = false) String realName,
                                                 @RequestParam(required = false) Integer status) {
        IPage<SysUser> result = userService.getUserPage(pageRequest, username, realName, status);

        PageResult<SysUser> pageResult = new PageResult<>(result.getRecords(), result.getTotal(),
                result.getSize(), result.getCurrent());

        return Result.<PageResult<SysUser>>success(pageResult);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result getUser(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result createUser(@RequestBody SysUser user) {
        boolean success = userService.createUser(user);
        return success ? Result.success("创建用户成功") : Result.error("创建用户失败");
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        boolean success = userService.updateUser(user);
        return success ? Result.success("更新用户成功") : Result.error("更新用户失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? Result.success("删除用户成功") : Result.error("删除用户失败");
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/password/reset")
    public Result resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        boolean success = userService.resetPassword(id, newPassword);
        return success ? Result.success("重置密码成功") : Result.error("重置密码失败");
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    public Result changeUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = userService.changeUserStatus(id, status);
        return success ? Result.success("修改用户状态成功") : Result.error("修改用户状态失败");
    }
}
