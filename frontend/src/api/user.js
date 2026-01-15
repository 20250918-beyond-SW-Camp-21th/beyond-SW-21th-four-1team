import api from './axios';

// Get Me: GET /api/v1/users/me
export const getMe = async () => {
    const response = await api.get('/users/me');
    return response.data; // Expected: { success: true, data: { loginId, username, email, userRole } }
};

// Get User (HQ Only): GET /api/v1/users/{userLoginId}
export const getUser = async (loginId) => {
    // loginId is a String now
    const response = await api.get(`/users/${loginId}`);
    return response.data;
};

// Update Me: PATCH /api/v1/users/me
export const updateMe = async (data) => {
    // data: { name, email } -> map to username
    const response = await api.patch('/users/me', {
        username: data.name,
        email: data.email
    });
    return response.data;
};

// Update Password: PATCH /api/v1/users/me/password
export const updatePassword = async (oldPassword, newPassword) => {
    // Backend expects 'currentPassword' not 'oldPassword'
    const response = await api.patch('/users/me/password', {
        currentPassword: oldPassword,
        newPassword
    });
    return response.data;
};

// Withdraw: DELETE /api/v1/users/me
export const withdraw = async () => {
    const response = await api.delete('/users/me');
    return response.data;
};
