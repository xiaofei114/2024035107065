<template>
  <div class="dashboard-container">
    <!-- 顶部标题栏 -->
    <div class="header">
      <h1>担保台账管理数据大屏</h1>
      <div class="time-display">{{ currentDateTime }}</div>
    </div>
    
    <!-- 统计卡片区域 -->
    <div class="stats-container">
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-number">{{ stats.totalBusiness }}</div>
        <div class="stat-title">总业务数</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📈</div>
        <div class="stat-number">{{ stats.monthlyNew }}</div>
        <div class="stat-title">本月新增</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-number">{{ stats.totalFee }}</div>
        <div class="stat-title">担保费总额</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💳</div>
        <div class="stat-number">{{ stats.totalBankFlow }}</div>
        <div class="stat-title">银行流水总额</div>
      </div>
    </div>
    
    <!-- 对账统计面板 -->
    <div class="stats-container">
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-number">{{ reconciliationStats.total }}</div>
        <div class="stat-title">总对账数</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-number">{{ reconciliationStats.autoMatched }}</div>
        <div class="stat-title">自动匹配</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">👤</div>
        <div class="stat-number">{{ reconciliationStats.manualConfirmed }}</div>
        <div class="stat-title">人工确认</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⚠️</div>
        <div class="stat-number">{{ reconciliationStats.needManualConfirm }}</div>
        <div class="stat-title">待人工确认</div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-container">
      <div class="chart-item">
        <div class="chart-title">银行分布</div>
        <div ref="pieChart" class="chart"></div>
      </div>
      <div class="chart-item">
        <div class="chart-title">月度趋势</div>
        <div ref="lineChart" class="chart"></div>
      </div>
      <div class="chart-item">
        <div class="chart-title">收支对比</div>
        <div ref="barChart" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getScreenStats, getGuaranteeByBank, getGuaranteeMonthly, getBankFlowMonthly } from "@/api/business/screen"
import { countReconciliationStatus } from "@/api/business/reconciliation"

