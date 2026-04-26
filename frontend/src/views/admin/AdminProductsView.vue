<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '@/api/http'

interface Attraction {
  id: number
  name: string
  city: string | null
  price: string
  active: boolean
}

interface Hotel {
  id: number
  name: string
  city: string | null
  pricePerNight: string
  active: boolean
}

const tab = ref<'ATTRACTION' | 'HOTEL'>('ATTRACTION')
const loading = ref(false)
const attractions = ref<Attraction[]>([])
const hotels = ref<Hotel[]>([])

const dlg = ref(false)
const editing = ref<'create' | 'edit'>('create')
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  description: '',
  city: '',
  categoryId: undefined as number | undefined,
  price: '',
  imageUrl: '',
  keywordTags: '',
  active: true,
})

async function load() {
  loading.value = true
  try {
    const [a, h] = await Promise.all([
      http.get<Attraction[]>('/admin/attractions'),
      http.get<Hotel[]>('/admin/hotels'),
    ])
    attractions.value = a.data
    hotels.value = h.data
  } catch (e) {
    ElMessage.error((e as Error).message)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = 'create'
  editingId.value = null
  form.name = ''
  form.description = ''
  form.city = ''
  form.categoryId = undefined
  form.price = ''
  form.imageUrl = ''
  form.keywordTags = ''
  form.active = true
  dlg.value = true
}

function openEditA(row: Attraction) {
  editing.value = 'edit'
  editingId.value = row.id
  form.name = row.name
  form.description = ''
  form.city = row.city ?? ''
  form.categoryId = undefined
  form.price = row.price
  form.imageUrl = ''
  form.keywordTags = ''
  form.active = row.active
  dlg.value = true
}

function openEditH(row: Hotel) {
  editing.value = 'edit'
  editingId.value = row.id
  form.name = row.name
  form.description = ''
  form.city = row.city ?? ''
  form.categoryId = undefined
  form.price = row.pricePerNight
  form.imageUrl = ''
  form.keywordTags = ''
  form.active = row.active
  dlg.value = true
}

async function save() {
  const body = {
    name: form.name,
    description: form.description || undefined,
    city: form.city || undefined,
    categoryId: form.categoryId,
    price: Number(form.price),
    imageUrl: form.imageUrl || undefined,
    keywordTags: form.keywordTags || undefined,
    active: form.active,
  }
  if (tab.value === 'ATTRACTION') {
    if (editing.value === 'create') await http.post('/admin/attractions', body)
    else if (editingId.value != null) await http.put(`/admin/attractions/${editingId.value}`, body)
  } else {
    if (editing.value === 'create') await http.post('/admin/hotels', body)
    else if (editingId.value != null) await http.put(`/admin/hotels/${editingId.value}`, body)
  }
  ElMessage.success('已保存')
  dlg.value = false
  await load()
}

async function removeA(id: number) {
  await http.delete(`/admin/attractions/${id}`)
  ElMessage.success('已删除')
  await load()
}

async function removeH(id: number) {
  await http.delete(`/admin/hotels/${id}`)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>

<template>
  <el-card v-loading="loading" shadow="never">
    <template #header>产品维护</template>
    <el-tabs v-model="tab" @tab-change="load">
      <el-tab-pane label="景点" name="ATTRACTION">
        <el-button type="primary" @click="openCreate">新增景点</el-button>
        <el-table :data="attractions" style="width: 100%; margin-top: 12px">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="city" label="城市" />
          <el-table-column prop="price" label="票价" width="100" />
          <el-table-column prop="active" label="上架" width="80">
            <template #default="{ row }">{{ row.active ? '是' : '否' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="openEditA(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="removeA(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="酒店" name="HOTEL">
        <el-button type="primary" @click="openCreate">新增酒店</el-button>
        <el-table :data="hotels" style="width: 100%; margin-top: 12px">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="city" label="城市" />
          <el-table-column prop="pricePerNight" label="每晚" width="100" />
          <el-table-column prop="active" label="上架" width="80">
            <template #default="{ row }">{{ row.active ? '是' : '否' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="openEditH(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="removeH(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dlg" :title="editing === 'create' ? '新增' : '编辑'" width="560px">
      <el-form label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item :label="tab === 'ATTRACTION' ? '票价' : '每晚价格'">
          <el-input v-model="form.price" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="form.keywordTags" placeholder="逗号分隔" />
        </el-form-item>
        <el-form-item label="上架">
          <el-switch v-model="form.active" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
