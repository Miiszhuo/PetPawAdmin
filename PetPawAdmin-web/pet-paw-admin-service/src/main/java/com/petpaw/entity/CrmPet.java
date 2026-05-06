package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petpaw.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * CRM宠物档案实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_pet")
public class CrmPet extends BaseEntity {

    /**
     * 所属客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 宠物姓名
     */
    @NotBlank(message = "宠物姓名不能为空")
    @Size(max = 50, message = "宠物姓名不能超过50个字符")
    private String petName;

    /**
     * 宠物类型：狗、猫等
     */
    @Size(max = 20, message = "宠物类型不能超过20个字符")
    private String petType;

    /**
     * 品种
     */
    @Size(max = 50, message = "品种不能超过50个字符")
    private String breed;

    /**
     * 性别：0-未知，1-雄，2-雌
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

    /**
     * 毛色
     */
    @Size(max = 20, message = "毛色不能超过20个字符")
    private String color;

    /**
     * 绝育：0-未绝育，1-已绝育
     */
    private Integer sterilization;

    /**
     * 血型
     */
    @Size(max = 10, message = "血型不能超过10个字符")
    private String bloodType;

    /**
     * 过敏源
     */
    @Size(max = 200, message = "过敏源不能超过200个字符")
    private String allergySource;

    /**
     * 芯片号
     */
    @Size(max = 50, message = "芯片号不能超过50个字符")
    private String chipNumber;

    /**
     * 健康状况
     */
    @Size(max = 200, message = "健康状况不能超过200个字符")
    private String healthStatus;

    /**
     * 状态：active-正常, in_treatment-就诊中, deceased-已离世
     */
    @Size(max = 20, message = "状态不能超过20个字符")
    private String status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
