<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '@/api/http'
import type { DashboardStats } from '@/types/api'

const loading = ref(false)
const stats = ref<DashboardStats | null>(null)

async function load() {
  loading.value = true
  try {
    const { data } = await http.get<DashboardStats>('/admin/dashboard/stats')
    stats.value = data
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
    <template #header>运营概览</template>
    <el-row v-if="stats" :gutter="16">
      <el-col :span="6">
        <el-statistic title="用户总数" :value="stats.totalUsers" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="今日新增用户" :value="stats.newUsersToday" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="待支付订单" :value="stats.pendingPaymentOrders" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="已支付订单" :value="stats.paidOrders" />
      </el-col>
      <el-col :span="6" style="margin-top: 16px">
        <el-statistic title="已完成订单" :value="stats.completedOrders" />
      </el-col>
      <el-col :span="6" style="margin-top: 16px">
        <el-statistic title="已取消订单" :value="stats.cancelledOrders" />
      </el-col>
      <el-col :span="12" style="margin-top: 16px">
        <el-statistic title="今日营收（已支付/完成）" :value="stats.revenueToday" />
      </el-col>
    </el-row>
  </el-card>
</template>
