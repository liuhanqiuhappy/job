<template>
  <div class="dashboard">
    <h1>👋 欢迎回来，个人用户</h1>
    <p>您可以在这里上传简历并查看职位推荐</p>

    <div class="profile-card">
      <h2 class="card-title">📋 我的档案</h2>
      <div v-if="isLoading" class="loading-text">加载中...</div>
      <div v-else-if="profileData" class="profile-content">
        <div class="info-item">
          <span class="label">姓名</span>
          <span class="value">{{ profileData.name || '暂未提取到该信息' }}</span>
        </div>
        <div class="info-item">
          <span class="label">学历</span>
          <span class="value">{{ profileData.education || '暂未提取到该信息' }}</span>
        </div>
        <div class="info-item">
          <span class="label">工作年限</span>
          <span class="value">{{ (profileData.experience || profileData.experience === 0) ? profileData.experience + '年' : '暂未提取到该信息' }}</span>
        </div>
        <div class="info-item">
          <span class="label">期望城市</span>
          <span class="value">{{ profileData.city || '暂未提取到该信息' }}</span>
        </div>
        <div class="info-item">
          <span class="label">技能</span>
          <div class="skill-tags" v-if="profileData.skills && profileData.skills.length > 0">
            <span v-for="skill in profileData.skills" :key="skill" class="skill-tag">{{ skill }}</span>
          </div>
          <span v-else class="value">暂未提取到该信息</span>
        </div>
      </div>
      <div v-else class="empty-text">暂未解析简历，请先上传并解析</div>
    </div>

    <div v-if="profileData || personalDimensions" class="graph-section">
      <div class="graph-card">
        <h2 class="card-title">📊 能力图谱</h2>
        <div v-if="isGraphLoading" class="loading-text">加载中...</div>
        <div v-else-if="personalDimensions" class="graph-content">
          <div class="chart-wrapper">
            <h3 class="chart-title">能力雷达图</h3>
            <RadarChart 
              :personal-data="personalDimensions" 
              :job-data="jobDimensions" 
            />
          </div>
          <div class="chart-wrapper">
            <h3 class="chart-title">技能关联图</h3>
            <ForceGraph 
              :personal-skills="personalDimensions.skillTags || []" 
              :job-skills="jobDimensions ? jobDimensions.skillTags || [] : []" 
            />
          </div>
        </div>
        <div v-else class="empty-text">暂无职位匹配，建议先查看推荐职位</div>
        
        <div class="quick-links">
          <router-link to="/recommend" class="quick-link">
            📋 查看推荐职位
          </router-link>
          <router-link to="/intents" class="quick-link">
            💬 我的意向
          </router-link>
        </div>
      </div>
    </div>
    
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

    <div class="action-area">
      <button 
        class="parse-btn" 
        :disabled="!hasResume || isParsing"
        @click="handleParse"
      >
        {{ isParsing ? '解析中...' : '解析简历' }}
      </button>
    </div>

    <div v-if="parseMessage" class="upload-message" :class="parseSuccess ? 'success' : 'error'">
      {{ parseMessage }}
    </div>

    <div v-if="parseResult" class="result-card">
      <h2 class="card-title">简历解析结果</h2>
      <div class="info-item">
        <span class="label">姓名</span>
        <span class="value">{{ parseResult.name || '暂未提取到该信息' }}</span>
      </div>
      <div class="info-item">
        <span class="label">学历</span>
        <span class="value">{{ parseResult.education || '暂未提取到该信息' }}</span>
      </div>
      <div class="info-item">
        <span class="label">技能</span>
        <div class="skill-tags" v-if="parseResult.skills && parseResult.skills.length > 0">
          <span v-for="skill in parseResult.skills" :key="skill" class="skill-tag">{{ skill }}</span>
        </div>
        <span v-else class="value">暂未提取到该信息</span>
      </div>
      <div class="info-item">
        <span class="label">工作年限</span>
        <span class="value">{{ (parseResult.experience || parseResult.experience === 0) ? parseResult.experience + '年' : '暂未提取到该信息' }}</span>
      </div>
      <div class="info-item">
        <span class="label">期望城市</span>
        <span class="value">{{ parseResult.city || '暂未提取到该信息' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { upload } from '../api/user'
import { parseResume } from '../api/parse'
import { getResumeProfile } from '../api/profile'
import { getResumeDimensions, getJobDimensions } from '../api/dimension'
import RadarChart from '../components/RadarChart.vue'
import ForceGraph from '../components/ForceGraph.vue'

const fileInput = ref(null)
const isLoading = ref(true)
const profileData = ref(null)
const isDragOver = ref(false)
const uploadMessage = ref('')
const uploadSuccess = ref(false)
const hasResume = ref(false)
const resumeId = ref(null)
const isParsing = ref(false)
const parseMessage = ref('')
const parseSuccess = ref(false)
const parseResult = ref(null)
const personalDimensions = ref(null)
const jobDimensions = ref(null)
const isGraphLoading = ref(false)

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
      const { code, msg, data } = response.data
      if (code === 0) {
        uploadMessage.value = '文件 ' + fileName + ' 上传成功'
        uploadSuccess.value = true
        hasResume.value = true
        resumeId.value = data || null
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

  let targetId = resumeId.value
  if (!targetId) {
    targetId = 1
  }

  parseResume(targetId)
    .then(response => {
      const { code, msg, data } = response.data
      if (code === 0) {
        parseMessage.value = msg
        parseSuccess.value = true
        if (data) {
          try {
            parseResult.value = typeof data === 'string' ? JSON.parse(data) : data
          } catch (e) {
            console.error('解析JSON失败', e)
            parseResult.value = {
              name: '解析失败',
              education: '解析失败',
              skills: [],
              experience: 0,
              city: '解析失败'
            }
          }
        } else {
          parseResult.value = {
            name: '暂未提取到该信息',
            education: '暂未提取到该信息',
            skills: [],
            experience: 0,
            city: '暂未提取到该信息'
          }
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
      if (parseSuccess.value) {
        loadDimensions()
      }
    })
}

const loadProfile = () => {
  isLoading.value = true
  profileData.value = null
  getResumeProfile()
    .then(response => {
      const { code, data } = response.data
      if (code === 0 && data) {
        try {
          profileData.value = typeof data.parsedJson === 'string' ? JSON.parse(data.parsedJson) : data.parsedJson
        } catch (e) {
          console.error('解析档案JSON失败', e)
          profileData.value = null
        }
      }
    })
    .catch(error => {
      console.error('加载档案失败', error)
      profileData.value = null
    })
    .finally(() => {
      isLoading.value = false
    })
}

const loadDimensions = () => {
  isGraphLoading.value = true
  personalDimensions.value = null
  jobDimensions.value = null

  let targetResumeId = resumeId.value
  if (!targetResumeId) {
    targetResumeId = 1
  }

  getResumeDimensions(targetResumeId)
    .then(response => {
      const { code, data } = response.data
      if (code === 0 && data) {
        personalDimensions.value = data
        fetchRecommendJobs()
      }
    })
    .catch(error => {
      console.error('获取个人维度失败', error)
    })
    .finally(() => {
      isGraphLoading.value = false
    })
}

const fetchRecommendJobs = () => {
  axios.get('/api/match/recommend/jobs', { withCredentials: true })
    .then(response => {
      const { code, data } = response.data
      if (code === 0 && data && data.length > 0) {
        const firstJob = data[0]
        const jobId = firstJob.id || firstJob.jobId
        if (jobId) {
          getJobDimensions(jobId)
            .then(response => {
              const { code, data } = response.data
              if (code === 0 && data) {
                jobDimensions.value = data
              }
            })
            .catch(error => {
              console.error('获取职位维度失败', error)
            })
        }
      }
    })
    .catch(error => {
      console.error('获取推荐职位失败', error)
    })
}

onMounted(() => {
  loadProfile()
})
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
  width: 80px;
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
  margin: 4px;
  background-color: #f0f2f5;
  color: #333;
  border-radius: 16px;
  font-size: 12px;
}

.profile-card {
  width: 400px;
  margin: 30px auto;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #e8ecf1;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: left;
}

.loading-text {
  color: #999;
  font-size: 14px;
  text-align: center;
  padding: 20px 0;
}

.empty-text {
  color: #999;
  font-size: 14px;
  text-align: center;
  padding: 20px 0;
}

.profile-content {
  padding-top: 8px;
}

.graph-section {
  margin-top: 30px;
}

.graph-card {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #e8ecf1;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: left;
}

.graph-content {
  display: flex;
  gap: 24px;
  padding-top: 16px;
}

.chart-wrapper {
  flex: 1;
  min-height: 350px;
}

.chart-title {
  color: #1E3A8A;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
}

.quick-links {
  display: flex;
  gap: 16px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e8ecf1;
}

.quick-link {
  padding: 8px 20px;
  background-color: #1E3A8A;
  color: #fff;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.quick-link:hover {
  background-color: #1a3175;
}

@media (max-width: 768px) {
  .graph-content {
    flex-direction: column;
  }
  
  .quick-links {
    flex-direction: column;
  }
}
</style>