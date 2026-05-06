<template>
  <div class="order-list">
    <PageHeader title="财务订单" description="查看和管理所有财务收支订单">
      <template #actions>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出订单
        </el-button>
      </template>
    </PageHeader>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="订单号">
          <el-input
            v-model="filterForm.orderNumber"
            placeholder="请输入订单号"
            clearable
            style="width: 160px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <DataTable
      :data="orderList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      @page-change="handlePageChange"
    >
      <template #column-totalAmount="{ row }">
        <span class="amount">¥{{ row.totalAmount }}</span>
      </template>

      <template #column-actualAmount="{ row }">
        <span class="amount actual">¥{{ row.actualAmount }}</span>
      </template>

      <template #column-paymentStatus="{ row }">
        <el-tag :type="getPaymentStatusType(row.paymentStatus)">
          {{ getPaymentStatusText(row.paymentStatus) }}
        </el-tag>
      </template>

      <template #column-orderType="{ row }">
        <el-tag
          effect="plain"
          :type="row.orderType === 'service' ? '' : 'info'"
        >
          {{ row.orderType === "service" ? "服务" : "商品" }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <el-button link type="primary" size="small" @click="viewDetail(row)"
          >详情</el-button
        >
      </template>
    </DataTable>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="600px"
      append-to-body
    >
      <el-descriptions v-if="currentOrder" :column="2" border>
        <el-descriptions-item label="订单号">{{
          currentOrder.orderNumber
        }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          currentOrder.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{
          currentOrder.orderType === "service" ? "服务" : "商品"
        }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag
            size="small"
            :type="getPaymentStatusType(currentOrder.paymentStatus)"
          >
            {{ getPaymentStatusText(currentOrder.paymentStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总金额"
          >¥{{ currentOrder.totalAmount }}</el-descriptions-item
        >
        <el-descriptions-item label="实付金额"
          >¥{{ currentOrder.paymentAmount }}</el-descriptions-item
        >
        <el-descriptions-item label="支付方式">{{
          currentOrder.paymentMethod || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="收银员ID">{{
          currentOrder.cashierId || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{
          currentOrder.remarks || "无"
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Search, Refresh, Download } from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import { financeApi } from "@/api";
import type { FinanceOrder, OrderQueryParams } from "@/api/finance";

// 状态
const loading = ref(false);
const orderList = ref<FinanceOrder[]>([]);
const total = ref(0);
const detailVisible = ref(false);
const currentOrder = ref<FinanceOrder | null>(null);

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

const filterForm = reactive({
  orderNumber: "",
  orderStatus: "",
});

// 表格列定义
const columns = [
  { prop: "orderNumber", label: "订单号", width: 180 },
  { prop: "orderType", label: "类型", width: 100 },
  { prop: "totalAmount", label: "总金额", width: 120 },
  { prop: "actualAmount", label: "实付金额", width: 120 },
  { prop: "paymentStatus", label: "支付状态", width: 100 },
  { prop: "paymentMethod", label: "支付方式", width: 120 },
  { prop: "createTime", label: "创建时间", width: 180 },
];

// 方法
const loadData = async () => {
  loading.value = true;
  try {
    const params: OrderQueryParams = {
      current: pagination.pageNum,
      size: pagination.pageSize,
      orderNumber: filterForm.orderNumber || undefined,
      orderStatus: filterForm.orderStatus || undefined,
    };
    const res = await financeApi.getOrders(params);
    if (res.code === 200) {
      orderList.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (error) {
    console.error("获取订单列表失败", error);
    ElMessage.error("获取订单列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.pageNum = 1;
  loadData();
};

const handleReset = () => {
  filterForm.orderNumber = "";
  filterForm.paymentStatus = "";
  handleSearch();
};

const handlePageChange = (page: number) => {
  pagination.pageNum = page;
  loadData();
};

const viewDetail = (row: FinanceOrder) => {
  currentOrder.value = row;
  detailVisible.value = true;
};

const handleExport = () => {
  ElMessage.info("导出功能开发中");
};

const getPaymentStatusType = (status: string) => {
  switch (status) {
    case "paid":
      return "success";
    case "unpaid":
      return "danger";
    case "refunded":
      return "info";
    default:
      return "warning";
  }
};

const getPaymentStatusText = (status: string) => {
  switch (status) {
    case "paid":
      return "已支付";
    case "unpaid":
      return "未支付";
    case "refunded":
      return "已退款";
    default:
      return status;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
.order-list {
  .filter-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  }

  .amount {
    font-family: Monaco, monospace;

    &.actual {
      color: #f56c6c;
      font-weight: bold;
    }
  }
}
</style>
