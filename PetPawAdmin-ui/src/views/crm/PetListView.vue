<template>
  <div class="pet-list">
    <PageHeader title="宠物档案" description="管理所有宠物的信息档案">
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增宠物
        </el-button>
      </template>
    </PageHeader>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="宠物昵称">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入宠物昵称"
            clearable
            style="width: 120px"
          />
        </el-form-item>

        <el-form-item label="主人姓名">
          <el-input
            v-model="filterForm.ownerName"
            placeholder="请输入主人姓名"
            clearable
            style="width: 130px"
          />
        </el-form-item>

        <el-form-item label="品种">
          <el-select
            v-model="filterForm.breed"
            placeholder="请选择品种"
            clearable
            filterable
            style="width: 120px"
          >
            <el-option
              v-for="breed in petBreeds"
              :key="breed"
              :label="breed"
              :value="breed"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select
            v-model="filterForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 100px"
          >
            <el-option label="正常" value="active" />
            <el-option label="就诊中" value="in_treatment" />
            <el-option label="已离世" value="deceased" />
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

    <!-- 数据表格 -->
    <DataTable
      :data="petList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      show-selection
      show-actions
      @selection-change="handleSelectionChange"
      @page-change="handlePageChange"
    >
      <template #column-gender="{ row }">
        <el-tag :type="row.gender === 'male' ? 'primary' : 'danger'">
          {{ row.gender === "male" ? "♂ 公" : "♀ 母" }}
        </el-tag>
      </template>

      <template #column-breed="{ row }">
        <span>{{ row.breed }}</span>
      </template>

      <template #column-age="{ row }">
        <span>{{ calculateAge(row.birthday) }}</span>
      </template>

      <template #column-status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>

      <template #column-actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">详情</el-button>
        <el-button size="small" type="primary" @click="editPet(row)"
          >编辑</el-button
        >
      </template>
    </DataTable>

    <!-- 新增/编辑宠物对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="petFormRef"
        :model="petForm"
        :rules="petRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="宠物昵称" prop="name">
              <el-input v-model="petForm.name" placeholder="请输入宠物昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属客户" prop="customerId">
              <el-select
                v-model="petForm.customerId"
                placeholder="请选择所属客户"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="customer in customers"
                  :key="customer.id"
                  :label="`${customer.customerName} (${customer.phone})`"
                  :value="customer.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品种" prop="breed">
              <el-select
                v-model="petForm.breed"
                placeholder="请选择品种"
                filterable
                allow-create
                default-first-option
              >
                <el-option
                  v-for="breed in petBreeds"
                  :key="breed"
                  :label="breed"
                  :value="breed"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="petForm.gender">
                <el-radio label="male">公</el-radio>
                <el-radio label="female">母</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期">
              <el-date-picker
                v-model="petForm.birthday"
                type="date"
                placeholder="选择出生日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)">
              <el-input-number
                v-model="petForm.weight"
                :precision="2"
                :min="0"
                :max="100"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血型">
              <el-select v-model="petForm.bloodType" placeholder="请选择血型">
                <el-option label="DEA 1.1" value="DEA1.1" />
                <el-option label="DEA 1.2" value="DEA1.2" />
                <el-option label="DEA 3" value="DEA3" />
                <el-option label="DEA 4" value="DEA4" />
                <el-option label="DEA 5" value="DEA5" />
                <el-option label="DEA 6" value="DEA6" />
                <el-option label="DEA 7" value="DEA7" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="绝育状态">
              <el-radio-group v-model="petForm.neutered">
                <el-radio :label="true">已绝育</el-radio>
                <el-radio :label="false">未绝育</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="过敏源">
          <el-input
            v-model="petForm.allergies"
            placeholder="请输入过敏源，如：鸡肉、鱼类等"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="petForm.remark"
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
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import { crmApi } from "@/api";
import type { PageParams } from "@/types/common";
import type { CrmPet, PetQueryParams } from "@/api/crm";

// 表格列配置
const columns = [
  { prop: "name", label: "宠物昵称", width: 120 },
  { prop: "ownerName", label: "主人姓名", width: 120 },
  { prop: "breed", label: "品种", width: 120 },
  { prop: "gender", label: "性别", width: 80 },
  { prop: "age", label: "年龄", width: 80 },
  { prop: "weight", label: "体重(kg)", width: 100 },
  { prop: "status", label: "状态", width: 100 },
];

