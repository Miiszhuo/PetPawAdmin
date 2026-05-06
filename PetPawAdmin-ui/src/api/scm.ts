import request from "@/utils/request";

// 商品管理相关接口
export interface ScmProduct {
  id?: number;
  productName: string;
  productCode?: string;
  category: string;
  description?: string;
  salePrice: number;
  purchasePrice?: number;
  stockQuantity: number;
  minStockQuantity?: number;
  unit: string;
  supplierId?: number;
  status: number;
  imageUrl?: string;
  createTime?: string;
  updateTime?: string;
}

export interface ProductQueryParams {
  current: number;
  size: number;
  productName?: string;
  category?: string;
  status?: number;
  supplierId?: number;
  stockStatus?: string;
  brand?: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

// 供应商管理相关接口
export interface ScmSupplier {
  id?: number;
  supplierName: string;
  contactPerson?: string;
  phone: string;
  email?: string;
  address?: string;
  level?: string;
  cooperationStatus: number;
  remarks?: string;
  createTime?: string;
  updateTime?: string;
}

export interface SupplierQueryParams {
  current: number;
  size: number;
  supplierName?: string;
  cooperationStatus?: number;
  contact?: string;
  level?: string;
}

export const scmApi = {
  // ============= 商品管理 =============
  // 获取商品列表
  getProducts: (params: ProductQueryParams) => {
    return request.get<PageResult<ScmProduct>>("/scm/products", { params });
  },

  // 获取商品详情
  getProduct: (id: number) => {
    return request.get<ScmProduct>(`/scm/products/${id}`);
  },

  // 创建商品
  createProduct: (data: ScmProduct) => {
    return request.post("/scm/products", data);
  },

  // 更新商品
  updateProduct: (id: number, data: ScmProduct) => {
    return request.put(`/scm/products/${id}`, data);
  },

  // 删除商品
  deleteProduct: (id: number) => {
    return request.delete(`/scm/products/${id}`);
  },

  // 更新库存
  updateStock: (id: number, quantity: number) => {
    return request.put(`/scm/products/${id}/stock`, null, {
      params: { quantity },
    });
  },

  // ============= 供应商管理 =============
  // 获取供应商列表
  getSuppliers: (params: SupplierQueryParams) => {
    return request.get<PageResult<ScmSupplier>>("/scm/suppliers", { params });
  },

  // 获取供应商详情
  getSupplier: (id: number) => {
    return request.get<ScmSupplier>(`/scm/suppliers/${id}`);
  },

  // 创建供应商
  createSupplier: (data: ScmSupplier) => {
    return request.post("/scm/suppliers", data);
  },

  // 更新供应商
  updateSupplier: (id: number, data: ScmSupplier) => {
    return request.put(`/scm/suppliers/${id}`, data);
  },

  // 删除供应商
  deleteSupplier: (id: number) => {
    return request.delete(`/scm/suppliers/${id}`);
  },

  // ============= 库存管理 =============
  // 获取库存记录列表
  getInventoryRecords: (params: InventoryRecordQueryParams) => {
    return request.get<PageResult<ScmInventoryRecord>>(
      "/scm/inventory/records",
      { params },
    );
  },

  // 执行入库操作
  inboundStock: (params: URLSearchParams) => {
    return request.post("/scm/inventory/inbound", params, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });
  },

  // 执行出库操作
  outboundStock: (params: URLSearchParams) => {
    return request.post("/scm/inventory/outbound", params, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });
  },

  // 执行库存调整
  adjustStock: (params: URLSearchParams) => {
    return request.post("/scm/inventory/adjust", params, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });
  },

  // 执行库存盘点
  inventoryCheck: (params: URLSearchParams) => {
    return request.post("/scm/inventory/stocktaking", params, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });
  },

  // 获取低库存商品列表
  getLowStockProducts: (params?: any) => {
    return request.get<PageResult<ScmProduct>>(
      "/scm/inventory/low-stock-products",
      { params },
    );
  },
};

// 库存记录相关接口
export interface ScmInventoryRecord {
  id?: number;
  productId: number;
  recordType: string;
  quantity: number;
  beforeQuantity: number;
  afterQuantity: number;
  supplierId?: number;
  orderId?: number;
  remark?: string;
  createBy?: string;
  createTime?: string;
}

export interface InventoryRecordQueryParams {
  current: number;
  size: number;
  productId?: number;
  recordType?: string;
  supplierId?: number;
}
