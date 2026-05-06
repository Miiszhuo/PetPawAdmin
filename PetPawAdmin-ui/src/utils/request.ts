import axios, {
  type AxiosInstance,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";
import { useAppStore } from "@/stores/app";

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore();
    const appStore = useAppStore();

    // 添加token
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`;
    }

    // 显示loading
    if (config.showLoading !== false) {
      appStore.setLoading(true);
    }

    // 调试日志
    console.log(
      "发送请求:",
      config.method?.toUpperCase(),
      config.url,
      config.data,
    );

    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// 标记是否正在刷新token，避免重复刷新
let isRefreshing = false;
let refreshPromise: Promise<any> | null = null;

// 刷新token的函数
const refreshToken = async () => {
  if (isRefreshing && refreshPromise) {
    return refreshPromise;
  }

  isRefreshing = true;
  const userStore = useUserStore();

  try {
    const { authApi } = await import("@/api/auth");
    refreshPromise = authApi.refreshToken();

    const response = await refreshPromise;
    if (response.data.code === 200) {
      userStore.setToken(response.data.data.token);
      return response.data.data.token;
    } else {
      throw new Error("Token refresh failed");
    }
  } catch (error) {
    userStore.logout();
    window.location.href = "/login";
    throw error;
  } finally {
    isRefreshing = false;
    refreshPromise = null;
  }
};

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const appStore = useAppStore();

    // 隐藏loading
    appStore.setLoading(false);

    const { data, status } = response;

    // 调试日志
    console.log(
      "收到响应:",
      response.config.url,
      "状态:",
      status,
      "数据:",
      data,
    );

    // 处理业务错误
    if (status === 200 && data.code !== 200) {
      ElMessage.error(data.message || "请求失败");

      return Promise.reject(new Error(data.message || "请求失败"));
    }

    // 成功时，返回 response.data（包含 code, message, data, timestamp）
    // 这样前端可以直接访问 response.data 获取业务数据
    return data;
  },
  async (error) => {
    const appStore = useAppStore();
    appStore.setLoading(false);

    const { response, config } = error;

    // 调试日志
    console.error(
      "请求错误:",
      error.config?.url,
      error.message,
      response?.status,
      response?.data,
    );

    if (response) {
      const { status, data } = response;

      switch (status) {
        case 400:
          ElMessage.error(data.message || "请求参数错误");
          break;
        case 401:
          {
            const userStore = useUserStore();
            if (userStore.token && !config._retry) {
              config._retry = true;
              try {
                await refreshToken();
                config.headers.Authorization = `Bearer ${userStore.token}`;
                return service(config);
              } catch (refreshError) {
                void refreshError;
                ElMessage.error("登录已过期，请重新登录");
                return Promise.reject(error);
              }
            } else {
              ElMessage.error("未授权，请重新登录");
              userStore.logout();
              window.location.href = "/login";
            }
          }
          break;
        case 403:
          ElMessage.error("拒绝访问");
          break;
        case 404:
          ElMessage.error("请求地址不存在");
          break;
        case 408:
          ElMessage.error("请求超时");
          break;
        case 500:
          ElMessage.error("服务器内部错误");
          break;
        case 501:
          ElMessage.error("服务未实现");
          break;
        case 502:
          ElMessage.error("网关错误");
          break;
        case 503:
          ElMessage.error("服务不可用");
          break;
        case 504:
          ElMessage.error("网关超时");
          break;
        case 505:
          ElMessage.error("HTTP版本不受支持");
          break;
        default:
          ElMessage.error(data.message || `连接错误${status}`);
      }
    } else {
      // 网络错误
      if (error.code === "ECONNABORTED") {
        ElMessage.error("请求超时");
      } else {
        ElMessage.error("网络错误，请检查网络连接");
      }
    }

    return Promise.reject(error);
  },
);

export default service;
