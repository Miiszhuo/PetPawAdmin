<template>
  <div class="role-manage">
    <PageHeader title="角色管理" description="管理系统角色及权限分配">
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
      </template>
    </PageHeader>

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="角色名称">
          <el-input
            v-model="filterForm.roleName"
            placeholder="请输入角色名称"
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
      :data="roleList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      @page-change="handlePageChange"
    >
      <template #column-status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
          {{ row.status === 1 ? "启用" : "禁用" }}
        </el-tag>
      </template>
      <template #column-actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">查看</el-button>
        <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
        <el-button
          size="small"
          type="success"
          @click="showAssignPermDialog(row)"
          >分配权限</el-button
        >
        <el-popconfirm
          title="确定要删除该角色吗？"
          @confirm="handleDelete(row)"
        >
          <template #reference>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </DataTable>

    <!-- 角色详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="角色详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="角色名称">{{
          currentRole.roleName
        }}</el-descriptions-item>
        <el-descriptions-item label="角色编码">{{
          currentRole.roleCode
        }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{
          currentRole.description
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRole.status === 1 ? 'success' : 'danger'">
            {{ currentRole.status === 1 ? "启用" : "禁用" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          currentRole.createTime
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 角色编辑/新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑角色' : '新增角色'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="roleForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="roleForm.status">
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

    <!-- 分配权限对话框 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="600px">
      <el-tree
        ref="permTreeRef"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :props="{ label: 'permissionName', children: 'children' }"
        default-expand-all
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="assignPermLoading"
            @click="handleAssignPerm"
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
import { systemApi } from "@/api";
import type { SysRole, SysPermission } from "@/api/system";

const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const isEdit = ref(false);
const roleList = ref<SysRole[]>([]);
const total = ref(0);
const currentRole = ref<SysRole>({} as SysRole);

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

const filterForm = reactive({
  roleName: "",
});

const roleForm = reactive({
  id: undefined as number | undefined,
  roleName: "",
  roleCode: "",
  description: "",
  status: 1,
});

const viewDetail = (row: SysRole) => {
  currentRole.value = { ...row };
  detailDialogVisible.value = true;
};

const roleFormRef = ref();
const roleRules = {
  roleName: [{ required: true, message: "请输入角色名称", trigger: "blur" }],
  roleCode: [{ required: true, message: "请输入角色编码", trigger: "blur" }],
};

const columns = [
  { prop: "roleName", label: "角色名称" },
  { prop: "roleCode", label: "角色编码" },
  { prop: "description", label: "描述" },
  { prop: "status", label: "状态", slot: "status" },
  { prop: "createTime", label: "创建时间" },
];

// 权限分配相关
const permDialogVisible = ref(false);
const permissionTree = ref<SysPermission[]>([]);
const permTreeRef = ref();
const currentRoleId = ref<number | undefined>(undefined);
const assignPermLoading = ref(false);

const loadData = async () => {
  loading.value = true;
  try {
    const res = await systemApi.getRoles({
      current: pagination.pageNum,
      size: pagination.pageSize,
      roleName: filterForm.roleName,
    });
    if (res.code === 200) {
      roleList.value = res.data.records;
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
  filterForm.roleName = "";
  handleSearch();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadData();
};

const showAddDialog = () => {
  isEdit.value = false;
  roleForm.id = undefined;
  roleForm.roleName = "";
  roleForm.roleCode = "";
  roleForm.description = "";
  roleForm.status = 1;
  dialogVisible.value = true;
};

const showEditDialog = (row: SysRole) => {
  isEdit.value = true;
  Object.assign(roleForm, row);
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!roleFormRef.value) return;
  await roleFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true;
      try {
        if (isEdit.value) {
          await systemApi.updateRole(roleForm.id!, roleForm);
          ElMessage.success("更新成功");
        } else {
          await systemApi.createRole(roleForm);
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

const handleDelete = async (row: SysRole) => {
  try {
    await systemApi.deleteRole(row.id!);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    console.error(error);
  }
};

// 权限分配逻辑
const showAssignPermDialog = async (row: SysRole) => {
  currentRoleId.value = row.id;
  permDialogVisible.value = true;
  // 加载所有权限（树形）
  try {
    const res = await systemApi.getPermissionTree(); // 已修正为 getPermissionTree
    if (res.code === 200) {
      permissionTree.value = res.data;
    }
  } catch (error) {
    console.error(error);
  }
};

const handleAssignPerm = async () => {
  if (!currentRoleId.value || !permTreeRef.value) return;
  const checkedKeys = permTreeRef.value.getCheckedKeys();
  const halfCheckedKeys = permTreeRef.value.getHalfCheckedKeys();
  const allKeys = [...checkedKeys, ...halfCheckedKeys];

  assignPermLoading.value = true;
  try {
    await systemApi.assignPermissionsToRole(currentRoleId.value, allKeys);
    ElMessage.success("权限分配成功");
    permDialogVisible.value = false;
  } catch (error) {
    console.error(error);
  } finally {
    assignPermLoading.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
.role-manage {
  .filter-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }
}
</style>
