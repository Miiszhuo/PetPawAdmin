<template>
  <div class="supplier-list">
    <PageHeader title="供应商管理" description="管理供应商信息和合作关系">
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增供应商
        </el-button>
      </template>
    </PageHeader>

    <!-- 供应商概览 -->
    <div class="supplier-overview">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#409eff"><Shop /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.totalSuppliers }}</div>
              <div class="card-label">总供应商数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#67c23a"><Check /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.activeSuppliers }}</div>
              <div class="card-label">合作中</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#f56c6c"><Close /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.inactiveSuppliers }}</div>
              <div class="card-label">已暂停</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#e6a23c"><Money /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">
                ¥{{ overview.totalPurchase.toLocaleString() }}
              </div>
              <div class="card-label">累计采购额</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="供应商名称">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入供应商名称"
            clearable
            style="width: 140px"
          />
        </el-form-item>

        <el-form-item label="联系人">
          <el-input
            v-model="filterForm.contact"
            placeholder="请输入联系人"
            clearable
            style="width: 120px"
          />
        </el-form-item>

        <el-form-item label="合作状态">
          <el-select
            v-model="filterForm.status"
            placeholder="请选择合作状态"
            clearable
            style="width: 120px"
          >
            <el-option label="合作中" value="active" />
            <el-option label="已暂停" value="inactive" />
            <el-option label="已终止" value="terminated" />
          </el-select>
        </el-form-item>

        <el-form-item label="供应商等级">
          <el-select
            v-model="filterForm.level"
            placeholder="请选择等级"
            clearable
            style="width: 100px"
          >
            <el-option label="A级" value="A" />
            <el-option label="B级" value="B" />
            <el-option label="C级" value="C" />
          </el-select>
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

    <!-- 供应商表格 -->
    <DataTable
      :data="supplierList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      show-selection
      show-actions
      @selection-change="handleSelectionChange"
      @page-change="handlePageChange"
    >
      <template #column-level="{ row }">
        <el-tag :type="getLevelType(row.level)">{{ row.level }}级</el-tag>
      </template>

      <template #column-status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>

      <template #column-cooperationYears="{ row }">
        <span>{{ row.cooperationYears }}年</span>
      </template>

      <template #column-lastOrderTime="{ row }">
        <span>{{ formatDate(row.lastOrderTime) }}</span>
      </template>

      <template #column-actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">详情</el-button>
        <el-button size="small" type="primary" @click="editSupplier(row)"
          >编辑</el-button
        >
      </template>
    </DataTable>

    <!-- 新增/编辑供应商对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="supplierFormRef"
        :model="supplierForm"
        :rules="supplierRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商名称" prop="name">
              <el-input
                v-model="supplierForm.name"
                placeholder="请输入供应商名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商编码" prop="code">
              <el-input
                v-model="supplierForm.code"
                placeholder="请输入供应商编码"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input
                v-model="supplierForm.contactName"
                placeholder="请输入联系人姓名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input
                v-model="supplierForm.contactPhone"
                placeholder="请输入联系电话"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱地址">
              <el-input
                v-model="supplierForm.email"
                placeholder="请输入邮箱地址"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商等级">
              <el-select v-model="supplierForm.level" placeholder="请选择等级">
                <el-option label="A级" value="A" />
                <el-option label="B级" value="B" />
                <el-option label="C级" value="C" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="公司地址" prop="address">
          <el-input
            v-model="supplierForm.address"
            placeholder="请输入公司详细地址"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="营业执照号">
              <el-input
                v-model="supplierForm.licenseNumber"
                placeholder="请输入营业执照号"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合作开始日期">
              <el-date-picker
                v-model="supplierForm.cooperationStartDate"
                type="date"
                placeholder="选择合作开始日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主要产品">
              <el-input
                v-model="supplierForm.mainProducts"
                placeholder="如：狗粮、猫粮、宠物用品"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合作状态">
              <el-radio-group v-model="supplierForm.status">
                <el-radio label="active">合作中</el-radio>
                <el-radio label="inactive">已暂停</el-radio>
                <el-radio label="terminated">已终止</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
            v-model="supplierForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>

        <el-form-item label="资质证书">
          <el-upload
            v-model:file-list="supplierForm.certificates"
            action="/api/upload"
            list-type="picture-card"
            :limit="5"
            :on-success="handleUploadSuccess"
            :on-remove="handleUploadRemove"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="upload-tip">最多上传5张资质证书，单张不超过2MB</div>
            </template>
          </el-upload>
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
    <el-dialog v-model="supplierDetailVisible" title="供应商详情" width="700px">
      <el-descriptions v-if="currentSupplier" :column="2" border>
        <el-descriptions-item label="供应商名称">{{
          currentSupplier.name
        }}</el-descriptions-item>
        <el-descriptions-item label="供应商编码">{{
          currentSupplier.code
        }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{
          currentSupplier.contactName
        }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{
          currentSupplier.contactPhone
        }}</el-descriptions-item>
        <el-descriptions-item label="邮箱地址">{{
          currentSupplier.email || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="供应商等级">
          <el-tag :type="getLevelType(currentSupplier.level)"
            >{{ currentSupplier.level }}级</el-tag
          >
        </el-descriptions-item>
        <el-descriptions-item label="公司地址" :span="2">{{
          currentSupplier.address
        }}</el-descriptions-item>
        <el-descriptions-item label="营业执照号">{{
          currentSupplier.licenseNumber || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="合作开始日期">{{
          currentSupplier.cooperationStartDate || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="主要产品" :span="2">{{
          currentSupplier.mainProducts || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="合作状态">
          <el-tag :type="getStatusType(currentSupplier.status)">{{
            getStatusText(currentSupplier.status)
          }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{
          currentSupplier.remark || "无"
        }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import {
  Plus,
  Download,
  Search,
  Refresh,
  ArrowDown,
  Shop,
  Check,
  Close,
  Money,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import { scmApi } from "@/api";
import type { PageParams } from "@/types/common";
import type { ScmSupplier, SupplierQueryParams } from "@/api/scm";

// 表格列配置
const columns = [
  { prop: "name", label: "供应商名称", width: 180 },
  { prop: "code", label: "供应商编码", width: 120 },
  { prop: "contactName", label: "联系人", width: 100 },
  { prop: "contactPhone", label: "联系电话", width: 130 },
  { prop: "level", label: "等级", width: 80 },
  { prop: "cooperationYears", label: "合作年限", width: 100 },
  { prop: "lastOrderTime", label: "最后采购", width: 160 },
  { prop: "status", label: "合作状态", width: 100 },
];

// 供应商接口（后端数据结构）
interface BackendScmSupplier {
  id?: number;
  supplierName?: string;
  contactPerson?: string;
  phone?: string;
  email?: string;
  address?: string;
  cooperationStatus?: number;
  createTime?: string;
  updateTime?: string;
  remarks?: string;
}

interface SupplierListResponse {
  code: number;
  total?: number;
  records?: BackendScmSupplier[];
  data?: {
    records?: BackendScmSupplier[];
    total?: number;
  };
  message?: string;
}

// 前端显示接口
interface Supplier {
  id: number;
  name: string;
  code: string;
  contactName: string;
  contactPhone: string;
  email?: string;
  address: string;
  level: "A" | "B" | "C";
  licenseNumber?: string;
  mainProducts?: string;
  cooperationStartDate: string;
  cooperationYears: number;
  lastOrderTime: string;
  status: "active" | "inactive" | "terminated";
  remark?: string;
  certificates?: any[];
}

// 响应式数据
const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref("新增供应商");
const isEdit = ref(false);

// 供应商概览
const overview = reactive({
  totalSuppliers: 0,
  activeSuppliers: 0,
  inactiveSuppliers: 0,
  totalPurchase: 0,
});

// 供应商列表
const supplierList = ref<Supplier[]>([]);
const total = ref(0);
const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

// 筛选表单
const filterForm = reactive({
  name: "",
  contact: "",
  status: "",
  level: "",
});

// 供应商表单
const supplierForm = reactive({
  id: 0,
  name: "",
  code: "",
  contactName: "",
  contactPhone: "",
  email: "",
  address: "",
  level: "C" as "A" | "B" | "C",
  licenseNumber: "",
  mainProducts: "",
  cooperationStartDate: "",
  status: "active" as "active" | "inactive" | "terminated",
  remark: "",
  certificates: [] as any[],
});

const supplierFormRef = ref<FormInstance>();

// 表单验证规则
const supplierRules: FormRules = {
  name: [
    { required: true, message: "请输入供应商名称", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "供应商名称长度在 2 到 50 个字符",
      trigger: "blur",
    },
  ],
  code: [
    { required: true, message: "请输入供应商编码", trigger: "blur" },
    {
      pattern: /^[A-Z0-9-_]+$/,
      message: "供应商编码只能包含字母、数字、下划线和连字符",
      trigger: "blur",
    },
  ],
  contactName: [
    { required: true, message: "请输入联系人姓名", trigger: "blur" },
    {
      min: 2,
      max: 20,
      message: "联系人姓名长度在 2 到 20 个字符",
      trigger: "blur",
    },
  ],
  contactPhone: [
    { required: true, message: "请输入联系电话", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
  address: [
    { required: true, message: "请输入公司地址", trigger: "blur" },
    {
      min: 5,
      max: 100,
      message: "地址长度在 5 到 100 个字符",
      trigger: "blur",
    },
  ],
};

// 方法
const loadSupplierList = async () => {
  loading.value = true;
  try {
    const params: SupplierQueryParams = {
      current: pagination.pageNum,
      size: pagination.pageSize,
      supplierName: filterForm.name || undefined,
      contact: filterForm.contact || undefined,
      cooperationStatus:
        filterForm.status === "active"
          ? 1
          : filterForm.status === "inactive"
            ? 0
            : undefined,
      level: filterForm.level || undefined,
    };

    // 1. 发起请求
    const response = await scmApi.getSuppliers<SupplierListResponse>(params);
    console.log("真实API响应:", response); // 调试用

    // 2. 兼容两种响应格式（优先外层，再内层）
    let records: ScmSupplier[] = [];
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

    // 3. 正确的字段映射
    supplierList.value = records.map((supplier) => ({
      id: supplier.id || 0,
      name: supplier.supplierName || "",
      code: `SUP${supplier.id?.toString().padStart(4, "0") || "0000"}`, // 生成虚拟编码
      contactName: supplier.contactPerson || "",
      contactPhone: supplier.phone || "",
      email: supplier.email || "",
      address: supplier.address || "",
      level: supplier.level || "C", // 默认为C
      licenseNumber: "", // API中没有此字段
      mainProducts: "", // API中没有此字段
      cooperationStartDate: supplier.createTime || "",
      cooperationYears: 0, // 暂时为0，后续可计算
      lastOrderTime: supplier.createTime || "", // 暂时使用创建时间
      status: supplier.cooperationStatus === 1 ? "active" : "inactive",
      remark: supplier.remark || "",
    }));

    console.log("处理后的数据:", supplierList.value); // 调试用

    // 计算概览数据
    calculateOverview();
  } catch (error) {
    console.error("加载供应商列表失败:", error);
    ElMessage.error("加载供应商列表失败");
    supplierList.value = [];
  } finally {
    loading.value = false;
  }
};

const calculateOverview = () => {
  overview.totalSuppliers = supplierList.value.length;
  overview.activeSuppliers = supplierList.value.filter(
    (s) => s.status === "active",
  ).length;
  overview.inactiveSuppliers = supplierList.value.filter(
    (s) => s.status === "inactive",
  ).length;
  overview.totalPurchase = 2568000; // 模拟数据
};

const handleSearch = () => {
  pagination.pageNum = 1;
  loadSupplierList();
};

const handleReset = () => {
  Object.keys(filterForm).forEach((key) => {
    filterForm[key] = "";
  });
  pagination.pageNum = 1;
  loadSupplierList();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadSupplierList();
};

const handleSelectionChange = (selection: Supplier[]) => {
  console.log("选中的供应商:", selection);
};

const showAddDialog = () => {
  dialogTitle.value = "新增供应商";
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const editSupplier = (supplier: Supplier) => {
  dialogTitle.value = "编辑供应商";
  isEdit.value = true;
  Object.assign(supplierForm, {
    id: supplier.id,
    name: supplier.name,
    code: supplier.code,
    contactName: supplier.contactName,
    contactPhone: supplier.contactPhone,
    email: supplier.email,
    address: supplier.address,
    level: supplier.level,
    licenseNumber: supplier.licenseNumber,
    mainProducts: supplier.mainProducts,
    cooperationStartDate: supplier.cooperationStartDate,
    cooperationYears: supplier.cooperationYears,
    lastOrderTime: supplier.lastOrderTime,
    status: supplier.status,
    remark: supplier.remark,
  });
  dialogVisible.value = true;
};

const resetForm = () => {
  supplierForm.id = 0;
  supplierForm.name = "";
  supplierForm.code = "";
  supplierForm.contactName = "";
  supplierForm.contactPhone = "";
  supplierForm.email = "";
  supplierForm.address = "";
  supplierForm.level = "C";
  supplierForm.licenseNumber = "";
  supplierForm.mainProducts = "";
  supplierForm.cooperationStartDate = "";
  supplierForm.status = "active";
  supplierForm.remark = "";
  supplierForm.certificates = [];
};

const handleSubmit = async () => {
  if (!supplierFormRef.value) return;

  try {
    await supplierFormRef.value.validate();

    submitLoading.value = true;

    const supplierData: ScmSupplier = {
      supplierName: supplierForm.name,
      contactPerson: supplierForm.contactName,
      phone: supplierForm.contactPhone,
      email: supplierForm.email,
      address: supplierForm.address,
      cooperationStatus: supplierForm.status === "active" ? 1 : 0,
      remarks: supplierForm.remark,
    };

    if (isEdit.value) {
      await scmApi.updateSupplier(supplierForm.id, supplierData);
      ElMessage.success("供应商信息更新成功");
    } else {
      await scmApi.createSupplier(supplierData);
      ElMessage.success("供应商添加成功");
    }

    dialogVisible.value = false;
    loadSupplierList();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

const supplierDetailVisible = ref(false);
const currentSupplier = ref<Supplier | null>(null);

const viewDetail = (supplier: Supplier) => {
  currentSupplier.value = supplier;
  supplierDetailVisible.value = true;
};

const handleAction = async (command: string, supplier: Supplier) => {
  switch (command) {
    case "viewProducts":
      ElMessage.info("供应商品功能开发中");
      break;
    case "orderHistory":
      ElMessage.info("采购记录功能开发中");
      break;
    case "delete":
      try {
        await ElMessageBox.confirm(
          `确定要删除供应商"${supplier.name}"吗？此操作不可撤销。`,
          "确认删除",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          },
        );
        await scmApi.deleteSupplier(supplier.id);
        ElMessage.success("供应商删除成功");
        loadSupplierList();
      } catch {
        // 用户取消操作
      }
      break;
  }
};

const getLevelType = (level: string) => {
  switch (level) {
    case "A":
      return "danger";
    case "B":
      return "warning";
    case "C":
      return "info";
    default:
      return "info";
  }
};

const getStatusType = (status: string) => {
  switch (status) {
    case "active":
      return "success";
    case "inactive":
      return "warning";
    case "terminated":
      return "danger";
    default:
      return "info";
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case "active":
      return "合作中";
    case "inactive":
      return "已暂停";
    case "terminated":
      return "已终止";
    default:
      return "未知";
  }
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return "-";
  const date = new Date(dateStr);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

const handleUploadSuccess = (response: any, file: any) => {
  console.log("上传成功:", response, file);
};

const handleUploadRemove = (file: any, fileList: any[]) => {
  console.log("删除文件:", file, fileList);
};

onMounted(() => {
  loadSupplierList();
});
</script>

<style scoped lang="scss">
.supplier-list {
  .supplier-overview {
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

  .upload-tip {
    font-size: 12px;
    color: #86909c;
    margin-top: 8px;
  }
}
</style>
