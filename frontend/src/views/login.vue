<template>
  <div class="login-container">
    <div class="login-box">
      <h2>智汇人才匹配系统</h2>
      
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
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 0;
  padding: 0;
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  width: 400px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.role-tabs {
  display: flex;
  margin-bottom: 24px;
  background: #f5f5f5;
  border-radius: 8px;
  padding: 4px;
}

.tab {
  flex: 1;
  padding: 10px;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.tab.active {
  background: #409eff;
  color: white;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #409eff;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
}

.login-btn:hover {
  background: #3088dd;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #666;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
}
</style>
