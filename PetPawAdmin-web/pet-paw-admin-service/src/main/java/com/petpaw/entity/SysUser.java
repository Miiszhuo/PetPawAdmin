package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.petpaw.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 图片链接 (非数据库字段)
     */
    @TableField(exist = false)
    private String imageUrl;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status = 1;

    /**
     * 最后登录时间
     */
    private String lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 角色ID列表（用于关联查询）
     */
    @TableField(exist = false)
    private java.util.List<Long> roleIds;
}
