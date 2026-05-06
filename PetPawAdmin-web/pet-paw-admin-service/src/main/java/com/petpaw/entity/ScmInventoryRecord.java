package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * SCM库存记录实体
 */
@Data
@TableName("scm_inventory_record")
public class ScmInventoryRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 记录类型：入库、出库、盘点、调拨
     */
    @NotBlank(message = "记录类型不能为空")
    @Size(max = 20, message = "记录类型不能超过20个字符")
    private String recordType;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    /**
     * 操作前数量
     */
    @NotNull(message = "操作前数量不能为空")
    private Integer beforeQuantity;

    /**
     * 操作后数量
     */
    @NotNull(message = "操作后数量不能为空")
    private Integer afterQuantity;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

    /**
     * 操作人
     */
    @Size(max = 50, message = "操作人不能超过50个字符")
    private String createBy;

    /**
     * 操作时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 逻辑删除标志
     */
    private Integer deleted = 0;
}
