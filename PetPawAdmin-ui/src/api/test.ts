// API测试文件 - 用于验证API调用的正确性
// 可以在浏览器控制台中运行这些测试

import {
  authApi,
  systemApi,
  crmApi,
  scmApi,
  appointmentApi,
  financeApi,
} from "./index";

// 测试认证API
export const testAuthApi = async () => {
  console.log("=== 测试认证API ===");

  try {
    // 测试登录
    console.log("测试登录...");
    const loginResult = await authApi.login({
      username: "admin",
      password: "123456",
    });
    console.log("登录结果:", loginResult);

    // 测试获取用户信息
    console.log("测试获取用户信息...");
    const userInfo = await authApi.getUserInfo();
    console.log("用户信息:", userInfo);
  } catch (error) {
    console.error("认证API测试失败:", error);
  }
};

// 测试系统管理API
export const testSystemApi = async () => {
  console.log("=== 测试系统管理API ===");

  try {
    // 测试获取用户列表
    console.log("测试获取用户列表...");
    const users = await systemApi.getUsers({
      current: 1,
      size: 5,
    });
    console.log("用户列表:", users);

    // 测试获取角色列表
    console.log("测试获取角色列表...");
    const roles = await systemApi.getRoles({
      current: 1,
      size: 5,
    });
    console.log("角色列表:", roles);
  } catch (error) {
    console.error("系统管理API测试失败:", error);
  }
};

// 测试CRM API
export const testCrmApi = async () => {
  console.log("=== 测试CRM API ===");

  try {
    // 测试获取客户列表
    console.log("测试获取客户列表...");
    const customers = await crmApi.getCustomers({
      current: 1,
      size: 5,
    });
    console.log("客户列表:", customers);

    // 测试获取宠物列表
    console.log("测试获取宠物列表...");
    const pets = await crmApi.getPets({
      current: 1,
      size: 5,
    });
    console.log("宠物列表:", pets);
  } catch (error) {
    console.error("CRM API测试失败:", error);
  }
};

// 测试供应链API
export const testScmApi = async () => {
  console.log("=== 测试供应链API ===");

  try {
    // 测试获取商品列表
    console.log("测试获取商品列表...");
    const products = await scmApi.getProducts({
      current: 1,
      size: 5,
    });
    console.log("商品列表:", products);

    // 测试获取供应商列表
    console.log("测试获取供应商列表...");
    const suppliers = await scmApi.getSuppliers({
      current: 1,
      size: 5,
    });
    console.log("供应商列表:", suppliers);
  } catch (error) {
    console.error("供应链API测试失败:", error);
  }
};

// 测试预约API
export const testAppointmentApi = async () => {
  console.log("=== 测试预约API ===");

  try {
    // 测试获取预约订单列表
    console.log("测试获取预约订单列表...");
    const orders = await appointmentApi.getOrders({
      current: 1,
      size: 5,
    });
    console.log("预约订单列表:", orders);

    // 测试获取服务项目列表
    console.log("测试获取服务项目列表...");
    const serviceItems = await appointmentApi.getServiceItems({
      current: 1,
      size: 5,
    });
    console.log("服务项目列表:", serviceItems);
  } catch (error) {
    console.error("预约API测试失败:", error);
  }
};

// 测试财务API
export const testFinanceApi = async () => {
  console.log("=== 测试财务API ===");

  try {
    // 测试获取财务订单列表
    console.log("测试获取财务订单列表...");
    const orders = await financeApi.getOrders({
      current: 1,
      size: 5,
    });
    console.log("财务订单列表:", orders);
  } catch (error) {
    console.error("财务API测试失败:", error);
  }
};

// 运行所有测试
export const runAllTests = async () => {
  console.log("开始运行API测试...");

  // 注意：需要先登录才能测试其他API
  await testAuthApi();

  // 等待一秒确保token设置完成
  await new Promise((resolve) => setTimeout(resolve, 1000));

  await testSystemApi();
  await testCrmApi();
  await testScmApi();
  await testAppointmentApi();
  await testFinanceApi();

  console.log("API测试完成");
};
