package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * CRM客户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer")
public class CrmCustomer extends BaseEntity {

    /**
     * 图片链接 (非数据库字段)
     */
    @TableField(exist = false)
    private String imageUrl;

    /**
     * 客户姓名
     */
    @NotBlank(message = "客户姓名不能为空")
    @Size(max = 50, message = "客户姓名不能超过50个字符")
    private String customerName;

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
     * 生日
     */
    private LocalDate birthday;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 客户类型：普通、高消费、VIP等
     */
    private String customerType;

    /**
     * 累计消费金额
     */
    private BigDecimal totalConsumption;

    /**
     * 最后到店时间
     */
    private LocalDate lastVisitTime;

    /**
     * 客户来源
     */
    @Size(max = 50, message = "客户来源不能超过50个字符")
    private String source;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
    /**
     * 客户状态：0-正常，1-停用
     */
    private Integer status;
}
