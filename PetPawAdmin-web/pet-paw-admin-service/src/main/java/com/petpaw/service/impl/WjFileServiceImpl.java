package com.petpaw.service.impl;

import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.exception.BusinessException;
import com.petpaw.config.OssConfig;
import com.petpaw.entity.WjFile;
import com.petpaw.mapper.WjFileMapper;
import com.petpaw.service.WjFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class WjFileServiceImpl extends ServiceImpl<WjFileMapper, WjFile> implements WjFileService {

    @Autowired(required = false)
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    @Override
    public String uploadFile(MultipartFile file, Long businessId, String businessType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String fileUrl;
        
        if (ossClient != null) {
            try {
                // 1. 生成文件名
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".") 
                        ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                        : ".jpg";
                String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
                
                // 按日期归档
                String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                String objectName = datePath + "/" + fileName;

                // 2. 上传到 OSS
                InputStream inputStream = file.getInputStream();
                ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream);

                // 3. 拼接访问 URL
                fileUrl = ossConfig.getUrlPrefix() + objectName;
            } catch (IOException e) {
                log.error("文件上传失败", e);
                throw new BusinessException("文件上传失败: " + e.getMessage());
            }
        } else {
            // 模拟上传 (当没有配置OSS时)
            log.warn("OSS未配置，使用模拟上传");
            String fileName = UUID.randomUUID().toString();
            fileUrl = "https://image-tww.oss-cn-hangzhou.aliyuncs.com/" + fileName + ".jpg";
            // 实际场景中，这里应该保存到本地或者提示错误
        }

        // 4. 保存记录到数据库
        // 如果 businessId 存在，则保存关联关系；如果不存在（如新增时），则只保存记录，后续通过 update 更新
        WjFile wjFile = new WjFile();
        wjFile.setBusinessId(businessId);
        wjFile.setBusinessType(businessType);
        wjFile.setFilePath(fileUrl);
        this.save(wjFile);

        return fileUrl;
    }

    @Override
    public List<WjFile> getFiles(Long businessId, String businessType) {
        return this.list(new LambdaQueryWrapper<WjFile>()
                .eq(WjFile::getBusinessId, businessId)
                .eq(WjFile::getBusinessType, businessType));
    }

    @Override
    public void deleteFiles(Long businessId, String businessType) {
        this.remove(new LambdaQueryWrapper<WjFile>()
                .eq(WjFile::getBusinessId, businessId)
                .eq(WjFile::getBusinessType, businessType));
    }
    
    @Override
    public void bindFile(String filePath, Long businessId, String businessType) {
        this.update(new LambdaUpdateWrapper<WjFile>()
                .eq(WjFile::getFilePath, filePath)
                .set(WjFile::getBusinessId, businessId)
                .set(WjFile::getBusinessType, businessType));
    }
}
