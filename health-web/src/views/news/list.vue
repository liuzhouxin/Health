<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <div slot="header" class="flex-between">
            <span>健康资讯列表</span>
            <el-button size="small" @click="handleAdd" v-if="canEdit">发布资讯</el-button>
          </div>
          <el-table :data="tableData" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="title" label="标题" show-overflow-tooltip></el-table-column>
            <el-table-column prop="category" label="分类" width="100">
              <template slot-scope="{ row }">
                <el-tag size="mini">{{ row.category }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="author" label="作者" width="100"></el-table-column>
            <el-table-column prop="viewCount" label="浏览量" width="100" sortable>
              <template slot-scope="{ row }">
                <i class="el-icon-view"></i> {{ row.viewCount || 0 }}
              </template>
            </el-table-column>
            <el-table-column prop="publishTime" label="发布时间" width="160" sortable>
              <template slot-scope="{ row }">{{ formatTime(row.publishTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template slot-scope="{ row }">
                <el-button type="text" size="small" @click="handleView(row)">查看</el-button>
                <el-button type="text" size="small" @click="handleEdit(row)" v-if="canEdit">编辑</el-button>
                <el-button type="text" size="small" style="color:#f56c6c" @click="handleDelete(row)" v-if="canDelete">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header"><span>热门资讯 TOP5</span></div>
          <div class="hot-list">
            <div v-for="(item, index) in hotNews" :key="item.id" class="hot-item" @click="handleView(item)">
              <div class="rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="hot-info">
                <div class="hot-title">{{ item.title }}</div>
                <div class="hot-meta">
                  <span><i class="el-icon-view"></i> {{ item.viewCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="标题">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width:100%">
            <el-option label="营养膳食" value="营养膳食"></el-option>
            <el-option label="运动健身" value="运动健身"></el-option>
            <el-option label="心理健康" value="心理健康"></el-option>
            <el-option label="疾病预防" value="疾病预防"></el-option>
            <el-option label="中医养生" value="中医养生"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author"></el-input>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="资讯详情" :visible.sync="detailVisible" width="600px">
      <div v-if="currentDetail">
        <h3 style="margin-top:0">{{ currentDetail.title }}</h3>
        <div style="color:#909399;margin-bottom:15px">
          作者: {{ currentDetail.author }} | 分类: {{ currentDetail.category }} | 浏览: {{ currentDetail.viewCount }}
        </div>
        <div style="line-height:1.8;color:#606266">{{ currentDetail.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNews, getHotNews, getNewsDetail, createNews, updateNews, deleteNews } from '@/api'

export default {
  name: 'NewsList',
  data() {
    return {
      tableData: [], hotNews: [], loading: false,
      dialogVisible: false, dialogTitle: '', form: {},
      detailVisible: false, currentDetail: null
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
        const [listRes, hotRes] = await Promise.all([listNews({}), getHotNews({ limit: 5 })])
        if (listRes.code === 200) this.tableData = listRes.data
        if (hotRes.code === 200) this.hotNews = hotRes.data
      } catch (e) {} finally { this.loading = false }
    },
    formatTime(t) {
      if (!t) return '-'
      const d = new Date(t)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    },
    handleAdd() {
      this.form = { category: '营养膳食', author: '管理员' }
      this.dialogTitle = '发布资讯'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑资讯'
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        if (this.form.id) {
          await updateNews(this.form)
        } else {
          await createNews(this.form)
        }
        this.$message.success('保存成功')
        this.dialogVisible = false
        this.loadData()
      } catch (e) {}
    },
    async handleView(row) {
      try {
        const res = await getNewsDetail(row.id)
        if (res.code === 200) {
          this.currentDetail = res.data
          this.detailVisible = true
        }
      } catch (e) {}
    },
    handleDelete(row) {
      this.$confirm('确定删除该资讯?', '提示', { type: 'warning' })
        .then(async () => {
          try {
            await deleteNews(row.id)
            this.$message.success('删除成功')
            this.loadData()
          } catch (e) {}
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.hot-list {
  .hot-item {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    
    &:hover .hot-title { color: #409EFF; }
    
    .rank {
      width: 22px; height: 22px; border-radius: 4px;
      background: #f0f0f0; color: #606266;
      display: flex; align-items: center; justify-content: center;
      font-size: 12px; font-weight: bold; margin-right: 10px; flex-shrink: 0;
      
      &.rank-1 { background: #f5222d; color: #fff; }
      &.rank-2 { background: #fa8c16; color: #fff; }
      &.rank-3 { background: #faad14; color: #fff; }
    }
    
    .hot-info {
      .hot-title { font-size: 14px; color: #303133; margin-bottom: 4px; }
      .hot-meta { font-size: 12px; color: #909399; }
    }
  }
}
</style>