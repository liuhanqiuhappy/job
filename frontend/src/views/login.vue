<template>
  <div class="login-page">
    <nav class="top-nav">
      <div class="nav-inner">
        <div class="nav-left">
          <span class="nav-logo">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
              <rect width="28" height="28" rx="6" fill="#1890ff"/>
              <path d="M8 20V12L14 8L20 12V20H16V16H12V20H8Z" fill="white"/>
            </svg>
            智汇人才匹配系统
          </span>
        </div>
        <div class="nav-right">
          <a href="/homepage" class="nav-item active">首页</a>
          <a href="#intro" class="nav-item">项目介绍</a>
          <a href="#features" class="nav-item">功能介绍</a>
        </div>
      </div>
    </nav>

    <div class="main-body">
      <div class="carousel-section">
        <div class="carousel-container">
          <div class="carousel-track" :style="{ transform: `translateX(-${slideIdx * 100}%)` }">
            <div v-for="(s, i) in slides" :key="i" class="carousel-slide" :style="{ background: s.bg }">
              <div class="slide-overlay"></div>
              <div class="slide-text">
                <h2>{{ s.title }}</h2>
                <p>{{ s.desc }}</p>
              </div>
            </div>
          </div>
          <button class="carousel-arrow left" @click="prevSlide">&lt;</button>
          <button class="carousel-arrow right" @click="nextSlide">&gt;</button>
          <div class="carousel-dots">
            <span v-for="(_, i) in slides" :key="i" :class="['dot', { active: slideIdx === i }]" @click="slideIdx = i"></span>
          </div>
        </div>
      </div>

      <div class="login-section">
        <div class="login-card">
          <div class="login-header">
            <div class="login-avatar">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
            <h2>用户登录</h2>
          </div>

          <div class="role-tabs">
            <button :class="['tab', role === 0 ? 'active' : '']" @click="role = 0">个人用户</button>
            <button :class="['tab', role === 1 ? 'active' : '']" @click="role = 1">企业用户</button>
          </div>

          <div class="form-group">
            <label>用户名</label>
            <input type="text" v-model="form.username" placeholder="请输入用户名">
          </div>
          <div class="form-group">
            <label>密码</label>
            <input type="password" v-model="form.password" placeholder="请输入密码">
          </div>
          <div class="form-group">
            <button class="login-btn" @click="handleLogin">登 录</button>
          </div>
          <p class="register-link">
            还没有账号？<a href="/register">立即注册 →</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const role = ref(0)

const slideIdx = ref(0)
const slides = [
  { title: '省级人才智慧匹配平台', desc: '依托 AI 技术，为企业和人才提供精准、高效的智能匹配服务', bg: '#1a1a2e' },
  { title: '智能简历解析', desc: '上传简历即可自动提取教育背景、工作经验、技能特长等核心信息', bg: '#1890ff' },
  { title: '精准人岗匹配', desc: '多维度能力画像分析，让每一份简历都能找到最适合的岗位', bg: '#096dd9' }
]
let slideTimer = null

const nextSlide = () => { slideIdx.value = (slideIdx.value + 1) % slides.length }
const prevSlide = () => { slideIdx.value = (slideIdx.value - 1 + slides.length) % slides.length }

onMounted(() => {
  slideTimer = setInterval(nextSlide, 4000)
})

onUnmounted(() => {
  if (slideTimer) clearInterval(slideTimer)
})

const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    alert('请输入用户名和密码')
    return
  }

  console.log('登录表单数据:', JSON.stringify(form))

  try {
    const response = await axios.post('/api/login', form, {
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json'
      }
    })

    console.log('登录响应:', response.data)

    if (response.data.code === 0) {
      const userInfo = response.data.data
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      
      const redirectPath = userInfo.role === 0 ? '/personal' : '/enterprise'
      router.push(redirectPath)
    } else {
      alert(response.data.msg || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    if (error.response) {
      console.error('响应状态',  error.response.status)
      console.error('响应数据:', error.response.data)
      alert(error.response.data?.msg || '登录失败')
    } else if (error.request) {
      console.error('请求已发送但无响应', error.request)
      alert('网络请求失败，请检查后端服务是否启动')
    } else {
      console.error('请求配置错误:', error.message)
      alert('请求配置错误: ' + error.message)
    }
  }
}
</script>
<style scoped>
.login-page {
  --primary: #1890ff;
  --primary-hover: #40a9ff;
  --primary-active: #096dd9;
  --bg: #f0f2f5;
  --card-bg: #ffffff;
  --text: #262626;
  --text-secondary: #595959;
  --text-muted: #8c8c8c;
  --border: #e8e8e8;
  --radius: 8px;
  --shadow: 0 2px 8px rgba(0,0,0,0.06);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  min-height: 100vh;
  background: var(--bg);
}

