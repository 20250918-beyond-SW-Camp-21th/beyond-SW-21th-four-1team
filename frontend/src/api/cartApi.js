import axios from 'axios';

const API_BASE_URL = '/api/v1/cart-items';

export const cartApi = {
    // 장바구니 추가
    addCartItem: async (userId, storeId, items) => {
        try {
            const response = await axios.post(`${API_BASE_URL}/${userId}/${storeId}`, items);
            return response.data;
        } catch (error) {
            console.error('Error adding to cart:', error);
            throw error;
        }
    },

    // 장바구니 조회
    getCartItems: async (userId, storeId) => {
        try {
            const response = await axios.get(`${API_BASE_URL}/${userId}/${storeId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching cart items:', error);
            throw error;
        }
    },

    // 장바구니 상품 삭제
    deleteCartItem: async (userId, storeId, cartItemId) => {
        try {
            const response = await axios.delete(`${API_BASE_URL}/${userId}/${storeId}/${cartItemId}`);
            return response.data;
        } catch (error) {
            console.error('Error deleting cart item:', error);
            throw error;
        }
    }
};
