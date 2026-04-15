<template>
  <div class="search-page">
    <!-- 顶部背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- Hero 搜索区域 -->
      <div class="hero-section">
        <div class="hero-badge">
          <span class="badge-icon"><i class="el-icon-medal"></i></span>
          智能专利检索系统
        </div>
        <h1 class="hero-title">探索知识产权的无限可能</h1>
        <p class="hero-subtitle">基于 Solr 全文检索引擎，快速精准查找海量专利数据</p>

        <!-- 搜索框 -->
        <div class="search-wrapper">
          <el-form :model="queryParams" ref="queryFormRef" class="search-form-inline">
            <div class="search-input-group">
              <i class="search-icon el-icon-search"></i>
              <el-input
                v-model="queryParams.keyword"
                placeholder="输入专利名称、申请人、申请号..."
                clearable
                class="main-search-input"
                @keyup.enter.native="handleQuery"
              />
              <el-button type="primary" class="main-search-btn" @click="handleQuery" :loading="loading">
                <span v-if="!loading">搜索</span>
                <span v-else>搜索中...</span>
              </el-button>
            </div>
          </el-form>
          <div class="search-tips">
            <span class="tips-label">热门领域：</span>
            <span v-for="(tag, index) in hotTags" :key="index" class="hot-tag" @click="quickSearch(tag)">
              {{ tag }}
            </span>
          </div>
          <div class="search-actions">
            <el-button type="text" icon="el-icon-arrow-right" @click="goToAdvanced">高级检索</el-button>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-section">
        <div class="stat-card">
          <div class="stat-icon stat-icon-1">
            <i class="el-icon-document-copy"></i>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ total > 0 ? total.toLocaleString() : '--' }}</span>
            <span class="stat-label">检索结果</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon-2">
            <i class="el-icon-data-line"></i>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ searchList.length }}</span>
            <span class="stat-label">当前页</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon stat-icon-3">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ currentPage }}</span>
            <span class="stat-label">当前页码</span>
          </div>
        </div>
      </div>

      <!-- 结果区域 - 左右布局：左侧列表，右侧图表 -->
      <div v-if="searchList.length > 0 || hasSearched" class="result-layout">
        <!-- 左侧：专利列表 -->
        <div class="result-left">
          <div class="result-section">
            <!-- 结果头部 -->
            <div class="result-header">
              <div class="result-count">
                <span class="count-icon"><i class="el-icon-success"></i></span>
                <span>为您找到 <strong class="count-number">{{ total.toLocaleString() }}</strong> 条相关专利</span>
              </div>
              <div class="result-actions">
                <el-button size="small" icon="el-icon-refresh" @click="resetQuery">重置</el-button>
              </div>
            </div>

            <!-- 专利列表 -->
            <div class="patent-grid">
              <div
                v-for="(item, index) in searchList"
                :key="index"
                class="patent-item"
                :class="{ 'patent-item-even': index % 2 === 0 }"
              >
                <div class="patent-index">{{ (queryParams.pageNum - 1) * queryParams.pageSize + index + 1 }}</div>
                <div class="patent-body">
                  <div class="patent-title-row">
                    <h3 class="patent-title">{{ item.title || '无标题' }}</h3>
                    <el-tag size="mini" type="info" class="patent-type">{{ item.typeName || '专利' }}</el-tag>
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
                    <span class="abstract-text">{{ truncateText(item.abs, 150) }}</span>
                  </div>
                  <div class="patent-footer">
                    <div class="patent-tags">
                      <el-tag v-if="item.mainClassification" size="small" effect="plain">{{ item.mainClassification }}</el-tag>
                      <el-tag v-if="item.legalType" size="small" type="success" effect="plain">{{ item.legalType }}</el-tag>
                    </div>
                    <div class="patent-actions">
                      <el-button size="mini" type="text" icon="el-icon-view">查看详情</el-button>
                    </div>
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
        </div>

        <!-- 右侧：统计图表 -->
        <div class="result-right">
          <div class="charts-section">
            <!-- 省份统计柱状图 -->
            <div class="chart-card">
              <div class="chart-header">
                <span class="chart-title"><i class="el-icon-map-location"></i> 省份分布</span>
                <el-tag size="mini" type="info">Top {{ provinceStat.items ? provinceStat.items.length : 0 }}</el-tag>
              </div>
              <div ref="provinceChart" class="chart-container"></div>
            </div>

            <!-- 类型统计饼图 -->
            <div class="chart-card">
              <div class="chart-header">
                <span class="chart-title"><i class="el-icon-pie-chart"></i> 专利类型分布</span>
              </div>
              <div ref="typeChart" class="chart-container"></div>
            </div>

            <!-- 年份趋势图 -->
            <div class="chart-card">
              <div class="chart-header">
                <span class="chart-title"><i class="el-icon-data-line"></i> 年度趋势</span>
              </div>
              <div ref="yearChart" class="chart-container"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-else-if="loading" class="loading-section">
        <div class="loading-spinner">
          <i class="el-icon-loading"></i>
        </div>
        <p>正在检索专利信息...</p>
      </div>

      <!-- 无结果 -->
      <div v-else-if="hasSearched" class="empty-section">
        <div class="empty-illustration">
          <i class="el-icon-search"></i>
        </div>
        <h3>未找到相关专利</h3>
        <p>请尝试使用其他关键词进行搜索</p>
        <el-button type="primary" plain @click="resetQuery">重新搜索</el-button>
      </div>

      <!-- 初始状态 -->
      <div v-else class="init-section">
        <div class="init-features">
          <div class="feature-card">
            <div class="feature-icon feature-icon-1">
              <i class="el-icon-lightning"></i>
            </div>
            <h4>快速检索</h4>
            <p>毫秒级响应，精准匹配关键词</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon feature-icon-2">
              <i class="el-icon-connection"></i>
            </div>
            <h4>海量数据</h4>
            <p>覆盖全球专利数据库</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon feature-icon-3">
              <i class="el-icon-filter"></i>
            </div>
            <h4>多维度筛选</h4>
            <p>按类型、日期、申请人等多维度筛选</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { listSearch, statByProvince, statByType, statByYear } from '@/api/search'
