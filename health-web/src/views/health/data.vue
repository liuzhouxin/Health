<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon"><i class="el-icon-upload2"></i></div>
          <div class="stat-value">{{ stats.totalReports || 0 }}</div>
          <div class="stat-label">总上报次数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon success"><i class="el-icon-circle-check"></i></div>
          <div class="stat-value">{{ stats.successCount || 0 }}</div>
          <div class="stat-label">成功次数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon warning"><i class="el-icon-circle-close"></i></div>
          <div class="stat-value">{{ stats.failCount || 0 }}</div>
          <div class="stat-label">失败次数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>单条数据上报</span></div>
          <el-form :model="dataForm" label-width="100px" size="small">
            <el-form-item label="设备ID">
              <el-input v-model="dataForm.deviceId" placeholder="如:device_001"></el-input>
            </el-form-item>
            <el-form-item label="数据类型">
              <el-select v-model="dataForm.dataType" style="width:100%">
                <el-option label="血压" value="blood_pressure"></el-option>
                <el-option label="血糖" value="blood_sugar"></el-option>
                <el-option label="心率" value="heart_rate"></el-option>
                <el-option label="体温" value="temperature"></el-option>
                <el-option label="血氧" value="spo2"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="数据值(JSON)">
              <el-input v-model="dataForm.data" type="textarea" :rows="3" placeholder='{"systolic":120,"diastolic":80}'></el-input>
            </el-form-item>
            <el-form-item label="上报时间">
              <el-date-picker v-model="dataForm.reportTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%"></el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="submitData">提交上报</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>批量模拟上报</span></div>
          <el-form :model="batchForm" label-width="100px" size="small">
            <el-form-item label="设备ID前缀">
              <el-input v-model="batchForm.devicePrefix"></el-input>
            </el-form-item>
            <el-form-item label="上报数量">
              <el-input-number v-model="batchForm.count" :min="1" :max="100"></el-input-number>
            </el-form-item>
            <el-form-item label="数据类型">
              <el-select v-model="batchForm.dataType" style="width:100%">
                <el-option label="随机" value="random"></el-option>
                <el-option label="血压" value="blood_pressure"></el-option>
                <el-option label="血糖" value="blood_sugar"></el-option>
                <el-option label="心率" value="heart_rate"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="batching" @click="batchSubmitData">批量上报</el-button>
            </el-form-item>
          </el-form>
          <el-divider></el-divider>
          <div class="tip">
            <p><i class="el-icon-info"></i> 提示: 数据上报后将通过 Netty 服务异步处理,异常数据会自动生成预警。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-20">
      <div slot="header"><span>上报历史</span></div>
      <el-table :data="historyList" stripe size="small">
        <el-table-column prop="deviceId" label="设备ID" width="120"></el-table-column>
        <el-table-column prop="dataType" label="类型" width="100">
          <template slot-scope="{ row }">
            <el-tag size="mini">{{ getTypeName(row.dataType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="data" label="数据值" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ formatData(row.data) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 'success' ? 'success' : 'danger'" size="mini">
              {{ row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上报时间" width="160">
          <template slot-scope="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { submitHealthData, batchSubmitData, getNettyStats } from '@/api'

export default {
  name: 'HealthData',
  data() {
    return {
      dataForm: { deviceId: 'device_001', dataType: 'blood_pressure', data: '{"systolic":120,"diastolic":80}', reportTime: '' },
      batchForm: { devicePrefix: 'device_', count: 10, dataType: 'random' },
      submitting: false, batching: false,
      stats: {}, historyList: []
    }
  },
  mounted() {
    this.loadStats()
    this.initMockHistory()
  },
  methods: {
    async loadStats() {
      try {
        const res = await getNettyStats()
        if (res.code === 200) this.stats = res.data
      } catch (e) {}
    },
    initMockHistory() {
      const types = ['blood_pressure', 'blood_sugar', 'heart_rate', 'temperature']
      const list = []
      for (let i = 0; i < 5; i++) {
        list.push({
          deviceId: 'device_00' + (i + 1),
          dataType: types[i % types.length],
          data: { systolic: 110 + Math.random() * 30, diastolic: 70 + Math.random() * 20 },
          status: Math.random() > 0.3 ? 'success' : 'fail',
          createTime: Date.now() - i * 300000
        })
      }
      this.historyList = list
    },
    async submitData() {
      this.submitting = true
      try {
        let data = {}
        try {
          data = JSON.parse(this.dataForm.data)
        } catch {
          this.$message.error('数据格式必须为JSON')
          return
        }
        const res = await submitHealthData({
          deviceId: this.dataForm.deviceId,
          dataType: this.dataForm.dataType,
          data: data,
          reportTime: this.dataForm.reportTime || new Date().toISOString()
        })
        if (res.code === 200) {
          this.$message.success('上报成功')
          this.loadStats()
        } else {
          this.$message.error(res.message || '上报失败')
        }
      } catch (e) {
        this.$message.error('上报失败: ' + e.message)
      } finally {
        this.submitting = false
      }
    },
    async batchSubmitData() {
      this.batching = true
      try {
        const count = this.batchForm.count
        const list = []
        for (let i = 0; i < count; i++) {
          const type = this.batchForm.dataType === 'random'
            ? ['blood_pressure', 'blood_sugar', 'heart_rate'][Math.floor(Math.random() * 3)]
            : this.batchForm.dataType
          const data = this.generateData(type)
          list.push({
            deviceId: this.batchForm.devicePrefix + (i + 1),
            dataType: type,
            data: data,
            reportTime: new Date().toISOString()
          })
        }
        const res = await batchSubmitData(list)
        if (res.code === 200) {
          this.$message.success(`批量上报成功,共${count}条数据`)
          this.loadStats()
        }
      } catch (e) {
        this.$message.error('批量上报失败')
      } finally {
        this.batching = false
      }
    },
    generateData(type) {
      switch (type) {
        case 'blood_pressure': return { systolic: 110 + Math.floor(Math.random() * 40), diastolic: 70 + Math.floor(Math.random() * 20) }
        case 'blood_sugar': return { value: (4 + Math.random() * 3).toFixed(1) }
        case 'heart_rate': return { value: 60 + Math.floor(Math.random() * 40) }
        default: return { value: Math.random() * 100 }
      }
    },
    resetForm() {
      this.dataForm = { deviceId: 'device_001', dataType: 'blood_pressure', data: '', reportTime: '' }
    },
    getTypeName(type) {
      const m = { blood_pressure: '血压', blood_sugar: '血糖', heart_rate: '心率', temperature: '体温', spo2: '血氧' }
      return m[type] || type
    },
    formatData(data) {
      if (typeof data === 'object') return JSON.stringify(data)
      return data
    },
    formatTime(t) {
      if (!t) return '-'
      const d = new Date(t)
      return `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}:${String(d.getSeconds()).padStart(2,'0')}`
    }
  }
}
</script>

<style scoped lang="scss">
.stat-card {
  text-align: center;
  padding: 15px;
  
  .stat-icon {
    font-size: 36px;
    color: #409EFF;
    margin-bottom: 10px;
    
    &.success { color: #67C23A; }
    &.warning { color: #E6A23C; }
  }
  
  .stat-value { font-size: 30px; font-weight: bold; color: #303133; }
  .stat-label { color: #909399; font-size: 13px; }
}

.tip {
  color: #909399;
  font-size: 12px;
  
  p { margin: 0; }
}
</style>