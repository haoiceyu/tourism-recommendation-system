<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/api/http'
import type { Order, OrderStatus } from '@/types/api'

const loading = ref(false)
const orders = ref<Order[]>([])

const statusText: Record<OrderStatus, string> = {
  PENDING_PAYMENT: '待支付',
  PAID: '已支付',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
}

function statusLabel(s: OrderStatus) {
  return statusText[s]
}

async function load() {
  loading.value = true
  try {
    const { data } = await http.get<Order[]>('/orders')
    orders.value = data
  } finally {
    loading.value = false
  }
}

async function pay(id: number) {
  await ElMessageBox.confirm('模拟在线支付：确认支付该订单？', '支付确认', { type: 'warning' })
  await http.post(`/orders/${id}/pay`)
  ElMessage.success('支付成功')
  await load()
}

async function cancel(id: number) {
  await ElMessageBox.confirm('确认取消该订单？', '取消确认', { type: 'warning' })
  await http.post(`/orders/${id}/cancel`)
  ElMessage.success('订单已取消')
  await load()
}

async function complete(id: number) {
  await http.post(`/orders/${id}/complete`)
  ElMessage.success('已确认完成')
  await load()
}

onMounted(load)
</script>

<template>
  <div class="page-wrap">
    <el-card shadow="never">
      <template #header>我的订单</template>
      <el-table :data="orders" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" min-width="160" />
        <el-table-column prop="totalAmount" label="金额" width="120" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            {{ statusLabel(row.status) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING_PAYMENT'"
              size="small"
              type="primary"
              @click="pay(row.id)"
            >
              支付
            </el-button>
            <el-button
              v-if="row.status === 'PENDING_PAYMENT' || row.status === 'PAID'"
              size="small"
              @click="cancel(row.id)"
            >
              取消
            </el-button>
            <el-button v-if="row.status === 'PAID'" size="small" @click="complete(row.id)">
              确认完成
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-collapse v-if="orders.length" class="detail">
        <el-collapse-item v-for="o in orders" :key="o.id" :title="`明细：${o.orderNo}`" :name="o.id">
          <el-table :data="o.items" size="small">
            <el-table-column prop="title" label="项目" />
            <el-table-column prop="productType" label="类型" width="100" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="subtotal" label="小计" width="120" />
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<style scoped>
.detail {
  margin-top: 16px;
}
</style>
