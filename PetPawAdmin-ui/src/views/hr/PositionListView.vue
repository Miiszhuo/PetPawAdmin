<template>
  <div class="position-manage">
    <PageHeader title="职位管理" description="管理企业职位及职责定义">
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增职位
        </el-button>
      </template>
    </PageHeader>

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="职位名称">
          <el-input
            v-model="filterForm.positionName"
            placeholder="请输入职位名称"
            clearable
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

    <DataTable
      :data="positionList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      @page-change="handlePageChange"
    >
      <template #column-status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">
          {{ row.status === 1 ? "启用" : "禁用" }}
        </el-tag>
      </template>
      <template #column-actions="{ row }">
        <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
        <el-popconfirm
          title="确定要删除该职位吗？"
          @confirm="handleDelete(row)"
        >
          <template #reference>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </DataTable>

    <!-- 职位编辑/新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑职位' : '新增职位'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="positionFormRef"
        :model="positionForm"
        :rules="positionRules"
        label-width="100px"
      >
        <el-form-item label="职位名称" prop="positionName">
          <el-input
            v-model="positionForm.positionName"
            placeholder="如：美容师"
          />
        </el-form-item>
        <el-form-item label="职位编码" prop="positionCode">
          <el-input
            v-model="positionForm.positionCode"
            placeholder="如：GROOMER"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="positionForm.description"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="positionForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
import type { HrPosition } from "@/api/hr";

const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const positionList = ref<HrPosition[]>([]);
const total = ref(0);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

const filterForm = reactive({
  positionName: "",
});

const positionForm = reactive({
  id: undefined as number | undefined,
  positionName: "",
  positionCode: "",
  description: "",
  status: 1,
});

const viewDetail = (row: HrPosition) => {
  currentPosition.value = { ...row };
  detailDialogVisible.value = true;
};

const positionFormRef = ref();
const positionRules = {
  positionName: [
    { required: true, message: "请输入职位名称", trigger: "blur" },
  ],
  positionCode: [
    { required: true, message: "请输入职位编码", trigger: "blur" },
  ],
};

const columns = [
  { prop: "positionName", label: "职位名称", width: "150" },
  { prop: "positionCode", label: "职位编码", width: "150" },
  { prop: "description", label: "描述" },
  { prop: "status", label: "状态", slot: "status", width: "100" },
  { prop: "createTime", label: "创建时间", width: "180" },
];

const loadData = async () => {
  loading.value = true;
  try {
    const res = await hrApi.getPositions({
      current: pagination.pageNum,
      size: pagination.pageSize,
      ...filterForm,
    });
    if (res.code === 200) {
      positionList.value = res.data.records;
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
  filterForm.positionName = "";
  handleSearch();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadData();
};

const showAddDialog = () => {
  isEdit.value = false;
  positionForm.id = undefined;
  positionForm.positionName = "";
  positionForm.positionCode = "";
  positionForm.description = "";
  positionForm.status = 1;
  dialogVisible.value = true;
};

const showEditDialog = (row: HrPosition) => {
  isEdit.value = true;
  Object.assign(positionForm, row);
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!positionFormRef.value) return;
  await positionFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true;
      try {
        if (isEdit.value) {
          await hrApi.updatePosition(positionForm.id!, positionForm);
          ElMessage.success("更新成功");
        } else {
          await hrApi.createPosition(positionForm);
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

const handleDelete = async (row: HrPosition) => {
  try {
    await hrApi.deletePosition(row.id!);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
.position-manage {
  .filter-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }
}
</style>
