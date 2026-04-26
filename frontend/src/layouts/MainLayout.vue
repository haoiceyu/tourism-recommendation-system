<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const active = computed(() => {
  const p = route.path
  if (p.startsWith('/admin')) return '/admin'
  if (p.startsWith('/attractions')) return '/attractions'
  if (p.startsWith('/hotels')) return '/hotels'
  return p
})

function go(path: string) {
  router.push(path)
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="brand" @click="go('/')">旅游推荐</div>
      <el-menu
        mode="horizontal"
        :ellipsis="false"
        class="menu"
        :default-active="active"
        router
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/attractions">景点</el-menu-item>
        <el-menu-item index="/hotels">酒店</el-menu-item>
        <el-menu-item v-if="auth.isAuthenticated" index="/recommendations">个性化推荐</el-menu-item>
        <el-menu-item v-if="auth.isAuthenticated" index="/orders">我的订单</el-menu-item>
        <el-menu-item v-if="auth.isAdmin" index="/admin">后台管理</el-menu-item>
      </el-menu>
      <div class="right">
        <template v-if="!auth.isAuthenticated">
          <el-button text @click="go('/login')">登录</el-button>
          <el-button type="primary" @click="go('/register')">注册</el-button>
        </template>
        <template v-else>
          <el-button text @click="go('/profile')">{{ auth.username }}</el-button>
          <el-button @click="logout">退出</el-button>
        </template>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
    <el-footer class="footer">旅游推荐系统 · Spring Boot + Vue · 演示数据：demo / demo12345 · 管理员 admin / admin123</el-footer>
  </el-container>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  flex-direction: column;
}
.header {
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #ebeef5;
  background: #fff;
}
.brand {
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
}
.menu {
  flex: 1;
  border-bottom: none;
}
.right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.main {
  background: #f5f7fa;
}
.footer {
  text-align: center;
  color: #909399;
  font-size: 12px;
  border-top: 1px solid #ebeef5;
}
</style>
