package com.petpaw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文件记录实体
 */
@Data
@TableName("wj_file")
public class WjFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务ID
     */
    private Long businessId;
    
    /**
     * 业务类型 (如: PRODUCT, USER, SERVICE, CUSTOMER)
     * 用于区分不同模块的ID
     */
    private String businessType;

    /**
     * 文件路径 (OSS路径)
     */
    private String filePath;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;
}
