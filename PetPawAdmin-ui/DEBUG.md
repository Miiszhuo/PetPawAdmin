# 前端调用后端接口调试指南

## 🔍 问题排查步骤

### 1. 检查浏览器控制台

打开浏览器开发者工具（F12），查看：
- **Console标签**: 查看是否有错误信息
- **Network标签**: 查看请求是否发送，响应状态码

### 2. 检查请求路径

登录接口的完整路径应该是：
```
POST http://localhost:3000/api/auth/login
```

实际代理到：
```
POST http://localhost:6782/api/auth/login
```

### 3. 检查后端服务

确保后端服务正在运行：
```bash
# 检查后端是否启动
curl http://localhost:6782/api/auth/login
```

### 4. 检查代理配置

在 `vite.config.ts` 中确认：
```typescript
proxy: {
  '/api': {
    target: 'http://localhost:6782',  // 确保端口正确
    changeOrigin: true
  }
}
```

### 5. 检查API调用

在 `src/api/auth.ts` 中：
```typescript
login: (data: LoginParams) => {
  return request.post('/auth/login', data)  // 路径是 /auth/login
}
```

在 `src/utils/request.ts` 中：
```typescript
baseURL: '/api'  // baseURL 是 /api
```

所以实际请求路径是：`/api/auth/login` ✅

### 6. 检查响应数据结构

后端返回格式：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "...",
    "user": {...}
  },
  "timestamp": 1234567890
}
```

前端应该访问：`response.data.data` 来获取 `{token, user}`

## 🐛 常见问题

### 问题1: 请求没有发送
- 检查浏览器控制台是否有错误
- 检查网络请求是否出现在 Network 标签
- 检查 Vite 开发服务器是否正常运行

### 问题2: 404 错误
- 检查后端服务是否启动
- 检查端口是否正确（6782）
- 检查代理配置是否正确

### 问题3: CORS 错误
- 检查后端是否配置了跨域支持
- 检查代理配置中的 `changeOrigin: true`

### 问题4: 响应数据格式错误
- 检查后端返回的数据结构
- 检查前端代码中访问数据的路径
- 查看浏览器控制台的响应数据

## 🔧 调试技巧

### 1. 添加 console.log
在登录函数中添加：
```javascript
console.log('开始登录', loginForm)
console.log('响应数据', response)
```

### 2. 查看 Network 请求
在浏览器 Network 标签中：
- 查看请求 URL
- 查看请求方法（POST）
- 查看请求体（Request Payload）
- 查看响应状态码
- 查看响应数据（Response）

### 3. 检查后端日志
查看后端控制台输出：
- 是否收到请求
- 请求路径是什么
- 是否有错误信息

## ✅ 验证步骤

1. 打开浏览器访问 `http://localhost:3000`
2. 打开开发者工具（F12）
3. 切换到 Network 标签
4. 输入用户名和密码，点击登录
5. 查看 Network 标签中是否有 `/api/auth/login` 请求
6. 查看请求的响应状态和数据

## 📝 当前配置

- **前端端口**: 3000
- **后端端口**: 6782
- **API路径**: `/api/auth/login`
- **代理配置**: `/api/*` → `http://localhost:6782`

## 🚀 快速测试

在浏览器控制台执行：
```javascript
fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'admin', password: '123456' })
})
.then(r => r.json())
.then(data => console.log('登录响应:', data))
.catch(err => console.error('错误:', err))
```

如果这个测试成功，说明代理和接口都正常，问题可能在 Vue 组件中。
