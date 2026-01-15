import { createRouter, createWebHistory } from 'vue-router'
import InventoryPage from '../features/inventory/pages/InventoryPage.vue'
import LoginView from '../features/user/LoginView.vue'
import SignupView from '../features/user/SignupView.vue'
import ProfileView from '../features/user/ProfileView.vue'
import AdminUserView from '../features/user/AdminUserView.vue'
import ProductListView from '../features/order/ProductListView.vue'
import CartListView from '../features/order/CartListView.vue'
import OrderListView from '../features/order/OrderListView.vue'
import MonthlySettlement from '../features/settlement/pages/MonthlySettlement.vue'
import DailySettlement from '../features/settlement/pages/DailySettlement.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            // For now, redirect home to inventory or show a welcome page.
            // Let's keep it simple and redirect to inventory as it's the main feature we built.
            redirect: '/inventory'
        },
        {
            path: '/inventory',
            name: 'inventory',
            component: InventoryPage,
            meta: { requiresAuth: true }
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView,
            meta: { guestOnly: true }
        },
        {
            path: '/signup',
            name: 'signup',
            component: SignupView,
            meta: { guestOnly: true }
        },
        {
            path: '/profile',
            name: 'profile',
            component: ProfileView,
            meta: { requiresAuth: true }
        },
        {
            path: '/admin/search',
            name: 'admin-search',
            component: AdminUserView,
            meta: {
                requiresAuth: true,
                roles: ['ADMIN']
            }
        },
        {
            path: '/products',
            name: 'products',
            component: ProductListView,
            meta: { requiresAuth: true }
        },
        {
            path: '/cart',
            name: 'cart',
            component: CartListView,
            meta: { requiresAuth: true }
        },
        {
            path: '/orders',
            name: 'orders',
            component: OrderListView,
            meta: { requiresAuth: true }
        },
        {
            path: '/settlements',
            redirect: '/settlements/daily'
        },
        {
            path: '/settlements/daily',
            name: 'DailySettlement',
            component: DailySettlement
        },
        {
            path: '/settlements/monthly',
            name: 'MonthlySettlement',
            component: MonthlySettlement
        }
    ]
})

export default router
