<template>
  <div class="reconciliation-orders">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <el-button type="primary" @click="openCreateOrderDialog">创建订单</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="mb-4">
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="选择状态">
            <el-option label="全部" value="" />
            <el-option label="待付款" value="pending" />
            <el-option label="部分付款" value="partial" />
            <el-option label="已付款" value="paid" />
          </el-select>
        </el-form-item>
        <el-form-item label="搜索">
          <el-input v-model="searchForm.search" placeholder="订单名称或描述" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="ordersList" style="width: 100%">
        <el-table-column prop="id" label="订单ID" width="100" />
        <el-table-column prop="order_name" label="订单名称" />
        <el-table-column prop="total_amount" label="总金额" width="120">
          <template #default="scope">
            {{ scope.row.total_amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="paid_amount" label="已付金额" width="120">
          <template #default="scope">
            {{ scope.row.paid_amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建日期" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewOrderDetails(scope.row.id)">查看详情</el-button>
            <el-button size="small" type="danger" @click="deleteOrder(scope.row.id)">删除</el-button>
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
    
    <!-- 创建订单对话框 -->
    <el-dialog
      v-model="createOrderDialogVisible"
      title="创建订单"
      width="800px"
    >
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="订单名称" required>
          <el-input v-model="orderForm.order_name" placeholder="请输入订单名称" />
        </el-form-item>
        <el-form-item label="订单描述">
          <el-input v-model="orderForm.description" type="textarea" placeholder="请输入订单描述" />
        </el-form-item>
        
        <el-form-item label="商品列表" required>
          <el-table :data="orderForm.items" style="width: 100%" border>
            <el-table-column prop="item_id" label="商品" width="200">
              <template #default="scope">
                <el-select v-model="scope.row.item_id" placeholder="请选择商品">
                  <el-option v-for="item in items" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100">
              <template #default="scope">
                <el-input v-model.number="scope.row.quantity" type="number" placeholder="请输入数量" min="1" />
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价" width="100">
              <template #default="scope">
                <el-input v-model.number="scope.row.price" type="number" placeholder="请输入单价" min="0" step="0.01" />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120">
              <template #default="scope">
                {{ (scope.row.quantity * scope.row.price).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="small" type="danger" @click="removeOrderItem(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button type="primary" @click="addOrderItem" style="margin-top: 10px">添加商品</el-button>
        </el-form-item>
        
        <el-form-item label="总金额">
          <el-input v-model="totalAmount" readonly />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createOrderDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createOrder">创建订单</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="orderDetailsDialogVisible"
      title="订单详情"
      width="900px"
    >
      <el-form :model="orderDetails" label-width="100px">
        <el-form-item label="订单ID">
          <el-input v-model="orderDetails.id" readonly />
        </el-form-item>
        <el-form-item label="订单名称">
          <el-input v-model="orderDetails.order_name" readonly />
        </el-form-item>
        <el-form-item label="总金额">
          <el-input v-model="orderDetails.total_amount" readonly />
        </el-form-item>
        <el-form-item label="已付金额">
          <el-input v-model="orderDetails.paid_amount" readonly />
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="getStatusType(orderDetails.status)">
            {{ getStatusText(orderDetails.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="创建日期">
          <el-input v-model="orderDetails.created_at" readonly />
        </el-form-item>
        <el-form-item label="订单描述">
          <el-input v-model="orderDetails.description" readonly />
        </el-form-item>
        
        <el-form-item label="商品列表">
          <el-table :data="orderDetails.items" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="item_name" label="商品名称" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="price" label="单价" width="100" />
            <el-table-column label="小计" width="120">
              <template #default="scope">
                {{ (scope.row.quantity * scope.row.price).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        
        <el-form-item label="付款记录">
          <div class="payment-section">
            <el-button type="primary" size="small" @click="openAddPaymentDialog" style="margin-bottom: 10px">添加付款</el-button>
            <el-table :data="orderDetails.payments" style="width: 100%" border>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="amount" label="金额" width="120">
                <template #default="scope">
                  {{ scope.row.amount.toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="payment_date" label="付款日期" width="180" />
              <el-table-column prop="payment_method" label="付款方式" />
              <el-table-column prop="description" label="描述" />
              <el-table-column label="操作" width="80">
                <template #default="scope">
                  <el-button size="small" type="danger" @click="deletePayment(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="orderDetailsDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 添加付款对话框 -->
    <el-dialog
      v-model="addPaymentDialogVisible"
      title="添加付款"
      width="600px"
    >
      <el-form :model="paymentForm" label-width="100px">
        <el-form-item label="付款金额" required>
          <el-input v-model.number="paymentForm.amount" type="number" placeholder="请输入付款金额" min="0.01" step="0.01" />
        </el-form-item>
        <el-form-item label="付款方式">
          <el-input v-model="paymentForm.payment_method" placeholder="请输入付款方式" />
        </el-form-item>
        <el-form-item label="付款描述">
          <el-input v-model="paymentForm.description" type="textarea" placeholder="请输入付款描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addPaymentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addPayment">确认付款</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { api_Get, api_Post } from '@/api/index.js';
import { ElMessage } from 'element-plus';

const ordersList = ref([]);
const total = ref(0);
const page = ref(1);
const limit = ref(10);
const items = ref([]);
const searchForm = reactive({
  search: '',
  status: ''
});

const createOrderDialogVisible = ref(false);
const orderForm = reactive({
  order_name: '',
  description: '',
  items: [
    { item_id: '', quantity: 1, price: '' }
  ]
});

const orderDetailsDialogVisible = ref(false);
const orderDetails = reactive({
  id: '',
  order_name: '',
  total_amount: '',
  paid_amount: '',
  status: '',
  created_at: '',
  description: '',
  items: [],
  payments: []
});

const addPaymentDialogVisible = ref(false);
const paymentForm = reactive({
  amount: '',
  payment_method: '',
  description: ''
});

const totalAmount = computed(() => {
  return orderForm.items.reduce((sum, item) => {
    return sum + (item.quantity * item.price || 0);
  }, 0).toFixed(2);
});

const getItems = async () => {
  try {
    const res = await api_Get('getItems', { limit: 1000 });
    if (res.code === 200) {
      items.value = res.data.items;
    }
  } catch (error) {
    ElMessage.error('获取商品列表失败');
  }
};

const getOrders = async () => {
  try {
    const res = await api_Get('getOrders', {
      page: page.value,
      limit: limit.value,
      search: searchForm.search,
      status: searchForm.status
    });
    if (res.code === 200) {
      ordersList.value = res.data.orders;
      total.value = res.data.total;
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败');
  }
};

const search = () => {
  page.value = 1;
  getOrders();
};

const reset = () => {
  Object.assign(searchForm, {
    search: '',
    status: ''
  });
  page.value = 1;
  getOrders();
};

const handleSizeChange = (val) => {
  limit.value = val;
  getOrders();
};

const handleCurrentChange = (val) => {
  page.value = val;
  getOrders();
};

const openCreateOrderDialog = () => {
  orderForm.order_name = '';
  orderForm.description = '';
  orderForm.items = [{ item_id: '', quantity: 1, price: '' }];
  createOrderDialogVisible.value = true;
};

const addOrderItem = () => {
  orderForm.items.push({ item_id: '', quantity: 1, price: '' });
};

const removeOrderItem = (index) => {
  orderForm.items.splice(index, 1);
};

const createOrder = async () => {
  if (!orderForm.order_name) {
    ElMessage.warning('请输入订单名称');
    return;
  }
  
  if (orderForm.items.length === 0) {
    ElMessage.warning('请添加商品');
    return;
  }
  
  // 检查所有商品是否填写完整
  for (const item of orderForm.items) {
    if (!item.item_id || !item.price || !item.quantity) {
      ElMessage.warning('请填写完整商品信息');
      return;
    }
  }
  
  try {
    const res = await api_Post('createOrder', orderForm);
    if (res.code === 200) {
      ElMessage.success('订单创建成功');
      createOrderDialogVisible.value = false;
      getOrders();
    } else {
      ElMessage.error(res.message || '订单创建失败');
    }
  } catch (error) {
    ElMessage.error('订单创建失败');
  }
};

const viewOrderDetails = async (id) => {
  try {
    const res = await api_Get('getOrderById', { id });
    if (res.code === 200) {
      Object.assign(orderDetails, res.data);
      orderDetailsDialogVisible.value = true;
    } else {
      ElMessage.error(res.message || '获取订单详情失败');
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败');
  }
};

const deleteOrder = async (id) => {
  try {
    const res = await api_Post('deleteOrder', { id });
    if (res.code === 200) {
      ElMessage.success('订单删除成功');
      getOrders();
    } else {
      ElMessage.error(res.message || '订单删除失败');
    }
  } catch (error) {
    ElMessage.error('订单删除失败');
  }
};

const openAddPaymentDialog = () => {
  paymentForm.amount = '';
  paymentForm.payment_method = '';
  paymentForm.description = '';
  addPaymentDialogVisible.value = true;
};

const addPayment = async () => {
  if (!paymentForm.amount || paymentForm.amount <= 0) {
    ElMessage.warning('请输入有效的付款金额');
    return;
  }
  
  try {
    const res = await api_Post('addPayment', {
      order_id: orderDetails.id,
      amount: paymentForm.amount,
      payment_method: paymentForm.payment_method,
      description: paymentForm.description
    });
    if (res.code === 200) {
      ElMessage.success('付款成功');
      addPaymentDialogVisible.value = false;
      viewOrderDetails(orderDetails.id); // 刷新订单详情
    } else {
      ElMessage.error(res.message || '付款失败');
    }
  } catch (error) {
    ElMessage.error('付款失败');
  }
};

const deletePayment = async (id) => {
  try {
    const res = await api_Post('deletePayment', { id });
    if (res.code === 200) {
      ElMessage.success('付款记录删除成功');
      viewOrderDetails(orderDetails.id); // 刷新订单详情
    } else {
      ElMessage.error(res.message || '付款记录删除失败');
    }
  } catch (error) {
    ElMessage.error('付款记录删除失败');
  }
};

const getStatusType = (status) => {
  switch (status) {
    case 'pending': return 'danger';
    case 'partial': return 'warning';
    case 'paid': return 'success';
    default: return '';
  }
};

const getStatusText = (status) => {
  switch (status) {
    case 'pending': return '待付款';
    case 'partial': return '部分付款';
    case 'paid': return '已付款';
    default: return status;
  }
};

onMounted(() => {
  getItems();
  getOrders();
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

.payment-section {
  margin-top: 10px;
}
</style>