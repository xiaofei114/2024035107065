<template>
  <div class="reconciliation-overview">
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <span>对账总览</span>
      </template>

      <div class="stats-container">
        <el-statistic :value="overviewData.totalItems" title="总商品数" />
        <el-statistic :value="overviewData.totalOrders" title="总订单数" />
        <el-statistic :value="overviewData.totalAmount" title="总交易金额" :precision="2" />
        <el-statistic :value="overviewData.totalPaid" title="已付款金额" :precision="2" />
      </div>
    </el-card>

    <el-card shadow="hover">
      <el-table :data="orderDetailsList" style="width: 100%" :span-method="arraySpanMethod" border>
        <el-table-column prop="order_id" label="订单ID" width="100" >
          <template #default="scope">
            <div class="box">
              {{ scope.row.order_id }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="order_name" label="订单名称" >
          <template #default="scope">
            <div class="box">
              {{ scope.row.order_name }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="item_name" label="商品名称" >
          <template #default="scope">
            <div class="box">
              {{ scope.row.item_name }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" >
          <template #default="scope">
            <div class="box">
              {{ scope.row.quantity }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="100" >
          <template #default="scope">
            <div class="box">
              {{ scope.row.price }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="小计" width="100">
          <template #default="scope">
            <div class="box">
              {{ (scope.row.quantity * scope.row.price).toFixed(2) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="payments" label="付款金额" width="180" class="payments-table">
          <template #default="scope">
            <el-table :data="scope.row.payments" style="width: 100%;height: 100%;" border>
              <el-table-column prop="payment_amount" label="" width="180" />
              <el-table-column prop="payment_date" label="" width="180" />
              <el-table-column prop="payment_method" label="" width="180" />
            </el-table>
          </template>
        </el-table-column>
        <el-table-column label="付款日期" width="180" />
        <el-table-column label="付款方式" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { api_Get } from '@/api/index.js';
import { ElMessage } from 'element-plus';

const overviewData = reactive({
  totalItems: 0,
  totalOrders: 0,
  totalAmount: 0,
  totalPaid: 0,
  recentOrders: []
});

const orderDetailsList = ref([]);

const getOverview = async () => {
  try {
    const res = await api_Get('getOverview');
    if (res.code === 200) {
      Object.assign(overviewData, res.data);
      // 构建订单详情列表，按照订单名称 -> 商品 -> 付款记录的结构
      buildOrderDetailsList(res.data.recentOrders);
    }
  } catch (error) {
    ElMessage.error('获取总览数据失败');
  }
};

const buildOrderDetailsList = (orders) => {
  const list = [];
  const payments = []

  orders.forEach(order => {
    // 添加付款记录
    order.payments.forEach(payment => {
      const paymentRow = {
        order_id: order.id,
        order_name: order.order_name,
        item_name: '', // 付款记录没有商品名称
        quantity: '', // 付款记录没有数量
        price: '', // 付款记录没有单价
        payment_amount: payment.amount,
        payment_date: payment.payment_date,
        payment_method: payment.payment_method,
      };
      payments.push(paymentRow);
    });
    // 添加订单头信息
    order.items.forEach(item => {
      // 添加商品信息
      const itemRow = {
        order_id: order.id,
        order_name: order.order_name,
        item_name: item.item_name,
        quantity: item.quantity,
        price: item.price,
        payments: payments.filter(p => p.order_id === order.id),
      };
      list.push(itemRow);
    });

  });

  console.log(list);


  orderDetailsList.value = list;
};

// 计算合并行
const arraySpanMethod = ({ row, column, rowIndex, columnIndex }) => {
  if (columnIndex === 0) { // 订单ID列
    const currentOrderId = row.order_id;
    let span = 1;

    // 检查后面有多少相同的订单
    for (let i = rowIndex + 1; i < orderDetailsList.value.length; i++) {
      if (orderDetailsList.value[i].order_id === currentOrderId) {
        span++;
      } else {
        break;
      }
    }

    // 检查当前行是否是该订单的第一个出现
    let isFirst = true;
    for (let i = rowIndex - 1; i >= 0; i--) {
      if (orderDetailsList.value[i].order_id === currentOrderId) {
        isFirst = false;
        break;
      }
    }

    if (isFirst && span > 1) {
      return { rowspan: span, colspan: 1 };
    } else if (!isFirst) {
      return { rowspan: 0, colspan: 0 };
    } else {
      return { rowspan: 1, colspan: 1 };
    }
  } else if (columnIndex === 1) { // 订单名称列
    const currentOrderId = row.order_id;
    let span = 1;

    // 检查后面有多少相同的订单
    for (let i = rowIndex + 1; i < orderDetailsList.value.length; i++) {
      if (orderDetailsList.value[i].order_id === currentOrderId) {
        span++;
      } else {
        break;
      }
    }

    // 检查当前行是否是该订单的第一个出现
    let isFirst = true;
    for (let i = rowIndex - 1; i >= 0; i--) {
      if (orderDetailsList.value[i].order_id === currentOrderId) {
        isFirst = false;
        break;
      }
    }

    if (isFirst && span > 1) {
      return { rowspan: span, colspan: 1 };
    } else if (!isFirst) {
      return { rowspan: 0, colspan: 0 };
    } else {
      return { rowspan: 1, colspan: 1 };
    }
  } else if (columnIndex === 2 || columnIndex === 3 || columnIndex === 4 || columnIndex === 5) { // 商品相关列
    // 检查当前行是否有商品信息
    if (row.item_name) {
      const currentOrderId = row.order_id;
      let span = 1;

      // 检查后面有多少付款记录属于同一个商品
      for (let i = rowIndex + 1; i < orderDetailsList.value.length; i++) {
        if (orderDetailsList.value[i].order_id === currentOrderId && !orderDetailsList.value[i].item_name) {
          span++;
        } else {
          break;
        }
      }

      if (span > 1) {
        return { rowspan: span, colspan: 1 };
      } else {
        return { rowspan: 1, colspan: 1 };
      }
    } else {
      return { rowspan: 0, colspan: 0 };
    }
  } else if (columnIndex === 6) { // 付款相关列
    const currentOrderId = row.order_id;
    let span = 1;

    // 检查后面有多少相同的订单
    for (let i = rowIndex + 1; i < orderDetailsList.value.length; i++) {
      if (orderDetailsList.value[i].order_id === currentOrderId) {
        span++;
      } else {
        break;
      }
    }

    // 检查当前行是否是该订单的第一个出现
    let isFirst = true;
    for (let i = rowIndex - 1; i >= 0; i--) {
      if (orderDetailsList.value[i].order_id === currentOrderId) {
        isFirst = false;
        break;
      }
    }

    if (isFirst && span > 1) {
      return { rowspan: span, colspan: 3 };
    } else if (!isFirst) {
      return { rowspan: 0, colspan: 0 };
    } else {
      return { rowspan: 1, colspan: 3 };
    }
  }
};

const viewOrderDetails = (orderId) => {
  // 跳转到订单详情页面
  window.location.href = `#/home/reconciliation-orders?id=${orderId}`;
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
  getOverview();
});
</script>

<style scoped>
.stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.mb-4 {
  margin-bottom: 20px;
}

:deep(.el-table .el-table__cell) {
  padding: 0;
}

:deep(.el-table .cell) {
  padding: 0;
}

.box {
  width: 100%;
  height: 100%;
  margin: 6px;
}
</style>