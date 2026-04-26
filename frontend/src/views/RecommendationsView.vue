<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '@/api/http'

interface Attraction {
  id: number
  name: string
  city: string | null
  price: string
  imageUrl: string | null
}

interface Hotel {
  id: number
  name: string
  city: string | null
  pricePerNight: string
  imageUrl: string | null
}

const router = useRouter()
const loading = ref(false)
const attractions = ref<Attraction[]>([])
const hotels = ref<Hotel[]>([])

async function load() {
  loading.value = true
  try {
    const { data } = await http.get<{ attractions: Attraction[]; hotels: Hotel[] }>(
      '/recommendations/mixed',
      { params: { limit: 6 } },
    )
    attractions.value = data.attractions
    hotels.value = data.hotels
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-wrap" v-loading="loading">
    <el-card shadow="never">
      <template #header>为您推荐（协同过滤 + 内容特征，Redis 缓存）</template>
      <h3>景点</h3>
      <el-row :gutter="16">
        <el-col v-for="a in attractions" :key="a.id" :xs="24" :sm="12" :md="8">
          <el-card class="item" shadow="hover" @click="router.push(`/attractions/${a.id}`)">
            <div class="cover" :style="{ backgroundImage: a.imageUrl ? `url(${a.imageUrl})` : undefined }" />
            <div class="title">{{ a.name }}</div>
            <div class="meta">{{ a.city }} · ￥{{ a.price }}</div>
          </el-card>
        </el-col>
      </el-row>

      <h3 style="margin-top: 24px">酒店</h3>
      <el-row :gutter="16">
        <el-col v-for="h in hotels" :key="h.id" :xs="24" :sm="12" :md="8">
          <el-card class="item" shadow="hover" @click="router.push(`/hotels/${h.id}`)">
            <div class="cover" :style="{ backgroundImage: h.imageUrl ? `url(${h.imageUrl})` : undefined }" />
            <div class="title">{{ h.name }}</div>
            <div class="meta">{{ h.city }} · ￥{{ h.pricePerNight }} / 晚</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style scoped>
.item {
  margin-bottom: 16px;
  cursor: pointer;
}
.cover {
  height: 120px;
  background: #e4e7ed center/cover no-repeat;
  border-radius: 4px;
  margin-bottom: 8px;
}
.title {
  font-weight: 600;
}
.meta {
  color: #909399;
  font-size: 13px;
  margin-top: 4px;
}
</style>
