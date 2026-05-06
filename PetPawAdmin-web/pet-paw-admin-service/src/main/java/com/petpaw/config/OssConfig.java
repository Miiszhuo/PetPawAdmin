package com.petpaw.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class OssConfig {

    @Value("${aliyun.oss.endpoint:oss-cn-hangzhou.aliyuncs.com}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id:}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret:}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name:}")
    private String bucketName;

    @Value("${aliyun.oss.url-prefix:}")
    private String urlPrefix;

    @Bean
    public OSS ossClient() {
        // 如果配置为空，返回 null 或者抛出异常，这里为了防止启动报错，可以判断一下
        if (accessKeyId == null || accessKeyId.isEmpty()) {
            return null;
        }
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
