package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 员工实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_employee")
public class HrEmployee extends BaseEntity {

    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空")
    @Size(max = 20, message = "工号不能超过20个字符")
    private String employeeNo;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过50个字符")
    private String realName;

    /**
     * 性别 1:男 2:女 0:未知
     */
    private Integer gender;

    /**
     * 手机号
     */
    @Size(max = 20, message = "手机号不能超过20个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Size(max = 50, message = "邮箱不能超过50个字符")
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 职位ID
     */
    private Long positionId;

    /**
     * 入职日期
     */
    private LocalDate entryDate;

    /**
     * 关联系统用户ID
     */
    private Long sysUserId;

    /**
     * 状态 1:在职 2:离职 3:休假
     */
    private Integer status;

    /**
     * 职位名称 (非数据库字段)
     */
    @TableField(exist = false)
    private String positionName;
}
