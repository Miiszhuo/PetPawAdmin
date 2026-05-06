package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 财务充值记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance_recharge_record")
public class FinanceRechargeRecord extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员卡ID
     */
    @NotNull(message = "会员卡ID不能为空")
    private Long memberCardId;

    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空")
    private BigDecimal amount;

    /**
     * 赠送金额
     */
    private BigDecimal giftAmount;

    /**
     * 支付方式
     */
    @Size(max = 50, message = "支付方式不能超过50个字符")
    private String paymentMethod;

    /**
     * 操作员ID（收银员ID）
     */
    private Long cashierId;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;
}
