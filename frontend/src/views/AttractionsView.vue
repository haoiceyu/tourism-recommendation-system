<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/api/http'
import type { ProductType } from '@/types/api'

interface Category {
  id: number
  name: string
  scope: ProductType
}

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

interface PageResp<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

const router = useRouter()
const loading = ref(false)
const categories = ref<Category[]>([])
const page = reactive({ number: 0, size: 12, total: 0 })
const filters = reactive({ keyword: '', categoryId: undefined as number | undefined })
const list = ref<Attraction[]>([])

async function loadCategories() {
  const { data } = await http.get<Category[]>('/categories', { params: { scope: 'ATTRACTION' } })
  categories.value = data
}

async function load() {
  loading.value = true
  try {
    const { data } = await http.get<PageResp<Attraction>>('/attractions', {
      params: {
        keyword: filters.keyword || undefined,
        categoryId: filters.categoryId,
        page: page.number,
        size: page.size,
      },
    })
    list.value = data.content
    page.total = data.totalElements
  } finally {
    loading.value = false
  }
}

function open(id: number) {
  router.push(`/attractions/${id}`)
}

onMounted(async () => {
  await loadCategories()
  await load()
})
</script>

<template>
  <div class="page-wrap">
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent="() => { page.number = 0; load() }">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="名称 / 城市 / 标签" style="width: 220px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filters.categoryId" clearable placeholder="全部" style="width: 180px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="16" v-loading="loading">
        <el-col v-for="a in list" :key="a.id" :xs="24" :sm="12" :md="8">
          <el-card class="item" shadow="hover" @click="open(a.id)">
            <div class="cover" :style="{ backgroundImage: a.imageUrl ? `url(${a.imageUrl})` : undefined }" />
            <div class="title">{{ a.name }}</div>
            <div class="meta">{{ a.city }} · ￥{{ a.price }}</div>
            <div v-if="a.avgRating != null" class="meta">评分 {{ a.avgRating.toFixed(1) }}</div>
          </el-card>
        </el-col>
      </el-row>

      <div class="pager">
        <el-pagination
          background
          layout="prev, pager, next, total, sizes"
          :total="page.total"
          :current-page="page.number + 1"
          :page-size="page.size"
          :page-sizes="[12, 24]"
          @current-change="(p: number) => { page.number = p - 1; load() }"
          @size-change="(s: number) => { page.size = s; page.number = 0; load() }"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.item {
  margin-bottom: 16px;
  cursor: pointer;
}
.cover {
  height: 140px;
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
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
