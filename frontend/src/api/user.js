import axios from 'axios'
import router from '../router'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000,
  withCredentials: true
})

service.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response && error.response.data) {
      const { code, msg } = error.response.data
      if (code !== 0 && msg === '未登录') {
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

export function login(data) {
  return service.post('/login', data)
}

export function register(data) {
  return service.post('/register', data)
}

export function upload(file, type) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', type)
  return service.post('/upload', formData, {
    timeout: 30000,
    withCredentials: true
  })
}

export { service }
