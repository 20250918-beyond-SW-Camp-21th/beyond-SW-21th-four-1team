<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router';
import { ref, watch, onMounted } from 'vue';
import { getMe } from '@/api/user';
import { logout } from '@/api/auth';

const router = useRouter();
const isLoggedIn = ref(false);
const userRole = ref('');
const userName = ref('');

const checkLoginStatus = async () => {
  const token = localStorage.getItem('accessToken');
  isLoggedIn.value = !!token;

  if (isLoggedIn.value) {
    if (!userRole.value || !userName.value) {
      try {
        const res = await getMe();
        if (res.success) {
          userRole.value = res.data.userRole; 
          userName.value = res.data.username;
        } else {
          userRole.value = '';
          userName.value = '';
          console.warn('getMe failed:', res.error);
        }
      } catch (e) {
        console.error('Failed to fetch user info', e);
        userRole.value = '';
        userName.value = '';
      }
    }
  } else {
    userRole.value = '';
    userName.value = '';
    if (router.currentRoute.value.meta.requiresAuth) {
      router.push('/login');
    }
  }
};

const handleLogout = async () => {
  if (confirm('정말 주방에서 퇴근하시겠습니까? 👩‍🍳')) {
    try {
        const token = localStorage.getItem('refreshToken');
        if (token) await logout(token);
    } finally {
        localStorage.clear();
        isLoggedIn.value = false;
        userRole.value = '';
        userName.value = '';
        router.push('/login');
    }
  }
};

const goToProfile = () => {
    router.push('/profile');
};

onMounted(() => {
  checkLoginStatus();
});

router.afterEach(() => {
  checkLoginStatus();
});
</script>

<template>
  <div class="tteokbokki-app-root">
    <nav class="tteok-nav" v-if="isLoggedIn">
      <div class="nav-container">
        <div class="brand" @click="router.push('/')">
          <span class="brand-icon">🌶️</span>
          <h1 class="brand-name">SPICY</h1>
        </div>
        
        <div class="nav-links">
          <RouterLink to="/inventory" class="nav-item">재고 관리</RouterLink>
          <RouterLink to="/products" class="nav-item">상품 목록</RouterLink>
          <RouterLink to="/orders" class="nav-item">주문 현황</RouterLink>
          <RouterLink to="/cart" class="nav-item">장바구니</RouterLink>
          
          <div class="nav-dropdown">
            <RouterLink to="/settlements" class="nav-item">정산 관리</RouterLink>
            <div class="dropdown-content">
              <RouterLink to="/settlements/daily" class="dropdown-item">일별 정산</RouterLink>
              <RouterLink to="/settlements/monthly" class="dropdown-item">월별 정산</RouterLink>
            </div>
          </div>
          
          <RouterLink v-if="userRole === 'HQ'" to="/admin/search" class="nav-item highlight">회원 조회</RouterLink>
          <div class="user-actions">
            <span class="user-info" @click="goToProfile">
               {{ userName || '셰프' }}님 👩‍🍳
            </span>
            <button class="logout-btn" @click="handleLogout">퇴근하기</button>
          </div>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>
/* Global Tteokbokki Root Styles */
.tteokbokki-app-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.tteok-nav {
  background-color: #ffffff;
  border-bottom: 4px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.75rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
}

.brand-icon {
  font-size: 1.75rem;
  filter: drop-shadow(0 2px 4px rgba(225, 29, 72, 0.2));
}

.brand-name {
  font-size: 1.5rem;
  font-weight: 900;
  color: var(--deep-brown);
  margin: 0;
  letter-spacing: -0.05em;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nav-item {
  text-decoration: none;
  font-weight: 800;
  font-size: 0.95rem;
  color: var(--text-muted);
  transition: all 0.2s;
  padding: 0.4rem 0.8rem;
  border-radius: 8px;
}

.nav-item:hover, .nav-item.router-link-active {
  color: var(--spicy-red);
  background: var(--rice-cream);
}

.nav-item.highlight {
    color: var(--sauce-orange);
}

/* Dropdown Menu Styles */
.nav-dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-content {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  background-color: white;
  min-width: 160px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  border: 2px solid var(--border-color);
  z-index: 1000;
  margin-top: 0;
  overflow: hidden;
  /* Add padding at top to bridge the gap */
  padding-top: 0.5rem;
}

/* Create invisible bridge between button and menu */
.dropdown-content::before {
  content: '';
  position: absolute;
  top: -0.5rem;
  left: 0;
  right: 0;
  height: 0.5rem;
  background: transparent;
}

.nav-dropdown:hover .dropdown-content {
  display: block;
  animation: fadeIn 0.2s ease-in;
  /* Add transition delay to prevent immediate closing */
  transition: opacity 0.3s ease;
}

/* Prevent menu from closing when moving mouse to it */
.nav-dropdown .dropdown-content:hover {
  display: block;
}

.dropdown-item {
  display: block;
  text-decoration: none;
  font-weight: 700;
  font-size: 0.9rem;
  color: var(--deep-brown);
  padding: 1rem 1.5rem;
  transition: all 0.2s;
  white-space: nowrap;
  width: 100%;
  box-sizing: border-box;
}

.dropdown-item:hover {
  background: var(--rice-cream);
  color: var(--spicy-red);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-actions {
  display: flex;
  align-items: center;
}

.user-info {
  font-size: 0.9rem;
  color: var(--text-muted);
  font-weight: 700;
  background: var(--rice-cream);
  padding: 0.4rem 1.2rem;
  border-radius: 99px 0 0 99px;
  border: 1px solid var(--border-color);
  border-right: none;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.user-info:hover {
  background: #fff7ed;
  color: var(--deep-brown);
}

.logout-btn {
  font-size: 0.9rem;
  font-weight: 800;
  color: #ffffff;
  background: var(--text-muted);
  padding: 0.4rem 1.2rem;
  border-radius: 0 99px 99px 0;
  cursor: pointer;
  border: 1px solid var(--text-muted);
  border-left: none;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: var(--deep-brown);
  border-color: var(--deep-brown);
}

.main-content {
  flex: 1;
}
</style>
