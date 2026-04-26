<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '@/api/http'
import type { UserProfile } from '@/types/api'

const loading = ref(false)
const pwdLoading = ref(false)
const profile = ref<UserProfile | null>(null)

const form = reactive({
  nickname: '',
  email: '',
  phone: '',
})

const pwd = reactive({
  oldPassword: '',
  newPassword: '',
})

async function load() {
  loading.value = true
  try {
    const { data } = await http.get<UserProfile>('/users/me')
    profile.value = data
    form.nickname = data.nickname ?? ''
    form.email = data.email
    form.phone = data.phone ?? ''
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  await http.put('/users/me', {
    nickname: form.nickname || undefined,
    email: form.email || undefined,
    phone: form.phone || undefined,
  })
  ElMessage.success('资料已更新')
  await load()
}

async function savePassword() {
  pwdLoading.value = true
  try {
    await http.put('/users/me/password', {
      oldPassword: pwd.oldPassword,
      newPassword: pwd.newPassword,
    })
    ElMessage.success('密码已修改')
    pwd.oldPassword = ''
    pwd.newPassword = ''
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    pwdLoading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-wrap" v-loading="loading">
    <el-card v-if="profile" shadow="never">
      <template #header>个人信息</template>
      <el-form label-width="100px" style="max-width: 520px">
        <el-form-item label="用户名">
          <el-input :model-value="profile.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存资料</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px">
      <template #header>修改密码</template>
      <el-form label-width="100px" style="max-width: 520px">
        <el-form-item label="原密码">
          <el-input v-model="pwd.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwd.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="pwdLoading" @click="savePassword">更新密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px">
      <template #header>密码重置（演示）</template>
      <p class="muted">
        调用 POST /api/auth/password-reset/request 获取 resetToken，再调用 confirm 接口完成重置。
      </p>
    </el-card>
  </div>
</template>

<style scoped>
.muted {
  color: #606266;
  line-height: 1.6;
}
</style>
