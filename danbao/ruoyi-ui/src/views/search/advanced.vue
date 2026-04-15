<template>
  <div class="advanced-search-page">
    <!-- 顶部背景 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
    </div>

    <div class="main-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <div class="header-left">
          <el-button icon="el-icon-arrow-left" @click="goBack">返回</el-button>
          <h1 class="page-title">高级检索</h1>
        </div>
        <p class="page-subtitle">多维度组合查询，精准定位专利信息</p>
      </div>

      <!-- 高级搜索表单 -->
      <div class="search-panel">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="advanced-form">
          <div class="form-row">
            <el-form-item label="标题关键词" prop="titleKeyword">
              <el-input
                v-model="queryParams.titleKeyword"
                placeholder="请输入专利标题关键词"
                clearable
                prefix-icon="el-icon-document"
                class="form-input"
              />
            </el-form-item>
            <el-form-item label="摘要关键词" prop="absKeyword">
              <el-input
                v-model="queryParams.absKeyword"
                placeholder="请输入专利摘要关键词"
                clearable
                prefix-icon="el-icon-document-copy"
                class="form-input"
              />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="分类号" prop="classification">
              <el-select
                v-model="queryParams.classification"
                placeholder="请选择或输入分类号"
                clearable
                filterable
                allow-create
                default-first-option
                class="form-input"
              >
                <el-option
                  v-for="item in classificationOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="申请日期" prop="dateRange">
              <el-date-picker
                v-model="queryParams.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
                class="form-date-range"
              />
            </el-form-item>
          </div>

          <div class="form-actions">
            <el-button type="primary" icon="el-icon-search" size="medium" @click="handleQuery" :loading="loading">
              搜索
            </el-button>
            <el-button icon="el-icon-refresh" size="medium" @click="resetQuery">重置</el-button>
          </div>
        </el-form>
      </div>

      <!-- 结果区域 -->
      <div v-if="searchList.length > 0" class="result-section">
        <!-- 结果统计 -->
        <div class="result-header">
          <div class="result-info">
            <i class="el-icon-success"></i>
            <span>找到 <strong>{{ total }}</strong> 条相关专利</span>
          </div>
          <div class="result-actions">
            <el-button size="small" icon="el-icon-document" @click="exportData">导出</el-button>
          </div>
        </div>

        <!-- 结果列表 -->
        <div class="patent-list">
          <div
            v-for="(item, index) in searchList"
            :key="index"
            class="patent-card"
          >
            <div class="patent-index">{{ (queryParams.pageNum - 1) * queryParams.pageSize + index + 1 }}</div>
            <div class="patent-body">
              <div class="patent-title-row">
                <h3 class="patent-title">{{ item.title || '无标题' }}</h3>
                <el-tag size="mini" type="info">{{ item.typeName || '专利' }}</el-tag>
              </div>
              <div class="patent-meta">
                <div class="meta-item">
                  <span class="meta-label"><i class="el-icon-postcard"></i> 申请号</span>
                  <span class="meta-value">{{ item.applicationNo || '-' }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label"><i class="el-icon-user"></i> 申请人</span>
                  <span class="meta-value highlight">{{ item.applicant || '-' }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label"><i class="el-icon-user-solid"></i> 发明人</span>
                  <span class="meta-value">{{ item.inventor || '-' }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label"><i class="el-icon-calendar"></i> 申请日期</span>
                  <span class="meta-value">{{ item.applicationDate || '-' }}</span>
                </div>
              </div>
              <div v-if="item.abs" class="patent-abstract">
                <span class="abstract-label">摘要：</span>
                <span class="abstract-text">{{ truncateText(item.abs, 200) }}</span>
              </div>
              <div class="patent-footer">
                <div class="patent-tags">
                  <el-tag v-if="item.mainClassification" size="small" effect="plain">{{ item.mainClassification }}</el-tag>
                  <el-tag v-if="item.classification" size="small" type="info">{{ item.classification }}</el-tag>
                  <el-tag v-if="item.legalType" size="small" type="success">{{ item.legalType }}</el-tag>
                </div>
                <el-button size="mini" type="text" icon="el-icon-view">查看详情</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-show="total > 0"
            :total="total"
            :page-size="queryParams.pageSize"
            :current-page="queryParams.pageNum"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            background
            prev-text="上一页"
            next-text="下一页"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-else-if="loading" class="loading-section">
        <i class="el-icon-loading"></i>
        <p>正在检索...</p>
      </div>

      <!-- 无结果 -->
      <div v-else-if="hasSearched" class="empty-section">
        <i class="el-icon-search"></i>
        <h3>未找到相关专利</h3>
        <p>请尝试调整检索条件</p>
        <el-button type="primary" @click="resetQuery">重新检索</el-button>
      </div>

      <!-- 初始状态 -->
      <div v-else class="init-section">
        <div class="tips-card">
          <i class="el-icon-info"></i>
          <div class="tips-content">
            <h4>检索提示</h4>
            <ul>
              <li>支持标题、摘要关键词模糊匹配</li>
              <li>可选择分类号进行分类维度过滤</li>
              <li>可设置申请日期区间进行时间范围限定</li>
              <li>多个条件组合查询，结果更精准</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { advancedSearch, listClassifications } from '@/api/search'

export default {
  name: 'AdvancedSearch',
  data() {
    return {
      queryParams: {
        titleKeyword: '',
        absKeyword: '',
        classification: '',
        startDate: '',
        endDate: '',
        dateRange: [],
        pageNum: 1,
        pageSize: 10
      },
      searchList: [],
      hasSearched: false,
      total: 0,
      loading: false,
      classificationOptions: []
    }
  },
  mounted() {
    this.loadClassifications()
  },
  methods: {
    loadClassifications() {
      listClassifications().then(response => {
        if (response.data && response.data.classifications) {
          this.classificationOptions = response.data.classifications
        }
      })
    },
    handleQuery(isNewSearch = true) {
      if (isNewSearch) {
        this.queryParams.pageNum = 1
      }

      // 处理日期范围
      if (this.queryParams.dateRange && this.queryParams.dateRange.length === 2) {
        this.queryParams.startDate = this.queryParams.dateRange[0]
        this.queryParams.endDate = this.queryParams.dateRange[1]
      } else {
        this.queryParams.startDate = ''
        this.queryParams.endDate = ''
      }

      this.loading = true

      advancedSearch(this.queryParams).then(response => {
        this.hasSearched = true
        const data = response.data || {}
        this.searchList = data.records || []
        this.total = data.total || 0
        this.loading = false

        if (this.searchList.length > 0) {
          this.$message.success(`找到 ${this.total} 条相关专利`)
        } else {
          this.$message.info('未找到相关专利')
        }
      }).catch(error => {
        console.error('高级检索失败:', error)
        this.$message.error('检索失败，请稍后重试')
        this.loading = false
      })
    },
    resetQuery() {
      this.queryParams = {
        titleKeyword: '',
        absKeyword: '',
        classification: '',
        startDate: '',
        endDate: '',
        dateRange: [],
        pageNum: 1,
        pageSize: 10
      }
      this.searchList = []
      this.hasSearched = false
      this.$refs.queryFormRef && this.$refs.queryFormRef.clearValidate()
    },
    handleSizeChange(size) {
      this.queryParams.pageSize = size
      this.queryParams.pageNum = 1
      this.handleQuery(false)
    },
    handleCurrentChange(page) {
      this.queryParams.pageNum = page
      this.handleQuery(false)
    },
    goBack() {
      this.$router.go(-1)
    },
    truncateText(text, maxLength) {
      if (!text) return ''
      if (text.length <= maxLength) return text
      return text.substring(0, maxLength) + '...'
    },
    exportData() {
      this.$message.info('导出功能开发中...')
    }
  }
}
</script>

<style scoped>
.advanced-search-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
  position: relative;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 280px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.bg-circle-1 {
  width: 300px;
  height: 300px;
  background: #667eea;
  top: -80px;
  right: -80px;
}

.bg-circle-2 {
  width: 200px;
  height: 200px;
  background: #764ba2;
  top: 30px;
  left: -40px;
}

/* 主内容 */
.main-content {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 60px;
}

/* 页面标题 */
.page-header {
  padding: 40px 0 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin: 8px 0 0 64px;
}

/* 搜索面板 */
.search-panel {
  background: #ffffff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.advanced-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.form-row .el-form-item {
  flex: 1;
  min-width: 280px;
  margin-bottom: 0;
}

.form-input {
  width: 100%;
}

.form-date-range {
  width: 320px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.form-actions >>> .el-button {
  min-width: 100px;
  height: 40px;
  font-size: 15px;
}

.form-actions >>> .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

/* 结果区域 */
.result-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.result-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 15px;
}

.result-info i {
  font-size: 18px;
  color: #67c23a;
}

.result-info strong {
  font-size: 18px;
  color: #667eea;
}

/* 专利列表 */
.patent-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.patent-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #fafbfc;
  border-radius: 10px;
  border: 1px solid transparent;
  transition: all 0.3s ease;
}

.patent-card:hover {
  background: #ffffff;
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  transform: translateX(4px);
}

.patent-index {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
}

.patent-body {
  flex: 1;
  min-width: 0;
}

.patent-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.patent-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.patent-meta {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-label {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-value {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.meta-value.highlight {
  color: #667eea;
}

.patent-abstract {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
  padding: 12px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 8px;
}

.abstract-label {
  color: #909399;
}

.patent-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.patent-tags {
  display: flex;
  gap: 8px;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 加载状态 */
.loading-section {
  text-align: center;
  padding: 80px 0;
  background: #ffffff;
  border-radius: 12px;
}

.loading-section i {
  font-size: 48px;
  color: #667eea;
  animation: rotate 1s linear infinite;
}

.loading-section p {
  font-size: 16px;
  color: #909399;
  margin-top: 16px;
}

/* 空状态 */
.empty-section {
  text-align: center;
  padding: 80px 0;
  background: #ffffff;
  border-radius: 12px;
}

.empty-section i {
  font-size: 64px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.empty-section h3 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 8px;
}

.empty-section p {
  font-size: 14px;
  color: #909399;
  margin: 0 0 20px;
}

/* 初始提示 */
.init-section {
  padding: 20px 0;
}

.tips-card {
  display: flex;
  gap: 16px;
  padding: 24px;
  background: #ffffff;
  border-radius: 12px;
  border-left: 4px solid #667eea;
}

.tips-card > i {
  font-size: 24px;
  color: #667eea;
  flex-shrink: 0;
}

.tips-content h4 {
  margin: 0 0 12px;
  font-size: 16px;
  color: #303133;
}

.tips-content ul {
  margin: 0;
  padding-left: 20px;
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
}

/* 动画 */
@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    padding: 30px 0 20px;
  }

  .page-title {
    font-size: 22px;
  }

  .search-panel {
    padding: 20px;
  }

  .form-row {
    flex-direction: column;
  }

  .form-row .el-form-item {
    min-width: 100%;
  }

  .form-date-range {
    width: 100%;
  }

  .patent-meta {
    grid-template-columns: repeat(2, 1fr);
  }

  .patent-card {
    flex-direction: column;
  }

  .patent-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
