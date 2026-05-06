import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

const app = createApp(App);

const pinia = createPinia();
app.use(pinia);
app.use(router);
app.use(ElementPlus);

// 在应用启动时恢复用户状态
import { useUserStore } from "@/stores/user";
const userStore = useUserStore(pinia);
userStore.loadFromStorage();

// 开发环境调试：清除无效token
if (import.meta.env.DEV) {
  console.log("应用启动，用户状态:", {
    hasToken: !!userStore.token,
    isLoggedIn: userStore.isLoggedIn,
    tokenValid: userStore.token
      ? (() => {
          try {
            const payload = JSON.parse(atob(userStore.token.split(".")[1]));
            const exp = payload.exp * 1000;
            return exp > Date.now();
          } catch {
            return false;
          }
        })()
      : false,
  });
}

app.mount("#app");