import * as echarts from 'echarts'

export default {
  name: 'Search',
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        keyword: ''
      },
      searchList: [],
      hasSearched: false,
      total: 0,
      loading: false,
      hotTags: ['人工智能', '新能源', '医疗器械', '5G通信', '区块链'],
      // 统计数据
      provinceStat: { items: [], total: 0 },
      typeStat: { items: [], total: 0 },
      yearStat: { items: [], total: 0 },
      // 图表实例
      provinceChart: null,
      typeChart: null,
      yearChart: null
    }
  },
  computed: {
    currentPage() {
      return this.queryParams.pageNum
    }
  },
  mounted() {
    // 窗口大小改变时重绘图表
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    // 销毁图表实例
    if (this.provinceChart) this.provinceChart.dispose()
    if (this.typeChart) this.typeChart.dispose()
    if (this.yearChart) this.yearChart.dispose()
  },
  methods: {
    handleQuery(isNewSearch = true) {
      const keyword = this.queryParams.keyword ? this.queryParams.keyword.trim() : ''

      if (!keyword) {
        this.$message.warning('请输入检索关键词')
        return
      }

      if (isNewSearch) {
        this.queryParams.pageNum = 1
      }

      this.loading = true

      // 并行发起搜索和统计请求
      Promise.all([
        listSearch(this.queryParams),
        statByProvince({ keyword: keyword, limit: 10 }),
        statByType({ keyword: keyword }),
        statByYear({ keyword: keyword, startYear: 2000, endYear: 2024 })
      ]).then(([searchRes, provinceRes, typeRes, yearRes]) => {
        this.hasSearched = true

        // 处理搜索结果
        const searchData = searchRes.data || {}
        this.searchList = searchData.records || []
        this.total = searchData.total || 0

        // 处理统计数据
        this.provinceStat = provinceRes.data || { items: [], total: 0 }
        this.typeStat = typeRes.data || { items: [], total: 0 }
        this.yearStat = yearRes.data || { items: [], total: 0 }

        this.loading = false

        // 渲染图表
        this.$nextTick(() => {
          this.renderCharts()
        })

        if (this.searchList.length > 0) {
          this.$message.success(`找到 ${this.total} 条相关专利`)
        } else {
          this.$message.info('未找到相关专利')
        }
      }).catch(error => {
        console.error('搜索失败:', error)
        this.$message.error('搜索失败，请稍后重试')
        this.loading = false
      })
    },

    // 渲染所有图表
    renderCharts() {
      this.renderProvinceChart()
      this.renderTypeChart()
      this.renderYearChart()
    },

    // 渲染省份统计柱状图
    renderProvinceChart() {
      if (!this.$refs.provinceChart) return

      const items = this.provinceStat.items || []
      const names = items.map(item => item.name)
      const values = items.map(item => item.count)

      if (!this.provinceChart) {
        this.provinceChart = echarts.init(this.$refs.provinceChart)
      }

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            rotate: 45,
            fontSize: 11
          }
        },
        yAxis: {
          type: 'value',
          name: '数量'
        },
        series: [{
          data: values,
          type: 'bar',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ])
          },
          barWidth: '60%'
        }]
      }

      this.provinceChart.setOption(option)
    },

    // 渲染类型统计饼图
    renderTypeChart() {
      if (!this.$refs.typeChart) return

      const items = this.typeStat.items || []
      const data = items.map(item => ({
        name: item.name,
        value: item.count
      }))

      if (!this.typeChart) {
        this.typeChart = echarts.init(this.$refs.typeChart)
      }

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          right: 10,
          top: 20,
          bottom: 20,
          textStyle: { fontSize: 11 }
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 6,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 12,
              fontWeight: 'bold'
            }
          },
          data: data
        }]
      }

      this.typeChart.setOption(option)
    },

    // 渲染年份趋势图
    renderYearChart() {
      if (!this.$refs.yearChart) return

      const items = this.yearStat.items || []
      const years = items.map(item => item.name)
      const values = items.map(item => item.count)

      if (!this.yearChart) {
        this.yearChart = echarts.init(this.$refs.yearChart)
      }

      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: years,
          axisLabel: {
            rotate: 45,
            fontSize: 10
          }
        },
        yAxis: {
          type: 'value',
          name: '数量'
        },
        series: [{
          data: values,
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: {
            color: '#667eea',
            width: 3
          },
          itemStyle: {
            color: '#667eea'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
            ])
          }
        }]
      }

      this.yearChart.setOption(option)
    },

    // 窗口大小改变时重绘
    handleResize() {
      if (this.provinceChart) this.provinceChart.resize()
      if (this.typeChart) this.typeChart.resize()
      if (this.yearChart) this.yearChart.resize()
    },

    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        keyword: ''
      }
      this.searchList = []
      this.hasSearched = false
      this.total = 0
      this.provinceStat = { items: [], total: 0 }
      this.typeStat = { items: [], total: 0 }
      this.yearStat = { items: [], total: 0 }
      this.$refs.queryFormRef && this.$refs.queryFormRef.clearValidate()
    },

    quickSearch(keyword) {
      this.queryParams.keyword = keyword
      this.handleQuery()
    },
    goToAdvanced() {
      this.$router.push('/search/advanced')
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

    truncateText(text, maxLength) {
      if (!text) return ''
      if (text.length <= maxLength) return text
      return text.substring(0, maxLength) + '...'
    }
  }
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 420px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.bg-circle-1 {
  width: 400px;
  height: 400px;
  background: #667eea;
  top: -100px;
  right: -100px;
}