.top-nav { position: fixed; top: 0; left: 0; right: 0; z-index: 100; background: #fff; border-bottom: 1px solid var(--border); height: 56px; }
.nav-inner { max-width: 1200px; margin: 0 auto; padding: 0 24px; height: 100%; display: flex; align-items: center; justify-content: space-between; }
.nav-left { display: flex; align-items: center; }
.nav-logo { display: flex; align-items: center; gap: 10px; font-size: 17px; font-weight: 700; color: var(--text); }
.nav-right { display: flex; align-items: center; gap: 6px; }
.nav-item { padding: 7px 16px; border-radius: var(--radius); font-size: 14px; color: var(--text-secondary); text-decoration: none; transition: all 0.2s; }
.nav-item:hover { color: var(--primary); background: rgba(24,144,255,0.06); }
.nav-item.active { color: var(--primary); font-weight: 500; }

.main-body { margin-top: 56px; max-width: 1200px; margin-left: auto; margin-right: auto; padding: 40px 24px; display: flex; gap: 32px; min-height: calc(100vh - 56px); }

.carousel-section { flex: 1; min-height: 420px; }
.carousel-container { position: relative; width: 100%; height: 100%; overflow: hidden; border-radius: 8px; min-height: 420px; }
.carousel-track { display: flex; height: 100%; transition: transform 0.5s ease; }
.carousel-slide { position: relative; min-width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
.slide-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.40); }
.slide-text { position: relative; z-index: 2; text-align: center; max-width: 560px; padding: 0 24px; }
.slide-text h2 { font-size: 32px; font-weight: 700; color: #fff; margin: 0 0 12px; }
.slide-text p { font-size: 15px; color: rgba(255,255,255,0.80); margin: 0; line-height: 1.6; }
.carousel-arrow { position: absolute; top: 50%; transform: translateY(-50%); z-index: 5; width: 36px; height: 36px; border-radius: 50%; border: none; background: rgba(255,255,255,0.20); color: #fff; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 16px; transition: background 0.2s; }
.carousel-arrow:hover { background: rgba(255,255,255,0.35); }
.carousel-arrow.left { left: 16px; }
.carousel-arrow.right { right: 16px; }
.carousel-dots { position: absolute; bottom: 20px; left: 50%; transform: translateX(-50%); z-index: 5; display: flex; gap: 8px; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: rgba(255,255,255,0.35); cursor: pointer; transition: all 0.3s; }
.dot.active { background: #fff; width: 22px; border-radius: 4px; }

.login-section { width: 360px; flex-shrink: 0; display: flex; align-items: center; }
.login-card { width: 100%; background: var(--card-bg); border-radius: 8px; padding: 32px 28px; box-shadow: var(--shadow); }
.login-header { text-align: center; margin-bottom: 24px; }
.login-avatar { width: 48px; height: 48px; border-radius: 50%; background: var(--primary); display: flex; align-items: center; justify-content: center; margin: 0 auto 12px; }
.login-header h2 { font-size: 20px; font-weight: 600; color: var(--text); margin: 0; }
.role-tabs { display: flex; margin-bottom: 20px; background: #f5f5f5; border-radius: var(--radius); padding: 3px; }
.tab { flex: 1; padding: 8px; border: none; background: transparent; border-radius: 6px; cursor: pointer; font-size: 13px; color: var(--text-secondary); transition: all 0.2s; }
.tab.active { background: #fff; color: var(--primary); box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-size: 13px; font-weight: 500; color: var(--text); }
.form-group input { width: 100%; padding: 10px 12px; border: 1px solid var(--border); border-radius: var(--radius); font-size: 14px; color: var(--text); box-sizing: border-box; transition: border-color 0.2s, box-shadow 0.2s; background: #fafafa; }
.form-group input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px rgba(24,144,255,0.10); background: #fff; }
.login-btn { width: 100%; padding: 10px; background: var(--primary); color: #fff; border: none; border-radius: var(--radius); font-size: 15px; font-weight: 500; cursor: pointer; transition: background 0.2s; }
.login-btn:hover { background: var(--primary-hover); }
.login-btn:active { background: var(--primary-active); }
.register-link { text-align: center; margin-top: 16px; font-size: 14px; color: var(--text-muted); }
.register-link a { color: var(--primary); text-decoration: none; font-weight: 500; }
.register-link a:hover { text-decoration: underline; }

@media (max-width: 900px) { .main-body { flex-direction: column; } .carousel-section { min-height: 280px; } .login-section { width: 100%; max-width: 400px; margin: 0 auto; } }
</style>