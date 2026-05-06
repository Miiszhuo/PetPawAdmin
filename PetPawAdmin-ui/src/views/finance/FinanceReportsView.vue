<template>
  <div class="finance-reports">
    <PageHeader title="财务报表" description="数据驱动的财务分析与决策支持">
      <template #actions>
        <el-date-picker
          v-model="reportDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="default"
          @change="updateReportData"
        />
      </template>
    </PageHeader>

    <!-- 财务概览指标 -->
    <div class="finance-overview">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="metric-card">
            <div class="metric-icon">
              <el-icon size="32" color="#409eff"><Money /></el-icon>
            </div>
            <div class="metric-content">
              <div class="metric-value">
                ¥{{ metrics.totalRevenue.toLocaleString() }}
              </div>
              <div class="metric-label">总营收</div>
              <div
                class="metric-change"
                :class="{
                  positive: metrics.revenueGrowth > 0,
                  negative: metrics.revenueGrowth < 0,
                }"
              >
                <el-icon size="14"
                  ><Top v-if="metrics.revenueGrowth > 0" /><Bottom v-else
                /></el-icon>
                {{ Math.abs(metrics.revenueGrowth) }}%
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="metric-card">
            <div class="metric-icon">
              <el-icon size="32" color="#67c23a"><ShoppingCart /></el-icon>
            </div>
            <div class="metric-content">
              <div class="metric-value">{{ metrics.totalOrders }}</div>
              <div class="metric-label">订单总数</div>
              <div
                class="metric-change"
                :class="{
                  positive: metrics.orderGrowth > 0,
                  negative: metrics.orderGrowth < 0,
                }"
              >
                <el-icon size="14"
                  ><Top v-if="metrics.orderGrowth > 0" /><Bottom v-else
                /></el-icon>
                {{ Math.abs(metrics.orderGrowth) }}%
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="metric-card">
            <div class="metric-icon">
              <el-icon size="32" color="#e6a23c"><TrendCharts /></el-icon>
            </div>
            <div class="metric-content">
              <div class="metric-value">¥{{ metrics.avgOrderValue }}</div>
              <div class="metric-label">平均客单价</div>
              <div
                class="metric-change"
                :class="{
                  positive: metrics.aovGrowth > 0,
                  negative: metrics.aovGrowth < 0,
                }"
              >
                <el-icon size="14"
                  ><Top v-if="metrics.aovGrowth > 0" /><Bottom v-else
                /></el-icon>
                {{ Math.abs(metrics.aovGrowth) }}%
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="metric-card">
            <div class="metric-icon">
              <el-icon size="32" color="#f56c6c"><Coin /></el-icon>
            </div>
            <div class="metric-content">
              <div class="metric-value">
                ¥{{ metrics.memberRevenue.toLocaleString() }}
              </div>
              <div class="metric-label">会员消费</div>
              <div
                class="metric-change"
                :class="{
                  positive: metrics.memberGrowth > 0,
                  negative: metrics.memberGrowth < 0,
                }"
              >
                <el-icon size="14"
                  ><Top v-if="metrics.memberGrowth > 0" /><Bottom v-else
                /></el-icon>
                {{ Math.abs(metrics.memberGrowth) }}%
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <!-- 营收趋势图 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>营收趋势分析</h3>
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

      <!-- 服务项目收入占比 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>服务项目收入占比</h3>
          </div>
          <div ref="serviceChartRef" class="chart-container"></div>
        </div>
      </el-col>

      <!-- 会员等级消费分析 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>会员等级消费分析</h3>
          </div>
          <div ref="memberChartRef" class="chart-container"></div>
        </div>
      </el-col>

      <!-- 商品销售Top10 -->
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>商品销售排行</h3>
          </div>
          <div ref="productChartRef" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <div class="data-tables">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="日营收明细" name="daily">
          <el-table :data="dailyRevenueData" style="width: 100%" size="small">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="revenue" label="营收金额" width="120">
              <template #default="scope">
                ¥{{ scope.row.revenue.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="orders" label="订单数" width="100" />
            <el-table-column prop="avgOrder" label="平均客单" width="120">
              <template #default="scope"> ¥{{ scope.row.avgOrder }} </template>
            </el-table-column>
            <el-table-column prop="cashPayment" label="现金支付" width="120">
              <template #default="scope">
                ¥{{ scope.row.cashPayment }}
              </template>
            </el-table-column>
            <el-table-column prop="cardPayment" label="会员卡支付" width="120">
              <template #default="scope">
                ¥{{ scope.row.cardPayment }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="商品销售统计" name="products">
          <el-table :data="productSalesData" style="width: 100%" size="small">
            <el-table-column prop="name" label="商品名称" width="200" />
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column
              prop="salesQuantity"
              label="销售数量"
              width="100"
            />
            <el-table-column prop="salesAmount" label="销售额" width="120">
              <template #default="scope">
                ¥{{ scope.row.salesAmount.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="grossProfit" label="毛利润" width="120">
              <template #default="scope">
                ¥{{ scope.row.grossProfit.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="grossMargin" label="毛利率" width="100">
              <template #default="scope">
                {{ scope.row.grossMargin }}%
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="会员消费统计" name="members">
          <el-table
            :data="memberConsumptionData"
            style="width: 100%"
            size="small"
          >
            <el-table-column prop="name" label="会员姓名" width="120" />
            <el-table-column prop="level" label="会员等级" width="100">
              <template #default="scope">
                <el-tag :type="getMemberLevelType(scope.row.level)">
                  {{ getMemberLevelText(scope.row.level) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="consumptionAmount"
              label="消费金额"
              width="120"
            >
              <template #default="scope">
                ¥{{ scope.row.consumptionAmount.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="ordersCount" label="消费次数" width="100" />
            <el-table-column prop="avgOrderValue" label="平均客单" width="120">
              <template #default="scope">
                ¥{{ scope.row.avgOrderValue }}
              </template>
            </el-table-column>
            <el-table-column prop="lastVisit" label="最后消费" width="160" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";
import { ElMessage } from "element-plus";
import {
  Money,
  ShoppingCart,
  TrendCharts,
  Coin,
  Top,
  Bottom,
} from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import { financeApi } from "@/api";

// 图表引用
const revenueChartRef = ref<HTMLElement>();
const serviceChartRef = ref<HTMLElement>();
const memberChartRef = ref<HTMLElement>();
const productChartRef = ref<HTMLElement>();

// 图表实例
let revenueChart: echarts.ECharts | null = null;
let serviceChart: echarts.ECharts | null = null;
let memberChart: echarts.ECharts | null = null;
let productChart: echarts.ECharts | null = null;

// 响应式数据
const activeTab = ref("daily");
const revenuePeriod = ref("30");
const reportDateRange = ref<[Date, Date]>([
  new Date(new Date().getTime() - 30 * 24 * 60 * 60 * 1000),
  new Date(),
]);

// 财务指标
const metrics = reactive({
  totalRevenue: 0,
  revenueGrowth: 0,
  totalOrders: 0,
  orderGrowth: 0,
  avgOrderValue: 0,
  aovGrowth: 0,
  memberRevenue: 0,
  memberGrowth: 0,
});

// 数据表格
const dailyRevenueData = ref([]);
const productSalesData = ref([]);
const memberConsumptionData = ref([]);

// 方法
const loadReportData = async () => {
  try {
    const startDate = reportDateRange.value[0].toISOString().split("T")[0];
    const endDate = reportDateRange.value[1].toISOString().split("T")[0];

    // 1. 加载概览统计
    const statsRes = await financeApi.getReportStats({ startDate, endDate });
    if (statsRes.code === 200) {
      Object.assign(metrics, statsRes.data);
    }

    // 2. 加载营收趋势
    const trendRes = await financeApi.getRevenueTrend({
      startDate,
      endDate,
      type: "day",
    });
    if (trendRes.code === 200) {
      renderRevenueChart(trendRes.data);
    }

    // 3. 加载收入构成
    const incomeRes = await financeApi.getIncomeComposition({
      startDate,
      endDate,
    });
    if (incomeRes.code === 200) {
      renderServiceChart(incomeRes.data);
    }

    // 4. 加载会员等级消费
    const memberRes = await financeApi.getMemberConsumptionStats({
      startDate,
      endDate,
    });
    if (memberRes.code === 200) {
      renderMemberChart(memberRes.data);
      if (memberRes.data?.list) {
        memberConsumptionData.value = memberRes.data.list;
      }
    }

    // 5. 加载商品销售排行
    const productRes = await financeApi.getProductSalesRanking({
      startDate,
      endDate,
    });
    if (productRes.code === 200) {
      renderProductChart(productRes.data);
      if (productRes.data?.list) {
        productSalesData.value = productRes.data.list;
      }
    }

    // 6. 加载日营收明细列表
    const dailyRes = await financeApi.getDailyRevenueList({
      startDate,
      endDate,
    });
    if (dailyRes.code === 200) {
      dailyRevenueData.value = dailyRes.data;
    }
  } catch (error) {
    console.error("加载报表数据失败", error);
    ElMessage.error("加载报表数据失败");
  }
};

const renderRevenueChart = (data: any) => {
  if (revenueChartRef.value) {
    if (!revenueChart) revenueChart = echarts.init(revenueChartRef.value);
    const option = {
      tooltip: { trigger: "axis" },
      legend: { data: ["营收", "目标"] },
      xAxis: {
        type: "category",
        data: data.dates,
      },
      yAxis: { type: "value" },
      series: [
        {
          name: "营收",
          type: "line",
          data: data.revenue,
          smooth: true,
          itemStyle: { color: "#409eff" },
        },
        {
          name: "目标",
          type: "line",
          data: data.target,
          smooth: true,
          lineStyle: { type: "dashed" },
          itemStyle: { color: "#e6a23c" },
        },
      ],
    };
    revenueChart.setOption(option);
  }
};

const renderServiceChart = (data: any[]) => {
  if (serviceChartRef.value) {
    if (!serviceChart) serviceChart = echarts.init(serviceChartRef.value);
    const option = {
      tooltip: { trigger: "item" },
      legend: { orient: "vertical", left: "left" },
      series: [
        {
          type: "pie",
          radius: "50%",
          data: data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: "rgba(0, 0, 0, 0.5)",
            },
          },
        },
      ],
    };
    serviceChart.setOption(option);
  }
};

const renderMemberChart = (data: any) => {
  if (memberChartRef.value) {
    if (!memberChart) memberChart = echarts.init(memberChartRef.value);
    const option = {
      tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
      xAxis: {
        type: "category",
        data: data.categories,
      },
      yAxis: { type: "value" },
      series: [
        {
          type: "bar",
          data: data.data,
          itemStyle: {
            color: function (params: any) {
              const colors = ["#f56c6c", "#e6a23c", "#409eff"];
              return colors[params.dataIndex % colors.length];
            },
          },
        },
      ],
    };
    memberChart.setOption(option);
  }
};

const renderProductChart = (data: any) => {
  if (productChartRef.value) {
    if (!productChart) productChart = echarts.init(productChartRef.value);
    const option = {
      tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
      xAxis: {
        type: "category",
        data: data.categories,
      },
      yAxis: { type: "value" },
      series: [
        {
          type: "bar",
          data: data.data,
          itemStyle: { color: "#67c23a" },
        },
      ],
    };
    productChart.setOption(option);
  }
};

const updateReportData = () => {
  loadReportData();
};

const updateRevenueChart = () => {
  const days = Number(revenuePeriod.value);
  if (!Number.isNaN(days) && days > 0) {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(endDate.getDate() - days + 1);
    reportDateRange.value = [startDate, endDate];
  }
  loadReportData();
};

const handleTabClick = () => {
  // 切换标签页
};

const getMemberLevelType = (level: string) => {
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

const getMemberLevelText = (level: string) => {
  switch (level) {
    case "diamond":
      return "钻石";
    case "vip":
      return "VIP";
    case "normal":
      return "普通";
    default:
      return "普通";
  }
};

const handleResize = () => {
  revenueChart?.resize();
  serviceChart?.resize();
  memberChart?.resize();
  productChart?.resize();
};

onMounted(() => {
  loadReportData();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  revenueChart?.dispose();
  serviceChart?.dispose();
  memberChart?.dispose();
  productChart?.dispose();
});
</script>

<style scoped lang="scss">
.finance-reports {
  .finance-overview {
    margin-bottom: 20px;

    .metric-card {
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

      .metric-icon {
        margin-right: 16px;
      }

      .metric-content {
        flex: 1;

        .metric-value {
          font-size: 24px;
          font-weight: 600;
          color: #1d2129;
          margin-bottom: 4px;
        }

        .metric-label {
          font-size: 14px;
          color: #86909c;
          margin-bottom: 4px;
        }

        .metric-change {
          font-size: 12px;
          display: flex;
          align-items: center;

          &.positive {
            color: #67c23a;
          }

          &.negative {
            color: #f56c6c;
          }

          .el-icon {
            margin-right: 2px;
          }
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

  .data-tables {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    padding: 20px;
  }
}
</style>
