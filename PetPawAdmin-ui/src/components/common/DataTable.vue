<template>
  <div class="data-table">
    <el-table
      v-loading="loading"
      :data="data"
      :stripe="stripe"
      :border="border"
      :height="height"
      style="width: 100%"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
    >
      <el-table-column v-if="showSelection" type="selection" width="55" />

      <el-table-column
        v-for="column in columns"
        :key="column.prop"
        :prop="column.prop"
        :label="column.label"
        :width="column.width"
        :min-width="column.minWidth"
        :sortable="column.sortable"
        :align="column.align || 'left'"
      >
        <template #default="scope">
          <slot
            :name="`column-${column.prop}`"
            :row="scope.row"
            :column="column"
            :index="scope.$index"
          >
            {{ scope.row[column.prop] }}
          </slot>
        </template>
      </el-table-column>

      <el-table-column
        v-if="showActions"
        label="操作"
        :width="actionsWidth"
        align="center"
        fixed="right"
      >
        <template #default="scope">
          <slot name="actions" :row="scope.row" :index="scope.$index">
            <slot
              :name="`column-actions`"
              :row="scope.row"
              :index="scope.$index"
            ></slot>
          </slot>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="pagination && total > 0"
      :current-page="pagination.pageNum"
      :page-size="pagination.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
interface Column {
  prop: string;
  label: string;
  width?: number | string;
  minWidth?: number | string;
  sortable?: boolean | string;
  align?: "left" | "center" | "right";
}

interface Pagination {
  pageNum: number;
  pageSize: number;
}

interface Props {
  data: any[];
  columns: Column[];
  loading?: boolean;
  total?: number;
  pagination?: Pagination;
  showSelection?: boolean;
  showActions?: boolean;
  actionsWidth?: number | string;
  stripe?: boolean;
  border?: boolean;
  height?: number | string;
}

interface Emits {
  (e: "selection-change", selection: any[]): void;
  (e: "sort-change", sort: any): void;
  (e: "page-change", pageNum: number, pageSize: number): void;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  total: 0,
  showSelection: false,
  showActions: true,
  actionsWidth: 180,
  stripe: true,
  border: false,
});

const emit = defineEmits<Emits>();

const handleSelectionChange = (selection: any[]) => {
  emit("selection-change", selection);
};

const handleSortChange = (sort: any) => {
  emit("sort-change", sort);
};

const handleSizeChange = (pageSize: number) => {
  if (props.pagination) {
    emit("page-change", props.pagination.pageNum, pageSize);
  }
};

const handleCurrentChange = (pageNum: number) => {
  if (props.pagination) {
    emit("page-change", pageNum, props.pagination.pageSize);
  }
};
</script>

<style scoped lang="scss">
.data-table {
  background-color: #fff;
  border-radius: 6px;

  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
