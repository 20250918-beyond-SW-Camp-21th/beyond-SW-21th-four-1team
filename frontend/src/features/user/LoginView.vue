<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login } from '@/api/auth.js';

const router = useRouter();
const loginId = ref('');
const password = ref('');
const errorMsg = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
    if (!loginId.value || !password.value) {
        errorMsg.value = '아이디와 비밀번호를 입력해주세요.';
        return;
    }
    isLoading.value = true;
    errorMsg.value = '';

    try {
        const response = await login(loginId.value, password.value);
        if (response.success) {
            localStorage.setItem('accessToken', response.data.accessToken);
            localStorage.setItem('refreshToken', response.data.refreshToken);
            router.push('/');
        } else {
            errorMsg.value = response.error || '로그인 실패';
        }
    } catch (error) {
        console.error(error);
        const msg = error.response?.data?.message || '로그인 실패';
        errorMsg.value = msg;
    } finally {
        isLoading.value = false;
    }
};
</script>

<template>
  
  <div class="tteokbokki-app">
    <div class="auth-container">
      <!-- Decorator -->
      <div class="auth-decor">🌶️</div>
      <div class="premium-card auth-card">
        <header class="auth-header">
          <div class="brand">
            <span class="brand-icon">🌶️</span>
            <h1 class="brand-name">SPICY</h1>
          </div>
          <p class="auth-subtitle">맛있는 서비스 이용을 위해 로그인해주세요</p>
        </header>

        <form @submit.prevent="handleLogin" class="auth-form">
          <div class="input-group">
            <label>아이디</label>
            <div class="input-wrapper premium-input">
              <span class="input-icon">👤</span>
              <input 
                v-model="loginId" 
                type="text" 
                placeholder="아이디를 입력하세요" 
                :disabled="isLoading" 
              />
            </div>
          </div>

          <div class="input-group">
            <label>비밀번호</label>
            <div class="input-wrapper premium-input">
              <span class="input-icon">🔒</span>
              <input 
                v-model="password" 
                type="password" 
                placeholder="비밀번호를 입력하세요" 
                :disabled="isLoading" 
              />
            </div>
          </div>

          <div v-if="errorMsg" class="error-msg premium-card">
            <span>🔥</span> {{ errorMsg }}
          </div>

          <button class="btn-spicy submit-btn" :disabled="isLoading">
            {{ isLoading ? '요리 중...' : '로그인하기' }}
          </button>
        </form>

        <div class="auth-footer">
          아직 셰프가 아니신가요? 
          <RouterLink to="/signup" class="link-text">회원가입</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tteokbokki-app {
  min-height: 100vh;
  background-color: var(--rice-cream);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.auth-container {
  width: 100%;
  max-width: 480px;
  padding: 1rem;
  position: relative;
  z-index: 10;
  animation: pop 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.auth-decor {
  position: absolute;
  top: -60px;
  right: -40px;
  font-size: 8rem;
  opacity: 0.1;
  transform: rotate(15deg);
  z-index: -1;
  pointer-events: none;
}

.auth-card {
  padding: 2.5rem 2rem;
  background: white;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.brand-icon {
  font-size: 2rem;
  filter: drop-shadow(0 2px 4px rgba(225, 29, 72, 0.2));
}

.brand-name {
  font-size: 2rem;
  font-weight: 900;
  color: var(--deep-brown);
  margin: 0;
  letter-spacing: -0.05em;
}

.auth-subtitle {
  color: var(--text-muted);
  font-weight: 600;
  margin: 0;
  font-size: 1rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-group label {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--deep-brown);
  margin-left: 0.25rem;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: #fdfafa;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  padding: 0 1rem;
  height: 54px;
  transition: all 0.2s;
}

.input-wrapper:focus-within {
  border-color: var(--sauce-orange);
  background: white;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

.input-icon {
  font-size: 1.2rem;
  margin-right: 0.75rem;
  opacity: 0.6;
}

.input-wrapper input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 1rem;
  font-weight: 600;
  color: var(--deep-brown);
  height: 100%;
}

.input-wrapper input::placeholder {
  color: #d6cfcd;
}

.input-wrapper input:focus {
  outline: none;
}

.error-msg {
  background-color: #fff1f2;
  border: 1px solid #fecaca;
  color: #e11d48;
  padding: 0.75rem 1rem;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: none; /* Override premium-card shadow if needed, or keep it */
}

.submit-btn {
  width: 100%;
  height: 56px;
  font-size: 1.1rem;
  margin-top: 1rem;
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  filter: grayscale(1);
}

.auth-footer {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 2px dashed var(--border-color);
  text-align: center;
  color: var(--text-muted);
  font-weight: 600;
  font-size: 0.95rem;
}

.link-text {
  color: var(--spicy-red);
  font-weight: 800;
  text-decoration: none;
  margin-left: 0.25rem;
}

.link-text:hover {
  text-decoration: underline;
}
</style>
