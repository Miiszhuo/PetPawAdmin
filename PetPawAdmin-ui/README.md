# 智慧宠物后台管理系统 - 前端

## 项目简介

这是一个专为宠物服务行业打造的企业资源计划（ERP）系统前端项目，采用现代化的Vue 3 + TypeScript技术栈构建。

## 核心功能

### 👥 客户与宠物档案中心 (CRM)
- 客户精细化管理
- 宠物电子病历/档案
- 可视化健康时间轴

### 📦 智慧供应链 (SCM)
- 商品中心管理
- 智能仓储系统
- 供应商管理

### 📅 服务预约与智能调度
- 服务项目库配置
- 可视化排班看板
- 工单全生命周期管理

### 💰 财务与收银中心
- 聚合收银台
- 会员账户体系
- 经营驾驶舱 (BI)

### 🛡️ 系统安全与权限 (RBAC)
- Spring Security 6 集成
- 动态权限控制
- 操作日志审计

## 技术栈

### 前端技术栈
- **框架**: Vue.js 3.3+
- **构建工具**: Vite 4.0+
- **语言**: TypeScript 5.x
- **状态管理**: Pinia 2.x
- **UI组件库**: Element Plus 2.x
- **网络请求**: Axios 1.6+
- **图表库**: ECharts 5.4+
- **路由**: Vue Router 4.x

### 开发工具
- **代码规范**: ESLint + Prettier
- **样式预处理器**: Sass
- **包管理**: npm

## 项目结构

```
PetPawAdmin-ui/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   │   └── auth.ts        # 认证相关接口
│   ├── components/        # 公共组件
│   │   ├── common/        # 通用组件
│   │   │   ├── PageHeader.vue    # 页面头部组件
│   │   │   └── DataTable.vue     # 数据表格组件
│   │   └── layout/        # 布局组件
│   ├── layouts/           # 页面布局
│   │   └── MainLayout.vue # 主布局
│   ├── router/            # 路由配置
│   │   └── index.ts       # 路由定义
│   ├── stores/            # Pinia状态管理
│   │   ├── user.ts        # 用户状态
│   │   └── app.ts         # 应用状态
│   ├── types/             # TypeScript类型定义
│   │   └── common.ts      # 通用类型
│   ├── utils/             # 工具函数
│   │   └── request.ts     # HTTP请求工具
│   ├── views/             # 页面组件
│   │   ├── auth/          # 认证页面
│   │   │   └── LoginView.vue
│   │   ├── dashboard/     # 数据大屏
│   │   │   └── DashboardView.vue
│   │   ├── crm/           # CRM模块
│   │   │   ├── CustomerListView.vue
│   │   │   └── ...
│   │   ├── scm/           # 供应链模块
│   │   ├── appointment/   # 预约调度模块
│   │   ├── finance/       # 财务模块
│   │   ├── system/        # 系统管理模块
│   │   └── error/         # 错误页面
│   ├── App.vue            # 根组件
│   └── main.ts            # 入口文件
├── package.json
├── vite.config.ts
├── tsconfig.json
└── README.md
```

## 快速开始

### 环境要求
- Node.js 16+
- npm 8+

### 安装依赖
```bash
npm install
```

### 开发环境运行
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 预览生产版本
```bash
npm run preview
```

### 代码检查
```bash
npm run lint
```

## 开发指南

### 页面开发流程
1. 在 `src/views/` 下创建对应的页面组件
2. 在 `src/router/index.ts` 中添加路由配置
3. 如需要API调用，在 `src/api/` 下添加接口定义
4. 如需要状态管理，在 `src/stores/` 下添加store

### 组件开发规范
- 使用Vue 3 Composition API
- 组件文件命名使用 PascalCase + View.vue
- 样式使用scoped Sass
- 遵循ESLint配置的代码规范

### API接口规范
- 所有API调用统一使用 `src/utils/request.ts`
- 接口定义放在 `src/api/` 目录下
- 使用TypeScript定义接口类型

## 部署说明

### 开发环境
- 默认运行在 `http://localhost:3000`
- API代理到 `http://localhost:8080`

### 生产环境
- 构建产物在 `dist/` 目录
- 支持静态资源部署
- 可配置Nginx反向代理

## 注意事项

1. **权限控制**: 当前版本的权限验证逻辑还未完全实现，后续会集成后端RBAC系统
2. **数据模拟**: 当前使用模拟数据，后续需要连接真实的后端API
3. **国际化**: 当前仅支持中文，后续可扩展多语言支持
4. **主题定制**: 当前使用默认主题，后续可添加主题切换功能

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证。
