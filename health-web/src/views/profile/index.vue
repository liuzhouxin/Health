<template>
  <div class="page-container">
    <el-card>
      <div slot="header">
        <span>个人中心</span>
      </div>

      <el-tabs v-model="activeTab">
        <!-- 基本资料 -->
        <el-tab-pane label="基本资料" name="info">
          <el-form :model="profileForm" label-width="100px" size="medium" style="max-width:600px" v-loading="loading">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="角色">
              <el-tag v-for="r in (profileForm.roleCodes || [])" :key="r" class="role-tag">{{ roleLabel(r) }}</el-tag>
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="profileForm.realName"></el-input>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="profileForm.phone"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email"></el-input>
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="profileForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="年龄">
              <el-input-number v-model="profileForm.age" :min="0" :max="150"></el-input-number>
            </el-form-item>
            <el-form-item label="身高(cm)">
              <el-input-number v-model="profileForm.height" :min="0" :max="250" :precision="1"></el-input-number>
            </el-form-item>
            <el-form-item label="体重(kg)">
              <el-input-number v-model="profileForm.weight" :min="0" :max="300" :precision="1"></el-input-number>
            </el-form-item>
            <el-form-item label="头像URL">
              <el-input v-model="profileForm.avatar" placeholder="可粘贴图片地址"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveProfile">保存修改</el-button>
              <el-button @click="loadProfile">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdForm" label-width="100px" size="medium" style="max-width:600px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="changing" @click="handleChangePassword">提交修改</el-button>
              <el-button @click="resetPwdForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { getProfile, updateProfile, changePassword } from '@/api'

export default {
  name: 'Profile',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.pwdForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      activeTab: 'info',
      loading: false,
      saving: false,
      changing: false,
      profileForm: {},
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirm, trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadProfile()
  },
  methods: {
    async loadProfile() {
      this.loading = true
      try {
        const res = await getProfile()
        if (res.code === 200 && res.data) {
          this.profileForm = { ...res.data }
        }
      } catch (e) {} finally {
        this.loading = false
      }
    },
    async handleSaveProfile() {
      this.saving = true
      try {
        const res = await updateProfile(this.profileForm)
        if (res.code === 200) {
          this.$message.success('资料更新成功')
          this.profileForm = { ...res.data }
          // 同步更新布局里的用户信息
          if (res.data) {
            this.$store.commit('SET_USER', { ...this.$store.getters.user, ...res.data })
          }
        }
      } catch (e) {} finally {
        this.saving = false
      }
    },
    handleChangePassword() {
      this.$refs.pwdForm.validate(async valid => {
        if (!valid) return
        this.changing = true
        try {
          const res = await changePassword({
            oldPassword: this.pwdForm.oldPassword,
            newPassword: this.pwdForm.newPassword
          })
          if (res.code === 200) {
            this.$message.success('密码修改成功,请重新登录')
            this.resetPwdForm()
            setTimeout(() => {
              this.$store.dispatch('logout').then(() => this.$router.push('/login'))
            }, 1500)
          }
        } catch (e) {} finally {
          this.changing = false
        }
      })
    },
    resetPwdForm() {
      this.pwdForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
      this.$refs.pwdForm && this.$refs.pwdForm.clearValidate()
    },
    roleLabel(code) {
      const map = { ROLE_ADMIN: '系统管理员', ROLE_DOCTOR: '医生', ROLE_USER: '普通用户' }
      return map[code] || code
    }
  }
}
</script>

<style scoped>
.role-tag {
  margin-right: 8px;
}
</style>
