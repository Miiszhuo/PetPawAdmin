<template>
  <div class="employee-manage">
    <PageHeader title="员工管理" description="管理企业员工信息及职位">
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增员工
        </el-button>
      </template>
    </PageHeader>

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="姓名">
          <el-input
            v-model="filterForm.realName"
            placeholder="请输入姓名"
            clearable
          />
        </el-form-item>
        <el-form-item label="工号">
          <el-input
            v-model="filterForm.employeeNo"
            placeholder="请输入工号"
            clearable
          />
        </el-form-item>
        <el-form-item label="职位">
          <el-select
            v-model="filterForm.positionId"
            placeholder="请选择职位"
            clearable
          >
            <el-option
              v-for="item in positionOptions"
              :key="item.id"
              :label="item.positionName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="filterForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="2" />
            <el-option label="休假" :value="3" />
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

    <DataTable
      :data="employeeList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      @page-change="handlePageChange"
    >
      <template #column-status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>
      <template #column-actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">查看</el-button>
        <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
        <el-popconfirm
          title="确定要删除该员工吗？"
          @confirm="handleDelete(row)"
        >
          <template #reference>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </DataTable>

    <!-- 员工详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="员工详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工号">{{
          currentEmployee.employeeNo
        }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{
          currentEmployee.realName
        }}</el-descriptions-item>
        <el-descriptions-item label="职位">{{
          currentEmployee.positionName
        }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{
          currentEmployee.gender === 1
            ? "男"
            : currentEmployee.gender === 2
              ? "女"
              : "未知"
        }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{
          currentEmployee.phone
        }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{
          currentEmployee.email
        }}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{
          currentEmployee.entryDate
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentEmployee.status)">
            {{ getStatusText(currentEmployee.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          currentEmployee.createTime
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 员工编辑/新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑员工' : '新增员工'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="employeeFormRef"
        :model="employeeForm"
        :rules="employeeRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号" prop="employeeNo">
              <el-input v-model="employeeForm.employeeNo" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="employeeForm.realName" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位" prop="positionId">
              <el-select
                v-model="employeeForm.positionId"
                placeholder="请选择职位"
                style="width: 100%"
              >
                <el-option
                  v-for="item in positionOptions"
                  :key="item.id"
                  :label="item.positionName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker
                v-model="employeeForm.entryDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="employeeForm.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="employeeForm.email" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="性别">
          <el-radio-group v-model="employeeForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
            <el-radio :label="0">未知</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="employeeForm.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="2">离职</el-radio>
            <el-radio :label="3">休假</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
            >确定</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Search, Refresh } from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import { hrApi } from "@/api/hr";
import type { HrEmployee, HrPosition } from "@/api/hr";

const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const employeeList = ref<HrEmployee[]>([]);
const currentEmployee = ref<Partial<HrEmployee>>({});
const positionOptions = ref<HrPosition[]>([]);
const total = ref(0);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

const filterForm = reactive({
  realName: "",
  employeeNo: "",
  positionId: undefined as number | undefined,
  status: undefined as number | undefined,
});

const employeeForm = reactive({
  id: undefined as number | undefined,
  employeeNo: "",
  realName: "",
  gender: 1,
  phone: "",
  email: "",
  positionId: undefined as number | undefined,
  entryDate: "",
  status: 1,
});

const viewDetail = (row: HrEmployee) => {
  currentEmployee.value = { ...row };
  detailDialogVisible.value = true;
};

const employeeFormRef = ref();
const employeeRules = {
  employeeNo: [{ required: true, message: "请输入工号", trigger: "blur" }],
  realName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  positionId: [{ required: true, message: "请选择职位", trigger: "change" }],
};

const columns = [
  { prop: "employeeNo", label: "工号", width: "100" },
  { prop: "realName", label: "姓名", width: "120" },
  { prop: "positionName", label: "职位", width: "120" },
  { prop: "phone", label: "手机号", width: "120" },
  { prop: "entryDate", label: "入职日期", width: "120" },
  { prop: "status", label: "状态", slot: "status", width: "100" },
  { prop: "createTime", label: "创建时间" },
];

const loadPositions = async () => {
  try {
    const res = await hrApi.getAllPositions();
    if (res.code === 200) {
      positionOptions.value = res.data;
    }
  } catch (error) {
    console.error(error);
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    const res = await hrApi.getEmployees({
      current: pagination.pageNum,
      size: pagination.pageSize,
      ...filterForm,
    });
    if (res.code === 200) {
      employeeList.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.pageNum = 1;
  loadData();
};

const handleReset = () => {
  filterForm.realName = "";
  filterForm.employeeNo = "";
  filterForm.positionId = undefined;
  filterForm.status = undefined;
  handleSearch();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadData();
};

const showAddDialog = () => {
  isEdit.value = false;
  employeeForm.id = undefined;
  employeeForm.employeeNo = "";
  employeeForm.realName = "";
  employeeForm.gender = 1;
  employeeForm.phone = "";
  employeeForm.email = "";
  employeeForm.positionId = undefined;
  employeeForm.entryDate = "";
  employeeForm.status = 1;
  dialogVisible.value = true;
};

const showEditDialog = (row: HrEmployee) => {
  isEdit.value = true;
  Object.assign(employeeForm, row);
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!employeeFormRef.value) return;
  await employeeFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true;
      try {
        if (isEdit.value) {
          await hrApi.updateEmployee(employeeForm.id!, employeeForm);
          ElMessage.success("更新成功");
        } else {
          await hrApi.createEmployee(employeeForm);
          ElMessage.success("创建成功");
        }
        dialogVisible.value = false;
        loadData();
      } catch (error) {
        console.error(error);
      } finally {
        submitLoading.value = false;
      }
    }
  });
};

const handleDelete = async (row: HrEmployee) => {
  try {
    await hrApi.deleteEmployee(row.id!);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    console.error(error);
  }
};

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: "success",
    2: "info",
    3: "warning",
  };
  return map[status] || "info";
};

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    1: "在职",
    2: "离职",
    3: "休假",
  };
  return map[status] || "未知";
};

onMounted(() => {
  loadPositions();
  loadData();
});
</script>

<style scoped lang="scss">
.employee-manage {
  .filter-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }
}
</style>
