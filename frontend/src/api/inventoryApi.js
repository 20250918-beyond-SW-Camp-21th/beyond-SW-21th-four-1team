import axios from 'axios';

const API_BASE_URL = '/api/v1/inventory';

export const inventoryApi = {
    // 전체 상품 조회
    getAllProducts: async () => {
        try {
            const response = await axios.get(API_BASE_URL);
            return response.data; // ApiResponse { success, data: { products: [] } }
        } catch (error) {
            console.error('Error fetching products:', error);
            throw error;
        }
    },

    // 상품 상세 조회
    searchProduct: async (id) => {
        try {
            const response = await axios.get(`${API_BASE_URL}/${id}`);
            return response.data;
        } catch (error) {
            console.error('Error searching product:', error);
            throw error;
        }
    }
};
