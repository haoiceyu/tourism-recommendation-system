<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
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
    const { data } = await http.get<Order[]>('/admin/orders')
    orders.value = data
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <el-card v-loading="loading" shadow="never">
    <template #header>订单审核 / 监控</template>
    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" min-width="160" />
      <el-table-column prop="totalAmount" label="金额" width="120" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          {{ statusLabel(row.status) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      <el-table-column prop="paidAt" label="支付时间" min-width="180" />
      <el-table-column prop="completedAt" label="完成时间" min-width="180" />
    </el-table>
  </el-card>
</template>
