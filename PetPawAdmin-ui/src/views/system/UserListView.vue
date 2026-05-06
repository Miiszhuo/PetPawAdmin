<template>
  <div class="user-manage">
    <PageHeader title="用户管理" description="管理系统用户、分配角色和权限">
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
      </template>
    </PageHeader>

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="用户名">
          <el-input
            v-model="filterForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input
            v-model="filterForm.realName"
            placeholder="请输入真实姓名"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="filterForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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
      :data="userList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
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
      <template #column-status="{ row }">
        <el-switch
          v-model="row.status"
          :active-value="1"
          :inactive-value="0"
          @change="handleStatusChange(row)"
        />
      </template>
      <template #column-actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">查看</el-button>
        <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
        <el-button
          size="small"
          type="success"
          @click="showAssignRoleDialog(row)"
          >分配角色</el-button
        >
        <el-popconfirm
          title="确定要删除该用户吗？"
          @confirm="handleDelete(row)"
        >
          <template #reference>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-popconfirm>
        <el-button size="small" type="warning" @click="handleResetPassword(row)"
          >重置密码</el-button
        >
      </template>
    </DataTable>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="用户详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{
          currentUser.username
        }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{
          currentUser.realName
        }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{
          currentUser.email
        }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{
          currentUser.phone
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? "启用" : "禁用" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          currentUser.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="最后登录时间">{{
          currentUser.lastLoginTime || "暂无"
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 用户编辑/新增对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="头像">
          <ImageUpload v-model="userForm.imageUrl" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="userForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
            <el-radio :label="0">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">正常</el-radio>
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

    <!-- 分配角色对话框 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-checkbox-group v-model="selectedRoles">
        <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">
          {{ role.roleName }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="assignRoleLoading"
            @click="handleAssignRole"
            >确定</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Search, Refresh, Picture } from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import ImageUpload from "@/components/common/ImageUpload.vue";
import { systemApi } from "@/api";
import type { SysUser, SysRole } from "@/api/system";

const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const userList = ref<SysUser[]>([]);
const currentUser = ref<Partial<SysUser>>({});
const detailDialogVisible = ref(false);
const total = ref(0);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

const filterForm = reactive({
  username: "",
  realName: "",
  status: undefined as number | undefined,
});

const userForm = reactive({
  id: undefined as number | undefined,
  username: "",
  realName: "",
  password: "",
  email: "",
  phone: "",
  gender: 1,
  status: 1,
  imageUrl: "",
});

const userFormRef = ref();
const userRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  realName: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const columns = [
  { prop: "imageUrl", label: "头像", width: 80 },
  { prop: "username", label: "用户名" },
  { prop: "realName", label: "真实姓名" },
  { prop: "email", label: "邮箱" },
  { prop: "phone", label: "手机号" },
  { prop: "status", label: "状态", slot: "status" },
  { prop: "createTime", label: "创建时间" },
];

// 角色分配相关
const roleDialogVisible = ref(false);
const roleList = ref<SysRole[]>([]);
const selectedRoles = ref<number[]>([]);
const currentUserId = ref<number | undefined>(undefined);
const assignRoleLoading = ref(false);

const loadData = async () => {
  loading.value = true;
  try {
    const res = await systemApi.getUsers({
      current: pagination.pageNum,
      size: pagination.pageSize,
      ...filterForm,
    });
    if (res.code === 200) {
      userList.value = res.data.records;
      console.log("加载的用户列表:", userList.value);

      // 检查ID是否存在
      const missingIds = userList.value.filter(
        (u) => u.id === undefined || u.id === null,
      );
      if (missingIds.length > 0) {
        console.error("严重警告：发现缺失ID的用户记录", missingIds);
        ElMessage.warning(
          `发现 ${missingIds.length} 条用户数据缺失ID，可能会影响操作`,
        );
      }

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
  filterForm.username = "";
  filterForm.realName = "";
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
  userForm.id = undefined;
  userForm.username = "";
  userForm.realName = "";
  userForm.password = "";
  userForm.email = "";
  userForm.phone = "";
  userForm.gender = 1;
  userForm.status = 1;
  userForm.imageUrl = "";
  dialogVisible.value = true;
};

const showEditDialog = (row: SysUser) => {
  isEdit.value = true;
  Object.assign(userForm, row);
  userForm.password = ""; // 编辑时不回显密码
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!userFormRef.value) return;
  await userFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true;
      try {
        if (isEdit.value) {
          await systemApi.updateUser(userForm.id!, userForm);
          ElMessage.success("更新成功");
        } else {
          await systemApi.createUser(userForm);
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

const handleDelete = async (row: SysUser) => {
  try {
    await systemApi.deleteUser(row.id!);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    console.error(error);
  }
};

const handleStatusChange = async (row: SysUser) => {
  console.log("状态变更行数据:", row);
  try {
    if (!row.id) {
      console.error("用户ID不存在", row);
      ElMessage.error("用户ID不存在，无法修改状态");
      row.status = row.status === 1 ? 0 : 1; // 恢复状态
      return;
    }
    await systemApi.changeUserStatus(row.id, row.status);
    ElMessage.success("状态更新成功");
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1; // 恢复状态
    console.error(error);
  }
};

const handleResetPassword = async (row: SysUser) => {
  try {
    await ElMessageBox.prompt("请输入新密码", "重置密码", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      inputPattern: /^.{6,}$/,
      inputErrorMessage: "密码长度至少6位",
    }).then(async ({ value }) => {
      await systemApi.resetPassword(row.id!, value);
      ElMessage.success("密码重置成功");
    });
  } catch {
    // 取消操作
  }
};

// 角色分配逻辑
const showAssignRoleDialog = async (row: SysUser) => {
  currentUserId.value = row.id;
  roleDialogVisible.value = true;
  // 加载所有角色
  try {
    const res = await systemApi.getRoles({ current: 1, size: 100 });
    if (res.code === 200) {
      roleList.value = res.data.records;
    }
    // 加载用户当前角色
    const userRolesRes = await systemApi.getUserRoles(row.id!);
    if (userRolesRes.code === 200) {
      selectedRoles.value = userRolesRes.data.map((r: SysRole) => r.id!);
    }
  } catch (error) {
    console.error(error);
  }
};

const handleAssignRole = async () => {
  if (!currentUserId.value) return;
  assignRoleLoading.value = true;
  try {
    await systemApi.assignRolesToUser(currentUserId.value, selectedRoles.value);
    ElMessage.success("角色分配成功");
    roleDialogVisible.value = false;
  } catch (error) {
    console.error(error);
  } finally {
    assignRoleLoading.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
.user-manage {
  .filter-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }
}
</style>
