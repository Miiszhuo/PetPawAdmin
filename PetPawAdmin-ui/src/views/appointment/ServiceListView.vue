<template>
  <div class="service-list">
    <PageHeader
      title="服务项目管理"
      description="管理宠物服务项目、定价和技能要求"
    >
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增服务项目
        </el-button>
      </template>
    </PageHeader>

    <!-- 服务项目概览 -->
    <div class="service-overview">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#409eff"><MagicStick /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.totalServices }}</div>
              <div class="card-label">总服务项目</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#67c23a"><Check /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.activeServices }}</div>
              <div class="card-label">启用项目</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#e6a23c"><Timer /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.avgDuration }}</div>
              <div class="card-label">平均时长(分钟)</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#f56c6c"><Money /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">¥{{ overview.avgPrice }}</div>
              <div class="card-label">平均价格</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="服务名称">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入服务名称"
            clearable
            style="width: 140px"
          />
        </el-form-item>

        <el-form-item label="服务分类">
          <el-select
            v-model="filterForm.category"
            placeholder="请选择分类"
            clearable
            style="width: 120px"
          >
            <el-option label="美容服务" value="grooming" />
            <el-option label="医疗服务" value="medical" />
            <el-option label="其他服务" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="技能要求">
          <el-select
            v-model="filterForm.requiredSkill"
            placeholder="请选择技能"
            clearable
            style="width: 120px"
          >
            <el-option label="洗澡" value="洗澡" />
            <el-option label="美容" value="美容" />
            <el-option label="造型" value="造型" />
            <el-option label="诊断" value="诊断" />
            <el-option label="疫苗" value="疫苗" />
            <el-option label="手术" value="手术" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select
            v-model="filterForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 100px"
          >
            <el-option label="启用" value="active" />
            <el-option label="禁用" value="inactive" />
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

    <!-- 服务项目表格 -->
    <DataTable
      :data="serviceList"
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
          style="width: 50px; height: 50px; border-radius: 4px"
          preview-teleported
        />
        <div
          v-else
          class="no-image"
          style="
            width: 50px;
            height: 50px;
            background: #f5f7fa;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 4px;
            color: #909399;
          "
        >
          <el-icon><Picture /></el-icon>
        </div>
      </template>

      <template #column-category="{ row }">
        <el-tag :type="getCategoryType(row.category)">
          {{ getCategoryText(row.category) }}
        </el-tag>
      </template>

      <template #column-price="{ row }">
        <span class="price">¥{{ row.price }}</span>
      </template>

      <template #column-duration="{ row }">
        <span>{{ row.duration }}分钟</span>
      </template>

      <template #column-requiredSkills="{ row }">
        <div class="skills-tags">
          <el-tag
            v-for="skill in row.requiredSkills"
            :key="skill"
            size="small"
            type="info"
          >
            {{ skill }}
          </el-tag>
        </div>
      </template>

      <template #column-status="{ row }">
        <el-tag :type="row.status === 'active' ? 'success' : 'info'">
          {{ row.status === "active" ? "启用" : "禁用" }}
        </el-tag>
      </template>

      <template #column-popularity="{ row }">
        <div class="popularity">
          <el-progress
            :percentage="row.popularity"
            :show-text="false"
            :stroke-width="8"
            color="#409eff"
          />
          <span class="popularity-text">{{ row.popularity }}%</span>
        </div>
      </template>

      <template #column-actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">详情</el-button>
        <el-button size="small" type="primary" @click="editService(row)"
          >编辑</el-button
        >
        <el-button size="small" type="danger" @click="handleDeleteService(row)">
          删除
        </el-button>
      </template>
    </DataTable>

    <!-- 新增/编辑服务项目对话框 -->
    <el-dialog
      v-model="serviceDialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="serviceFormRef"
        :model="serviceForm"
        :rules="serviceRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="服务名称" prop="name">
              <el-input
                v-model="serviceForm.name"
                placeholder="请输入服务名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务编码" prop="code">
              <el-input
                v-model="serviceForm.code"
                placeholder="请输入服务编码"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="服务分类" prop="category">
              <el-select
                v-model="serviceForm.category"
                placeholder="请选择服务分类"
              >
                <el-option label="美容服务" value="grooming" />
                <el-option label="医疗服务" value="medical" />
                <el-option label="其他服务" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务时长(分钟)" prop="duration">
              <el-input-number
                v-model="serviceForm.duration"
                :min="15"
                :max="480"
                :step="15"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标准价格" prop="price">
              <el-input-number
                v-model="serviceForm.price"
                :precision="2"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员价格">
              <el-input-number
                v-model="serviceForm.memberPrice"
                :precision="2"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="所需技能" prop="requiredSkills">
          <el-checkbox-group v-model="serviceForm.requiredSkills">
            <el-checkbox label="洗澡">洗澡</el-checkbox>
            <el-checkbox label="美容">美容</el-checkbox>
            <el-checkbox label="造型">造型</el-checkbox>
            <el-checkbox label="诊断">诊断</el-checkbox>
            <el-checkbox label="疫苗">疫苗</el-checkbox>
            <el-checkbox label="手术">手术</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约提前期(小时)">
              <el-input-number
                v-model="serviceForm.advanceBookingHours"
                :min="0"
                :max="168"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="可预约技师">
              <el-select
                v-model="serviceForm.availableStaff"
                multiple
                placeholder="选择可提供此服务的技师"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="staff in allStaff"
                  :key="staff.id"
                  :label="`${staff.name} (${staff.title})`"
                  :value="staff.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="服务描述" prop="description">
          <el-input
            v-model="serviceForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入服务详细描述"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="注意事项">
              <el-input
                v-model="serviceForm.notes"
                type="textarea"
                :rows="3"
                placeholder="请输入服务注意事项"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适用宠物">
              <el-checkbox-group v-model="serviceForm.suitablePets">
                <el-checkbox label="小型犬">小型犬</el-checkbox>
                <el-checkbox label="中型犬">中型犬</el-checkbox>
                <el-checkbox label="大型犬">大型犬</el-checkbox>
                <el-checkbox label="猫">猫</el-checkbox>
                <el-checkbox label="其他">其他</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="serviceForm.status">
                <el-radio label="active">启用</el-radio>
                <el-radio label="inactive">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number
                v-model="serviceForm.sortOrder"
                :min="0"
                :max="999"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="服务图片">
          <ImageUpload v-model="serviceForm.imageUrl" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="serviceDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmitService"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 服务详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="服务详情" width="600px">
      <el-descriptions v-if="currentService" :column="2" border>
        <el-descriptions-item label="服务名称">{{
          currentService.name
        }}</el-descriptions-item>
        <el-descriptions-item label="服务编码">{{
          currentService.code || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="分类">
          <el-tag :type="getCategoryType(currentService.category)">
            {{ getCategoryText(currentService.category) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag
            :type="currentService.status === 'active' ? 'success' : 'info'"
          >
            {{ currentService.status === "active" ? "启用" : "禁用" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="价格"
          >¥{{ currentService.price }}</el-descriptions-item
        >
        <el-descriptions-item label="会员价格"
          >¥{{ currentService.memberPrice }}</el-descriptions-item
        >
        <el-descriptions-item label="时长"
          >{{ currentService.duration }}分钟</el-descriptions-item
        >
        <el-descriptions-item label="预约提前期"
          >{{ currentService.advanceBookingHours }}小时</el-descriptions-item
        >
        <el-descriptions-item label="所需技能" :span="2">
          <div class="skills-tags">
            <el-tag
              v-for="skill in currentService.requiredSkills"
              :key="skill"
              size="small"
              type="info"
            >
              {{ skill }}
            </el-tag>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="适用宠物" :span="2">
          <div class="skills-tags">
            <el-tag
              v-for="pet in currentService.suitablePets"
              :key="pet"
              size="small"
              type="warning"
            >
              {{ pet }}
            </el-tag>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{
          currentService.description || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="注意事项" :span="2">{{
          currentService.notes || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{
          currentService.createdTime
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
import {
  Plus,
  Download,
  Search,
  Refresh,
  ArrowDown,
  MagicStick,
  Check,
  Timer,
  Money,
  Picture,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import ImageUpload from "@/components/common/ImageUpload.vue";
import { appointmentApi } from "@/api";
import type { PageParams } from "@/types/common";
import type {
  AppointmentServiceItem,
  ServiceItemQueryParams,
} from "@/api/appointment";

// 表格列配置
const columns = [
  { prop: "imageUrl", label: "图片", width: 100 },
  { prop: "name", label: "服务名称", width: 150 },
  { prop: "code", label: "服务编码", width: 120 },
  { prop: "category", label: "分类", width: 100 },
  { prop: "price", label: "价格", width: 100 },
  { prop: "duration", label: "时长", width: 80 },
  { prop: "requiredSkills", label: "所需技能", width: 150 },
  { prop: "status", label: "状态", width: 80 },
  { prop: "popularity", label: "热度", width: 120 },
];

// 服务项目接口
// 服务接口（后端数据结构）
interface BackendAppointmentServiceItem {
  id?: number;
  serviceName?: string;
  serviceCode?: string;
  category?: string;
  description?: string;
  price?: number;
  duration?: number;
  standardDuration?: number;
  status?: number;
  imageUrl?: string;
  createTime?: string;
  updateTime?: string;
}

interface ServiceListResponse {
  code: number;
  total?: number;
  records?: BackendAppointmentServiceItem[];
  data?: {
    records?: BackendAppointmentServiceItem[];
    total?: number;
  };
  message?: string;
}

// 前端显示接口
interface Service {
  id: number;
  name: string;
  code: string;
  category: "grooming" | "medical" | "other";
  description?: string;
  price: number;
  memberPrice?: number;
  duration: number;
  requiredSkills: string[];
  availableStaff: number[];
  advanceBookingHours: number;
  suitablePets: string[];
  notes?: string;
  status: "active" | "inactive";
  sortOrder: number;
  popularity: number;
  imageUrl?: string;
  createdTime: string;
}

// 技师接口
interface Staff {
  id: number;
  name: string;
  title: string;
}

// 响应式数据
const loading = ref(false);
const submitLoading = ref(false);
const serviceDialogVisible = ref(false);
const dialogTitle = ref("新增服务项目");
const isEdit = ref(false);
const detailDialogVisible = ref(false);
const currentService = ref<Service | null>(null);

// 服务项目概览
const overview = reactive({
  totalServices: 0,
  activeServices: 0,
  avgDuration: 0,
  avgPrice: 0,
});

// 服务项目列表
const serviceList = ref<Service[]>([]);
const total = ref(0);
const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

// 筛选表单
const filterForm = reactive({
  name: "",
  category: "",
  requiredSkill: "",
  status: "",
});

// 服务项目表单
const serviceForm = reactive({
  id: 0,
  name: "",
  code: "",
  category: "grooming" as "grooming" | "medical" | "other",
  description: "",
  price: 0,
  memberPrice: 0,
  duration: 60,
  requiredSkills: [] as string[],
  availableStaff: [] as number[],
  advanceBookingHours: 24,
  suitablePets: [] as string[],
  notes: "",
  status: "active" as "active" | "inactive",
  sortOrder: 0,
  imageUrl: "",
});

// 所有技师列表
const allStaff = ref<Staff[]>([
  { id: 1, name: "李师傅", title: "高级美容师" },
  { id: 2, name: "王医生", title: "宠物医师" },
  { id: 3, name: "张助理", title: "美容助理" },
  { id: 4, name: "赵技师", title: "高级技师" },
]);

const serviceFormRef = ref();

// 表单验证规则
const serviceRules = {
  name: [
    { required: true, message: "请输入服务名称", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "服务名称长度在 2 到 50 个字符",
      trigger: "blur",
    },
  ],
  code: [
    { required: true, message: "请输入服务编码", trigger: "blur" },
    {
      pattern: /^[A-Z0-9-_]+$/,
      message: "服务编码只能包含字母、数字、下划线和连字符",
      trigger: "blur",
    },
  ],
  category: [{ required: true, message: "请选择服务分类", trigger: "change" }],
  price: [
    { required: true, message: "请输入服务价格", trigger: "blur" },
    { type: "number", min: 0, message: "价格必须大于等于0", trigger: "blur" },
  ],
  duration: [
    { required: true, message: "请输入服务时长", trigger: "blur" },
    { type: "number", min: 15, message: "服务时长至少15分钟", trigger: "blur" },
  ],
  requiredSkills: [
    {
      required: true,
      message: "请至少选择一项所需技能",
      trigger: "change",
      type: "array",
    },
  ],
  description: [
    { required: true, message: "请输入服务描述", trigger: "blur" },
    {
      min: 10,
      max: 500,
      message: "服务描述长度在 10 到 500 个字符",
      trigger: "blur",
    },
  ],
};

// 方法
const loadServiceList = async () => {
  loading.value = true;
  try {
    const params: ServiceItemQueryParams = {
      current: pagination.pageNum,
      size: pagination.pageSize,
      serviceName: filterForm.name || undefined,
      category: filterForm.category || undefined,
      status: filterForm.status
        ? filterForm.status === "active"
          ? 1
          : 0
        : undefined,
      requiredSkill: filterForm.requiredSkill || undefined,
    };

    // 1. 发起请求
    const response =
      await appointmentApi.getServiceItems<ServiceListResponse>(params);
    console.log("真实API响应:", response); // 调试用

    // 2. 兼容两种响应格式（优先外层，再内层）
    let records: AppointmentServiceItem[] = [];
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
    serviceList.value = records.map((service) => ({
      id: service.id || 0,
      name: service.serviceName || "",
      code: service.serviceCode || "",
      category:
        (service.category as "grooming" | "medical" | "other") || "other",
      description: service.description || "",
      price: service.price || 0,
      memberPrice: (service.price || 0) * 0.9,
      duration: service.duration ?? service.standardDuration ?? 0,
      requiredSkills: [], // API中没有此字段
      availableStaff: [], // API中没有此字段
      advanceBookingHours: 24, // API中没有此字段，暂时固定
      suitablePets: [], // API中没有此字段
      notes: "", // API中没有此字段
      status: service.status === 1 ? "active" : "inactive",
      sortOrder: 0, // API中没有此字段
      popularity: 0, // API中没有此字段
      imageUrl: service.imageUrl || "",
      createdTime: service.createTime || "",
    }));

    console.log("处理后的数据:", serviceList.value); // 调试用

    // 计算概览数据
    calculateOverview();
  } catch (error) {
    console.error("加载服务项目列表失败:", error);
    ElMessage.error("加载服务项目列表失败");
    serviceList.value = [];
  } finally {
    loading.value = false;
  }
};

const calculateOverview = () => {
  overview.totalServices = serviceList.value.length;
  overview.activeServices = serviceList.value.filter(
    (service) => service.status === "active",
  ).length;

  const activeServices = serviceList.value.filter(
    (service) => service.status === "active",
  );
  if (activeServices.length > 0) {
    overview.avgDuration = Math.round(
      activeServices.reduce((sum, service) => sum + service.duration, 0) /
        activeServices.length,
    );
    overview.avgPrice = Math.round(
      activeServices.reduce((sum, service) => sum + service.price, 0) /
        activeServices.length,
    );
  }
};

const handleSearch = () => {
  pagination.pageNum = 1;
  loadServiceList();
};

const handleReset = () => {
  Object.keys(filterForm).forEach((key) => {
    filterForm[key] = "";
  });
  pagination.pageNum = 1;
  loadServiceList();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadServiceList();
};

const handleSelectionChange = (selection: Service[]) => {
  console.log("选中的服务项目:", selection);
};

const showAddDialog = () => {
  dialogTitle.value = "新增服务项目";
  isEdit.value = false;
  resetServiceForm();
  serviceDialogVisible.value = true;
};

const editService = (service: Service) => {
  dialogTitle.value = "编辑服务项目";
  isEdit.value = true;
  Object.assign(serviceForm, service);
  serviceDialogVisible.value = true;
};

const resetServiceForm = () => {
  serviceForm.id = 0;
  serviceForm.name = "";
  serviceForm.code = "";
  serviceForm.category = "grooming";
  serviceForm.description = "";
  serviceForm.price = 0;
  serviceForm.memberPrice = 0;
  serviceForm.duration = 60;
  serviceForm.requiredSkills = [];
  serviceForm.availableStaff = [];
  serviceForm.advanceBookingHours = 24;
  serviceForm.suitablePets = [];
  serviceForm.notes = "";
  serviceForm.status = "active";
  serviceForm.sortOrder = 0;
  serviceForm.imageUrl = "";
};

const handleSubmitService = async () => {
  if (!serviceFormRef.value) return;

  try {
    await serviceFormRef.value.validate();

    submitLoading.value = true;

    const serviceData: AppointmentServiceItem = {
      serviceName: serviceForm.name,
      serviceCode: serviceForm.code,
      category: serviceForm.category,
      description: serviceForm.description,
      price: serviceForm.price,
      duration: serviceForm.duration,
      standardDuration: serviceForm.duration,
      status: serviceForm.status === "active" ? 1 : 0,
      imageUrl: serviceForm.imageUrl,
    };

    if (isEdit.value) {
      await appointmentApi.updateServiceItem(serviceForm.id, serviceData);
      ElMessage.success("服务项目更新成功");
    } else {
      await appointmentApi.createServiceItem(serviceData);
      ElMessage.success("服务项目添加成功");
    }

    serviceDialogVisible.value = false;
    loadServiceList();
  } catch (error) {
    console.error("提交失败:", error);
    ElMessage.error("提交失败");
  } finally {
    submitLoading.value = false;
  }
};

const viewDetail = (service: Service) => {
  currentService.value = service;
  detailDialogVisible.value = true;
};

const handleDeleteService = async (service: Service) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除服务项目"${service.name}"吗？此操作不可撤销。`,
      "确认删除",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );
    await appointmentApi.deleteServiceItem(service.id);
    ElMessage.success("服务项目删除成功");
    loadServiceList();
  } catch (e) {
    return;
  }
};

const getCategoryType = (category: string) => {
  switch (category) {
    case "grooming":
      return "primary";
    case "medical":
      return "danger";
    case "other":
      return "info";
    default:
      return "info";
  }
};

const getCategoryText = (category: string) => {
  switch (category) {
    case "grooming":
      return "美容服务";
    case "medical":
      return "医疗服务";
    case "other":
      return "其他服务";
    default:
      return category;
  }
};

onMounted(() => {
  loadServiceList();
});
</script>

<style scoped lang="scss">
.service-list {
  .service-overview {
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

  .price {
    color: #f56c6c;
    font-weight: 500;
  }

  .skills-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
  }

  .popularity {
    display: flex;
    align-items: center;
    gap: 8px;

    .popularity-text {
      font-size: 12px;
      color: #86909c;
      min-width: 35px;
    }
  }

  .upload-tip {
    font-size: 12px;
    color: #86909c;
    margin-top: 8px;
  }
}
</style>
