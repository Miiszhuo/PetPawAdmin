import { defineStore } from "pinia";
import { ref } from "vue";

export const useAppStore = defineStore("app", () => {
  // 状态
  const sidebarCollapsed = ref(false);
  const loading = ref(false);
  const theme = ref<"light" | "dark">("light");

  // 动作
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value;
  };

  const setLoading = (value: boolean) => {
    loading.value = value;
  };

  const setTheme = (newTheme: "light" | "dark") => {
    theme.value = newTheme;
    document.documentElement.setAttribute("data-theme", newTheme);
  };

  return {
    sidebarCollapsed,
    loading,
    theme,
    toggleSidebar,
    setLoading,
    setTheme,
  };
});
