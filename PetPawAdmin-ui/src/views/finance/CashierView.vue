<template>
  <div class="cashier">
    <PageHeader title="收银台" description="快速处理宠物服务订单结算">
      <template #actions>
        <el-button @click="clearOrder">
          <el-icon><Delete /></el-icon>
          清空订单
        </el-button>
        <el-button @click="orderHistory">
          <el-icon><List /></el-icon>
          订单历史
        </el-button>
      </template>
    </PageHeader>

    <div class="cashier-content">
      <!-- 左侧商品和服务选择区域 -->
      <div class="left-panel">
        <!-- 商品分类 -->
        <div class="category-tabs">
          <el-tabs v-model="activeCategory" @tab-click="switchCategory">
            <el-tab-pane label="商品" name="products">
              <div class="product-grid">
                <div
                  v-for="product in filteredProducts"
                  :key="product.id"
                  class="product-item"
                  @click="addProductToOrder(product)"
                >
                  <div class="product-image">
                    <el-image
                      :src="product.image"
                      :alt="product.name"
                      fit="cover"
                      style="width: 80px; height: 80px; border-radius: 4px"
                    >
                      <template #error>
                        <div
                          class="image-slot"
                          style="
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            width: 100%;
                            height: 100%;
                            background: #f5f7fa;
                            color: #909399;
                          "
                        >
                          <el-icon size="24"><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>
                  </div>
                  <div class="product-info">
                    <h4>{{ product.name }}</h4>
                    <p class="product-spec">{{ product.specification }}</p>
                    <div class="product-price">
                      <span class="price">¥{{ product.price }}</span>
                      <span class="stock">库存: {{ product.stock }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="服务" name="services">
              <div class="service-grid">
                <div
                  v-for="service in serviceList"
                  :key="service.id"
                  class="service-item"
                  @click="addServiceToOrder(service)"
                >
                  <div class="service-icon">
                    <el-image
                      v-if="service.image"
                      :src="service.image"
                      :alt="service.name"
                      style="width: 60px; height: 60px; border-radius: 4px"
                      fit="cover"
                    >
                      <template #error>
                        <div
                          style="
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            width: 100%;
                            height: 100%;
                            background: #f5f7fa;
                          "
                        >
                          <el-icon size="24" color="#909399"
                            ><Picture
                          /></el-icon>
                        </div>
                      </template>
                    </el-image>
                    <el-icon v-else size="32"><MagicStick /></el-icon>
                  </div>
                  <div class="service-info">
                    <h4>{{ service.name }}</h4>
                    <p class="service-desc">{{ service.description }}</p>
                    <div class="service-price">
                      <span class="price">¥{{ service.price }}</span>
                      <span class="duration">{{ service.duration }}分钟</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <!-- 右侧订单结算区域 -->
      <div class="right-panel">
        <!-- 客户信息 -->
        <div class="customer-info">
          <div class="customer-header">
            <h3>客户信息</h3>
            <el-button type="primary" size="small" @click="selectCustomer">
              选择客户
            </el-button>
          </div>
          <div v-if="selectedCustomer" class="customer-details">
            <div class="customer-basic">
              <el-avatar :size="40" :src="selectedCustomer.avatar">
                {{ selectedCustomer.name.charAt(0) }}
              </el-avatar>
              <div class="customer-text">
                <div class="customer-name">{{ selectedCustomer.name }}</div>
                <div class="customer-phone">{{ selectedCustomer.phone }}</div>
              </div>
            </div>
            <div class="customer-stats">
              <div class="stat-item">
                <span class="label">会员等级：</span>
                <el-tag :type="getMemberLevelType(selectedCustomer.level)">
                  {{ getMemberLevelText(selectedCustomer.level) }}
                </el-tag>
              </div>
              <div class="stat-item">
                <span class="label">余额：</span>
                <span class="value">¥{{ selectedCustomer.balance }}</span>
              </div>
              <div class="stat-item">
                <span class="label">积分：</span>
                <span class="value">{{ selectedCustomer.points }}</span>
              </div>
            </div>
          </div>
          <div v-else class="no-customer">
            <el-empty description="未选择客户" :image-size="60">
              <el-button type="primary" @click="selectCustomer"
                >选择客户</el-button
              >
            </el-empty>
          </div>
        </div>

        <!-- 订单商品列表 -->
        <div class="order-items">
          <div class="order-header">
            <h3>订单商品</h3>
            <span class="item-count">{{ orderItems.length }} 项</span>
          </div>
          <div class="items-list">
            <div
              v-for="(item, index) in orderItems"
              :key="index"
              class="order-item"
            >
              <div class="item-info">
                <h4>{{ item.name }}</h4>
                <div class="item-details">
                  <span class="price">¥{{ item.price }}</span>
                  <span v-if="item.specification" class="spec">{{
                    item.specification
                  }}</span>
                </div>
              </div>
              <div class="item-controls">
                <el-input-number
                  v-model="item.quantity"
                  :min="1"
                  :max="item.stock || 999"
                  size="small"
                  @change="updateItemQuantity(index)"
                />
                <el-button
                  type="danger"
                  size="small"
                  icon="Delete"
                  @click="removeOrderItem(index)"
                />
              </div>
            </div>
          </div>
          <div v-if="orderItems.length === 0" class="empty-order">
            <el-empty description="请添加商品或服务" :image-size="60" />
          </div>
        </div>

        <!-- 订单金额 -->
        <div class="order-summary">
          <div class="summary-row">
            <span>商品金额：</span>
            <span>¥{{ orderSummary.subtotal.toFixed(2) }}</span>
          </div>
          <div v-if="orderSummary.discount > 0" class="summary-row discount">
            <span>优惠金额：</span>
            <span>-¥{{ orderSummary.discount.toFixed(2) }}</span>
          </div>
          <div
            v-if="selectedCustomer && selectedCustomer.level === 'vip'"
            class="summary-row"
          >
            <span>VIP折扣：</span>
            <span>-¥{{ orderSummary.vipDiscount.toFixed(2) }}</span>
          </div>
          <div class="summary-row total">
            <span>应收金额：</span>
            <span class="total-amount"
              >¥{{ orderSummary.total.toFixed(2) }}</span
            >
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="payment-section">
          <h3>支付方式</h3>
          <div class="payment-methods">
            <div
              v-for="method in paymentMethods"
              :key="method.value"
              class="payment-method"
              :class="{ active: selectedPaymentMethod === method.value }"
              @click="selectPaymentMethod(method.value)"
            >
              <el-icon :size="24"><component :is="method.icon" /></el-icon>
              <span>{{ method.label }}</span>
            </div>
          </div>

          <div v-if="selectedPaymentMethod === 'mixed'" class="mixed-payment">
            <div class="payment-input">
              <label>现金：</label>
              <el-input-number
                v-model="paymentAmount.cash"
                :precision="2"
                :min="0"
                :max="orderSummary.total"
                placeholder="输入现金金额"
              />
            </div>
            <div class="payment-input">
              <label>微信：</label>
              <el-input-number
                v-model="paymentAmount.wechat"
                :precision="2"
                :min="0"
                :max="orderSummary.total"
                placeholder="输入微信金额"
              />
            </div>
            <div class="payment-input">
              <label>支付宝：</label>
              <el-input-number
                v-model="paymentAmount.alipay"
                :precision="2"
                :min="0"
                :max="orderSummary.total"
                placeholder="输入支付宝金额"
              />
            </div>
            <div v-if="selectedCustomer" class="payment-input">
              <label>会员卡：</label>
              <el-input-number
                v-model="paymentAmount.memberCard"
                :precision="2"
                :min="0"
                :max="Math.min(orderSummary.total, selectedCustomer.balance)"
                placeholder="输入会员卡金额"
              />
            </div>
          </div>

          <div class="payment-actions">
            <el-button
              type="primary"
              size="large"
              :disabled="orderItems.length === 0"
              :loading="settling"
              @click="settleOrder"
            >
              <el-icon><Money /></el-icon>
              结算 (¥{{ orderSummary.total.toFixed(2) }})
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 客户选择对话框 -->
    <el-dialog
      v-model="customerDialogVisible"
      title="选择客户"
      width="800px"
      :close-on-click-modal="false"
    >
      <div class="customer-search">
        <el-input
          v-model="customerSearch"
          placeholder="输入客户姓名或手机号搜索"
          clearable
          @input="searchCustomers"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="customer-list">
        <div
          v-for="customer in searchedCustomers"
          :key="customer.id"
          class="customer-item"
          @click="selectCustomerFromList(customer)"
        >
          <el-avatar :size="40" :src="customer.avatar">
            {{ customer.name.charAt(0) }}
          </el-avatar>
          <div class="customer-info">
            <div class="customer-name">{{ customer.name }}</div>
            <div class="customer-phone">{{ customer.phone }}</div>
            <el-tag size="small" :type="getMemberLevelType(customer.level)">
              {{ getMemberLevelText(customer.level) }}
            </el-tag>
          </div>
          <div class="customer-balance">
            <div>余额: ¥{{ customer.balance }}</div>
            <div>积分: {{ customer.points }}</div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 历史订单对话框 -->
    <el-dialog v-model="orderHistoryVisible" title="订单历史" width="900px">
      <el-table
        v-loading="historyLoading"
        :data="historyOrders"
        style="width: 100%"
      >
        <el-table-column prop="orderNumber" label="订单号" width="180" />
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column prop="orderType" label="类型" width="100" />
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="{ row }"> ¥{{ row.totalAmount }} </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              @click="viewHistoryDetail(row)"
              >详情</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div
        class="pagination-container"
        style="margin-top: 20px; text-align: right"
      >
        <el-pagination
          v-model:current-page="historyPagination.pageNum"
          :page-size="historyPagination.pageSize"
          :total="historyPagination.total"
          layout="total, prev, pager, next"
          @current-change="handleHistoryPageChange"
        />
      </div>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="orderDetailVisible" title="订单详情" width="600px">
      <el-descriptions v-if="currentHistoryOrder" :column="2" border>
        <el-descriptions-item label="订单号">{{
          currentHistoryOrder.orderNumber
        }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          currentHistoryOrder.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{
          currentHistoryOrder.orderType
        }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">{{
          currentHistoryOrder.paymentStatus
        }}</el-descriptions-item>
        <el-descriptions-item label="总金额"
          >¥{{ currentHistoryOrder.totalAmount }}</el-descriptions-item
        >
        <el-descriptions-item label="实付金额"
          >¥{{ currentHistoryOrder.actualAmount }}</el-descriptions-item
        >
        <el-descriptions-item label="支付方式">{{
          currentHistoryOrder.paymentMethod
        }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{
          currentHistoryOrder.remarks || "无"
        }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Delete,
  List,
  Money,
  Search,
  MagicStick,
  Picture,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import { financeApi, scmApi, appointmentApi, crmApi } from "@/api";
import type { FinanceOrder } from "@/api/finance";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

// 商品接口
interface Product {
  id: number;
  name: string;
  specification: string;
  price: number;
  stock: number;
  image: string;
  category: string;
}

// 服务接口
interface Service {
  id: number;
  name: string;
  description: string;
  price: number;
  duration: number;
  image?: string;
}

// 客户接口
interface Customer {
  id: number;
  name: string;
  phone: string;
  level: "normal" | "vip" | "diamond";
  balance: number;
  points: number;
  avatar?: string;
}

// 订单项接口
interface OrderItem {
  id: number;
  name: string;
  specification?: string;
  price: number;
  quantity: number;
  stock?: number;
  type: "product" | "service";
}

// 响应式数据
const activeCategory = ref("products");
const customerDialogVisible = ref(false);
const orderHistoryVisible = ref(false);
const orderDetailVisible = ref(false);
const customerSearch = ref("");
const settling = ref(false);
const selectedPaymentMethod = ref("cash");

// 历史订单相关
const historyLoading = ref(false);
const historyOrders = ref<FinanceOrder[]>([]);
const currentHistoryOrder = ref<FinanceOrder | null>(null);
const historyPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});

