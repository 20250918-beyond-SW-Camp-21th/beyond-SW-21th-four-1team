import api from './axios';

const API_BASE_URL = '/cart-items';

export const cartApi = {
    // 장바구니 추가
    addCartItem: async (storeId, items) => {
        try {
            const response = await api.post(`${API_BASE_URL}/${storeId}`, items);
            return response.data;
        } catch (error) {
            console.error('Error adding to cart:', error);
            throw error;
        }
    },

    // 장바구니 조회
    getCartItems: async (storeId) => {
        try {
            const response = await api.get(`${API_BASE_URL}/${storeId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching cart items:', error);
            throw error;
        }
    },

    // 장바구니 상품 삭제
    deleteCartItem: async (storeId, cartItemId) => {
        try {
            const response = await api.delete(`${API_BASE_URL}/${storeId}/${cartItemId}`);
            return response.data;
        } catch (error) {
            console.error('Error deleting cart item:', error);
            throw error;
        }
    }
};
