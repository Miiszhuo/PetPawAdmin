<template>
  <div class="pet-detail">
    <PageHeader
      :title="`宠物详情 - ${petInfo?.name || ''}`"
      description="查看宠物详细信息及健康记录"
    >
      <template #actions>
        <el-button @click="$router.go(-1)">
          <el-icon><Back /></el-icon>
          返回
        </el-button>
        <el-button type="primary" @click="editPet">
          <el-icon><Edit /></el-icon>
          编辑信息
        </el-button>
        <el-button @click="addHealthRecord">
          <el-icon><Plus /></el-icon>
          添加记录
        </el-button>
      </template>
    </PageHeader>

    <div class="detail-content">
      <!-- 宠物基本信息 -->
      <div class="info-section">
        <div class="pet-avatar">
          <el-avatar :size="120" :src="petInfo?.avatar">
            {{ petInfo?.name?.charAt(0) }}
          </el-avatar>
        </div>
        <div class="pet-info">
          <h2>{{ petInfo?.name }}</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">主人：</span>
              <span class="value">{{ petInfo?.ownerName }}</span>
            </div>
            <div class="info-item">
              <span class="label">品种：</span>
              <span class="value">{{ petInfo?.breed }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别：</span>
              <el-tag :type="petInfo?.gender === 'male' ? 'primary' : 'danger'">
                {{ petInfo?.gender === "male" ? "♂ 公" : "♀ 母" }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">年龄：</span>
              <span class="value">{{ calculateAge(petInfo?.birthday) }}</span>
            </div>
            <div class="info-item">
              <span class="label">体重：</span>
              <span class="value">{{ petInfo?.weight }} kg</span>
            </div>
            <div class="info-item">
              <span class="label">血型：</span>
              <span class="value">{{ petInfo?.bloodType || "未知" }}</span>
            </div>
            <div class="info-item">
              <span class="label">绝育：</span>
              <el-tag :type="petInfo?.neutered ? 'success' : 'warning'">
                {{ petInfo?.neutered ? "已绝育" : "未绝育" }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">状态：</span>
              <el-tag :type="getStatusType(petInfo?.status)">
                {{ getStatusText(petInfo?.status) }}
              </el-tag>
            </div>
          </div>
          <div class="additional-info">
            <div v-if="petInfo?.allergies" class="info-item">
              <span class="label">过敏源：</span>
              <span class="value">{{ petInfo.allergies }}</span>
            </div>
            <div v-if="petInfo?.remark" class="info-item">
              <span class="label">备注：</span>
              <span class="value">{{ petInfo.remark }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 健康记录时间轴 -->
      <div class="health-timeline">
        <div class="timeline-header">
          <h3>健康记录时间轴</h3>
          <el-radio-group v-model="timelineFilter" size="small">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="vaccination">疫苗</el-radio-button>
            <el-radio-button label="treatment">治疗</el-radio-button>
            <el-radio-button label="checkup">体检</el-radio-button>
          </el-radio-group>
        </div>

        <el-timeline>
          <el-timeline-item
            v-for="record in filteredRecords"
            :key="record.id"
            :timestamp="formatDate(record.date)"
            :type="getRecordType(record.type)"
            :hollow="record.type === 'checkup'"
          >
            <div class="record-content">
              <div class="record-header">
                <h4>{{ getRecordTypeText(record.type) }}</h4>
                <el-tag size="small" :type="getRecordType(record.type)">
                  {{ record.doctor || "兽医师" }}
                </el-tag>
              </div>
              <div class="record-body">
                <p v-if="record.description">{{ record.description }}</p>
                <div v-if="record.details" class="record-details">
                  <div
                    v-for="(detail, index) in record.details"
                    :key="index"
                    class="detail-item"
                  >
                    <span class="detail-label">{{ detail.label }}：</span>
                    <span class="detail-value">{{ detail.value }}</span>
                  </div>
                </div>
              </div>
              <div v-if="record.nextDate" class="record-footer">
                <el-alert
                  :title="`下次${getRecordTypeText(record.type)}时间：${formatDate(record.nextDate)}`"
                  type="warning"
                  :closable="false"
                  size="small"
                />
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>

        <div v-if="filteredRecords.length === 0" class="no-records">
          <el-empty description="暂无健康记录">
            <el-button type="primary" @click="addHealthRecord"
              >添加第一条记录</el-button
            >
          </el-empty>
        </div>
      </div>

      <!-- 健康统计 -->
      <div class="health-stats">
        <h3>健康统计</h3>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="32" color="#67c23a"><Check /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ healthStats.totalRecords }}</div>
                <div class="stat-label">总记录数</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="32" color="#409eff"><Timer /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ healthStats.lastVaccination }}</div>
                <div class="stat-label">上次疫苗</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="32" color="#e6a23c"><Warning /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">
                  {{ healthStats.upcomingReminders }}
                </div>
                <div class="stat-label">待办提醒</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon size="32" color="#f56c6c"><Warning /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ healthStats.totalTreatments }}</div>
                <div class="stat-label">治疗次数</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  Back,
  Edit,
  Plus,
  Check,
  Timer,
  Warning,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";

