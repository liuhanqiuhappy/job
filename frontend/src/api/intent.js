import { service } from './user'

export const sendIntent = (data) => {
  return service.post('/intent/send', data)
}

export const acceptIntent = (intentId) => {
  return service.post(`/intent/accept/${intentId}`)
}

export const rejectIntent = (intentId) => {
  return service.post(`/intent/reject/${intentId}`)
}

export const getMySentIntents = () => {
  return service.get('/intent/my-sent')
}

export const getMyReceivedIntents = () => {
  return service.get('/intent/my-received')
}

export const getContactInfo = (intentId) => {
  return service.get(`/intent/contact/${intentId}`)
}