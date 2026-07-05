import axios from 'axios'

export const getResumeDimensions = (resumeId) => {
  return axios.get(`/api/profile/dimensions/resume/${resumeId}`, { withCredentials: true })
}

export const getJobDimensions = (jobId) => {
  return axios.get(`/api/profile/dimensions/job/${jobId}`, { withCredentials: true })
}