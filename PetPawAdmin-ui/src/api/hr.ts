import request from "@/utils/request";

// 职位相关接口
export interface HrPosition {
  id?: number;
  positionName: string;
  positionCode: string;
  description?: string;
  status: number;
  createTime?: string;
}

export interface PositionQueryParams {
  current: number;
  size: number;
  positionName?: string;
}

// 员工相关接口
export interface HrEmployee {
  id?: number;
  employeeNo: string;
  realName: string;
  gender: number;
  phone?: string;
  email?: string;
  positionId?: number;
  positionName?: string; // 列表查询时关联出来的
  entryDate?: string;
  sysUserId?: number;
  status: number;
  avatar?: string;
  createTime?: string;
}

export interface EmployeeQueryParams {
  current: number;
  size: number;
  realName?: string;
  employeeNo?: string;
  status?: number;
  positionId?: number;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

export const hrApi = {
  // ============= 职位管理 =============
  // 获取职位列表
  getPositions: (params: PositionQueryParams) => {
    return request.get<PageResult<HrPosition>>("/hr/positions", { params });
  },

  // 获取所有启用的职位列表（用于下拉选择）
  getAllPositions: () => {
    return request.get<HrPosition[]>("/hr/positions/list");
  },

  // 创建职位
  createPosition: (data: HrPosition) => {
    return request.post("/hr/positions", data);
  },

  // 更新职位
  updatePosition: (id: number, data: HrPosition) => {
    return request.put(`/hr/positions/${id}`, data);
  },

  // 删除职位
  deletePosition: (id: number) => {
    return request.delete(`/hr/positions/${id}`);
  },

  // ============= 员工管理 =============
  // 获取员工列表
  getEmployees: (params: EmployeeQueryParams) => {
    return request.get<PageResult<HrEmployee>>("/hr/employees", { params });
  },

  // 获取员工详情
  getEmployee: (id: number) => {
    return request.get<HrEmployee>(`/hr/employees/${id}`);
  },

  // 创建员工
  createEmployee: (data: HrEmployee) => {
    return request.post("/hr/employees", data);
  },

  // 更新员工
  updateEmployee: (id: number, data: HrEmployee) => {
    return request.put(`/hr/employees/${id}`, data);
  },

  // 删除员工
  deleteEmployee: (id: number) => {
    return request.delete(`/hr/employees/${id}`);
  },

  // 获取所有技师
  getTechnicians: () => {
    return request.get<HrEmployee[]>("/hr/employees/technicians");
  },
};
