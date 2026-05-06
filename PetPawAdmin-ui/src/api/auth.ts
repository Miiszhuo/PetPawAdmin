import request from "@/utils/request";

export interface LoginParams {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: {
    id: number;
    username: string;
    realName: string;
    avatar?: string;
    status: number;
  };
}

export interface UserInfo {
  id: number;
  username: string;
  realName: string;
  avatar?: string;
  status: number;
}

export const authApi = {
  // 登录
  login: (data: LoginParams) => {
    return request.post<{
      token: string;
      user: UserInfo;
    }>("/auth/login", data);
  },

  // 登出
  logout: () => {
    return request.post("/auth/logout");
  },

  // 获取用户信息
  getUserInfo: () => {
    return request.get<UserInfo>("/auth/user");
  },

  // 刷新token
  refreshToken: () => {
    return request.post("/auth/refresh");
  },

  // AES测试接口（如果需要）
  aesTest: () => {
    return request.get("/auth/aes-test");
  },
};
