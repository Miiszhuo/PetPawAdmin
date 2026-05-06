<template>
  <div class="page-header">
    <div class="page-header-content">
      <div class="page-title">
        <h2>{{ title }}</h2>
        <p v-if="description" class="page-description">{{ description }}</p>
      </div>
      <div class="page-actions">
        <slot name="actions"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title: string;
  description?: string;
}

defineProps<Props>();
</script>

<style scoped lang="scss">
.page-header {
  background-color: #fff;
  padding: 20px 24px;
  border-bottom: 1px solid #e6e6e6;
  margin-bottom: 20px;
  border-radius: 6px;

  .page-header-content {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;

    .page-title {
      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
        font-weight: 600;
        color: #1d2129;
      }

      .page-description {
        margin: 0;
        color: #86909c;
        font-size: 14px;
      }
    }

    .page-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }
}
</style>
