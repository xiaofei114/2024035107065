<template>
  <div class="reconciliation-container">
    <!-- 统计面板 -->
    <div class="stats-panel">
      <div class="stat-item">
        <div class="stat-label">总对账数</div>
        <div class="stat-value">{{ totalCount }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">自动匹配</div>
        <div class="stat-value">{{ autoMatchedCount }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">待人工确认</div>
        <div class="stat-value">{{ needManualConfirmCount }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">人工确认</div>
        <div class="stat-value">{{ manualConfirmedCount }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">对账率</div>
        <div class="stat-value">{{ reconciliationRate }}%</div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-panel">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="债务人名称">
          <el-input v-model="queryParams.debtorName" placeholder="请输入债务人名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="对方户名">
          <el-input v-model="queryParams.oppositeName" placeholder="请输入对方户名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="所属银行">
          <el-select v-model="queryParams.bankName" placeholder="请选择银行" clearable style="width: 200px">
            <el-option label="工商银行" value="工商银行" />
            <el-option label="建设银行" value="建设银行" />
            <el-option label="农业银行" value="农业银行" />
            <el-option label="中国银行" value="中国银行" />
            <el-option label="交通银行" value="交通银行" />
          </el-select>
        </el-form-item>
        <el-form-item label="对账状态">
          <el-select v-model="queryParams.reconciliationStatus" placeholder="请选择对账状态" clearable style="width: 200px">
            <el-option label="未对账" value="未对账" />
            <el-option label="自动匹配" value="自动匹配" />
            <el-option label="待人工确认" value="待人工确认" />
            <el-option label="人工确认" value="人工确认" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="action-panel">
      <el-button type="primary" icon="el-icon-sync" @click="handleAutoReconciliation">批量自动对账</el-button>
      <el-button type="success" icon="el-icon-download" @click="handleExport">导出Excel</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="reconciliationList" style="width: 100%">
      <el-table-column label="序号" type="index" width="80" />
      <el-table-column label="对账状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.reconciliationStatus)">
            {{ scope.row.reconciliationStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="产品名称" min-width="150">
        <template slot-scope="scope">
          {{ scope.row.guaranteeFee ? scope.row.guaranteeFee.productName : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="债务人" min-width="150">
        <template slot-scope="scope">
          {{ scope.row.guaranteeFee ? scope.row.guaranteeFee.debtorName : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="银行" min-width="120">
        <template slot-scope="scope">
          {{ scope.row.guaranteeFee ? scope.row.guaranteeFee.bankName : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="担保费" min-width="120">
        <template slot-scope="scope">
          {{ scope.row.guaranteeFee ? '¥' + formatNumber(scope.row.guaranteeFee.guaranteeFee) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="对方户名" min-width="150">
        <template slot-scope="scope">
          {{ scope.row.bankFlow ? scope.row.bankFlow.oppositeName : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="交易金额" min-width="120">
        <template slot-scope="scope">
          {{ scope.row.bankFlow ? '¥' + formatNumber(scope.row.bankFlow.tradeAmount) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="对账类型" min-width="100">
        <template slot-scope="scope">
          {{ scope.row.reconciliationType || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作人" min-width="100">
        <template slot-scope="scope">
          {{ scope.row.operator || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button type="primary" size="small" @click="handleManualReconciliation(scope.row)" v-if="scope.row.reconciliationStatus === '未对账' || scope.row.reconciliationStatus === '待人工确认'">
            手动对账
          </el-button>
          <el-button type="danger" size="small" @click="handleUnbind(scope.row)" v-if="scope.row.reconciliationStatus !== '未对账'">
            解绑
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-show="total > 0"
        :total="total"
        :page-size="queryParams.pageSize"
        :current-page="queryParams.pageNum"
        :page-sizes="[20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 手动对账弹窗 -->
    <el-dialog title="手动对账" :visible.sync="manualReconciliationVisible" width="800px">
      <div class="manual-reconciliation-dialog">
        <!-- 担保业务信息 -->
        <div class="guarantee-info">
          <h3>担保业务信息</h3>
          <div v-if="selectedGuaranteeFee">
            <div>产品名称：{{ selectedGuaranteeFee.productName }}</div>
            <div>债务人：{{ selectedGuaranteeFee.debtorName }}</div>
            <div>银行：{{ selectedGuaranteeFee.bankName }}</div>
            <div>担保费：¥{{ formatNumber(selectedGuaranteeFee.guaranteeFee) }}</div>
            <div>债权时间：{{ formatDate(selectedGuaranteeFee.creditStartDate) }} 至 {{ formatDate(selectedGuaranteeFee.creditEndDate) }}</div>
          </div>
        </div>

        <!-- 银行流水列表 -->
        <div class="bank-flow-list">
          <h3>银行流水列表</h3>
          <el-form :inline="true" :model="bankFlowQuery" class="demo-form-inline">
            <el-form-item label="银行">
              <el-select v-model="bankFlowQuery.bankName" placeholder="请选择银行" clearable style="width: 150px">
                <el-option label="工商银行" value="工商银行" />
                <el-option label="建设银行" value="建设银行" />
                <el-option label="农业银行" value="农业银行" />
                <el-option label="中国银行" value="中国银行" />
                <el-option label="交通银行" value="交通银行" />
              </el-select>
            </el-form-item>
            <el-form-item label="金额范围">
              <el-input v-model="bankFlowQuery.minAmount" placeholder="最小金额" clearable style="width: 100px" />
              <span style="margin: 0 10px">至</span>
              <el-input v-model="bankFlowQuery.maxAmount" placeholder="最大金额" clearable style="width: 100px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="handleBankFlowQuery">搜索</el-button>
            </el-form-item>
          </el-form>
          <el-table ref="bankFlowTable" :data="bankFlowList" highlight-current-row @current-change="handleBankFlowSelect" style="width: 100%">
            <el-table-column label="对方户名" prop="oppositeName" />
            <el-table-column label="交易金额" prop="tradeAmount">
              <template slot-scope="scope">
                ¥{{ formatNumber(scope.row.tradeAmount) }}
              </template>
            </el-table-column>
            <el-table-column label="交易时间" prop="tradeTime">
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.tradeTime) }}
              </template>
            </el-table-column>
            <el-table-column label="银行" prop="bankName" />
            <el-table-column label="支付类型" prop="payType">
              <template slot-scope="scope">
                <el-tag :type="scope.row.payType === 1 ? 'success' : 'danger'">
                  {{ scope.row.payType === 1 ? '收入' : '支出' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div class="dialog-footer">
            <el-button @click="manualReconciliationVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmManualReconciliation">建立绑定</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listReconciliation, batchAutoReconciliation, manualReconciliation, unbindReconciliation, countReconciliationStatus } from "@/api/business/reconciliation"
import { listUnboundBankFlow } from "@/api/business/bankflow"

export default {
  name: 'ReconciliationList',
  data() {
    return {
      loading: false,
      reconciliationList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        debtorName: '',
        oppositeName: '',
        bankName: '',
        reconciliationStatus: ''
      },
      // 统计数据
      totalCount: 0,
      autoMatchedCount: 0,
      needManualConfirmCount: 0,
      manualConfirmedCount: 0,
      reconciliationRate: 0,
      // 手动对账弹窗
      manualReconciliationVisible: false,
      selectedGuaranteeFee: null,
      selectedBankFlow: null,
      bankFlowList: [],
      bankFlowQuery: {
        bankName: '',
        minAmount: '',
        maxAmount: ''
      }
    }
  },
  created() {
    this.getList()
    this.getStatusCount()
  },
  methods: {
    // 获取列表数据
    getList() {
      this.loading = true
      // 构建查询参数，将筛选条件放入params对象
      const queryData = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize,
        reconciliationStatus: this.queryParams.reconciliationStatus,
        params: {
          debtorName: this.queryParams.debtorName,
          oppositeName: this.queryParams.oppositeName,
          bankName: this.queryParams.bankName
        }
      }
      listReconciliation(queryData).then(response => {
        this.reconciliationList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 获取状态统计
    getStatusCount() {
      countReconciliationStatus().then(response => {
        const statusCount = response.data
        this.autoMatchedCount = statusCount['自动匹配'] || 0
        this.needManualConfirmCount = statusCount['待人工确认'] || 0
        this.manualConfirmedCount = statusCount['人工确认'] || 0
        this.totalCount = this.autoMatchedCount + this.needManualConfirmCount + this.manualConfirmedCount
        this.reconciliationRate = this.totalCount > 0 ? Math.round((this.autoMatchedCount + this.manualConfirmedCount) / this.totalCount * 100) : 0
      })
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        debtorName: '',
        oppositeName: '',
        bankName: '',
        reconciliationStatus: ''
      }
      this.getList()
    },
    // 批量自动对账
    handleAutoReconciliation() {
      this.$confirm('确定要执行批量自动对账吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        batchAutoReconciliation().then(response => {
          this.$message.success('自动对账完成')
          this.getList()
          this.getStatusCount()
        })
      })
    },
    // 手动对账
    handleManualReconciliation(row) {
      // 优先使用row中的担保业务信息
      if (row.guaranteeFee && row.guaranteeFee.id) {
        this.selectedGuaranteeFee = row.guaranteeFee
      } else if (row.guaranteeBusinessId) {
        // 如果没有guaranteeFee对象，但有guaranteeBusinessId，创建一个基础对象
        this.selectedGuaranteeFee = {
          id: row.guaranteeBusinessId,
          productName: row.guaranteeFee ? row.guaranteeFee.productName : '-',
          debtorName: row.guaranteeFee ? row.guaranteeFee.debtorName : '-',
          bankName: row.guaranteeFee ? row.guaranteeFee.bankName : '-',
          guaranteeFee: row.guaranteeFee ? row.guaranteeFee.guaranteeFee : 0
        }
      } else {
        this.$message.error('担保业务信息不完整，无法手动对账')
        return
      }
      this.manualReconciliationVisible = true
      this.getBankFlowList()
    },
    // 获取银行流水列表（只显示未被绑定的）
    getBankFlowList() {
      const params = {
        pageNum: 1,
        pageSize: 100,
        bankName: this.bankFlowQuery.bankName,
        params: {
          minAmount: this.bankFlowQuery.minAmount,
          maxAmount: this.bankFlowQuery.maxAmount
        }
      }
      listUnboundBankFlow(params).then(response => {
        this.bankFlowList = response.rows
      })
    },
    // 搜索银行流水
    handleBankFlowQuery() {
      this.getBankFlowList()
    },
    // 银行流水选择事件
    handleBankFlowSelect(val) {
      this.selectedBankFlow = val
    },
    // 确认手动对账
    confirmManualReconciliation() {
      if (!this.selectedBankFlow) {
        this.$message.error('请选择一条银行流水')
        return
      }
      if (!this.selectedGuaranteeFee || !this.selectedGuaranteeFee.id) {
        this.$message.error('担保业务信息不完整，请重新选择')
        return
      }
      const bankFlow = this.selectedBankFlow
      const guaranteeFee = this.selectedGuaranteeFee.guaranteeFee || 0
      if (bankFlow.tradeAmount < guaranteeFee) {
        this.$message.error('银行流水金额必须大于等于担保费')
        return
      }
      const data = {
        guaranteeBusinessId: this.selectedGuaranteeFee.id,
        bankTransactionId: bankFlow.id,
        operator: this.$store.state.user?.username || '系统用户'
      }
      console.log('手动对账请求参数：', data)
      manualReconciliation(data).then(response => {
        this.$message.success('手动对账成功')
        this.manualReconciliationVisible = false
        this.selectedBankFlow = null
        this.getList()
        this.getStatusCount()
      }).catch(error => {
        this.$message.error('手动对账失败：' + (error.message || '未知错误'))
      })
    },
    // 解绑对账
    handleUnbind(row) {
      this.$confirm('确定要解绑吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const data = {
          operator: this.$store.state.user?.username || '系统用户'
        }
        unbindReconciliation(row.id, data).then(response => {
          this.$message.success('解绑成功')
          this.getList()
          this.getStatusCount()
        })
      })
    },
    // 导出Excel
    handleExport() {
      this.$confirm('确定要导出当前筛选条件下的对账数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.download('/business/reconciliation/export', {
          ...this.queryParams
        }, `对账列表_${new Date().getTime()}.xlsx`)
      }).catch(() => {
        // 取消导出
      })
    },
    // 分页
    handleSizeChange(size) {
      this.queryParams.pageSize = size
      this.getList()
    },
    handleCurrentChange(current) {
      this.queryParams.pageNum = current
      this.getList()
    },
    // 工具方法
    getStatusType(status) {
      switch (status) {
        case '自动匹配': return 'success'
        case '待人工确认': return 'warning'
        case '人工确认': return 'primary'
        default: return 'info'
      }
    },
    formatNumber(value) {
      if (value === null || value === undefined) return '0'
      return Number(value).toLocaleString()
    },
    formatDate(value) {
      if (!value) return ''
      const date = new Date(value)
      return date.toISOString().split('T')[0]
    },
    formatDateTime(value) {
      if (!value) return ''
      const date = new Date(value)
      return date.toLocaleString()
    }
  }
}
</script>

<style scoped>
.reconciliation-container {
  padding: 20px;
}

.stats-panel {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.search-panel {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.action-panel {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.manual-reconciliation-dialog {
  max-height: 600px;
  overflow-y: auto;
}

.guarantee-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.guarantee-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: bold;
}

.bank-flow-list h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.dialog-footer {
  margin-top: 20px;
  text-align: right;
}
</style>
