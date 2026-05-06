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
 * CRM宠物健康记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_pet_health_record")
public class CrmPetHealthRecord extends BaseEntity {

    /**
     * 宠物ID
     */
    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    /**
     * 记录类型：疫苗、驱虫、手术、洗护等
     */
    @NotBlank(message = "记录类型不能为空")
    @Size(max = 20, message = "记录类型不能超过20个字符")
    private String recordType;

    /**
     * 记录日期
     */
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    /**
     * 记录内容
     */
    @NotBlank(message = "记录内容不能为空")
    @Size(max = 1000, message = "记录内容不能超过1000个字符")
    private String recordContent;

    /**
     * 下次提醒日期
     */
    private LocalDate nextReminderDate;

    /**
     * 医生姓名
     */
    @Size(max = 50, message = "医生姓名不能超过50个字符")
    private String doctorName;

    /**
     * 费用
     */
    private BigDecimal cost;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
