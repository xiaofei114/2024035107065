<template>
  <div class="student-management">
    <!-- 顶部标题 -->
    <h1 class="page-title">学生管理</h1>

    <!-- 搜索和操作栏 -->
    <div class="action-bar">
      <!-- 搜索输入框，支持按姓名搜索 -->
      <!-- v-model实现双向绑定：将输入框的值与searchName变量同步 -->
      <!-- 当用户输入时，searchName自动更新；当searchName变化时，输入框显示相应内容 -->
      <el-input
        v-model="searchName"
        placeholder="请输入学生姓名"
        :prefix-icon="Search"
        style="width: 300px; margin-right: 20px;"
        clearable
      />
      <!-- 添加学生按钮 -->
      <el-button type="primary" :icon="Plus" @click="handleAddStudent">
        添加学生
      </el-button>
    </div>

    <!-- 学生表格 -->
    <el-table :data="filteredStudents" border style="width: 100%; margin-top: 30px;">
      <el-table-column prop="id" label="学号" align="center" />
      <el-table-column prop="name" label="姓名" align="center" />
      <el-table-column prop="gender" label="性别" align="center">
        <template #default="{ row }">
          {{ row.gender === 'male' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="className" label="班级" align="center" />
      <el-table-column prop="score" label="成绩" align="center" />
      <el-table-column label="操作" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 统计信息卡片 -->
    <el-row :gutter="20" style="margin-top: 40px;">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-title">学生总数</div>
            <div class="stat-value">{{ totalStudents }}<span class="stat-unit">人</span></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-title">平均成绩</div>
            <div class="stat-value">{{ averageScore }}<span class="stat-unit">分</span></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-title">最高分</div>
            <div class="stat-value">{{ highestScore }}<span class="stat-unit">分</span></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
// 导入Element Plus图标
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'

// 搜索关键字
// 使用ref创建响应式数据，v-model将输入框的值双向绑定到此变量
// 双向绑定意味着：输入框的值变化会更新searchName，searchName变化也会更新输入框显示
const searchName = ref('')

// 示例学生数据
const students = ref([
  { id: '20230001', name: '张三', gender: 'male', className: '计算机科学与技术1班', score: 85 },
  { id: '20230002', name: '李四', gender: 'female', className: '软件工程2班', score: 92 },
  { id: '20230003', name: '王五', gender: 'male', className: '网络工程3班', score: 78 },
  { id: '20230004', name: '赵六', gender: 'female', className: '信息安全4班', score: 88 },
  { id: '20230005', name: '钱七', gender: 'male', className: '人工智能5班', score: 95 }
])

// 计算属性：根据搜索关键字实时过滤学生列表
// computed计算属性会自动追踪依赖（searchName），当searchName变化时自动重新计算
// 实现实时搜索：输入框内容变化 → searchName更新 → filteredStudents自动重新计算 → 表格数据更新
const filteredStudents = computed(() => {
  // 如果搜索框为空或只有空格，返回所有学生
  if (!searchName.value.trim()) {
    return students.value
  }
  // 否则，过滤出姓名包含搜索关键词的学生
  // 搜索不区分大小写，使用includes进行模糊匹配
  return students.value.filter(student =>
    student.name.includes(searchName.value.trim())
  )
})


// 计算属性：统计学生总数（基于过滤后的学生列表）
const totalStudents = computed(() => {
  return filteredStudents.value.length
})

// 计算属性：计算平均成绩（基于过滤后的学生列表）
const averageScore = computed(() => {
  if (filteredStudents.value.length === 0) return 0
  const total = filteredStudents.value.reduce((sum, student) => sum + student.score, 0)
  return Math.round(total / filteredStudents.value.length)
})

// 计算属性：获取最高分（基于过滤后的学生列表）
const highestScore = computed(() => {
  if (filteredStudents.value.length === 0) return 0
  return Math.max(...filteredStudents.value.map(student => student.score))
})

// 添加学生处理函数
const handleAddStudent = () => {
  // 实际项目中此处应弹出表单对话框
  alert('添加学生功能待实现')
}

// 编辑学生处理函数
const handleEdit = (row) => {
  // 实际项目中此处应弹出编辑表单
  alert(`编辑学生：${row.name}`)
}

// 删除学生处理函数
const handleDelete = (row) => {
  // 实际项目中此处应有确认对话框
  if (confirm(`确定删除学生 ${row.name} 吗？`)) {
    const index = students.value.findIndex(student => student.id === row.id)
    if (index !== -1) {
      students.value.splice(index, 1)
    }
  }
}
</script>

<style scoped>
.student-management {
  padding: 30px;
  background-color: #f8fafc;
  min-height: calc(100vh - 60px);
}

.page-title {
  margin-bottom: 24px;
  color: #303133;
  font-size: 28px;
  font-weight: 600;
  position: relative;
  padding-bottom: 12px;
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #409eff, #67c23a);
  border-radius: 2px;
}

.action-bar {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 表格样式优化 - 现代美观设计 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border: 1px solid #ebeef5;
}

:deep(.el-table th) {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  color: #606266;
  font-weight: 600;
  height: 52px;
  font-size: 14px;
}

:deep(.el-table td) {
  color: #606266;
  height: 48px;
  font-size: 14px;
}

:deep(.el-table tr:hover td) {
  background-color: #f5f7fa;
}

:deep(.el-table--border) {
  border: 1px solid #ebeef5;
}

:deep(.el-table--border th, .el-table--border td) {
  border-right: 1px solid #ebeef5;
}

:deep(.el-table--border th:last-child, .el-table--border td:last-child) {
  border-right: none;
}

/* 表格行交替背景色 */
:deep(.el-table__row--striped td) {
  background-color: #fafafa;
}

/* 统计卡片样式 - 现代美观设计 */
.stat-card {
  border-radius: 12px;
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  background: white;
  overflow: hidden;
}

/* 第一个卡片：学生总数 - 蓝色主题 */
.stat-card:first-child {
  border-top: 4px solid #409eff;
}

/* 第二个卡片：平均成绩 - 绿色主题 */
.stat-card:nth-child(2) {
  border-top: 4px solid #67c23a;
}

/* 第三个卡片：最高分 - 橙色主题 */
.stat-card:nth-child(3) {
  border-top: 4px solid #e6a23c;
}

.stat-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
  transform: translateY(-5px);
}

.stat-content {
  padding: 24px;
  text-align: center;
}

.stat-title {
  font-size: 15px;
  color: #909399;
  margin-bottom: 12px;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

/* 添加单位样式 */
.stat-unit {
  font-size: 16px;
  color: #909399;
  font-weight: 500;
}
</style>