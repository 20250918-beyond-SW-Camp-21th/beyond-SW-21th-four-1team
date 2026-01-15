<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { getMe, updateMe, updatePassword, withdraw } from '@/api/user.js';

const router = useRouter();
const user = ref(null);
const loading = ref(true);

const activeTab = ref('info'); // info, edit, security
const editForm = ref({ name: '', email: '' });
const pwForm = ref({ oldPassword: '', newPassword: '' });

// Error States
const profileError = ref('');
const passwordError = ref('');

// Role Localization Map
const roleMap = {
    'HQ': '본사 관리자',
    'FRANCHISE': '가맹점 관리자',
    'GENERAL': '일반 회원'
};

const displayRole = computed(() => {
    if (!user.value || !user.value.userRole) return '';
    return roleMap[user.value.userRole] || user.value.userRole;
});

const fetchUser = async () => {
    loading.value = true;
    try {
        const response = await getMe();
        if (response.success) {
            user.value = response.data;
            editForm.value.name = user.value.username;
            editForm.value.email = user.value.email;
        }
    } catch (e) {
        router.push('/login');
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchUser();
});

const handleUpdateProfile = async () => {
    profileError.value = '';
    try {
        const response = await updateMe(editForm.value);
        if (response.success) {
            alert('정보가 맛깔나게 수정되었습니다! 🍳');
            fetchUser();
        } else {
             profileError.value = response.error || '수정 실패';
        }
    } catch (e) {
        if (e.response && e.response.data) {
             profileError.value = e.response.data.error || e.response.data.message || '오류 발생';
        } else {
             profileError.value = '오류 발생';
        }
    }
};

const handleChangePassword = async () => {
    passwordError.value = '';
    try {
        const response = await updatePassword(pwForm.value.oldPassword, pwForm.value.newPassword);
        if (response.success) {
            alert('비밀번호가 안전하게 변경되었습니다! 🔒');
            pwForm.value = { oldPassword: '', newPassword: '' };
        } else {
            passwordError.value = response.error || '변경 실패';
        }
    } catch (e) {
        if (e.response && e.response.data) {
             passwordError.value = e.response.data.error || e.response.data.message || '오류 발생';
        } else {
             passwordError.value = '오류 발생';
        }
    }
};

const handleWithdraw = async () => {
    if (confirm('정말 탈퇴하시겠습니까? 😢 모든 레시피(데이터)가 사라집니다.')) {
        try {
            await withdraw();
            localStorage.clear();
            router.push('/login');
        } catch (e) {
            alert('오류 발생');
        }
    }
};
</script>

