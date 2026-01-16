import api from './axios';

const API_BASE_URL = '/orders';

export const orderApi = {
    // 주문 생성
    createOrder: async (storeId, orderData) => {
        console.log('[orderApi] createOrder called', { storeId, orderData });
        try {
            const response = await api.post(`${API_BASE_URL}/${storeId}`, orderData);
            console.log('[orderApi] createOrder response success', response.data);
            return response.data; // ApiResponse { success, data, error }
        } catch (error) {
            console.error('Error creating order:', error);
            throw error;
        }
    },

    // 주문 조회 (Status: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
    getOrders: async (storeId, status) => {
        try {
            const response = await api.get(`${API_BASE_URL}/${status}/${storeId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching orders:', error);
            throw error;
        }
    },

    // 주문 상세 조회
    getOrderDetails: async (storeId, orderId) => {
        try {
            const response = await api.get(`${API_BASE_URL}/${storeId}/${orderId}/details`);
            return response.data;
        } catch (error) {
            console.error('Error fetching order details:', error);
            throw error;
        }
    },

    // 주문 취소
    cancelOrder: async (storeId, orderId) => {
        try {
            const response = await api.patch(`${API_BASE_URL}/${storeId}/${orderId}`);
            return response.data;
        } catch (error) {
            console.error('Error cancelling order:', error);
            throw error;
        }
    }
};
