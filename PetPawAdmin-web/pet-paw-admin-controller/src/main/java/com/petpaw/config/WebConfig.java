package com.petpaw.config;

import com.petpaw.controller.JwtAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web配置类
 */
@Configuration
public class WebConfig {

    /**
     * 注册JWT认证过滤器
     */
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter() {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1); // 设置执行顺序，越小越先执行
        return registrationBean;
    }
}