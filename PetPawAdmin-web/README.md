# 智慧宠物后台管理系统

## 项目简介

智慧宠物后台管理系统是一个专为宠物服务行业打造的企业资源计划（ERP）系统。通过统一平台打通"人、货、场、客、财"五大维度，实现精细化运营与数据驱动决策。

## 技术栈

- **后端框架**: Spring Boot 3.2.0
- **Java版本**: JDK 17
- **数据库**: MySQL 8.0+
- **ORM框架**: MyBatis Plus 3.5.4.1
- **缓存**: Redis 7.0
- **API文档**: SpringDoc OpenAPI 3
- **构建工具**: Maven 3.9+

## 核心功能模块

### 👥 客户与宠物档案中心 (CRM)
- 客户精细化管理
- 宠物电子病历/档案
- 可视化健康时间轴

### 📦 智慧供应链 (SCM)
- 商品中心SPU/SKU管理
- 智能仓储管理
- 供应商管理

### 📅 服务预约与智能调度
- 服务项目库管理
- 可视化排班看板
- 工单全生命周期管理

### 💰 财务与收银中心
- 聚合收银台
- 会员账户体系
- 经营驾驶舱(BI报表)

### 🛡️ 系统安全与权限 (RBAC)
- 用户权限管理
- 角色权限控制
- 操作日志审计

## 快速开始

### 1. 环境准备

- JDK 17+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.9+

### 2. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE petpaw_admin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 运行初始化脚本
source database/init.sql;
```

### 3. 配置数据库连接

编辑 `pet-paw-admin-controller/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/petpaw_admin?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 123456  # 修改为你的数据库密码
```

### 4. 编译运行

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包运行
mvn clean package -DskipTests
java -jar pet-paw-admin-controller/target/pet-paw-admin-controller-1.0.0.jar
```

### 5. 访问系统

- **后端API**: http://localhost:6759
- **API文档**: http://localhost:6759/swagger-ui.html

### 6. 默认账号

- **用户名**: admin
- **密码**: 123456

## 项目结构

```
PetPawAdmin-web/
├── pet-paw-admin-common/          # 公共模块
│   ├── src/main/java/com/petpaw/common/
│   │   ├── result/               # 统一响应结构
│   │   ├── exception/            # 异常处理
│   │   ├── model/                # 基础模型
│   │   └── utils/                # 工具类
├── pet-paw-admin-service/         # 业务服务模块
│   ├── src/main/java/com/petpaw/
│   │   ├── entity/               # 实体类
│   │   ├── mapper/               # 数据访问层
│   │   ├── service/              # 业务逻辑层
│   │   └── config/               # 配置类
├── pet-paw-admin-controller/      # 控制器模块
│   ├── src/main/java/com/petpaw/
│   │   ├── controller/           # REST控制器
│   │   └── PetPawAdminApplication.java
└── database/init.sql              # 数据库初始化脚本
```

## API 接口文档

系统提供了完整的REST API接口，主要包括：

### 用户管理
- `GET /api/users` - 分页查询用户
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### 角色管理
- `GET /api/roles` - 分页查询角色
- `POST /api/roles` - 创建角色
- `PUT /api/roles/{id}` - 更新角色
- `POST /api/roles/assign/{userId}` - 为用户分配角色

### 权限管理
- `GET /api/permissions` - 分页查询权限
- `POST /api/permissions` - 创建权限
- `GET /api/permissions/tree` - 获取权限树

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发规范
- 使用Lombok简化代码
- 统一异常处理
- 统一响应格式

### 数据库规范
- 表名使用下划线分隔
- 字段使用驼峰命名
- 必须有创建时间和更新时间字段
- 使用逻辑删除

### API规范
- RESTful风格
- 统一响应格式
- 分页查询统一参数
- 详细的错误信息

## 部署说明

### 生产环境部署

1. 修改配置文件中的数据库和Redis连接信息
2. 设置合适的JVM参数
3. 配置日志级别为INFO或WARN
4. 启用安全配置（如HTTPS）

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/pet-paw-admin-controller-1.0.0.jar app.jar
EXPOSE 6759
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认数据库和用户密码配置正确
- 检查数据库是否存在

### 2. Redis连接失败
- 检查Redis服务是否启动
- 确认Redis配置正确

### 3. 端口占用
- 修改application.yml中的server.port配置
- 或停止占用该端口的服务

## 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题或建议，请通过以下方式联系：
- 项目地址: [GitHub Repository]
- 邮箱: your-email@example.com