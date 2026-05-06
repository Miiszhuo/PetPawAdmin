<template>
  <div class="dashboard">
    <PageHeader title="数据大屏" description="实时监控业务数据，洞察经营状况">
      <template #actions>
        <el-button type="primary" @click="refreshData">
          <el-icon><RefreshRight /></el-icon>
          刷新数据
        </el-button>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="default"
          @change="handleDateChange"
        />
      </template>
    </PageHeader>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon size="32" color="#409eff"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.todayCustomers }}</div>
              <div class="stat-label">今日客户</div>
              <div
                class="stat-trend"
                :class="{
                  'trend-up': stats.customerGrowth > 0,
                  'trend-down': stats.customerGrowth < 0,
                }"
              >
                <el-icon size="14"
                  ><Top v-if="stats.customerGrowth > 0" /><Bottom v-else
                /></el-icon>
                {{ Math.abs(stats.customerGrowth) }}%
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon size="32" color="#67c23a"><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">
                ¥{{ stats.todayRevenue.toLocaleString() }}
              </div>
              <div class="stat-label">今日营收</div>
              <div
                class="stat-trend"
                :class="{
                  'trend-up': stats.revenueGrowth > 0,
                  'trend-down': stats.revenueGrowth < 0,
                }"
              >
                <el-icon size="14"
                  ><Top v-if="stats.revenueGrowth > 0" /><Bottom v-else
                /></el-icon>
                {{ Math.abs(stats.revenueGrowth) }}%
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon size="32" color="#e6a23c"><Calendar /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.todayAppointments }}</div>
              <div class="stat-label">今日预约</div>
              <div
                class="stat-trend"
                :class="{
                  'trend-up': stats.appointmentGrowth > 0,
                  'trend-down': stats.appointmentGrowth < 0,
                }"
              >
                <el-icon size="14"
                  ><Top v-if="stats.appointmentGrowth > 0" /><Bottom v-else
                /></el-icon>
                {{ Math.abs(stats.appointmentGrowth) }}%
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon size="32" color="#f56c6c"><Box /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.lowStockItems }}</div>
              <div class="stat-label">库存预警</div>
              <div class="stat-desc">低于安全库存</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>营收趋势</h3>
            <el-select
              v-model="revenuePeriod"
              size="small"
              @change="updateRevenueChart"
            >
              <el-option label="最近7天" value="7" />
              <el-option label="最近30天" value="30" />
              <el-option label="最近90天" value="90" />
            </el-select>
          </div>
          <div ref="revenueChartRef" class="chart-container"></div>
        </div>
      </el-col>

      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>服务项目Top10</h3>
          </div>
          <div ref="serviceChartRef" class="chart-container"></div>
        </div>
      </el-col>

      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>客户来源分析</h3>
          </div>
          <div ref="customerSourceChartRef" class="chart-container"></div>
        </div>
      </el-col>

      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>库存状态分布</h3>
          </div>
          <div ref="inventoryChartRef" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 近期活动 -->
    <el-row :gutter="20" class="activity-section">
      <el-col :xs="24" :lg="12">
        <div class="activity-card">
          <div class="card-header">
            <h3>今日预约</h3>
            <el-button
              type="text"
              size="small"
              @click="$router.push('/dashboard/appointment/schedule')"
              >查看全部</el-button
            >
          </div>
          <div class="activity-list">
            <div
              v-for="item in todayAppointments"
              :key="item.id"
              class="activity-item"
            >
              <div class="activity-time">{{ item.time }}</div>
              <div class="activity-content">
                <div class="activity-title">{{ item.serviceName }}</div>
                <div class="activity-customer">
                  {{ item.customerName }} - {{ item.petName }}
                </div>
              </div>
              <el-tag :type="getStatusType(item.status)" size="small">{{
                item.status
              }}</el-tag>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :lg="12">
        <div class="activity-card">
          <div class="card-header">
            <h3>库存预警</h3>
            <el-button
              type="text"
              size="small"
              @click="$router.push('/dashboard/scm/inventory')"
              >查看全部</el-button
            >
          </div>
          <div class="activity-list">
            <div
              v-for="item in lowStockItems"
              :key="item.id"
              class="activity-item"
            >
              <div class="activity-content">
                <div class="activity-title">{{ item.productName }}</div>
                <div class="activity-desc">
                  剩余: {{ item.currentStock }} {{ item.unit }} (安全库存:
                  {{ item.minStock }})
                </div>
              </div>
              <el-tag type="warning" size="small">缺货</el-tag>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";
