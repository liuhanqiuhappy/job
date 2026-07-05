import axios from 'axios'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000,
  withCredentials: true
})

export function parseResume(id) {
  return service.post('/parse/resume/' + id)
}

export function parseJob(id) {
  return service.post('/parse/job/' + id)
}