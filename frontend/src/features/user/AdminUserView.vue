<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { getUser } from '@/api/user.js';

const router = useRouter();
const searchId = ref('');
const searchedUser = ref(null);
const loading = ref(false);
const errorMsg = ref('');

// Role Localization Map
const roleMap = {
    'HQ': '본사 관리자',
    'FRANCHISE': '가맹점 관리자',
    'GENERAL': '일반 회원'
};

const handleSearch = async () => {
    if (!searchId.value) {
        alert('회원 아이디를 입력해주세요.');
        return;
    }

    loading.value = true;
    errorMsg.value = '';
    searchedUser.value = null;

    try {
        const response = await getUser(searchId.value);
        if (response.success) {
            searchedUser.value = response.data;
        } else {
            errorMsg.value = response.error || '회원을 찾을 수 없습니다.';
        }
    } catch (e) {
        if (e.response && e.response.status === 404) {
            errorMsg.value = '해당 아이디를 가진 회원이 존재하지 않습니다.';
        } else if (e.response && e.response.status === 403) {
            alert('조회 권한이 없습니다.');
            router.push('/');
        } else {
            errorMsg.value = '조회 중 오류가 발생했습니다.';
        }
    } finally {
        loading.value = false;
    }
};
</script>

<template>
  <div class="tteokbokki-app">
    <div class="admin-container">
        <header class="page-header">
            <div class="header-content">
                <h2>회원 조회 (HQ)</h2>
                <p>가맹점 관리자 및 일반 회원의 정보를 조회합니다.</p>
            </div>
            <div class="header-icon">🔎</div>
        </header>

        <div class="premium-card search-box">
            <form @submit.prevent="handleSearch" class="search-form">
                <div class="search-wrapper">
                    <span class="icon">👤</span>
                    <input 
                        v-model="searchId" 
                        type="text" 
                        placeholder="조회할 회원의 로그인 아이디 입력" 
                        class="search-input"
                    />
                </div>
                <button type="submit" class="btn-spicy search-btn" :disabled="loading">
                    {{ loading ? '검색 중...' : '조회하기' }}
                </button>
            </form>
            <div v-if="errorMsg" class="error-msg">
                <span>⚠️</span> {{ errorMsg }}
            </div>
        </div>

        <transition name="pop">
            <div v-if="searchedUser" class="premium-card result-card">
                <div class="result-header">
                    <h3>회원 상세 정보</h3>
                    <span class="badge">검색 결과</span>
                </div>
                
                <div class="user-profile">
                    <div class="profile-icon">🍳</div>
                    <dl class="info-list">
                        <div class="info-row">
                            <dt>아이디</dt>
                            <dd>{{ searchedUser.loginId }}</dd>
                        </div>
                        <div class="info-row">
                            <dt>이름</dt>
                            <dd>{{ searchedUser.username }}</dd>
                        </div>
                        <div class="info-row">
                            <dt>이메일</dt>
                            <dd>{{ searchedUser.email }}</dd>
                        </div>
                        <div class="info-row">
                            <dt>권한</dt>
                            <dd class="role-text">
                                {{ roleMap[searchedUser.userRole] || searchedUser.userRole }}
                            </dd>
                        </div>
                    </dl>
                </div>
            </div>
        </transition>
    </div>
  </div>
</template>

<style scoped>
.tteokbokki-app {
    min-height: 100vh;
    background-color: var(--rice-cream);
}

/* Admin Container */
.admin-container {
    max-width: 800px;
    margin: 3rem auto;
    padding: 0 1.5rem;
}

.page-header {
    margin-bottom: 2rem;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
}

.header-content h2 {
    font-size: 2rem;
    font-weight: 900;
    color: var(--deep-brown);
    margin: 0 0 0.5rem;
}

.header-content p {
    color: var(--text-muted);
    font-weight: 600;
    margin: 0;
}

.header-icon {
    font-size: 3rem;
    opacity: 0.5;
    transform: rotate(10deg);
}

/* Search Box */
.search-box {
    padding: 2rem;
    background: white;
    margin-bottom: 2rem;
}

.search-form {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
}

.search-wrapper {
    flex: 1;
    min-width: 200px;
    position: relative;
    display: flex;
    align-items: center;
    background: #fdfafa;
    border: 2px solid var(--border-color);
    border-radius: 12px;
    padding: 0 1rem;
    height: 54px;
    transition: all 0.2s;
}

.search-wrapper:focus-within {
    border-color: var(--sauce-orange);
    background: white;
    box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

.search-wrapper .icon {
    font-size: 1.2rem;
    margin-right: 0.5rem;
    opacity: 0.6;
}

.search-input {
    border: none;
    background: transparent;
    width: 100%;
    height: 100%;
    font-size: 1rem;
    font-weight: 600;
    color: var(--deep-brown);
}

.search-input:focus {
    outline: none;
}

.search-btn {
    padding: 0 2rem;
    height: 54px;
    font-size: 1.05rem;
}

.error-msg {
    margin-top: 1rem;
    background: #fff1f2;
    color: #e11d48;
    padding: 0.75rem;
    border-radius: 8px;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

/* Result Card */
.result-card {
    background: white;
    padding: 2.5rem;
    animation: pop 0.4s ease-out;
}

.result-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 2px dashed var(--border-color);
    padding-bottom: 1rem;
    margin-bottom: 2rem;
}

.result-header h3 {
    margin: 0;
    font-size: 1.4rem;
    color: var(--deep-brown);
    font-weight: 800;
}

.badge {
    background: var(--rice-cream);
    color: var(--sauce-orange);
    padding: 0.25rem 0.75rem;
    border-radius: 99px;
    font-weight: 700;
    font-size: 0.85rem;
    border: 1px solid var(--border-color);
}

.user-profile {
    display: flex;
    gap: 2rem;
    align-items: start;
}

.profile-icon {
    font-size: 4rem;
    background: #fff7ed;
    width: 100px;
    height: 100px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    border: 4px solid var(--rice-cream);
}

.info-list {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin: 0;
}

.info-row {
    display: flex;
    align-items: center;
    padding-bottom: 0.75rem;
    border-bottom: 1px solid #f9fafb;
}

.info-row dt {
    width: 100px;
    color: var(--text-muted);
    font-weight: 700;
}

.info-row dd {
    margin: 0;
    color: var(--deep-brown);
    font-weight: 600;
    font-size: 1.1rem;
}

.role-text {
    color: var(--spicy-red) !important;
}

@keyframes pop {
    from { transform: translateY(10px); opacity: 0; }
    to { transform: translateY(0); opacity: 1; }
}

@media (max-width: 640px) {
    .user-profile {
        flex-direction: column;
        align-items: center;
    }
}
</style>
