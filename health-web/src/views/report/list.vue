<template>
  <div class="page-container">
    <el-card>
      <div slot="header" class="flex-between">
        <span>体检报告列表</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd" v-if="canEdit">新增报告</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="reportTitle" label="报告标题"></el-table-column>
        <el-table-column prop="reportNo" label="报告编号" width="160"></el-table-column>
        <el-table-column prop="examDate" label="体检日期" width="120"></el-table-column>
        <el-table-column prop="hospital" label="体检机构"></el-table-column>
        <el-table-column prop="abnormalCount" label="异常项数" width="100">
          <template slot-scope="{ row }">
            <el-tag v-if="row.abnormalCount > 0" type="danger" size="mini">{{ row.abnormalCount }}</el-tag>
            <span v-else style="color:#67C23A">{{ row.abnormalCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="总项数" width="80">
          <template slot-scope="{ row }">{{ row.totalCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleDetail(row)">详情</el-button>
            <el-button type="text" size="small" @click="handleEdit(row)" v-if="canEdit">编辑</el-button>
            <el-button type="text" size="small" style="color:#f56c6c" @click="handleDelete(row)" v-if="canDelete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px" size="small">
        <el-form-item label="报告标题">
          <el-input v-model="form.reportTitle"></el-input>
        </el-form-item>
        <el-form-item label="报告编号">
          <el-input v-model="form.reportNo"></el-input>
        </el-form-item>
        <el-form-item label="体检日期">
          <el-date-picker v-model="form.examDate" type="date" value-format="yyyy-MM-dd" style="width:100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="体检机构">
          <el-input v-model="form.hospital"></el-input>
        </el-form-item>
        <el-form-item label="体检医生">
          <el-input v-model="form.doctor"></el-input>
        </el-form-item>
        <el-form-item label="体检摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2"></el-input>
        </el-form-item>
        <el-form-item label="医生建议">
          <el-input v-model="form.suggestion" type="textarea" :rows="2"></el-input>
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
import { listReports, createReport, updateReport, deleteReport } from '@/api'

export default {
  name: 'ReportList',
  data() {
    return {
      tableData: [], loading: false,
      dialogVisible: false, dialogTitle: '', form: {}
    }
  },
  computed: {
    canEdit() {
      const roles = this.$store.getters.roles || []
      return roles.some(r => r === 'ROLE_ADMIN' || r === 'ROLE_DOCTOR')
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
        const res = await listReports()
        if (res.code === 200) this.tableData = res.data
      } catch (e) {} finally { this.loading = false }
    },
    handleAdd() {
      this.form = { reportNo: 'REP' + Date.now(), examDate: new Date().toISOString().slice(0, 10) }
      this.dialogTitle = '新增报告'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑报告'
      this.dialogVisible = true
    },
    handleDetail(row) {
      this.$router.push('/report/detail/' + row.id)
    },
    async handleSubmit() {
      try {
        const payload = { report: this.form, items: [] }
        if (this.form.id) await updateReport(payload)
        else await createReport(payload)
        this.$message.success('操作成功')
        this.dialogVisible = false
        this.loadData()
      } catch (e) {}
    },
    handleDelete(row) {
      this.$confirm('确定删除该报告?', '提示', { type: 'warning' })
        .then(async () => {
          try { await deleteReport(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {}
        }).catch(() => {})
    }
  }
}
</script>