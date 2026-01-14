const BASE_URL = '/api/v1/inventory';

const getHeaders = () => {
    return {
        'Content-Type': 'application/json',
    };
};

export const inventoryApi = {
    // 전체 상품 조회
    async getAllProducts() {
        console.log(`Fetching products from ${BASE_URL}...`);
        try {
            const response = await fetch(`${BASE_URL}`, {
                method: 'GET',
                headers: getHeaders(),
            });

            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("text/html")) {
                throw new Error("Received HTML response. CORS/Proxy issue likely. Please RESTART 'npm run dev'.");
            }

            if (!response.ok) throw new Error(`Network response was not ok: ${response.status}`);

            const data = await response.json();
            console.log("API Response:", data);

            // Backend returns { success: true, data: { products: [...] } }
            if (data.data && data.data.products) {
                return data.data.products;
            } else if (data.products) {
                return data.products;
            }

            return data.data || [];
        } catch (error) {
            console.error('Error fetching products:', error);
            throw error;
        }
    },

    async searchProduct(id) {
        try {
            const response = await fetch(`${BASE_URL}/${id}`, {
                method: 'GET',
                headers: getHeaders(),
            });
            if (!response.ok) {
                const text = await response.text();
                throw new Error(`Detail fetch failed: ${response.status} ${text}`);
            }
            const data = await response.json();
            return data.data;
        } catch (error) {
            console.error(`Error fetching product ${id}:`, error);
            throw error;
        }
    },

    // 상품 검색 (이름)
    async searchByName(name) {
        try {
            const response = await fetch(`${BASE_URL}/search?name=${encodeURIComponent(name)}`, {
                method: 'GET',
                headers: getHeaders(),
            });
            if (!response.ok) throw new Error('Network response was not ok');
            const data = await response.json();
            return data.data;
        } catch (error) {
            console.error(`Error searching product ${name}:`, error);
            throw error;
        }
    },

    // 재고 입고
    async inbound(inboundData) {
        try {
            const response = await fetch(`${BASE_URL}/inbound`, {
                method: 'POST',
                headers: getHeaders(),
                body: JSON.stringify(inboundData),
            });
            if (!response.ok) throw new Error('Network response was not ok');
            return await response.json();
        } catch (error) {
            console.error('Error during inbound:', error);
            throw error;
        }
    },

    // 재고 출고 (차감)
    async outbound(outboundData) {
        try {
            const response = await fetch(`${BASE_URL}/outbound`, {
                method: 'POST',
                headers: getHeaders(),
                body: JSON.stringify(outboundData),
            });
            if (!response.ok) throw new Error('Network response was not ok');
            return await response.json();
        } catch (error) {
            console.error('Error during outbound:', error);
            throw error;
        }
    },

    // 안전재고/수요예측 확인
    async checkDemand(productId) {
        try {
            const response = await fetch(`/api/v1/demand-plan/${productId}`, {
                method: 'GET',
                headers: getHeaders(),
            });
            if (!response.ok) {
                const text = await response.text();
                throw new Error(`Demand fetch failed: [${response.status}] ${text}`);
            }
            const data = await response.json();
            return data.data; // Returns { isOrderRequired, message }
        } catch (error) {
            console.error(`Error checking demand for product ${productId}:`, error);
            throw error; // Let the component handle/display the specific error
        }
    }
};
