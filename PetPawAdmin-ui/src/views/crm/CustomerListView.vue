<template>
  <div class="customer-list">
    <PageHeader title="客户列表" description="管理宠物店的客户信息">
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增客户
        </el-button>
      </template>
    </PageHeader>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="客户姓名">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入客户姓名"
            clearable
            style="width: 120px"
          />
        </el-form-item>

        <el-form-item label="联系电话">
          <el-input
            v-model="filterForm.phone"
            placeholder="请输入联系电话"
            clearable
            style="width: 140px"
          />
        </el-form-item>

        <el-form-item label="客户等级">
          <el-select
            v-model="filterForm.level"
            placeholder="请选择等级"
            clearable
            style="width: 100px"
          >
            <el-option label="普通客户" value="normal" />
            <el-option label="VIP客户" value="vip" />
            <el-option label="钻石客户" value="diamond" />
          </el-select>
        </el-form-item>

        <el-form-item label="注册时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
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

    <!-- 数据表格 -->
    <DataTable
      :data="customerList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      show-selection
      show-actions
      @selection-change="handleSelectionChange"
      @page-change="handlePageChange"
    >
      <template #column-imageUrl="{ row }">
        <el-image
          v-if="row.imageUrl"
          :src="row.imageUrl"
          :preview-src-list="[row.imageUrl]"
          fit="cover"
          style="width: 40px; height: 40px; border-radius: 50%"
          preview-teleported
        />
        <div
          v-else
          style="
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: #f5f7fa;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #909399;
          "
        >
          <el-icon><Picture /></el-icon>
        </div>
      </template>

      <template #column-level="{ row }">
        <el-tag :type="getLevelType(row.level)">{{
          getLevelText(row.level)
        }}</el-tag>
      </template>

      <template #column-gender="{ row }">
        <span>{{ row.gender === "male" ? "男" : "女" }}</span>
      </template>

      <template #column-status="{ row }">
        <el-tag :type="row.status === 'active' ? 'success' : 'info'">
          {{ row.status === "active" ? "正常" : "停用" }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">查看</el-button>
        <el-button size="small" type="primary" @click="editCustomer(row)"
          >编辑</el-button
        >
        <el-dropdown @command="(command) => handleAction(command, row)">
          <el-button size="small">
            更多操作
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="viewBusiness"
                >查看业务</el-dropdown-item
              >
              <el-dropdown-item command="viewPets">查看宠物</el-dropdown-item>
              <el-dropdown-item command="viewConsumption"
                >消费记录</el-dropdown-item
              >
              <el-dropdown-item command="delete" type="danger" divided
                >删除客户</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </DataTable>

    <!-- 新增/编辑客户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="customerFormRef"
        :model="customerForm"
        :rules="customerRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="name">
              <el-input
                v-model="customerForm.name"
                placeholder="请输入客户姓名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="头像">
              <ImageUpload v-model="customerForm.imageUrl" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input
                v-model="customerForm.phone"
                placeholder="请输入联系电话"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="customerForm.gender" placeholder="请选择性别">
                <el-option label="男" value="male" />
                <el-option label="女" value="female" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期">
              <el-date-picker
                v-model="customerForm.birthday"
                type="date"
                placeholder="选择出生日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="联系地址">
          <el-input
            v-model="customerForm.address"
            placeholder="请输入联系地址"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户等级">
              <el-select
                v-model="customerForm.level"
                placeholder="请选择客户等级"
                disabled
              >
                <el-option label="普通客户" value="normal" />
                <el-option label="VIP客户" value="vip" />
                <el-option label="钻石客户" value="diamond" />
              </el-select>
              <div class="form-tip">请在会员管理中升级</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="customerForm.status">
                <el-radio label="active">正常</el-radio>
                <el-radio label="inactive">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
            v-model="customerForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 客户详情弹窗 -->
    <el-dialog
      v-model="customerDetailDialogVisible"
      title="客户详情"
      width="600px"
    >
      <el-descriptions v-if="currentCustomer" :column="2" border>
        <el-descriptions-item label="客户姓名">{{
          currentCustomer.name
        }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{
          currentCustomer.phone
        }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{
          currentCustomer.gender === "male" ? "男" : "女"
        }}</el-descriptions-item>
        <el-descriptions-item label="等级">
          <el-tag :type="getLevelType(currentCustomer.level)">{{
            getLevelText(currentCustomer.level)
          }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="出生日期">{{
          currentCustomer.birthday || "未设置"
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag
            :type="currentCustomer.status === 'active' ? 'success' : 'info'"
          >
            {{ currentCustomer.status === "active" ? "正常" : "停用" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="联系地址" :span="2">{{
          currentCustomer.address || "未设置"
        }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{
          currentCustomer.remark || "无"
        }}</el-descriptions-item>
        <el-descriptions-item label="宠物数量">{{
          currentCustomer.petCount
        }}</el-descriptions-item>
        <el-descriptions-item label="累计消费"
          >¥{{ currentCustomer.totalConsumption }}</el-descriptions-item
        >
        <el-descriptions-item label="最后到店" :span="2">{{
          currentCustomer.lastVisitTime || "暂无记录"
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="customerDetailDialogVisible = false"
            >关闭</el-button
          >
        </span>
      </template>
    </el-dialog>

    <!-- 宠物列表弹窗 -->
    <el-dialog v-model="petDialogVisible" title="客户宠物" width="800px">
      <el-table v-loading="petLoading" :data="petList" border stripe>
        <el-table-column prop="petName" label="宠物昵称" />
        <el-table-column prop="petType" label="种类" />
        <el-table-column prop="breed" label="品种" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? "公" : "母" }}
          </template>
        </el-table-column>
        <el-table-column prop="birthday" label="生日" />
        <el-table-column prop="weight" label="体重(kg)" width="100" />
      </el-table>
    </el-dialog>

    <!-- 预约业务弹窗 -->
    <el-dialog v-model="businessDialogVisible" title="预约记录" width="900px">
      <el-table v-loading="businessLoading" :data="businessList" border stripe>
        <el-table-column prop="orderNumber" label="订单编号" width="180" />
        <el-table-column prop="serviceName" label="服务项目" />
        <el-table-column prop="appointmentTime" label="预约时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="
                row.status === 'completed'
                  ? 'success'
                  : row.status === 'pending'
                    ? 'warning'
                    : 'info'
              "
            >
              {{
                row.status === "completed"
                  ? "已完成"
                  : row.status === "pending"
                    ? "待处理"
                    : row.status
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="金额" width="100" />
      </el-table>
    </el-dialog>

    <!-- 消费记录弹窗 -->
    <el-dialog
      v-model="consumptionDialogVisible"
      title="消费记录"
      width="900px"
    >
      <el-table
        v-loading="consumptionLoading"
        :data="consumptionList"
        border
        stripe
      >
        <el-table-column prop="orderNumber" label="订单编号" width="180" />
        <el-table-column label="订单类型" width="100">
          <template #default="{ row }">
            <el-tag>{{
              row.orderType === "service"
                ? "服务订单"
                : row.orderType === "product"
                  ? "商品订单"
                  : "其他"
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实付金额" width="100">
          <template #default="{ row }">¥{{ row.actualAmount }}</template>
        </el-table-column>
        <el-table-column label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.paymentStatus === 'paid' ? 'success' : 'danger'">
              {{ row.paymentStatus === "paid" ? "已支付" : "未支付" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
    </el-dialog>

    <!-- 客户详情弹窗 -->
    <el-dialog
      v-model="customerDetailDialogVisible"
      title="客户详情"
      width="700px"
    >
      <el-descriptions v-if="currentCustomer" :column="2" border>
        <el-descriptions-item label="客户姓名">{{
          currentCustomer.name
        }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{
          currentCustomer.phone
        }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{
          currentCustomer.gender === "male" ? "男" : "女"
        }}</el-descriptions-item>
        <el-descriptions-item label="客户等级">
          <el-tag :type="getLevelType(currentCustomer.level || 'normal')">
            {{ getLevelText(currentCustomer.level || "normal") }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag
            :type="currentCustomer.status === 'active' ? 'success' : 'info'"
          >
            {{ currentCustomer.status === "active" ? "正常" : "停用" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="出生日期">{{
          currentCustomer.birthday || "未填写"
        }}</el-descriptions-item>
        <el-descriptions-item label="联系地址" :span="2">{{
          currentCustomer.address || "未填写"
        }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{
          currentCustomer.remark || "无"
        }}</el-descriptions-item>
        <el-descriptions-item label="宠物数量">{{
          currentCustomer.petCount
        }}</el-descriptions-item>
        <el-descriptions-item label="累计消费"
          >¥{{ currentCustomer.totalConsumption }}</el-descriptions-item
        >
        <el-descriptions-item label="最后到店" :span="2">{{
          currentCustomer.lastVisitTime
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="customerDetailDialogVisible = false"
            >关闭</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
} from "element-plus";
import {
  Plus,
  Download,
  Search,
  Refresh,
  ArrowDown,
  Picture,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import ImageUpload from "@/components/common/ImageUpload.vue";
import { crmApi, appointmentApi, financeApi } from "@/api";
import type { PageParams } from "@/types/common";
import type {
  CrmCustomer,
  CustomerQueryParams,
  CrmPet,
  AppointmentOrder,
  FinanceOrder,
} from "@/api";

// 统一类型定义（只保留一份）
interface Customer {
  id: number;
  customerName?: string;
  customer_name?: string;
  phone?: string;
  gender?: number; // 1=男 2=女
  level?: string; // normal/vip/diamond
  customerType?: string; // 后端对应字段
  petCount?: number;
  totalConsumption?: number;
  lastVisitTime?: string;
  create_time?: string;
  createTime?: string;
  status?: number; // 1=active 0=inactive
  address?: string;
  birthday?: string;
  remark?: string;
  imageUrl?: string;
}

interface CustomerListResponse {
  code: number;
  total?: number;
  records?: Customer[];
  data?: {
    records?: Customer[];
    total?: number;
  };
  message?: string;
}

// 表格列配置（保持不变）
const columns = [
  { prop: "imageUrl", label: "头像", width: 80 },
  { prop: "name", label: "客户姓名", width: 120 },
  { prop: "phone", label: "联系电话", width: 130 },
  { prop: "gender", label: "性别", width: 80 },
  { prop: "level", label: "客户等级", width: 100 },
  { prop: "petCount", label: "宠物数量", width: 100 },
  { prop: "totalConsumption", label: "累计消费", width: 120 },
  { prop: "lastVisitTime", label: "最后到店", width: 160 },
  { prop: "status", label: "状态", width: 80 },
];

// 响应式数据（保持不变）
const router = useRouter();
const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref("新增客户");
const isEdit = ref(false);

const customerList = ref<Customer[]>([]);
const total = ref(0);
const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

const filterForm = reactive({
  name: "",
  phone: "",
  level: "",
  dateRange: [] as [string, string] | [],
});

const customerForm = reactive({
  id: 0,
  name: "",
  phone: "",
  gender: "male" as "male" | "female",
  level: "normal" as "normal" | "vip" | "diamond",
  status: "inactive" as "active" | "inactive",
  address: "",
  birthday: "",
  remark: "",
  imageUrl: "",
});

const customerFormRef = ref<FormInstance>();

const customerRules: FormRules = {
  name: [
    { required: true, message: "请输入客户姓名", trigger: "blur" },
    { min: 2, max: 20, message: "姓名长度在 2 到 20 个字符", trigger: "blur" },
  ],
  phone: [
    { required: true, message: "请输入联系电话", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
};

// 弹窗状态管理
const petDialogVisible = ref(false);
const businessDialogVisible = ref(false);
const consumptionDialogVisible = ref(false);

// 数据列表
const petList = ref<CrmPet[]>([]);
const businessList = ref<AppointmentOrder[]>([]);
const consumptionList = ref<FinanceOrder[]>([]);

// 加载状态
const petLoading = ref(false);
const businessLoading = ref(false);
const consumptionLoading = ref(false);

// 通用列表响应解析函数
function parseListResponse<T>(res: any): T[] {
  if (Array.isArray(res.records)) {
    return res.records;
  }
  if (Array.isArray(res.data?.records)) {
    return res.data.records;
  }
  if (Array.isArray(res.data?.data?.records)) {
    return res.data.data.records;
  }
  if (Array.isArray(res)) {
    return res;
  }
  if (Array.isArray(res.data)) {
    return res.data;
  }
  return [];
}

// 业务方法
const handleViewPets = async (customer: Customer) => {
  petDialogVisible.value = true;
  petLoading.value = true;
  try {
    const res = await crmApi.getPets({
      current: 1,
      size: 100,
      customerId: customer.id,
    });
    petList.value = parseListResponse(res);
  } catch (error) {
    console.error(error);
    ElMessage.error("获取宠物列表失败");
  } finally {
    petLoading.value = false;
  }
};

const handleViewBusiness = async (customer: Customer) => {
  businessDialogVisible.value = true;
  businessLoading.value = true;
  try {
    const res = await appointmentApi.getOrders({
      current: 1,
      size: 100,
      customerId: customer.id,
    });
    businessList.value = parseListResponse(res);
  } catch (error) {
    console.error(error);
    ElMessage.error("获取预约记录失败");
  } finally {
    businessLoading.value = false;
  }
};

const handleViewConsumption = async (customer: Customer) => {
  consumptionDialogVisible.value = true;
  consumptionLoading.value = true;
  try {
    const res = await financeApi.getOrders({
      current: 1,
      size: 100,
      customerId: customer.id,
    });
    consumptionList.value = parseListResponse(res);
  } catch (error) {
    console.error(error);
    ElMessage.error("获取消费记录失败");
  } finally {
    consumptionLoading.value = false;
  }
};

// 修复核心：加载客户列表方法
const loadCustomerList = async () => {
  loading.value = true;
  try {
    const params: CustomerQueryParams = {
      current: pagination.pageNum,
      size: pagination.pageSize,
      customerName: filterForm.name || undefined,
      phone: filterForm.phone || undefined,
      customerType: filterForm.level || undefined,
      startDate: filterForm.dateRange?.[0] || undefined,
      endDate: filterForm.dateRange?.[1] || undefined,
    };

    // 1. 发起请求
    const response = await crmApi.getCustomers<CustomerListResponse>(params);
    console.log("真实API响应:", response); // 调试用

    // 2. 兼容两种响应格式（优先外层，再内层）
    let records: Customer[] = [];
    let totalCount = 0;

    // 处理响应数据
    if (Array.isArray(response.records)) {
      records = response.records;
      totalCount = response.total || 0;
    }
    // 适配原来的取值方式
    else if (response.data?.data?.records) {
      records = response.data.data.records;
      totalCount = response.data.data.total || 0;
    }
    // 兼容其他格式
    else if (response.data?.records) {
      records = response.data.records;
      totalCount = response.data.total || 0;
    }

    total.value = totalCount;

    // 3. 正确的字段映射（修复level硬编码问题）
    customerList.value = records.map((customer) => ({
      id: customer.id || 0,
      name: customer.customerName || customer.customer_name || "未知客户",
      phone: customer.phone || "",
      gender: customer.gender === 1 ? "male" : "female",
      // 优先使用 customerType 作为 level
      level: customer.customerType || customer.level || "normal",
      petCount: customer.petCount || 0,
      totalConsumption: customer.totalConsumption || 0,
      lastVisitTime:
        customer.lastVisitTime ||
        customer.createTime ||
        customer.create_time ||
        "",
      status: customer.status === 1 ? "active" : "inactive",
      address: customer.address || "",
      birthday: customer.birthday || "",
      remark: customer.remark || "",
      imageUrl: customer.imageUrl || "",
    }));

    console.log("处理后的数据:", customerList.value); // 调试用
  } catch (error) {
    console.error("加载客户列表失败:", error);
    ElMessage.error("加载客户列表失败");
    customerList.value = [];
  } finally {
    loading.value = false;
  }
};

// 修复表单提交的remarks字段问题
const handleSubmit = async () => {
  if (!customerFormRef.value) return;

  try {
    await customerFormRef.value.validate();

    submitLoading.value = true;

    const customerData: CrmCustomer = {
      customerName: customerForm.name,
      phone: customerForm.phone,
      address: customerForm.address,
      gender: customerForm.gender === "male" ? 1 : 2,
      birthday: customerForm.birthday,
      remark: customerForm.remark,
      status: customerForm.status === "active" ? 1 : 0,
      customerType: customerForm.level, // 添加客户类型字段
      imageUrl: customerForm.imageUrl,
    };

    if (isEdit.value) {
      await crmApi.updateCustomer(customerForm.id, customerData);
      ElMessage.success("客户信息更新成功");
    } else {
      await crmApi.createCustomer(customerData);
      ElMessage.success("客户添加成功");
    }

    dialogVisible.value = false;
    loadCustomerList();
  } catch (error) {
    console.error("提交失败:", error);
    ElMessage.error("提交失败");
  } finally {
    submitLoading.value = false;
  }
};

// 其他方法保持不变...
const handleSearch = () => {
  pagination.pageNum = 1;
  loadCustomerList();
};

const handleReset = () => {
  Object.keys(filterForm).forEach((key) => {
    filterForm[key] = "";
  });
  filterForm.dateRange = [];
  pagination.pageNum = 1;
  loadCustomerList();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadCustomerList();
};

const handleSelectionChange = (selection: Customer[]) => {
  console.log("选中的客户:", selection);
};

const showAddDialog = () => {
  dialogTitle.value = "新增客户";
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const editCustomer = (customer: Customer) => {
  dialogTitle.value = "编辑客户";
  isEdit.value = true;
  Object.assign(customerForm, {
    id: customer.id,
    name:
      customer.name || customer.customerName || customer.customer_name || "",
    phone: customer.phone || "",
    gender: customer.gender === 1 ? "male" : "female",
    level: customer.level || customer.customerType || "normal",
    status: customer.status === 1 ? "active" : "inactive",
    address: customer.address || "",
    birthday: customer.birthday || "",
    remark: customer.remark || "",
    imageUrl: customer.imageUrl || "",
  });
  dialogVisible.value = true;
};

const resetForm = () => {
  customerForm.id = 0;
  customerForm.name = "";
  customerForm.phone = "";
  customerForm.gender = "male";
  customerForm.level = "normal";
  customerForm.status = "inactive";
  customerForm.address = "";
  customerForm.birthday = "";
  customerForm.remark = "";
  customerForm.imageUrl = "";
};

const customerDetailDialogVisible = ref(false);
const currentCustomer = ref<Customer | null>(null);

const viewDetail = (customer: Customer) => {
  currentCustomer.value = customer;
  customerDetailDialogVisible.value = true;
};

const handleAction = async (command: string, customer: Customer) => {
  switch (command) {
    case "delete":
      try {
        await ElMessageBox.confirm(
          `确定要删除客户"${customer.customerName || customer.customer_name}"吗？此操作不可撤销。`,
          "确认删除",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          },
        );
        await crmApi.deleteCustomer(customer.id);
        ElMessage.success("客户删除成功");
        loadCustomerList();
      } catch {
        // 用户取消操作
      }
      break;
    case "viewBusiness":
      handleViewBusiness(customer);
      break;
    case "viewPets":
      handleViewPets(customer);
      break;
    case "viewConsumption":
      handleViewConsumption(customer);
      break;
  }
};

const getLevelType = (level: string) => {
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

const getLevelText = (level: string) => {
  switch (level) {
    case "diamond":
      return "钻石";
    case "vip":
      return "VIP";
    case "normal":
      return "普通";
    default:
      return "普通";
  }
};

onMounted(() => {
  loadCustomerList();
});
</script>

<style scoped lang="scss">
.customer-list {
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
}
.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.2;
  margin-top: 4px;
}
</style>