.bg-circle-2 {
  width: 300px;
  height: 300px;
  background: #764ba2;
  top: 50px;
  left: -50px;
}

.bg-circle-3 {
  width: 200px;
  height: 200px;
  background: #f093fb;
  bottom: -50px;
  right: 30%;
}

/* 主内容 */
.main-content {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px 60px;
  min-height: calc(100vh - 500px);
}

/* Hero 区域 */
.hero-section {
  text-align: center;
  padding: 80px 0 60px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: rgba(102, 126, 234, 0.2);
  border: 1px solid rgba(102, 126, 234, 0.3);
  border-radius: 50px;
  color: #a8b4fc;
  font-size: 14px;
  margin-bottom: 24px;
  animation: fadeInDown 0.6s ease;
}

.badge-icon {
  font-size: 16px;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 16px;
  letter-spacing: 2px;
  animation: fadeInUp 0.6s ease 0.1s both;
}

.hero-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 40px;
  animation: fadeInUp 0.6s ease 0.2s both;
}

/* 搜索框 */
.search-wrapper {
  animation: fadeInUp 0.6s ease 0.3s both;
}

.search-input-group {
  display: flex;
  align-items: center;
  max-width: 800px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 12px;
  padding: 6px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  transition: box-shadow 0.3s ease;
}

.search-input-group:focus-within {
  box-shadow: 0 15px 50px rgba(102, 126, 234, 0.3);
}

.search-icon {
  padding-left: 16px;
  font-size: 20px;
  color: #909399;
}

.main-search-input {
  flex: 1;
}

.main-search-input >>> .el-input__inner {
  border: none;
  font-size: 16px;
  padding-left: 12px;
  background: transparent;
}

