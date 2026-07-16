<template>
  <div class="dash-layout">
    <AppSidebar :user-info="userInfo" />

    <div class="main-area">
      <header class="topbar">
        <h2 class="page-title">{{ currentPageTitle }}</h2>
        <div class="topbar-right">
          <div class="user-dropdown" @click="showDropdown = !showDropdown">
            <div class="avatar-wrap">
              <span>{{ userInfo?.username?.charAt(0) || 'U' }}</span>
            </div>
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9l6 6 6-6"/></svg>
            <div v-if="showDropdown" class="dropdown-menu">
              <div class="dropdown-header">{{ userInfo?.username || '用户' }}</div>
              <button class="dropdown-item" @click="handleLogout">退出登录</button>
            </div>
          </div>
        </div>
      </header>

      <main class="main-content">
        <section class="dash-carousel">
          <div class="dash-carousel-track" :style="{ transform: 'translateX(-' + (dashCurrent * 100) + '%)' }">
            <div v-for="(banner, idx) in dashBanners" :key="idx" class="dash-slide" :style="{ background: banner.bg }">
              <div class="dash-slide-content">
                <h3>{{ banner.title }}</h3>
                <p>{{ banner.desc }}</p>
              </div>
            </div>
          </div>
          <div class="dash-dots">
            <span v-for="(_, idx) in dashBanners" :key="idx" :class="['dash-dot', { active: dashCurrent === idx }]" @click="dashCurrent = idx"></span>
          </div>
        </section>

        <section class="stats-grid">
          <div class="stat-card" v-for="(stat, idx) in statCards" :key="idx">
            <div class="stat-icon" :class="stat.colorClass" v-html="stat.icon"></div>
            <div class="stat-info"><span class="stat-val">{{ stat.value }}</span><span class="stat-lbl">{{ stat.label }}</span></div>
          </div>
        </section>

        <section class="card-grid">
          <router-link v-for="card in dashboardCards" :key="card.path" :to="card.path" class="dash-card" :style="{ '--accent': card.color }">
            <div class="card-header">
              <span class="card-ico" v-html="card.icon"></span>
              <span class="card-ttl">{{ card.title }}</span>
            </div>
            <p class="card-desc">{{ card.desc }}</p>
            <span class="card-act">进入</span>
          </router-link>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppSidebar from '../components/AppSidebar.vue'

const router = useRouter()
const route = useRoute()

const userInfo = ref(null)
const showDropdown = ref(false)
const dashCurrent = ref(0)
let dashTimer = null

const dashBanners = [
  { title: '欢迎回来', desc: '查看最新的人才匹配推荐和能力分析报告', bg: '#1890ff' },
  { title: '智能匹配进行中', desc: 'AI 正在为您分析最佳匹配方案', bg: '#096dd9' },
  { title: '新功能上线', desc: '体验全新的能力图谱可视化功能', bg: '#1a1a2e' }
]

const roleLabel = computed(() => {
  return userInfo.value?.role === 0 ? '个人用户' : userInfo.value?.role === 1 ? '企业用户' : '用户'
})

const isPersonal = computed(() => userInfo.value?.role === 0)
const currentPath = computed(() => route.path)
const currentPageTitle = computed(() => '工作台')

