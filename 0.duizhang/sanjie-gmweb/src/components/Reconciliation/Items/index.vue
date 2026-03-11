<template>
  <div class="reconciliation-items">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>物品管理</span>
          <el-button type="primary" @click="openAddDialog">添加物品</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="mb-4">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="请输入物品名称" style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="itemsList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="物品名称" />
        <el-table-column prop="value" label="价值" width="120" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteItem(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="limit"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 添加/编辑物品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="物品名称">
          <el-input v-model="form.name" placeholder="请输入物品名称" />
        </el-form-item>
        <el-form-item label="价值">
          <el-input v-model.number="form.value" type="number" placeholder="请输入价值" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveItem">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { api_Get, api_Post } from '@/api/index.js';
import { ElMessage } from 'element-plus';

const itemsList = ref([]);
const total = ref(0);
const page = ref(1);
const limit = ref(10);
const searchForm = reactive({
  keyword: ''
});
const dialogVisible = ref(false);
const dialogTitle = ref('添加物品');
const form = reactive({
  id: '',
  name: '',
  value: '',
  description: ''
});

const getItems = async () => {
  try {
    const res = await api_Get('getItems', {
      page: page.value,
      limit: limit.value,
      keyword: searchForm.keyword
    });
    if (res.code === 200) {
      itemsList.value = res.data.items;
      total.value = res.data.total;
    }
  } catch (error) {
    ElMessage.error('获取物品列表失败');
  }
};

const search = () => {
  page.value = 1;
  getItems();
};

const reset = () => {
  searchForm.keyword = '';
  page.value = 1;
  getItems();
};

const handleSizeChange = (val) => {
  limit.value = val;
  getItems();
};

const handleCurrentChange = (val) => {
  page.value = val;
  getItems();
};

const openAddDialog = () => {
  dialogTitle.value = '添加物品';
  Object.assign(form, {
    id: '',
    name: '',
    value: '',
    description: ''
  });
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  dialogTitle.value = '编辑物品';
  Object.assign(form, row);
  dialogVisible.value = true;
};

const saveItem = async () => {
  if (!form.name || !form.value) {
    ElMessage.warning('请填写必要信息');
    return;
  }
  
  try {
    let res;
    if (form.id) {
      res = await api_Post('updateItem', form);
    } else {
      res = await api_Post('addItem', form);
    }
    
    if (res.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '添加成功');
      dialogVisible.value = false;
      getItems();
    } else {
      ElMessage.error(res.message || '操作失败');
    }
  } catch (error) {
    ElMessage.error('操作失败');
  }
};

const deleteItem = async (id) => {
  try {
    const res = await api_Post('deleteItem', { id });
    if (res.code === 200) {
      ElMessage.success('删除成功');
      getItems();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  } catch (error) {
    ElMessage.error('删除失败');
  }
};

onMounted(() => {
  getItems();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>