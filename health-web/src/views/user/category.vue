<template>
  <div class="page-container">
    <el-card>
      <div slot="header" class="flex-between">
        <span>健康档案分类管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="canEdit">新增分类</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="categoryName" label="分类名称"></el-table-column>
        <el-table-column prop="categoryCode" label="分类编码" width="150"></el-table-column>
        <el-table-column prop="icon" label="图标" width="80">
          <template slot-scope="{ row }"><i :class="row.icon"></i></template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="mini">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleEdit(row)" v-if="canEdit">编辑</el-button>
            <el-button type="text" size="small" style="color:#f56c6c" @click="handleDelete(row)" v-if="canDelete">删除</el-button>
            <span v-if="!canEdit && !canDelete" style="color:#909399">仅查看</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="450px">
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="分类名称">
          <el-input v-model="form.categoryName"></el-input>
        </el-form-item>
        <el-form-item label="分类编码">
          <el-input v-model="form.categoryCode"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="如:el-icon-folder"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2"></el-input>
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
import { listCategories, createCategory, updateCategory, deleteCategory } from '@/api'

export default {
  name: 'CategoryList',
  data() {
    return {
      tableData: [],
      loading: false,
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
  mounted() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await listCategories()
        if (res.code === 200) this.tableData = res.data
      } catch (e) {} finally { this.loading = false }
    },
    handleAdd() {
      this.form = { sortOrder: 0, status: 1 }
      this.dialogTitle = '新增分类'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑分类'
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        if (this.form.id) await updateCategory(this.form)
        else await createCategory(this.form)
        this.$message.success('操作成功')
        this.dialogVisible = false
        this.loadData()
      } catch (e) {}
    },
    handleDelete(row) {
      this.$confirm('确定删除该分类?', '提示', { type: 'warning' })
        .then(async () => {
          try { await deleteCategory(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {}
        }).catch(() => {})
    }
  }
}
</script>