const navItems = computed(() => {
  if (userInfo.value?.role === 0) {
    return [
      { path: '/dashboard', label: '工作台', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>' },
      { path: '/personal', label: '能力图谱', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>' },
      { path: '/recommend', label: '职位推荐', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>' },
      { path: '/intents', label: '我的意向', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>' }
    ]
  }
  return [
    { path: '/dashboard', label: '工作台', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>' },
    { path: '/enterprise', label: '职位档案', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>' },
    { path: '/candidates', label: '候选人才', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>' },
    { path: '/intents', label: '意向管理', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>' }
  ]
})

const statCards = computed(() => [
  { value: '-', label: '简历/职位', colorClass: 'blue', icon: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>' },
  { value: '-', label: '匹配推荐', colorClass: 'green', icon: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>' },
  { value: '-', label: '意向处理', colorClass: 'orange', icon: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>' },
  { value: '-', label: '能力分析', colorClass: 'purple', icon: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>' }
])

const dashboardCards = computed(() => {
  if (userInfo.value?.role === 0) {
    return [
      { path: '/personal', title: '我的档案', desc: '查看简历解析结果和能力画像', color: '#1677ff', icon: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>' },
      { path: '/recommend', title: '职位推荐', desc: 'AI 智能匹配最适合您的岗位', color: '#52c41a', icon: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>' },
      { path: '/intents', title: '意向管理', desc: '跟踪投递状态和面试邀请', color: '#faad14', icon: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>' }
    ]
  }
  return [
    { path: '/enterprise', title: '职位档案', desc: '管理职位描述和招聘需求', color: '#1677ff', icon: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>' },
    { path: '/candidates', title: '候选人才', desc: '查看推荐候选人和匹配评分', color: '#52c41a', icon: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>' },
    { path: '/intents', title: '意向管理', desc: '管理人才邀请和意向反馈', color: '#faad14', icon: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>' }
  ]
})

const handleLogout = () => {
  localStorage.removeItem('userInfo')
  router.push('/login')
}

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    try { userInfo.value = JSON.parse(stored) } catch { userInfo.value = null }
  }
  if (!userInfo.value) { router.push('/login') }
  dashTimer = setInterval(() => {
    dashCurrent.value = (dashCurrent.value + 1) % dashBanners.length
  }, 4500)
})

onUnmounted(() => {
  if (dashTimer) clearInterval(dashTimer)
})
</script>

<style scoped>
.dash-layout {
  --bg: #f0f2f5;
  --card-bg: #ffffff;
  --text: #262626;
  --text-secondary: #595959;
  --text-muted: #8c8c8c;
  --border: #f0f0f0;
  --sidebar-width: 220px;
  --radius: 8px;
  --shadow: 0 1px 4px rgba(0,0,0,0.04);
  --shadow-hover: 0 4px 12px rgba(0,0,0,0.08);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: var(--bg);
  color: var(--text);
  min-height: 100vh;
  display: flex;
}

.main-area { margin-left: var(--sidebar-width); flex: 1; display: flex; flex-direction: column; min-height: 100vh; }

.topbar { height: 52px; background: var(--card-bg); border-bottom: 1px solid var(--border); display: flex; align-items: center; justify-content: space-between; padding: 0 28px; position: sticky; top: 0; z-index: 50; }
.page-title { font-size: 16px; font-weight: 600; color: var(--text); margin: 0; }
.topbar-right { display: flex; align-items: center; gap: 14px; }
.user-dropdown { position: relative; display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 4px 8px; border-radius: 6px; transition: background 0.2s; }
.user-dropdown:hover { background: rgba(0,0,0,0.04); }
.avatar-wrap { width: 32px; height: 32px; border-radius: 50%; background: #1890ff; display: flex; align-items: center; justify-content: center; }
.avatar-wrap span { color: #fff; font-size: 13px; font-weight: 600; }
.dropdown-menu { position: absolute; top: calc(100% + 6px); right: 0; background: var(--card-bg); border-radius: 6px; border: 1px solid #e8e8e8; box-shadow: 0 4px 12px rgba(0,0,0,0.10); min-width: 140px; overflow: hidden; z-index: 200; }
.dropdown-header { padding: 8px 14px; font-size: 12px; color: var(--text-muted); border-bottom: 1px solid #f0f0f0; }
.dropdown-item { display: block; width: 100%; padding: 8px 14px; border: none; background: transparent; text-align: left; font-size: 13px; color: var(--text-secondary); cursor: pointer; transition: all 0.15s; }
.dropdown-item:hover { background: rgba(255,77,79,0.06); color: #ff4d4f; }

.main-content { padding: 24px 28px; flex: 1; }

.dash-carousel { position: relative; overflow: hidden; border-radius: 8px; margin-bottom: 20px; height: 150px; }
.dash-carousel-track { display: flex; height: 100%; transition: transform 0.5s ease; }
.dash-slide { min-width: 100%; height: 100%; display: flex; align-items: center; padding: 0 32px; }
.dash-slide-content { color: #fff; }
.dash-slide-content h3 { font-size: 18px; font-weight: 700; margin: 0 0 6px; }
.dash-slide-content p { font-size: 14px; margin: 0; opacity: 0.80; }
.dash-dots { position: absolute; bottom: 12px; left: 32px; display: flex; gap: 6px; }
.dash-dot { width: 6px; height: 6px; border-radius: 50%; background: rgba(255,255,255,0.30); cursor: pointer; transition: all 0.3s; }
.dash-dot.active { background: #fff; width: 20px; border-radius: 3px; }

.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 20px; }
.stat-card { background: var(--card-bg); border-radius: var(--radius); padding: 18px; display: flex; align-items: center; gap: 14px; border: 1px solid var(--border); transition: all 0.2s; }
.stat-card:hover { box-shadow: var(--shadow-hover); }
.stat-icon { width: 42px; height: 42px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-icon.blue { background: rgba(24,144,255,0.10); color: #1890ff; }
.stat-icon.green { background: rgba(82,196,26,0.10); color: #52c41a; }
.stat-icon.orange { background: rgba(250,173,20,0.10); color: #faad14; }
.stat-icon.purple { background: rgba(114,46,209,0.10); color: #722ed1; }
.stat-info { display: flex; flex-direction: column; }
.stat-val { font-size: 22px; font-weight: 700; color: var(--text); line-height: 1.2; }
.stat-lbl { font-size: 12px; color: var(--text-muted); margin-top: 2px; }

.card-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 18px; }
.dash-card { --accent: #1890ff; background: var(--card-bg); border-radius: var(--radius); padding: 24px 22px; border: 1px solid var(--border); text-decoration: none; transition: all 0.25s; display: flex; flex-direction: column; }
.dash-card:hover { box-shadow: var(--shadow-hover); transform: translateY(-3px); border-color: var(--accent); }
.card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.card-ico { width: 44px; height: 44px; border-radius: 10px; background: color-mix(in srgb, var(--accent) 10%, transparent); color: var(--accent); display: flex; align-items: center; justify-content: center; }
.card-ttl { font-size: 15px; font-weight: 600; color: var(--text); }
.card-desc { font-size: 13px; color: var(--text-secondary); line-height: 1.5; margin: 0 0 16px; flex: 1; }
.card-act { font-size: 13px; font-weight: 600; color: var(--accent); }

@media (max-width: 1024px) { .stats-grid { grid-template-columns: repeat(2,1fr); } .card-grid { grid-template-columns: repeat(2,1fr); } }
@media (max-width: 768px) { .main-area { margin-left: 0; } .card-grid { grid-template-columns: 1fr; } }
@media (max-width: 600px) { .stats-grid { grid-template-columns: 1fr; } .main-content { padding: 16px; } }
</style>