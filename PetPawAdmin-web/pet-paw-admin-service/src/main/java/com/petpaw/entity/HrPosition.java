package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 职位实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_position")
public class HrPosition extends BaseEntity {

    /**
     * 职位名称
     */
    @NotBlank(message = "职位名称不能为空")
    @Size(max = 50, message = "职位名称不能超过50个字符")
    private String positionName;

    /**
     * 职位编码
     */
    @NotBlank(message = "职位编码不能为空")
    @Size(max = 50, message = "职位编码不能超过50个字符")
    private String positionCode;

    /**
     * 描述
     */
    @Size(max = 200, message = "描述不能超过200个字符")
    private String description;

    /**
     * 状态 1:启用 0:禁用
     */
    private Integer status;
}