// 客户相关
const selectedCustomer = ref<Customer | null>(null);
const searchedCustomers = ref<Customer[]>([]);

// 订单相关
const orderItems = ref<OrderItem[]>([]);

// 支付金额
const paymentAmount = reactive({
  cash: 0,
  wechat: 0,
  alipay: 0,
  memberCard: 0,
});

// 商品列表
const productList = ref<Product[]>([]);

// 服务列表
const serviceList = ref<Service[]>([]);

// 加载商品列表
const loadProducts = async () => {
  try {
    const res = await scmApi.getProducts({
      current: 1,
      size: 100,
      status: 1, // 上架商品
    });
    if (res.code === 200) {
      console.log("API Response Records:", res.data.records);
      productList.value = res.data.records.map((p: any) => ({
        id: p.id,
        name: p.productName,
        specification: p.unit, // 简单映射单位
        price: p.salePrice,
        stock: p.stockQuantity,
        image: p.imageUrl || "",
        category: "product",
      }));
      console.log("Mapped Product List:", productList.value);
    }
  } catch (error) {
    console.error("加载商品失败", error);
  }
};

// 加载服务列表
const loadServices = async () => {
  try {
    const res = await appointmentApi.getServiceItems({
      current: 1,
      size: 100,
      status: 1, // 启用服务
    });
    if (res.code === 200) {
      serviceList.value = res.data.records.map((s: any) => ({
        id: s.id,
        name: s.serviceName,
        description: s.description || "",
        price: s.price,
        duration: s.duration || 60,
        image: s.imageUrl || "",
      }));
    }
  } catch (error) {
    console.error("加载服务失败", error);
  }
};

