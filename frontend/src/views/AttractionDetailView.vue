<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '@/api/http'
import { useAuthStore } from '@/stores/auth'

interface Attraction {
  id: number
  name: string
  description: string | null
  city: string | null
  categoryId: number | null
  price: string
  imageUrl: string | null
  keywordTags: string | null
  active: boolean
  avgRating: number | null
}

const route = useRoute()
const auth = useAuthStore()
const item = ref<Attraction | null>(null)
const qty = ref(1)
const orderOpen = ref(false)

async function load() {
  const id = Number(route.params.id)
  const { data } = await http.get<Attraction>(`/attractions/${id}`)
  item.value = data
  if (auth.isAuthenticated) {
    try {
      await http.post('/behavior/view', {}, {
        params: { productType: 'ATTRACTION', productId: id },
      })
    } catch {
      /* ignore */
    }
  }
}

async function toggleFavorite() {
  if (!auth.isAuthenticated) {
    ElMessage.warning('请先登录')
    return
  }
  if (!item.value) return
  try {
    await http.post('/favorites', {}, {
      params: { productType: 'ATTRACTION', productId: item.value.id },
    })
    ElMessage.success('已收藏')
  } catch (e) {
    ElMessage.error((e as Error).message)
  }
}

async function createOrder() {
  if (!auth.isAuthenticated || !item.value) return
  await http.post('/orders', {
    items: [{ productType: 'ATTRACTION', productId: item.value.id, quantity: qty.value }],
  })
  ElMessage.success('订单已创建，请前往我的订单支付')
  orderOpen.value = false
}

onMounted(load)
</script>

<template>
  <div class="page-wrap" v-if="item">
    <el-card shadow="never">
      <div class="hero" :style="{ backgroundImage: item.imageUrl ? `url(${item.imageUrl})` : undefined }" />
      <h2>{{ item.name }}</h2>
      <p class="muted">{{ item.city }} · ￥{{ item.price }}</p>
      <p v-if="item.avgRating != null">平均评分：{{ item.avgRating.toFixed(1) }}</p>
      <p class="desc">{{ item.description }}</p>
      <p v-if="item.keywordTags" class="tags">标签：{{ item.keywordTags }}</p>
      <el-space>
        <el-button type="primary" @click="orderOpen = true">预订门票</el-button>
        <el-button @click="toggleFavorite">收藏</el-button>
      </el-space>
    </el-card>

    <el-dialog v-model="orderOpen" title="创建订单" width="420px">
      <el-form label-width="100px">
        <el-form-item label="数量">
          <el-input-number v-model="qty" :min="1" :max="99" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orderOpen = false">取消</el-button>
        <el-button type="primary" @click="createOrder">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.hero {
  height: 260px;
  background: #e4e7ed center/cover no-repeat;
  border-radius: 8px;
  margin-bottom: 16px;
}
.muted {
  color: #909399;
}
.desc {
  line-height: 1.7;
  color: #606266;
}
.tags {
  color: #606266;
  font-size: 13px;
}
</style>
