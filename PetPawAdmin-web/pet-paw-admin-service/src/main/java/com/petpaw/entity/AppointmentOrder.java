package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 预约订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("appointment_order")
public class AppointmentOrder extends BaseEntity {

    /**
     * 订单编号
     */
    @Size(max = 50, message = "订单编号不能超过50个字符")
    private String orderNumber;

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 客户姓名
     */
    @Size(max = 100, message = "客户姓名不能超过100个字符")
    private String customerName;

    /**
     * 宠物ID
     */
    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    /**
     * 服务项目ID
     */
    @NotNull(message = "服务项目ID不能为空")
    private Long serviceItemId;

    /**
     * 服务人员ID
     */
    @NotNull(message = "服务人员ID不能为空")
    private Long staffId;

    /**
     * 预约日期
     */
    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;

    /**
     * 预约时间
     */
    @NotNull(message = "预约时间不能为空")
    private LocalTime appointmentTime;

    /**
     * 预约时长(分钟)
     */
    @NotNull(message = "预约时长不能为空")
    private Integer duration;

    /**
     * 状态：待确认、已预约、已签到、服务中、待结算、已完成、已取消
     */
    @Size(max = 20, message = "状态不能超过20个字符")
    private String status;

    /**
     * 实际开始时间
     */
    private java.time.LocalDateTime actualStartTime;

    /**
     * 实际结束时间
     */
    private java.time.LocalDateTime actualEndTime;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 总金额
     */
    @NotNull(message = "总金额不能为空")
    private BigDecimal totalAmount;

    /**
     * 支付状态：未支付、部分支付、已支付
     */
    @Size(max = 20, message = "支付状态不能超过20个字符")
    private String paymentStatus;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

    /**
     * 宠物姓名
     */
    @Size(max = 100, message = "宠物姓名不能超过100个字符")
    private String petName;

    /**
     * 服务项目名称
     */
    @Size(max = 100, message = "服务项目名称不能超过100个字符")
    private String serviceName;

    /**
     * 服务类型/分类
     */
    @Size(max = 50, message = "服务类型不能超过50个字符")
    private String serviceType;

    /**
     * 服务人员姓名
     */
    @Size(max = 100, message = "服务人员姓名不能超过100个字符")
    private String staffName;
}