// 支付方式
const paymentMethods = [
  { label: "现金", value: "cash", icon: "Money" },
  { label: "微信", value: "wechat", icon: "Wechat" },
  { label: "支付宝", value: "alipay", icon: "Alipay" },
  { label: "混合支付", value: "mixed", icon: "CreditCard" },
];

// 计算属性
const filteredProducts = computed(() => {
  // 这里可以根据分类筛选商品
  return productList.value;
});

const orderSummary = computed(() => {
  const subtotal = orderItems.value.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0,
  );
  const vipDiscount =
    selectedCustomer.value?.level === "vip" ? subtotal * 0.05 : 0;
  const discount = 0; // 其他优惠
  const total = subtotal - vipDiscount - discount;

  return {
    subtotal,
    vipDiscount,
    discount,
    total: Math.max(0, total),
  };
});

// 方法
const switchCategory = () => {
  // 切换分类时的处理
};

const addProductToOrder = (product: Product) => {
  const existingItem = orderItems.value.find(
    (item) => item.id === product.id && item.type === "product",
  );

  if (existingItem) {
    existingItem.quantity++;
  } else {
    orderItems.value.push({
      id: product.id,
      name: product.name,
      specification: product.specification,
      price: product.price,
      quantity: 1,
      stock: product.stock,
      type: "product",
    });
  }
};

