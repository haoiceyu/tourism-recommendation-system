export type UserRole = 'USER' | 'ADMIN'

export interface AuthResponse {
  token: string
  userId: number
  username: string
  role: UserRole
}

export interface UserProfile {
  id: number
  username: string
  email: string
  nickname: string | null
  phone: string | null
  role: UserRole
  createdAt: string
}

export type ProductType = 'ATTRACTION' | 'HOTEL'

export type OrderStatus = 'PENDING_PAYMENT' | 'PAID' | 'COMPLETED' | 'CANCELLED'

export interface OrderItem {
  id: number
  productType: ProductType
  productId: number
  title: string
  unitPrice: string
  quantity: number
  subtotal: string
}

export interface Order {
  id: number
  orderNo: string
  totalAmount: string
  status: OrderStatus
  createdAt: string
  paidAt: string | null
  completedAt: string | null
  cancelledAt: string | null
  items: OrderItem[]
}

export interface DashboardStats {
  totalUsers: number
  newUsersToday: number
  pendingPaymentOrders: number
  paidOrders: number
  completedOrders: number
  cancelledOrders: number
  revenueToday: string
}
