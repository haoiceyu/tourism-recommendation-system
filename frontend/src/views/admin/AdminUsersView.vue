<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '@/api/http'
import type { UserProfile, UserRole } from '@/types/api'

const loading = ref(false)
const users = ref<UserProfile[]>([])

async function load() {
  loading.value = true
  try {
    const { data } = await http.get<UserProfile[]>('/admin/users')
    users.value = data
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    loading.value = false
  }
}

async function changeRole(id: number, role: UserRole) {
  await http.put(`/admin/users/${id}/role`, { role })
  ElMessage.success('角色已更新')
  await load()
}

onMounted(load)
</script>

<template>
  <el-card v-loading="loading" shadow="never">
    <template #header>用户管理</template>
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column label="角色" width="200">
        <template #default="{ row }">
          <el-select :model-value="row.role" @change="(v: UserRole) => changeRole(row.id, v)">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" min-width="180" />
    </el-table>
  </el-card>
</template>
