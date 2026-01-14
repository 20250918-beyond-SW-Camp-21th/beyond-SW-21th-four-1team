import { createRouter, createWebHistory } from 'vue-router'
import InventoryPage from '../features/inventory/pages/InventoryPage.vue'

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
        }
    ]
})

export default router
