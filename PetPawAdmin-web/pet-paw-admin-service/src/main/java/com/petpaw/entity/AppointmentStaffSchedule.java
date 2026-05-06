package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petpaw.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 预约员工排班实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("appointment_staff_schedule")
public class AppointmentStaffSchedule extends BaseEntity {

    /**
     * 员工ID
     */
    @NotNull(message = "员工ID不能为空")
    private Long staffId;

    /**
     * 工作日期
     */
    @NotNull(message = "工作日期不能为空")
    private LocalDate workDate;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    /**
     * 状态：正常、请假、调休
     */
    @Size(max = 20, message = "状态不能超过20个字符")
    private String status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
