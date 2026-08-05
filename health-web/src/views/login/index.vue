<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <i class="el-icon-medical login-icon"></i>
        <h1>智慧健康管理系统</h1>
        <p>Smart Health Management Platform</p>
      </div>
      <el-form ref="loginForm" :model="loginForm" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="el-icon-user" size="large"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="el-icon-lock" size="large" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" size="large" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-tip">
        <p>测试账号: admin / 123456 &nbsp;|&nbsp; doctor / 123456 &nbsp;|&nbsp; user / 123456</p>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: { username: 'admin', password: '123456' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      this.$refs.loginForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await login(this.loginForm)
          if (res.code === 200) {
            const { token, user } = res.data
            this.$store.commit('SET_TOKEN', token)
            this.$store.commit('SET_USER', user)
            this.$store.commit('SET_ROLES', user.roles || [])
            this.$message.success('登录成功')
            this.$router.push('/')
          } else {
            this.$message.error(res.message || '登录失败')
          }
        } catch (e) {
          console.error(e)
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  
  .login-icon {
    font-size: 56px;
    color: #409EFF;
  }
  
  h1 {
    margin: 10px 0 5px;
    font-size: 24px;
    color: #303133;
  }
  
  p {
    margin: 0;
    color: #909399;
    font-size: 13px;
  }
}

.login-form {
  .login-btn {
    width: 100%;
  }
}

.login-tip {
  text-align: center;
  margin-top: 20px;
  
  p {
    color: #909399;
    font-size: 12px;
  }
}
</style>