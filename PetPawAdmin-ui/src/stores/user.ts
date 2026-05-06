import { defineStore } from "pinia";
import { ref, computed } from "vue";

export interface User {
  id: number;
  username: string;
  realName: string;
  avatar?: string;
  status: number;
}

export const useUserStore = defineStore("user", () => {
  // 状态
  const user = ref<User | null>(null);
  const token = ref<string>("");

  // 计算属性
  const isLoggedIn = computed(() => !!token.value);
  const hasRole = computed(() => (role: string) => {
    return user.value?.roles.includes(role) ?? false;
  });
  const hasPermission = computed(() => (permission: string) => {
    return user.value?.permissions.includes(permission) ?? false;
  });

  // 动作
  const setUser = (userData: User) => {
    user.value = userData;
  };

  const setToken = (tokenValue: string) => {
    token.value = tokenValue;
    localStorage.setItem("token", tokenValue);
  };

  const logout = () => {
    user.value = null;
    token.value = "";
    localStorage.removeItem("token");
  };

  // 检查token是否有效
  const isTokenValid = (tokenStr: string): boolean => {
    if (!tokenStr) return false;
    try {
      const payload = JSON.parse(atob(tokenStr.split(".")[1]));
      const exp = payload.exp * 1000; // 转换为毫秒
      const now = Date.now();
      return exp > now; // token未过期
    } catch {
      return false; // token格式错误
    }
  };

  const loadFromStorage = () => {
    const storedToken = localStorage.getItem("token");
    if (storedToken && isTokenValid(storedToken)) {
      token.value = storedToken;
    } else {
      // token无效或不存在，清除localStorage
      localStorage.removeItem("token");
      token.value = "";
    }
  };

  return {
    user,
    token,
    isLoggedIn,
    hasRole,
    hasPermission,
    setUser,
    setToken,
    logout,
    loadFromStorage,
  };
});