// 宠物数据接口
interface Pet {
  id: number;
  name: string;
  ownerName: string;
  customerId?: number;
  breed: string;
  gender: "male" | "female";
  birthday: string;
  weight: number;
  bloodType?: string;
  neutered: boolean;
  allergies?: string;
  remark?: string;
  status: "active" | "in_treatment" | "deceased";
  lastVisitTime?: string;
}

// 宠物品种列表
const petBreeds = [
  "金毛犬",
  "拉布拉多",
  "哈士奇",
  "泰迪犬",
  "比熊犬",
  "边境牧羊犬",
  "波斯猫",
  "英短",
  "美短",
  "布偶猫",
  "暹罗猫",
  "缅因猫",
];

// 响应式数据
const router = useRouter();
const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref("新增宠物");
const isEdit = ref(false);

// 客户列表
const customers = ref<CrmCustomer[]>([]);

// 宠物列表
const petList = ref<Pet[]>([]);
const total = ref(0);
const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

// 筛选表单
const filterForm = reactive({
  name: "",
  ownerName: "",
  breed: "",
  status: "",
});

// 宠物表单
const petForm = reactive({
  id: 0,
  name: "",
  customerId: "",
  breed: "",
  gender: "male" as "male" | "female",
  birthday: "",
  weight: 0,
  bloodType: "",
  neutered: false,
  allergies: "",
  remark: "",
  status: "active" as "active" | "in_treatment" | "deceased",
});

const petFormRef = ref<FormInstance>();

// 表单验证规则
const petRules: FormRules = {
  name: [
    { required: true, message: "请输入宠物昵称", trigger: "blur" },
    { min: 1, max: 20, message: "昵称长度在 1 到 20 个字符", trigger: "blur" },
  ],
  customerId: [
    { required: true, message: "请选择所属客户", trigger: "change" },
  ],
  breed: [{ required: true, message: "请选择品种", trigger: "change" }],
};

// 方法
// 加载客户列表
const loadCustomers = async () => {
  try {
    const response = await crmApi.getCustomers({ current: 1, size: 1000 });
    if (Array.isArray(response.records)) {
      customers.value = response.records;
    } else if (response.data?.data?.records) {
      customers.value = response.data.data.records;
    } else if (response.data?.records) {
      customers.value = response.data.records;
    }
  } catch (error) {
    console.error("加载客户列表失败:", error);
  }
};

// 统一类型定义（只保留一份）
interface BackendPetRecord {
  id: number;
  petName?: string;
  pet_name?: string;
  breed?: string;
  gender?: number;
  birthday?: string;
  weight?: number;
  sterilization?: number;
  healthStatus?: string;
  createTime?: string;
  create_time?: string;
  customerId?: number;
}

interface PetListResponse {
  code: number;
  total?: number;
  records?: BackendPetRecord[];
  data?: {
    records?: BackendPetRecord[];
    total?: number;
  };
  message?: string;
}

