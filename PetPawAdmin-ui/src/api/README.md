# 前端API调用文档

本文档描述了前端项目中所有API调用的使用方法。

## 目录结构

```
src/api/
├── auth.ts          # 认证相关API
├── system.ts        # 系统管理API (用户、角色、权限)
├── crm.ts          # CRM模块API (客户、宠物)
├── scm.ts          # 供应链API (商品、供应商)
├── appointment.ts  # 预约模块API (订单、服务项目)
├── finance.ts      # 财务模块API (订单、会员)
└── index.ts        # 统一导出文件
```

## 使用方法

### 1. 导入API模块

```typescript
// 导入单个API模块
import { systemApi } from '@/api/system'
import { crmApi } from '@/api/crm'

// 或者导入所有API
import { authApi, systemApi, crmApi, scmApi, appointmentApi, financeApi } from '@/api'
```

### 2. API调用示例

#### 认证相关
```typescript
// 登录
const loginResult = await authApi.login({
  username: 'admin',
  password: '123456'
})

// 获取用户信息
const userInfo = await authApi.getUserInfo()

// 刷新token
const refreshResult = await authApi.refreshToken()
```

#### 系统管理
```typescript
// 获取用户列表
const users = await systemApi.getUsers({
  current: 1,
  size: 10,
  username: 'admin'
})

// 创建用户
const newUser = await systemApi.createUser({
  username: 'newuser',
  realName: '新用户',
  status: 1
})

// 获取角色列表
const roles = await systemApi.getRoles({
  current: 1,
  size: 10
})
```

#### CRM管理
```typescript
// 获取客户列表
const customers = await crmApi.getCustomers({
  current: 1,
  size: 10,
  customerName: '张三'
})

// 创建客户
const newCustomer = await crmApi.createCustomer({
  customerName: '李四',
  phone: '13800138000'
})

// 获取宠物列表
const pets = await crmApi.getPets({
  current: 1,
  size: 10,
  customerId: 1
})
```

#### 供应链管理
```typescript
// 获取商品列表
const products = await scmApi.getProducts({
  current: 1,
  size: 10,
  productName: '宠物粮'
})

// 创建商品
const newProduct = await scmApi.createProduct({
  productName: '高级宠物粮',
  category: '宠物食品',
  price: 99.99,
  stockQuantity: 100,
  unit: '袋'
})

// 获取供应商列表
const suppliers = await scmApi.getSuppliers({
  current: 1,
  size: 10
})
```

#### 预约管理
```typescript
// 获取预约订单列表
const orders = await appointmentApi.getOrders({
  current: 1,
  size: 10,
  status: 'PENDING'
})

// 创建预约订单
const newOrder = await appointmentApi.createOrder({
  orderNumber: 'AP20241226001',
  customerId: 1,
  serviceItemId: 1,
  appointmentTime: '2024-12-26 14:00:00',
  status: 'PENDING'
})

// 获取服务项目列表
const serviceItems = await appointmentApi.getServiceItems({
  current: 1,
  size: 10,
  category: '洗澡'
})
```

#### 财务管理
```typescript
// 获取订单列表
const financeOrders = await financeApi.getOrders({
  current: 1,
  size: 10,
  orderStatus: 'PAID'
})

// 创建财务订单
const newFinanceOrder = await financeApi.createOrder({
  orderNumber: 'FO20241226001',
  customerId: 1,
  orderType: 'SERVICE',
  totalAmount: 199.99,
  actualAmount: 199.99
})

// 更新订单状态
await financeApi.updateOrderStatus(1, 'COMPLETED', 'PAID')
```

## 错误处理

所有API调用都会返回Promise，包含以下结构：

```typescript
interface ApiResponse<T = any> {
  code: number      // 状态码：200成功，其他失败
  message: string   // 响应消息
  data: T          // 响应数据
  timestamp: number // 时间戳
}
```

在Vue组件中使用时：

```typescript
import { ElMessage } from 'element-plus'

try {
  const result = await api.someMethod(params)
  if (result.data.code === 200) {
    // 成功处理
    console.log(result.data.data)
  } else {
    // 业务错误
    ElMessage.error(result.data.message)
  }
} catch (error) {
  // 网络错误或其他异常
  ElMessage.error('请求失败，请稍后重试')
}
```

## 分页查询

支持分页的API都遵循以下参数结构：

```typescript
interface PageQueryParams {
  current: number  // 当前页码
  size: number     // 每页大小
  // 其他查询条件...
}
```

响应数据结构：

```typescript
interface PageResult<T> {
  records: T[]     // 数据列表
  total: number    // 总记录数
  size: number     // 每页大小
  current: number  // 当前页码
}
```

## 注意事项

1. 所有API调用都会自动添加认证token（如果已登录）
2. 请求/响应拦截器会自动处理loading状态和错误提示
3. token过期时会自动刷新，无需手动处理
4. 所有时间字段使用ISO 8601格式的字符串
