package com.petpaw.common.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * 注意：需要在service模块的实体类中继承此类并添加MyBatis Plus注解
 */
@Data
public class BaseEntity {

    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
    private Integer deleted = 0;
}
