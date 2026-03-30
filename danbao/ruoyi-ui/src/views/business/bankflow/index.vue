<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="对方户名" prop="oppositeName">
        <el-input v-model="queryParams.oppositeName" placeholder="请输入对方户名" clearable style="width: 200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="所属银行" prop="bankName">
        <el-select v-model="queryParams.bankName" placeholder="请选择所属银行" clearable style="width: 200px">
          <el-option v-for="item in bankOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="支付类型" prop="payType">
        <el-select v-model="queryParams.payType" placeholder="请选择支付类型" clearable style="width: 200px">
          <el-option label="支出" value="0" />
          <el-option label="收入" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮区域 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="bankFlowList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="对方户名" align="center" prop="oppositeName" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="交易金额" align="center" prop="tradeAmount" width="140">
        <template slot-scope="scope">
          <span>{{ formatMoney(scope.row.tradeAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="交易时间" align="center" prop="tradeTime" width="180" />
      <el-table-column label="所属银行" align="center" prop="bankName" width="120" />
      <el-table-column label="支付类型" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.payType === 0" type="danger">支出</el-tag>
          <el-tag v-else-if="scope.row.payType === 1" type="success">收入</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color: #f56c6c;" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加/编辑对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="对方户名" prop="oppositeName">
              <el-input v-model="form.oppositeName" placeholder="请输入对方户名" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="交易金额" prop="tradeAmount">
              <el-input-number v-model="form.tradeAmount" :precision="2" :min="0" :controls="false" placeholder="请输入交易金额" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交易时间" prop="tradeTime">
              <el-date-picker v-model="form.tradeTime" type="datetime" placeholder="选择交易时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属银行" prop="bankName">
              <el-select v-model="form.bankName" placeholder="请选择所属银行" style="width: 100%">
                <el-option v-for="item in bankOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付类型" prop="payType">
              <el-select v-model="form.payType" placeholder="请选择支付类型" style="width: 100%">
                <el-option label="支出" value="0" />
                <el-option label="收入" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBankFlow, getBankFlow, addBankFlow, updateBankFlow, delBankFlow, exportBankFlow } from "@/api/business/bankflow"

export default {
  name: 'BankFlow',
  data() {
    return {
      // 遮罩层
      loading: false,
      // 显示搜索条件
      showSearch: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 账单表格数据
      bankFlowList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 银行选项
      bankOptions: [
        { label: '工商银行', value: '工商银行' },
        { label: '建设银行', value: '建设银行' },
        { label: '农业银行', value: '农业银行' },
        { label: '中国银行', value: '中国银行' },
        { label: '交通银行', value: '交通银行' }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        oppositeName: '',
        bankName: '',
        payType: ''
      },
      // 表单参数
      form: {
        id: undefined,
        oppositeName: '',
        tradeAmount: undefined,
        tradeTime: '',
        bankName: '',
        payType: undefined
      },
      // 表单校验
      rules: {
        oppositeName: [
          { required: true, message: '对方户名不能为空', trigger: 'blur' }
        ],
        tradeAmount: [
          { required: true, message: '交易金额不能为空', trigger: 'blur' }
        ],
        tradeTime: [
          { required: true, message: '交易时间不能为空', trigger: 'change' }
        ],
        bankName: [
          { required: true, message: '所属银行不能为空', trigger: 'change' }
        ],
        payType: [
          { required: true, message: '支付类型不能为空', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询账单列表 */
    getList() {
      this.loading = true
      listBankFlow(this.queryParams).then(response => {
        this.bankFlowList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        oppositeName: '',
        bankName: '',
        payType: ''
      }
      this.handleQuery()
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增账单'
    },
    /** 编辑按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getBankFlow(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '编辑账单'
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids
      this.$confirm('是否确认删除选中的账单数据？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delBankFlow(ids).then(() => {
          this.getList()
          this.$message.success('删除成功')
        })
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/business/bankflow/export', {
        ...this.queryParams
      }, `账单台账_${new Date().getTime()}.xlsx`)
    },
    /** 提交表单 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id) {
            // 修改
            updateBankFlow(this.form).then(() => {
              this.$message.success('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            // 新增
            addBankFlow(this.form).then(() => {
              this.$message.success('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 取消按钮 */
    cancel() {
      this.open = false
      this.reset()
    },
    /** 表单重置 */
    reset() {
      this.form = {
        id: undefined,
        oppositeName: '',
        tradeAmount: undefined,
        tradeTime: '',
        bankName: '',
        payType: undefined
      }
      this.resetForm('form')
    },
    /** 格式化金额（千分位） */
    formatMoney(amount) {
      if (amount === undefined || amount === null) return '-'
      return '¥' + Number(amount).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }
  }
}
</script>

<style scoped>
.fee-calc-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
.fee-calc-tip i {
  margin-right: 4px;
  color: #409eff;
}
</style>
