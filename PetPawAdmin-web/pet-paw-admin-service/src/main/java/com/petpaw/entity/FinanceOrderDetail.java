package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 财务订单明细实体
 */
@Data
@TableName("finance_order_detail")
public class FinanceOrderDetail {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 明细类型：商品、服务
     */
    @NotBlank(message = "明细类型不能为空")
    @Size(max = 20, message = "明细类型不能超过20个字符")
    private String itemType;

    /**
     * 商品/服务ID
     */
    @NotNull(message = "商品/服务ID不能为空")
    private Long itemId;

    /**
     * 商品/服务名称
     */
    @NotBlank(message = "商品/服务名称不能为空")
    @Size(max = 100, message = "商品/服务名称不能超过100个字符")
    private String itemName;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    /**
     * 总金额
     */
    @NotNull(message = "总金额不能为空")
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

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
