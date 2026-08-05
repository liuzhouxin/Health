import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '@/store'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

service.interceptors.request.use(
  config => {
    if (store.getters.token) {
      config.headers['Authorization'] = 'Bearer ' + store.getters.token
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      Message({ message: res.message || '错误', type: 'error', duration: 3000 })
      if (res.code === 401 || res.code === 4011 || res.code === 4012) {
        MessageBox.confirm('登录已过期,请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('logout').then(() => {
            router.push('/login')
          })
        })
      }
      return Promise.reject(new Error(res.message || '错误'))
    }
    return res
  },
  error => {
    Message({ message: error.message || '网络错误', type: 'error', duration: 3000 })
    if (error.response && error.response.status === 401) {
      store.dispatch('logout').then(() => router.push('/login'))
    }
    return Promise.reject(error)
  }
)

export default service