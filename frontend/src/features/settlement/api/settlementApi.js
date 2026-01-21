import api from '@/api/axios';

export const settlementApi = {
    // 일별 정산 조회
    async getDailySettlement(storeId, productId, date) {
        console.log(`Fetching daily settlement for store ${storeId}, product ${productId} on ${date}...`);
        try {
            const response = await api.get('/settlements/daily', {
                params: { storeId, productId, date }
            });
            console.log("Daily Settlement API Response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching daily settlement:', error);
            throw error;
        }
    },

    // 월별 정산 조회
    async getMonthlySettlement(storeId, productId, yearMonth) {
        console.log(`Fetching monthly settlement for store ${storeId}, product ${productId} for ${yearMonth}...`);
        try {
            const response = await api.get('/settlements/monthly', {
                params: { storeId, productId, yearMonth }
            });
            console.log("Monthly Settlement API Response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching monthly settlement:', error);
            throw error;
        }
    },

    // 정산 목록 조회
    async getSettlementList(storeId) {
        console.log(`Fetching settlement list for store ${storeId}...`);
        try {
            const response = await api.get('/settlements/list', {
                params: { storeId }
            });
            console.log("Settlement List API Response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching settlement list:', error);
            throw error;
        }
    },

    // 정산 포함 주문 상세 조회
    async getSettlementOrderDetails(storeId, date) {
        console.log(`Fetching settlement order details for store ${storeId} on ${date}...`);
        try {
            const response = await api.get('/settlements/details', {
                params: { storeId, date }
            });
            console.log("Settlement Order Details API Response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching settlement order details:', error);
            throw error;
        }
    },

    // 기간별 정산 통계 조회
    async getSettlementStats(storeId, startDate, endDate) {
        console.log(`Fetching settlement stats for store ${storeId} from ${startDate} to ${endDate}...`);
        try {
            const response = await api.get('/settlements/stats', {
                params: { storeId, startDate, endDate }
            });
            console.log("Settlement Stats API Response:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching settlement stats:', error);
            throw error;
        }
    },

    // 월별 정산 PDF 다운로드
    async downloadMonthlyPdf(storeId, productId, yearMonth) {
        console.log(`Downloading monthly PDF for store ${storeId}, product ${productId} for ${yearMonth}...`);
        try {
            const response = await api.get('/settlements/monthly/download', {
                params: { storeId, productId, yearMonth },
                responseType: 'blob'
            });

            // Create a download link
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const a = document.createElement('a');
            a.href = url;
            a.download = `월별영수증_${yearMonth}.pdf`;
            document.body.appendChild(a);
            a.click();

            // Cleanup
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);

            console.log("Monthly PDF downloaded successfully");
            return true;
        } catch (error) {
            console.error('Error downloading monthly PDF:', error);
            throw error;
        }
    },

    // 일별 정산 PDF 다운로드
    async downloadDailyPdf(storeId, productId, date) {
        console.log(`Downloading daily PDF for store ${storeId}, product ${productId} on ${date}...`);
        try {
            const response = await api.get('/settlements/daily/download', {
                params: { storeId, productId, date },
                responseType: 'blob'
            });

            // Create a download link
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const a = document.createElement('a');
            a.href = url;
            a.download = `일별영수증_${date}.pdf`;
            document.body.appendChild(a);
            a.click();

            // Cleanup
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);

            console.log("Daily PDF downloaded successfully");
            return true;
        } catch (error) {
            console.error('Error downloading daily PDF:', error);
            throw error;
        }
    },

    // ID로 PDF 다운로드
    async downloadPdfById(settlementId) {
        console.log(`Downloading PDF for settlement ID ${settlementId}...`);
        try {
            const response = await api.get(`/settlements/${settlementId}/download`, {
                responseType: 'blob'
            });

            // Extract filename from Content-Disposition header or use default
            const contentDisposition = response.headers['content-disposition'];
            let fileName = `정산영수증_${settlementId}.pdf`;

            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
                if (fileNameMatch && fileNameMatch[1]) {
                    fileName = fileNameMatch[1].replace(/['"]/g, '');
                }
            }

            // Create a download link
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();

            // Cleanup
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);

            console.log("PDF downloaded successfully by ID");
            return true;
        } catch (error) {
            console.error('Error downloading PDF by ID:', error);
            throw error;
        }
    },

    // 일일 정산 생성
    async createSettlement(storeId, productId, date) {
        console.log(`Creating settlement for store ${storeId}, product ${productId} on ${date}...`);
        try {
            const response = await api.post('/settlements/generate', {
                storeId,
                productId,
                date
            });
            console.log("Settlement created successfully:", response.data);
            return response.data;
        } catch (error) {
            console.error('Error creating settlement:', error);
            throw error;
        }
    },

    // 주문 목록 조회 (상태별)
    async getOrdersByStatus(storeId, status = 'ALL') {
        console.log(`Fetching orders for store ${storeId} with status ${status}...`);
        try {
            const response = await api.get(`/orders/${status}/${storeId}`);
            console.log('Orders API Response:', response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching orders:', error);
            throw error;
        }
    }
};