// 修复核心：加载宠物列表方法
const loadPetList = async () => {
  loading.value = true;
  try {
    const params: PetQueryParams = {
      current: pagination.pageNum,
      size: pagination.pageSize,
      petName: filterForm.name || undefined,
      breed: filterForm.breed || undefined,
      ownerName: filterForm.ownerName || undefined,
      status: filterForm.status || undefined,
    };

    // 1. 发起请求
    const response = await crmApi.getPets<PetListResponse>(params);
    console.log("真实API响应:", response); // 调试用

    // 2. 兼容两种响应格式（优先外层，再内层）
    let records: BackendPetRecord[] = [];
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
    petList.value = records.map((pet) => {
      // 查找对应的客户信息
      const customer = customers.value.find((c) => c.id === pet.customerId);
      return {
        id: pet.id || 0,
        name: pet.petName || pet.pet_name || "未知宠物",
        ownerName: customer ? customer.customerName : "未知主人",
        customerId: pet.customerId,
        breed: pet.breed || "",
        gender: pet.gender === 1 ? "male" : "female",
        birthday: pet.birthday || "",
        weight: pet.weight || 0,
        bloodType: "", // API中没有此字段
        neutered: pet.sterilization === 1,
        allergies: pet.healthStatus || "",
        status: "active", // 暂时默认为active，后续可根据业务逻辑调整
        lastVisitTime:
          pet.lastVisitTime || pet.createTime || pet.create_time || "",
      };
    });

    console.log("处理后的数据:", petList.value); // 调试用
  } catch (error) {
    console.error("加载宠物列表失败:", error);
    ElMessage.error("加载宠物列表失败");
    petList.value = [];
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.pageNum = 1;
  loadPetList();
};

const handleReset = () => {
  Object.keys(filterForm).forEach((key) => {
    filterForm[key] = "";
  });
  pagination.pageNum = 1;
  loadPetList();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadPetList();
};

const handleSelectionChange = (selection: Pet[]) => {
  console.log("选中的宠物:", selection);
};

const showAddDialog = () => {
  dialogTitle.value = "新增宠物";
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const editPet = (pet: Pet) => {
  dialogTitle.value = "编辑宠物";
  isEdit.value = true;
  Object.assign(petForm, {
    id: pet.id,
    name: pet.name,
    customerId: pet.customerId ? pet.customerId.toString() : "",
    breed: pet.breed,
    gender: pet.gender,
    birthday: pet.birthday,
    weight: pet.weight,
    bloodType: pet.bloodType || "",
    neutered: pet.neutered,
    allergies: pet.allergies || "",
    remark: pet.remark || "",
    status: pet.status,
  });
  dialogVisible.value = true;
};

const resetForm = () => {
  petForm.id = 0;
  petForm.name = "";
  petForm.customerId = "";
  petForm.breed = "";
  petForm.gender = "male";
  petForm.birthday = "";
  petForm.weight = 0;
  petForm.bloodType = "";
  petForm.neutered = false;
  petForm.allergies = "";
  petForm.remark = "";
  petForm.status = "active";
};

const handleSubmit = async () => {
  if (!petFormRef.value) return;

  try {
    await petFormRef.value.validate();

    submitLoading.value = true;

    const petData: CrmPet = {
      customerId: parseInt(petForm.customerId),
      petName: petForm.name,
      petType: petForm.breed,
      breed: petForm.breed,
      gender: petForm.gender === "male" ? 1 : 2,
      birthday: petForm.birthday,
      weight: petForm.weight,
      sterilization: petForm.neutered ? 1 : 0,
      healthStatus: petForm.allergies,
      remark: petForm.remark,
    };

    if (isEdit.value) {
      await crmApi.updatePet(petForm.id, petData);
      ElMessage.success("宠物信息更新成功");
    } else {
      await crmApi.createPet(petData);
      ElMessage.success("宠物添加成功");
    }

    dialogVisible.value = false;
    loadPetList();
  } catch (error) {
    console.error("提交失败:", error);
    ElMessage.error("提交失败");
  } finally {
    submitLoading.value = false;
  }
};

const viewDetail = (pet: Pet) => {
  currentPet.value = pet;
  detailDialogVisible.value = true;
};

const handleAction = async (command: string, pet: Pet) => {
  switch (command) {
    case "delete":
      try {
        await ElMessageBox.confirm(
          `确定要删除宠物"${pet.name}"吗？此操作不可撤销。`,
          "确认删除",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          },
        );
        await crmApi.deletePet(pet.id);
        ElMessage.success("宠物删除成功");
        loadPetList();
      } catch {
        // 用户取消操作
      }
      break;
    case "addRecord":
      ElMessage.info("添加健康记录功能开发中");
      break;
    case "viewRecords":
      currentPet.value = pet;
      detailDialogVisible.value = true;
      break;
    case "vaccination":
      ElMessage.info("疫苗提醒功能开发中");
      break;
  }
};

const calculateAge = (birthday: string) => {
  if (!birthday) return "-";
  const birth = new Date(birthday);
  const now = new Date();
  const ageInMs = now.getTime() - birth.getTime();
  const ageInYears = ageInMs / (1000 * 60 * 60 * 24 * 365.25);
  const years = Math.floor(ageInYears);
  const months = Math.floor((ageInYears - years) * 12);
  return `${years}岁${months}个月`;
};

const getStatusType = (status: string) => {
  switch (status) {
    case "active":
      return "success";
    case "in_treatment":
      return "warning";
    case "deceased":
      return "danger";
    default:
      return "info";
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case "active":
      return "正常";
    case "in_treatment":
      return "就诊中";
    case "deceased":
      return "已离世";
    default:
      return "正常";
  }
};

onMounted(() => {
  loadCustomers();
  loadPetList();
});
</script>

<style scoped lang="scss">
.pet-list {
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
</style>
