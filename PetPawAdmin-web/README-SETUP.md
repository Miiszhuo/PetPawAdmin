# 智慧宠物后台管理系统 - 设置指南

## 🚀 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Node.js 16+ (前端开发)
- Maven 3.6+

### 2. 后端设置

#### 数据库初始化
```bash
# 方式1：使用完整设置脚本（推荐）
mysql -u root -proot < setup-database.sql

# 方式2：分别执行
mysql -u root -proot < database/init.sql
mysql -u root -proot petpaw_admin < update-passwords.sql
```

#### 启动后端服务
```bash
cd PetPawAdmin-web
mvn clean package -DskipTests
java -jar pet-paw-admin-controller/target/pet-paw-admin-controller-1.0.0.jar
```

后端服务将在 `http://localhost:6782` 启动

### 3. 前端设置

#### 安装依赖
```bash
cd PetPawAdmin-ui
npm install
```

#### 启动开发服务器
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

## 🔐 密码加密说明

### 加密方式变更
- **之前**: BCrypt单向加密
- **现在**: AES可逆加密

### 密码验证流程
```
前端输入密码 → 发送到后端 → AES解密数据库密码 → 明文比较 → 返回结果
```

### 测试账号

| 用户名 | 密码 | 角色 | 加密方式 |
|--------|------|------|----------|
| admin | 123456 | 系统管理员 | AES |
| mmm | 111222 | 测试员 | AES |

### 密码更新

如果需要添加新用户或更新密码：
```java
import com.petpaw.common.utils.AesUtils;

// 获取AES加密结果
String encryptedPassword = AesUtils.encrypt("your_password");
System.out.println("Encrypted: " + encryptedPassword);
```

然后在数据库中更新：
```sql
UPDATE sys_user SET password = 'AES_ENCRYPTED_RESULT' WHERE username = 'your_username';
```

## 🧪 测试验证

### 1. 后端API测试
```bash
# 测试登录接口
curl -X POST "http://localhost:6782/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 2. 前端登录测试
1. 打开浏览器访问 `http://localhost:3000`
2. 输入测试账号密码
3. 点击登录，查看是否成功跳转

### 3. 数据库验证
```sql
-- 查看用户密码状态
SELECT username, password,
       CASE WHEN LENGTH(password) > 10 THEN 'AES' ELSE 'Plain' END as encryption
FROM sys_user WHERE deleted = 0;
```

## 🔧 故障排除

### 问题1: 密码验证失败
- 检查数据库中的密码是否为AES加密格式
- 确认AES密钥是否一致（AesUtils.SECRET_KEY）
- 查看后端日志中的解密过程

### 问题2: 前端无法连接后端
- 检查后端服务是否启动（端口6782）
- 检查前端代理配置是否正确
- 查看浏览器Network标签的请求状态

### 问题3: 数据库连接失败
- 确认MySQL服务正在运行
- 检查数据库连接配置
- 确认数据库和表已正确创建

## 📋 系统架构

```
前端 (Vue 3 + TypeScript)
    ↓ HTTP请求 (端口3000)
前端代理 (/api/* → http://localhost:6782)
    ↓ JWT认证
后端 (Spring Boot 3.1.5)
    ↓ AES密码验证
数据库 (MySQL 8.0)
```

## 🎯 安全说明

⚠️ **重要提醒**:
- AES加密用于演示和测试目的
- 生产环境建议使用BCrypt或其他单向加密算法
- 敏感信息不应以可逆方式存储

## 📞 技术支持

如果遇到问题，请检查：
1. 控制台错误日志
2. 数据库连接状态
3. 网络请求状态
4. 浏览器开发者工具

---
**最后更新**: 2025-12-25
