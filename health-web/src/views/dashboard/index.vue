<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card warning-bg">
          <div class="stat-icon"><i class="el-icon-warning-outline"></i></div>
          <div class="stat-content">
            <div class="stat-value">{{ warningData.total || 0 }}</div>
            <div class="stat-label">预警总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card danger-bg">
          <div class="stat-icon"><i class="el-icon-circle-close"></i></div>
          <div class="stat-content">
            <div class="stat-value">{{ warningData.unhandledCount || 0 }}</div>
            <div class="stat-label">待处理预警</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card success-bg">
          <div class="stat-icon"><i class="el-icon-document"></i></div>
          <div class="stat-content">
            <div class="stat-value">{{ reportCount }}</div>
            <div class="stat-label">体检报告</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card info-bg">
          <div class="stat-icon"><i class="el-icon-data-analysis"></i></div>
          <div class="stat-content">
            <div class="stat-value">{{ recordCount }}</div>
            <div class="stat-label">健康记录</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card>
          <div slot="header" class="card-header">
            <span>最新预警</span>
            <el-button type="text" size="small" @click="$router.push('/warning/list')">查看全部</el-button>
          </div>
          <el-table :data="recentWarnings" stripe style="width: 100%">
            <el-table-column prop="warningType" label="类型" width="120">
              <template slot-scope="{ row }">
                <el-tag :type="getWarningTagColor(row.warningLevel)">{{ getTypeName(row.warningType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="warningContent" label="预警内容" show-overflow-tooltip></el-table-column>
            <el-table-column prop="warningLevel" label="等级" width="100">
              <template slot-scope="{ row }">
                <el-tag :type="getLevelTagColor(row.warningLevel)" size="mini">{{ getLevelText(row.warningLevel) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="160">
              <template slot-scope="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="isHandled" label="状态" width="80">
              <template slot-scope="{ row }">
                <el-tag :type="row.isHandled ? 'success' : 'warning'" size="mini">
                  {{ row.isHandled ? '已处理' : '待处理' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header" class="card-header">
            <span>热门资讯</span>
            <el-button type="text" size="small" @click="$router.push('/news/list')">查看全部</el-button>
          </div>
          <div class="news-list">
            <div v-for="(news, index) in hotNews" :key="news.id" class="news-item" @click="viewNews(news)">
              <div class="news-rank">{{ index + 1 }}</div>
              <div class="news-info">
                <div class="news-title">{{ news.title }}</div>
                <div class="news-meta">
                  <span><i class="el-icon-view"></i> {{ news.viewCount }}</span>
                  <span class="date">{{ formatDate(news.publishTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>预警等级分布</span></div>
          <div ref="warningChart" style="height: 280px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>健康数据上报</span></div>
          <div ref="recordChart" style="height: 280px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getWarningCount, listWarnings, getHotNews, listRecords, listReports } from '@/api'
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      warningData: {},
      recentWarnings: [],
      hotNews: [],
      recordCount: 0,
      reportCount: 0
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [warningRes, newsRes, recordRes, reportRes] = await Promise.all([
          getWarningCount(),
          getHotNews({ limit: 5 }),
          listRecords({}),
          listReports()
        ])
        if (warningRes.code === 200) this.warningData = warningRes.data
        if (newsRes.code === 200) this.hotNews = newsRes.data
        if (recordRes.code === 200) this.recordCount = recordRes.data?.length || 0
        if (reportRes.code === 200) this.reportCount = reportRes.data?.length || 0
      } catch (e) {
        console.error(e)
      }
      this.loadRecentWarnings()
      this.$nextTick(() => {
        this.initWarningChart()
        this.initRecordChart()
      })
    },
    async loadRecentWarnings() {
      try {
        const res = await listWarnings({ isHandled: 0 })
        if (res.code === 200) this.recentWarnings = (res.data || []).slice(0, 5)
      } catch (e) {}
    },
    initWarningChart() {
      const chart = echarts.init(this.$refs.warningChart)
      chart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: '5%', left: 'center' },
        series: [{
          name: '预警等级',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          label: { show: false },
          data: [
            { value: this.warningData.criticalCount || 0, name: '严重', itemStyle: { color: '#F56C6C' } },
            { value: this.warningData.highCount || 0, name: '高', itemStyle: { color: '#E6A23C' } },
            { value: this.warningData.mediumCount || 0, name: '中', itemStyle: { color: '#409EFF' } },
            { value: this.warningData.lowCount || 0, name: '低', itemStyle: { color: '#67C23A' } }
          ]
        }]
      })
      window.addEventListener('resize', () => chart.resize())
    },
    initRecordChart() {
      const chart = echarts.init(this.$refs.recordChart)
      const types = ['血压', '血糖', '心率', '体温', '血氧']
      const counts = [12, 8, 15, 6, 10]
      chart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: types, axisLabel: { color: '#606266' } },
        yAxis: { type: 'value', axisLabel: { color: '#606266' } },
        series: [{
          type: 'bar',
          data: counts,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#409EFF' },
              { offset: 1, color: '#79bbff' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          barWidth: '50%'
        }]
      })
      window.addEventListener('resize', () => chart.resize())
    },
    getTypeName(type) {
      const map = {
        blood_pressure: '血压', blood_sugar: '血糖', heart_rate: '心率',
        temperature: '体温', spo2: '血氧', cholesterol: '胆固醇', triglyceride: '甘油三酯'
      }
      return map[type] || type
    },
    getWarningTagColor(level) {
      return this.getLevelTagColor(level)
    },
    getLevelTagColor(level) {
      const map = { critical: 'danger', high: 'danger', medium: 'warning', low: 'info' }
      return map[level] || 'info'
    },
    getLevelText(level) {
      const map = { critical: '严重', high: '高', medium: '中', low: '低' }
      return map[level] || level
    },
    formatDate(time) {
      if (!time) return '-'
      const d = new Date(time)
      return `${d.getMonth() + 1}-${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
    },
    viewNews(news) {
      this.$router.push('/news/list')
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card {
    text-align: left;
    display: flex;
    align-items: center;
    padding: 10px;
    
    .stat-icon {
      font-size: 44px;
      width: 70px;
      height: 70px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      margin-right: 15px;
    }
    
    .stat-value {
      font-size: 26px;
      font-weight: bold;
      color: #303133;
    }
    
    .stat-label {
      color: #909399;
      font-size: 13px;
    }
    
    &.warning-bg .stat-icon { background: linear-gradient(135deg, #faad14, #ff7a45); }
    &.danger-bg .stat-icon { background: linear-gradient(135deg, #f5222d, #ff7875); }
    &.success-bg .stat-icon { background: linear-gradient(135deg, #52c41a, #95de64); }
    &.info-bg .stat-icon { background: linear-gradient(135deg, #1890ff, #40a9ff); }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .news-list {
    .news-item {
      display: flex;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      
      &:hover {
        .news-title { color: #409EFF; }
      }
      
      &:last-child { border-bottom: none; }
      
      .news-rank {
        width: 24px;
        height: 24px;
        border-radius: 4px;
        background: #f0f0f0;
        color: #606266;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        font-weight: bold;
        margin-right: 10px;
        flex-shrink: 0;
        
        &:nth-child(1) { background: #f5222d; color: #fff; }
      }
      
      .news-info {
        flex: 1;
        overflow: hidden;
        
        .news-title {
          font-size: 14px;
          color: #303133;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          margin-bottom: 4px;
        }
        
        .news-meta {
          display: flex;
          gap: 15px;
          font-size: 12px;
          color: #909399;
          
          .date { margin-left: auto; }
        }
      }
    }
  }
}
</style>