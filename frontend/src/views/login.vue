<template>
  <div class="login-container">
    <div class="login-card">
      <div class="brand-section">
        <div class="logo-wrapper">
          <span class="logo-icon">👤</span>
        </div>
        <h1>智汇人才匹配系统</h1>
        <p class="subtitle">省级人才智慧匹配平台</p>
      </div>

      <div class="role-tabs">
        <button 
          :class="['tab', role === 0 ? 'active' : '']"
          @click="role = 0"
        >
          个人用户
        </button>
        <button 
          :class="['tab', role === 1 ? 'active' : '']"
          @click="role = 1"
        >
          企业用户
        </button>
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
        <button class="login-btn" @click="handleLogin">登录</button>
      </div>

      <p class="register-link">
        还没有账号？<a href="/register">去注册</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const role = ref(0)

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
      console.error('响应状态:', error.response.status)
      console.error('响应数据:', error.response.data)
      alert(error.response.data?.msg || '登录失败')
    } else if (error.request) {
      console.error('请求已发送但无响应:', error.request)
      alert('网络请求失败，请检查后端服务是否启动')
    } else {
      console.error('请求配置错误:', error.message)
      alert('请求配置错误: ' + error.message)
    }
  }
}
</script>

<style scoped>
.login-container {
  --primary-color: #1677ff;
  --primary-hover: #0958d9;
  --bg-color: #f5f7fa;
  --card-bg: #ffffff;
  --text-primary: #333333;
  --text-secondary: #6b7280;
  --border-color: #d9d9d9;
  --border-radius-sm: 8px;
  --border-radius-md: 16px;

  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--bg-color);
  margin: 0;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.login-card {
  background-color: var(--card-bg);
  padding: 56px 44px;
  border-radius: var(--border-radius-md);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  width: 420px;
  max-width: 90vw;
}

.brand-section {
  text-align: center;
  margin-bottom: 36px;
}

.logo-wrapper {
  width: 72px;
  height: 72px;
  background-color: rgba(22, 119, 255, 0.1);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.logo-icon {
  font-size: 36px;
}

h1 {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 10px 0;
}

.subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.role-tabs {
  display: flex;
  margin-bottom: 32px;
  background-color: #f5f5f5;
  border-radius: var(--border-radius-sm);
  padding: 4px;
}

.tab {
  flex: 1;
  padding: 12px;
  border: none;
  background-color: transparent;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: var(--text-secondary);
}

.tab.active {
  background-color: var(--primary-color);
  color: #ffffff;
}

.tab:hover:not(.active) {
  background-color: rgba(22, 119, 255, 0.08);
}

.form-group {
  margin-bottom: 22px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.form-group input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: 15px;
  color: var(--text-primary);
  box-sizing: border-box;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.2);
}

.form-group input::placeholder {
  color: #9ca3af;
}

.login-btn {
  width: 100%;
  padding: 15px;
  background-color: var(--primary-color);
  color: #ffffff;
  border: none;
  border-radius: var(--border-radius-sm);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.login-btn:hover {
  background-color: var(--primary-hover);
}

.register-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-secondary);
}

.register-link a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-card {
    padding: 40px 28px;
    width: 100%;
  }

  h1 {
    font-size: 22px;
  }

  .logo-wrapper {
    width: 60px;
    height: 60px;
  }

  .logo-icon {
    font-size: 28px;
  }
}
</style>
