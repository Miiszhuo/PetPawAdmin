<template>
  <div class="order-list">
    <PageHeader title="工单管理" description="管理服务工单的全生命周期">
      <template #actions>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建工单
        </el-button>
      </template>
    </PageHeader>

    <!-- 工单概览 -->
    <div class="order-overview">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="4">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#409eff"><DocumentAdd /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.totalOrders }}</div>
              <div class="card-label">总工单数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#e6a23c"><Clock /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.pendingOrders }}</div>
              <div class="card-label">待处理</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#67c23a"><Check /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.completedToday }}</div>
              <div class="card-label">今日完成</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#f56c6c"><Close /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.cancelledOrders }}</div>
              <div class="card-label">已取消</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#e6a23c"><Star /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.avgRating }}</div>
              <div class="card-label">平均评分</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#909399"><Money /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">¥{{ overview.todayRevenue }}</div>
              <div class="card-label">今日营收</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="工单号">
          <el-input
            v-model="filterForm.orderNo"
            placeholder="请输入工单号"
            clearable
            style="width: 140px"
          />
        </el-form-item>

        <el-form-item label="客户姓名">
          <el-input
            v-model="filterForm.customerName"
            placeholder="请输入客户姓名"
            clearable
            style="width: 120px"
          />
        </el-form-item>

        <el-form-item label="服务项目">
          <el-select
            v-model="filterForm.serviceType"
            placeholder="请选择服务项目"
            clearable
            style="width: 130px"
          >
            <el-option label="宠物洗澡" value="wash" />
            <el-option label="宠物美容" value="grooming" />
            <el-option label="疫苗接种" value="vaccination" />
            <el-option label="健康体检" value="checkup" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="工单状态">
          <el-select
            v-model="filterForm.status"
            placeholder="请选择工单状态"
            clearable
            style="width: 120px"
          >
            <el-option label="待确认" value="待确认" />
            <el-option label="已预约" value="已预约" />
            <el-option label="已确认" value="已确认" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
            <el-option label="已挂单" value="已挂单" />
          </el-select>
        </el-form-item>

        <el-form-item label="服务日期">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
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

    <!-- 工单表格 -->
    <DataTable
      :data="orderList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      show-selection
      show-actions
      @selection-change="handleSelectionChange"
      @page-change="handlePageChange"
    >
      <template #column-serviceType="{ row }">
        <el-tag size="small" :type="getServiceTypeColor(row.serviceType)">
          {{ getServiceTypeText(row.serviceType) }}
        </el-tag>
      </template>

      <template #column-serviceName="{ row }">
        <div class="service-info">
          <div class="service-name">{{ row.serviceName }}</div>
          <div class="service-category">
            <el-tag size="mini" :type="getServiceTypeColor(row.serviceType)">
              {{ getServiceTypeText(row.serviceType) }}
            </el-tag>
          </div>
        </div>
      </template>

      <template #column-status="{ row }">
        <el-tag :type="getStatusType(row.status)" size="small">
          {{ row.status }}
        </el-tag>
      </template>

      <template #column-amount="{ row }">
        <span class="amount">¥{{ row.amount }}</span>
      </template>

      <template #column-rating="{ row }">
        <div v-if="row.rating" class="rating">
          <el-rate
            v-model="row.rating"
            disabled
            show-score
            text-color="#ff9900"
            size="small"
          />
        </div>
        <span v-else class="no-rating">-</span>
      </template>

      <template #column-actions="{ row }">
        <el-button size="small" @click="viewOrderDetail(row)">详情</el-button>
        <el-button
          v-if="row.status === '已确认'"
          size="small"
          type="primary"
          @click="startService(row)"
        >
          开始服务
        </el-button>
        <el-button
          v-if="row.status === '进行中'"
          size="small"
          type="success"
          @click="completeService(row)"
        >
          完成服务
        </el-button>
        <el-dropdown
          v-if="!['已完成', '已取消'].includes(row.status)"
          @command="(command) => handleAction(command, row)"
        >
          <el-button size="small">
            更多操作
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-if="['待确认', '已预约'].includes(row.status)"
                command="confirm"
              >
                确定
              </el-dropdown-item>
              <el-dropdown-item
                v-if="['待确认', '已预约'].includes(row.status)"
                command="suspend"
              >
                挂单
              </el-dropdown-item>
              <el-dropdown-item v-if="row.status === '已挂单'" command="resume">
                恢复工单
              </el-dropdown-item>
              <el-dropdown-item
                v-if="!['已完成', '已取消'].includes(row.status)"
                command="cancel"
                divided
              >
                取消工单
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </DataTable>

    <!-- 新建工单对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="新建工单"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="100px"
      >
        <el-form-item label="客户" prop="customerId">
          <el-select
            v-model="createForm.customerId"
            placeholder="请选择客户"
            filterable
            remote
            :remote-method="searchCustomers"
            :loading="customerLoading"
            style="width: 100%"
            @change="handleCustomerChange"
          >
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="item.customerName + ' (' + item.phone + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="宠物" prop="petId">
          <el-select
            v-model="createForm.petId"
            placeholder="请选择宠物"
            style="width: 100%"
            :disabled="!createForm.customerId"
          >
            <el-option
              v-for="item in petOptions"
              :key="item.id"
              :label="item.petName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="服务项目" prop="serviceItemId">
          <el-select
            v-model="createForm.serviceItemId"
            placeholder="请选择服务项目"
            style="width: 100%"
            @change="handleServiceChange"
          >
            <el-option
              v-for="item in serviceOptions"
              :key="item.id"
              :label="item.serviceName + ' (¥' + item.price + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="服务人员" prop="staffId">
          <el-select
            v-model="createForm.staffId"
            placeholder="请选择服务人员"
            style="width: 100%"
          >
            <el-option
              v-for="item in staffOptions"
              :key="item.id"
              :label="item.realName || item.employeeNo"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="预约日期" prop="appointmentDate">
          <el-date-picker
            v-model="createForm.appointmentDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="预约时间" prop="appointmentTime">
          <el-time-picker
            v-model="createForm.appointmentTime"
            placeholder="选择时间"
            format="HH:mm"
            value-format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="时长(分钟)" prop="duration">
          <el-input-number v-model="createForm.duration" :min="15" :step="15" />
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="createForm.price"
            :min="0"
            :precision="2"
            :step="10"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="createForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="createLoading"
            @click="submitCreate"
            >确定</el-button
          >
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="工单详情"
      width="600px"
      destroy-on-close
    >
      <el-descriptions
        v-if="currentOrder"
        v-loading="detailLoading"
        :column="2"
        border
      >
        <el-descriptions-item label="工单号">{{
          currentOrder.orderNumber
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)" size="small">
            {{ currentOrder.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户">{{
          currentOrder.customerName
        }}</el-descriptions-item>
        <el-descriptions-item label="宠物">{{
          currentOrder.petName
        }}</el-descriptions-item>
        <el-descriptions-item label="服务项目">{{
          currentOrder.serviceName
        }}</el-descriptions-item>
        <el-descriptions-item label="服务人员">{{
          currentOrder.staffName
        }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{
          currentOrder.appointmentDate
        }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{
          currentOrder.appointmentTime
        }}</el-descriptions-item>
        <el-descriptions-item label="时长"
          >{{ currentOrder.duration }}分钟</el-descriptions-item
        >
        <el-descriptions-item label="金额"
          >¥{{ currentOrder.totalAmount }}</el-descriptions-item
        >
        <el-descriptions-item label="创建时间" :span="2">{{
          currentOrder.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{
          currentOrder.remark || "无"
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance } from "element-plus";
import {
  Plus,
  Download,
  Search,
  Refresh,
  ArrowDown,
  DocumentAdd,
  Clock,
  Check,
  Close,
  Star,
  Money,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import { appointmentApi, crmApi, systemApi, hrApi } from "@/api";
import type { PageParams } from "@/types/common";
import type {
  AppointmentOrder,
  OrderQueryParams,
  AppointmentServiceItem,
} from "@/api/appointment";
import type { CrmCustomer, CrmPet } from "@/api/crm";
import type { SysUser } from "@/api/system";
import type { HrEmployee } from "@/api/hr";

// 工单接口（后端数据结构）
interface ExtendedAppointmentOrder extends AppointmentOrder {
  customerName?: string;
  petName?: string;
  serviceName?: string;
  staffName?: string;
  serviceType?: string;
  amount?: number;
  rating?: number;
}

interface OrderListResponse {
  code: number;
  total?: number;
  records?: ExtendedAppointmentOrder[];
  data?: {
    records?: ExtendedAppointmentOrder[];
    total?: number;
  };
  message?: string;
}

// 前端显示接口
interface Order {
  id: number;
  orderNo: string;
  customerName: string;
  customerPhone: string;
  petName: string;
  petBreed: string;
  serviceType: string;
  serviceName: string;
  staffName: string;
  appointmentTime: string;
  status: string;
  amount: number;
  rating?: number;
  createdTime: string;
  updatedTime: string;
}

// 表格列配置
const columns = [
  { prop: "orderNo", label: "工单号", width: 150 },
  { prop: "customerName", label: "客户姓名", width: 100 },
  { prop: "petName", label: "宠物名称", width: 100 },
  { prop: "serviceType", label: "服务类型", width: 100 },
  { prop: "serviceName", label: "服务项目", width: 120 },
  { prop: "staffName", label: "服务人员", width: 100 },
  { prop: "appointmentTime", label: "预约时间", width: 160 },
  { prop: "status", label: "工单状态", width: 100 },
  { prop: "amount", label: "金额", width: 80 },
  { prop: "rating", label: "评分", width: 120 },
];

// 响应式数据
const loading = ref(false);

// 工单概览
const overview = reactive({
  totalOrders: 0,
  pendingOrders: 0,
  completedToday: 0,
  cancelledOrders: 0,
  avgRating: 0,
  todayRevenue: 0,
});

// 工单列表
const orderList = ref<Order[]>([]);
const total = ref(0);
const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

// 筛选表单
const filterForm = reactive({
  orderNo: "",
  customerName: "",
  serviceType: "",
  status: "",
  dateRange: [] as [string, string] | [],
});

// 新建工单相关
const createDialogVisible = ref(false);
const createLoading = ref(false);
const createFormRef = ref<FormInstance>();
const customerLoading = ref(false);
const customerOptions = ref<CrmCustomer[]>([]);
const petOptions = ref<CrmPet[]>([]);
const serviceOptions = ref<AppointmentServiceItem[]>([]);
const staffOptions = ref<HrEmployee[]>([]);

const createForm = reactive({
  customerId: undefined as number | undefined,
  petId: undefined as number | undefined,
  serviceItemId: undefined as number | undefined,
  staffId: undefined as number | undefined,
  appointmentDate: "",
  appointmentTime: "",
  duration: 60,
  price: 0,
  remark: "",
});

const createRules = {
  customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
  petId: [{ required: true, message: "请选择宠物", trigger: "change" }],
  serviceItemId: [
    { required: true, message: "请选择服务项目", trigger: "change" },
  ],
  staffId: [{ required: true, message: "请选择服务人员", trigger: "change" }],
  appointmentDate: [
    { required: true, message: "请选择日期", trigger: "change" },
  ],
  appointmentTime: [
    { required: true, message: "请选择时间", trigger: "change" },
  ],
  duration: [{ required: true, message: "请输入时长", trigger: "blur" }],
  price: [{ required: true, message: "请输入价格", trigger: "blur" }],
};

// 方法
const loadOrderList = async () => {
  loading.value = true;
  try {
    const params: OrderQueryParams = {
      current: pagination.pageNum,
      size: pagination.pageSize,
      orderNumber: filterForm.orderNo || undefined,
      status: filterForm.status || undefined,
      customerName: filterForm.customerName || undefined,
      serviceType: filterForm.serviceType || undefined,
      startDate: filterForm.dateRange?.[0] || undefined,
      endDate: filterForm.dateRange?.[1] || undefined,
    };

    // 1. 发起请求
    const response = await appointmentApi.getOrders<OrderListResponse>(params);

    // 2. 兼容两种响应格式
    let records: ExtendedAppointmentOrder[] = [];
    let totalCount = 0;

    if (Array.isArray(response.records)) {
      records = response.records;
      totalCount = response.total || 0;
    } else if (response.data?.data?.records) {
      records = response.data.data.records;
      totalCount = response.data.data.total || 0;
    } else if (response.data?.records) {
      records = response.data.records;
      totalCount = response.data.total || 0;
    }

    total.value = totalCount;

    // 3. 正确的字段映射
    orderList.value = records.map((order) => ({
      id: order.id || 0,
      orderNo: order.orderNumber || "",
      customerName: order.customerName || "客户" + (order.customerId || ""),
      customerPhone: "",
      petName: order.petName || (order.petId ? "宠物" + order.petId : ""),
      petBreed: "",
      serviceType: order.serviceType || "other",
      serviceName:
        order.serviceName || "服务项目" + (order.serviceItemId || ""),
      staffName: order.staffName || "",
      appointmentTime:
        (order.appointmentDate || "") + " " + (order.appointmentTime || ""),
      status: order.status || "pending",
      amount: order.totalAmount || 0,
      rating: undefined,
      createdTime: order.createTime || "",
      updatedTime: order.updateTime || "",
    }));

    calculateOverview();
  } catch (error) {
    console.error("加载工单列表失败:", error);
    ElMessage.error("加载工单列表失败");
    orderList.value = [];
  } finally {
    loading.value = false;
  }
};

const calculateOverview = () => {
  overview.totalOrders = orderList.value.length;
  overview.pendingOrders = orderList.value.filter(
    (order) => order.status === "待确认",
  ).length;
  overview.completedToday = orderList.value.filter(
    (order) =>
      order.status === "已完成" &&
      order.updatedTime.startsWith(new Date().toISOString().split("T")[0]),
  ).length;
  overview.cancelledOrders = orderList.value.filter(
    (order) => order.status === "已取消",
  ).length;

  // 简单计算平均评分（如果有评分字段）
  overview.avgRating = 0;
  overview.todayRevenue = orderList.value
    .filter((order) => order.status === "已完成")
    .reduce((sum, order) => sum + (order.amount || 0), 0);
};

const handleSearch = () => {
  pagination.pageNum = 1;
  loadOrderList();
};

const handleReset = () => {
  Object.keys(filterForm).forEach((key) => {
    filterForm[key] = "";
  });
  filterForm.dateRange = [];
  pagination.pageNum = 1;
  loadOrderList();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadOrderList();
};

const handleSelectionChange = (selection: Order[]) => {
  console.log("选中的工单:", selection);
};

// 新建工单逻辑
const handleCreate = async () => {
  createDialogVisible.value = true;
  // 加载基础数据
  await Promise.all([searchCustomers(""), fetchServices(), fetchStaffs()]);
};

const searchCustomers = async (query: string) => {
  customerLoading.value = true;
  try {
    const res: any = await crmApi.getCustomers({
      current: 1,
      size: 50,
      customerName: query || undefined,
    });

    // 兼容多种响应结构
    if (Array.isArray(res.records)) {
      customerOptions.value = res.records;
    } else if (Array.isArray(res.data?.records)) {
      customerOptions.value = res.data.records;
    } else if (Array.isArray(res.data?.data?.records)) {
      customerOptions.value = res.data.data.records;
    } else {
      customerOptions.value = [];
    }
  } catch (e) {
    console.error("搜索客户失败", e);
    customerOptions.value = [];
  } finally {
    customerLoading.value = false;
  }
};

const fetchServices = async () => {
  try {
    const res: any = await appointmentApi.getServiceItems({
      current: 1,
      size: 100,
      status: 1,
    });

    let records: AppointmentServiceItem[] = [];
    if (Array.isArray(res.records)) {
      records = res.records;
    } else if (Array.isArray(res.data?.records)) {
      records = res.data.records;
    } else if (Array.isArray(res.data?.data?.records)) {
      records = res.data.data.records;
    }

    serviceOptions.value = records;
  } catch (e) {
    console.error("获取服务项目失败", e);
    serviceOptions.value = [];
  }
};

const fetchStaffs = async () => {
  try {
    const res: any = await hrApi.getEmployees({
      current: 1,
      size: 100,
      status: 1,
    });

    if (Array.isArray(res.records)) {
      staffOptions.value = res.records;
    } else if (Array.isArray(res.data?.records)) {
      staffOptions.value = res.data.records;
    } else if (Array.isArray(res.data?.data?.records)) {
      staffOptions.value = res.data.data.records;
    } else {
      staffOptions.value = [];
    }
  } catch (e) {
    console.error("获取服务人员失败", e);
    staffOptions.value = [];
  }
};

const handleCustomerChange = async (customerId: number) => {
  createForm.petId = undefined;
  if (!customerId) {
    petOptions.value = [];
    return;
  }
  try {
    const res: any = await crmApi.getPets({ current: 1, size: 50, customerId });

    if (Array.isArray(res.records)) {
      petOptions.value = res.records;
    } else if (Array.isArray(res.data?.records)) {
      petOptions.value = res.data.records;
    } else if (Array.isArray(res.data?.data?.records)) {
      petOptions.value = res.data.data.records;
    } else {
      petOptions.value = [];
    }
  } catch (e) {
    console.error("获取宠物列表失败", e);
    petOptions.value = [];
  }
};

const handleServiceChange = (serviceId: number) => {
  const service = serviceOptions.value.find((s) => s.id === serviceId);
  if (service) {
    createForm.price = service.price;
    // 优先使用 standardDuration，兼容 duration
    createForm.duration = service.standardDuration || service.duration || 60;
  }
};

const submitCreate = async () => {
  if (!createFormRef.value) return;

  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      createLoading.value = true;
      try {
        await appointmentApi.createOrder({
          customerId: createForm.customerId!,
          petId: createForm.petId!,
          serviceItemId: createForm.serviceItemId!,
          staffId: createForm.staffId!,
          appointmentDate: createForm.appointmentDate,
          appointmentTime: createForm.appointmentTime + ":00", // 补全秒
          duration: createForm.duration,
          price: createForm.price,
          totalAmount: createForm.price, // 简单处理
          orderNumber: "", // 后端生成
          status: "待确认",
        });
        ElMessage.success("工单创建成功");
        createDialogVisible.value = false;
        loadOrderList();
      } catch (error: any) {
        ElMessage.error(error.message || "创建失败");
      } finally {
        createLoading.value = false;
      }
    }
  });
};

const startService = async (order: Order) => {
  try {
    await ElMessageBox.confirm(
      `确定开始为 ${order.customerName} 的 ${order.petName} 提供 ${order.serviceName} 服务吗？`,
      "确认开始服务",
      { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" },
    );
    await appointmentApi.updateOrderStatus(order.id, "进行中");
    ElMessage.success("服务已开始");
    loadOrderList();
  } catch (error) {
    void error;
  }
};

const completeService = async (order: Order) => {
  try {
    await ElMessageBox.confirm(
      `确定完成 ${order.customerName} 的 ${order.petName} 的 ${order.serviceName} 服务吗？`,
      "确认完成服务",
      { confirmButtonText: "确定", cancelButtonText: "取消", type: "success" },
    );
    await appointmentApi.updateOrderStatus(order.id, "已完成");
    ElMessage.success("服务已完成");
    loadOrderList();
  } catch (error) {
    void error;
  }
};

const handleAction = async (command: string, order: Order) => {
  switch (command) {
    case "confirm":
      try {
        await ElMessageBox.confirm("确定客户已到店并确认服务吗？", "确认服务", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "primary",
        });
        await appointmentApi.updateOrderStatus(order.id, "已确认");
        ElMessage.success("工单已确认");
        loadOrderList();
      } catch (error) {
        void error;
      }
      break;
    case "suspend":
      try {
        await appointmentApi.updateOrderStatus(order.id, "已挂单");
        ElMessage.success("工单已挂单");
        loadOrderList();
      } catch (error) {
        void error;
      }
      break;
    case "resume":
      try {
        await appointmentApi.updateOrderStatus(order.id, "待确认");
        ElMessage.success("工单已恢复");
        loadOrderList();
      } catch (error) {
        ElMessage.error("恢复工单失败");
      }
      break;
    case "cancel":
      try {
        await ElMessageBox.confirm("确定取消工单吗？", "确认取消", {
          type: "error",
        });
        await appointmentApi.cancelOrder(order.id);
        ElMessage.success("工单已取消");
        loadOrderList();
      } catch (error) {
        void error;
      }
      break;
  }
};

const detailDialogVisible = ref(false);
const currentOrder = ref<ExtendedAppointmentOrder | null>(null);
const detailLoading = ref(false);

const viewOrderDetail = async (row: Order) => {
  detailLoading.value = true;
  detailDialogVisible.value = true;
  try {
    const res: any = await appointmentApi.getOrder(row.id);
    currentOrder.value = res.data || res;
  } catch (error) {
    console.error("获取工单详情失败:", error);
    ElMessage.error("获取工单详情失败");
  } finally {
    detailLoading.value = false;
  }
};

const getServiceTypeColor = (type: string) => {
  switch (type) {
    case "wash":
      return "primary";
    case "grooming":
      return "success";
    case "vaccination":
      return "warning";
    case "checkup":
      return "info";
    default:
      return "info";
  }
};

const getServiceTypeText = (type: string) => {
  switch (type) {
    case "wash":
      return "洗澡";
    case "grooming":
      return "美容";
    case "vaccination":
      return "疫苗";
    case "checkup":
      return "体检";
    default:
      return type || "其他";
  }
};

const getStatusType = (status: string) => {
  switch (status) {
    case "待确认":
      return "warning";
    case "已确认":
      return "success";
    case "已预约":
      return "info";
    case "进行中":
      return "primary";
    case "已完成":
      return "success";
    case "已取消":
      return "danger";
    case "已挂单":
      return "info";
    default:
      return "info";
  }
};

onMounted(() => {
  loadOrderList();
});
</script>

<style scoped lang="scss">
.order-list {
  .order-overview {
    margin-bottom: 20px;

    .overview-card {
      background-color: #fff;
      padding: 20px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: transform 0.2s;

      &:hover {
        transform: translateY(-2px);
      }

      .card-icon {
        margin-right: 16px;
      }

      .card-content {
        .card-value {
          font-size: 24px;
          font-weight: 600;
          color: #1d2129;
          margin-bottom: 4px;
        }

        .card-label {
          font-size: 14px;
          color: #86909c;
        }
      }
    }
  }

  .filter-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .filter-form {
      .el-form-item {
        margin-bottom: 12px;
      }
    }
  }

  .amount {
    color: #f56c6c;
    font-weight: 500;
  }

  .rating {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .no-rating {
    color: #c9cdd4;
  }
}
</style>
