<template>
  <div class="login-container">
    <div class="login-form-container">
      <div class="login-header">
        <h1 class="title">智慧宠物后台管理系统</h1>
        <p class="subtitle">请登录您的账号</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
        class="login-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p class="tips">建议使用 Chrome 浏览器获得最佳体验</p>
      </div>
    </div>

    <div class="login-bg">
      <div class="bg-content">
        <div class="feature-list">
          <div class="feature-item">
            <el-icon size="32" class="feature-icon"><Management /></el-icon>
            <div class="feature-text">
              <h3>全链路数字化</h3>
              <p>打通人、货、场、客、财五大维度</p>
            </div>
          </div>
          <div class="feature-item">
            <el-icon size="32" class="feature-icon"><DataAnalysis /></el-icon>
            <div class="feature-text">
              <h3>数据驱动决策</h3>
              <p>多维度的报表分析，精准经营决策</p>
            </div>
          </div>
          <div class="feature-item">
            <el-icon size="32" class="feature-icon"><Timer /></el-icon>
            <div class="feature-text">
              <h3>智能调度</h3>
              <p>可视化排班看板，优化资源配置</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import {
  User,
  Lock,
  Management,
  DataAnalysis,
  Timer,
} from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";
import { authApi, type LoginParams } from "@/api/auth";

const router = useRouter();
const userStore = useUserStore();

const loginFormRef = ref<FormInstance>();
const loading = ref(false);

const loginForm = reactive<LoginParams>({
  username: "",
  password: "",
});

const loginRules: FormRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 3,
      max: 20,
      message: "用户名长度在 3 到 20 个字符",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度在 6 到 20 个字符", trigger: "blur" },
  ],
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  try {
    await loginFormRef.value.validate();

    loading.value = true;

    const response = await authApi.login(loginForm);

    // 后端返回的数据结构：{code: 200, message: "登录成功", data: {token: "...", user: {...}}}
    // 现在response直接是Result对象，response.data才是实际的业务数据
    if (response && response.code === 200 && response.data) {
      const { token, user } = response.data;

      // 保存token和用户信息
      userStore.setToken(token);
      userStore.setUser(user);

      ElMessage.success("登录成功");

      // 跳转到首页
      await router.push("/dashboard/home");
    } else {
      ElMessage.error(response.data?.message || "登录失败");
    }
  } catch (error: any) {
    console.error("登录失败:", error);
    // 错误处理已在request拦截器中处理
    if (error.message) {
      ElMessage.error(error.message);
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  // 如果已经登录，跳转到首页
  if (userStore.isLoggedIn) {
    router.push("/dashboard/home");
  }
});
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .login-form-container {
    flex: 1;
    max-width: 400px;
    padding: 40px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    background-color: #fff;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);

    .login-header {
      text-align: center;
      margin-bottom: 40px;

      .title {
        font-size: 24px;
        font-weight: 600;
        color: #1d2129;
        margin-bottom: 8px;
      }

      .subtitle {
        color: #86909c;
        font-size: 14px;
      }
    }

    .login-form {
      .login-btn {
        width: 100%;
        margin-top: 20px;
      }
    }

    .login-footer {
      margin-top: 40px;
      text-align: center;

      .tips {
        color: #c9cdd4;
        font-size: 12px;
      }
    }
  }

  .login-bg {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;

    .bg-content {
      max-width: 500px;
      color: #fff;

      .feature-list {
        .feature-item {
          display: flex;
          align-items: center;
          margin-bottom: 32px;

          &:last-child {
            margin-bottom: 0;
          }

          .feature-icon {
            margin-right: 20px;
            color: #409eff;
            background-color: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
            padding: 12px;
          }

          .feature-text {
            h3 {
              font-size: 18px;
              font-weight: 600;
              margin-bottom: 8px;
            }

            p {
              font-size: 14px;
              opacity: 0.8;
              margin: 0;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;

    .login-form-container {
      max-width: none;
      flex: none;
      height: 100vh;
    }

    .login-bg {
      display: none;
    }
  }
}
</style>
