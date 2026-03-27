<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="产品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable style="width: 200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="所属银行" prop="bankName">
        <el-select v-model="queryParams.bankName" placeholder="请选择所属银行" clearable style="width: 200px">
          <el-option v-for="item in bankOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="债务人" prop="debtorName">
        <el-input v-model="queryParams.debtorName" placeholder="请输入债务人名称" clearable style="width: 200px" @keyup.enter.native="handleQuery" />
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
    <el-table v-loading="loading" :data="guaranteeList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="产品名称" align="center" prop="productName" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="所属银行" align="center" prop="bankName" width="120" />
      <el-table-column label="债务人名称" align="center" prop="debtorName" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="债务人证件号" align="center" prop="debtorIdCard" width="160" />
      <el-table-column label="主债权起始" align="center" prop="creditStartDate" width="100" />
      <el-table-column label="主债权到期" align="center" prop="creditEndDate" width="100" />
      <el-table-column label="主债权金额" align="center" prop="creditAmount" width="140">
        <template slot-scope="scope">
          <span>{{ formatMoney(scope.row.creditAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="担保费率" align="center" prop="guaranteeFeeRate" width="100">
        <template slot-scope="scope">
          <span>{{ (scope.row.guaranteeFeeRate * 100).toFixed(2) }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="担保费金额" align="center" prop="guaranteeFee" width="140">
        <template slot-scope="scope">
          <span style="color: #f56c6c; font-weight: bold;">{{ formatMoney(scope.row.guaranteeFee) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
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
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品名称" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入产品名称" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属银行" prop="bankName">
              <el-select v-model="form.bankName" placeholder="请选择所属银行" style="width: 100%">
                <el-option v-for="item in bankOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="债务人名称" prop="debtorName">
              <el-input v-model="form.debtorName" placeholder="请输入债务人名称" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="债务人证件号">
              <el-input v-model="form.debtorIdCard" placeholder="请输入债务人证件号" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主债权起始" prop="creditStartDate">
              <el-date-picker v-model="form.creditStartDate" type="date" placeholder="选择起始日期" value-format="yyyy-MM-dd" style="width: 100%" @change="calculateGuaranteeFee" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主债权到期" prop="creditEndDate">
              <el-date-picker v-model="form.creditEndDate" type="date" placeholder="选择到期日期" value-format="yyyy-MM-dd" style="width: 100%" @change="calculateGuaranteeFee" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主债权金额" prop="creditAmount">
              <el-input-number v-model="form.creditAmount" :precision="2" :min="0" :controls="false" placeholder="请输入主债权金额" style="width: 100%" @change="calculateGuaranteeFee" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="担保费费率" prop="guaranteeFeeRate">
              <el-input-number v-model="form.guaranteeFeeRate" :precision="4" :min="0" :max="1" :step="0.001" :controls="false" placeholder="请输入费率，如0.015" style="width: 100%" @change="calculateGuaranteeFee">
                <template slot="append">%</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="担保费金额">
              <el-input v-model="displayGuaranteeFee" readonly style="width: 100%">
                <template slot="append">元</template>
              </el-input>
              <div class="fee-calc-tip" v-if="form.guaranteeFee && form.guaranteeFee > 0">
                <i class="el-icon-info"></i>
                计算公式：{{ formatMoney(form.creditAmount) }} × {{ (form.guaranteeFeeRate * 100).toFixed(2) }}% × {{ calculateDays() }}天 / 365
              </div>
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
import { listGuarantee, getGuarantee, addGuarantee, updateGuarantee, delGuarantee, exportGuarantee } from "@/api/business/guarantee"

export default {
  name: 'Guarantee',
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
      // 担保业务表格数据
      guaranteeList: [],
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
        productName: '',
        bankName: '',
        debtorName: ''
      },
      // 表单参数
      form: {
        id: undefined,
        productName: '',
        bankName: '',
        debtorName: '',
        debtorIdCard: '',
        creditStartDate: '',
        creditEndDate: '',
        creditAmount: undefined,
        guaranteeFeeRate: undefined,
        guaranteeFee: undefined
      },
      // 表单校验
      rules: {
        productName: [
          { required: true, message: '产品名称不能为空', trigger: 'blur' }
        ],
        bankName: [
          { required: true, message: '所属银行不能为空', trigger: 'change' }
        ],
        debtorName: [
          { required: true, message: '债务人名称不能为空', trigger: 'blur' }
        ],
        creditStartDate: [
          { required: true, message: '主债权起始时间不能为空', trigger: 'change' }
        ],
        creditEndDate: [
          { required: true, message: '主债权到期时间不能为空', trigger: 'change' },
          {
            validator: (rule, value, callback) => {
              if (value && this.form.creditStartDate && value < this.form.creditStartDate) {
                callback(new Error('到期时间不能早于起始时间'))
              } else {
                callback()
              }
            },
            trigger: 'change'
          }
        ],
        creditAmount: [
          { required: true, message: '主债权金额不能为空', trigger: 'blur' }
        ],
        guaranteeFeeRate: [
          { required: true, message: '担保费费率不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    // 显示的担保费金额（格式化）
    displayGuaranteeFee() {
      if (this.form.guaranteeFee) {
        return this.formatMoney(this.form.guaranteeFee)
      }
      return ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询担保业务列表 */
    getList() {
      this.loading = true
      listGuarantee(this.queryParams).then(response => {
        this.guaranteeList = response.rows
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
        productName: '',
        bankName: '',
        debtorName: ''
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
      this.title = '新增担保业务'
    },
    /** 编辑按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getGuarantee(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '编辑担保业务'
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids
      this.$confirm('是否确认删除选中的担保业务数据？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delGuarantee(ids).then(() => {
          this.getList()
          this.$message.success('删除成功')
        })
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/business/guarantee/export', {
        ...this.queryParams
      }, `担保业务台账_${new Date().getTime()}.xlsx`)
    },
    /** 提交表单 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          // 确保担保费已计算
          this.calculateGuaranteeFee()
          
          if (this.form.id) {
            // 修改
            updateGuarantee(this.form).then(() => {
              this.$message.success('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            // 新增
            addGuarantee(this.form).then(() => {
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
        productName: '',
        bankName: '',
        debtorName: '',
        debtorIdCard: '',
        creditStartDate: '',
        creditEndDate: '',
        creditAmount: undefined,
        guaranteeFeeRate: undefined,
        guaranteeFee: undefined
      }
      this.resetForm('form')
    },
    /** 计算担保费 */
    calculateGuaranteeFee() {
      const { creditAmount, guaranteeFeeRate, creditStartDate, creditEndDate } = this.form
      
      if (creditAmount && guaranteeFeeRate && creditStartDate && creditEndDate) {
        const days = this.calculateDays()
        if (days > 0) {
          // 担保费 = 主债权金额 × 担保费费率 × 天数 / 365
          const fee = creditAmount * guaranteeFeeRate * days / 365
          this.form.guaranteeFee = Math.round(fee * 100) / 100
        }
      }
    },
    /** 计算天数 */
    calculateDays() {
      if (this.form.creditStartDate && this.form.creditEndDate) {
        const start = new Date(this.form.creditStartDate)
        const end = new Date(this.form.creditEndDate)
        const diffTime = end - start
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
        return diffDays > 0 ? diffDays : 0
      }
      return 0
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
