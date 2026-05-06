<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <el-aside
      :width="sidebarCollapsed ? '72px' : '220px'"
      class="sidebar"
      :class="{ 'is-collapsed': sidebarCollapsed }"
    >
      <div class="sidebar-header">
        <div class="logo">
          <el-icon v-if="!sidebarCollapsed" size="32" class="logo-icon"
            ><Management
          /></el-icon>
          <span v-if="!sidebarCollapsed" class="logo-text"
            >智慧宠物管理系统</span
          >
          <el-icon v-else size="24" class="logo-icon"><Menu /></el-icon>
        </div>
      </div>

      <el-menu
        :key="menuKey"
        :default-active="$route.path"
        :default-openeds="defaultOpeneds"
        :collapse="sidebarCollapsed"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
        unique-opened
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard/home">
          <el-icon><Odometer /></el-icon>
          <template #title>数据大屏</template>
        </el-menu-item>

        <el-sub-menu index="crm-menu">
          <template #title>
            <el-icon><User /></el-icon>
            <span>客户管理</span>
          </template>
          <el-menu-item index="/dashboard/crm/customers">客户列表</el-menu-item>
          <el-menu-item index="/dashboard/crm/pets">宠物档案</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="scm-menu">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>供应链管理</span>
          </template>
          <el-menu-item index="/dashboard/scm/products">商品管理</el-menu-item>
          <el-menu-item index="/dashboard/scm/inventory">库存管理</el-menu-item>
          <el-menu-item index="/dashboard/scm/suppliers"
            >供应商管理</el-menu-item
          >
        </el-sub-menu>

        <el-sub-menu index="appointment-menu">
          <template #title>
            <el-icon><Calendar /></el-icon>
            <span>预约调度</span>
          </template>
          <el-menu-item index="/dashboard/appointment/schedule"
            >排班看板</el-menu-item
          >
          <el-menu-item index="/dashboard/appointment/orders"
            >工单管理</el-menu-item
          >
          <el-menu-item index="/dashboard/appointment/services"
            >服务项目</el-menu-item
          >
        </el-sub-menu>

        <el-sub-menu index="finance-menu">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>财务收银</span>
          </template>
          <el-menu-item index="/dashboard/finance/orders"
            >财务订单</el-menu-item
          >
          <el-menu-item index="/dashboard/finance/cashier">收银台</el-menu-item>
          <el-menu-item index="/dashboard/finance/members"
            >会员管理</el-menu-item
          >
          <el-menu-item index="/dashboard/finance/reports"
            >财务报表</el-menu-item
          >
        </el-sub-menu>

        <el-sub-menu index="hr-menu">
          <template #title>
            <el-icon><UserFilled /></el-icon>
            <span>人力资源</span>
          </template>
          <el-menu-item index="/dashboard/hr/employees">员工管理</el-menu-item>
          <el-menu-item index="/dashboard/hr/positions">职位管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button type="text" class="collapse-btn" @click="toggleSidebar">
            <el-icon
              ><Fold v-if="!sidebarCollapsed" /><Expand v-else
            /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="item in breadcrumbs"
              :key="item.path"
              :to="item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="user?.avatar">
                {{ user?.realName?.charAt(0) || user?.username?.charAt(0) }}
              </el-avatar>
              <span class="username">{{
                user?.realName || user?.username
              }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided
                  >退出登录</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </div>

    <!-- 全局loading -->
    <div v-if="loading" class="global-loading">
      <el-icon class="is-loading">
        <Loading />
      </el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { useAppStore } from "@/stores/app";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const appStore = useAppStore();

import {
  Menu,
  User,
  Box,
  Calendar,
  Money,
  Fold,
  Expand,
  ArrowDown,
  Odometer,
  Loading,
  Management,
  UserFilled,
} from "@element-plus/icons-vue";
import { ElMessageBox, ElMessage } from "element-plus";

const sidebarCollapsed = computed(() => appStore.sidebarCollapsed);
const loading = computed(() => appStore.loading);
const user = computed(() => userStore.user);

// 根据当前路由自动展开对应的一级菜单，保证“子菜单”结构更直观
const defaultOpeneds = computed(() => {
  const path = route.path;
  if (path.startsWith("/dashboard/crm/")) return ["crm-menu"];
  if (path.startsWith("/dashboard/scm/")) return ["scm-menu"];
  if (path.startsWith("/dashboard/appointment/")) return ["appointment-menu"];
  if (path.startsWith("/dashboard/finance/")) return ["finance-menu"];
  if (path.startsWith("/dashboard/hr/")) return ["hr-menu"];
  return [];
});

// 通过 key 触发菜单重渲染，确保首次进入页面时默认展开正确生效
const menuKey = computed(
  () => `${sidebarCollapsed.value}-${defaultOpeneds.value.join(",")}`,
);

// 面包屑导航
const breadcrumbs = computed(() => {
  const matched = route.matched.filter((item) => item.meta?.title);
  return matched.map((item) => ({
    title: item.meta.title as string,
    path: item.path,
  }));
});

const toggleSidebar = () => {
  appStore.toggleSidebar();
};

const handleCommand = (command: string) => {
  switch (command) {
    case "profile":
      ElMessage.info("个人资料功能开发中");
      break;
    case "settings":
      ElMessage.info("系统设置功能开发中");
      break;
    case "logout":
      ElMessageBox.confirm("确定要退出登录吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        userStore.logout();
        router.push("/");
        ElMessage.success("已退出登录");
      });
      break;
  }
};

