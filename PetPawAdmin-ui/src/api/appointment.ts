import request from "@/utils/request";

// 预约订单管理相关接口
export interface AppointmentOrder {
  id?: number;
  orderNumber: string;
  customerId: number;
  petId?: number;
  serviceItemId: number;
  appointmentTime: string;
  status: string;
  remarks?: string;
  createTime?: string;
  updateTime?: string;
}

export interface OrderQueryParams {
  current: number;
  size: number;
  orderNumber?: string;
  customerId?: number;
  status?: string;
  customerName?: string;
  serviceType?: string;
  startDate?: string;
  endDate?: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

// 服务项目管理相关接口
export interface AppointmentServiceItem {
  id?: number;
  serviceName: string;
  serviceCode?: string;
  category: string;
  description?: string;
  price: number;
  duration?: number;
  standardDuration?: number;
  status: number;
  imageUrl?: string;
  createTime?: string;
  updateTime?: string;
}

export interface ServiceItemQueryParams {
  current: number;
  size: number;
  serviceName?: string;
  category?: string;
  status?: number;
  requiredSkill?: string;
}

export const appointmentApi = {
  // ============= 预约订单管理 =============
  // 获取预约订单列表
  getOrders: (params: OrderQueryParams) => {
    return request.get<PageResult<AppointmentOrder>>("/appointment/orders", {
      params,
    });
  },

  // 获取预约订单详情
  getOrder: (id: number) => {
    return request.get<AppointmentOrder>(`/appointment/orders/${id}`);
  },

  // 创建预约订单
  createOrder: (data: AppointmentOrder) => {
    return request.post("/appointment/orders", data);
  },

  // 更新预约订单
  updateOrder: (id: number, data: AppointmentOrder) => {
    return request.put(`/appointment/orders/${id}`, data);
  },

  // 更新订单状态
  updateOrderStatus: (id: number, status: string) => {
    return request.put(`/appointment/orders/${id}/status`, null, {
      params: { status },
    });
  },

  // 取消订单
  cancelOrder: (id: number) => {
    return request.put(`/appointment/orders/${id}/cancel`);
  },

  // 获取排班看板数据
  getScheduleBoard: (date: string) => {
    return request.get("/appointment/schedules/board", {
      params: { date },
    });
  },

  // ============= 服务项目管理 =============
  // 获取服务项目列表
  getServiceItems: (params: ServiceItemQueryParams) => {
    return request.get<PageResult<AppointmentServiceItem>>(
      "/appointment/service-items",
      { params },
    );
  },

  // 获取服务项目详情
  getServiceItem: (id: number) => {
    return request.get<AppointmentServiceItem>(
      `/appointment/service-items/${id}`,
    );
  },

  // 创建服务项目
  createServiceItem: (data: AppointmentServiceItem) => {
    return request.post("/appointment/service-items", data);
  },

  // 更新服务项目
  updateServiceItem: (id: number, data: AppointmentServiceItem) => {
    return request.put(`/appointment/service-items/${id}`, data);
  },

  // 删除服务项目
  deleteServiceItem: (id: number) => {
    return request.delete(`/appointment/service-items/${id}`);
  },
};
