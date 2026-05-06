<template>
  <div class="coupon-list">
    <PageHeader
      title="优惠券管理"
      description="管理优惠券配置、发放与使用状态"
    />

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="优惠券名称">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入名称"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="filterForm.status"
            placeholder="全部"
            clearable
            style="width: 140px"
          >
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
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
      <template #column-discountType="{ row }">
        <el-tag :type="row.discountType === '满减' ? 'success' : 'info'">
          {{ row.discountType }}
        </el-tag>
      </template>
      <template #column-status="{ row }">
        <el-tag
          :type="
            row.status === '进行中'
              ? 'success'
              : row.status === '未开始'
                ? 'warning'
                : 'info'
          "
        >
          {{ row.status }}
        </el-tag>
      </template>
      <template #column-actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">查看</el-button>
        <el-button size="small" @click="toggleEnable(row)">{{
          row.enabled ? "停用" : "启用"
        }}</el-button>
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

type CouponDiscountType = "满减" | "折扣";
type CouponStatus = "未开始" | "进行中" | "已结束";

interface Coupon {
  id: number;
  name: string;
  code: string;
  discountType: CouponDiscountType;
  discountValue: string;
  status: CouponStatus;
  validRange: string;
  enabled: boolean;
  createTime: string;
}

const loading = ref(false);

const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

const filterForm = reactive({
  name: "",
  status: "" as "" | CouponStatus,
});

const allData = ref<Coupon[]>([
  {
    id: 1,
    name: "新客专享券",
    code: "NEW-2025",
    discountType: "满减",
    discountValue: "满199减30",
    status: "进行中",
    validRange: "2025-12-01 ~ 2026-01-31",
    enabled: true,
    createTime: "2025-12-01 10:00:00",
  },
  {
    id: 2,
    name: "洗护折扣券",
    code: "WASH-9",
    discountType: "折扣",
    discountValue: "9折",
    status: "进行中",
    validRange: "2025-12-15 ~ 2026-02-15",
    enabled: true,
    createTime: "2025-12-15 09:30:00",
  },
  {
    id: 3,
    name: "节日满减券",
    code: "FEST-50",
    discountType: "满减",
    discountValue: "满299减50",
    status: "未开始",
    validRange: "2026-01-10 ~ 2026-02-10",
    enabled: false,
    createTime: "2025-12-28 14:20:00",
  },
  {
    id: 4,
    name: "会员专属券",
    code: "VIP-20",
    discountType: "满减",
    discountValue: "满129减20",
    status: "已结束",
    validRange: "2025-10-01 ~ 2025-11-30",
    enabled: false,
    createTime: "2025-09-25 11:10:00",
  },
]);

const filteredData = computed(() => {
  const name = filterForm.name.trim();
  const status = filterForm.status;

  return allData.value.filter((item) => {
    const matchName =
      !name || item.name.includes(name) || item.code.includes(name);
    const matchStatus = !status || item.status === status;
    return matchName && matchStatus;
  });
});

const total = computed(() => filteredData.value.length);

const pagedData = computed(() => {
  const start = (pagination.pageNum - 1) * pagination.pageSize;
  return filteredData.value.slice(start, start + pagination.pageSize);
});

const columns = [
  { prop: "name", label: "优惠券名称", width: 160 },
  { prop: "code", label: "券码", width: 120 },
  { prop: "discountType", label: "类型", slot: "discountType", width: 100 },
  { prop: "discountValue", label: "力度", width: 120 },
  { prop: "status", label: "状态", slot: "status", width: 100 },
  { prop: "validRange", label: "有效期", width: 220 },
  { prop: "createTime", label: "创建时间", width: 180 },
  { prop: "actions", label: "操作", slot: "actions", width: 160 },
];

const handleSearch = () => {
  pagination.pageNum = 1;
};

const handleReset = () => {
  filterForm.name = "";
  filterForm.status = "";
  pagination.pageNum = 1;
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
};

const viewDetail = (row: Coupon) => {
  ElMessage.info(`优惠券：${row.name}（${row.code}）`);
};

const toggleEnable = (row: Coupon) => {
  const target = allData.value.find((x) => x.id === row.id);
  if (!target) return;
  target.enabled = !target.enabled;
  ElMessage.success(target.enabled ? "已启用" : "已停用");
};
</script>

<style scoped lang="scss">
.coupon-list {
  .filter-section {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}
</style>
