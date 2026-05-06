<template>
  <div class="not-found">
    <div class="error-content">
      <div class="error-code">404</div>
      <h1 class="error-title">页面未找到</h1>
      <p class="error-description">抱歉，您访问的页面不存在或已被移除。</p>
      <div class="error-actions">
        <el-button type="primary" @click="$router.go(-1)">
          <el-icon><Back /></el-icon>
          返回上一页
        </el-button>
        <el-button @click="$router.push('/dashboard')">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Back, HomeFilled } from "@element-plus/icons-vue";
</script>

<style scoped lang="scss">
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;

  .error-content {
    text-align: center;
    max-width: 400px;

    .error-code {
      font-size: 72px;
      font-weight: 700;
      color: #409eff;
      margin-bottom: 16px;
    }

    .error-title {
      font-size: 24px;
      font-weight: 600;
      color: #1d2129;
      margin-bottom: 12px;
    }

    .error-description {
      color: #86909c;
      font-size: 16px;
      margin-bottom: 32px;
      line-height: 1.5;
    }

    .error-actions {
      display: flex;
      gap: 16px;
      justify-content: center;

      .el-button {
        min-width: 120px;
      }
    }
  }
}
</style>
