<template>
  <div class="product-list">
    <PageHeader title="商品管理" description="管理宠物商品信息和库存">
      <template #actions>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增商品
        </el-button>
        <el-button @click="batchImport">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
      </template>
    </PageHeader>

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

        <el-form-item label="商品分类">
          <el-select
            v-model="filterForm.category"
            placeholder="请选择分类"
            clearable
            style="width: 120px"
          >
            <el-option
              v-for="category in productCategories"
              :key="category.value"
              :label="category.label"
              :value="category.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="品牌">
          <el-select
            v-model="filterForm.brand"
            placeholder="请选择品牌"
            clearable
            filterable
            style="width: 120px"
          >
            <el-option
              v-for="brand in productBrands"
              :key="brand"
              :label="brand"
              :value="brand"
            />
          </el-select>
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

    <!-- 数据表格 -->
    <DataTable
      :data="productList"
      :columns="columns"
      :loading="loading"
      :total="total"
      :pagination="pagination"
      show-selection
      show-actions
      @selection-change="handleSelectionChange"
      @page-change="handlePageChange"
    >
      <template #column-image="{ row }">
        <el-image
          style="width: 50px; height: 50px; border-radius: 4px"
          :src="row.imageUrl"
          :preview-src-list="row.imageUrl ? [row.imageUrl] : []"
          fit="cover"
          preview-teleported
        >
          <template #error>
            <div class="image-slot">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
      </template>

      <template #column-category="{ row }">
        <el-tag size="small">{{ getCategoryText(row.category) }}</el-tag>
      </template>

      <template #column-price="{ row }">
        <span class="price">¥{{ row.price }}</span>
      </template>

      <template #column-stock="{ row }">
        <div class="stock-info">
          <span :class="getStockClass(row)">{{ row.currentStock }}</span>
          <span class="stock-unit">{{ row.unit }}</span>
          <el-tag
            v-if="row.currentStock <= row.minStock"
            type="warning"
            size="mini"
          >
            库存不足
          </el-tag>
        </div>
      </template>

      <template #column-status="{ row }">
        <el-tag :type="row.status === 'active' ? 'success' : 'info'">
          {{ row.status === "active" ? "上架" : "下架" }}
        </el-tag>
      </template>

      <template #column-actions="{ row }">
        <el-button size="small" @click="viewDetail(row)">详情</el-button>
        <el-button size="small" type="primary" @click="editProduct(row)"
          >编辑</el-button
        >
        <el-button size="small" type="danger" @click="handleDeleteProduct(row)">
          删除
        </el-button>
      </template>
    </DataTable>

    <!-- 新增/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="productRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="name">
              <el-input
                v-model="productForm.name"
                placeholder="请输入商品名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品编码" prop="code">
              <el-input
                v-model="productForm.code"
                placeholder="请输入商品编码"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品分类" prop="category">
              <el-select
                v-model="productForm.category"
                placeholder="请选择分类"
              >
                <el-option
                  v-for="category in productCategories"
                  :key="category.value"
                  :label="category.label"
                  :value="category.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌">
              <el-select
                v-model="productForm.brand"
                placeholder="请选择品牌"
                filterable
                allow-create
                default-first-option
              >
                <el-option
                  v-for="brand in productBrands"
                  :key="brand"
                  :label="brand"
                  :value="brand"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规格型号">
              <el-input
                v-model="productForm.specification"
                placeholder="如：15kg/袋、500ml/瓶"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="productForm.unit" placeholder="请选择单位">
                <el-option label="个" value="个" />
                <el-option label="袋" value="袋" />
                <el-option label="瓶" value="瓶" />
                <el-option label="盒" value="盒" />
                <el-option label="罐" value="罐" />
                <el-option label="kg" value="kg" />
                <el-option label="箱" value="箱" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="零售价" prop="price">
              <el-input-number
                v-model="productForm.price"
                :precision="2"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成本价">
              <el-input-number
                v-model="productForm.costPrice"
                :precision="2"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前库存" prop="currentStock">
              <el-input-number
                v-model="productForm.currentStock"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="安全库存">
              <el-input-number
                v-model="productForm.minStock"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-select
                v-model="productForm.supplierId"
                placeholder="请选择供应商"
                filterable
                clearable
              >
                <el-option
                  v-for="supplier in suppliers"
                  :key="supplier.id"
                  :label="supplier.name"
                  :value="supplier.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="productForm.status">
                <el-radio label="active">上架</el-radio>
                <el-radio label="inactive">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品描述">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>

        <el-form-item label="商品图片">
          <ImageUpload
            v-model="productForm.imageUrl"
            business-type="PRODUCT"
            :business-id="productForm.id"
            @uploading-change="handleImageUploadingChange"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="submitLoading"
            :disabled="imageUploading"
            @click="handleSubmit"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog v-model="productDetailVisible" title="商品详情" width="700px">
      <div
        v-if="currentProduct"
        v-show="currentProduct.imageUrl"
        style="text-align: center; margin-bottom: 20px"
      >
        <el-image
          style="width: 150px; height: 150px; border-radius: 8px"
          :src="currentProduct.imageUrl"
          :preview-src-list="[currentProduct.imageUrl]"
          fit="cover"
        />
      </div>
      <el-descriptions v-if="currentProduct" :column="2" border>
        <el-descriptions-item label="商品名称">{{
          currentProduct.name
        }}</el-descriptions-item>
        <el-descriptions-item label="商品编码">{{
          currentProduct.code
        }}</el-descriptions-item>
        <el-descriptions-item label="分类">
          <el-tag size="small">{{
            getCategoryText(currentProduct.category)
          }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="品牌">{{
          currentProduct.brand || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{
          currentProduct.specification || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{
          currentProduct.unit
        }}</el-descriptions-item>
        <el-descriptions-item label="零售价"
          >¥{{ currentProduct.price }}</el-descriptions-item
        >
        <el-descriptions-item label="成本价"
          >¥{{ currentProduct.costPrice || "-" }}</el-descriptions-item
        >
        <el-descriptions-item label="当前库存">
          <span :class="getStockClass(currentProduct)">{{
            currentProduct.currentStock
          }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="安全库存">{{
          currentProduct.minStock
        }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{
          getSupplierName(currentProduct.supplierId)
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag
            :type="currentProduct.status === 'active' ? 'success' : 'info'"
          >
            {{ currentProduct.status === "active" ? "上架" : "下架" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{
          currentProduct.description || "无"
        }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
} from "element-plus";
import {
  Plus,
  Upload,
  Download,
  Search,
  Refresh,
  ArrowDown,
  Picture,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import DataTable from "@/components/common/DataTable.vue";
import ImageUpload from "@/components/common/ImageUpload.vue";
import { scmApi } from "@/api";
import type { PageParams } from "@/types/common";
import type { ScmProduct, ProductQueryParams } from "@/api/scm";

// 表格列配置
const columns = [
  { prop: "image", label: "图片", width: 80, slot: "column-image" },
  { prop: "name", label: "商品名称", width: 150 },
  { prop: "code", label: "商品编码", width: 120 },
  { prop: "category", label: "分类", width: 100 },
  { prop: "brand", label: "品牌", width: 100 },
  { prop: "price", label: "零售价", width: 100 },
  { prop: "stock", label: "库存", width: 120 },
  { prop: "status", label: "状态", width: 80 },
];

// 商品分类
const productCategories = [
  { label: "主粮", value: "main_food" },
  { label: "零食", value: "snack" },
  { label: "用品", value: "supplies" },
  { label: "药品", value: "medicine" },
  { label: "美容", value: "grooming" },
  { label: "玩具", value: "toy" },
];

// 商品品牌
const productBrands = [
  "皇家",
  "渴望",
  "比瑞吉",
  "网易严选",
  "麦富迪",
  "麦德氏",
  "宝路",
];

// 供应商列表
const suppliers = ref([
  { id: 1, name: "皇家宠物食品有限公司" },
  { id: 2, name: "渴望宠物食品有限公司" },
  { id: 3, name: "本地宠物用品批发商" },
]);

// 商品数据接口
interface Product {
  id: number;
  name: string;
  code: string;
  category: string;
  brand?: string;
  specification?: string;
  unit: string;
  price: number;
  costPrice?: number;
  currentStock: number;
  minStock: number;
  supplierId?: number;
  status: "active" | "inactive";
  description?: string;
  imageUrl?: string;
}

// 响应式数据
const loading = ref(false);
const submitLoading = ref(false);
const imageUploading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref("新增商品");
const isEdit = ref(false);

// 商品列表
const productList = ref<Product[]>([]);
const total = ref(0);
const pagination = reactive<PageParams>({
  pageNum: 1,
  pageSize: 10,
});

// 筛选表单
const filterForm = reactive({
  name: "",
  category: "",
  brand: "",
  stockStatus: "",
  status: "",
});

// 商品表单
const productForm = reactive({
  id: 0,
  name: "",
  code: "",
  category: "",
  brand: "",
  specification: "",
  unit: "袋",
  price: 0,
  costPrice: 0,
  currentStock: 0,
  minStock: 10,
  supplierId: undefined,
  status: "active" as "active" | "inactive",
  description: "",
  imageUrl: "",
});

const productFormRef = ref<FormInstance>();

// 表单验证规则
const productRules: FormRules = {
  name: [
    { required: true, message: "请输入商品名称", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "商品名称长度在 2 到 50 个字符",
      trigger: "blur",
    },
  ],
  code: [
    { required: true, message: "请输入商品编码", trigger: "blur" },
    {
      pattern: /^[A-Z0-9-_]+$/,
      message: "商品编码只能包含字母、数字、下划线和连字符",
      trigger: "blur",
    },
  ],
  category: [{ required: true, message: "请选择商品分类", trigger: "change" }],
  price: [
    { required: true, message: "请输入零售价", trigger: "blur" },
    { type: "number", min: 0, message: "零售价必须大于等于0", trigger: "blur" },
  ],
  currentStock: [
    { required: true, message: "请输入当前库存", trigger: "blur" },
    {
      type: "number",
      min: 0,
      message: "库存数量必须大于等于0",
      trigger: "blur",
    },
  ],
  unit: [{ required: true, message: "请选择单位", trigger: "change" }],
};

// API 类型
interface BackendScmProduct {
  id?: number;
  productName?: string;
  productCode?: string;
  category?: string;
  brand?: string;
  specification?: string;
  unit?: string;
  salePrice?: number;
  purchasePrice?: number;
  stockQuantity?: number;
  minStockQuantity?: number;
  supplierId?: number;
  status?: number;
  description?: string;
  createTime?: string;
  updateTime?: string;
  imageUrl?: string;
}

interface ProductListResponse {
  code: number;
  total?: number;
  records?: BackendScmProduct[];
  data?: {
    records?: BackendScmProduct[];
    total?: number;
  };
  message?: string;
}

const loadProductList = async () => {
  loading.value = true;
  try {
    const params: ProductQueryParams = {
      current: pagination.pageNum,
      size: pagination.pageSize,
      productName: filterForm.name || undefined,
      category: filterForm.category || undefined,
      brand: filterForm.brand || undefined,
      stockStatus: filterForm.stockStatus || undefined,
      status: filterForm.status
        ? filterForm.status === "active"
          ? 1
          : 0
        : undefined,
    };

    const response = await scmApi.getProducts<ProductListResponse>(params);

    let records: BackendScmProduct[] = [];
    let totalCount = 0;

    if (Array.isArray(response.records)) {
      records = response.records;
      totalCount = response.total || 0;
    } else if (response.data?.data?.records) {
      records = response.data.data.records;
      totalCount = response.data.data.total || 0;
    } else if (response.data?.records) {
      records = response.data.records;
      totalCount = response.data.total || 0;
    }

    total.value = totalCount;

    productList.value = records.map((product) => ({
      id: product.id || 0,
      name: product.productName || "",
      code: product.productCode || "",
      category: product.category || "",
      brand: product.brand || "",
      specification: product.specification || "",
      unit: product.unit || "",
      price: product.salePrice || 0,
      costPrice: product.purchasePrice || 0,
      currentStock: product.stockQuantity || 0,
      minStock: product.minStockQuantity || 0,
      supplierId: product.supplierId || 0,
      status: product.status === 1 ? "active" : "inactive",
      description: product.description || "",
      imageUrl: product.imageUrl || "",
    }));
  } catch (error) {
    console.error("加载商品列表失败:", error);
    ElMessage.error("加载商品列表失败");
    productList.value = [];
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.pageNum = 1;
  loadProductList();
};

const handleReset = () => {
  Object.keys(filterForm).forEach((key) => {
    filterForm[key] = "";
  });
  pagination.pageNum = 1;
  loadProductList();
};

const handlePageChange = (pageNum: number, pageSize: number) => {
  pagination.pageNum = pageNum;
  pagination.pageSize = pageSize;
  loadProductList();
};

const handleSelectionChange = (selection: Product[]) => {
  console.log("选中的商品:", selection);
};

const showAddDialog = () => {
  dialogTitle.value = "新增商品";
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const editProduct = (product: Product) => {
  dialogTitle.value = "编辑商品";
  isEdit.value = true;
  imageUploading.value = false;
  Object.assign(productForm, {
    id: product.id,
    name: product.name,
    code: product.code,
    category: product.category,
    brand: product.brand,
    specification: product.specification,
    unit: product.unit,
    price: product.price,
    costPrice: product.costPrice,
    currentStock: product.currentStock,
    minStock: product.minStock,
    supplierId: product.supplierId,
    status: product.status,
    description: product.description,
    imageUrl: product.imageUrl,
  });
  dialogVisible.value = true;
};

const resetForm = () => {
  imageUploading.value = false;
  productForm.id = 0;
  productForm.name = "";
  productForm.code = "";
  productForm.category = "";
  productForm.brand = "";
  productForm.specification = "";
  productForm.unit = "袋";
  productForm.price = 0;
  productForm.costPrice = 0;
  productForm.currentStock = 0;
  productForm.minStock = 10;
  productForm.supplierId = undefined;
  productForm.status = "active";
  productForm.description = "";
  productForm.imageUrl = "";
};

const handleImageUploadingChange = (value: boolean) => {
  imageUploading.value = value;
};

const handleSubmit = async () => {
  if (!productFormRef.value) return;

  try {
    await productFormRef.value.validate();

    if (imageUploading.value) {
      ElMessage.warning("图片上传中，请稍后再提交");
      return;
    }

    submitLoading.value = true;

    const productData: ScmProduct = {
      productName: productForm.name,
      productCode: productForm.code,
      category: productForm.category,
      brand: productForm.brand,
      specification: productForm.specification,
      description: productForm.description,
      salePrice: productForm.price,
      purchasePrice: productForm.costPrice,
      stockQuantity: productForm.currentStock,
      minStockQuantity: productForm.minStock,
      unit: productForm.unit,
      supplierId: productForm.supplierId,
      status: productForm.status === "active" ? 1 : 0,
      imageUrl: productForm.imageUrl,
    };

    if (isEdit.value) {
      await scmApi.updateProduct(productForm.id, productData);
      ElMessage.success("商品信息更新成功");
    } else {
      await scmApi.createProduct(productData);
      ElMessage.success("商品添加成功");
    }

    dialogVisible.value = false;
    loadProductList();
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitLoading.value = false;
  }
};

const productDetailVisible = ref(false);
const currentProduct = ref<Product | null>(null);

const viewDetail = (product: Product) => {
  currentProduct.value = product;
  productDetailVisible.value = true;
};

const getSupplierName = (supplierId?: number) => {
  if (!supplierId) return "-";
  const supplier = suppliers.value.find((s) => s.id === supplierId);
  return supplier ? supplier.name : "未知供应商";
};

const handleDeleteProduct = async (product: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品"${product.name}"吗？此操作不可撤销。`,
      "确认删除",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );
    await scmApi.deleteProduct(product.id);
    ElMessage.success("商品删除成功");
    loadProductList();
  } catch (e) {
    return;
  }
};

const batchImport = () => {
  ElMessage.info("批量导入功能开发中");
};

const getCategoryText = (category: string) => {
  const categoryObj = productCategories.find((c) => c.value === category);
  return categoryObj?.label || category;
};

const getStockClass = (product: Product) => {
  if (product.currentStock <= product.minStock) {
    return "stock-warning";
  }
  return "stock-normal";
};

onMounted(() => {
  loadProductList();
});
</script>

<style scoped lang="scss">
.product-list {
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

  .price {
    color: #f56c6c;
    font-weight: 500;
  }

  .stock-info {
    display: flex;
    align-items: center;
    gap: 4px;

    .stock-warning {
      color: #e6a23c;
      font-weight: 500;
    }

    .stock-normal {
      color: #67c23a;
    }

    .stock-unit {
      color: #86909c;
      font-size: 12px;
    }
  }

  .image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background: #f5f7fa;
    color: #909399;
    font-size: 20px;
  }
}
</style>
