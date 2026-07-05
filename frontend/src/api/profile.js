import { service } from './user'

export const getResumeProfile = () => {
  return service.get('/profile/resume')
}

export const getJobProfile = () => {
  return service.get('/profile/job')
}