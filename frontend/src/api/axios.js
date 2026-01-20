import axios from 'axios';

// Create axios instance
const api = axios.create({
    baseURL: 'http://localhost:8080/api/v1',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor: add access token header
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Flag to prevent multiple alerts/redirects
let isRedirecting = false;

// Response interceptor
api.interceptors.response.use(
    (response) => {
        // Check for business logic failure if success is false
        if (response.data && response.data.success === false) {
            // You might choose to reject here, or handle it in the component.
            // For consistency with catch blocks, we can reject.
            return Promise.reject({
                response: {
                    data: {
                        message: response.data.error || 'Unknown Error'
                    }
                }
            });
        }
        return response;
    },
    (error) => {
        // Basic error logging
        if (error.response) {
            console.error('API Error Status:', error.response.status);
            console.error('API Error Data:', error.response.data);

            // Access Token is invalid or expired
            if (error.response.status === 401 || error.response.status === 403) {
                // Prevent infinite loops and double alerts
                if (!window.location.pathname.startsWith('/login') && !isRedirecting) {
                    isRedirecting = true;
                    alert('세션이 만료되었거나 접근 권한이 없습니다. 다시 로그인해주세요.');
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('refreshToken');
                    window.location.href = '/login';
                }
            }
        } else if (error.request) {
            console.error('API No Response:', error.request);
        } else {
            console.error('API Error Config:', error.message);
        }
        return Promise.reject(error);
    }
);

export default api;
