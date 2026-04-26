import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('@/views/HomeView.vue') },
        {
          path: 'attractions',
          name: 'attractions',
          component: () => import('@/views/AttractionsView.vue'),
        },
        {
          path: 'attractions/:id',
          name: 'attraction-detail',
          component: () => import('@/views/AttractionDetailView.vue'),
        },
        {
          path: 'hotels',
          name: 'hotels',
          component: () => import('@/views/HotelsView.vue'),
        },
        {
          path: 'hotels/:id',
          name: 'hotel-detail',
          component: () => import('@/views/HotelDetailView.vue'),
        },
        {
          path: 'recommendations',
          name: 'recommendations',
          meta: { requiresAuth: true },
          component: () => import('@/views/RecommendationsView.vue'),
        },
        {
          path: 'orders',
          name: 'orders',
          meta: { requiresAuth: true },
          component: () => import('@/views/OrdersView.vue'),
        },
        {
          path: 'profile',
          name: 'profile',
          meta: { requiresAuth: true },
          component: () => import('@/views/ProfileView.vue'),
        },
        {
          path: 'admin',
          meta: { requiresAuth: true, requiresAdmin: true },
          component: () => import('@/layouts/AdminLayout.vue'),
          children: [
            {
              path: '',
              name: 'admin-dashboard',
              component: () => import('@/views/admin/AdminDashboardView.vue'),
            },
            {
              path: 'users',
              name: 'admin-users',
              component: () => import('@/views/admin/AdminUsersView.vue'),
            },
            {
              path: 'orders',
              name: 'admin-orders',
              component: () => import('@/views/admin/AdminOrdersView.vue'),
            },
            {
              path: 'products',
              name: 'admin-products',
              component: () => import('@/views/admin/AdminProductsView.vue'),
            },
          ],
        },
      ],
    },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    { path: '/register', name: 'register', component: () => import('@/views/RegisterView.vue') },
  ],
})

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }
  if (to.meta.requiresAdmin && !auth.isAdmin) {
    next({ name: 'home' })
    return
  }
  next()
})

export default router
