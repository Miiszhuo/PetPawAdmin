<template>
  <el-upload
    class="avatar-uploader"
    action="#"
    :show-file-list="false"
    :http-request="uploadImage"
    :before-upload="beforeAvatarUpload"
  >
    <img v-if="modelValue" :src="modelValue" class="avatar" />
    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { ElMessage, type UploadRequestOptions } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import request from "@/utils/request";

const props = defineProps<{
  modelValue?: string;
  businessId?: number | string;
  businessType?: string;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
  (e: "uploading-change", value: boolean): void;
}>();

const uploading = ref(false);

const uploadImage = async (options: UploadRequestOptions) => {
  const formData = new FormData();
  formData.append("file", options.file);
  if (props.businessId !== undefined && props.businessId !== null) {
    formData.append("businessId", props.businessId.toString());
  }
  if (props.businessType) {
    formData.append("businessType", props.businessType);
  }

  try {
    uploading.value = true;
    emit("uploading-change", true);
    const res: any = await request.post("/file/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    if (res.code === 200) {
      // 直接发射事件更新父组件数据
      emit("update:modelValue", res.data);
      ElMessage.success("上传成功");
    }
  } catch (error) {
    console.error("Upload failed", error);
    ElMessage.error("图片上传失败");
  } finally {
    uploading.value = false;
    emit("uploading-change", false);
  }
};

const beforeAvatarUpload = (rawFile: any) => {
  const isJPG = rawFile.type === "image/jpeg" || rawFile.type === "image/png";
  const isLt2M = rawFile.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error("上传头像图片只能是 JPG/PNG 格式!");
  }
  if (!isLt2M) {
    ElMessage.error("上传头像图片大小不能超过 2MB!");
  }
  return isJPG && isLt2M;
};
</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
  border-radius: 6px;
}
</style>
