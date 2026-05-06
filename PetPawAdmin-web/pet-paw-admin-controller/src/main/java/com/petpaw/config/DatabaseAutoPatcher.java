package com.petpaw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据库自动补丁
 * 用于自动修复数据库表结构差异
 */
@Slf4j
@Component
public class DatabaseAutoPatcher implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseAutoPatcher(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        patchScmProductTable();
        patchScmSupplierTable();
        patchAppointmentServiceItemTable();
        patchAppointmentOrderTable();
        patchCrmCustomerTable();
    }

    private void patchCrmCustomerTable() {
        try {
            log.info("Checking crm_customer table structure...");
            
            // Check for 'customer_type' column
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crm_customer' AND COLUMN_NAME = 'customer_type'"
            );

            if (columns.isEmpty()) {
                log.info("Column 'customer_type' missing in 'crm_customer'. Adding it...");
                jdbcTemplate.execute("ALTER TABLE crm_customer ADD COLUMN customer_type VARCHAR(20) COMMENT '客户类型' AFTER gender");
                log.info("Successfully added 'customer_type' column to 'crm_customer'.");
            } else {
                log.info("Column 'customer_type' already exists in 'crm_customer'.");
            }
            
        } catch (Exception e) {
            log.error("Failed to patch crm_customer table: {}", e.getMessage());
        }
    }

    private void patchAppointmentOrderTable() {
        try {
            log.info("Checking appointment_order table structure...");
            
            // Check for 'customer_name' column
            if (!hasColumn("appointment_order", "customer_name")) {
                jdbcTemplate.execute("ALTER TABLE appointment_order ADD COLUMN customer_name VARCHAR(100) COMMENT '客户姓名' AFTER customer_id");
                log.info("Added 'customer_name' to 'appointment_order'.");
            }
            
            // Check for 'pet_name' column
            if (!hasColumn("appointment_order", "pet_name")) {
                jdbcTemplate.execute("ALTER TABLE appointment_order ADD COLUMN pet_name VARCHAR(100) COMMENT '宠物姓名' AFTER pet_id");
                log.info("Added 'pet_name' to 'appointment_order'.");
            }

            // Check for 'service_name' column
            if (!hasColumn("appointment_order", "service_name")) {
                jdbcTemplate.execute("ALTER TABLE appointment_order ADD COLUMN service_name VARCHAR(100) COMMENT '服务项目名称' AFTER service_item_id");
                log.info("Added 'service_name' to 'appointment_order'.");
            }

            // Check for 'service_type' column
            if (!hasColumn("appointment_order", "service_type")) {
                jdbcTemplate.execute("ALTER TABLE appointment_order ADD COLUMN service_type VARCHAR(50) COMMENT '服务类型' AFTER service_name");
                log.info("Added 'service_type' to 'appointment_order'.");
            }

            // Check for 'staff_name' column
            if (!hasColumn("appointment_order", "staff_name")) {
              jdbcTemplate.execute("ALTER TABLE appointment_order ADD COLUMN staff_name VARCHAR(100) COMMENT '服务人员姓名' AFTER staff_id");
              log.info("Added 'staff_name' to 'appointment_order'.");
            }

            // Patch crm_pet table
            log.info("Checking crm_pet table structure...");
            if (!hasColumn("crm_pet", "status")) {
                jdbcTemplate.execute("ALTER TABLE crm_pet ADD COLUMN status VARCHAR(20) DEFAULT 'active' COMMENT '状态' AFTER health_status");
                log.info("Added 'status' to 'crm_pet'.");
            }
            
        } catch (Exception e) {
            log.error("Failed to patch appointment_order table: {}", e.getMessage());
        }
    }

    private boolean hasColumn(String tableName, String columnName) {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
            tableName, columnName
        );
        return !columns.isEmpty();
    }

    private void patchAppointmentServiceItemTable() {
        try {
            log.info("Checking appointment_service_item table structure...");
            
            // Check for 'required_skills' column
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'appointment_service_item' AND COLUMN_NAME = 'required_skills'"
            );

            if (columns.isEmpty()) {
                log.info("Column 'required_skills' missing in 'appointment_service_item'. Adding it...");
                jdbcTemplate.execute("ALTER TABLE appointment_service_item ADD COLUMN required_skills VARCHAR(500) COMMENT '所需技能' AFTER skill_level_required");
                log.info("Successfully added 'required_skills' column to 'appointment_service_item'.");
            } else {
                log.info("Column 'required_skills' already exists in 'appointment_service_item'.");
            }

            // Patch standard_duration to have default value
            jdbcTemplate.execute("ALTER TABLE appointment_service_item MODIFY COLUMN standard_duration INT DEFAULT 60 COMMENT '标准时长(分钟)'");
            log.info("Updated 'standard_duration' in 'appointment_service_item' to have default value 60.");
            
        } catch (Exception e) {
            log.error("Failed to patch appointment_service_item table: {}", e.getMessage());
        }
    }

    private void patchScmSupplierTable() {
        try {
            log.info("Checking scm_supplier table structure...");
            
            // Check for 'level' column
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'scm_supplier' AND COLUMN_NAME = 'level'"
            );

            if (columns.isEmpty()) {
                log.info("Column 'level' missing in 'scm_supplier'. Adding it...");
                jdbcTemplate.execute("ALTER TABLE scm_supplier ADD COLUMN level VARCHAR(10) COMMENT '供应商等级' AFTER tax_number");
                log.info("Successfully added 'level' column to 'scm_supplier'.");
            } else {
                log.info("Column 'level' already exists in 'scm_supplier'.");
            }
            
        } catch (Exception e) {
            log.error("Failed to patch scm_supplier table: {}", e.getMessage());
        }
    }

    private void patchScmProductTable() {
        try {
            log.info("Checking scm_product table structure...");
            
            // 检查supplier_id列是否存在
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'scm_product' AND COLUMN_NAME = 'supplier_id'"
            );

            if (columns.isEmpty()) {
                log.info("Column 'supplier_id' missing in 'scm_product'. Adding it...");
                jdbcTemplate.execute("ALTER TABLE scm_product ADD COLUMN supplier_id BIGINT COMMENT '供应商ID' AFTER status");
                jdbcTemplate.execute("ALTER TABLE scm_product ADD INDEX idx_supplier_id (supplier_id)");
                log.info("Successfully added 'supplier_id' column to 'scm_product'.");
            } else {
                log.info("Column 'supplier_id' already exists in 'scm_product'.");
            }

            if (!hasColumn("scm_product", "image_url")) {
                jdbcTemplate.execute("ALTER TABLE scm_product ADD COLUMN image_url VARCHAR(500) COMMENT '商品图片' AFTER remark");
                log.info("Added 'image_url' to 'scm_product'.");
            } else {
                log.info("Column 'image_url' already exists in 'scm_product'.");
            }
            
        } catch (Exception e) {
            log.error("Failed to patch database: {}", e.getMessage());
            // 不抛出异常，以免影响系统启动，但记录错误
        }
    }
}
