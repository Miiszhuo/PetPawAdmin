package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petpaw.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 财务订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_order")
public class FinanceOrder extends BaseEntity {

    /**
     * 订单编号
     */
    @Size(max = 50, message = "订单编号不能超过50个字符")
    private String orderNumber;

    /**
     * 订单类型：商品、服务、混合
     */
    @NotBlank(message = "订单类型不能为空")
    @Size(max = 20, message = "订单类型不能超过20个字符")
    private String orderType;

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 订单总金额
     */
    @NotNull(message = "订单总金额不能为空")
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    @NotNull(message = "实付金额不能为空")
    private BigDecimal paymentAmount;

    /**
     * 支付方式
     */
    @Size(max = 50, message = "支付方式不能超过50个字符")
    private String paymentMethod;

    /**
     * 支付状态：未支付、部分支付、已支付
     */
    @Size(max = 20, message = "支付状态不能超过20个字符")
    private String paymentStatus;

    /**
     * 订单状态：待处理、处理中、已完成、已取消
     */
    @Size(max = 20, message = "订单状态不能超过20个字符")
    private String orderStatus;

    /**
     * 收银员ID
     */
    private Long cashierId;

    /**
     * 支付时间
     */
    private java.time.LocalDateTime paymentTime;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
