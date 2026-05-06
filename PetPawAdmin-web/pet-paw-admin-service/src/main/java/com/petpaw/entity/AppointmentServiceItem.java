package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.petpaw.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 预约服务项目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("appointment_service_item")
public class AppointmentServiceItem extends BaseEntity {

    /**
     * 图片链接 (非数据库字段)
     */
    @TableField(exist = false)
    private String imageUrl;

    /**
     * 服务名称
     */
    @NotBlank(message = "服务名称不能为空")
    @Size(max = 100, message = "服务名称不能超过100个字符")
    private String serviceName;

    /**
     * 服务编码
     */
    @Size(max = 50, message = "服务编码不能超过50个字符")
    private String serviceCode;

    /**
     * 服务分类
     */
    @Size(max = 50, message = "服务分类不能超过50个字符")
    private String category;

    /**
     * 服务描述
     */
    @Size(max = 500, message = "服务描述不能超过500个字符")
    private String description;

    /**
     * 标准时长(分钟)
     */
    @NotNull(message = "标准时长不能为空")
    private Integer standardDuration;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    /**
     * 所需技能等级：1-初级，2-中级，3-高级
     */
    private Integer skillLevelRequired;

    /**
     * 所需技能
     */
    @Size(max = 500, message = "所需技能不能超过500个字符")
    private String requiredSkills;

    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
