import { createRouter, createWebHistory } from 'vue-router'
import InventoryPage from '../features/inventory/pages/InventoryPage.vue'
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
            component: InventoryPage
        },
        {
            path: '/products',
            name: 'products',
            component: ProductListView
        },
        {
            path: '/cart',
            name: 'cart',
            component: CartListView
        },
        {
            path: '/orders',
            name: 'orders',
            component: OrderListView
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
