package com.petpaw.common.model;

import lombok.Data;

/**
 * 分页请求基础类
 */
@Data
public class PageRequest {

    private Long current = 1L;
    private Long size = 10L;
    private String sortField;
    private String sortOrder = "asc";

    public Long getOffset() {
        return (current - 1) * size;
    }
}
