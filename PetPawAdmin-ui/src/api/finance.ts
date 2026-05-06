import request from "@/utils/request";

// 财务订单管理相关接口
export interface FinanceOrder {
  id?: number;
  orderNumber: string;
  customerId: number;
  petId?: number;
  orderType: string;
  orderStatus: string;
  paymentStatus: string;
  totalAmount: number;
  discountAmount?: number;
  paymentAmount: number;
  paymentMethod?: string;
  cashierId?: number;
  remarks?: string;
  createTime?: string;
  updateTime?: string;
}

export interface OrderQueryParams {
  current: number;
  size: number;
  orderNumber?: string;
  customerId?: number;
  orderStatus?: string;
  paymentStatus?: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

// 会员管理相关接口
export interface FinanceMember {
  id?: number;
  customerId: number;
  memberLevel: string;
  points: number;
  balance: number;
  status: number;
  joinTime?: string;
  expireTime?: string;
  createTime?: string;
  updateTime?: string;
}

export interface MemberQueryParams {
  current: number;
  size: number;
  customerId?: number;
  memberLevel?: string;
  status?: number;
}

export interface MemberQueryParams {
  current: number;
  size: number;
  customerId?: number;
  memberLevel?: string;
  status?: number;
}

export const financeApi = {
  // ============= 财务订单管理 =============
  // 获取订单列表
  getOrders: (params: OrderQueryParams) => {
    return request.get<PageResult<FinanceOrder>>("/finance/orders", { params });
  },

  // 获取订单详情
  getOrder: (id: number) => {
    return request.get<FinanceOrder>(`/finance/orders/${id}`);
  },

  // 创建订单
  createOrder: (data: FinanceOrder) => {
    return request.post("/finance/orders", data);
  },

  // 更新订单
  updateOrder: (id: number, data: FinanceOrder) => {
    return request.put(`/finance/orders/${id}`, data);
  },

  // 更新订单状态
  updateOrderStatus: (
    id: number,
    orderStatus: string,
    paymentStatus: string,
  ) => {
    return request.put(`/finance/orders/${id}/status`, null, {
      params: { orderStatus, paymentStatus },
    });
  },

  // 订单支付
  payOrder: (id: number, paymentMethod: string, cashierId: number) => {
    return request.put(`/finance/orders/${id}/pay`, null, {
      params: { paymentMethod, cashierId },
    });
  },

  // ============= 会员管理 =============
  // 获取会员卡列表
  getMemberList: (params: MemberQueryParams) => {
    return request.get<PageResult<FinanceMember>>("/finance/members/cards", {
      params,
    });
  },

  // 获取会员卡详情
  getMember: (id: number) => {
    return request.get<FinanceMember>(`/finance/members/cards/${id}`);
  },

  // 创建会员卡
  createMember: (data: FinanceMember) => {
    return request.post("/finance/members/cards", data);
  },

  // 更新会员卡
  updateMember: (id: number, data: FinanceMember) => {
    return request.put(`/finance/members/cards/${id}`, data);
  },

  // 删除会员卡
  deleteMember: (id: number) => {
    return request.delete(`/finance/members/cards/${id}`);
  },

  // 会员充值
  rechargeMember: (
    memberId: number,
    data: {
      rechargeAmount: number;
      giftAmount?: number;
      paymentMethod: string;
      operatorId: number;
      remark?: string;
    },
  ) => {
    // 构造查询参数
    const params = new URLSearchParams();
    params.append("amount", data.rechargeAmount.toString());
    params.append("paymentMethod", data.paymentMethod);
    params.append("cashierId", data.operatorId.toString());

    return request.post(`/finance/members/cards/${memberId}/recharge`, null, {
      params,
    });
  },

  // 会员消费扣费
  consumeMember: (
    memberId: number,
    data: { consumeAmount: number; remark?: string },
  ) => {
    const params = new URLSearchParams();
    params.append("amount", data.consumeAmount.toString());
    params.append("cashierId", "1"); // 默认收银员ID
    return request.post(`/finance/members/cards/${memberId}/consume`, null, {
      params,
    });
  },

  // 变更会员积分
  changeMemberPoints: (
    memberId: number,
    pointsChange: number,
    remark?: string,
  ) => {
    const params = new URLSearchParams();
    params.append("points", pointsChange.toString());
    params.append("reason", remark || "");
    return request.post(`/finance/members/cards/${memberId}/points`, null, {
      params,
    });
  },

  // 启用/禁用会员 (激活/注销)
  changeMemberStatus: (memberId: number, status: number) => {
    if (status === 1) {
      return request.post(`/finance/members/cards/${memberId}/activate`);
    } else {
      const params = new URLSearchParams();
      params.append("reason", "管理员操作");
      return request.post(
        `/finance/members/cards/${memberId}/deactivate`,
        null,
        { params },
      );
    }
  },

  // 获取会员充值记录
  getMemberRechargeRecords: (
    memberId: number,
    params: { current: number; size: number },
  ) => {
    return request.get(`/finance/members/cards/${memberId}/recharge-records`, {
      params,
    });
  },

  // ============= 收银管理 =============
  // 获取今日营业概览
  getTodayOverview: () => {
    return request.get("/finance/cashier/overview/today");
  },

  // 快速收银
  quickCheckout: (data: any) => {
    // 暂时指向订单创建接口，实际应为专用收银接口
    return request.post("/finance/orders", data);
  },

  // 订单退款
  refundOrder: (
    orderId: number,
    refundAmount: number,
    refundReason: string,
    cashierId: number,
  ) => {
    return request.post(`/finance/cashier/${orderId}/refund`, null, {
      params: { refundAmount, refundReason, cashierId },
    });
  },

  // 挂单
  suspendOrder: (data: any) => {
    return request.post("/finance/cashier/suspend-order", data);
  },

  // 恢复并支付挂单
  resumeAndPayOrder: (
    orderId: number,
    paymentMethod: string,
    cashierId: number,
  ) => {
    return request.put(`/finance/cashier/${orderId}/resume-and-pay`, null, {
      params: { paymentMethod, cashierId },
    });
  },

  // 取消挂单
  cancelSuspendedOrder: (orderId: number, cancelReason: string) => {
    return request.put(`/finance/cashier/${orderId}/cancel-suspended`, null, {
      params: { cancelReason },
    });
  },

  // 获取财务报表统计数据
  getReportStats: (params: { startDate: string; endDate: string }) => {
    return request.get("/finance/reports/stats", { params });
  },

  // 获取营收趋势数据
  getRevenueTrend: (params: {
    startDate: string;
    endDate: string;
    type: string;
  }) => {
    return request.get("/finance/reports/revenue-trend", { params });
  },

  // 获取收入构成数据
  getIncomeComposition: (params: { startDate: string; endDate: string }) => {
    return request.get("/finance/reports/income-composition", { params });
  },

  // 获取会员等级消费统计
  getMemberConsumptionStats: (params: {
    startDate: string;
    endDate: string;
  }) => {
    return request.get("/finance/reports/member-consumption", { params });
  },

  // 获取商品销售排行
  getProductSalesRanking: (params: { startDate: string; endDate: string }) => {
    return request.get("/finance/reports/product-sales-ranking", { params });
  },

  // 获取日营收明细列表
  getDailyRevenueList: (params: { startDate: string; endDate: string }) => {
    return request.get("/finance/reports/daily-revenue-list", { params });
  },
};
