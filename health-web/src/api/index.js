import request from '@/utils/request'

export function login(data) {
  return request({ url: '/auth/login', method: 'post', data })
}

export function logout() {
  return request({ url: '/auth/logout', method: 'post' })
}

export function getUserInfo() {
  return request({ url: '/auth/userInfo', method: 'get' })
}

export function listUsers(params) {
  return request({ url: '/user/list', method: 'get', params })
}

export function pageUsers(params) {
  return request({ url: '/user/page', method: 'get', params })
}

export function createUser(data) {
  return request({ url: '/user', method: 'post', data })
}

export function updateUser(data) {
  return request({ url: '/user', method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: '/user/' + id, method: 'delete' })
}

export function listCategories() {
  return request({ url: '/user/category/list', method: 'get' })
}

export function createCategory(data) {
  return request({ url: '/user/category', method: 'post', data })
}

export function updateCategory(data) {
  return request({ url: '/user/category', method: 'put', data })
}

export function deleteCategory(id) {
  return request({ url: '/user/category/' + id, method: 'delete' })
}

export function listRecords(params) {
  return request({ url: '/record/list', method: 'get', params })
}

export function pageRecords(params) {
  return request({ url: '/record/page', method: 'get', params })
}

export function createRecord(data) {
  return request({ url: '/record', method: 'post', data })
}

export function updateRecord(data) {
  return request({ url: '/record', method: 'put', data })
}

export function deleteRecord(id) {
  return request({ url: '/record/' + id, method: 'delete' })
}

export function listReports() {
  return request({ url: '/record/report/list', method: 'get' })
}

export function getReportDetail(id) {
  return request({ url: '/record/report/' + id, method: 'get' })
}

export function createReport(data) {
  return request({ url: '/record/report', method: 'post', data })
}

export function updateReport(data) {
  return request({ url: '/record/report', method: 'put', data })
}

export function deleteReport(id) {
  return request({ url: '/record/report/' + id, method: 'delete' })
}

export function listExamItems(params) {
  return request({ url: '/record/exam-item/list', method: 'get', params })
}

export function getCommonExamItems() {
  return request({ url: '/record/exam-item/common', method: 'get' })
}

export function getWarningCount() {
  return request({ url: '/warning/count', method: 'get' })
}

export function listWarnings(params) {
  return request({ url: '/warning/list', method: 'get', params })
}

export function handleWarning(id, data) {
  return request({ url: '/warning/handle/' + id, method: 'put', data })
}

export function deleteWarning(id) {
  return request({ url: '/warning/' + id, method: 'delete' })
}

export function getHotNews(params) {
  return request({ url: '/warning/news/hot', method: 'get', params })
}

export function listNews(params) {
  return request({ url: '/warning/news/list', method: 'get', params })
}

export function getNewsDetail(id) {
  return request({ url: '/warning/news/' + id, method: 'get' })
}

export function createNews(data) {
  return request({ url: '/warning/news', method: 'post', data })
}

export function updateNews(data) {
  return request({ url: '/warning/news', method: 'put', data })
}

export function deleteNews(id) {
  return request({ url: '/warning/news/' + id, method: 'delete' })
}

export function submitHealthData(data) {
  return request({ url: '/netty/data', method: 'post', data })
}

export function batchSubmitData(data) {
  return request({ url: '/netty/batch', method: 'post', data })
}

export function getNettyStats() {
  return request({ url: '/netty/stats', method: 'get' })
}

export function getProfile() {
  return request({ url: '/user/profile', method: 'get' })
}

export function updateProfile(data) {
  return request({ url: '/user/profile', method: 'put', data })
}

export function changePassword(data) {
  return request({ url: '/user/password', method: 'put', data })
}