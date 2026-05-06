<template>
  <div class="schedule">
    <PageHeader title="排班看板" description="可视化管理技师排班和预约情况">
      <template #actions>
        <el-button type="primary" @click="showAddAppointmentDialog">
          <el-icon><Plus /></el-icon>
          新增预约
        </el-button>
        <el-button @click="exportSchedule">
          <el-icon><Download /></el-icon>
          导出排班表
        </el-button>
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="week">周视图</el-radio-button>
          <el-radio-button label="day">日视图</el-radio-button>
        </el-radio-group>
      </template>
    </PageHeader>

    <!-- 排班控制面板 -->
    <div class="schedule-controls">
      <div class="date-navigation">
        <el-button size="small" @click="previousPeriod">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="current-period">{{ currentPeriodText }}</span>
        <el-button size="small" @click="nextPeriod">
          <el-icon><ArrowRight /></el-icon>
        </el-button>
        <el-button size="small" type="primary" @click="goToToday"
          >今天</el-button
        >
      </div>

      <div class="staff-filter">
        <el-checkbox-group v-model="selectedStaff" @change="updateSchedule">
          <el-checkbox
            v-for="staff in staffList"
            :key="staff.id"
            :label="staff.id"
          >
            {{ staff.name }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
    </div>

    <!-- 排班表格 -->
    <div v-loading="loading" class="schedule-table">
      <div class="time-column">
        <div class="time-header">时间</div>
        <div v-for="hour in timeSlots" :key="hour" class="time-slot">
          {{ hour }}:00
        </div>
      </div>

      <div v-for="staff in filteredStaff" :key="staff.id" class="staff-column">
        <div class="staff-header">
          <div class="staff-info">
            <el-avatar :size="32" :src="staff.avatar">{{
              staff.name.charAt(0)
            }}</el-avatar>
            <div class="staff-details">
              <div class="staff-name">{{ staff.name }}</div>
              <div class="staff-title">{{ staff.title }}</div>
            </div>
          </div>
          <div class="staff-stats">
            <span class="appointment-count">{{
              getStaffAppointmentCount(staff.id)
            }}</span>
          </div>
        </div>

        <div class="time-slots">
          <div
            v-for="hour in timeSlots"
            :key="hour"
            class="time-slot"
            @click="handleTimeSlotClick(staff.id, hour)"
            @drop="handleDrop($event, staff.id, hour)"
            @dragover.prevent
          >
            <div
              v-for="appointment in getAppointmentsForSlot(staff.id, hour)"
              :key="appointment.id"
              class="appointment-item"
              :class="getAppointmentClass(appointment)"
              :draggable="true"
              @dragstart="handleDragStart($event, appointment)"
              @click.stop="viewAppointment(appointment)"
            >
              <div class="appointment-time">
                {{ appointment.startTime }}-{{ appointment.endTime }}
              </div>
              <div class="appointment-service">
                {{ appointment.serviceName }}
              </div>
              <div class="appointment-customer">
                {{ appointment.customerName }}
              </div>
              <div class="appointment-pet">{{ appointment.petName }}</div>
              <el-tag
                size="mini"
                :type="getStatusType(appointment.status)"
                class="status-tag"
              >
                {{ getStatusText(appointment.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增预约对话框 -->
    <el-dialog
      v-model="appointmentDialogVisible"
      title="新增预约"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="appointmentFormRef"
        :model="appointmentForm"
        :rules="appointmentRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约日期" prop="date">
              <el-date-picker
                v-model="appointmentForm.date"
                type="date"
                placeholder="选择预约日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledDate"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约时间" prop="timeSlot">
              <el-select
                v-model="appointmentForm.timeSlot"
                placeholder="选择时间段"
              >
                <el-option
                  v-for="slot in availableTimeSlots"
                  :key="slot.value"
                  :label="slot.label"
                  :value="slot.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="选择技师" prop="staffId">
          <el-select
            v-model="appointmentForm.staffId"
            placeholder="选择服务技师"
            filterable
          >
            <el-option
              v-for="staff in staffList"
              :key="staff.id"
              :label="`${staff.name} (${staff.title})`"
              :value="staff.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="服务项目" prop="serviceId">
          <el-select
            v-model="appointmentForm.serviceId"
            placeholder="选择服务项目"
            filterable
          >
            <el-option
              v-for="service in serviceList"
              :key="service.id"
              :label="`${service.name} - ¥${service.price} (${service.duration}分钟)`"
              :value="service.id"
            >
              <div class="service-option">
                <div class="service-name">{{ service.name }}</div>
                <div class="service-info">
                  <span class="service-price">¥{{ service.price }}</span>
                  <span class="service-duration"
                    >{{ service.duration }}分钟</span
                  >
                  <el-tag
                    size="small"
                    :type="getCategoryType(service.category)"
                  >
                    {{ getCategoryText(service.category) }}
                  </el-tag>
                </div>
                <div v-if="service.description" class="service-description">
                  {{ service.description }}
                </div>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="客户信息" prop="customerId">
          <el-select
            v-model="appointmentForm.customerId"
            placeholder="选择客户"
            filterable
            remote
            :remote-method="searchCustomers"
            :loading="customerLoading"
          >
            <el-option
              v-for="customer in customerList"
              :key="customer.id"
              :label="`${customer.name} (${customer.phone})`"
              :value="customer.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="宠物信息" prop="petId">
          <el-select
            v-model="appointmentForm.petId"
            placeholder="选择宠物"
            :disabled="!appointmentForm.customerId"
          >
            <el-option
              v-for="pet in petList"
              :key="pet.id"
              :label="`${pet.name} (${pet.breed})`"
              :value="pet.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="预约备注">
          <el-input
            v-model="appointmentForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入预约备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="appointmentDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="appointmentLoading"
            @click="handleCreateAppointment"
          >
            确认预约
          </el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 预约详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="预约详情" width="500px">
      <el-descriptions v-if="currentAppointment" :column="1" border>
        <el-descriptions-item label="预约编号"
          >#{{ currentAppointment.id }}</el-descriptions-item
        >
        <el-descriptions-item label="预约时间"
          >{{ currentAppointment.date }} {{ currentAppointment.startTime }}-{{
            currentAppointment.endTime
          }}</el-descriptions-item
        >
        <el-descriptions-item label="服务项目">{{
          currentAppointment.serviceName
        }}</el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{
          currentAppointment.customerName
        }}</el-descriptions-item>
        <el-descriptions-item label="宠物信息">{{
          currentAppointment.petName
        }}</el-descriptions-item>
        <el-descriptions-item label="技师">{{
          getStaffName(currentAppointment.staffId)
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentAppointment.status)">
            {{ getStatusText(currentAppointment.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{
          currentAppointment.remark || "无"
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { Plus, Download, ArrowLeft, ArrowRight } from "@element-plus/icons-vue";
import PageHeader from "@/components/common/PageHeader.vue";
import { appointmentApi, crmApi } from "@/api";

// 技师信息接口
interface Staff {
  id: number;
  name: string;
  title: string;
  avatar?: string;
  skills: string[];
}

// 预约信息接口
interface Appointment {
  id: number;
  date: string;
  startTime: string;
  endTime: string;
  staffId: number;
  serviceId: number;
  serviceName: string;
  customerId: number;
  customerName: string;
  petId: number;
  petName: string;
  status: "pending" | "confirmed" | "in_progress" | "completed" | "cancelled";
  remark?: string;
}

// 服务项目接口
interface Service {
  id: number;
  name: string;
  price: number;
  duration: number; // 分钟
  category: "grooming" | "medical" | "other";
  description?: string;
  requiredSkills?: string[];
}

// 客户接口
interface Customer {
  id: number;
  name: string;
  phone: string;
}

// 宠物接口
interface Pet {
  id: number;
  name: string;
  breed: string;
  customerId: number;
}

// 响应式数据
const loading = ref(false);
const appointmentDialogVisible = ref(false);
const viewDialogVisible = ref(false);
const currentAppointment = ref<Appointment | null>(null);
const appointmentLoading = ref(false);
const customerLoading = ref(false);
const viewMode = ref<"week" | "day">("day"); // 默认日视图，因为周视图逻辑复杂且API目前按天返回
const currentDate = ref(new Date());
const selectedStaff = ref<number[]>([]);

// 拖拽相关
const draggedAppointment = ref<Appointment | null>(null);

// 时间段 (9:00 - 18:00)
const timeSlots = [9, 10, 11, 12, 13, 14, 15, 16, 17];

// 技师列表
const staffList = ref<Staff[]>([]);

// 服务项目列表
const serviceList = ref<Service[]>([]);

// 预约列表
const appointments = ref<Appointment[]>([]);

// 客户列表
const customerList = ref<Customer[]>([]);

// 宠物列表
const petList = ref<Pet[]>([]);

// 预约表单
const appointmentForm = reactive({
  date: "",
  timeSlot: "",
  staffId: undefined as number | undefined,
  serviceId: undefined as number | undefined,
  customerId: undefined as number | undefined,
  petId: undefined as number | undefined,
  remark: "",
});

const appointmentFormRef = ref<FormInstance>();

// 表单验证规则
const appointmentRules: FormRules = {
  date: [{ required: true, message: "请选择预约日期", trigger: "change" }],
  timeSlot: [{ required: true, message: "请选择预约时间", trigger: "change" }],
  staffId: [{ required: true, message: "请选择技师", trigger: "change" }],
  serviceId: [{ required: true, message: "请选择服务项目", trigger: "change" }],
  customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
  petId: [{ required: true, message: "请选择宠物", trigger: "change" }],
};

// 计算属性
const filteredStaff = computed(() => {
  return staffList.value.filter((staff) =>
    selectedStaff.value.includes(staff.id),
  );
});

const currentPeriodText = computed(() => {
  if (viewMode.value === "week") {
    const startOfWeek = new Date(currentDate.value);
    startOfWeek.setDate(
      currentDate.value.getDate() - currentDate.value.getDay() + 1,
    );
    const endOfWeek = new Date(startOfWeek);
    endOfWeek.setDate(startOfWeek.getDate() + 6);
    return `${startOfWeek.toLocaleDateString("zh-CN")} - ${endOfWeek.toLocaleDateString("zh-CN")}`;
  } else {
    return currentDate.value.toLocaleDateString("zh-CN");
  }
});

const availableTimeSlots = computed(() => {
  const slots = [];
  for (let hour = 9; hour <= 17; hour++) {
    const startTime = `${hour.toString().padStart(2, "0")}:00`;
    const endTime = `${(hour + 1).toString().padStart(2, "0")}:00`;
    slots.push({
      label: `${startTime} - ${endTime}`,
      value: `${hour}`,
    });
  }
  return slots;
});

// 方法
const loadServices = async () => {
  try {
    const res = await appointmentApi.getServiceItems({
      current: 1,
      size: 100,
      status: 1, // 启用服务
    });
    if (res.code === 200) {
      serviceList.value = res.data.records.map((s: any) => ({
        id: s.id,
        name: s.serviceName,
        price: s.price,
        duration: s.standardDuration || 60,
        category: s.category || "other",
        description: s.description || "",
        requiredSkills: [],
      }));
    }
  } catch (error) {
    console.error("加载服务失败", error);
  }
};

const loadScheduleData = async () => {
  loading.value = true;
  try {
    const dateStr = currentDate.value.toISOString().split("T")[0];
    const res = await appointmentApi.getScheduleBoard(dateStr);
    if (res.code === 200) {
      const data = res.data;

      // 解析后端数据
      staffList.value = (data || []).map((item: any) => ({
        id: item.staffId,
        name: item.staffName,
        title: item.positionName || "技师",
        avatar: item.avatar,
        skills: item.skills || [],
      }));

      // 收集所有预约
      let allAppointments: Appointment[] = [];
      data.forEach((staff: any) => {
        if (staff.appointments && staff.appointments.length > 0) {
          const staffAppts = staff.appointments.map((appt: any) => ({
            id: appt.id,
            date: appt.appointmentDate, // 确保后端返回字段名为 appointmentDate
            startTime: appt.appointmentTime,
            endTime: calculateEndTime(
              appt.appointmentTime,
              appt.duration || 60,
            ),
            staffId: staff.staffId,
            serviceId: appt.serviceItemId,
            serviceName: appt.serviceName || "未知服务",
            customerId: appt.customerId,
            customerName: appt.customerName || "未知客户",
            petId: appt.petId,
            petName: appt.petName || "未知宠物",
            status: mapStatus(appt.status),
            remark: appt.remark,
          }));
          allAppointments = allAppointments.concat(staffAppts);
        }
      });
      appointments.value = allAppointments;

      // 默认全选技师
      if (selectedStaff.value.length === 0) {
        selectedStaff.value = staffList.value.map((s) => s.id);
      }
    }
  } catch (error) {
    console.error("加载排班数据失败", error);
    ElMessage.error("加载排班数据失败");
  } finally {
    loading.value = false;
  }
};

// 辅助函数
const calculateEndTime = (startTime: string, durationMinutes: number) => {
  if (!startTime) return "";
  const [hours, minutes] = startTime.split(":").map(Number);
  const date = new Date();
  date.setHours(hours, minutes + durationMinutes);
  return `${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}`;
};

const mapStatus = (status: string) => {
  const map: Record<string, string> = {
    待确认: "pending",
    已预约: "confirmed",
    进行中: "in_progress",
    已完成: "completed",
    已取消: "cancelled",
  };
  return map[status] || "pending";
};

const updateSchedule = () => {
  // 重新渲染排班表
  loadScheduleData();
};

const previousPeriod = () => {
  if (viewMode.value === "week") {
    currentDate.value.setDate(currentDate.value.getDate() - 7);
  } else {
    currentDate.value.setDate(currentDate.value.getDate() - 1);
  }
  currentDate.value = new Date(currentDate.value);
  loadScheduleData();
};

const nextPeriod = () => {
  if (viewMode.value === "week") {
    currentDate.value.setDate(currentDate.value.getDate() + 7);
  } else {
    currentDate.value.setDate(currentDate.value.getDate() + 1);
  }
  currentDate.value = new Date(currentDate.value);
  loadScheduleData();
};

const goToToday = () => {
  currentDate.value = new Date();
  loadScheduleData();
};

const getStaffAppointmentCount = (staffId: number) => {
  const today = currentDate.value.toISOString().split("T")[0];
  return appointments.value.filter(
    (app) => app.staffId === staffId && app.date === today,
  ).length;
};

const getAppointmentsForSlot = (staffId: number, hour: number) => {
  const today = currentDate.value.toISOString().split("T")[0];
  return appointments.value.filter((app) => {
    if (app.staffId !== staffId || app.date !== today) return false;
    const startHour = parseInt(app.startTime.split(":")[0]);
    return startHour === hour;
  });
};

const handleTimeSlotClick = (staffId: number, hour: number) => {
  // 点击时间段创建预约
  showAddAppointmentDialog();
  appointmentForm.date = currentDate.value.toISOString().split("T")[0];
  appointmentForm.timeSlot = hour.toString();
  appointmentForm.staffId = staffId;
};

const handleDragStart = (event: DragEvent, appointment: Appointment) => {
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = "move";
    event.dataTransfer.setData("text/plain", JSON.stringify(appointment));
    draggedAppointment.value = appointment;
  }
};

const handleDrop = async (event: DragEvent, staffId: number, hour: number) => {
  event.preventDefault();
  if (!draggedAppointment.value) return;

  const appointment = draggedAppointment.value;
  const targetDate = currentDate.value.toISOString().split("T")[0];
  const targetTime = `${hour.toString().padStart(2, "0")}:00`;

  // 如果位置没有变化，直接返回
  const currentStartHour = parseInt(appointment.startTime.split(":")[0]);
  if (
    appointment.staffId === staffId &&
    appointment.date === targetDate &&
    currentStartHour === hour
  ) {
    return;
  }

  try {
    // 调用更新API
    // 注意：这里需要根据后端API实际情况构建数据
    // 假设后端支持部分更新或我们需要构造完整对象
    // 这里使用模拟更新逻辑，实际应调用 updateOrder

    // 构造更新数据
    // 寻找服务信息以获取duration等
    const service = serviceList.value.find(
      (s) => s.name === appointment.serviceName,
    ); // 尝试匹配服务
    // 或者直接使用原有duration（如果API支持）

    // 简化处理：仅更新时间和技师
    // 由于后端API updateOrder 需要 AppointmentOrder 对象，我们可能需要先获取详情或尽可能填充
    // 这里我们假设后端接受关键字段更新

    // 注意：appointment.id 是前端展示用的id，可能对应 appointment_order.id

    // 临时方案：提示用户确认（为了安全），或者直接尝试更新
    // 考虑到用户体验，直接尝试更新

    // 获取原始订单详情以便完整更新（推荐）
    // const originalOrder = await appointmentApi.getOrder(appointment.id)
    // originalOrder.staffId = staffId
    // originalOrder.appointmentDate = targetDate
    // originalOrder.appointmentTime = targetTime
    // await appointmentApi.updateOrder(appointment.id, originalOrder)

    // 由于没有getOrder的同步调用，我们构造一个尽量完整的对象
    const updateData: any = {
      id: appointment.id,
      staffId: staffId,
      appointmentDate: targetDate, // 假设拖拽只在当前显示日期内（日视图）
      appointmentTime: targetTime,
      // 其他必填字段可能需要从 appointment 中获取或保留原值
      customerId: appointment.customerId,
      petId: appointment.petId,
      serviceItemId: appointment.serviceId,
      orderNumber: `APT${appointment.id}`, // 占位
      status: mapStatus(appointment.status) || "已预约",
      duration: 60, // 默认或计算
      price: 0,
      totalAmount: 0,
    };

    // 注意：实际项目中应调用 getOrder 获取完整数据再更新
    // 这里为演示拖拽功能，我们仅发送关键变更，假设后端能处理或我们能获取足够信息

    // 尝试调用 updateOrder (可能失败如果缺少字段)
    // 或者我们假设后端有一个专门的 moveAppointment 接口？目前没有。
    // 我们尝试获取详情
    const res = await appointmentApi.getOrder(appointment.id);
    if (res.code === 200 && res.data) {
      const fullOrder = res.data;
      fullOrder.staffId = staffId;
      fullOrder.appointmentDate = targetDate; // 注意：如果日视图切换了日期，这里需要是目标日期
      // 目前ScheduleView只显示一天或一周，currentDate是当前视图日期
      // 拖拽是在当前视图内
      // 如果是周视图，targetDate需要根据列来判断（目前周视图逻辑未完全实现，且ViewMode默认Day）
      fullOrder.appointmentTime = targetTime;

      await appointmentApi.updateOrder(appointment.id, fullOrder);
      ElMessage.success("预约调整成功");
      loadScheduleData();
    } else {
      throw new Error("无法获取订单详情");
    }
  } catch (error) {
    console.error("调整预约失败", error);
    ElMessage.error("调整预约失败");
  } finally {
    draggedAppointment.value = null;
  }
};

const viewAppointment = (appointment: Appointment) => {
  currentAppointment.value = appointment;
  viewDialogVisible.value = true;
};

const showAddAppointmentDialog = () => {
  appointmentDialogVisible.value = true;
  resetAppointmentForm();
};

const resetAppointmentForm = () => {
  Object.keys(appointmentForm).forEach((key) => {
    appointmentForm[key] = "";
  });
  appointmentForm.staffId = undefined;
  appointmentForm.serviceId = undefined;
  appointmentForm.customerId = undefined;
  appointmentForm.petId = undefined;
};

const handleCreateAppointment = async () => {
  if (!appointmentFormRef.value) return;

  try {
    await appointmentFormRef.value.validate();

    appointmentLoading.value = true;

    // 获取选中服务的信息
    const selectedService = serviceList.value.find(
      (s) => s.id === appointmentForm.serviceId!,
    );
    const duration = selectedService ? selectedService.duration : 60;
    const price = selectedService ? selectedService.price : 0;

    // 调用创建API
    const appointmentData = {
      orderNumber: `APT${Date.now()}`, // 临时生成订单号，实际应由后端生成
      customerId: appointmentForm.customerId!,
      petId: appointmentForm.petId,
      serviceItemId: appointmentForm.serviceId!,
      appointmentDate: appointmentForm.date,
      appointmentTime:
        availableTimeSlots.value
          .find((s) => s.value === appointmentForm.timeSlot)
          ?.label.split(" - ")[0] || "09:00",
      staffId: appointmentForm.staffId!,
      status: "已预约",
      duration: duration,
      price: price,
      totalAmount: price,
      remark: appointmentForm.remark,
    };

    await appointmentApi.createOrder(appointmentData);

    ElMessage.success("预约创建成功");
    appointmentDialogVisible.value = false;
    loadScheduleData();
  } catch (error) {
    console.error("创建预约失败:", error);
    ElMessage.error("创建预约失败");
  } finally {
    appointmentLoading.value = false;
  }
};

const getStaffName = (staffId: number) => {
  const staff = staffList.value.find((s) => s.id === staffId);
  return staff ? staff.name : "未知技师";
};

const searchCustomers = async (query: string) => {
  if (query !== "") {
    customerLoading.value = true;
    try {
      const res = await crmApi.getCustomers({
        current: 1,
        size: 10,
        customerName: query, // 假设API支持按名称搜索
      });
      if (res.code === 200) {
        customerList.value = res.data.records.map((c: any) => ({
          id: c.id,
          name: c.customerName,
          phone: c.phone,
        }));
      }
    } catch (error) {
      console.error("搜索客户失败", error);
    } finally {
      customerLoading.value = false;
    }
  } else {
    customerList.value = [];
  }
};

const exportSchedule = () => {
  ElMessage.success("导出排班表功能开发中");
};

const getAppointmentClass = (appointment: Appointment) => {
  return `appointment-${appointment.status}`;
};

const getStatusType = (status: string) => {
  switch (status) {
    case "pending":
      return "warning";
    case "confirmed":
      return "success";
    case "in_progress":
      return "primary";
    case "completed":
      return "info";
    case "cancelled":
      return "danger";
    default:
      return "info";
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case "pending":
      return "待确认";
    case "confirmed":
      return "已确认";
    case "in_progress":
      return "进行中";
    case "completed":
      return "已完成";
    case "cancelled":
      return "已取消";
    default:
      return status;
  }
};

const getCategoryType = (category: string) => {
  switch (category) {
    case "grooming":
      return "primary";
    case "medical":
      return "danger";
    case "other":
      return "info";
    default:
      return "info";
  }
};

const getCategoryText = (category: string) => {
  switch (category) {
    case "grooming":
      return "美容";
    case "medical":
      return "医疗";
    case "other":
      return "其他";
    default:
      return category;
  }
};

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7; // 不允许选择昨天之前的日期
};

// 监听客户选择变化，加载对应宠物
watch(
  () => appointmentForm.customerId,
  async (newCustomerId) => {
    if (newCustomerId) {
      // 模拟加载客户宠物
      petList.value = [
        { id: 1, name: "旺财", breed: "金毛犬", customerId: newCustomerId },
        { id: 2, name: "小白", breed: "波斯猫", customerId: newCustomerId },
      ];
    } else {
      petList.value = [];
    }
  },
);

onMounted(() => {
  loadServices();
  loadScheduleData();
});
</script>

<style scoped lang="scss">
.schedule {
  .schedule-controls {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    display: flex;
    justify-content: space-between;
    align-items: center;

    .date-navigation {
      display: flex;
      align-items: center;
      gap: 12px;

      .current-period {
        font-weight: 600;
        color: #1d2129;
        min-width: 200px;
        text-align: center;
      }
    }

    .staff-filter {
      .el-checkbox-group {
        display: flex;
        gap: 16px;
      }
    }
  }

  .schedule-table {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    display: flex;
    overflow-x: auto;

    .time-column {
      min-width: 80px;
      border-right: 1px solid #e6e6e6;

      .time-header {
        height: 80px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        background-color: #f5f5f5;
        border-bottom: 1px solid #e6e6e6;
      }

      .time-slot {
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-bottom: 1px solid #f0f0f0;
        font-size: 12px;
        color: #86909c;
      }
    }

    .staff-column {
      min-width: 200px;
      border-right: 1px solid #e6e6e6;

      .staff-header {
        height: 80px;
        padding: 12px;
        background-color: #f5f5f5;
        border-bottom: 1px solid #e6e6e6;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .staff-info {
          display: flex;
          align-items: center;
          gap: 8px;

          .staff-details {
            .staff-name {
              font-weight: 600;
              color: #1d2129;
              font-size: 14px;
            }

            .staff-title {
              font-size: 12px;
              color: #86909c;
            }
          }
        }

        .staff-stats {
          .appointment-count {
            font-size: 18px;
            font-weight: 600;
            color: #409eff;
          }
        }
      }

      .time-slots {
        .time-slot {
          height: 60px;
          border-bottom: 1px solid #f0f0f0;
          position: relative;
          cursor: pointer;
          transition: background-color 0.2s;

          &:hover {
            background-color: #f5f5f5;
          }

          .appointment-item {
            position: absolute;
            top: 2px;
            left: 2px;
            right: 2px;
            bottom: 2px;
            background-color: #409eff;
            border-radius: 4px;
            padding: 4px 6px;
            font-size: 11px;
            color: white;
            cursor: move;
            overflow: hidden;

            &.appointment-pending {
              background-color: #e6a23c;
            }

            &.appointment-confirmed {
              background-color: #67c23a;
            }

            &.appointment-in_progress {
              background-color: #409eff;
            }

            &.appointment-completed {
              background-color: #909399;
            }

            &.appointment-cancelled {
              background-color: #f56c6c;
            }

            .appointment-time {
              font-weight: 500;
              margin-bottom: 2px;
            }

            .appointment-service {
              font-weight: 500;
              margin-bottom: 2px;
            }

            .appointment-customer,
            .appointment-pet {
              opacity: 0.8;
              margin-bottom: 1px;
            }

            .status-tag {
              position: absolute;
              top: 2px;
              right: 2px;
            }
          }
        }
      }
    }
  }
}

.customer-list {
  max-height: 400px;
  overflow-y: auto;

  .customer-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background-color 0.2s;

    &:hover {
      background-color: #f5f5f5;
    }

    .customer-info {
      flex: 1;

      .customer-name {
        font-weight: 500;
        color: #1d2129;
        margin-bottom: 4px;
      }

      .customer-phone {
        color: #86909c;
        font-size: 12px;
        margin-bottom: 4px;
      }
    }

    .customer-balance {
      text-align: right;
      font-size: 12px;
      color: #86909c;
    }
  }
}

.service-option {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .service-name {
    font-weight: 500;
    color: #1d2129;
  }

  .service-info {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12px;

    .service-price {
      color: #f56c6c;
      font-weight: 500;
    }

    .service-duration {
      color: #e6a23c;
    }
  }

  .service-description {
    font-size: 11px;
    color: #86909c;
    line-height: 1.3;
    margin-top: 2px;
  }
}
</style>
