<template>
  <div class="log-table">
    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="用户名">
          <el-input
            v-model="filterForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 120px"
          />
        </el-form-item>

        <el-form-item label="操作类型">
          <el-select
            v-model="filterForm.operation"
            placeholder="请选择操作类型"
            clearable
            style="width: 130px"
          >
            <el-option label="登录" value="login" />
            <el-option label="新增" value="add" />
            <el-option label="修改" value="edit" />
            <el-option label="删除" value="delete" />
            <el-option label="查询" value="view" />
            <el-option label="导出" value="export" />
          </el-select>
        </el-form-item>

        <el-form-item label="日志级别">
          <el-select
            v-model="filterForm.level"
            placeholder="请选择日志级别"
            clearable
            style="width: 120px"
          >
            <el-option label="调试" value="debug" />
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="错误" value="error" />
          </el-select>
        </el-form-item>

        <el-form-item label="操作结果">
          <el-select
            v-model="filterForm.result"
            placeholder="请选择操作结果"
            clearable
            style="width: 100px"
          >
            <el-option label="成功" value="success" />
            <el-option label="失败" value="failure" />
          </el-select>
        </el-form-item>

        <el-form-item label="操作时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
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

    <!-- 日志表格 -->
    <el-table
      :data="displayLogs"
      style="width: 100%"
      :loading="loading"
      size="small"
      @sort-change="handleSortChange"
    >
      <el-table-column
        prop="createdTime"
        label="操作时间"
        width="160"
        sortable="custom"
      >
        <template #default="scope">
          <span class="datetime">{{
            formatDateTime(scope.row.createdTime)
          }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="username" label="用户名" width="100" />

      <el-table-column prop="operation" label="操作" width="120" />

      <el-table-column prop="module" label="模块" width="120" />

      <el-table-column
        prop="description"
        label="描述"
        min-width="200"
        show-overflow-tooltip
      />

      <el-table-column prop="ip" label="IP地址" width="120" />

      <el-table-column prop="level" label="级别" width="80">
        <template #default="scope">
          <el-tag :type="getLevelType(scope.row.level)">
            {{ getLevelText(scope.row.level) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="result" label="结果" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.result === 'success' ? 'success' : 'danger'">
            {{ scope.row.result === "success" ? "成功" : "失败" }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="100" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewDetails(scope.row)"
            >详情</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`日志详情 - ${selectedLog?.operation || ''}`"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedLog" class="log-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="操作时间">
            {{ formatDateTime(selectedLog.createdTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ selectedLog.username }}
          </el-descriptions-item>
          <el-descriptions-item label="操作">
            {{ selectedLog.operation }}
          </el-descriptions-item>
          <el-descriptions-item label="模块">
            {{ selectedLog.module }}
          </el-descriptions-item>
          <el-descriptions-item label="IP地址">
            {{ selectedLog.ip }}
          </el-descriptions-item>
          <el-descriptions-item label="地理位置">
            {{ selectedLog.location || "未知" }}
          </el-descriptions-item>
          <el-descriptions-item label="日志级别">
            <el-tag :type="getLevelType(selectedLog.level)">
              {{ getLevelText(selectedLog.level) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作结果">
            <el-tag
              :type="selectedLog.result === 'success' ? 'success' : 'danger'"
            >
              {{ selectedLog.result === "success" ? "成功" : "失败" }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="description-section">
          <h4>操作描述：</h4>
          <p>{{ selectedLog.description }}</p>
        </div>

        <div v-if="selectedLog.details" class="details-section">
          <h4>详细信息：</h4>
          <el-json-viewer
            :data="selectedLog.details"
            :expand-depth="2"
            theme="dark"
          />
        </div>

        <div v-if="selectedLog.userAgent" class="user-agent-section">
          <h4>用户代理：</h4>
          <p class="user-agent">{{ selectedLog.userAgent }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import { Search, Refresh } from "@element-plus/icons-vue";
import type { PageParams } from "@/types/common";

// 日志接口
interface LogEntry {
  id: number;
  type: "login" | "operation" | "error" | "security";
  level: "info" | "warning" | "error" | "debug";
  username: string;
  operation: string;
  module: string;
  ip: string;
  location?: string;
  userAgent?: string;
  description: string;
  result: "success" | "failure";
  createdTime: string;
  details?: any;
}

interface Props {
  logs: LogEntry[];
  loading: boolean;
}

const props = defineProps<Props>();

// 响应式数据
const detailDialogVisible = ref(false);
const selectedLog = ref<LogEntry | null>(null);

// 分页
const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

// 筛选表单
const filterForm = reactive({
  username: "",
  operation: "",
  level: "",
  result: "",
  dateRange: [] as [string, string] | [],
});

// 计算属性
const filteredLogs = computed(() => {
  let filtered = [...props.logs];

  if (filterForm.username) {
    filtered = filtered.filter((log) =>
      log.username.includes(filterForm.username),
    );
  }

  if (filterForm.operation) {
    filtered = filtered.filter((log) =>
      log.operation.includes(filterForm.operation),
    );
  }

  if (filterForm.level) {
    filtered = filtered.filter((log) => log.level === filterForm.level);
  }

  if (filterForm.result) {
    filtered = filtered.filter((log) => log.result === filterForm.result);
  }

  if (filterForm.dateRange && filterForm.dateRange.length === 2) {
    const [startDate, endDate] = filterForm.dateRange;
    filtered = filtered.filter((log) => {
      const logTime = new Date(log.createdTime).getTime();
      return (
        logTime >= new Date(startDate).getTime() &&
        logTime <= new Date(endDate).getTime()
      );
    });
  }

  return filtered;
});

const displayLogs = computed(() => {
  const startIndex = (pagination.pageNum - 1) * pagination.pageSize;
  const endIndex = startIndex + pagination.pageSize;
  return filteredLogs.value.slice(startIndex, endIndex);
});

const total = computed(() => filteredLogs.value.length);

// 方法
const handleSearch = () => {
  pagination.pageNum = 1;
};

const handleReset = () => {
  Object.keys(filterForm).forEach((key) => {
    if (key === "dateRange") {
      filterForm[key] = [];
    } else {
      filterForm[key] = "";
    }
  });
  pagination.pageNum = 1;
};

const handleSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize;
  pagination.pageNum = 1;
};

const handleCurrentChange = (pageNum: number) => {
  pagination.pageNum = pageNum;
};

const handleSortChange = (sort: any) => {
  // 处理排序
  console.log("排序:", sort);
};

const viewDetails = (log: LogEntry) => {
  selectedLog.value = log;
  detailDialogVisible.value = true;
};

const formatDateTime = (dateTimeStr: string) => {
  const date = new Date(dateTimeStr);
  return date.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
};

const getLevelType = (level: string) => {
  switch (level) {
    case "error":
      return "danger";
    case "warning":
      return "warning";
    case "info":
      return "success";
    case "debug":
      return "info";
    default:
      return "info";
  }
};

const getLevelText = (level: string) => {
  switch (level) {
    case "error":
      return "错误";
    case "warning":
      return "警告";
    case "info":
      return "信息";
    case "debug":
      return "调试";
    default:
      return level;
  }
};

// 监听props.logs变化，重置分页
watch(
  () => props.logs,
  () => {
    pagination.pageNum = 1;
  },
);
</script>

<style scoped lang="scss">
.log-table {
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

  .datetime {
    font-family: "Courier New", monospace;
    font-size: 12px;
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }

  .log-details {
    .description-section,
    .details-section,
    .user-agent-section {
      margin-top: 20px;

      h4 {
        margin: 0 0 8px 0;
        font-size: 14px;
        font-weight: 600;
        color: #1d2129;
      }

      p {
        margin: 0;
        color: #4e5969;
        line-height: 1.5;
      }

      .user-agent {
        font-family: "Courier New", monospace;
        font-size: 12px;
        background-color: #f5f5f5;
        padding: 8px;
        border-radius: 4px;
        word-break: break-all;
      }
    }
  }
}
</style>
