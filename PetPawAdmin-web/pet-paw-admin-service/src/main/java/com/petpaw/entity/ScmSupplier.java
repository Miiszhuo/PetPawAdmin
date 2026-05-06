package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petpaw.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * SCM供应商实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm_supplier")
public class ScmSupplier extends BaseEntity {

    /**
     * 供应商名称
     */
    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 100, message = "供应商名称不能超过100个字符")
    private String supplierName;

    /**
     * 编码
     */
    @Size(max = 50, message = "编码不能超过50个字符")
    private String coding;

    /**
     * 联系人
     */
    @Size(max = 50, message = "联系人不能超过50个字符")
    private String contactPerson;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    /**
     * 地址
     */
    @Size(max = 200, message = "地址不能超过200个字符")
    private String address;

    /**
     * 银行账号
     */
    @Size(max = 50, message = "银行账号不能超过50个字符")
    private String bankAccount;

    /**
     * 开户银行
     */
    @Size(max = 100, message = "开户银行不能超过100个字符")
    private String bankName;

    /**
     * 税号
     */
    @Size(max = 50, message = "税号不能超过50个字符")
    private String taxNumber;

    /**
     * 供应商等级：A/B/C
     */
    @Size(max = 10, message = "供应商等级不能超过10个字符")
    private String level;

    /**
     * 合作状态：0-终止，1-合作中
     */
    private Integer cooperationStatus;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
