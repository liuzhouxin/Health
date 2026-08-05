<template>
  <div class="page-container">
    <el-card>
      <div slot="header" class="flex-between">
        <span>健康记录</span>
        <div>
          <el-button size="small" @click="$refs.formDialog.open()" v-if="canEdit">新增记录</el-button>
        </div>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="标题" width="180"></el-table-column>
        <el-table-column prop="recordType" label="类型" width="120">
          <template slot-scope="{ row }">
            <el-tag size="mini">{{ getTypeName(row.recordType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recordValue" label="记录值">
          <template slot-scope="{ row }">{{ formatValue(row.recordValue) }}</template>
        </el-table-column>
        <el-table-column prop="recordDate" label="记录日期" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 0 ? 'danger' : 'success'" size="mini">
              {{ row.status === 0 ? '异常' : '正常' }}
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
      <el-pagination class="mt-20"
        @current-change="handleCurrentChange"
        :current-page="pageNo"
        :page-size="pageSize"
        layout="total, prev, pager, next"
        :total="total">
      </el-pagination>
    </el-card>

    <record-form-dialog ref="formDialog" @success="loadData"></record-form-dialog>
  </div>
</template>

<script>
import { pageRecords, deleteRecord } from '@/api'
import RecordFormDialog from './form-dialog.vue'

export default {
  name: 'RecordList',
  components: { RecordFormDialog },
  data() {
    return {
      tableData: [], loading: false, pageNo: 1, pageSize: 10, total: 0
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
        const res = await pageRecords({ pageNo: this.pageNo, pageSize: this.pageSize })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (e) {} finally { this.loading = false }
    },
    handleCurrentChange(page) { this.pageNo = page; this.loadData() },
    getTypeName(type) {
      const m = { blood_pressure: '血压', blood_sugar: '血糖', heart_rate: '心率', temperature: '体温', spo2: '血氧' }
      return m[type] || type
    },
    formatValue(val) {
      try {
        const obj = typeof val === 'string' ? JSON.parse(val) : val
        return JSON.stringify(obj)
      } catch { return val }
    },
    handleEdit(row) { this.$refs.formDialog.open(row) },
    handleDelete(row) {
      this.$confirm('确定删除该记录?', '提示', { type: 'warning' })
        .then(async () => {
          try { await deleteRecord(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {}
        }).catch(() => {})
    }
  }
}
</script>