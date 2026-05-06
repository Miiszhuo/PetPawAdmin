package com.petpaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 智慧宠物后台管理系统启动类
 */
@SpringBootApplication
@EnableTransactionManagement
public class PetPawAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetPawAdminApplication.class, args);
    }
}
