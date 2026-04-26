import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import http from '@/api/http'
import type { AuthResponse, UserProfile, UserRole } from '@/types/api'

const LS_TOKEN = 'tourism_token'
const LS_USER = 'tourism_user'

function loadUser(): { userId: number; username: string; role: UserRole } | null {
  const raw = localStorage.getItem(LS_USER)
  if (!raw) return null
  try {
    return JSON.parse(raw) as { userId: number; username: string; role: UserRole }
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(LS_TOKEN))
  const cached = loadUser()
  const userId = ref<number | null>(cached?.userId ?? null)
  const username = ref<string | null>(cached?.username ?? null)
  const role = ref<UserRole | null>(cached?.role ?? null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ADMIN')

  function persist(res: AuthResponse) {
    token.value = res.token
    userId.value = res.userId
    username.value = res.username
    role.value = res.role
    localStorage.setItem(LS_TOKEN, res.token)
    localStorage.setItem(
      LS_USER,
      JSON.stringify({ userId: res.userId, username: res.username, role: res.role }),
    )
  }

  function clear() {
    token.value = null
    userId.value = null
    username.value = null
    role.value = null
    localStorage.removeItem(LS_TOKEN)
    localStorage.removeItem(LS_USER)
  }

  async function login(payload: { username: string; password: string }) {
    const { data } = await http.post<AuthResponse>('/auth/login', payload)
    persist(data)
    return data
  }

  async function register(payload: {
    username: string
    email: string
    password: string
    nickname?: string
  }) {
    const { data } = await http.post<AuthResponse>('/auth/register', payload)
    persist(data)
    return data
  }

  function logout() {
    clear()
  }

  async function fetchProfile(): Promise<UserProfile> {
    const { data } = await http.get<UserProfile>('/users/me')
    return data
  }

  return {
    token,
    userId,
    username,
    role,
    isAuthenticated,
    isAdmin,
    login,
    register,
    logout,
    fetchProfile,
    persist,
    clear,
  }
})
