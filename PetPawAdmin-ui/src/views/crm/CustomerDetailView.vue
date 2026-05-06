<template>
  <div class="customer-detail">
    <PageHeader
      :title="`客户详情 - ${customerInfo?.nickname || customerInfo?.name || ''}`"
      description="查看客户详细信息及消费记录"
    >
      <template #actions>
        <el-button @click="$router.go(-1)">
          <el-icon><Back /></el-icon>
          返回
        </el-button>
        <el-button type="primary" @click="editCustomer">
          <el-icon><Edit /></el-icon>
          编辑信息
        </el-button>
      </template>
    </PageHeader>

    <div class="detail-content">
      <!-- 客户基本信息 -->
      <div class="info-section">
        <div class="customer-avatar">
          <el-avatar :size="120" :src="customerInfo?.avatar">
            {{
              customerInfo?.nickname?.charAt(0) || customerInfo?.name?.charAt(0)
            }}
          </el-avatar>
        </div>
        <div class="customer-info">
          <div class="name-section">
            <h2>{{ customerInfo?.nickname || customerInfo?.name }}</h2>
            <el-tag :type="getLevelType(customerInfo?.level)" size="large">
              {{ getLevelText(customerInfo?.level) }}
            </el-tag>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ customerInfo?.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话：</span>
              <span class="value">{{ customerInfo?.phone }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别：</span>
              <span class="value">{{
                customerInfo?.gender === "male" ? "男" : "女"
              }}</span>
            </div>
            <div class="info-item">
              <span class="label">年龄：</span>
              <span class="value">{{
                calculateAge(customerInfo?.birthday)
              }}</span>
            </div>
            <div class="info-item">
              <span class="label">注册时间：</span>
              <span class="value">{{
                formatDate(customerInfo?.createdTime)
              }}</span>
            </div>
            <div class="info-item">
              <span class="label">最后到店：</span>
              <span class="value">{{
                formatDate(customerInfo?.lastVisitTime)
              }}</span>
            </div>
          </div>
          <div class="contact-info">
            <div v-if="customerInfo?.address" class="info-item">
              <span class="label">联系地址：</span>
              <span class="value">{{ customerInfo.address }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 消费统计 -->
      <div class="stats-section">
        <h3>消费统计</h3>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="32" color="#409eff"><Money /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">
                  ¥{{ consumptionStats.totalAmount.toLocaleString() }}
                </div>
                <div class="stat-label">累计消费</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="32" color="#67c23a"><ShoppingCart /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ consumptionStats.totalOrders }}</div>
                <div class="stat-label">消费次数</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="32" color="#e6a23c"><TrendCharts /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">¥{{ consumptionStats.avgAmount }}</div>
                <div class="stat-label">平均客单价</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="32" color="#f56c6c"><Timer /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">
                  {{ consumptionStats.lastVisitDays }}天
                </div>
                <div class="stat-label">距上次消费</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 宠物列表 -->
      <div class="pets-section">
        <div class="section-header">
          <h3>名下宠物 ({{ pets.length }})</h3>
        </div>
        <div v-if="pets.length > 0" class="pets-grid">
          <div
            v-for="pet in pets"
            :key="pet.id"
            class="pet-card"
            @click="viewPet(pet.id)"
          >
            <div class="pet-avatar">
              <el-avatar :size="60">
                <img :src="pet.avatar" alt="宠物头像" />
              </el-avatar>
            </div>
            <div class="pet-info">
              <h4>{{ pet.name }}</h4>
              <p>{{ pet.breed }}</p>
              <div class="pet-tags">
                <el-tag
                  size="small"
                  :type="pet.gender === 'male' ? 'primary' : 'danger'"
                >
                  {{ pet.gender === "male" ? "♂" : "♀" }}
                </el-tag>
                <el-tag size="small">{{ calculateAge(pet.birthday) }}</el-tag>
              </div>
            </div>
            <div class="pet-status">
              <el-tag :type="getPetStatusType(pet.status)" size="small">
                {{ getPetStatusText(pet.status) }}
              </el-tag>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无宠物信息" />
      </div>

      <!-- 消费记录 -->
      <div class="consumption-section">
        <div class="section-header">
          <h3>消费记录</h3>
          <el-button
            type="text"
            size="small"
            @click="$router.push('/finance/members')"
            >查看全部</el-button
          >
        </div>
        <el-table :data="consumptionRecords" style="width: 100%" size="small">
          <el-table-column prop="date" label="消费日期" width="120">
            <template #default="scope">
              {{ formatDate(scope.row.date) }}
            </template>
          </el-table-column>
          <el-table-column prop="orderNo" label="订单号" width="150" />
          <el-table-column prop="serviceName" label="服务项目" width="150" />
          <el-table-column prop="amount" label="金额" width="100">
            <template #default="scope"> ¥{{ scope.row.amount }} </template>
          </el-table-column>
          <el-table-column prop="petName" label="宠物" width="100" />
          <el-table-column prop="staffName" label="服务人员" width="100" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag
                :type="scope.row.status === 'completed' ? 'success' : 'info'"
                size="small"
              >
                {{ scope.row.status === "completed" ? "已完成" : "进行中" }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  Back,
  Edit,
  Money,
  ShoppingCart,
  TrendCharts,
  Timer,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";

import { crmApi } from "@/api/crm";

// 客户信息接口
interface CustomerInfo {
  id: number;
  name: string;
  nickname?: string;
  phone: string;
  gender: "male" | "female";
  birthday?: string;
  address?: string;
  level: "normal" | "vip" | "diamond";
  avatar?: string;
  createdTime: string;
  lastVisitTime?: string;
}

// 宠物信息接口
interface Pet {
  id: number;
  name: string;
  breed: string;
  gender: "male" | "female";
  birthday: string;
  avatar?: string;
  status: "active" | "in_treatment" | "deceased";
}

// 消费记录接口
interface ConsumptionRecord {
  id: number;
  date: string;
  orderNo: string;
  serviceName: string;
  amount: number;
  petName: string;
  staffName: string;
  status: "completed" | "in_progress";
}

const route = useRoute();
const router = useRouter();

// 客户信息
const customerInfo = ref<CustomerInfo | null>(null);

// 宠物列表
const pets = ref<Pet[]>([]);

// 消费记录
const consumptionRecords = ref<ConsumptionRecord[]>([]);

// 消费统计
const consumptionStats = reactive({
  totalAmount: 0,
  totalOrders: 0,
  avgAmount: 0,
  lastVisitDays: 0,
});

// 加载客户详情
const loadCustomerDetail = async () => {
  const customerId = route.params.id as string;

  try {
    const response = await crmApi.getCustomer(parseInt(customerId));

    // 将API数据转换为前端显示格式
    customerInfo.value = {
      id: response.id || parseInt(customerId),
      name: response.customerName || "",
      nickname: "", // API中没有此字段
      phone: response.phone || "",
      gender: response.gender === 1 ? "male" : "female",
      birthday: response.birthday || "",
      address: response.address || "",
      level: (response.customerType as any) || "normal", // 使用 customerType 映射 level
      createdTime: response.createTime || "",
      lastVisitTime: response.lastVisitTime || response.updateTime || "",
    };

    // 模拟宠物列表
    pets.value = [
      {
        id: 1,
        name: "旺财",
        breed: "金毛犬",
        gender: "male",
        birthday: "2020-05-15",
        status: "active",
      },
      {
        id: 2,
        name: "小白",
        breed: "波斯猫",
        gender: "female",
        birthday: "2019-08-20",
        status: "active",
      },
    ];

    // 模拟消费记录
    consumptionRecords.value = [
      {
        id: 1,
        date: "2024-01-15",
        orderNo: "ORD20240115001",
        serviceName: "宠物洗澡",
        amount: 150,
        petName: "旺财",
        staffName: "李师傅",
        status: "completed",
      },
      {
        id: 2,
        date: "2024-01-10",
        orderNo: "ORD20240110001",
        serviceName: "疫苗接种",
        amount: 200,
        petName: "小白",
        staffName: "王医生",
        status: "completed",
      },
      {
        id: 3,
        date: "2023-12-20",
        orderNo: "ORD20231220001",
        serviceName: "宠物美容",
        amount: 300,
        petName: "旺财",
        staffName: "赵师傅",
        status: "completed",
      },
    ];

    // 计算消费统计
    calculateConsumptionStats();
  } catch (error) {
    ElMessage.error("加载客户详情失败");
  }
};

// 计算消费统计
const calculateConsumptionStats = () => {
  consumptionStats.totalOrders = consumptionRecords.value.length;
  consumptionStats.totalAmount = consumptionRecords.value.reduce(
    (sum, record) => sum + record.amount,
    0,
  );
  consumptionStats.avgAmount = Math.round(
    consumptionStats.totalAmount / consumptionStats.totalOrders,
  );

  if (customerInfo.value?.lastVisitTime) {
    const lastVisit = new Date(customerInfo.value.lastVisitTime);
    const now = new Date();
    consumptionStats.lastVisitDays = Math.floor(
      (now.getTime() - lastVisit.getTime()) / (1000 * 60 * 60 * 24),
    );
  }
};

// 方法
const editCustomer = () => {
  // TODO: 实现编辑客户逻辑
  console.log("编辑客户", customerId);
};

const viewPet = (petId: number) => {
  router.push(`/crm/pet/${petId}`);
};

const calculateAge = (birthday?: string) => {
  if (!birthday) return "-";
  const birth = new Date(birthday);
  const now = new Date();
  const age = now.getFullYear() - birth.getFullYear();
  return `${age}岁`;
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

const getLevelType = (level?: string) => {
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

const getLevelText = (level?: string) => {
  switch (level) {
    case "diamond":
      return "钻石会员";
    case "vip":
      return "VIP会员";
    case "normal":
      return "普通会员";
    default:
      return "普通会员";
  }
};

const getPetStatusType = (status: string) => {
  switch (status) {
    case "active":
      return "success";
    case "in_treatment":
      return "warning";
    case "deceased":
      return "danger";
    default:
      return "info";
  }
};

const getPetStatusText = (status: string) => {
  switch (status) {
    case "active":
      return "正常";
    case "in_treatment":
      return "就诊中";
    case "deceased":
      return "已离世";
    default:
      return "正常";
  }
};

onMounted(() => {
  loadCustomerDetail();
});
</script>

<style scoped lang="scss">
.customer-detail {
  .detail-content {
    display: flex;
    flex-direction: column;
    gap: 20px;

    .info-section {
      background-color: #fff;
      padding: 30px;
      border-radius: 8px;
      display: flex;
      gap: 30px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .customer-avatar {
        flex-shrink: 0;
      }

      .customer-info {
        flex: 1;

        .name-section {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 20px;

          h2 {
            margin: 0;
            color: #1d2129;
            font-size: 24px;
            font-weight: 600;
          }
        }

        .info-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 12px 20px;
          margin-bottom: 20px;

          .info-item {
            display: flex;
            align-items: center;

            .label {
              font-weight: 500;
              color: #86909c;
              min-width: 80px;
            }

            .value {
              color: #1d2129;
            }
          }
        }

        .contact-info {
          .info-item {
            margin-bottom: 8px;

            .label {
              font-weight: 500;
              color: #86909c;
            }

            .value {
              color: #1d2129;
            }
          }
        }
      }
    }

    .stats-section {
      background-color: #fff;
      padding: 30px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      h3 {
        margin: 0 0 20px 0;
        font-size: 18px;
        font-weight: 600;
        color: #1d2129;
      }

      .stat-card {
        background-color: #f7f8fa;
        padding: 20px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        transition: transform 0.2s;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .stat-icon {
          margin-right: 16px;
        }

        .stat-content {
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #1d2129;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 14px;
            color: #86909c;
          }
        }
      }
    }

    .pets-section,
    .consumption-section {
      background-color: #fff;
      padding: 30px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        h3 {
          margin: 0;
          font-size: 18px;
          font-weight: 600;
          color: #1d2129;
        }
      }

      .pets-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 16px;

        .pet-card {
          display: flex;
          align-items: center;
          padding: 16px;
          border: 1px solid #e5e6eb;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.2s;

          &:hover {
            border-color: #409eff;
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
          }

          .pet-avatar {
            margin-right: 12px;
          }

          .pet-info {
            flex: 1;

            h4 {
              margin: 0 0 4px 0;
              font-size: 16px;
              font-weight: 500;
              color: #1d2129;
            }

            p {
              margin: 0 0 8px 0;
              color: #86909c;
              font-size: 14px;
            }

            .pet-tags {
              display: flex;
              gap: 8px;
            }
          }

          .pet-status {
            margin-left: 12px;
          }
        }
      }

      .no-pets {
        text-align: center;
        padding: 40px 0;
      }
    }
  }
}
</style>
