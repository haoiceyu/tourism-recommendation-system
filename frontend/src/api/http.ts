import axios from 'axios'

const TOKEN_KEY = 'tourism_token'

const http = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (r) => r,
  (err) => {
    const body = err.response?.data as { message?: string } | undefined
    const msg = body?.message ?? err.message ?? '请求失败'
    return Promise.reject(new Error(msg))
  },
)

export default http
