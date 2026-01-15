const BASE_URL = '/api/v1/settlements';

const getHeaders = () => {
    return {
        'Content-Type': 'application/json',
    };
};

export const settlementApi = {
    // 일별 정산 조회
    async getDailySettlement(storeId, date) {
        console.log(`Fetching daily settlement for store ${storeId} on ${date}...`);
        try {
            const response = await fetch(`${BASE_URL}/daily?storeId=${storeId}&date=${date}`, {
                method: 'GET',
                headers: getHeaders(),
            });

            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("text/html")) {
                throw new Error("Received HTML response. CORS/Proxy issue likely. Please RESTART 'npm run dev'.");
            }

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `Network response was not ok: ${response.status}`);
            }

            const data = await response.json();
            console.log("Daily Settlement API Response:", data);
            return data.data || data;
        } catch (error) {
            console.error('Error fetching daily settlement:', error);
            throw error;
        }
    },

    // 월별 정산 조회
    async getMonthlySettlement(storeId, yearMonth) {
        console.log(`Fetching monthly settlement for store ${storeId} for ${yearMonth}...`);
        try {
            const response = await fetch(`${BASE_URL}/monthly?storeId=${storeId}&yearMonth=${yearMonth}`, {
                method: 'GET',
                headers: getHeaders(),
            });

            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("text/html")) {
                throw new Error("Received HTML response. CORS/Proxy issue likely. Please RESTART 'npm run dev'.");
            }

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `Network response was not ok: ${response.status}`);
            }

            const data = await response.json();
            console.log("Monthly Settlement API Response:", data);
            return data.data || data;
        } catch (error) {
            console.error('Error fetching monthly settlement:', error);
            throw error;
        }
    },

    // 월별 정산 PDF 다운로드
    async downloadMonthlyPdf(storeId, yearMonth) {
        console.log(`Downloading PDF for store ${storeId} for ${yearMonth}...`);
        try {
            const response = await fetch(`${BASE_URL}/monthly/download?storeId=${storeId}&yearMonth=${yearMonth}`, {
                method: 'GET',
            });

            if (!response.ok) {
                throw new Error(`PDF download failed: ${response.status}`);
            }

            // Get the blob data
            const blob = await response.blob();

            // Create a download link
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `settlement_${yearMonth}.pdf`;
            document.body.appendChild(a);
            a.click();

            // Cleanup
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);

            console.log("PDF downloaded successfully");
            return true;
        } catch (error) {
            console.error('Error downloading PDF:', error);
            throw error;
        }
    },

    // 일일 정산 생성
    async createSettlement(storeId, date) {
        console.log(`Creating settlement for store ${storeId} on ${date}...`);
        try {
            const response = await fetch(`${BASE_URL}/generate`, {
                method: 'POST',
                headers: getHeaders(),
                body: JSON.stringify({ storeId, date }),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `Network response was not ok: ${response.status}`);
            }

            console.log("Settlement created successfully");
            return true;
        } catch (error) {
            console.error('Error creating settlement:', error);
            throw error;
        }
    }
};
