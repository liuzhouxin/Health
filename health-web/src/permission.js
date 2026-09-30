import router from './router'
import store from './store'
import NProgress from 'nprogress'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login']

function hasPermission(roles, route) {
  if (route.meta && route.meta.roles && route.meta.roles.length > 0) {
    if (!roles || roles.length === 0) return false
    return route.meta.roles.some(r => roles.includes(r))
  }
  return true
}

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  document.title = to.meta.title ? to.meta.title + ' - 智慧健康管理系统' : '智慧健康管理系统'

  const hasToken = store.getters.token
  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      const roles = store.getters.roles || []
      if (hasPermission(roles, to)) {
        next()
      } else {
        next('/dashboard')
        NProgress.done()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next('/login')
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})