.main-search-btn {
  height: 48px;
  padding: 0 36px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.main-search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.search-tips {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.tips-label {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.hot-tag {
  padding: 6px 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.hot-tag:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  transform: translateY(-2px);
}

.search-actions {
  margin-top: 16px;
  text-align: center;
}

.search-actions >>> .el-button--text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.search-actions >>> .el-button--text:hover {
  color: #ffffff;
}

/* 统计卡片 */
.stats-section {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 40px;
  animation: fadeInUp 0.6s ease 0.4s both;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 30px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 24px;
}

.stat-icon-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
}

.stat-icon-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #ffffff;
}

.stat-icon-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #ffffff;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

/* 结果布局 - 左右分栏 */
.result-layout {
  display: flex;
  gap: 24px;
  animation: fadeIn 0.5s ease;
}

.result-left {
  flex: 1;
  min-width: 0;
}

.result-right {
  width: 380px;
  flex-shrink: 0;
}

/* 结果区域 */
.result-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 24px;
}

.result-count {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: #606266;
}

.count-icon {
  font-size: 20px;
  color: #67c23a;
}

.count-number {
  font-size: 22px;
  color: #667eea;
}

/* 专利列表 */
.patent-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.patent-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #fafbfc;
  border-radius: 12px;
  border: 1px solid transparent;
  transition: all 0.3s ease;
}

.patent-item:hover {
  background: #ffffff;
  border-color: #667eea;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
  transform: translateX(4px);
}

.patent-item-even {
  background: #f5f7fa;
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
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.patent-type {
  flex-shrink: 0;
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

.meta-label i {
  font-size: 12px;
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

.patent-actions >>> .el-button {
  color: #667eea;
}

.patent-actions >>> .el-button:hover {
  color: #764ba2;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.pagination-wrapper >>> .el-pagination {
  font-weight: 500;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled):hover {
  color: #667eea;
}

/* 图表区域 */
.charts-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-title i {
  color: #667eea;
}

.chart-container {
  width: 100%;
  height: 220px;
}

/* 加载状态 */
.loading-section {
  text-align: center;
  padding: 80px 0;
}

.loading-spinner {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 20px;
  animation: rotate 1s linear infinite;
}

.loading-section p {
  font-size: 16px;
  color: #909399;
}

/* 空状态 */
.empty-section {
  text-align: center;
  padding: 80px 0;
}

.empty-illustration {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-illustration i {
  font-size: 48px;
  color: #c0c4cc;
}

.empty-section h3 {
  font-size: 20px;
  color: #303133;
  margin: 0 0 8px;
}

.empty-section p {
  font-size: 14px;
  color: #909399;
  margin: 0 0 24px;
}

/* 初始特性 */
.init-section {
  padding: 40px 0;
}

.init-features {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.feature-card {
  text-align: center;
  padding: 40px 30px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
}

.feature-icon {
  width: 70px;
  height: 70px;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  font-size: 32px;
}

.feature-icon-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
}

.feature-icon-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #ffffff;
}

.feature-icon-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #ffffff;
}

.feature-card h4 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 8px;
}

.feature-card p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 1200px) {
  .result-layout {
    flex-direction: column;
  }

  .result-right {
    width: 100%;
  }

  .charts-section {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .chart-card {
    flex: 1;
    min-width: 300px;
  }
}

@media (max-width: 1024px) {
  .hero-title {
    font-size: 32px;
  }

  .stats-section {
    gap: 15px;
  }

  .stat-card {
    padding: 15px 20px;
  }

  .stat-value {
    font-size: 22px;
  }

  .patent-meta {
    grid-template-columns: repeat(2, 1fr);
  }

  .init-features {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 40px 0 30px;
  }

  .hero-title {
    font-size: 26px;
  }

  .hero-subtitle {
    font-size: 15px;
  }

  .search-input-group {
    flex-direction: column;
    padding: 10px;
  }

  .main-search-btn {
    width: 100%;
    height: 44px;
  }

  .stats-section {
    flex-direction: column;
    align-items: center;
    margin-top: 30px;
  }

  .stat-card {
    width: 100%;
    max-width: 300px;
  }

  .result-section {
    padding: 20px;
  }

  .result-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .patent-item {
    flex-direction: column;
  }

  .patent-index {
    width: 30px;
    height: 30px;
    font-size: 12px;
  }

  .chart-card {
    min-width: 100%;
  }
}
</style>