<template>
  <div class="tteokbokki-app">
    <header class="tteok-header">
      <div class="header-card premium-card">
        <h2>마이 키친</h2>
        <p>나의 정보를 관리하고 주방 설정을 변경합니다.</p>
        <div class="header-decor">👨‍🍳</div>
      </div>
    </header>

    <main class="main-content" v-if="!loading && user">
      <div class="dashboard-grid">
        <!-- Sidebar -->
        <aside class="profile-sidebar premium-card">
            <div class="user-brief">
                <div class="avatar-circle">
                    {{ user.username.charAt(0) }}
                </div>
                <h3>{{ user.username }}</h3>
                <span class="role-badge">{{ displayRole }}</span>
            </div>
          
            <nav class="side-nav">
                <button :class="{ active: activeTab === 'info' }" @click="activeTab = 'info'">
                    <span class="icon">📋</span> 내 정보
                </button>
                <button :class="{ active: activeTab === 'edit' }" @click="activeTab = 'edit'">
                    <span class="icon">✏️</span> 정보 수정
                </button>
                <button :class="{ active: activeTab === 'security' }" @click="activeTab = 'security'">
                    <span class="icon">🔒</span> 비밀번호 & 보안
                </button>
            </nav>
        </aside>

        <!-- Main Content Area -->
        <section class="content-area">
             <transition name="pop" mode="out-in">
                <!-- Info Tab -->
                <div v-if="activeTab === 'info'" class="tab-card premium-card">
                    <header class="tab-header">
                        <h2>내 정보 카드</h2>
                        <span class="decor-icon">🎫</span>
                    </header>
                    
                    <div class="info-list">
                        <div class="info-item">
                            <label>아이디</label>
                            <div class="value">{{ user.loginId }}</div>
                        </div>
                        <div class="info-item">
                            <label>이름</label>
                            <div class="value">{{ user.username }}</div>
                        </div>
                        <div class="info-item">
                            <label>이메일</label>
                            <div class="value">{{ user.email }}</div>
                        </div>
                        <div class="info-item">
                            <label>직급</label>
                            <div class="value highlight">{{ displayRole }}</div>
                        </div>
                    </div>
                </div>

                <!-- Edit Tab -->
                <div v-else-if="activeTab === 'edit'" class="tab-card premium-card">
                    <header class="tab-header">
                        <h2>개인정보 수정</h2>
                        <span class="decor-icon">📝</span>
                    </header>

                    <form @submit.prevent="handleUpdateProfile" class="spicy-form">
                        <div class="form-group">
                            <label>이름</label>
                            <input v-model="editForm.name" class="spicy-input" />
                        </div>
                        <div class="form-group">
                            <label>이메일</label>
                            <input v-model="editForm.email" class="spicy-input" />
                        </div>

                        <div v-if="profileError" class="error-msg">
                            <span>🔥</span> {{ profileError }}
                        </div>

                        <button class="btn-spicy action-btn">수정 완료</button>
                    </form>
                </div>

                <!-- Security Tab -->
                <div v-else-if="activeTab === 'security'" class="tab-card premium-card">
                    <header class="tab-header">
                        <h2>보안 설정</h2>
                        <span class="decor-icon">🔐</span>
                    </header>

                    <div class="security-section">
                        <h3>비밀번호 변경</h3>
                        <form @submit.prevent="handleChangePassword" class="spicy-form">
                            <div class="form-group">
                                <label>현재 비밀번호</label>
                                <input v-model="pwForm.oldPassword" type="password" class="spicy-input" />
                            </div>
                            <div class="form-group">
                                <label>새 비밀번호</label>
                                <input v-model="pwForm.newPassword" type="password" class="spicy-input" />
                            </div>

                            <div v-if="passwordError" class="error-msg">
                                <span>🔥</span> {{ passwordError }}
                            </div>

                            <button class="btn-spicy action-btn">비밀번호 변경</button>
                        </form>
                    </div>

                    <div class="danger-zone">
                        <div class="danger-info">
                            <h3>🚨 회원 탈퇴</h3>
                            <p>주방을 완전히 떠나시겠습니까?</p>
                        </div>
                        <button class="btn-danger" @click="handleWithdraw">탈퇴하기</button>
                    </div>
                </div>
             </transition>
        </section>
      </div>
    </main>
  </div>
</template>

<style scoped>
.tteokbokki-app {
  min-height: 100vh;
  background-color: var(--rice-cream);
}

/* Header */
.tteok-header {
  max-width: 1200px;
  margin: 2rem auto 0;
  padding: 0 2rem;
}

.header-card {
  padding: 2.5rem;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #ffffff, #fff7ed);
}

.header-card h2 {
    font-size: 2.25rem;
    font-weight: 900;
    color: var(--deep-brown);
    margin: 0 0 0.5rem;
}

.header-card p {
    color: var(--text-muted);
    font-size: 1.1rem;
    font-weight: 600;
    margin: 0;
}

.header-decor {
    position: absolute;
    right: 2rem;
    bottom: -10px;
    font-size: 6rem;
    opacity: 0.15;
    transform: rotate(10deg);
}

/* Main Layout */
.main-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
}

.dashboard-grid {
    display: grid;
    grid-template-columns: 300px 1fr;
    gap: 2rem;
}

/* Sidebar */
.profile-sidebar {
    background: white;
    padding: 2rem;
    height: fit-content;
    text-align: center;
}

