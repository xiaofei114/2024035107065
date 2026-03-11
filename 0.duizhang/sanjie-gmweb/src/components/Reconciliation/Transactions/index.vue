<template>
  <div class="reconciliation-transactions">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>交易记录</span>
          <el-button type="primary" @click="openAddDialog">添加交易</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="mb-4">
        <el-form-item label="物品">
          <el-select v-model="searchForm.itemId" placeholder="请选择物品">
            <el-option v-for="item in items" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="searchForm.startDate" type="date" placeholder="选择开始日期" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="searchForm.endDate" type="date" placeholder="选择结束日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="transactionsList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="item_name" label="物品名称" />
        <el-table-column prop="amount" label="单价" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="总价" width="100">
          <template #default="scope">
            {{ (scope.row.amount * scope.row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="transaction_date" label="交易日期" width="180" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteTransaction(scope.row.id)">删除</el-button>
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
    
    <!-- 添加/编辑交易对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="物品">
          <el-select v-model="form.itemId" placeholder="请选择物品">
            <el-option v-for="item in items" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价">
          <el-input v-model.number="form.amount" type="number" placeholder="请输入单价" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model.number="form.quantity" type="number" placeholder="请输入数量" />
        </el-form-item>
        <el-form-item label="交易日期">
          <el-date-picker v-model="form.transactionDate" type="datetime" placeholder="选择交易日期" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveTransaction">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { api_Get, api_Post } from '@/api/index.js';
import { ElMessage } from 'element-plus';

const transactionsList = ref([]);
const total = ref(0);
const page = ref(1);
const limit = ref(10);
const items = ref([]);
const searchForm = reactive({
  itemId: '',
  startDate: '',
  endDate: ''
});
const dialogVisible = ref(false);
const dialogTitle = ref('添加交易');
const form = reactive({
  id: '',
  itemId: '',
  amount: '',
  quantity: 1,
  transactionDate: new Date(),
  description: ''
});

const getItems = async () => {
  try {
    const res = await api_Get('getItems', { limit: 1000 });
    if (res.code === 200) {
      items.value = res.data.items;
    }
  } catch (error) {
    ElMessage.error('获取物品列表失败');
  }
};

const getTransactions = async () => {
  try {
    const res = await api_Get('getTransactions', {
      page: page.value,
      limit: limit.value,
      itemId: searchForm.itemId,
      startDate: searchForm.startDate,
      endDate: searchForm.endDate
    });
    if (res.code === 200) {
      transactionsList.value = res.data.transactions;
      total.value = res.data.total;
    }
  } catch (error) {
    ElMessage.error('获取交易记录失败');
  }
};

const search = () => {
  page.value = 1;
  getTransactions();
};

const reset = () => {
  Object.assign(searchForm, {
    itemId: '',
    startDate: '',
    endDate: ''
  });
  page.value = 1;
  getTransactions();
};

const handleSizeChange = (val) => {
  limit.value = val;
  getTransactions();
};

const handleCurrentChange = (val) => {
  page.value = val;
  getTransactions();
};

const openAddDialog = () => {
  dialogTitle.value = '添加交易';
  Object.assign(form, {
    id: '',
    itemId: '',
    amount: '',
    quantity: 1,
    transactionDate: new Date(),
    description: ''
  });
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  dialogTitle.value = '编辑交易';
  Object.assign(form, row);
  form.transactionDate = new Date(row.transaction_date);
  dialogVisible.value = true;
};

const saveTransaction = async () => {
  if (!form.itemId || !form.amount) {
    ElMessage.warning('请填写必要信息');
    return;
  }
  
  try {
    let res;
    if (form.id) {
      res = await api_Post('updateTransaction', form);
    } else {
      res = await api_Post('addTransaction', form);
    }
    
    if (res.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '添加成功');
      dialogVisible.value = false;
      getTransactions();
    } else {
      ElMessage.error(res.message || '操作失败');
    }
  } catch (error) {
    ElMessage.error('操作失败');
  }
};

const deleteTransaction = async (id) => {
  try {
    const res = await api_Post('deleteTransaction', { id });
    if (res.code === 200) {
      ElMessage.success('删除成功');
      getTransactions();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  } catch (error) {
    ElMessage.error('删除失败');
  }
};

onMounted(() => {
  getItems();
  getTransactions();
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