import { ElMessage } from "element-plus";
import {
  RefreshRight,
  User,
  Money,
  Calendar,
  Box,
  Top,
  Bottom,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";

import { dashboardApi } from "@/api/dashboard";

// 统计数据
const stats = reactive({
  todayCustomers: 0,
  customerGrowth: 0,
  todayRevenue: 0,
  revenueGrowth: 0,
  todayAppointments: 0,
  appointmentGrowth: 0,
  lowStockItems: 0,
});

// 图表引用
const revenueChartRef = ref<HTMLElement>();
const serviceChartRef = ref<HTMLElement>();
const customerSourceChartRef = ref<HTMLElement>();
const inventoryChartRef = ref<HTMLElement>();

// 图表实例
let revenueChart: echarts.ECharts | null = null;
let serviceChart: echarts.ECharts | null = null;
let customerSourceChart: echarts.ECharts | null = null;
let inventoryChart: echarts.ECharts | null = null;

// 筛选条件
const dateRange = ref<[Date, Date]>();
const revenuePeriod = ref("7");

// 活动数据
const todayAppointments = ref([]);
const lowStockItems = ref([]);

const refreshData = () => {
  loadDashboardData();
};

const handleDateChange = () => {
  loadDashboardData();
};

const loadDashboardData = async () => {
  try {
    const res = await dashboardApi.getDashboardData();
    if (res.code === 200) {
      const data = res.data;

      // 更新统计数据
      stats.todayCustomers = data.todayCustomers;
      stats.customerGrowth = data.customerGrowth;
      stats.todayRevenue = data.todayRevenue;
      stats.revenueGrowth = data.revenueGrowth;
      stats.todayAppointments = data.todayAppointments;
      stats.appointmentGrowth = data.appointmentGrowth;
      stats.lowStockItems = data.lowStockItems;

      // 更新列表数据
      todayAppointments.value = data.todayAppointmentsList || [];
      lowStockItems.value = data.lowStockItemsList || [];

      // 更新图表
      updateCharts(data);

      ElMessage.success("数据已刷新");
    }
  } catch (error) {
    console.error("加载仪表盘数据失败", error);
  }
};

const updateCharts = (data: any) => {
  // 营收趋势图
  if (revenueChart) {
    revenueChart.setOption({
      xAxis: {
        data: data.revenueChartData.dates,
      },
      series: [
        {
          name: "营收",
          data: data.revenueChartData.revenues,
        },
        {
          name: "目标",
          data: data.revenueChartData.targets,
        },
      ],
    });
  }

  // 服务项目图
  if (serviceChart) {
    serviceChart.setOption({
      xAxis: {
        data: data.serviceChartData.names,
      },
      series: [
        {
          data: data.serviceChartData.counts,
        },
      ],
    });
  }

  // 客户来源图
  if (customerSourceChart) {
    customerSourceChart.setOption({
      series: [
        {
          data: data.customerSourceData,
        },
      ],
    });
  }

  // 库存状态图
  if (inventoryChart) {
    inventoryChart.setOption({
      series: [
        {
          data: data.inventoryData,
        },
      ],
    });
  }
};

const getStatusType = (status: string) => {
  switch (status) {
    case "待签到":
      return "warning";
    case "已预约":
      return "info";
    case "已完成":
      return "success";
    case "已取消":
      return "danger";
    default:
      return "info";
  }
};

const initCharts = () => {
  // 解决 ECharts 中文显示为乱码/方块：显式指定常见中文字体族
  const baseFontFamily =
    "Microsoft YaHei, PingFang SC, Hiragino Sans GB, Heiti SC, Arial, sans-serif";

  const baseTextStyle = {
    fontFamily: baseFontFamily,
  };

  const baseAxisLabel = {
    fontFamily: baseFontFamily,
  };

  if (revenueChartRef.value) {
    revenueChart = echarts.init(revenueChartRef.value);
    const option = {
      textStyle: baseTextStyle,
      tooltip: { trigger: "axis" },
      legend: { data: ["营收", "目标"], textStyle: baseTextStyle },
      xAxis: {
        type: "category",
        axisLabel: baseAxisLabel,
        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      },
      yAxis: { type: "value", axisLabel: baseAxisLabel },
      series: [
        {
          name: "营收",
          type: "line",
          data: [820, 932, 901, 934, 1290, 1330, 1320],
          smooth: true,
        },
        {
          name: "目标",
          type: "line",
          data: [720, 832, 801, 834, 1090, 1130, 1120],
          smooth: true,
          lineStyle: { type: "dashed" },
        },
      ],
    };
    revenueChart.setOption(option);
  }

  if (serviceChartRef.value) {
    serviceChart = echarts.init(serviceChartRef.value);
    const option = {
      textStyle: baseTextStyle,
      tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
      xAxis: {
        type: "category",
        axisLabel: baseAxisLabel,
        data: ["洗澡", "美容", "疫苗", "绝育", "体检", "其他"],
      },
      yAxis: { type: "value", axisLabel: baseAxisLabel },
      series: [
        {
          type: "bar",
          data: [120, 98, 76, 45, 32, 28],
          itemStyle: { color: "#409eff" },
        },
      ],
    };
    serviceChart.setOption(option);
  }

  if (customerSourceChartRef.value) {
    customerSourceChart = echarts.init(customerSourceChartRef.value);
    const option = {
      textStyle: baseTextStyle,
      tooltip: { trigger: "item" },
      legend: { orient: "vertical", left: "left", textStyle: baseTextStyle },
      series: [
        {
          type: "pie",
          radius: "50%",
          label: { fontFamily: baseFontFamily },
          data: [
            { value: 45, name: "线上预约" },
            { value: 30, name: "门店介绍" },
            { value: 15, name: "朋友推荐" },
            { value: 10, name: "广告宣传" },
          ],
        },
      ],
    };
    customerSourceChart.setOption(option);
  }

  if (inventoryChartRef.value) {
    inventoryChart = echarts.init(inventoryChartRef.value);
    const option = {
      textStyle: baseTextStyle,
      tooltip: { trigger: "item" },
      legend: { orient: "vertical", left: "left", textStyle: baseTextStyle },
      series: [
        {
          type: "pie",
          radius: ["40%", "70%"],
          label: { fontFamily: baseFontFamily },
          data: [
            { value: 335, name: "正常库存" },
            { value: 310, name: "低库存预警" },
            { value: 234, name: "缺货" },
          ],
        },
      ],
    };
    inventoryChart.setOption(option);
  }
};

const updateRevenueChart = () => {
  // 根据选择的周期更新营收图表
  console.log("更新营收图表，周期:", revenuePeriod.value);
};

const handleResize = () => {
  revenueChart?.resize();
  serviceChart?.resize();
  customerSourceChart?.resize();
  inventoryChart?.resize();
};

onMounted(() => {
  loadDashboardData();
  initCharts();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  revenueChart?.dispose();
  serviceChart?.dispose();
  customerSourceChart?.dispose();
  inventoryChart?.dispose();
});
</script>

<style scoped lang="scss">
.dashboard {
  .stats-cards {
    margin-bottom: 20px;

    .stat-card {
      background-color: #fff;
      padding: 20px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .stat-icon {
        margin-right: 16px;
      }

      .stat-content {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #1d2129;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #86909c;
          margin-bottom: 4px;
        }

        .stat-trend {
          font-size: 12px;
          display: flex;
          align-items: center;

          &.trend-up {
            color: #67c23a;
          }

          &.trend-down {
            color: #f56c6c;
          }

          .el-icon {
            margin-right: 2px;
          }
        }

        .stat-desc {
          font-size: 12px;
          color: #86909c;
        }
      }
    }
  }

  .charts-section {
    margin-bottom: 20px;

    .chart-card {
      background-color: #fff;
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .chart-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h3 {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          color: #1d2129;
        }
      }

      .chart-container {
        height: 300px;
        width: 100%;
      }
    }
  }

  .activity-section {
    .activity-card {
      background-color: #fff;
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h3 {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          color: #1d2129;
        }
      }

      .activity-list {
        .activity-item {
          display: flex;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .activity-time {
            font-size: 14px;
            color: #409eff;
            font-weight: 600;
            min-width: 60px;
          }

          .activity-content {
            flex: 1;
            margin: 0 12px;

            .activity-title {
              font-size: 14px;
              font-weight: 500;
              color: #1d2129;
              margin-bottom: 2px;
            }

            .activity-customer,
            .activity-desc {
              font-size: 12px;
              color: #86909c;
            }
          }
        }
      }
    }
  }
}
</style>
