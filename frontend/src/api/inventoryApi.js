import api from './axios';

const API_BASE_URL = '/inventory';

export const inventoryApi = {
    // 전체 상품 조회
    getAllProducts: async () => {
        try {
            const response = await api.get(API_BASE_URL);
            return response.data; // ApiResponse { success, data: { products: [] } }
        } catch (error) {
            console.error('Error fetching products:', error);
            throw error;
        }
    },

    // 상품 상세 조회
    searchProduct: async (id) => {
        try {
            const response = await api.get(`${API_BASE_URL}/${id}`);
            return response.data;
        } catch (error) {
            console.error('Error searching product:', error);
            throw error;
        }
    },

    // 이름으로 상품 검색
    searchByName: async (name) => {
        try {
            const response = await api.get(`${API_BASE_URL}/search`, { params: { name } });
            return response.data;
        } catch (error) {
            console.error('Error searching by name:', error);
            throw error;
        }
    },

    // 재고 입고
    inbound: async (data) => {
        try {
            const response = await api.post(`${API_BASE_URL}/inbound`, data);
            return response.data;
        } catch (error) {
            console.error('Error inbounding inventory:', error);
            throw error;
        }
    },

    // 재고 출고
    outbound: async (data) => {
        try {
            const response = await api.post(`${API_BASE_URL}/outbound`, data);
            return response.data;
        } catch (error) {
            console.error('Error outbounding inventory:', error);
            throw error;
        }
    }
};
