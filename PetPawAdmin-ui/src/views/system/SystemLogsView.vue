<template>
  <div class="log-manage">
    <PageHeader title="操作日志" description="查看系统操作记录" />

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="操作人">
          <el-input
            v-model="filterForm.username"
            placeholder="请输入操作人"
            clearable
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-input
            v-model="filterForm.operation"
            placeholder="请输入操作类型"
            clearable
          />
        </el-form-item>
        <el-form-item label="起止日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
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
      :data="logList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      @page-change="handlePageChange"
    >
      <template #column-actions="{ row }">
        <el-button size="small" @click="showDetail(row)">详情</el-button>
      </template>
    </DataTable>

    <!-- 日志详情对话框 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作人">{{
          currentLog.username
        }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{
          currentLog.operation
        }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{
          currentLog.method
        }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre class="json-code">{{ currentLog.params }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="执行时长"
          >{{ currentLog.time }} ms</el-descriptions-item
        >
        <el-descriptions-item label="IP地址">{{
          currentLog.ip
        }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          currentLog.createTime
        }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { Search, Refresh } from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import { systemApi } from "@/api";
import type { SysLog } from "@/api/system";

const loading = ref(false);
const logList = ref<SysLog[]>([]);
const total = ref(0);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

const filterForm = reactive({
  username: "",
  operation: "",
  startDate: "",
  endDate: "",
});
const dateRange = ref<[string, string] | null>(null);

const columns = [
  { prop: "username", label: "操作人" },
  { prop: "operation", label: "操作类型" },
  { prop: "method", label: "请求方法" },
  { prop: "ip", label: "IP地址" },
  { prop: "time", label: "耗时(ms)" },
  { prop: "createTime", label: "操作时间" },
];

// 详情相关
const detailVisible = ref(false);
const currentLog = ref<any>({});

const loadData = async () => {
  loading.value = true;
  // 处理日期范围
  if (dateRange.value) {
    filterForm.startDate = dateRange.value[0];
    filterForm.endDate = dateRange.value[1];
  } else {
    filterForm.startDate = "";
    filterForm.endDate = "";
  }

  try {
    const res = await systemApi.getLogs({
      current: pagination.pageNum,
      size: pagination.pageSize,
      ...filterForm,
    });
    if (res.code === 200) {
      logList.value = res.data.records;
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
  filterForm.operation = "";
  dateRange.value = null;
  handleSearch();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadData();
};

const showDetail = (row: SysLog) => {
  currentLog.value = row;
  detailVisible.value = true;
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
.log-manage {
  .filter-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
  }
  .json-code {
    white-space: pre-wrap;
    word-break: break-all;
    max-height: 200px;
    overflow-y: auto;
  }
}
</style>
