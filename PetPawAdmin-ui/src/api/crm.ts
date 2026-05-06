import request from "@/utils/request";

// 客户管理相关接口
export interface CrmCustomer {
  id?: number;
  customerName: string;
  phone: string;
  email?: string;
  address?: string;
  gender?: number;
  birthday?: string;
  remark?: string;
  status?: number;
  customerType?: string;
  imageUrl?: string;
  createTime?: string;
  updateTime?: string;
}

export interface CustomerQueryParams {
  current: number;
  size: number;
  customerName?: string;
  phone?: string;
  customerType?: string;
  startDate?: string;
  endDate?: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

// 宠物管理相关接口
export interface CrmPet {
  id?: number;
  customerId: number;
  petName: string;
  petType?: string;
  breed?: string;
  gender?: number;
  birthday?: string;
  weight?: number;
  color?: string;
  sterilization?: number;
  healthStatus?: string;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

export interface PetQueryParams {
  current: number;
  size: number;
  customerId?: number;
  petName?: string;
  petType?: string;
  breed?: string;
  ownerName?: string;
  status?: string;
}

export const crmApi = {
  // ============= 客户管理 =============
  // 获取客户列表
  getCustomers: <T = PageResult<CrmCustomer>>(params: CustomerQueryParams) => {
    return request.get<T>("/crm/customers", { params });
  },

  // 获取客户详情
  getCustomer: (id: number) => {
    return request.get<CrmCustomer>(`/crm/customers/${id}`);
  },

  // 创建客户
  createCustomer: (data: CrmCustomer) => {
    return request.post("/crm/customers", data);
  },

  // 更新客户
  updateCustomer: (id: number, data: CrmCustomer) => {
    return request.put(`/crm/customers/${id}`, data);
  },

  // 删除客户
  deleteCustomer: (id: number) => {
    return request.delete(`/crm/customers/${id}`);
  },

  // ============= 宠物管理 =============
  // 获取宠物列表
  getPets: (params: PetQueryParams) => {
    return request.get<PageResult<CrmPet>>("/crm/pets", { params });
  },

  // 获取宠物详情
  getPet: (id: number) => {
    return request.get<CrmPet>(`/crm/pets/${id}`);
  },

  // 创建宠物
  createPet: (data: CrmPet) => {
    return request.post("/crm/pets", data);
  },

  // 更新宠物
  updatePet: (id: number, data: CrmPet) => {
    return request.put(`/crm/pets/${id}`, data);
  },

  // 删除宠物
  deletePet: (id: number) => {
    return request.delete(`/crm/pets/${id}`);
  },
};
