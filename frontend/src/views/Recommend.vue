<template>
  <div class="recommend-page">
    <h1 class="page-title">📋 智能职位推荐</h1>
    
    <div v-if="isLoading" class="loading-text">加载中...</div>
    
    <div v-else-if="recommendList && recommendList.length > 0" class="job-list">
      <div v-for="item in recommendList" :key="item.jobId" class="job-card">
        <div class="job-header">
          <h3 class="job-title">{{ getJobName(item) }}</h3>
          <span class="company-name">{{ item.username }}</span>
        </div>
        
        <div class="match-score">
          <span class="score-label">匹配度</span>
          <span class="score-value" :class="getScoreClass(item.matchResult.totalScore)">
            {{ Math.round(item.matchResult.totalScore) }}分
          </span>
          <div class="score-bar">
            <div class="score-fill" :class="getScoreClass(item.matchResult.totalScore)" 
                 :style="{ width: item.matchResult.totalScore + '%' }"></div>
          </div>
        </div>
        
        <div class="job-details">
          <div class="detail-item">
            <span class="detail-label">学历要求</span>
            <span class="detail-value">{{ getEducation(item) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">工作城市</span>
            <span class="detail-value">{{ getCity(item) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">技能要求</span>
            <div class="skill-tags">
              <span v-for="skill in getSkills(item)" :key="skill" class="skill-tag">{{ skill }}</span>
            </div>
          </div>
        </div>
        
        <button class="send-intent-btn" @click="openSendModal(item)">发送意向</button>
      </div>
    </div>
    
    <div v-else class="empty-text">暂无推荐职位，请先上传并解析简历</div>
    
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h3 class="modal-title">发送意向书</h3>
        <div class="modal-body">
          <p class="modal-info">职位：{{ selectedJob ? getJobName(selectedJob) : '' }}</p>
          <p class="modal-info">公司：{{ selectedJob ? selectedJob.username : '' }}</p>
          <textarea 
            class="message-input"
            v-model="message"
            placeholder="请输入自荐语（最多200字）"
            maxlength="200"
            rows="4"
          ></textarea>
          <span class="char-count">{{ message.length }}/200</span>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeModal">取消</button>
          <button class="confirm-btn" :disabled="!message.trim()" @click="submitIntent">确认发送</button>
        </div>
      </div>
    </div>
    
    <div v-if="showMessage" class="toast-message" :class="messageType">
      {{ messageText }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { service } from '../api/user'
import { sendIntent } from '../api/intent'

const isLoading = ref(true)
const recommendList = ref([])
const showModal = ref(false)
const selectedJob = ref(null)
const message = ref('')
const showMessage = ref(false)
const messageText = ref('')
const messageType = ref('success')

const loadRecommendJobs = () => {
  isLoading.value = true
  service.get('/match/recommend/jobs')
    .then(response => {
      const { code, data } = response.data
      if (code === 0 && data) {
        recommendList.value = data
      }
    })
    .catch(error => {
      console.error('获取推荐职位失败', error)
    })
    .finally(() => {
      isLoading.value = false
    })
}

const getJobName = (item) => {
  if (item.matchResult && item.matchResult.jobParsed) {
    const parsed = typeof item.matchResult.jobParsed === 'string' 
      ? JSON.parse(item.matchResult.jobParsed) 
      : item.matchResult.jobParsed
    return parsed.positionName || '未知职位'
  }
  return '未知职位'
}

const getEducation = (item) => {
  if (item.matchResult && item.matchResult.jobParsed) {
    const parsed = typeof item.matchResult.jobParsed === 'string' 
      ? JSON.parse(item.matchResult.jobParsed) 
      : item.matchResult.jobParsed
    return parsed.educationRequirement || '不限'
  }
  return '不限'
}

const getCity = (item) => {
  if (item.matchResult && item.matchResult.jobParsed) {
    const parsed = typeof item.matchResult.jobParsed === 'string' 
      ? JSON.parse(item.matchResult.jobParsed) 
      : item.matchResult.jobParsed
    return parsed.workCity || '不限'
  }
  return '不限'
}

const getSkills = (item) => {
  if (item.matchResult && item.matchResult.jobParsed) {
    const parsed = typeof item.matchResult.jobParsed === 'string' 
      ? JSON.parse(item.matchResult.jobParsed) 
      : item.matchResult.jobParsed
    return parsed.skillRequirements || []
  }
  return []
}

const getScoreClass = (score) => {
  if (score >= 80) return 'score-green'
  if (score >= 60) return 'score-orange'
  return 'score-red'
}

const openSendModal = (item) => {
  selectedJob.value = item
  message.value = ''
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedJob.value = null
  message.value = ''
}

const submitIntent = () => {
  if (!selectedJob.value || !message.value.trim()) return
  
  sendIntent({
    toUserId: selectedJob.value.userId,
    jobId: selectedJob.value.jobId,
    message: message.value.trim()
  })
  .then(response => {
    const { code, msg } = response.data
    if (code === 0) {
      messageText.value = msg
      messageType.value = 'success'
      showMessage.value = true
      closeModal()
      setTimeout(() => {
        showMessage.value = false
      }, 3000)
    } else {
      messageText.value = msg
      messageType.value = 'error'
      showMessage.value = true
      setTimeout(() => {
        showMessage.value = false
      }, 3000)
    }
  })
  .catch(error => {
    messageText.value = error.response?.data?.msg || '发送失败，请稍后重试'
    messageType.value = 'error'
    showMessage.value = true
    setTimeout(() => {
      showMessage.value = false
    }, 3000)
  })
}

onMounted(() => {
  loadRecommendJobs()
})
</script>

<style scoped>
.recommend-page {
  padding: 40px;
  max-width: 1000px;
  margin: 0 auto;
}

.page-title {
  color: #1E3A8A;
  font-size: 24px;
  margin-bottom: 30px;
  text-align: center;
}

.loading-text {
  text-align: center;
  color: #999;
  padding: 50px 0;
}

.empty-text {
  text-align: center;
  color: #999;
  padding: 50px 0;
  font-size: 16px;
}

.job-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.job-card {
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #e8ecf1;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 20px;
}

.job-header {
  margin-bottom: 16px;
}

.job-title {
  color: #1E3A8A;
  font-size: 18px;
  margin: 0 0 8px 0;
}

.company-name {
  color: #666;
  font-size: 14px;
}

.match-score {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.score-label {
  color: #666;
  font-size: 14px;
  margin-right: 10px;
}

.score-value {
  font-size: 24px;
  font-weight: bold;
}

.score-green {
  color: #52C41A;
}

.score-orange {
  color: #FAAD14;
}

.score-red {
  color: #FF4D4F;
}

.score-bar {
  height: 6px;
  background-color: #e8ecf1;
  border-radius: 3px;
  margin-top: 8px;
  overflow: hidden;
}

.score-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.5s ease;
}

.score-fill.score-green {
  background-color: #52C41A;
}

.score-fill.score-orange {
  background-color: #FAAD14;
}

.score-fill.score-red {
  background-color: #FF4D4F;
}

.job-details {
  margin-bottom: 16px;
}

.detail-item {
  margin-bottom: 12px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-label {
  display: inline-block;
  width: 80px;
  color: #1E3A8A;
  font-weight: 500;
  font-size: 14px;
}

.detail-value {
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

.send-intent-btn {
  width: 100%;
  padding: 10px 20px;
  font-size: 14px;
  color: #fff;
  background-color: #1E3A8A;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.send-intent-btn:hover {
  background-color: #1a3175;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  width: 400px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.modal-title {
  color: #1E3A8A;
  font-size: 18px;
  margin: 0 0 16px 0;
}

.modal-body {
  margin-bottom: 20px;
}

.modal-info {
  color: #666;
  font-size: 14px;
  margin: 4px 0;
}

.message-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #e8ecf1;
  border-radius: 4px;
  font-size: 14px;
  resize: none;
  box-sizing: border-box;
}

.message-input:focus {
  outline: none;
  border-color: #1E3A8A;
}

.char-count {
  color: #999;
  font-size: 12px;
  float: right;
  margin-top: 4px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 8px 20px;
  font-size: 14px;
  color: #666;
  background-color: #f0f2f5;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.cancel-btn:hover {
  background-color: #e8ecf1;
}

.confirm-btn {
  padding: 8px 20px;
  font-size: 14px;
  color: #fff;
  background-color: #1E3A8A;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.confirm-btn:hover:not(:disabled) {
  background-color: #1a3175;
}

.confirm-btn:disabled {
  background-color: #999;
  cursor: not-allowed;
}

.toast-message {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 16px 32px;
  border-radius: 4px;
  font-size: 16px;
  z-index: 2000;
}

.toast-message.success {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.toast-message.error {
  background-color: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}
</style>