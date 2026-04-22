<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="专利标题" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="请输入专利标题" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImport">导入</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="patentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="专利标题" align="left" prop="title" :show-overflow-tooltip="true" min-width="200">
        <template slot-scope="scope">
          <span v-html="scope.row.title"></span>
        </template>
      </el-table-column>
      <el-table-column label="申请号" align="center" prop="applicationNo" width="150" />
      <el-table-column label="申请人" align="center" prop="applicant" width="150" :show-overflow-tooltip="true" />
      <el-table-column label="申请日期" align="center" prop="applicationDate" width="120" />
      <el-table-column label="专利类型" align="center" prop="typeName" width="100" />
      <el-table-column label="法律状态" align="center" prop="legalType" width="100" />
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="专利标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入专利标题" maxlength="500" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请号" prop="applicationNo">
              <el-input v-model="form.applicationNo" placeholder="请输入申请号" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="公开号" prop="publicationNo">
              <el-input v-model="form.publicationNo" placeholder="请输入公开号" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请日期" prop="applicationDate">
              <el-date-picker v-model="form.applicationDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicant">
              <el-input v-model="form.applicant" placeholder="请输入申请人" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发明人" prop="inventor">
              <el-input v-model="form.inventor" placeholder="请输入发明人" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="专利类型" prop="typeName">
              <el-select v-model="form.typeName" placeholder="请选择专利类型" style="width: 100%;">
                <el-option label="发明专利" value="发明专利" />
                <el-option label="实用新型" value="实用新型" />
                <el-option label="外观设计" value="外观设计" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="法律状态" prop="legalType">
              <el-select v-model="form.legalType" placeholder="请选择法律状态" style="width: 100%;">
                <el-option label="有效" value="有效" />
                <el-option label="无效" value="无效" />
                <el-option label="审中" value="审中" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="分类号" prop="classification">
              <el-input v-model="form.classification" placeholder="请输入分类号" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主分类号" prop="mainClassification">
              <el-input v-model="form.mainClassification" placeholder="请输入主分类号" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="专利代理机构" prop="patentAgency">
              <el-input v-model="form.patentAgency" placeholder="请输入专利代理机构" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入地址" maxlength="500" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="摘要" prop="abs">
              <el-input v-model="form.abs" type="textarea" :rows="4" placeholder="请输入专利摘要" maxlength="4000" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="权力要求书" prop="patClaim">
              <div class="editor-container">
                <quill-editor
                  ref="quillEditor"
                  v-model="form.patClaim"
                  :options="editorOptions"
                  class="rich-editor"
                />
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="来源" prop="source">
              <el-input v-model="form.source" placeholder="请输入来源" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="专利详情" :visible.sync="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border class="patent-detail-descriptions">
        <el-descriptions-item label="专利标题" :span="2">{{ viewForm.title }}</el-descriptions-item>
        <el-descriptions-item label="申请号">{{ viewForm.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="公开号">{{ viewForm.publicationNo }}</el-descriptions-item>
        <el-descriptions-item label="申请日期">{{ viewForm.applicationDate }}</el-descriptions-item>
        <el-descriptions-item label="专利类型">{{ viewForm.typeName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ viewForm.applicant }}</el-descriptions-item>
        <el-descriptions-item label="发明人">{{ viewForm.inventor }}</el-descriptions-item>
        <el-descriptions-item label="法律状态">{{ viewForm.legalType }}</el-descriptions-item>
        <el-descriptions-item label="分类号">{{ viewForm.classification }}</el-descriptions-item>
        <el-descriptions-item label="主分类号">{{ viewForm.mainClassification }}</el-descriptions-item>
        <el-descriptions-item label="专利代理机构" :span="2">{{ viewForm.patentAgency }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ viewForm.address }}</el-descriptions-item>
        <el-descriptions-item label="来源" :span="2">{{ viewForm.source }}</el-descriptions-item>
        <el-descriptions-item label="摘要" :span="2">
          <div class="content-text">{{ viewForm.abs }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="权力要求书" :span="2">
          <div class="content-text patent-claim" v-html="viewForm.patClaim"></div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog :title="'数据导入'" :visible.sync="importDialogVisible" width="800px" append-to-body :close-on-click-modal="false">
      <div class="import-container">
        <!-- 模板下载 -->
        <div class="import-section">
          <h4>第一步：下载导入模板</h4>
          <p class="import-tip">请下载导入模板，按照模板格式填写数据</p>
          <el-button type="primary" plain icon="el-icon-download" @click="handleDownloadTemplate">下载导入模板</el-button>
        </div>

        <!-- 文件上传 -->
        <div class="import-section">
          <h4>第二步：上传Excel文件</h4>
          <p class="import-tip">支持 .xlsx 格式，文件大小不超过 10MB</p>
          <el-upload
            ref="upload"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :show-file-list="true"
            :limit="1"
            accept=".xlsx,.xls"
          >
            <el-button slot="trigger" type="primary" icon="el-icon-upload">选择文件</el-button>
          </el-upload>
        </div>

        <!-- 数据预览 -->
        <div class="import-section" v-if="importPreviewData.length > 0">
          <h4>数据预览（共 {{ importTotal }} 条，显示前 {{ importPreviewData.length }} 条）</h4>
          <el-table :data="importPreviewData" border size="small" max-height="300">
            <el-table-column prop="id" label="ID" width="100" show-overflow-tooltip />
            <el-table-column prop="title" label="专利标题" min-width="150" show-overflow-tooltip />
            <el-table-column prop="applicationNo" label="申请号" width="120" />
            <el-table-column prop="applicant" label="申请人" width="100" />
            <el-table-column prop="typeName" label="专利类型" width="80" />
          </el-table>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeImportDialog">取 消</el-button>
        <el-button type="primary" @click="confirmImport" :loading="isImporting" :disabled="importPreviewData.length === 0">
          {{ isImporting ? '导入中...' : '确认导入' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPatent, getPatent, addPatent, updatePatent, delPatent, delBatchPatent, exportPatent, importPatent, importPreview, downloadTemplate } from '@/api/patent'
import { quillEditor } from 'vue-quill-editor'
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

export default {
  name: 'Patent',
  components: {
    quillEditor
  },
  data() {
    return {
      // 遮罩层
      loading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 专利表格数据
      patentList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 是否显示查看弹出层
      viewOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        keyword: null
      },
      // 表单参数
      form: {
        id: undefined,
        title: undefined,
        applicationNo: undefined,
        publicationNo: undefined,
        applicationDate: undefined,
        applicant: undefined,
        inventor: undefined,
        typeName: undefined,
        legalType: undefined,
        classification: undefined,
        mainClassification: undefined,
        patentAgency: undefined,
        address: undefined,
        abs: undefined,
        patClaim: undefined,
        source: undefined
      },
      // 查看表单
      viewForm: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: '专利标题不能为空', trigger: 'blur' }
        ],
        applicationNo: [
          { required: true, message: '申请号不能为空', trigger: 'blur' }
        ]
      },
      // 富文本编辑器配置
      editorOptions: {
        placeholder: '请输入权力要求书内容...',
        theme: 'snow',
        modules: {
          toolbar: [
            ['bold', 'italic', 'underline', 'strike'],
            ['blockquote', 'code-block'],
            [{ 'header': 1 }, { 'header': 2 }],
            [{ 'list': 'ordered' }, { 'list': 'bullet' }],
            [{ 'script': 'sub' }, { 'script': 'super' }],
            [{ 'indent': '-1' }, { 'indent': '+1' }],
            [{ 'direction': 'rtl' }],
            [{ 'size': ['small', false, 'large', 'huge'] }],
            [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
            [{ 'color': [] }, { 'background': [] }],
            [{ 'font': [] }],
            [{ 'align': [] }],
            ['clean'],
            ['link']
          ]
        }
      },
      // 导入相关
      importDialogVisible: false,
      importFile: null,
      importPreviewData: [],
      importTotal: 0,
      isImporting: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询专利列表 */
    getList() {
      this.loading = true
      listPatent(this.queryParams).then(response => {
        this.patentList = response.data.list
        this.total = response.data.total
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
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        title: undefined,
        applicationNo: undefined,
        publicationNo: undefined,
        applicationDate: undefined,
        applicant: undefined,
        inventor: undefined,
        typeName: undefined,
        legalType: undefined,
        classification: undefined,
        mainClassification: undefined,
        patentAgency: undefined,
        address: undefined,
        abs: undefined,
        patClaim: undefined,
        source: undefined
      }
      this.resetForm('form')
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加专利'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      if (Array.isArray(id)) {
        this.msgError('请选择单条数据修改')
        return
      }
      getPatent(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改专利'
      })
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.viewForm = row
      this.viewOpen = true
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id !== undefined) {
            updatePatent(this.form).then(response => {
              if (response.code === 200) {
                this.$modal.msgSuccess('修改成功')
                this.open = false
                this.getList()
              } else {
                this.$modal.msgError(response.msg || '修改失败')
              }
            }).catch(error => {
              this.$modal.msgError('修改失败：' + (error.message || '未知错误'))
            })
          } else {
            addPatent(this.form).then(response => {
              if (response.code === 200) {
                this.$modal.msgSuccess('新增成功')
                this.open = false
                this.getList()
              } else {
                this.$modal.msgError(response.msg || '新增失败')
              }
            }).catch(error => {
              this.$modal.msgError('新增失败：' + (error.message || '未知错误'))
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
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      const that = this
      this.$confirm('是否确认删除选中的专利数据?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(function() {
        if (Array.isArray(ids)) {
          return delBatchPatent(ids)
        } else {
          return delPatent(ids)
        }
      }).then(response => {
        if (response.code === 200) {
          that.getList()
          that.$modal.msgSuccess('删除成功')
        } else {
          that.$modal.msgError(response.msg || '删除失败')
        }
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      const that = this
      this.$confirm('是否确认导出所有专利数据?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(function() {
        that.$modal.loading('正在导出数据，请稍候...')
        return exportPatent(that.queryParams.keyword)
      }).then(response => {
        that.downloadFile(response, '专利数据_' + new Date().getTime() + '.xlsx')
        that.$modal.closeLoading()
        that.$modal.msgSuccess('导出成功')
      }).catch(error => {
        that.$modal.closeLoading()
        if (error) {
          that.$modal.msgError('导出失败：' + (error.message || '未知错误'))
        }
      })
    },
    /** 下载文件 */
    downloadFile(data, filename) {
      const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.importDialogVisible = true
      this.importFile = null
      this.importPreviewData = []
      this.importTotal = 0
    },
    /** 下载导入模板 */
    handleDownloadTemplate() {
      const that = this
      this.$modal.loading('正在下载模板，请稍候...')
      downloadTemplate().then(response => {
        that.downloadFile(response, '专利导入模板.xlsx')
        that.$modal.closeLoading()
        that.$modal.msgSuccess('模板下载成功')
      }).catch(error => {
        that.$modal.closeLoading()
        that.$modal.msgError('模板下载失败：' + (error.message || '未知错误'))
      })
    },
    /** 文件选择变化 */
    handleFileChange(file) {
      this.importFile = file.raw
      this.previewImport()
    },
    /** 预览导入数据 */
    previewImport() {
      if (!this.importFile) {
        this.$modal.msgWarning('请选择要导入的文件')
        return
      }
      const that = this
      this.$modal.loading('正在解析文件，请稍候...')
      importPreview(this.importFile).then(response => {
        that.$modal.closeLoading()
        if (response.code === 200) {
          that.importPreviewData = response.data.preview
          that.importTotal = response.data.total
          that.$modal.msgSuccess('文件解析成功，共 ' + that.importTotal + ' 条数据')
        } else {
          that.$modal.msgError(response.msg || '文件解析失败')
        }
      }).catch(error => {
        that.$modal.closeLoading()
        that.$modal.msgError('文件解析失败：' + (error.message || '未知错误'))
      })
    },
    /** 确认导入 */
    confirmImport() {
      if (!this.importFile) {
        this.$modal.msgWarning('请选择要导入的文件')
        return
      }
      const that = this
      this.$confirm('确认导入 ' + this.importTotal + ' 条数据到Solr?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(function() {
        that.isImporting = true
        that.$modal.loading('正在导入数据，请稍候...')
        return importPatent(that.importFile)
      }).then(response => {
        that.isImporting = false
        that.$modal.closeLoading()
        if (response.code === 200) {
          that.importDialogVisible = false
          that.$modal.msgSuccess(response.msg || '导入成功')
          that.getList()
        } else {
          that.$modal.msgError(response.msg || '导入失败')
        }
      }).catch(error => {
        that.isImporting = false
        that.$modal.closeLoading()
        that.$modal.msgError('导入失败：' + (error.message || '未知错误'))
      })
    },
    /** 关闭导入对话框 */
    closeImportDialog() {
      this.importDialogVisible = false
      this.importFile = null
      this.importPreviewData = []
      this.importTotal = 0
    }
  }
}
</script>

<style scoped>
.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
.rich-editor {
  height: 300px;
}
.rich-editor >>> .ql-editor {
  min-height: 250px;
}
.content-text {
  white-space: pre-wrap;
  line-height: 1.8;
  max-height: 300px;
  overflow-y: auto;
  word-break: break-all;
}

/* 查看详情对话框样式 */
>>> .patent-detail-descriptions .el-descriptions-item__content {
  word-break: break-all;
  white-space: normal;
}
>>> .patent-detail-descriptions .el-descriptions-item__label {
  white-space: nowrap;
}
>>> .patent-claim {
  max-height: 400px;
  overflow-y: auto;
}

/* 导入对话框样式 */
.import-container {
  padding: 10px 0;
}
.import-section {
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px dashed #e0e0e0;
}
.import-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}
.import-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 15px;
}
.import-tip {
  margin: 0 0 15px 0;
  color: #909399;
  font-size: 13px;
}
</style>
