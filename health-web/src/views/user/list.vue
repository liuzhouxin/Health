<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm" size="small">
        <el-form-item label="关键字">
          <el-input v-model="searchForm.keyword" placeholder="用户名/姓名/手机号" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card>
      <div slot="header" class="flex-between">
        <span>用户列表</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="canEdit">新增用户</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="realName" label="姓名" width="100"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="gender" label="性别" width="70">
          <template slot-scope="{ row }">{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="70"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="mini">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleEdit(row)" v-if="canEdit">编辑</el-button>
            <el-button type="text" size="small" style="color:#f56c6c" @click="handleDelete(row)" v-if="canDelete">删除</el-button>
            <span v-if="!canEdit && !canDelete" style="color:#909399">仅查看</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="mt-20"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNo"
        :page-sizes="[10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" @close="handleDialogClose">
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="用户名" v-if="!form.id">
          <el-input v-model="form.username" :disabled="!!form.id"></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!form.id">
          <el-input v-model="form.password" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleCode" placeholder="请选择角色" style="width:100%">
            <el-option label="普通用户" value="ROLE_USER"></el-option>
            <el-option label="医生" value="ROLE_DOCTOR"></el-option>
            <el-option label="系统管理员" value="ROLE_ADMIN"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="150"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { pageUsers, createUser, updateUser, deleteUser } from '@/api'

export default {
  name: 'UserList',
  data() {
    return {
      searchForm: { keyword: '' },
      tableData: [],
      loading: false,
      pageNo: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      form: {}
    }
  },
  computed: {
    canEdit() {
      const roles = this.$store.getters.roles || []
      return roles.includes('ROLE_ADMIN')
    },
    canDelete() {
      const roles = this.$store.getters.roles || []
      return roles.includes('ROLE_ADMIN')
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await pageUsers({
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          keyword: this.searchForm.keyword
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (e) {} finally {
        this.loading = false
      }
    },
    handleSearch() { this.pageNo = 1; this.loadData() },
    handleReset() { this.searchForm.keyword = ''; this.handleSearch() },
    handleSizeChange(size) { this.pageSize = size; this.loadData() },
    handleCurrentChange(page) { this.pageNo = page; this.loadData() },
    handleAdd() {
      this.form = { gender: 1, status: 1, roleCode: 'ROLE_USER' }
      this.dialogTitle = '新增用户'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row, roleCode: (row.roleCodes && row.roleCodes[0]) || '' }
      this.dialogTitle = '编辑用户'
      this.dialogVisible = true
    },
    handleDialogClose() { this.form = {} },
    async handleSubmit() {
      try {
        const data = { ...this.form }
        if (data.roleCode) {
          data.roleCodes = [data.roleCode]
        } else {
          delete data.roleCodes
        }
        delete data.roleCode
        if (data.id) {
          await updateUser(data)
        } else {
          await createUser(data)
        }
        this.$message.success('操作成功')
        this.dialogVisible = false
        this.loadData()
      } catch (e) {}
    },
    handleDelete(row) {
      this.$confirm(`确定删除用户"${row.realName || row.username}"?`, '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteUser(row.id)
          this.$message.success('删除成功')
          this.loadData()
        } catch (e) {}
      }).catch(() => {})
    }
  }
}
</script>