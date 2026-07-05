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
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 0;
  padding: 0;
}

.register-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  width: 450px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
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

.role-radio {
  display: flex;
  gap: 20px;
}

.role-radio label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.register-btn {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
}

.register-btn:hover {
  background: #3088dd;
}

.login-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}
</style>
