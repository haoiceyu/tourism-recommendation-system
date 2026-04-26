<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function backHome() {
  router.push('/')
}
</script>

<template>
  <div class="admin-shell">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-menu router :default-active="$route.path" class="side">
          <el-menu-item index="/admin">数据概览</el-menu-item>
          <el-menu-item index="/admin/users">用户管理</el-menu-item>
          <el-menu-item index="/admin/orders">订单审核</el-menu-item>
          <el-menu-item index="/admin/products">产品维护</el-menu-item>
          <el-menu-item @click="backHome">返回前台</el-menu-item>
        </el-menu>
      </el-col>
      <el-col :span="20">
        <div class="page-wrap">
          <router-view />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.admin-shell {
  padding: 16px;
  min-height: 100vh;
  background: #f5f7fa;
}
.side {
  border-right: none;
  min-height: calc(100vh - 32px);
}
</style>