// 宠物信息接口
interface PetInfo {
  id: number;
  name: string;
  ownerName: string;
  breed: string;
  gender: "male" | "female";
  birthday: string;
  weight: number;
  bloodType?: string;
  neutered: boolean;
  allergies?: string;
  remark?: string;
  status: "active" | "in_treatment" | "deceased";
  avatar?: string;
}

// 健康记录接口
interface HealthRecord {
  id: number;
  type: "vaccination" | "treatment" | "checkup" | "surgery";
  date: string;
  description: string;
  doctor?: string;
  nextDate?: string;
  details?: Array<{ label: string; value: string }>;
}

const route = useRoute();
const router = useRouter();

// 宠物信息
const petInfo = ref<PetInfo | null>(null);

// 健康记录
const healthRecords = ref<HealthRecord[]>([]);

// 时间轴筛选
const timelineFilter = ref("all");

// 健康统计
const healthStats = reactive({
  totalRecords: 0,
  lastVaccination: "暂无",
  upcomingReminders: 0,
  totalTreatments: 0,
});

// 加载宠物详情
const loadPetDetail = async () => {
  const petId = route.params.id as string;

  try {
    const response = await crmApi.getPet(parseInt(petId));

    // 将API数据转换为前端显示格式
    petInfo.value = {
      id: response.id || parseInt(petId),
      name: response.petName || "",
      ownerName: "客户" + response.customerId, // 暂时用ID表示，后续可关联查询
      breed: response.breed || "",
      gender: response.gender === 1 ? "male" : "female",
      birthday: response.birthday || "",
      weight: response.weight || 0,
      bloodType: response.bloodType || "",
      neutered: response.neutered || false,
      allergies: response.allergies || "",
      status: response.status === 1 ? "active" : "inactive",
      remark: response.remarks || "",
    };

    // 模拟健康记录
    healthRecords.value = [
      {
        id: 1,
        type: "vaccination",
        date: "2024-01-15",
        description: "完成年度疫苗接种",
        doctor: "李医生",
        nextDate: "2025-01-15",
        details: [
          { label: "疫苗种类", value: "六联疫苗" },
          { label: "疫苗批号", value: "V202401001" },
        ],
      },
      {
        id: 2,
        type: "checkup",
        date: "2024-01-10",
        description: "年度体检",
        doctor: "王医生",
        nextDate: "2025-01-10",
        details: [
          { label: "体重", value: "25.5kg" },
          { label: "体温", value: "38.5°C" },
          { label: "心率", value: "120次/分" },
        ],
      },
      {
        id: 3,
        type: "treatment",
        date: "2023-12-20",
        description: "皮肤过敏治疗",
        doctor: "赵医生",
        details: [
          { label: "诊断", value: "食物过敏" },
          { label: "治疗方案", value: "抗过敏药物+饮食调整" },
          { label: "用药", value: "扑尔敏、益生菌" },
        ],
      },
      {
        id: 4,
        type: "vaccination",
        date: "2023-01-15",
        description: "完成年度疫苗接种",
        doctor: "李医生",
        nextDate: "2024-01-15",
        details: [
          { label: "疫苗种类", value: "狂犬疫苗" },
          { label: "疫苗批号", value: "R202301001" },
        ],
      },
    ];

    // 计算统计数据
    calculateHealthStats();
  } catch (error) {
    ElMessage.error("加载宠物详情失败");
  }
};

