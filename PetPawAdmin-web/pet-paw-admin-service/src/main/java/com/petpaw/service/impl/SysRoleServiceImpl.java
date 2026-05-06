package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.exception.BusinessException;
import com.petpaw.entity.SysRole;
import com.petpaw.entity.SysRolePermission;
import com.petpaw.entity.SysUserRole;
import com.petpaw.mapper.SysRoleMapper;
import com.petpaw.mapper.SysRolePermissionMapper;
import com.petpaw.mapper.SysUserRoleMapper;
import com.petpaw.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public List<SysRole> findRolesByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(wrapper);

        if (userRoles.isEmpty()) {
            return List.of();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        return this.listByIds(roleIds);
    }

    @Override
    @Transactional
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        // 先删除用户的所有角色
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        // 再添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole userRole = new SysUserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());
            // 逐个插入
            int count = 0;
            for (SysUserRole userRole : userRoles) {
                count += userRoleMapper.insert(userRole);
            }
            return count > 0;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean createRole(SysRole role) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode())
               .eq(SysRole::getDeleted, 0);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        return this.save(role);
    }

    @Override
    @Transactional
    public boolean updateRole(SysRole role) {
        // 检查角色编码是否已被其他角色使用
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode())
               .eq(SysRole::getDeleted, 0)
               .ne(SysRole::getId, role.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        return this.updateById(role);
    }

    @Override
    @Transactional
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 先删除角色的所有权限
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);

        // 再添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> {
                        SysRolePermission rolePermission = new SysRolePermission();
                        rolePermission.setRoleId(roleId);
                        rolePermission.setPermissionId(permissionId);
                        return rolePermission;
                    })
                    .collect(Collectors.toList());
            // 逐个插入
            int count = 0;
            for (SysRolePermission rolePermission : rolePermissions) {
                count += rolePermissionMapper.insert(rolePermission);
            }
            return count > 0;
        }

        return true;
    }
}