const addServiceToOrder = (service: Service) => {
  const existingItem = orderItems.value.find(
    (item) => item.id === service.id && item.type === "service",
  );

  if (existingItem) {
    existingItem.quantity++;
  } else {
    orderItems.value.push({
      id: service.id,
      name: service.name,
      price: service.price,
      quantity: 1,
      type: "service",
    });
  }
};

const updateItemQuantity = (index: number) => {
  // 更新商品数量
};

const removeOrderItem = (index: number) => {
  orderItems.value.splice(index, 1);
};

const selectCustomer = () => {
  customerDialogVisible.value = true;
  searchCustomers();
};

const selectCustomerFromList = (customer: Customer) => {
  selectedCustomer.value = customer;
  customerDialogVisible.value = false;
};

const searchCustomers = async () => {
  if (customerSearch.value) {
    try {
      const res = await crmApi.getCustomers({
        current: 1,
        size: 10,
        customerName: customerSearch.value,
      });
      if (res.code === 200) {
        searchedCustomers.value = res.data.records.map((c: any) => ({
          id: c.id,
          name: c.customerName,
          phone: c.phone,
          level: (c.customerType as any) || "normal",
          balance: 0, // 暂时无余额字段
          points: 0, // 暂时无积分字段
          avatar: c.imageUrl,
        }));
      }
    } catch (error) {
      console.error("搜索客户失败", error);
    }
  } else {
    searchedCustomers.value = [];
  }
};

