<template>
  <div class="register-container">
    <div class="register-box">
      <h2>用户注册</h2>

      <div class="form-group">
        <label>角色</label>
        <div class="role-radio">
          <label>
            <input type="radio" v-model="form.role" :value="0">
            个人用户
          </label>
          <label>
            <input type="radio" v-model="form.role" :value="1">
            企业用户
          </label>
        </div>
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
        <label>手机号</label>
        <input type="text" v-model="form.phone" placeholder="请输入手机号">
      </div>
      <div class="form-group">
        <label>城市</label>
        <input type="text" v-model="form.city" placeholder="请输入所在城市">
      </div>
      <div class="form-group">
        <button class="register-btn" @click="handleRegister">注册</button>
      </div>

      <p class="login-link">
        已有账号？<a href="/login">去登录</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  role: 0,
  phone: '',
  city: ''
})

const handleRegister = async () => {
  if (!form.username || !form.password) {
    alert('请输入用户名和密码')
    return
  }

  const requestData = {
    username: form.username,
    password: form.password,
    role: form.role,
    phone: form.phone || '',
    city: form.city || ''
  }

  console.log('注册请求数据:', JSON.stringify(requestData))

  try {
    const response = await axios.post('/api/register', requestData, {
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json'
      }
    })

    console.log('注册响应:', response.data)

    if (response.data.code === 0) {
      alert('注册成功')
      router.push('/login')
    } else {
      alert(response.data.msg || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    if (error.response) {
      console.error('响应状态:', error.response.status)
      console.error('响应数据:', error.response.data)
      alert('注册失败: ' + (error.response.data?.msg || '未知错误'))
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
.register-container {
  --primary: #1677ff;
  --primary-hover: #4096ff;
  --primary-active: #0958d9;
  --bg: #f0f2f5;
  --card-bg: #ffffff;
  --text: #1d1d1f;
  --text-secondary: #6b7280;
  --text-muted: #9ca3af;
  --border: #e5e7eb;
  --radius: 12px;
  --shadow: 0 4px 24px rgba(0,0,0,0.08);
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: var(--bg);
  margin: 0; padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.register-box {
  background: var(--card-bg);
  padding: 40px 40px;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  width: 440px; max-width: 90vw;
}

h2 { text-align: center; font-size: 22px; font-weight: 700; color: var(--text); margin-bottom: 28px; }

.form-group { margin-bottom: 16px; }
.form-group label { display: block; margin-bottom: 6px; font-size: 14px; color: var(--text); font-weight: 500; }
.form-group input {
  width: 100%; padding: 10px 12px;
  border: 1.5px solid var(--border); border-radius: 8px;
  font-size: 14px; color: var(--text); box-sizing: border-box;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: #fafafa;
}
.form-group input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px rgba(22,119,255,0.12); background: #fff; }
.form-group input::placeholder { color: var(--text-muted); }

.role-radio {
  display: flex; gap: 24px;
}
.role-radio label {
  display: flex; align-items: center; gap: 8px;
  cursor: pointer; font-size: 14px; color: var(--text);
}
.role-radio input { accent-color: var(--primary); }

.register-btn {
  width: 100%; padding: 11px;
  background: var(--primary); color: #fff;
  border: none; border-radius: 8px;
  font-size: 15px; font-weight: 600; cursor: pointer;
  transition: background 0.2s;
}
.register-btn:hover { background: var(--primary-hover); }
.register-btn:active { background: var(--primary-active); }

.login-link { text-align: center; margin-top: 20px; font-size: 14px; color: var(--text-muted); }
.login-link a { color: var(--primary); text-decoration: none; font-weight: 500; }
.login-link a:hover { text-decoration: underline; }

@media (max-width: 480px) {
  .register-box { padding: 32px 20px; width: 100%; }
}
</style>
