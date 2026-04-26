<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function go(path: string) {
  router.push(path)
}
</script>

<template>
  <div class="page-wrap">
    <el-card shadow="never">
      <h2>欢迎来到旅游推荐系统</h2>
      <p class="muted">
        基于浏览、收藏与预订行为的协同过滤 + 内容特征混合推荐，结果缓存于 Redis，支持景点与酒店预订、评价与后台运营监控。
      </p>
      <el-space wrap>
        <el-button type="primary" @click="go('/attractions')">浏览景点</el-button>
        <el-button @click="go('/hotels')">查找酒店</el-button>
        <el-button @click="go('/recommendations')">查看推荐</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<style scoped>
h2 {
  margin-top: 0;
}
.muted {
  color: #606266;
  line-height: 1.6;
}
</style>