onMounted(() => {
  // 加载用户信息
  userStore.loadFromStorage();
});
</script>

<style scoped lang="scss">
.main-layout {
  height: 100vh;
  display: flex;

  .sidebar {
    background: linear-gradient(180deg, #243246 0%, #1d2736 100%);
    transition: width 0.3s;
    box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);

    .sidebar-header {
      height: 60px;
      display: flex;
      align-items: center;
      padding: 0 20px;
      border-bottom: 1px solid #3a3f4b;

      .logo {
        display: flex;
        align-items: center;
        color: #fff;

        .logo-text {
          font-size: 16px;
          font-weight: 600;
        }

        .logo-icon {
          margin-right: 10px;
        }
      }
    }

    &.is-collapsed {
      .sidebar-header {
        padding: 0;
        justify-content: center;
      }

      .logo {
        justify-content: center;

        .logo-icon {
          margin-right: 0;
        }
      }
    }

    .sidebar-menu {
      border-right: none;
      height: calc(100vh - 60px);
      overflow-y: auto;

      :deep(.el-menu) {
        border-right: none;
      }

      :deep(.el-menu-item),
      :deep(.el-sub-menu__title) {
        height: 44px;
        line-height: 44px;
        margin: 6px 10px;
        border-radius: 10px;
      }

      :deep(.el-menu-item:hover),
      :deep(.el-sub-menu__title:hover) {
        background-color: rgba(255, 255, 255, 0.06);
      }

      :deep(.el-menu-item.is-active) {
        background-color: rgba(64, 158, 255, 0.18);
        color: #fff;
      }

      :deep(.el-sub-menu .el-menu-item) {
        height: 40px;
        line-height: 40px;
        margin: 4px 14px;
        border-radius: 8px;
      }

      :deep(.el-sub-menu .el-menu-item.is-active) {
        background-color: rgba(64, 158, 255, 0.22);
      }
    }
  }

  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;

    .header {
      height: 60px;
      background-color: #fff;
      border-bottom: 1px solid #e6e6e6;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

      .header-left {
        display: flex;
        align-items: center;

        .collapse-btn {
          margin-right: 20px;
        }
      }

      .header-right {
        .user-info {
          display: flex;
          align-items: center;
          cursor: pointer;
          padding: 8px 12px;
          border-radius: 4px;
          transition: background-color 0.3s;

          &:hover {
            background-color: #f5f5f5;
          }

          .username {
            margin: 0 8px;
            font-size: 14px;
          }
        }
      }
    }

    .content {
      flex: 1;
      padding: 16px;
      background-color: #f0f2f5;
      overflow-y: auto;
    }
  }

  .global-loading {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;

    .el-icon {
      font-size: 40px;
      color: #409eff;
    }
  }
}
</style>
