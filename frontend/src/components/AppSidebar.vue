<template>
  <aside class="app-sidebar">
    <div class="sidebar-header">
      <div class="logo-wrap">
        <svg width="32" height="32" viewBox="0 0 28 28" fill="none">
          <rect width="28" height="28" rx="6" fill="#1890ff"/>
          <path d="M8 20V12L14 8L20 12V20H16V16H12V20H8Z" fill="white"/>
        </svg>
        <span class="logo-text">智汇人才</span>
      </div>
    </div>

    <div class="user-block" v-if="userInfo">
      <div class="user-avatar">
        <span>{{ userInfo.username?.charAt(0) || 'U' }}</span>
      </div>
      <div class="user-info">
        <span class="user-name">{{ userInfo.username || '用户' }}</span>
        <span class="user-role">{{ roleLabel }}</span>
      </div>
    </div>

    <nav class="sidebar-navs">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        :class="{ active: currentPath === item.path }"
      >
        <span class="nav-ico" v-html="item.icon"></span>
        <span class="nav-lbl">{{ item.label }}</span>
      </router-link>
    </nav>

    <div class="sidebar-footer">
      <button class="logout-btn" @click="handleLogout">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"/></svg>
        <span>退出登录</span>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const props = defineProps({
  userInfo: { type: Object, default: null }
})

const emit = defineEmits(['logout'])
const router = useRouter()
const route = useRoute()

const currentPath = computed(() => route.path)

const roleLabel = computed(() => {
  return props.userInfo?.role === 0 ? '个人用户' : props.userInfo?.role === 1 ? '企业用户' : '用户'
})

const navItems = computed(() => {
  if (props.userInfo?.role === 0) {
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

const handleLogout = () => {
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style scoped>
.app-sidebar {
  --sidebar-bg: #001529;
  --sidebar-hover: rgba(255,255,255,0.06);
  --sidebar-active: rgba(24,144,255,0.15);
  --sidebar-text: rgba(255,255,255,0.65);
  --sidebar-text-active: #ffffff;
  --sidebar-width: 220px;
  position: fixed; top: 0; left: 0;
  width: var(--sidebar-width); height: 100vh;
  background: var(--sidebar-bg);
  display: flex; flex-direction: column; z-index: 100;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.sidebar-header { padding: 18px 18px 14px; border-bottom: 1px solid rgba(255,255,255,0.08); }
.logo-wrap { display: flex; align-items: center; gap: 10px; }
.logo-text { font-size: 16px; font-weight: 700; color: #fff; }

.user-block { display: flex; align-items: center; gap: 12px; padding: 18px 18px 14px; border-bottom: 1px solid rgba(255,255,255,0.08); }
.user-avatar { width: 38px; height: 38px; border-radius: 50%; background: #1890ff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.user-avatar span { color: #fff; font-size: 15px; font-weight: 600; }
.user-info { display: flex; flex-direction: column; min-width: 0; }
.user-name { font-size: 14px; font-weight: 600; color: #fff; }
.user-role { font-size: 11px; color: var(--sidebar-text); }

.sidebar-navs { flex: 1; padding: 12px 10px; display: flex; flex-direction: column; gap: 2px; overflow-y: auto; }
.nav-item {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 12px; border-radius: 6px;
  color: var(--sidebar-text); text-decoration: none;
  font-size: 14px; font-weight: 500; transition: all 0.15s;
}
.nav-item:hover { background: var(--sidebar-hover); color: var(--sidebar-text-active); }
.nav-item.active { background: var(--sidebar-active); color: var(--sidebar-text-active); font-weight: 600; }
.nav-ico { display: flex; align-items: center; width: 18px; justify-content: center; }

.sidebar-footer { padding: 12px 10px 18px; border-top: 1px solid rgba(255,255,255,0.08); }
.logout-btn {
  display: flex; align-items: center; gap: 8px;
  padding: 9px 12px; border: none; border-radius: 6px;
  background: transparent; color: var(--sidebar-text);
  font-size: 13px; cursor: pointer; transition: all 0.15s; width: 100%;
}
.logout-btn:hover { background: rgba(255,77,79,0.15); color: #ff4d4f; }
</style>