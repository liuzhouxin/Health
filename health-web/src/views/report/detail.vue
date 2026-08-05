<template>
  <div class="page-container" v-loading="loading">
    <el-page-header @back="$router.back()" :content="'返回 - 报告详情'"></el-page-header>
    
    <el-card class="mt-20" v-if="report">
      <div class="report-header">
        <h2>{{ report.reportTitle }}</h2>
        <div class="report-meta">
          <el-tag size="small">编号: {{ report.reportNo }}</el-tag>
          <el-tag size="small" type="success">体检日期: {{ formatDate(report.examDate) }}</el-tag>
          <el-tag size="small" type="info">机构: {{ report.hospital }}</el-tag>
          <el-tag size="small" type="warning" v-if="report.doctor">医生: {{ report.doctor }}</el-tag>
          <el-tag size="small" :type="report.status === 1 ? 'success' : 'info'">
            {{ report.status === 1 ? '正常' : '异常' }}
          </el-tag>
        </div>
      </div>
      
      <el-row :gutter="20" class="mt-20">
        <el-col :span="8">
          <div class="stat-block">
            <div class="stat-num">{{ examItems.length }}</div>
            <div class="stat-label">总项目数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-block abnormal">
            <div class="stat-num">{{ abnormalCount }}</div>
            <div class="stat-label">异常项数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-block normal">
            <div class="stat-num">{{ normalCount }}</div>
            <div class="stat-label">正常项数</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="mt-20" v-if="report && report.summary">
      <div slot="header"><span>体检摘要</span></div>
      <div class="report-text">{{ report.summary }}</div>
    </el-card>

    <el-card class="mt-20" v-if="examItems.length">
      <div slot="header"><span>体检项目详情</span></div>
      <el-table :data="examItems" stripe border>
        <el-table-column prop="itemName" label="项目名称" width="150"></el-table-column>
        <el-table-column prop="itemValue" label="检测结果" width="120">
          <template slot-scope="{ row }">
            <span :class="{ 'abnormal-value': row.isAbnormal === 1 }">{{ row.itemValue }}</span>
            <span v-if="row.isAbnormal === 1" class="abnormal-tag">{{ getAbnormalArrow(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80"></el-table-column>
        <el-table-column prop="referenceRange" label="参考范围"></el-table-column>
        <el-table-column prop="isAbnormal" label="状态" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="row.isAbnormal === 1 ? 'danger' : 'success'" size="mini">
              {{ row.isAbnormal === 1 ? '异常' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="abnormalLevel" label="异常等级" width="100">
          <template slot-scope="{ row }">
            <el-tag v-if="row.abnormalLevel" :type="getLevelColor(row.abnormalLevel)" size="mini">
              {{ getLevelText(row.abnormalLevel) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
      </el-table>
    </el-card>

    <el-card class="mt-20" v-if="abnormalItems.length">
      <div slot="header"><span>异常项目详情分析</span></div>
      <el-collapse v-model="activeCollapse">
        <el-collapse-item v-for="item in abnormalItems" :key="item.id" :title="item.itemName" :name="item.id">
          <div class="abnormal-detail">
            <p><strong>项目值:</strong> {{ item.itemValue }} {{ item.unit }}</p>
            <p><strong>参考范围:</strong> {{ item.referenceRange }}</p>
            <p><strong>异常等级:</strong> {{ getLevelText(item.abnormalLevel) }}</p>
            <p><strong>备注:</strong> {{ item.remark || '暂无' }}</p>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <el-card class="mt-20" v-if="report && report.conclusion">
      <div slot="header"><span>体检结论</span></div>
      <div class="report-text">{{ report.conclusion }}</div>
    </el-card>

    <el-card class="mt-20" v-if="report && report.suggestion">
      <div slot="header"><span>医生建议</span></div>
      <div class="report-text">{{ report.suggestion }}</div>
    </el-card>
  </div>
</template>

<script>
import { getReportDetail } from '@/api'

export default {
  name: 'ReportDetail',
  data() {
    return {
      report: null,
      examItems: [],
      loading: false,
      activeCollapse: []
    }
  },
  computed: {
    abnormalCount() { return this.examItems.filter(i => i.isAbnormal === 1).length },
    normalCount() { return this.examItems.filter(i => i.isAbnormal !== 1).length },
    abnormalItems() { return this.examItems.filter(i => i.isAbnormal === 1) }
  },
  mounted() {
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      this.loading = true
      try {
        const id = this.$route.params.id
        const res = await getReportDetail(id)
        if (res.code === 200) {
          this.report = res.data?.report
          this.examItems = res.data?.items || []
        }
      } catch (e) {} finally { this.loading = false }
    },
    formatDate(d) {
      if (!d) return '-'
      const date = new Date(d)
      return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,'0')}-${String(date.getDate()).padStart(2,'0')}`
    },
    getAbnormalArrow(row) {
      if (row.abnormalLevel === 'high' || row.abnormalLevel === 'severe') return '↑'
      return '↓'
    },
    getLevelColor(level) {
      const map = { mild: 'warning', moderate: 'danger', severe: 'danger', high: 'danger' }
      return map[level] || 'info'
    },
    getLevelText(level) {
      const map = { mild: '轻度', moderate: '中度', severe: '重度', high: '偏高', low: '偏低' }
      return map[level] || level
    }
  }
}
</script>

<style scoped lang="scss">
.report-header {
  h2 { margin: 0 0 10px; color: #303133; }
  .report-meta { display: flex; gap: 8px; flex-wrap: wrap; }
}

.stat-block {
  text-align: center;
  padding: 30px 10px;
  
  .stat-num {
    font-size: 36px;
    font-weight: bold;
    color: #409EFF;
  }
  
  .stat-label {
    color: #909399;
    font-size: 14px;
    margin-top: 5px;
  }
  
  &.abnormal .stat-num { color: #F56C6C; }
  &.normal .stat-num { color: #67C23A; }
}

.abnormal-value { color: #F56C6C; font-weight: bold; }
.abnormal-tag { color: #F56C6C; margin-left: 5px; }

.abnormal-detail p {
  margin: 8px 0;
  color: #606266;
  line-height: 1.6;
}

.report-text {
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
}
</style>