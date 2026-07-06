<template>
  <div class="intent-page">
    <h1 class="page-title">💬 我的意向</h1>
    
    <div class="tab-container">
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'sent' }"
        @click="switchTab('sent')"
      >
        我发出的
      </button>
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'received' }"
        @click="switchTab('received')"
      >
        我收到的
      </button>
    </div>
    
    <div v-if="isLoading" class="loading-text">加载中...</div>
    
    <div v-else-if="currentList && currentList.length > 0" class="intent-list">
      <div v-for="item in currentList" :key="item.id" class="intent-card">
        <div class="intent-header">
          <span class="intent-type" :class="item.type === 0 ? 'type-intent' : 'type-invite'">
            {{ item.typeDesc }}
          </span>
          <span class="intent-status" :class="getStatusClass(item.status)">
            {{ item.statusDesc }}
          </span>
        </div>
        
        <div class="intent-info">
          <div class="info-row">
            <span class="info-label">对方</span>
            <span class="info-value">{{ activeTab === 'sent' ? item.toUsername : item.fromUsername }}</span>
          </div>
          <div class="info-row" v-if="item.jobTitle">
            <span class="info-label">职位</span>
            <span class="info-value">{{ item.jobTitle }}</span>
          </div>
          <div class="info-row" v-if="item.resumeTitle">
            <span class="info-label">简历</span>
            <span class="info-value">{{ item.resumeTitle }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">附言</span>
            <span class="info-value message-text">{{ item.message || '无' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">时间</span>
            <span class="info-value">{{ item.createTime }}</span>
          </div>
        </div>
        
        <div class="intent-actions">
          <button 
            v-if="item.status === 0 && activeTab === 'received'" 
            class="action-btn accept-btn"
            @click="handleAccept(item.id)"
          >
            接受
          </button>
          <button 
            v-if="item.status === 0 && activeTab === 'received'" 
            class="action-btn reject-btn"
            @click="handleReject(item.id)"
          >
            拒绝
          </button>
          <button 
            v-if="item.status === 1" 
            class="action-btn contact-btn"
            @click="handleContact(item.id)"
          >
            查看联系方式
          </button>
          <span v-if="item.status === 1 && !item.contactInfo" class="contact-hint">
            对方联系方式将在意向达成后解锁
          </span>
        </div>
      </div>
    </div>
    
    <div v-else class="empty-text">暂无{{ activeTab === 'sent' ? '发出的' : '收到的' }}意向</div>
    
    <div v-if="showContactModal" class="modal-overlay" @click="closeContactModal">
      <div class="modal-content" @click.stop>
        <h3 class="modal-title">联系方式</h3>
        <div class="modal-body" v-if="contactInfo">
          <div class="contact-item">
            <span class="contact-label">姓名</span>
            <span class="contact-value">{{ contactInfo.username }}</span>
          </div>
          <div class="contact-item">
            <span class="contact-label">手机号</span>
            <span class="contact-value">{{ contactInfo.phone }}</span>
          </div>
          <div class="contact-item">
            <span class="contact-label">邮箱</span>
            <span class="contact-value">{{ contactInfo.email }}</span>
          </div>
        </div>
        <div class="modal-body" v-else>
          <p class="empty-contact">加载中...</p>
        </div>
        <div class="modal-footer">
          <button class="confirm-btn" @click="closeContactModal">确定</button>
        </div>
      </div>
    </div>
    
    <div v-if="showMessage" class="toast-message" :class="messageType">
      {{ messageText }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMySentIntents, getMyReceivedIntents, acceptIntent, rejectIntent, getContactInfo } from '../api/intent'

const activeTab = ref('sent')
const isLoading = ref(true)
const sentList = ref([])
const receivedList = ref([])
const showContactModal = ref(false)
const contactInfo = ref(null)
const showMessage = ref(false)
const messageText = ref('')
const messageType = ref('success')

const currentList = computed(() => {
  return activeTab.value === 'sent' ? sentList.value : receivedList.value
})

const loadSentIntents = () => {
  getMySentIntents()
    .then(response => {
      const { code, data } = response.data
      if (code === 0 && data) {
        sentList.value = data
      }
    })
    .catch(error => {
      console.error('获取发出的意向失败', error)
    })
}

const loadReceivedIntents = () => {
  getMyReceivedIntents()
    .then(response => {
      const { code, data } = response.data
      if (code === 0 && data) {
        receivedList.value = data
      }
    })
    .catch(error => {
      console.error('获取收到的意向失败', error)
    })
}

const switchTab = (tab) => {
  activeTab.value = tab
}

const getStatusClass = (status) => {
  switch (status) {
    case 0: return 'status-pending'
    case 1: return 'status-accepted'
    case 2: return 'status-rejected'
    case 3: return 'status-expired'
    default: return ''
  }
}

const handleAccept = (intentId) => {
  acceptIntent(intentId)
    .then(response => {
      const { code, msg } = response.data
      if (code === 0) {
        messageText.value = msg
        messageType.value = 'success'
        showMessage.value = true
        setTimeout(() => {
          showMessage.value = false
        }, 3000)
        loadReceivedIntents()
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
      messageText.value = error.response?.data?.msg || '操作失败，请稍后重试'
      messageType.value = 'error'
      showMessage.value = true
      setTimeout(() => {
        showMessage.value = false
      }, 3000)
    })
}

const handleReject = (intentId) => {
  rejectIntent(intentId)
    .then(response => {
      const { code, msg } = response.data
      if (code === 0) {
        messageText.value = msg
        messageType.value = 'success'
        showMessage.value = true
        setTimeout(() => {
          showMessage.value = false
        }, 3000)
        loadReceivedIntents()
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
      messageText.value = error.response?.data?.msg || '操作失败，请稍后重试'
      messageType.value = 'error'
      showMessage.value = true
      setTimeout(() => {
        showMessage.value = false
      }, 3000)
    })
}

const handleContact = (intentId) => {
  contactInfo.value = null
  showContactModal.value = true
  getContactInfo(intentId)
    .then(response => {
      const { code, data } = response.data
      if (code === 0 && data) {
        contactInfo.value = data
      } else {
        messageText.value = response.data.msg || '获取联系方式失败'
        messageType.value = 'error'
        showMessage.value = true
        setTimeout(() => {
          showMessage.value = false
        }, 3000)
        closeContactModal()
      }
    })
    .catch(error => {
      messageText.value = error.response?.data?.msg || '获取联系方式失败'
      messageType.value = 'error'
      showMessage.value = true
      setTimeout(() => {
        showMessage.value = false
      }, 3000)
      closeContactModal()
    })
}

const closeContactModal = () => {
  showContactModal.value = false
  contactInfo.value = null
}

onMounted(() => {
  isLoading.value = true
  Promise.all([loadSentIntents(), loadReceivedIntents()])
    .finally(() => {
      isLoading.value = false
    })
})
</script>

<style scoped>
.intent-page {
  padding: 40px;
  max-width: 800px;
  margin: 0 auto;
}

.page-title {
  color: #1E3A8A;
  font-size: 24px;
  margin-bottom: 30px;
  text-align: center;
}

.tab-container {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-bottom: 30px;
}

.tab-btn {
  padding: 10px 30px;
  font-size: 16px;
  color: #666;
  background-color: #fff;
  border: 1px solid #e8ecf1;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  border-color: #1E3A8A;
  color: #1E3A8A;
}

.tab-btn.active {
  background-color: #1E3A8A;
  color: #fff;
  border-color: #1E3A8A;
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

.intent-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.intent-card {
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #e8ecf1;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 20px;
}

.intent-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.intent-type {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
}

.type-intent {
  background-color: #e6f7ff;
  color: #1890ff;
}

.type-invite {
  background-color: #fff7e6;
  color: #fa8c16;
}

.intent-status {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
}

.status-pending {
  background-color: #fff7e6;
  color: #fa8c16;
}

.status-accepted {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-rejected {
  background-color: #fff2f0;
  color: #ff4d4f;
}

.status-expired {
  background-color: #f5f5f5;
  color: #999;
}

.intent-info {
  margin-bottom: 16px;
}

.info-row {
  margin-bottom: 10px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  display: inline-block;
  width: 60px;
  color: #1E3A8A;
  font-weight: 500;
  font-size: 14px;
}

.info-value {
  color: #333;
  font-size: 14px;
}

.message-text {
  display: inline-block;
  max-width: calc(100% - 60px);
  vertical-align: top;
  word-break: break-all;
}

.intent-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.action-btn {
  padding: 8px 20px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.accept-btn {
  background-color: #52C41A;
  color: #fff;
}

.accept-btn:hover {
  background-color: #389e0d;
}

.reject-btn {
  background-color: #FF4D4F;
  color: #fff;
}

.reject-btn:hover {
  background-color: #cf1322;
}

.contact-btn {
  background-color: #1E3A8A;
  color: #fff;
}

.contact-btn:hover {
  background-color: #1a3175;
}

.contact-hint {
  color: #999;
  font-size: 14px;
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

.empty-contact {
  text-align: center;
  color: #999;
}

.contact-item {
  margin-bottom: 12px;
}

.contact-item:last-child {
  margin-bottom: 0;
}

.contact-label {
  display: inline-block;
  width: 80px;
  color: #1E3A8A;
  font-weight: 500;
  font-size: 14px;
}

.contact-value {
  color: #333;
  font-size: 14px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
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

.confirm-btn:hover {
  background-color: #1a3175;
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