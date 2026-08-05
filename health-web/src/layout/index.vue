<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-area">
        <i class="el-icon-medical logo-icon"></i>
        <span v-if="!isCollapse" class="logo-text">健康管理平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#001529"
        text-color="#ffffffb3"
        active-text-color="#409EFF">
        <template v-for="route in menuRoutes">
          <el-menu-item :key="route.path" :index="'/' + route.path" v-if="!route.meta.hidden">
            <i :class="route.meta.icon"></i>
            <span slot="title">{{ route.meta.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <i
            :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
            class="collapse-btn"
            @click="toggleCollapse">
          </i>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="userAvatar"></el-avatar>
              <span class="username">{{ user?.realName || user?.username || '用户' }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'
import { getUserInfo, logout } from '@/api'

export default {
  name: 'Layout',
  data() {
    return {
      isCollapse: false,
      user: null,
      activeMenu: '/dashboard'
    }
  },
  computed: {
    ...mapGetters(['user', 'roles']),
    menuRoutes() {
      const routes = this.$router.options.routes.find(r => r.path === '/')
      return routes ? routes.children.filter(c => !c.meta.hidden) : []
    },
    userAvatar() {
      return this.user && this.user.avatar ? '' : 'el-icon-user-solid'
    }
  },
  created() {
    this.loadUserInfo()
    this.activeMenu = this.$route.path
  },
  methods: {
    async loadUserInfo() {
      try {
        const res = await getUserInfo()
        if (res.code === 200 && res.data) {
          this.user = res.data
          this.$store.commit('SET_USER', res.data)
        }
      } catch (e) {}
    },
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    },
    async handleCommand(command) {
      if (command === 'logout') {
        try {
          await logout()
        } catch (e) {}
        this.$store.dispatch('logout')
        this.$router.push('/login')
      } else if (command === 'profile') {
        this.$message.info('个人中心功能开发中')
      }
    }
  },
  watch: {
    '$route'(to) {
      this.activeMenu = to.path
    }
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

.layout-aside {
  background: #001529;
  transition: width 0.3s;
  overflow: hidden;
  
  .logo-area {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 16px;
    font-weight: bold;
    
    .logo-icon {
      font-size: 28px;
      color: #409EFF;
      margin-right: 8px;
    }
    
    .logo-text {
      white-space: nowrap;
    }
  }
  
  .el-menu {
    border-right: none;
  }
}

.layout-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  
  .header-left {
    display: flex;
    align-items: center;
    
    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      margin-right: 20px;
      color: #606266;
      
      &:hover {
        color: #409EFF;
      }
    }
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      
      .username {
        margin: 0 8px;
        color: #606266;
      }
    }
  }
}

.layout-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>