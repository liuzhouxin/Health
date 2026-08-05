<template>
  <el-dialog :title="title" :visible.sync="visible" width="500px" @close="handleClose">
    <el-form :model="form" label-width="100px" size="small" :rules="rules" ref="form">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title"></el-input>
      </el-form-item>
      <el-form-item label="记录类型" prop="recordType">
        <el-select v-model="form.recordType" placeholder="请选择类型" style="width:100%">
          <el-option label="血压" value="blood_pressure"></el-option>
          <el-option label="血糖" value="blood_sugar"></el-option>
          <el-option label="心率" value="heart_rate"></el-option>
          <el-option label="体温" value="temperature"></el-option>
          <el-option label="血氧" value="spo2"></el-option>
          <el-option label="胆固醇" value="cholesterol"></el-option>
          <el-option label="甘油三酯" value="triglyceride"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="收缩压" v-if="form.recordType === 'blood_pressure'">
        <el-input-number v-model="form.systolic" :min="0" :max="300"></el-input-number>
        <span class="ml-10">mmHg</span>
      </el-form-item>
      <el-form-item label="舒张压" v-if="form.recordType === 'blood_pressure'">
        <el-input-number v-model="form.diastolic" :min="0" :max="200"></el-input-number>
        <span class="ml-10">mmHg</span>
      </el-form-item>
      <el-form-item label="血糖值" v-if="form.recordType === 'blood_sugar'">
        <el-input-number v-model="form.sugarValue" :min="0" :precision="1" :step="0.1"></el-input-number>
        <span class="ml-10">mmol/L</span>
      </el-form-item>
      <el-form-item label="心率" v-if="form.recordType === 'heart_rate'">
        <el-input-number v-model="form.heartRate" :min="0" :max="250"></el-input-number>
        <span class="ml-10">次/分</span>
      </el-form-item>
      <el-form-item label="体温" v-if="form.recordType === 'temperature'">
        <el-input-number v-model="form.temperature" :min="30" :max="45" :precision="1" :step="0.1"></el-input-number>
        <span class="ml-10">℃</span>
      </el-form-item>
      <el-form-item label="血氧" v-if="form.recordType === 'spo2'">
        <el-input-number v-model="form.spo2" :min="0" :max="100"></el-input-number>
        <span class="ml-10">%</span>
      </el-form-item>
      <el-form-item label="记录日期" prop="recordDate">
        <el-date-picker v-model="form.recordDate" type="date" value-format="yyyy-MM-dd" style="width:100%"></el-date-picker>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { createRecord, updateRecord } from '@/api'

export default {
  name: 'RecordFormDialog',
  props: {
    row: { type: Object, default: null }
  },
  data() {
    return {
      visible: false,
      title: '新增记录',
      form: { recordType: 'blood_pressure', recordDate: new Date().toISOString().slice(0, 10) },
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        recordType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        recordDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
      }
    }
  },
  methods: {
    open(row = null) {
      this.title = row ? '编辑记录' : '新增记录'
      if (row) {
        this.form = { ...row }
        try {
          const val = typeof row.recordValue === 'string' ? JSON.parse(row.recordValue) : row.recordValue
          if (val) {
            Object.keys(val).forEach(k => { this.form[k] = val[k] })
          }
        } catch {}
      } else {
        this.form = { recordType: 'blood_pressure', recordDate: new Date().toISOString().slice(0, 10) }
      }
      this.visible = true
    },
    handleClose() { this.$refs.form && this.$refs.form.resetFields() },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        const data = { ...this.form }
        const recordValue = {}
        if (data.systolic !== undefined) recordValue.systolic = data.systolic
        if (data.diastolic !== undefined) recordValue.diastolic = data.diastolic
        if (data.sugarValue !== undefined) recordValue.sugarValue = data.sugarValue
        if (data.heartRate !== undefined) recordValue.heartRate = data.heartRate
        if (data.temperature !== undefined) recordValue.temperature = data.temperature
        if (data.spo2 !== undefined) recordValue.spo2 = data.spo2
        data.recordValue = JSON.stringify(recordValue)
        
        try {
          if (data.id) {
            await updateRecord(data)
          } else {
            await createRecord(data)
          }
          this.$message.success('操作成功')
          this.visible = false
          this.$emit('success')
        } catch (e) {}
      })
    }
  }
}
</script>

<style scoped>
.ml-10 { margin-left: 10px; color: #909399; }
</style>