const selectPaymentMethod = (method: string) => {
  selectedPaymentMethod.value = method;
  if (method !== "mixed") {
    // 重置混合支付金额
    Object.keys(paymentAmount).forEach((key) => {
      paymentAmount[key] = 0;
    });
  }
};

const settleOrder = async () => {
  if (orderItems.value.length === 0) {
    ElMessage.warning("请先添加商品或服务");
    return;
  }

  // 验证支付金额
  if (selectedPaymentMethod.value === "mixed") {
    const totalPaid =
      paymentAmount.cash +
      paymentAmount.wechat +
      paymentAmount.alipay +
      paymentAmount.memberCard;
    if (totalPaid !== orderSummary.value.total) {
      ElMessage.error("支付金额与订单金额不匹配");
      return;
    }
  }

  try {
    await ElMessageBox.confirm(
      `确认结算订单？\n应收金额：¥${orderSummary.value.total.toFixed(2)}`,
      "确认结算",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    settling.value = true;

    // =================================================================================
    // 模拟支付过程
    // 说明：这里前端使用 setTimeout 模拟支付等待时间，
    // 实际上是直接调用后端的 quickCheckout 接口将数据存储到数据库。
    // 支付方式（微信/支付宝等）仅作为记录字段传给后端，不涉及真实支付网关调用。
    // 后端存储后，大屏和报表数据会自动更新。
    // =================================================================================

    // 模拟支付处理延迟 (1.5秒)
    const loadingMessage = ElMessage({
      message: "正在结算订单...",
      type: "warning",
      duration: 0,
      icon: "Loading",
    });

    await new Promise((resolve) => setTimeout(resolve, 1500));

    loadingMessage.close();

    // 调用快速收银API
    // 注意：这里将实际金额传给后端，确保财务数据准确
    const checkoutData = {
      orderNumber: `ORD${Date.now()}`, // 生成临时订单号
      customerId: selectedCustomer.value?.id || 1,
      orderType: "商品", // 暂时固定为商品，后续可根据订单内容判断
      totalAmount: orderSummary.value.total,
      discountAmount: orderSummary.value.discount,
      paymentAmount: orderSummary.value.total,
      paymentMethod: selectedPaymentMethod.value,
      paymentStatus: "paid", // 默认状态为已支付
      cashierId: userStore.user?.id || null, // 从用户store获取当前登录用户ID
      remarks: "收银台结算",
      orderDetails: orderItems.value.map((item) => ({
        itemType: item.type === "product" ? "商品" : "服务",
        itemId: item.id,
        itemName: item.name,
        quantity: item.quantity,
        unitPrice: item.price,
        totalPrice: item.price * item.quantity,
        discountAmount: 0, // 暂时无单品优惠
      })),
    };

    await financeApi.quickCheckout(checkoutData);

    ElMessage.success("订单结算成功！");

    // 清空订单
    clearOrder();
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("结算失败:", error);
      ElMessage.error(error.message || "结算失败，请重试");
    }
  } finally {
    settling.value = false;
  }
};

