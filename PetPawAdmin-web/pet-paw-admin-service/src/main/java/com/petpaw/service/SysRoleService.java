package com.petpaw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> findRolesByUserId(Long userId);

    /**
     * 为用户分配角色
     */
    boolean assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 创建角色
     */
    boolean createRole(SysRole role);

    /**
     * 更新角色
     */
    boolean updateRole(SysRole role);

    /**
     * 为角色分配权限
     */
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}
