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
 * SCM商品实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_product")
public class ScmProduct extends BaseEntity {

    private String imageUrl;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称不能超过100个字符")
    private String productName;

    /**
     * 商品编码
     */
    @Size(max = 50, message = "商品编码不能超过50个字符")
    private String productCode;

    /**
     * 分类
     */
    @Size(max = 50, message = "分类不能超过50个字符")
    private String category;

    /**
     * 品牌
     */
    @Size(max = 50, message = "品牌不能超过50个字符")
    private String brand;

    /**
     * 规格
     */
    @Size(max = 100, message = "规格不能超过100个字符")
    private String specification;

    /**
     * 单位
     */
    @Size(max = 10, message = "单位不能超过10个字符")
    private String unit;

    /**
     * 采购价
     */
    private BigDecimal purchasePrice;

    /**
     * 销售价
     */
    @NotNull(message = "销售价不能为空")
    private BigDecimal salePrice;

    /**
     * 库存数量
     */
    private Integer stockQuantity;

    /**
     * 最低库存预警
     */
    private Integer minStockQuantity;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
