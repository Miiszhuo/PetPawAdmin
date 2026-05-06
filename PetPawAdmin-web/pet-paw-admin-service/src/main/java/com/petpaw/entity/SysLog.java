package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_log")
public class SysLog extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型：LOGIN-登录, LOGOUT-登出, CREATE-创建, UPDATE-更新, DELETE-删除, QUERY-查询
     */
    private String operationType;

    /**
     * 操作模块：USER-用户管理, ROLE-角色管理, CUSTOMER-客户管理, PET-宠物管理, PRODUCT-商品管理等
     */
    private String operationModule;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 操作结果：0-失败，1-成功
     */
    private Integer result = 1;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 地理位置
     */
    private String location;
}
