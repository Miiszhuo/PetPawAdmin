package com.petpaw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petpaw.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper接口
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

}
