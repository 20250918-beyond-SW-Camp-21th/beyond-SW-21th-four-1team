<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { signup } from '@/api/auth.js';

const router = useRouter();
const form = ref({
    loginId: '',
    password: '',
    passwordConfirm: '',
    name: '',
    email: '',
    userRole: 'FRANCHISE', // Default to Franchise
    adminToken: ''
});
const isLoading = ref(false);
const errorMsg = ref('');

const isPwMismatch = computed(() => {
    return form.value.password && form.value.passwordConfirm && 
           form.value.password !== form.value.passwordConfirm;
});

const handleSignup = async () => {
    if (isPwMismatch.value) return;
    
    // HQ check
    if (form.value.userRole === 'HQ' && !form.value.adminToken) {
        errorMsg.value = '본사 관리자 가입 시 시크릿 키는 필수입니다.';
        return;
    }

    isLoading.value = true;
    errorMsg.value = '';
    
    try {
        const res = await signup(form.value);
        if (res.success) {
            alert('회원가입이 완료되었습니다.');
            router.push('/login');
        } else {
            errorMsg.value = res.error || '가입 실패';
        }
    } catch (e) {
        // e.response.data looks like { success: false, data: null, error: '...' }
        if (e.response && e.response.data && e.response.data.error) {
            errorMsg.value = e.response.data.error;
        } else if (e.response && e.response.data && e.response.data.message) {
            // Sometimes GlobalExceptionHandler might fallback to message
             errorMsg.value = e.response.data.message;
        } else {
            errorMsg.value = '가입 중 오류가 발생했습니다.';
        }
    } finally {
        isLoading.value = false;
    }
};
</script>

<template>
  <div class="tteokbokki-app">
    <div class="auth-container">
      <div class="auth-decor">🥘</div>
      
      <div class="premium-card auth-card">
        <header class="auth-header">
          <div class="brand">
            <span class="brand-icon">🌶️</span>
            <h1 class="brand-name">회원가입</h1>
          </div>
          <p class="auth-subtitle">함께 맛있는 서비스를 만들어가요!</p>
        </header>

        <form @submit.prevent="handleSignup" class="auth-form">
          <!-- Role Selection -->
          <div class="role-selection">
              <label class="role-card" :class="{ active: form.userRole === 'FRANCHISE' }">
                  <input type="radio" value="FRANCHISE" v-model="form.userRole" class="hidden-radio">
                  <span class="role-icon">🏪</span>
                  <span class="role-name">가맹점 셰프</span>
              </label>
              <label class="role-card" :class="{ active: form.userRole === 'HQ' }">
                  <input type="radio" value="HQ" v-model="form.userRole" class="hidden-radio">
                  <span class="role-icon">🏢</span>
                  <span class="role-name">본사 매니저</span>
              </label>
          </div>

          <div class="input-group">
            <label>아이디</label>
            <div class="input-wrapper premium-input">
              <span class="input-icon">👤</span>
              <input v-model="form.loginId" placeholder="아이디를 입력하세요" required />
            </div>
          </div>

          <div class="input-group">
            <label>이름</label>
            <div class="input-wrapper premium-input">
              <span class="input-icon">📛</span>
              <input v-model="form.name" placeholder="이름을 입력하세요" required />
            </div>
          </div>

          <div class="input-group">
            <label>이메일</label>
            <div class="input-wrapper premium-input">
              <span class="input-icon">📧</span>
              <input v-model="form.email" type="email" placeholder="이메일을 입력하세요" required />
            </div>
          </div>

          <div class="input-group">
            <label>비밀번호</label>
            <div class="input-wrapper premium-input">
              <span class="input-icon">🔒</span>
              <input v-model="form.password" type="password" placeholder="비밀번호" required />
            </div>
          </div>

          <div class="input-group">
            <label>비밀번호 확인</label>
            <div class="input-wrapper premium-input" :class="{ 'error-border': isPwMismatch }">
              <span class="input-icon">🔐</span>
              <input 
                  v-model="form.passwordConfirm" 
                  type="password" 
                  placeholder="비밀번호 재입력" 
                  required 
              />
            </div>
            <span v-if="isPwMismatch" class="error-text">비밀번호가 일치하지 않습니다.</span>
          </div>

          <!-- Confirm Token (Only for HQ) -->
          <div class="input-group" v-if="form.userRole === 'HQ'">
            <label class="highlight-label">본사 시크릿 키</label>
            <div class="input-wrapper premium-input highlight-input">
              <span class="input-icon">🔑</span>
              <input 
                v-model="form.adminToken" 
                type="password" 
                placeholder="시크릿 키를 입력하세요" 
                required
              />
            </div>
          </div>

          <div v-if="errorMsg" class="error-msg premium-card">
              <span>🔥</span> {{ errorMsg }}
          </div>

          <button class="btn-spicy submit-btn" :disabled="isLoading || isPwMismatch">
            {{ isLoading ? '조리 중...' : '가입 완료하기' }}
          </button>
        </form>

        <div class="auth-footer">
          이미 계정이 있으신가요? 
          <RouterLink to="/login" class="link-text">로그인</RouterLink>
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
  padding: 2rem 1rem; /* Added padding for smaller screens */
  overflow-y: auto; /* Allow scrolling if content is tall */
}

.auth-container {
  width: 100%;
  max-width: 520px;
  position: relative;
  z-index: 10;
  margin: auto; /* Center with flexbox but safe margin */
  animation: pop 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.auth-decor {
  position: absolute;
  top: -50px;
  left: -40px;
  font-size: 6rem;
  opacity: 0.15;
  transform: rotate(-15deg);
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

.role-selection {
    display: flex;
    justify-content: center;
    gap: 1rem;
    margin-bottom: 2rem;
}

.hidden-radio {
    display: none;
}

.role-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    border: 2px solid var(--border-color);
    border-radius: 16px;
    background: #fdfafa;
    cursor: pointer;
    transition: all 0.2s;
    height: 100px;
}

.role-card:hover {
    transform: translateY(-2px);
    background: white;
    border-color: var(--sauce-orange);
}

.role-card.active {
    background: #fff7ed;
    border-color: var(--sauce-orange);
    color: var(--deep-brown);
    box-shadow: 0 4px 0 rgba(249, 115, 22, 0.2);
}

.role-icon {
    font-size: 1.8rem;
    margin-bottom: 0.5rem;
}

.role-name {
    font-weight: 800;
    font-size: 0.95rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.input-group label {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--deep-brown);
  margin-left: 0.25rem;
}

.highlight-label {
    color: var(--spicy-red) !important;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: #fdfafa;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  padding: 0 1rem;
  height: 50px;
  transition: all 0.2s;
}

.input-wrapper:focus-within {
  border-color: var(--sauce-orange);
  background: white;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

.highlight-input {
    border-color: #fecaca;
    background: #fff1f2;
}
.highlight-input:focus-within {
    border-color: var(--spicy-red);
    box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.1);
}

.error-border {
    border-color: #e11d48 !important;
    background: #fff1f2;
}

.input-icon {
  font-size: 1.1rem;
  margin-right: 0.75rem;
  opacity: 0.6;
}

.input-wrapper input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 0.95rem;
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

.error-text {
    color: #e11d48;
    font-size: 0.8rem;
    font-weight: 700;
    margin-left: 0.5rem;
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
  box-shadow: none;
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
