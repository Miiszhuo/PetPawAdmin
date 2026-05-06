import request from "@/utils/request";

export const dashboardApi = {
  getDashboardData() {
    return request({
      url: "/dashboard/data",
      method: "get",
    });
  },
};
