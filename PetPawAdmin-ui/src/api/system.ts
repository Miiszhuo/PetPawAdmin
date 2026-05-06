import request from "@/utils/request";

// 用户管理相关接口
export interface SysUser {
  id?: number;
  username: string;
  password?: string;
  realName: string;
  email?: string;
  phone?: string;
  gender?: number;
  avatar?: string;
  imageUrl?: string;
  status: number;
  createTime?: string;
  updateTime?: string;
}

export interface UserQueryParams {
  current: number;
  size: number;
  username?: string;
  realName?: string;
  status?: number;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

// 角色管理相关接口
export interface SysRole {
  id?: number;
  roleName: string;
  roleCode: string;
  description?: string;
  status: number;
  createTime?: string;
  updateTime?: string;
}

export interface RoleQueryParams {
  current: number;
  size: number;
  roleName?: string;
  roleCode?: string;
  status?: number;
}

// 权限管理相关接口
export interface SysPermission {
  id?: number;
  permissionName: string;
  permissionCode: string;
  permissionType: number;
  parentId?: number;
  url?: string;
  icon?: string;
  sort: number;
  status: number;
  createTime?: string;
  updateTime?: string;
}

export interface PermissionQueryParams {
  current: number;
  size: number;
  permissionName?: string;
  permissionType?: number;
  status?: number;
}

export const systemApi = {
  // ============= 用户管理 =============
  // 获取用户列表
  getUsers: (params: UserQueryParams) => {
    return request.get<PageResult<SysUser>>("/users", { params });
  },

  // 获取用户详情
  getUser: (id: number) => {
    return request.get<SysUser>(`/users/${id}`);
  },

  // 创建用户
  createUser: (data: SysUser) => {
    return request.post("/users", data);
  },

  // 更新用户
  updateUser: (id: number, data: SysUser) => {
    return request.put(`/users/${id}`, data);
  },

  // 删除用户
  deleteUser: (id: number) => {
    return request.delete(`/users/${id}`);
  },

  // 重置密码
  resetPassword: (id: number, newPassword: string) => {
    return request.put(`/users/${id}/password/reset`, null, {
      params: { newPassword },
    });
  },

  // 启用/禁用用户
  changeUserStatus: (id: number, status: number) => {
    return request.put(`/users/${id}/status`, null, {
      params: { status },
    });
  },

  // ============= 角色管理 =============
  // 获取角色列表
  getRoles: (params: RoleQueryParams) => {
    return request.get<PageResult<SysRole>>("/roles", { params });
  },

  // 获取角色详情
  getRole: (id: number) => {
    return request.get<SysRole>(`/roles/${id}`);
  },

  // 创建角色
  createRole: (data: SysRole) => {
    return request.post("/roles", data);
  },

  // 更新角色
  updateRole: (id: number, data: SysRole) => {
    return request.put(`/roles/${id}`, data);
  },

  // 删除角色
  deleteRole: (id: number) => {
    return request.delete(`/roles/${id}`);
  },

  // 为用户分配角色
  assignRolesToUser: (userId: number, roleIds: number[]) => {
    // 根据后端 SysUserController 接口，没有专门的 assign 方法，通常通过更新用户来实现
    // 或者需要后端新增接口。这里假设后端路径为 /users/{id}/roles
    return request.post(`/users/${userId}/roles`, roleIds);
  },

  // 获取用户的角色列表
  getUserRoles: (userId: number) => {
    return request.get<SysRole[]>(`/users/${userId}/roles`);
  },

  // 为角色分配权限
  assignPermissionsToRole: (roleId: number, permissionIds: number[]) => {
    return request.post(`/roles/${roleId}/permissions`, permissionIds);
  },

  // ============= 权限管理 =============
  // 获取权限列表
  getPermissions: (params: PermissionQueryParams) => {
    return request.get<PageResult<SysPermission>>("/permissions", { params });
  },

  // 获取权限详情
  getPermission: (id: number) => {
    return request.get<SysPermission>(`/permissions/${id}`);
  },

  // 创建权限
  createPermission: (data: SysPermission) => {
    return request.post("/permissions", data);
  },

  // 更新权限
  updatePermission: (id: number, data: SysPermission) => {
    return request.put(`/permissions/${id}`, data);
  },

  // 删除权限
  deletePermission: (id: number) => {
    return request.delete(`/permissions/${id}`);
  },

  // 获取权限树形结构
  getPermissionTree: () => {
    return request.get<SysPermission[]>("/permissions/tree");
  },

  // ============= 系统日志管理 =============
  // 获取系统日志列表
  getLogs: (params: LogQueryParams) => {
    return request.get<PageResult<SysLog>>("/logs", { params });
  },

  // 获取日志详情
  getLog: (id: number) => {
    return request.get<SysLog>(`/logs/${id}`);
  },

  // 清理过期日志
  cleanExpiredLogs: (days: number) => {
    return request.delete(`/logs/expired/${days}`);
  },

  // 导出日志数据
  exportLogs: (params: Omit<LogQueryParams, "current" | "size">) => {
    return request.get<SysLog[]>("/logs/export", { params });
  },

  // 删除日志
  deleteLog: (id: number) => {
    return request.delete(`/logs/${id}`);
  },
};

// 系统日志相关接口
export interface SysLog {
  id?: number;
  userId?: number;
  username?: string;
  operationType: string;
  operationModule: string;
  operationDesc?: string;
  method?: string;
  params?: string;
  ip?: string;
  url?: string;
  result?: number;
  errorMsg?: string;
  executionTime?: number;
  userAgent?: string;
  location?: string;
  createTime?: string;
}

export interface LogQueryParams {
  current: number;
  size: number;
  username?: string;
  operationType?: string;
  operationModule?: string;
  startTime?: string;
  endTime?: string;
}
