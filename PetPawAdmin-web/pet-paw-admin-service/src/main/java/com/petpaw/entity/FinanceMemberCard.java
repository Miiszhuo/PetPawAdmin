package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petpaw.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 财务会员卡实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_member_card")
public class FinanceMemberCard extends BaseEntity {

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 卡号
     */
    @Size(max = 50, message = "卡号不能超过50个字符")
    private String cardNumber;

    /**
     * 卡类型：储值卡、折扣卡、积分卡
     */
    @Size(max = 20, message = "卡类型不能超过20个字符")
    private String cardType;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 累计充值
     */
    private BigDecimal totalRecharge;

    /**
     * 累计消费
     */
    private BigDecimal totalConsumption;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;

    /**
     * 到期时间
     */
    private LocalDate expireTime;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

    /**
     * 客户姓名
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String customerName;

    /**
     * 客户电话
     */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String phone;
}
