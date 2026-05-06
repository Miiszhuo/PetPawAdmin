package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.petpaw.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * 系统角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status = 1;

    /**
     * 排序
     */
    private Integer sort = 0;

    /**
     * 权限ID列表（用于关联查询）
     */
    @TableField(exist = false)
    private List<Long> permissionIds;
}
