import api from './axios';

// Login: POST /api/v1/auth/login
export const login = async (loginId, password) => {
    const response = await api.post('/auth/login', { loginId, password });
    return response.data; // Expected: { success: true, data: { accessToken, refreshToken } }
};

// Signup: POST /api/v1/auth/signup
export const signup = async (data) => {
    // data: { loginId, password, name, email }
    // Backend DTO: loginId, password, username, email, userRole, adminToken
    const payload = {
        loginId: data.loginId,
        password: data.password,
        username: data.name, // Map name -> username
        email: data.email,
        userRole: data.userRole // 'HQ' or 'FRANCHISE'
    };

    if (data.userRole === 'HQ' && data.adminToken) {
        payload.adminToken = data.adminToken;
    }

    const response = await api.post('/auth/signup', payload);
    return response.data;
};

// Logout: POST /api/v1/auth/logout
export const logout = async (refreshToken) => {
    const response = await api.post('/auth/logout', { refreshToken });
    return response.data;
};
