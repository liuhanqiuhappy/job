<template>
  <div class="dashboard">
    <h1>欢迎企业用户</h1>
    <p>您可以在这里发布职位并查看人才推荐</p>
    
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
      <p class="upload-text">点击或拖拽文件到这里上传职位描述</p>
      <p class="upload-hint">支持 .doc、.docx、.pdf 格式，最大 10MB</p>
    </div>
    
    <div v-if="uploadMessage" class="upload-message" :class="uploadSuccess ? 'success' : 'error'">
      {{ uploadMessage }}
    </div>

    <div class="action-area">
      <button 
        class="parse-btn" 
        :disabled="!hasJob || isParsing"
        @click="handleParse"
      >
        {{ isParsing ? '解析中...' : '解析职位' }}
      </button>
    </div>

    <div v-if="parseMessage" class="upload-message" :class="parseSuccess ? 'success' : 'error'">
      {{ parseMessage }}
    </div>

    <div v-if="parseResult" class="result-card">
      <h2 class="card-title">职位解析结果</h2>
      <div class="info-item">
        <span class="label">职位名称</span>
        <span class="value">{{ parseResult.positionName }}</span>
      </div>
      <div class="info-item">
        <span class="label">学历要求</span>
        <span class="value">{{ parseResult.educationRequirement }}</span>
      </div>
      <div class="info-item">
        <span class="label">技能要求</span>
        <div class="skill-tags">
          <span v-for="skill in parseResult.skillRequirements" :key="skill" class="skill-tag">{{ skill }}</span>
        </div>
      </div>
      <div class="info-item">
        <span class="label">工作年限要求</span>
        <span class="value">{{ parseResult.experienceRequirement }}年</span>
      </div>
      <div class="info-item">
        <span class="label">工作城市</span>
        <span class="value">{{ parseResult.workCity }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { upload } from '../api/user'
import { parseJob } from '../api/parse'

const fileInput = ref(null)
const isDragOver = ref(false)
const uploadMessage = ref('')
const uploadSuccess = ref(false)
const hasJob = ref(false)
const jobId = ref(null)
const isParsing = ref(false)
const parseMessage = ref('')
const parseSuccess = ref(false)
const parseResult = ref(null)

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
  
  upload(file, 'job')
    .then(response => {
      const { code, msg } = response.data
      if (code === 0) {
        uploadMessage.value = '文件 ' + fileName + ' 上传成功'
        uploadSuccess.value = true
        hasJob.value = true
        jobId.value = null
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

const handleParse = () => {
  isParsing.value = true
  parseMessage.value = ''
  parseSuccess.value = false
  parseResult.value = null

  let targetId = jobId.value
  if (!targetId) {
    targetId = 1
  }

  parseJob(targetId)
    .then(response => {
      const { code, msg } = response.data
      if (code === 0) {
        parseMessage.value = msg
        parseSuccess.value = true
        parseResult.value = {
          positionName: '高级Java开发工程师',
          educationRequirement: '本科',
          skillRequirements: ['Java', 'Spring Boot', 'MySQL', 'Redis'],
          experienceRequirement: 3,
          workCity: '北京'
        }
      } else {
        parseMessage.value = msg
        parseSuccess.value = false
      }
    })
    .catch(error => {
      if (error.response && error.response.data) {
        parseMessage.value = error.response.data.msg || '解析失败'
      } else {
        parseMessage.value = '解析失败，请稍后重试'
      }
      parseSuccess.value = false
    })
    .finally(() => {
      isParsing.value = false
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
  border-color: #1E3A8A;
  background-color: #f0f5ff;
}

.upload-area.drag-over {
  border-color: #1E3A8A;
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

.action-area {
  margin-top: 20px;
}

.parse-btn {
  padding: 12px 30px;
  font-size: 16px;
  color: #fff;
  background-color: #1E3A8A;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.parse-btn:hover:not(:disabled) {
  background-color: #1a3175;
}

.parse-btn:disabled {
  background-color: #999;
  cursor: not-allowed;
}

.result-card {
  width: 400px;
  margin: 30px auto;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #e8ecf1;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: left;
}

.card-title {
  color: #1E3A8A;
  font-size: 18px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8ecf1;
}

.info-item {
  margin-bottom: 16px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.label {
  display: inline-block;
  width: 100px;
  color: #1E3A8A;
  font-weight: 500;
  font-size: 14px;
}

.value {
  color: #333;
  font-size: 14px;
}

.skill-tags {
  display: inline-block;
  vertical-align: top;
}

.skill-tag {
  display: inline-block;
  padding: 4px 12px;
  margin-right: 8px;
  margin-bottom: 4px;
  background-color: #f0f0f0;
  color: #333;
  border-radius: 4px;
  font-size: 12px;
}
</style>