export default {
  name: 'BusinessScreen',
  data() {
    return {
      currentDateTime: '',
      stats: {
        totalBusiness: '0',
        monthlyNew: '0',
        totalFee: '¥0',
        totalBankFlow: '¥0'
      },
      reconciliationStats: {
        total: '0',
        autoMatched: '0',
        manualConfirmed: '0',
        needManualConfirm: '0'
      },
      pieChart: null,
      lineChart: null,
      barChart: null,
      timeInterval: null
    }
  },
  mounted() {
    this.updateDateTime()
    this.timeInterval = setInterval(() => {
      this.updateDateTime()
    }, 1000)
    
    this.loadData()
    window.addEventListener('resize', this.handleResize)
  },
  methods: {
    async loadData() {
      // 加载统计数据
      await this.loadStats()
      // 加载对账统计数据
      await this.loadReconciliationStats()
      // 加载图表数据
      await this.loadChartData()
    },
    async loadStats() {
      try {
        const response = await getScreenStats()
        if (response.code === 200) {
          const data = response.data
          this.stats = {
            totalBusiness: data.totalBusiness.toString(),
            monthlyNew: data.monthlyNew.toString(),
            totalFee: '¥' + (data.totalFee || 0).toLocaleString(),
            totalBankFlow: '¥' + (data.totalBankFlow || 0).toLocaleString()
          }
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },
    async loadReconciliationStats() {
      try {
        const response = await countReconciliationStatus()
        if (response.code === 200) {
          const data = response.data
          const total = (data['自动匹配'] || 0) + (data['待人工确认'] || 0) + (data['人工确认'] || 0)
          this.reconciliationStats = {
            total: total.toString(),
            autoMatched: (data['自动匹配'] || 0).toString(),
            manualConfirmed: (data['人工确认'] || 0).toString(),
            needManualConfirm: (data['待人工确认'] || 0).toString()
          }
        }
      } catch (error) {
        console.error('加载对账统计数据失败:', error)
      }
    },
    async loadChartData() {
      // 初始化图表实例
      this.initChartInstances()
      // 加载饼图数据
      await this.loadPieChartData()
      // 加载折线图数据
      await this.loadLineChartData()
      // 加载柱状图数据
      await this.loadBarChartData()
    },
    initChartInstances() {
      if (!this.pieChart) {
        this.pieChart = echarts.init(this.$refs.pieChart)
      }
      if (!this.lineChart) {
        this.lineChart = echarts.init(this.$refs.lineChart)
      }
      if (!this.barChart) {
        this.barChart = echarts.init(this.$refs.barChart)
      }
    },
    async loadPieChartData() {
      try {
        const response = await getGuaranteeByBank()
        if (response.code === 200) {
          const data = response.data || []
          const option = {
            tooltip: {
              trigger: 'item',
              formatter: '{a} <br/>{b}: {c} ({d}%)'
            },
            legend: {
              orient: 'vertical',
              left: 'left',
              textStyle: {
                color: '#fff'
              }
            },
            series: [
              {
                name: '银行分布',
                type: 'pie',
                radius: '50%',
                data: data,
                emphasis: {
                  itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 212, 255, 0.5)'
                  }
                },
                itemStyle: {
                  color: function(params) {
                    const colors = ['#00d4ff', '#00a8ff', '#0072ff', '#0047ff', '#001cff']
                    return colors[params.dataIndex % colors.length]
                  }
                }
              }
            ]
          }
          this.pieChart.setOption(option)
        }
      } catch (error) {
        console.error('加载银行分布数据失败:', error)
      }
    },
    async loadLineChartData() {
      try {
        const response = await getGuaranteeMonthly()
        if (response.code === 200) {
          const data = response.data || []
          const months = data.map(item => item.month)
          const amounts = data.map(item => item.amount || 0)
          const option = {
            tooltip: {
              trigger: 'axis'
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              boundaryGap: false,
              data: months,
              axisLine: {
                lineStyle: {
                  color: 'rgba(255, 255, 255, 0.3)'
                }
              },
              axisLabel: {
                color: 'rgba(255, 255, 255, 0.7)'
              }
            },
            yAxis: {
              type: 'value',
              axisLine: {
                lineStyle: {
                  color: 'rgba(255, 255, 255, 0.3)'
                }
              },
              axisLabel: {
                color: 'rgba(255, 255, 255, 0.7)'
              },
              splitLine: {
                lineStyle: {
                  color: 'rgba(255, 255, 255, 0.1)'
                }
              }
            },
            series: [
              {
                name: '担保费',
                type: 'line',
                stack: 'Total',
                data: amounts,
                lineStyle: {
                  color: '#00d4ff'
                },
                areaStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgba(0, 212, 255, 0.3)' },
                    { offset: 1, color: 'rgba(0, 212, 255, 0.1)' }
                  ])
                }
              }
            ]
          }
          this.lineChart.setOption(option)
        }
      } catch (error) {
        console.error('加载月度担保费数据失败:', error)
      }
    },
    async loadBarChartData() {
      try {
        const response = await getBankFlowMonthly()
        if (response.code === 200) {
          const data = response.data || []
          const months = data.map(item => item.month)
          const income = data.map(item => item.income || 0)
          const expense = data.map(item => item.expense || 0)
          const option = {
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
              }
            },
            legend: {
              data: ['收入', '支出'],
              textStyle: {
                color: '#fff'
              }
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              data: months,
              axisLine: {
                lineStyle: {
                  color: 'rgba(255, 255, 255, 0.3)'
                }
              },
              axisLabel: {
                color: 'rgba(255, 255, 255, 0.7)'
              }
            },
            yAxis: {
              type: 'value',
              axisLine: {
                lineStyle: {
                  color: 'rgba(255, 255, 255, 0.3)'
                }
              },
              axisLabel: {
                color: 'rgba(255, 255, 255, 0.7)'
              },
              splitLine: {
                lineStyle: {
                  color: 'rgba(255, 255, 255, 0.1)'
                }
              }
            },
            series: [
              {
                name: '收入',
                type: 'bar',
                data: income,
                itemStyle: {
                  color: '#00d4ff'
                }
              },
              {
                name: '支出',
                type: 'bar',
                data: expense,
                itemStyle: {
                  color: '#ff4d4f'
                }
              }
            ]
          }
          this.barChart.setOption(option)
        }
      } catch (error) {
        console.error('加载月度收支数据失败:', error)
      }
    },
    updateDateTime() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      this.currentDateTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    handleResize() {
      if (this.pieChart) {
        this.pieChart.resize()
      }
      if (this.lineChart) {
        this.lineChart.resize()
      }
      if (this.barChart) {
        this.barChart.resize()
      }
    }
  },
  beforeDestroy() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval)
    }
    if (this.pieChart) {
      this.pieChart.dispose()
    }
    if (this.lineChart) {
      this.lineChart.dispose()
    }
    if (this.barChart) {
      this.barChart.dispose()
    }
    window.removeEventListener('resize', this.handleResize)
  }
}
</script>

<style scoped>
.dashboard-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: #0d1b2a;
  color: #fff;
  overflow: hidden;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  z-index: 9999;
  display: flex;
  flex-direction: column;
}

.header {
  height: 80px;
  background: linear-gradient(135deg, #0a1a2a 0%, #0d1b2a 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
  box-shadow: 0 2px 10px rgba(0, 212, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #fff;
  text-align: center;
  flex: 1;
}

.time-display {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  font-family: monospace;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  padding: 20px 30px;
  max-height: 200px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 20px 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  min-height: 120px;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #00d4ff, #00a8ff);
}

.stat-card:hover {
  box-shadow: 0 6px 30px rgba(0, 212, 255, 0.2);
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 28px;
  margin-bottom: 10px;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #00d4ff;
  margin-bottom: 8px;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
  text-align: center;
  word-wrap: break-word;
  max-width: 100%;
}

.stat-title {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
  word-wrap: break-word;
  max-width: 100%;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding: 0 30px 30px;
  flex: 1;
  min-height: 300px;
}

.chart-item {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
  color: rgba(255, 255, 255, 0.9);
  text-align: center;
}

.chart {
  flex: 1;
  min-height: 250px;
  width: 100%;
  height: calc(100% - 40px);
}

@media (max-width: 1200px) {
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-container {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .chart-item {
    height: 400px;
  }
}
</style>