<template>
  <div class="inventory">
    <PageHeader title="库存管理" description="实时监控库存状态，智能预警提醒">
      <template #actions>
        <el-button type="primary" @click="showInboundDialog">
          <el-icon><Plus /></el-icon>
          商品入库
        </el-button>
        <el-button @click="() => showOutboundDialog()">
          <el-icon><Minus /></el-icon>
          商品出库
        </el-button>
        <el-button @click="inventoryCheck">
          <el-icon><DocumentChecked /></el-icon>
          库存盘点
        </el-button>
      </template>
    </PageHeader>

    <!-- 库存概览 -->
    <div class="inventory-overview">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#67c23a"><Box /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.totalProducts }}</div>
              <div class="card-label">总商品数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card">
            <div class="card-icon">
              <el-icon size="32" color="#409eff"><TrendCharts /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.totalStock }}</div>
              <div class="card-label">总库存量</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card warning">
            <div class="card-icon">
              <el-icon size="32" color="#e6a23c"><Warning /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.lowStockItems }}</div>
              <div class="card-label">库存预警</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-card danger">
            <div class="card-icon">
              <el-icon size="32" color="#f56c6c"><CircleClose /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-value">{{ overview.outOfStockItems }}</div>
              <div class="card-label">缺货商品</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="商品名称">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入商品名称"
            clearable
            style="width: 140px"
          />
        </el-form-item>

        <el-form-item label="库存状态">
          <el-select
            v-model="filterForm.stockStatus"
            placeholder="请选择库存状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" value="normal" />
            <el-option label="库存不足" value="low" />
            <el-option label="缺货" value="out" />
          </el-select>
        </el-form-item>

        <el-form-item label="供应商">
          <el-select
            v-model="filterForm.supplierId"
            placeholder="请选择供应商"
            clearable
            filterable
            style="width: 140px"
          >
            <el-option
              v-for="supplier in suppliers"
              :key="supplier.id"
              :label="supplier.supplierName || supplier.name"
              :value="supplier.id"
            />
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

    <!-- 库存表格 -->
    <DataTable
      :data="inventoryList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      show-selection
      show-actions
      @selection-change="handleSelectionChange"
      @page-change="handlePageChange"
    >
      <template #column-stock="{ row }">
        <div class="stock-display">
          <span :class="getStockStatusClass(row)">{{ row.currentStock }}</span>
          <span class="unit">{{ row.unit }}</span>
          <div class="stock-info">
            <span class="min-stock">安全: {{ row.minStock }}</span>
          </div>
        </div>
      </template>

      <template #column-stockValue="{ row }">
        <span class="stock-value"
          >¥{{ (row.currentStock * row.price).toLocaleString() }}</span
        >
      </template>

      <template #column-status="{ row }">
        <el-tag :type="getStockStatusType(row)">
          {{ getStockStatusText(row) }}
        </el-tag>
      </template>

      <template #column-actions="{ row }">
        <el-button size="small" @click="adjustStock(row)">调整库存</el-button>
        <el-button size="small" type="primary" @click="viewHistory(row)"
          >库存记录</el-button
        >
        <el-dropdown @command="(command) => handleAction(command, row)">
          <el-button size="small">
            更多操作
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="inbound">入库</el-dropdown-item>
              <el-dropdown-item command="outbound">出库</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </DataTable>

    <!-- 入库对话框 -->
    <el-dialog
      v-model="inboundDialogVisible"
      title="商品入库"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="inboundFormRef"
        :model="inboundForm"
        :rules="inboundRules"
        label-width="100px"
      >
        <el-form-item label="选择商品" prop="productId">
          <el-select
            v-model="inboundForm.productId"
            placeholder="请选择入库商品"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="product in allProducts"
              :key="product.id"
              :label="`${product.name} (${product.code})`"
              :value="product.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="入库数量" prop="quantity">
          <el-input-number
            v-model="inboundForm.quantity"
            :min="1"
            :max="10000"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="入库价格">
          <el-input-number
            v-model="inboundForm.price"
            :precision="2"
            :min="0"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="供应商">
          <el-select
            v-model="inboundForm.supplierId"
            placeholder="请选择供应商"
            filterable
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="supplier in suppliers"
              :key="supplier.id"
              :label="supplier.supplierName || supplier.name"
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="入库备注">
          <el-input
            v-model="inboundForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入入库备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="inboundDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="inboundLoading"
            @click="handleInbound"
          >
            确认入库
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 出库对话框 -->
    <el-dialog
      v-model="outboundDialogVisible"
      title="商品出库"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="outboundFormRef"
        :model="outboundForm"
        :rules="outboundRules"
        label-width="100px"
      >
        <el-form-item label="选择商品" prop="productId">
          <el-select
            v-model="outboundForm.productId"
            placeholder="请选择出库商品"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="product in availableProducts"
              :key="product.id"
              :label="`${product.name} (库存: ${product.currentStock}${product.unit})`"
              :value="product.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="出库数量" prop="quantity">
          <el-input-number
            v-model="outboundForm.quantity"
            :min="1"
            :max="selectedProduct?.currentStock || 0"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="出库类型" prop="type">
          <el-radio-group v-model="outboundForm.type">
            <el-radio label="sale">销售出库</el-radio>
            <el-radio label="damage">损耗出库</el-radio>
            <el-radio label="transfer">调拨出库</el-radio>
            <el-radio label="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="出库备注">
          <el-input
            v-model="outboundForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入出库备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="outboundDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="outboundLoading"
            @click="handleOutbound"
          >
            确认出库
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 调整库存对话框 -->
    <el-dialog
      v-model="adjustDialogVisible"
      title="调整库存"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="adjustFormRef"
        :model="adjustForm"
        :rules="adjustRules"
        label-width="100px"
      >
        <el-form-item label="当前库存">
          <span>{{ adjustForm.currentStock }}</span>
        </el-form-item>

        <el-form-item label="调整类型" prop="adjustType">
          <el-radio-group v-model="adjustForm.adjustType">
            <el-radio label="add">增加库存</el-radio>
            <el-radio label="sub">减少库存</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="调整数量" prop="quantity">
          <el-input-number
            v-model="adjustForm.quantity"
            :min="1"
            :max="
              adjustForm.adjustType === 'sub' ? adjustForm.currentStock : 99999
            "
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="调整备注" prop="remark">
          <el-input
            v-model="adjustForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="adjustDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="adjustLoading"
            @click="handleAdjust"
          >
            确认调整
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 库存记录对话框 -->
    <el-dialog v-model="historyDialogVisible" title="库存记录" width="900px">
      <div
        v-if="currentHistoryItem"
        class="history-header"
        style="margin-bottom: 20px"
      >
        <el-descriptions :column="3" border>
          <el-descriptions-item label="商品名称">{{
            currentHistoryItem.name
          }}</el-descriptions-item>
          <el-descriptions-item label="商品编码">{{
            currentHistoryItem.code
          }}</el-descriptions-item>
          <el-descriptions-item label="当前库存">
            <span :class="getStockStatusClass(currentHistoryItem)">
              {{ currentHistoryItem.currentStock }}
              {{ currentHistoryItem.unit }}
            </span>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <el-table
        v-loading="historyLoading"
        :data="inventoryRecords"
        style="width: 100%"
        border
        stripe
        height="400"
      >
        <el-table-column prop="createTime" label="操作时间" width="180">
          <template #default="{ row }">
            {{ row.createTime || row.create_time }}
          </template>
        </el-table-column>
        <el-table-column prop="recordType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getRecordTypeTag(row.recordType)">
              {{ getRecordTypeLabel(row.recordType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="变动数量" width="120">
          <template #default="{ row }">
            <span
              :class="
                row.quantity > 0
                  ? 'text-success'
                  : row.quantity < 0
                    ? 'text-danger'
                    : ''
              "
            >
              {{ row.quantity > 0 ? "+" : "" }}{{ row.quantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="afterQuantity" label="变动后库存" width="120">
          <template #default="{ row }">
            {{ row.afterQuantity || row.stockAfter }}
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="操作人" width="100" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import {
  Plus,
  Minus,
  DocumentChecked,
  Box,
  TrendCharts,
  Warning,
  CircleClose,
  Search,
  Refresh,
  ArrowDown,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import { scmApi } from "@/api";
import type { PageParams } from "@/types/common";
import type {
  ScmInventoryRecord,
  InventoryRecordQueryParams,
  ScmSupplier,
} from "@/api/scm";

// 表格列配置
// 表格列配置（保持不变）
const columns = [
  { prop: "name", label: "商品名称", width: 180 },
  { prop: "code", label: "商品编码", width: 120 },
  { prop: "stock", label: "当前库存", width: 120 },
  { prop: "stockValue", label: "库存价值", width: 120 },
  { prop: "supplier", label: "供应商", width: 120 },
  { prop: "lastUpdate", label: "最后更新", width: 160 },
  { prop: "status", label: "库存状态", width: 100 },
];

// 供应商列表
const suppliers = ref<ScmSupplier[]>([]);

// 所有商品列表
const allProducts = ref<any[]>([]);

// 加载供应商列表
const loadSuppliers = async () => {
  try {
    const res = await scmApi.getSuppliers({
      current: 1,
      size: 100,
    });
    if (res.records) {
      suppliers.value = res.records;
    } else if (res.data?.records) {
      suppliers.value = res.data.records;
    }
  } catch (error) {
    console.error("加载供应商失败:", error);
  }
};

// 加载所有商品（用于下拉选择）
const loadAllProducts = async () => {
  try {
    const res = await scmApi.getProducts({
      current: 1,
      size: 1000,
    });
    let products = [];
    if (res.records) {
      products = res.records;
    } else if (res.data?.records) {
      products = res.data.records;
    }

    allProducts.value = products.map((p) => ({
      id: p.id,
      name: p.productName || p.product_name,
      code: p.productCode || p.product_code || "",
    }));
  } catch (error) {
    console.error("加载商品列表失败:", error);
  }
};

// 库存数据接口
interface InventoryItem {
  id: number;
  name: string;
  code: string;
  currentStock: number;
  minStock: number;
  unit: string;
  price: number;
  supplierName: string;
  lastInboundTime: string;
  status: "normal" | "low" | "out";
}

// 响应式数据
const loading = ref(false);
const inboundLoading = ref(false);
const outboundLoading = ref(false);
const inboundDialogVisible = ref(false);
const outboundDialogVisible = ref(false);
const adjustDialogVisible = ref(false);
const adjustLoading = ref(false);

// 库存概览
const overview = reactive({
  totalProducts: 0,
  totalStock: 0,
  lowStockItems: 0,
  outOfStockItems: 0,
});

// 库存列表
const inventoryList = ref<InventoryItem[]>([]);
const total = ref(0);
const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

// 筛选表单
const filterForm = reactive({
  name: "",
  stockStatus: "",
  supplierId: "",
});

// 入库表单
const inboundForm = reactive({
  productId: undefined as number | undefined,
  quantity: 1,
  price: 0,
  supplierId: "",
  remark: "",
});

// 出库表单
const outboundForm = reactive({
  productId: undefined as number | undefined,
  quantity: 1,
  type: "sale",
  remark: "",
});

const adjustForm = reactive({
  productId: "",
  currentStock: 0,
  adjustType: "add",
  quantity: 1,
  remark: "",
});

const inboundFormRef = ref<FormInstance>();
const outboundFormRef = ref<FormInstance>();
const adjustFormRef = ref<FormInstance>();

// 计算属性
const availableProducts = computed(() => {
  return inventoryList.value.filter((item) => item.currentStock > 0);
});

const selectedProduct = computed(() => {
  if (outboundForm.productId) {
    return inventoryList.value.find(
      (item) => item.id === outboundForm.productId,
    );
  }
  return null;
});

// 表单验证规则
const inboundRules: FormRules = {
  productId: [{ required: true, message: "请选择入库商品", trigger: "change" }],
  quantity: [
    { required: true, message: "请输入入库数量", trigger: "blur" },
    { type: "number", min: 1, message: "入库数量必须大于0", trigger: "blur" },
  ],
};

const outboundRules: FormRules = {
  productId: [{ required: true, message: "请选择出库商品", trigger: "change" }],
  quantity: [
    { required: true, message: "请输入出库数量", trigger: "blur" },
    { type: "number", min: 1, message: "出库数量必须大于0", trigger: "blur" },
  ],
  type: [{ required: true, message: "请选择出库类型", trigger: "change" }],
};

const adjustRules: FormRules = {
  quantity: [
    { required: true, message: "请输入调整数量", trigger: "blur" },
    { type: "number", min: 1, message: "调整数量必须大于0", trigger: "blur" },
  ],
  remark: [{ required: true, message: "请输入调整备注", trigger: "blur" }],
};

// 统一类型定义（只保留一份）
interface ScmProduct {
  id?: number;
  productName?: string;
  product_name?: string;
  productCode?: string;
  product_code?: string;
  category?: string;
  unit?: string;
  stockQuantity?: number;
  stock_quantity?: number;
  minStockQuantity?: number;
  min_stock_quantity?: number;
  salePrice?: number;
  sale_price?: number;
  status?: number;
  createTime?: string;
  create_time?: string;
  updateTime?: string;
  update_time?: string;
}

interface InventoryListResponse {
  code: number;
  total?: number;
  records?: ScmProduct[];
  data?: {
    records?: ScmProduct[];
    total?: number;
  };
  message?: string;
}

// 修复核心：加载库存列表方法
const loadInventoryList = async () => {
  loading.value = true;
  try {
    const params = {
      current: pagination.pageNum,
      size: pagination.pageSize,
      productName: filterForm.name || undefined,
      supplierId: filterForm.supplierId || undefined,
      stockStatus: filterForm.stockStatus || undefined,
    };

    // 1. 发起请求
    const response = await scmApi.getProducts(params);
    console.log("真实API响应:", response); // 调试用

    // 2. 兼容两种响应格式（优先外层，再内层）
    let records: ScmProduct[] = [];
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
    // 直接返回列表的情况
    else if (Array.isArray(response.data)) {
      records = response.data;
      totalCount = records.length;
    }
    // 适配 Result<PageResult> 结构
    else if (response.records) {
      records = response.records;
      totalCount = response.total || 0;
    }

    total.value = totalCount;

    // 3. 正确的字段映射
    inventoryList.value = records.map((product) => ({
      id: product.id || 0,
      name: product.productName || product.product_name || "未知商品",
      code: product.productCode || product.product_code || "",
      category: product.category || "",
      currentStock: product.stockQuantity || product.stock_quantity || 0,
      minStock: product.minStockQuantity || product.min_stock_quantity || 0,
      unit: product.unit || "个",
      price: product.salePrice || product.sale_price || 0,
      supplier:
        suppliers.value.find((s) => s.id === product.supplierId)
          ?.supplierName ||
        suppliers.value.find((s) => s.id === product.supplierId)?.name ||
        "未知供应商",
      location: "", // API中没有此字段
      status:
        (product.stockQuantity || 0) <= (product.minStockQuantity || 0)
          ? "low"
          : "normal",
      lastUpdate:
        product.updateTime ||
        product.update_time ||
        product.createTime ||
        product.create_time ||
        "",
    }));

    console.log("处理后的数据:", inventoryList.value); // 调试用

    // 计算概览数据
    calculateOverview();
  } catch (error) {
    console.error("加载库存列表失败:", error);
    ElMessage.error("加载库存列表失败");
    inventoryList.value = [];
  } finally {
    loading.value = false;
  }
};

const calculateOverview = () => {
  overview.totalProducts = inventoryList.value.length;
  overview.totalStock = inventoryList.value.reduce(
    (sum, item) => sum + item.currentStock,
    0,
  );
  overview.lowStockItems = inventoryList.value.filter(
    (item) => item.status === "low",
  ).length;
  overview.outOfStockItems = inventoryList.value.filter(
    (item) => item.status === "out",
  ).length;
};

const handleSearch = () => {
  pagination.pageNum = 1;
  loadInventoryList();
};

const handleReset = () => {
  Object.keys(filterForm).forEach((key) => {
    filterForm[key] = "";
  });
  pagination.pageNum = 1;
  loadInventoryList();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadInventoryList();
};

const handleSelectionChange = (selection: InventoryItem[]) => {
  console.log("选中的库存项:", selection);
};

const showInboundDialog = () => {
  inboundDialogVisible.value = true;
  inboundForm.productId = undefined;
  inboundForm.quantity = 1;
  inboundForm.price = 0;
  inboundForm.supplierId = "";
  inboundForm.remark = "";
};

const showOutboundDialog = (productId?: number) => {
  outboundDialogVisible.value = true;
  outboundForm.productId = productId;
  outboundForm.quantity = 1;
  outboundForm.type = "sale";
  outboundForm.remark = "";
};

const handleInbound = async () => {
  if (!inboundFormRef.value) return;

  try {
    await inboundFormRef.value.validate();

    inboundLoading.value = true;

    const params = new URLSearchParams();
    params.append("productId", inboundForm.productId?.toString() || "");
    params.append("quantity", inboundForm.quantity.toString());
    if (inboundForm.supplierId) {
      params.append("supplierId", inboundForm.supplierId);
    }
    if (inboundForm.remark) {
      params.append("remark", inboundForm.remark);
    }
    params.append("createBy", "admin"); // 暂时使用固定值，后续从用户store获取

    await scmApi.inboundStock(params);

    ElMessage.success("商品入库成功");
    inboundDialogVisible.value = false;
    loadInventoryList();
  } catch (error) {
    console.error("入库失败:", error);
  } finally {
    inboundLoading.value = false;
  }
};

const handleOutbound = async () => {
  if (!outboundFormRef.value) return;

  try {
    await outboundFormRef.value.validate();

    outboundLoading.value = true;

    const params = new URLSearchParams();
    params.append("productId", outboundForm.productId?.toString() || "");
    params.append("quantity", outboundForm.quantity.toString());
    if (outboundForm.remark) {
      params.append("remark", outboundForm.remark);
    }
    params.append("createBy", "admin"); // 暂时使用固定值，后续从用户store获取

    await scmApi.outboundStock(params);

    ElMessage.success("商品出库成功");
    outboundDialogVisible.value = false;
    loadInventoryList();
  } catch (error) {
    console.error("出库失败:", error);
  } finally {
    outboundLoading.value = false;
  }
};

const handleAdjust = async () => {
  if (!adjustFormRef.value) return;

  try {
    await adjustFormRef.value.validate();

    adjustLoading.value = true;

    const params = new URLSearchParams();
    params.append("productId", adjustForm.productId);

    // Calculate new quantity based on adjustment type
    const adjustQty = adjustForm.quantity;
    const currentQty = adjustForm.currentStock;
    const newQty =
      adjustForm.adjustType === "add"
        ? currentQty + adjustQty
        : currentQty - adjustQty;

    params.append("newQuantity", newQty.toString());
    params.append("remark", adjustForm.remark);
    params.append("createBy", "admin");

    await scmApi.adjustStock(params);

    ElMessage.success("库存调整成功");
    adjustDialogVisible.value = false;
    loadInventoryList();
  } catch (error) {
    console.error("调整失败:", error);
  } finally {
    adjustLoading.value = false;
  }
};

const inventoryCheck = async () => {
  try {
    // 这里应该弹出盘点对话框，让用户输入盘点数量
    // 暂时模拟一个盘点操作
    const params = new URLSearchParams();
    params.append("productId", "1"); // 示例商品ID
    params.append("actualQuantity", "100"); // 盘点数量
    params.append("remark", "定期盘点");
    params.append("createBy", "admin");

    await scmApi.inventoryCheck(params);
    ElMessage.success("库存盘点完成");
    loadInventoryList();
  } catch (error) {
    console.error("盘点失败:", error);
    ElMessage.error("盘点失败");
  }
};

const adjustStock = (item: InventoryItem) => {
  adjustForm.productId = item.id.toString();
  adjustForm.currentStock = item.currentStock;
  adjustForm.adjustType = "add";
  adjustForm.quantity = 1;
  adjustForm.remark = "";
  adjustDialogVisible.value = true;
};

// 库存记录相关
const historyDialogVisible = ref(false);
const historyLoading = ref(false);
const inventoryRecords = ref<ScmInventoryRecord[]>([]);
const currentHistoryItem = ref<InventoryItem | null>(null);

const viewHistory = async (item: InventoryItem) => {
  currentHistoryItem.value = item;
  historyDialogVisible.value = true;
  await loadInventoryRecords(item.id);
};

const loadInventoryRecords = async (productId: number) => {
  historyLoading.value = true;
  try {
    const res = await scmApi.getInventoryRecords({
      current: 1,
      size: 50, // 获取最近50条记录
      productId: productId,
    });

    let records: ScmInventoryRecord[] = [];
    if (res.records) {
      records = res.records;
    } else if (res.data?.records) {
      records = res.data.records;
    } else if (Array.isArray(res.data)) {
      records = res.data;
    }

    inventoryRecords.value = records;
  } catch (error) {
    console.error("加载库存记录失败:", error);
    ElMessage.error("加载库存记录失败");
  } finally {
    historyLoading.value = false;
  }
};

const getRecordTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    inbound: "入库",
    outbound: "出库",
    adjust: "调整",
    stocktaking: "盘点",
    sale: "销售",
    return: "退货",
    damage: "损耗",
    transfer: "调拨",
    other: "其他",
  };
  return map[type] || type;
};

const getRecordTypeTag = (type: string) => {
  const map: Record<string, string> = {
    inbound: "success",
    outbound: "warning",
    adjust: "info",
    stocktaking: "primary",
    sale: "danger",
    return: "success",
    damage: "info",
    transfer: "warning",
    other: "info",
  };
  return map[type] || "";
};

const handleAction = (command: string, item: InventoryItem) => {
  switch (command) {
    case "inbound":
      showInboundDialog();
      inboundForm.productId = item.id;
      break;
    case "outbound":
      showOutboundDialog(item.id);
      break;
  }
};

const getStockStatusClass = (item: InventoryItem) => {
  if (item.currentStock === 0) return "stock-out";
  if (item.currentStock <= item.minStock) return "stock-low";
  return "stock-normal";
};

const getStockStatusType = (item: InventoryItem) => {
  if (item.currentStock === 0) return "danger";
  if (item.currentStock <= item.minStock) return "warning";
  return "success";
};

const getStockStatusText = (item: InventoryItem) => {
  if (item.currentStock === 0) return "缺货";
  if (item.currentStock <= item.minStock) return "库存不足";
  return "正常";
};

// 监听出库商品选择变化
watch(
  () => outboundForm.productId,
  (newVal) => {
    if (newVal) {
      const product = inventoryList.value.find((item) => item.id === newVal);
      if (product) {
        outboundForm.quantity = Math.min(
          outboundForm.quantity,
          product.currentStock,
        );
      }
    }
  },
);

onMounted(() => {
  loadInventoryList();
  loadSuppliers();
  loadAllProducts();
});
</script>

<style scoped lang="scss">
.inventory {
  .inventory-overview {
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

      &.warning {
        border-left: 4px solid #e6a23c;
      }

      &.danger {
        border-left: 4px solid #f56c6c;
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

  .stock-display {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .stock-normal {
      color: #67c23a;
      font-weight: 500;
    }

    .stock-low {
      color: #e6a23c;
      font-weight: 500;
    }

    .stock-out {
      color: #f56c6c;
      font-weight: 500;
    }

    .unit {
      color: #86909c;
      font-size: 12px;
    }

    .stock-info {
      .min-stock {
        color: #86909c;
        font-size: 12px;
      }
    }
  }

  .stock-value {
    color: #409eff;
    font-weight: 500;
  }

  .text-success {
    color: #67c23a;
    font-weight: bold;
  }

  .text-danger {
    color: #f56c6c;
    font-weight: bold;
  }
}
</style>