// 计算健康统计
const calculateHealthStats = () => {
  healthStats.totalRecords = healthRecords.value.length;
  healthStats.totalTreatments = healthRecords.value.filter(
    (r) => r.type === "treatment",
  ).length;

  const vaccinationRecords = healthRecords.value.filter(
    (r) => r.type === "vaccination",
  );
  if (vaccinationRecords.length > 0) {
    healthStats.lastVaccination = formatDate(vaccinationRecords[0].date);
  }

  // 计算即将到期的提醒
  const now = new Date();
  healthStats.upcomingReminders = healthRecords.value.filter((record) => {
    if (record.nextDate) {
      const nextDate = new Date(record.nextDate);
      const diffDays = Math.ceil(
        (nextDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24),
      );
      return diffDays <= 30 && diffDays >= 0; // 30天内到期
    }
    return false;
  }).length;
};

// 筛选后的记录
const filteredRecords = computed(() => {
  if (timelineFilter.value === "all") {
    return healthRecords.value;
  }
  return healthRecords.value.filter(
    (record) => record.type === timelineFilter.value,
  );
});

// 方法
const editPet = () => {
  ElMessage.info("编辑宠物信息功能开发中");
};

const addHealthRecord = () => {
  ElMessage.info("添加健康记录功能开发中");
};

const calculateAge = (birthday?: string) => {
  if (!birthday) return "-";
  const birth = new Date(birthday);
  const now = new Date();
  const ageInMs = now.getTime() - birth.getTime();
  const ageInYears = ageInMs / (1000 * 60 * 60 * 24 * 365.25);
  const years = Math.floor(ageInYears);
  const months = Math.floor((ageInYears - years) * 12);
  return `${years}岁${months}个月`;
};

const getStatusType = (status?: string) => {
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

const getStatusText = (status?: string) => {
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

const getRecordType = (type: string) => {
  switch (type) {
    case "vaccination":
      return "success";
    case "treatment":
      return "danger";
    case "checkup":
      return "info";
    case "surgery":
      return "warning";
    default:
      return "info";
  }
};

const getRecordTypeText = (type: string) => {
  switch (type) {
    case "vaccination":
      return "疫苗接种";
    case "treatment":
      return "治疗记录";
    case "checkup":
      return "体检记录";
    case "surgery":
      return "手术记录";
    default:
      return type;
  }
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

onMounted(() => {
  loadPetDetail();
});
</script>

<style scoped lang="scss">
.pet-detail {
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

      .pet-avatar {
        flex-shrink: 0;
      }

      .pet-info {
        flex: 1;

        h2 {
          margin: 0 0 20px 0;
          color: #1d2129;
          font-size: 24px;
          font-weight: 600;
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
              min-width: 60px;
            }

            .value {
              color: #1d2129;
            }
          }
        }

        .additional-info {
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

    .health-timeline {
      background-color: #fff;
      padding: 30px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .timeline-header {
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

      .record-content {
        .record-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;

          h4 {
            margin: 0;
            font-size: 16px;
            font-weight: 500;
            color: #1d2129;
          }
        }

        .record-body {
          margin-bottom: 12px;

          p {
            margin: 0 0 8px 0;
            color: #4e5969;
            line-height: 1.5;
          }

          .record-details {
            .detail-item {
              display: flex;
              margin-bottom: 4px;

              .detail-label {
                font-weight: 500;
                color: #86909c;
                min-width: 80px;
              }

              .detail-value {
                color: #1d2129;
              }
            }
          }
        }

        .record-footer {
          margin-top: 12px;
        }
      }

      .no-records {
        text-align: center;
        padding: 40px 0;
      }
    }

    .health-stats {
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
  }
}
</style>