.avatar-circle {
    width: 100px;
    height: 100px;
    background: linear-gradient(135deg, var(--sauce-orange), var(--spicy-red));
    color: white;
    font-size: 2.5rem;
    font-weight: 800;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 1rem;
    box-shadow: 0 4px 10px rgba(225, 29, 72, 0.3);
    border: 4px solid var(--rice-cream);
}

.user-brief h3 {
    margin: 0 0 0.5rem;
    font-size: 1.4rem;
    font-weight: 800;
    color: var(--deep-brown);
}

.role-badge {
    display: inline-block;
    padding: 0.3rem 0.8rem;
    background: #fffbeb;
    color: var(--text-muted);
    font-size: 0.9rem;
    border-radius: 99px;
    font-weight: 700;
    border: 1px solid var(--border-color);
}

.side-nav {
    margin-top: 2.5rem;
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
}

.side-nav button {
    background: transparent;
    border: none;
    padding: 1rem;
    text-align: left;
    cursor: pointer;
    border-radius: 12px;
    font-size: 1rem;
    font-weight: 700;
    color: var(--text-muted);
    transition: all 0.2s;
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.side-nav button:hover {
    background: var(--rice-cream);
    color: var(--deep-brown);
}

.side-nav button.active {
    background: #fff7ed;
    color: var(--sauce-orange);
    border: 2px solid var(--border-color);
}

/* Content Area */
.tab-card {
    background: white;
    padding: 2.5rem;
    min-height: 500px;
    animation: pop 0.4s ease-out;
}

.tab-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 2px dashed var(--border-color);
    padding-bottom: 1.5rem;
    margin-bottom: 2rem;
}

.tab-header h2 {
    font-size: 1.5rem;
    font-weight: 800;
    color: var(--deep-brown);
    margin: 0;
}

.decor-icon {
    font-size: 2rem;
}

/* Info List */
.info-list {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.info-item {
    display: flex;
    align-items: center;
    padding: 1rem;
    background: var(--rice-cream);
    border-radius: 12px;
}

.info-item label {
    width: 100px;
    font-weight: 700;
    color: var(--text-muted);
}

.info-item .value {
    font-weight: 600;
    color: var(--deep-brown);
    font-size: 1.1rem;
}

.info-item .highlight {
    color: var(--spicy-red);
}

/* Forms */
.spicy-form {
    max-width: 480px;
}

.form-group {
    margin-bottom: 1.5rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 700;
    color: var(--deep-brown);
}

.spicy-input {
    width: 100%;
    padding: 0.8rem 1rem;
    border: 2px solid var(--border-color);
    border-radius: 12px;
    font-size: 1rem;
    font-weight: 600;
    color: var(--deep-brown);
    background: white;
    transition: all 0.2s;
}

.spicy-input:focus {
    outline: none;
    border-color: var(--sauce-orange);
    box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

.action-btn {
    width: 100%;
    padding: 1rem;
    font-size: 1.1rem;
    margin-top: 1rem;
}

.error-msg {
    background-color: #fff1f2;
    border: 1px solid #fecaca;
    color: #e11d48;
    padding: 0.75rem;
    border-radius: 8px;
    margin-bottom: 1rem;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

/* Danger Zone */
.danger-zone {
    margin-top: 4rem;
    padding: 1.5rem;
    border: 2px solid #fecaca;
    background: #fff1f2;
    border-radius: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.danger-info h3 {
    color: #be123c;
    margin: 0 0 0.25rem;
    font-weight: 800;
}

.danger-info p {
    color: #e11d48;
    margin: 0;
    font-weight: 600;
}

.btn-danger {
    background: white;
    color: #e11d48;
    border: 2px solid #e11d48;
    padding: 0.6rem 1.2rem;
    border-radius: 12px;
    font-weight: 800;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-danger:hover {
    background: #e11d48;
    color: white;
}

@keyframes pop {
  0% { transform: scale(0.95); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

@media (max-width: 768px) {
    .dashboard-grid {
        grid-template-columns: 1fr;
    }
}
</style>
