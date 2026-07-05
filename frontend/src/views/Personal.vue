<template>
  <div class="dashboard">
    <h1>欢迎个人用户</h1>
    <p>您可以在这里上传简历并查看职位推荐</p>
    
    <div 
      class="upload-area"
      :class="{ 'drag-over': isDragOver }"
      @dragover.prevent="onDragOver"
      @dragleave="onDragLeave"
      @drop.prevent="onDrop"
      @click="triggerFileInput"
    >
      <input 
        type="file" 
        ref="fileInput"
        class="file-input"
        accept=".doc,.docx,.pdf"
        @change="onFileSelect"
      />
      <div class="upload-icon">📄</div>
      <p class="upload-text">点击或拖拽文件到这里上传简历</p>
      <p class="upload-hint">支持 .doc、.docx、.pdf 格式，最大 10MB</p>
    </div>
    
    <div v-if="uploadMessage" class="upload-message" :class="uploadSuccess ? 'success' : 'error'">
      {{ uploadMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { upload } from '../api/user'

const fileInput = ref(null)
const isDragOver = ref(false)
const uploadMessage = ref('')
const uploadSuccess = ref(false)

const triggerFileInput = () => {
  fileInput.value.click()
}

const onDragOver = () => {
  isDragOver.value = true
}

const onDragLeave = () => {
  isDragOver.value = false
}

const onDrop = (e) => {
  isDragOver.value = false
  const files = e.dataTransfer.files
  if (files.length > 0) {
    handleFile(files[0])
  }
}

const onFileSelect = (e) => {
  const files = e.target.files
  if (files.length > 0) {
    handleFile(files[0])
  }
}

const handleFile = (file) => {
  const fileName = file.name
  const extension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase()
  const allowedExtensions = ['.doc', '.docx', '.pdf']
  
  if (!allowedExtensions.includes(extension)) {
    uploadMessage.value = '只支持 .doc、.docx、.pdf 格式的文件'
    uploadSuccess.value = false
    return
  }
  
  if (file.size > 10 * 1024 * 1024) {
    uploadMessage.value = '文件大小不能超过10MB'
    uploadSuccess.value = false
    return
  }
  
  uploadMessage.value = '正在上传...'
  uploadSuccess.value = false
  
  upload(file, 'resume')
    .then(response => {
      const { code, msg } = response.data
      if (code === 0) {
        uploadMessage.value = '文件 ' + fileName + ' 上传成功'
        uploadSuccess.value = true
      } else {
        uploadMessage.value = msg
        uploadSuccess.value = false
      }
    })
    .catch(error => {
      if (error.response && error.response.data) {
        uploadMessage.value = error.response.data.msg || '上传失败'
      } else {
        uploadMessage.value = '上传失败，请稍后重试'
      }
      uploadSuccess.value = false
    })
}
</script>

<style scoped>
.dashboard {
  padding: 40px;
  text-align: center;
}

h1 {
  color: #333;
  margin-bottom: 20px;
}

p {
  color: #666;
  margin: 10px 0;
}

.upload-area {
  width: 400px;
  height: 200px;
  margin: 30px auto;
  border: 2px dashed #ccc;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #fff;
}

.upload-area:hover {
  border-color: #1890ff;
  background-color: #f0f5ff;
}

.upload-area.drag-over {
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.file-input {
  display: none;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.upload-text {
  color: #333;
  font-size: 16px;
  margin: 0 0 10px 0;
}

.upload-hint {
  color: #999;
  font-size: 12px;
  margin: 0;
}

.upload-message {
  margin-top: 20px;
  padding: 12px 20px;
  border-radius: 4px;
  font-size: 14px;
  display: inline-block;
}

.upload-message.success {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.upload-message.error {
  background-color: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}
</style>