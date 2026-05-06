<template>
  <div class="store-ops">
    <PageHeader
      title="门店设置"
      description="管理门店基础信息、营业时间与公告展示"
    />

    <el-row :gutter="20" class="content">
      <el-col :xs="24" :lg="10">
        <div class="card">
          <div class="card-title">基础信息</div>
          <el-form :model="storeForm" label-width="90px">
            <el-form-item label="门店名称">
              <el-input
                v-model="storeForm.storeName"
                placeholder="请输入门店名称"
              />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input
                v-model="storeForm.phone"
                placeholder="请输入联系电话"
              />
            </el-form-item>
            <el-form-item label="门店地址">
              <el-input
                v-model="storeForm.address"
                placeholder="请输入门店地址"
              />
            </el-form-item>
            <el-form-item label="营业时间">
              <el-time-picker
                v-model="storeForm.businessHours"
                is-range
                range-separator="至"
                start-placeholder="开始"
                end-placeholder="结束"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveStoreInfo">保存</el-button>
              <el-button @click="resetStoreInfo">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <el-col :xs="24" :lg="14">
        <div class="card">
          <div class="card-title">公告管理</div>
          <div class="notice-actions">
            <el-input
              v-model="newNotice"
              placeholder="输入公告内容后添加"
              clearable
            />
            <el-button type="primary" @click="addNotice">添加</el-button>
          </div>
          <el-table :data="notices" style="width: 100%" border>
            <el-table-column prop="content" label="公告内容" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button size="small" @click="removeNotice(row.id)"
                  >删除</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import PageHeader from "@/components/common/PageHeader.vue";

interface Notice {
  id: number;
  content: string;
  createTime: string;
}

const storeForm = reactive({
  storeName: "PetPaw旗舰店",
  phone: "400-888-8888",
  address: "北京市朝阳区示例路 88 号",
  businessHours: [
    new Date(2025, 11, 29, 9, 0, 0),
    new Date(2025, 11, 29, 21, 0, 0),
  ] as [Date, Date],
});

const newNotice = ref("");

const notices = ref<Notice[]>([
  {
    id: 1,
    content: "元旦活动：洗护项目 9 折起",
    createTime: "2025-12-28 10:30:00",
  },
  {
    id: 2,
    content: "请提前预约高峰时段服务",
    createTime: "2025-12-20 09:00:00",
  },
]);

const saveStoreInfo = () => {
  ElMessage.success("门店信息已保存");
};

const resetStoreInfo = () => {
  storeForm.storeName = "PetPaw旗舰店";
  storeForm.phone = "400-888-8888";
  storeForm.address = "北京市朝阳区示例路 88 号";
  storeForm.businessHours = [
    new Date(2025, 11, 29, 9, 0, 0),
    new Date(2025, 11, 29, 21, 0, 0),
  ];
  ElMessage.success("已重置");
};

const addNotice = () => {
  const content = newNotice.value.trim();
  if (!content) return;
  notices.value.unshift({
    id: Date.now(),
    content,
    createTime: new Date().toLocaleString(),
  });
  newNotice.value = "";
  ElMessage.success("已添加公告");
};

const removeNotice = (id: number) => {
  notices.value = notices.value.filter((x) => x.id !== id);
  ElMessage.success("已删除");
};
</script>

<style scoped lang="scss">
.store-ops {
  .content {
    .card {
      background-color: #fff;
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      margin-bottom: 20px;

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: #1d2129;
        margin-bottom: 16px;
      }

      .notice-actions {
        display: flex;
        gap: 12px;
        margin-bottom: 12px;
      }
    }
  }
}
</style>
