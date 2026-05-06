// 统一导出所有API模块
export { authApi } from "./auth";
export { systemApi } from "./system";
export { crmApi } from "./crm";
export { scmApi } from "./scm";
export { appointmentApi } from "./appointment";
export { financeApi } from "./finance";
export { hrApi } from "./hr";

// 导出系统日志相关类型
export type { SysLog } from "./system";

// 导出所有类型定义
export type {
  // 认证相关类型
  LoginParams,
  LoginResponse,
  UserInfo,

  // 系统管理相关类型
  SysUser,
  UserQueryParams,
  SysRole,
  RoleQueryParams,
  SysPermission,
  PermissionQueryParams,

  // CRM相关类型
  CrmCustomer,
  CustomerQueryParams,
  CrmPet,
  PetQueryParams,

  // 供应链相关类型
  ScmProduct,
  ProductQueryParams,
  ScmSupplier,
  SupplierQueryParams,

  // 预约相关类型
  AppointmentOrder,
  OrderQueryParams,
  AppointmentServiceItem,
  ServiceItemQueryParams,

  // 财务相关类型
  FinanceOrder,
  OrderQueryParams as FinanceOrderQueryParams,
  FinanceMember,
  MemberQueryParams,

  // 通用类型
  PageResult,
} from "./finance";

export type {
  SysUser,
  UserQueryParams,
  SysRole,
  RoleQueryParams,
  SysPermission,
  PermissionQueryParams,
  SysLog,
  LogQueryParams,
} from "./system";

export type {
  CrmCustomer,
  CustomerQueryParams,
  CrmPet,
  PetQueryParams,
} from "./crm";

export type {
  ScmProduct,
  ProductQueryParams,
  ScmSupplier,
  SupplierQueryParams,
  ScmInventoryRecord,
  InventoryRecordQueryParams,
} from "./scm";

export type {
  AppointmentOrder,
  OrderQueryParams,
  AppointmentServiceItem,
  ServiceItemQueryParams,
} from "./appointment";

export type { FinanceOrder, FinanceMember, MemberQueryParams } from "./finance";
