<template>
  <div class="consumable-stock">
    <PageHeader
      title="耗材出入库"
      description="管理耗材库存与出入库记录，支持快速查询"
    />

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="耗材名称">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入名称"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="filterForm.category"
            placeholder="全部"
            clearable
            style="width: 140px"
          >
            <el-option label="清洁" value="清洁" />
            <el-option label="护理" value="护理" />
            <el-option label="医疗" value="医疗" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <DataTable
      :data="pagedData"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      @page-change="handlePageChange"
    >
      <template #column-stockStatus="{ row }">
        <el-tag
          :type="
            row.stockStatus === '缺货'
              ? 'danger'
              : row.stockStatus === '预警'
                ? 'warning'
                : 'success'
          "
        >
          {{ row.stockStatus }}
        </el-tag>
      </template>
      <template #column-actions="{ row }">
        <el-button size="small" @click="inbound(row)">入库</el-button>
        <el-button size="small" @click="outbound(row)">出库</el-button>
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import type { PageParams } from "@/types/common";

type StockStatus = "正常" | "预警" | "缺货";

interface ConsumableItem {
  id: number;
  name: string;
  category: string;
  unit: string;
  stock: number;
  safeStock: number;
  stockStatus: StockStatus;
  updateTime: string;
}

const loading = ref(false);

const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

const filterForm = reactive({
  name: "",
  category: "",
});

const allData = ref<ConsumableItem[]>([
  {
    id: 1,
    name: "宠物洗护香波",
    category: "护理",
    unit: "瓶",
    stock: 48,
    safeStock: 20,
    stockStatus: "正常",
    updateTime: "2025-12-29 09:12:00",
  },
  {
    id: 2,
    name: "一次性手套",
    category: "医疗",
    unit: "盒",
    stock: 6,
    safeStock: 10,
    stockStatus: "预警",
    updateTime: "2025-12-29 09:10:00",
  },
  {
    id: 3,
    name: "消毒喷雾",
    category: "清洁",
    unit: "瓶",
    stock: 0,
    safeStock: 8,
    stockStatus: "缺货",
    updateTime: "2025-12-28 18:40:00",
  },
  {
    id: 4,
    name: "棉签",
    category: "医疗",
    unit: "包",
    stock: 12,
    safeStock: 12,
    stockStatus: "预警",
    updateTime: "2025-12-28 16:20:00",
  },
]);

const filteredData = computed(() => {
  const name = filterForm.name.trim();
  const category = filterForm.category.trim();
  return allData.value.filter((item) => {
    const matchName = !name || item.name.includes(name);
    const matchCategory = !category || item.category === category;
    return matchName && matchCategory;
  });
});

const total = computed(() => filteredData.value.length);

const pagedData = computed(() => {
  const start = (pagination.pageNum - 1) * pagination.pageSize;
  return filteredData.value.slice(start, start + pagination.pageSize);
});

const columns = [
  { prop: "name", label: "耗材名称", width: 180 },
  { prop: "category", label: "分类", width: 120 },
  { prop: "unit", label: "单位", width: 80 },
  { prop: "stock", label: "库存", width: 100 },
  { prop: "safeStock", label: "安全库存", width: 120 },
  { prop: "stockStatus", label: "状态", slot: "stockStatus", width: 100 },
  { prop: "updateTime", label: "更新时间", width: 180 },
  { prop: "actions", label: "操作", slot: "actions", width: 160 },
];

const handleSearch = () => {
  pagination.pageNum = 1;
};

const handleReset = () => {
  filterForm.name = "";
  filterForm.category = "";
  pagination.pageNum = 1;
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
};

const recalcStatus = (item: ConsumableItem) => {
  if (item.stock <= 0) return "缺货" as StockStatus;
  if (item.stock <= item.safeStock) return "预警" as StockStatus;
  return "正常" as StockStatus;
};

const inbound = (row: ConsumableItem) => {
  const target = allData.value.find((x) => x.id === row.id);
  if (!target) return;
  target.stock += 5;
  target.stockStatus = recalcStatus(target);
  target.updateTime = new Date().toLocaleString();
  ElMessage.success("已入库");
};

const outbound = (row: ConsumableItem) => {
  const target = allData.value.find((x) => x.id === row.id);
  if (!target) return;
  target.stock = Math.max(0, target.stock - 3);
  target.stockStatus = recalcStatus(target);
  target.updateTime = new Date().toLocaleString();
  ElMessage.success("已出库");
};
</script>

<style scoped lang="scss">
.consumable-stock {
  .filter-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}
</style>
