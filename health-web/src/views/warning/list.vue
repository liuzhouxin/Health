<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="count-card">
          <div class="count-icon unhandled"><i class="el-icon-warning-outline"></i></div>
          <div class="count-info">
            <div class="count-value">{{ warningData.unhandledCount || 0 }}</div>
            <div class="count-label">待处理预警</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="count-card">
          <div class="count-icon critical"><i class="el-icon-error"></i></div>
          <div class="count-info">
            <div class="count-value">{{ warningData.criticalCount || 0 }}</div>
            <div class="count-label">严重预警</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="count-card">
          <div class="count-icon high"><i class="el-icon-warning"></i></div>
          <div class="count-info">
            <div class="count-value">{{ warningData.highCount || 0 }}</div>
            <div class="count-label">高级预警</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="count-card">
          <div class="count-icon total"><i class="el-icon-data-analysis"></i></div>
          <div class="count-info">
            <div class="count-value">{{ warningData.total || 0 }}</div>
            <div class="count-label">预警总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-20">
      <div slot="header" class="flex-between">
        <span>预警列表</span>
        <el-form :inline="true" size="small">
          <el-form-item>
            <el-select v-model="searchForm.isHandled" placeholder="状态" clearable>
              <el-option label="待处理" :value="0"></el-option>
              <el-option label="已处理" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="searchForm.warningLevel" placeholder="等级" clearable>
              <el-option label="严重" value="critical"></el-option>
              <el-option label="高" value="high"></el-option>
              <el-option label="中" value="medium"></el-option>
              <el-option label="低" value="low"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="loadData">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="warningType" label="类型" width="120">
          <template slot-scope="{ row }">
            <el-tag size="mini">{{ getTypeName(row.warningType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningContent" label="预警内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="warningLevel" label="等级" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="getLevelColor(row.warningLevel)" size="mini">
              {{ getLevelText(row.warningLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isHandled" label="状态" width="80">
          <template slot-scope="{ row }">
            <el-tag :type="row.isHandled ? 'success' : 'danger'" size="mini">
              {{ row.isHandled ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="160">
          <template slot-scope="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleProcess(row)" v-if="!row.isHandled">处理</el-button>
            <el-button type="text" size="small" style="color:#f56c6c" @click="handleDelete(row)" v-if="canDelete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-20"
        @current-change="handlePageChange"
        :current-page="pageNo" :page-size="10"
        layout="total, prev, pager, next" :total="total">
      </el-pagination>
    </el-card>

    <el-dialog title="处理预警" :visible.sync="handleDialogVisible" width="500px">
      <el-form :model="handleForm" label-width="100px" size="small">
        <el-form-item label="预警内容">
          <el-input v-model="handleForm.warningContent" type="textarea" :rows="3" disabled></el-input>
        </el-form-item>
        <el-form-item label="处理方式">
          <el-select v-model="handleForm.handleMethod" style="width:100%">
            <el-option label="已就医" value="medical"></el-option>
            <el-option label="已服药" value="medication"></el-option>
            <el-option label="已观察" value="observation"></el-option>
            <el-option label="无需处理" value="ignore"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="handleForm.handleRemark" type="textarea" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getWarningCount, listWarnings, handleWarning, deleteWarning } from '@/api'

export default {
  name: 'WarningList',
  data() {
    return {
      warningData: {},
      searchForm: { isHandled: '', warningLevel: '' },
      tableData: [], loading: false, pageNo: 1, total: 0,
      handleDialogVisible: false, handleForm: {}, currentRow: null
    }
  },
  computed: {
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
        const [countRes, listRes] = await Promise.all([
          getWarningCount(),
          listWarnings({
            isHandled: this.searchForm.isHandled === '' ? null : this.searchForm.isHandled,
            warningLevel: this.searchForm.warningLevel,
            pageNo: this.pageNo
          })
        ])
        if (countRes.code === 200) this.warningData = countRes.data
        if (listRes.code === 200) {
          this.tableData = listRes.data.records || listRes.data || []
          this.total = listRes.data.total || this.tableData.length
        }
      } catch (e) {} finally { this.loading = false }
    },
    handlePageChange(page) { this.pageNo = page; this.loadData() },
    getTypeName(type) {
      const m = { blood_pressure: '血压', blood_sugar: '血糖', heart_rate: '心率', temperature: '体温', spo2: '血氧' }
      return m[type] || type
    },
    getLevelColor(level) {
      return { critical: 'danger', high: 'danger', medium: 'warning', low: 'info' }[level] || 'info'
    },
    getLevelText(level) {
      return { critical: '严重', high: '高', medium: '中', low: '低' }[level] || level
    },
    formatTime(t) {
      if (!t) return '-'
      const d = new Date(t)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    },
    handleProcess(row) {
      this.currentRow = row
      this.handleForm = { warningContent: row.warningContent, handleMethod: 'medical', handleRemark: '' }
      this.handleDialogVisible = true
    },
    async submitHandle() {
      try {
        await handleWarning(this.currentRow.id, this.handleForm)
        this.$message.success('处理成功')
        this.handleDialogVisible = false
        this.loadData()
      } catch (e) {}
    },
    handleDelete(row) {
      this.$confirm('确定删除该预警?', '提示', { type: 'warning' })
        .then(async () => {
          try { await deleteWarning(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {}
        }).catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.count-card {
  display: flex;
  align-items: center;
  padding: 10px;
  
  .count-icon {
    width: 60px; height: 60px; border-radius: 10px;
    display: flex; align-items: center; justify-content: center;
    font-size: 30px; color: #fff; margin-right: 15px;
    
    &.unhandled { background: linear-gradient(135deg, #faad14, #ff7a45); }
    &.critical { background: linear-gradient(135deg, #f5222d, #ff7875); }
    &.high { background: linear-gradient(135deg, #fa541c, #ff7a45); }
    &.total { background: linear-gradient(135deg, #1890ff, #40a9ff); }
  }
  
  .count-value { font-size: 28px; font-weight: bold; color: #303133; }
  .count-label { color: #909399; font-size: 13px; }
}
</style>