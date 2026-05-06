package com.petpaw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.entity.WjFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WjFileService extends IService<WjFile> {
    
    /**
     * 上传文件
     * @param file 文件
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @return 文件访问路径
     */
    String uploadFile(MultipartFile file, Long businessId, String businessType);

    /**
     * 获取文件列表
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @return 文件记录列表
     */
    List<WjFile> getFiles(Long businessId, String businessType);
    
    /**
     * 删除文件
     * @param businessId 业务ID
     * @param businessType 业务类型
     */
    void deleteFiles(Long businessId, String businessType);
    
    /**
     * 更新业务关联
     * @param filePath 文件路径
     * @param businessId 业务ID
     * @param businessType 业务类型
     */
    void bindFile(String filePath, Long businessId, String businessType);
}
