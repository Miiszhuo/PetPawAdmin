package com.petpaw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.entity.SysUser;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户
     */
    com.baomidou.mybatisplus.core.metadata.IPage<SysUser> getUserPage(com.petpaw.common.model.PageRequest pageRequest, String username, String realName, Integer status);

    /**
     * 根据用户名查询用户
     */
    SysUser findByUsername(String username);

    /**
     * 创建用户
     */
    boolean createUser(SysUser user);

    /**
     * 更新用户信息
     */
    boolean updateUser(SysUser user);

    /**
     * 重置密码
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 启用/禁用用户
     */
    boolean changeUserStatus(Long userId, Integer status);
}
