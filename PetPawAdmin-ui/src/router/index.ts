import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import { useUserStore } from "@/stores/user";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/auth/LoginView.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/dashboard",
    component: () => import("@/layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        redirect: "/dashboard/home",
      },
      {
        path: "home",
        name: "Dashboard",
        component: () => import("@/views/dashboard/DashboardView.vue"),
        meta: { title: "数据大屏" },
      },
      // CRM模块 - 客户与宠物档案中心
      {
        path: "crm/customers",
        name: "CustomerList",
        component: () => import("@/views/crm/CustomerListView.vue"),
        meta: { title: "客户列表" },
      },
      {
        path: "crm/customer/:id",
        name: "CustomerDetail",
        component: () => import("@/views/crm/CustomerDetailView.vue"),
        meta: { title: "客户详情" },
      },
      {
        path: "crm/pets",
        name: "PetList",
        component: () => import("@/views/crm/PetListView.vue"),
        meta: { title: "宠物档案" },
      },
      {
        path: "crm/pet/:id",
        name: "PetDetail",
        component: () => import("@/views/crm/PetDetailView.vue"),
        meta: { title: "宠物详情" },
      },
      // SCM模块 - 智慧供应链
      {
        path: "scm/products",
        name: "ProductList",
        component: () => import("@/views/scm/ProductListView.vue"),
        meta: { title: "商品管理" },
      },
      {
        path: "scm/inventory",
        name: "Inventory",
        component: () => import("@/views/scm/InventoryView.vue"),
        meta: { title: "库存管理" },
      },
      {
        path: "scm/suppliers",
        name: "SupplierList",
        component: () => import("@/views/scm/SupplierListView.vue"),
        meta: { title: "供应商管理" },
      },
      // 服务预约与调度
      {
        path: "appointment/schedule",
        name: "Schedule",
        component: () => import("@/views/appointment/ScheduleView.vue"),
        meta: { title: "排班看板" },
      },
      {
        path: "appointment/orders",
        name: "OrderList",
        component: () => import("@/views/appointment/OrderListView.vue"),
        meta: { title: "工单管理" },
      },
      {
        path: "appointment/services",
        name: "ServiceList",
        component: () => import("@/views/appointment/ServiceListView.vue"),
        meta: { title: "服务项目" },
      },
      // 财务与收银
      {
        path: "finance/cashier",
        name: "Cashier",
        component: () => import("@/views/finance/CashierView.vue"),
        meta: { title: "收银台" },
      },
      {
        path: "finance/orders",
        name: "FinanceOrderList",
        component: () => import("@/views/finance/OrderListView.vue"),
        meta: { title: "财务订单" },
      },
      {
        path: "finance/members",
        name: "MemberList",
        component: () => import("@/views/finance/MemberListView.vue"),
        meta: { title: "会员管理" },
      },
      {
        path: "finance/reports",
        name: "FinanceReports",
        component: () => import("@/views/finance/FinanceReportsView.vue"),
        meta: { title: "财务报表" },
      },
      // 人力资源
      {
        path: "hr/employees",
        name: "EmployeeList",
        component: () => import("@/views/hr/EmployeeListView.vue"),
        meta: { title: "员工管理" },
      },
      {
        path: "hr/positions",
        name: "PositionList",
        component: () => import("@/views/hr/PositionListView.vue"),
        meta: { title: "职位管理" },
      },
    ],
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import("@/views/error/NotFoundView.vue"),
    meta: { title: "页面未找到" },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// 检查token是否有效
const isTokenValid = (token: string): boolean => {
  if (!token) return false;
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    const exp = payload.exp * 1000; // 转换为毫秒
    const now = Date.now();
    return exp > now; // token未过期
  } catch {
    return false; // token格式错误
  }
};

// 检查token是否即将过期（5分钟内）
const isTokenExpiringSoon = (token: string): boolean => {
  if (!isTokenValid(token)) return true;
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    const exp = payload.exp * 1000; // 转换为毫秒
    const now = Date.now();
    const fiveMinutes = 5 * 60 * 1000;
    return exp - now < fiveMinutes;
  } catch {
    return true;
  }
};

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 智慧宠物后台管理系统`;
  }

  // 获取用户store
  const userStore = useUserStore();

  // 白名单路由，不需要登录
  const whiteList = ["/"];

  if (whiteList.includes(to.path)) {
    // 如果已登录，重定向到dashboard
    if (userStore.token && isTokenValid(userStore.token)) {
      next("/dashboard/home");
      return;
    }
    next();
    return;
  }

  // 需要登录的路由
  if (!userStore.token || !isTokenValid(userStore.token)) {
    // 未登录或token无效，重定向到登录页
    userStore.logout();
    next("/");
    return;
  }

  // 已登录用户访问，检查token是否即将过期
  if (isTokenExpiringSoon(userStore.token)) {
    try {
      const { authApi } = await import("@/api/auth");
      const response = await authApi.refreshToken();
      if ((response as any).data.code === 200) {
        userStore.setToken((response as any).data.data.token);
      } else {
        // 刷新失败，登出
        userStore.logout();
        next("/");
        return;
      }
    } catch (error) {
      // 刷新失败，登出
      userStore.logout();
      next("/");
      return;
    }
  }

  next();
});

export default router;