const clearOrder = () => {
  orderItems.value = [];
  selectedCustomer.value = null;
  selectedPaymentMethod.value = "cash";
  Object.keys(paymentAmount).forEach((key) => {
    paymentAmount[key] = 0;
  });
};

const orderHistory = () => {
  orderHistoryVisible.value = true;
  // 这里应该调用 API 获取历史订单，暂时使用空数据或 mock 数据
  // 实际开发中应该调用 financeApi.getOrders
  historyLoading.value = true;
  setTimeout(() => {
    // Mock data for demo
    historyOrders.value = [
      {
        id: 1,
        orderNumber: "ORD20231027001",
        customerId: 1,
        orderType: "商品",
        orderStatus: "completed",
        paymentStatus: "paid",
        totalAmount: 128.0,
        actualAmount: 128.0,
        paymentMethod: "wechat",
        createTime: "2023-10-27 10:30:00",
        remarks: "猫粮和罐头",
      },
      {
        id: 2,
        orderNumber: "ORD20231027002",
        customerId: 2,
        orderType: "服务",
        orderStatus: "completed",
        paymentStatus: "paid",
        totalAmount: 88.0,
        paymentAmount: 80.0,
        paymentMethod: "alipay",
        createTime: "2023-10-27 11:15:00",
        remarks: "洗澡套餐",
      },
    ];
    historyPagination.total = 2;
    historyLoading.value = false;
  }, 500);
};

const handleHistoryPageChange = (page: number) => {
  historyPagination.pageNum = page;
  // Reload history...
};

const viewHistoryDetail = (row: FinanceOrder) => {
  currentHistoryOrder.value = row;
  orderDetailVisible.value = true;
};

const getMemberLevelType = (level: string) => {
  switch (level) {
    case "diamond":
      return "danger";
    case "vip":
      return "warning";
    case "normal":
      return "info";
    default:
      return "info";
  }
};

const getMemberLevelText = (level: string) => {
  switch (level) {
    case "diamond":
      return "钻石会员";
    case "vip":
      return "VIP会员";
    case "normal":
      return "普通会员";
    default:
      return "普通会员";
  }
};

onMounted(() => {
  loadProducts();
  loadServices();
});
</script>

