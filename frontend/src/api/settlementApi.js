import axios from 'axios';

const API_BASE_URL = 'http://localhost:8000/api/v1/settlements';

export const settlementApi = {
    // 일별 정산 조회
    getDailySettlement: async (storeId, date) => {
        try {
            const response = await axios.get(`${API_BASE_URL}/daily`, {
                params: { storeId, date }
            });
            return response.data;
        } catch (error) {
            console.error('Error fetching daily settlement:', error);
            throw error;
        }
    },

    // 월별 정산 조회
    getMonthlySettlement: async (storeId, yearMonth) => {
        try {
            const response = await axios.get(`${API_BASE_URL}/monthly`, {
                params: { storeId, yearMonth }
            });
            return response.data;
        } catch (error) {
            console.error('Error fetching monthly settlement:', error);
            throw error;
        }
    },

    // 월별 정산 PDF 다운로드
    downloadMonthlyPdf: async (storeId, yearMonth) => {
        try {
            const response = await axios.get(`${API_BASE_URL}/monthly/download`, {
                params: { storeId, yearMonth },
                responseType: 'blob'
            });

            // Create download link
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `settlement_${yearMonth}.pdf`);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);

            return response.data;
        } catch (error) {
            console.error('Error downloading PDF:', error);
            throw error;
        }
    },

    // 일일 정산 생성
    createSettlement: async (storeId, date) => {
        try {
            const response = await axios.post(`${API_BASE_URL}/generate`, {
                storeId,
                date
            });
            return response.data;
        } catch (error) {
            console.error('Error creating settlement:', error);
            throw error;
        }
    }
};
