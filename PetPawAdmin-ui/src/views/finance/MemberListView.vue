<template>
  <div class="member-list">
    <PageHeader title="会员管理" description="管理会员信息、会员卡和积分">
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增会员
        </el-button>
        <el-button @click="exportMembers">
          <el-icon><Download /></el-icon>
          导出会员
        </el-button>
        <el-button @click="batchRecharge">
          <el-icon><Coin /></el-icon>
          批量充值
        </el-button>
      </template>
    </PageHeader>

    <!-- 会员概览 -->
    <div class="member-overview">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#409eff"><User /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.totalMembers }}</div>
              <div class="card-label">总会员数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#67c23a"><Coin /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">
                ¥{{ overview.totalBalance.toLocaleString() }}
              </div>
              <div class="card-label">会员总余额</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="card-icon">
            <el-icon size="32" color="#e6a23c"><Star /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">
              {{ overview.totalPoints.toLocaleString() }}
            </div>
            <div class="card-label">总积分</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#f56c6c"><TrendCharts /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.newMembersToday }}</div>
              <div class="card-label">今日新增</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="会员姓名">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入会员姓名"
            clearable
            style="width: 120px"
          />
        </el-form-item>

        <el-form-item label="联系电话">
          <el-input
            v-model="filterForm.phone"
            placeholder="请输入联系电话"
            clearable
            style="width: 130px"
          />
        </el-form-item>

        <el-form-item label="会员等级">
          <el-select
            v-model="filterForm.level"
            placeholder="请选择等级"
            clearable
            style="width: 100px"
          >
            <el-option label="钻石会员" value="diamond" />
            <el-option label="VIP会员" value="vip" />
            <el-option label="普通会员" value="normal" />
          </el-select>
        </el-form-item>

        <el-form-item label="会员状态">
          <el-select
            v-model="filterForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 100px"
          >
            <el-option label="正常" value="active" />
            <el-option label="停用" value="inactive" />
            <el-option label="过期" value="expired" />
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

    <!-- 会员表格 -->
    <DataTable
      :data="memberList"
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
        <el-tag :type="getLevelType(row.level)">{{
          getLevelText(row.level)
        }}</el-tag>
      </template>

      <template #column-status="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>

      <template #column-balance="{ row }">
        <span class="balance">¥{{ row.balance }}</span>
      </template>

      <template #column-points="{ row }">
        <span class="points">{{ row.points }}</span>
      </template>

      <template #column-lastVisit="{ row }">
        <span>{{ formatDate(row.lastVisit) }}</span>
      </template>

      <template #column-actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">详情</el-button>
        <el-button size="small" type="primary" @click="recharge(row)"
          >充值</el-button
        >
        <el-button size="small" type="success" @click="adjustPoints(row)"
          >积分</el-button
        >
        <el-dropdown @command="(command) => handleAction(command, row)">
          <el-button size="small">
            更多操作
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="edit">编辑信息</el-dropdown-item>
              <el-dropdown-item command="delete" type="danger" divided
                >删除会员</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </DataTable>

    <!-- 新增会员对话框 -->
    <el-dialog
      v-model="memberDialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="memberFormRef"
        :model="memberForm"
        :rules="memberRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="选择客户" prop="customerId">
              <el-select
                v-model="memberForm.customerId"
                placeholder="请选择客户"
                filterable
                remote
                :remote-method="searchCustomers"
                :loading="customerLoading"
                style="width: 100%"
                @change="handleCustomerSelect"
              >
                <el-option
                  v-for="item in customerOptions"
                  :key="item.id"
                  :label="`${item.customerName} (${item.phone})`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会员等级" prop="level">
              <el-select
                v-model="memberForm.level"
                placeholder="请选择等级"
                style="width: 100%"
              >
                <el-option label="钻石会员" value="diamond" />
                <el-option label="VIP会员" value="vip" />
                <el-option label="普通会员" value="normal" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员卡号" prop="cardNumber">
              <el-input
                v-model="memberForm.cardNumber"
                placeholder="自动生成/手动输入"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="初始余额">
              <el-input-number
                v-model="memberForm.initialBalance"
                :precision="2"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="初始积分">
              <el-input-number
                v-model="memberForm.initialPoints"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
            v-model="memberForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="memberDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmitMember"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 充值对话框 -->
    <el-dialog
      v-model="rechargeDialogVisible"
      title="会员充值"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="recharge-info">
        <div class="member-info">
          <el-avatar :size="50" :src="selectedMember?.avatar">
            {{ selectedMember?.name?.charAt(0) }}
          </el-avatar>
          <div class="member-details">
            <div class="member-name">{{ selectedMember?.name }}</div>
            <div class="member-phone">{{ selectedMember?.phone }}</div>
            <div class="current-balance">
              当前余额：¥{{ selectedMember?.balance }}
            </div>
          </div>
        </div>
      </div>

      <el-form
        ref="rechargeFormRef"
        :model="rechargeForm"
        :rules="rechargeRules"
        label-width="100px"
      >
        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="rechargeForm.amount"
            :precision="2"
            :min="0.01"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="赠送金额">
          <el-input-number
            v-model="rechargeForm.giftAmount"
            :precision="2"
            :min="0"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="rechargeForm.paymentMethod">
            <el-radio label="cash">现金</el-radio>
            <el-radio label="wechat">微信</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="rechargeForm.remark"
            placeholder="请输入充值备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rechargeDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="rechargeLoading"
            @click="handleRecharge"
          >
            确认充值
          </el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 会员详情对话框 -->
    <el-dialog v-model="memberDetailVisible" title="会员详情" width="600px">
      <el-descriptions v-if="currentMember" :column="2" border>
        <el-descriptions-item label="会员姓名">{{
          currentMember.name
        }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{
          currentMember.phone
        }}</el-descriptions-item>
        <el-descriptions-item label="会员等级">
          <el-tag :type="getLevelType(currentMember.level)">
            {{ getLevelText(currentMember.level) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentMember.status)">
            {{ getStatusText(currentMember.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账户余额" :span="2">
          <span class="balance">¥{{ currentMember.balance }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="积分" :span="2">
          <span class="points">{{ currentMember.points }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{
          currentMember.createdTime
        }}</el-descriptions-item>
        <el-descriptions-item label="最后到店" :span="2">{{
          formatDate(currentMember.lastVisit)
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="memberDetailVisible = false">关闭</el-button>
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
  Coin,
  User,
  Star,
  TrendCharts,
  Search,
  Refresh,
  ArrowDown,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import { financeApi, crmApi } from "@/api";
import type { PageParams } from "@/types/common";
import type { FinanceMember, MemberQueryParams } from "@/api";

// 会员数据接口
interface Member {
  id: number;
  customerId: number;
  name: string;
  phone: string;
  level: string;
  balance: number;
  points: number;
  status: "active" | "inactive" | "expired";
  createdTime: string;
  lastVisit?: string;
}

// 客户选项接口
interface CustomerOption {
  id: number;
  customerName: string;
  phone: string;
}

// 表格列配置
const columns = [
  { prop: "name", label: "会员姓名", width: 120 },
  { prop: "phone", label: "联系电话", width: 130 },
  { prop: "level", label: "会员等级", width: 100 },
  { prop: "balance", label: "账户余额", width: 100 },
  { prop: "points", label: "积分", width: 80 },
  { prop: "lastVisit", label: "最后到店", width: 160 },
  { prop: "status", label: "状态", width: 80 },
];

// 响应式数据
const loading = ref(false);
const submitLoading = ref(false);
const rechargeLoading = ref(false);
const customerLoading = ref(false);
const memberDialogVisible = ref(false);
const rechargeDialogVisible = ref(false);
const dialogTitle = ref("新增会员");
const isEdit = ref(false);
const selectedMember = ref<Member | null>(null);
const customerOptions = ref<CustomerOption[]>([]);

// 会员概览
const overview = reactive({
  totalMembers: 0,
  totalBalance: 0,
  totalPoints: 0,
  newMembersToday: 0,
});

// 会员列表
const memberList = ref<Member[]>([]);
const total = ref(0);
const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

// 筛选表单
const filterForm = reactive({
  name: "",
  phone: "",
  level: "",
  status: "",
  customerId: undefined as number | undefined,
  dateRange: [] as [string, string] | [],
});

// 会员表单
const memberForm = reactive({
  id: 0,
  customerId: undefined as number | undefined,
  cardNumber: "",
  level: "normal",
  initialBalance: 0,
  initialPoints: 0,
  remark: "",
});

// 充值表单
const rechargeForm = reactive({
  amount: 0,
  giftAmount: 0,
  paymentMethod: "cash",
  remark: "",
});

const memberFormRef = ref();
const rechargeFormRef = ref();

// 表单验证规则
const memberRules = {
  customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
  level: [{ required: true, message: "请选择会员等级", trigger: "change" }],
};

const rechargeRules = {
  amount: [
    { required: true, message: "请输入充值金额", trigger: "blur" },
    {
      type: "number",
      min: 0.01,
      message: "充值金额必须大于0",
      trigger: "blur",
    },
  ],
  paymentMethod: [
    { required: true, message: "请选择支付方式", trigger: "change" },
  ],
};

// 方法
const loadMemberList = async () => {
  loading.value = true;
  try {
    const params: MemberQueryParams = {
      current: pagination.pageNum,
      size: pagination.pageSize,
      customerId: filterForm.customerId || undefined,
      memberLevel: filterForm.level || undefined,
      status: filterForm.status
        ? filterForm.status === "active"
          ? 1
          : 0
        : undefined,
    };

    const response = await financeApi.getMemberList(params);

    if (response.code === 200) {
      total.value = response.data.total;

      // 将API数据转换为前端显示格式
      memberList.value = response.data.records.map((member: any) => ({
        id: member.id || 0,
        name: member.customerName || member.cardNo || "会员" + member.id, // 优先显示客户姓名
        phone: member.phone || "", // 显示客户电话
        gender: "male", // API中没有此字段
        level: member.cardType || "normal", // 使用cardType
        balance: member.balance || 0,
        points: member.points || 0,
        status: member.status === 1 ? "active" : "inactive",
        birthday: "", // API中没有此字段
        address: "", // API中没有此字段
        createdTime: member.createTime || "",
        lastVisit: member.updateTime || "",
      }));

      // 计算概览数据
      calculateOverview();
    } else {
      ElMessage.error(response.message || "加载会员列表失败");
    }
  } catch (error) {
    console.error("加载会员列表失败", error);
    ElMessage.error("加载会员列表失败");
  } finally {
    loading.value = false;
  }
};

const calculateOverview = () => {
  overview.totalMembers = memberList.value.length;
  overview.totalBalance = memberList.value.reduce(
    (sum, member) => sum + member.balance,
    0,
  );
  overview.totalPoints = memberList.value.reduce(
    (sum, member) => sum + member.points,
    0,
  );
  overview.newMembersToday = memberList.value.filter((member) =>
    member.createdTime.startsWith("2024-01-15"),
  ).length;
};

const handleSearch = () => {
  pagination.pageNum = 1;
  loadMemberList();
};

const handleReset = () => {
  Object.keys(filterForm).forEach((key) => {
    filterForm[key] = "";
  });
  filterForm.dateRange = [];
  pagination.pageNum = 1;
  loadMemberList();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadMemberList();
};

const handleSelectionChange = (selection: Member[]) => {
  console.log("选中的会员:", selection);
};

// 搜索客户
const searchCustomers = async (query: string) => {
  if (query) {
    customerLoading.value = true;
    try {
      // 假设 crmApi.getCustomers 支持按名称或电话搜索
      const res = await crmApi.getCustomers({
        current: 1,
        size: 20,
        customerName: query, // 这里可能需要后端支持模糊搜索
      });
      if (res.code === 200) {
        customerOptions.value = res.data.records.map((c: any) => ({
          id: c.id,
          customerName: c.customerName,
          phone: c.phone,
        }));
      }
    } catch (error) {
      console.error(error);
    } finally {
      customerLoading.value = false;
    }
  } else {
    customerOptions.value = [];
  }
};

// 选中客户时，可选自动填充一些信息（如需）
const handleCustomerSelect = (id: number) => {
  const customer = customerOptions.value.find((c) => c.id === id);
  if (customer) {
    // 可以在这里做一些逻辑，例如检查该客户是否已经是会员
  }
};

const showAddDialog = () => {
  dialogTitle.value = "新增会员";
  isEdit.value = false;
  memberForm.id = 0;
  memberForm.customerId = undefined;
  memberForm.cardNumber = "";
  memberForm.level = "normal";
  memberForm.initialBalance = 0;
  memberForm.initialPoints = 0;
  memberForm.remark = "";
  customerOptions.value = []; // 清空选项
  memberDialogVisible.value = true;
};

const showEditDialog = (row: Member) => {
  dialogTitle.value = "编辑会员";
  isEdit.value = true;
  // 这里需要反查客户信息以显示在select中
  memberForm.id = row.id;
  memberForm.customerId = row.customerId;
  memberForm.level = row.level as string;
  memberForm.cardNumber = ""; // 编辑时卡号通常不可改或只显示
  memberForm.initialBalance = 0; // 编辑时不显示余额修改
  memberForm.initialPoints = 0;
  memberForm.remark = "";

  // 模拟填充当前客户到选项中，以便显示
  customerOptions.value = [
    {
      id: row.customerId,
      customerName: row.name,
      phone: row.phone,
    },
  ];

  memberDialogVisible.value = true;
};

const handleSubmitMember = async () => {
  if (!memberFormRef.value) return;

  try {
    await memberFormRef.value.validate();

    submitLoading.value = true;

    if (isEdit.value) {
      await financeApi.updateMember(memberForm.id, {
        customerId: memberForm.customerId!, // 确保 customerId 存在
        cardType: memberForm.level, // 映射 level -> cardType
        cardNumber: memberForm.cardNumber, // 保持卡号
        balance: memberForm.initialBalance, // 允许更新余额? 通常不允许，但接口可能包含
        points: memberForm.initialPoints,
        status: 1, // 默认为启用
      } as any); // 使用 as any 绕过类型检查，或者修正 FinanceMember 接口
      ElMessage.success("会员信息更新成功");
    } else {
      await financeApi.createMember({
        customerId: memberForm.customerId!,
        cardType: memberForm.level, // 注意后端字段可能叫 cardType
        cardNumber: memberForm.cardNumber,
        balance: memberForm.initialBalance,
        points: memberForm.initialPoints,
      });
      ElMessage.success("会员添加成功");
    }

    memberDialogVisible.value = false;
    loadMemberList();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

const recharge = (member: Member) => {
  selectedMember.value = member;
  rechargeDialogVisible.value = true;
  resetRechargeForm();
};

const resetRechargeForm = () => {
  rechargeForm.amount = 0;
  rechargeForm.giftAmount = 0;
  rechargeForm.paymentMethod = "cash";
  rechargeForm.remark = "";
};

const handleRecharge = async () => {
  if (!rechargeFormRef.value) return;

  try {
    await rechargeFormRef.value.validate();

    rechargeLoading.value = true;

    await financeApi.rechargeMember(selectedMember.value!.id, {
      rechargeAmount: rechargeForm.amount,
      giftAmount: rechargeForm.giftAmount,
      paymentMethod: rechargeForm.paymentMethod,
      operatorId: 1, // 暂时使用固定值，后续从用户store获取
      remark: rechargeForm.remark,
    });

    const totalAmount = rechargeForm.amount + rechargeForm.giftAmount;
    ElMessage.success(`充值成功！充值金额：¥${totalAmount}`);

    rechargeDialogVisible.value = false;
    loadMemberList();
  } catch (error) {
    console.error("充值失败:", error);
  } finally {
    rechargeLoading.value = false;
  }
};

const adjustPoints = (member: Member) => {
  ElMessage.info("积分调整功能开发中");
};

const memberDetailVisible = ref(false);
const currentMember = ref<Member | null>(null);

const viewDetail = (member: Member) => {
  currentMember.value = member;
  memberDetailVisible.value = true;
};

const handleAction = async (command: string, member: Member) => {
  switch (command) {
    case "edit":
      showAddDialog();
      Object.assign(memberForm, member);
      isEdit.value = true;
      dialogTitle.value = "编辑会员";
      break;
    case "consumption":
      ElMessage.info("消费记录功能开发中");
      break;
    case "transfer":
      ElMessage.info("积分转让功能开发中");
      break;
    case "delete":
      try {
        await ElMessageBox.confirm(
          `确定要删除会员"${member.name}"吗？此操作不可撤销，将同时删除该会员的所有消费记录。`,
          "确认删除",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          },
        );
        await financeApi.deleteMember(member.id);
        ElMessage.success("会员删除成功");
        loadMemberList();
      } catch {
        // 用户取消操作
      }
      break;
  }
};

const exportMembers = () => {
  ElMessage.success("导出会员功能开发中");
};

const batchRecharge = () => {
  ElMessage.info("批量充值功能开发中");
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

const getStatusType = (status: string) => {
  switch (status) {
    case "active":
      return "success";
    case "inactive":
      return "warning";
    case "expired":
      return "danger";
    default:
      return "info";
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case "active":
      return "正常";
    case "inactive":
      return "停用";
    case "expired":
      return "过期";
    default:
      return "正常";
  }
};

const formatDate = (dateStr?: string) => {
  if (!dateStr) return "-";
  const date = new Date(dateStr);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

onMounted(() => {
  loadMemberList();
});
</script>

<style scoped lang="scss">
.member-list {
  .member-overview {
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

  .balance {
    color: #67c23a;
    font-weight: 500;
  }

  .points {
    color: #e6a23c;
    font-weight: 500;
  }

  .recharge-info {
    margin-bottom: 20px;

    .member-info {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px;
      background-color: #f5f5f5;
      border-radius: 8px;

      .member-details {
        .member-name {
          font-size: 16px;
          font-weight: 600;
          color: #1d2129;
          margin-bottom: 4px;
        }

        .member-phone {
          color: #86909c;
          margin-bottom: 4px;
        }

        .current-balance {
          color: #67c23a;
          font-weight: 500;
        }
      }
    }
  }
}
</style>