<style scoped lang="scss">
.cashier {
  .cashier-content {
    display: flex;
    gap: 20px;
    height: calc(100vh - 140px);

    .left-panel {
      flex: 2;
      background-color: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .category-tabs {
        height: 100%;

        :deep(.el-tabs__content) {
          height: calc(100% - 55px);
          overflow-y: auto;
        }

        .product-grid,
        .service-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
          gap: 16px;
          padding: 20px;

          .product-item,
          .service-item {
            background-color: #f5f5f5;
            border-radius: 8px;
            padding: 16px;
            cursor: pointer;
            transition: all 0.2s;
            border: 2px solid transparent;

            &:hover {
              border-color: #409eff;
              box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
            }

            .product-image {
              text-align: center;
              margin-bottom: 12px;

              img {
                width: 80px;
                height: 80px;
                object-fit: cover;
                border-radius: 4px;
              }
            }

            .product-info,
            .service-info {
              h4 {
                margin: 0 0 8px 0;
                font-size: 14px;
                font-weight: 500;
                color: #1d2129;
              }

              .product-spec,
              .service-desc {
                margin: 0 0 8px 0;
                font-size: 12px;
                color: #86909c;
              }

              .product-price,
              .service-price {
                display: flex;
                justify-content: space-between;
                align-items: center;

                .price {
                  font-size: 16px;
                  font-weight: 600;
                  color: #f56c6c;
                }

                .stock,
                .duration {
                  font-size: 12px;
                  color: #86909c;
                }
              }
            }

            .service-icon {
              text-align: center;
              margin-bottom: 12px;
              color: #409eff;
            }
          }
        }
      }
    }

    .right-panel {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 20px;

      .customer-info,
      .order-items,
      .order-summary,
      .payment-section {
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      }

      .customer-info {
        padding: 20px;

        .customer-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;

          h3 {
            margin: 0;
            font-size: 16px;
            font-weight: 600;
          }
        }

        .customer-details {
          .customer-basic {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 16px;

            .customer-text {
              .customer-name {
                font-weight: 500;
                color: #1d2129;
              }

              .customer-phone {
                font-size: 12px;
                color: #86909c;
              }
            }
          }

          .customer-stats {
            .stat-item {
              display: flex;
              justify-content: space-between;
              margin-bottom: 8px;

              .label {
                color: #86909c;
              }

              .value {
                font-weight: 500;
                color: #1d2129;
              }
            }
          }
        }

        .no-customer {
          text-align: center;
          padding: 20px 0;
        }
      }

      .order-items {
        flex: 1;
        padding: 20px;
        display: flex;
        flex-direction: column;

        .order-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;

          h3 {
            margin: 0;
            font-size: 16px;
            font-weight: 600;
          }

          .item-count {
            color: #86909c;
            font-size: 14px;
          }
        }

        .items-list {
          flex: 1;
          overflow-y: auto;

          .order-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;

            &:last-child {
              border-bottom: none;
            }

            .item-info {
              flex: 1;

              h4 {
                margin: 0 0 4px 0;
                font-size: 14px;
                font-weight: 500;
              }

              .item-details {
                display: flex;
                gap: 12px;

                .price {
                  color: #f56c6c;
                  font-weight: 500;
                }

                .spec {
                  color: #86909c;
                  font-size: 12px;
                }
              }
            }

            .item-controls {
              display: flex;
              align-items: center;
              gap: 8px;
            }
          }
        }

        .empty-order {
          flex: 1;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }

      .order-summary {
        padding: 20px;

        .summary-row {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;

          &.discount {
            color: #67c23a;
          }

          &.total {
            font-size: 18px;
            font-weight: 600;
            color: #1d2129;
            border-top: 1px solid #e6e6e6;
            padding-top: 12px;
            margin-top: 8px;

            .total-amount {
              color: #f56c6c;
            }
          }
        }
      }

      .payment-section {
        padding: 20px;

        h3 {
          margin: 0 0 16px 0;
          font-size: 16px;
          font-weight: 600;
        }

        .payment-methods {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 12px;
          margin-bottom: 20px;

          .payment-method {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 12px;
            border: 2px solid #e6e6e6;
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.2s;

            &:hover {
              border-color: #409eff;
            }

            &.active {
              border-color: #409eff;
              background-color: #ecf5ff;
            }
          }
        }

        .mixed-payment {
          margin-bottom: 20px;

          .payment-input {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 12px;

            label {
              min-width: 60px;
              color: #1d2129;
            }
          }
        }

        .payment-actions {
          .el-button {
            width: 100%;
          }
        }
      }
    }
  }

  .customer-search {
    margin-bottom: 20px;
  }

  .customer-list {
    max-height: 400px;
    overflow-y: auto;

    .customer-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      transition: background-color 0.2s;

      &:hover {
        background-color: #f5f5f5;
      }

      .customer-info {
        flex: 1;

        .customer-name {
          font-weight: 500;
          color: #1d2129;
          margin-bottom: 4px;
        }

        .customer-phone {
          color: #86909c;
          font-size: 12px;
          margin-bottom: 4px;
        }
      }

      .customer-balance {
        text-align: right;
        font-size: 12px;
        color: #86909c;
      }
    }
  }
}
</style>
