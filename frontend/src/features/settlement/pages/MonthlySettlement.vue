<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { settlementApi } from '../api/settlementApi';
import SettlementFilter from '../components/SettlementFilter.vue';
import MonthlyTable from '../components/MonthlyTable.vue';

const router = useRouter();

// State
const settlementData = ref(null);
const loading = ref(false);
const error = ref(null);
const currentFilters = ref({ 
  storeId: 1, 
  yearMonth: new Date().toISOString().slice(0, 7),
  status: 'ALL'
});

const loadMonthlySettlement = async (filters) => {
  loading.value = true;
  error.value = null;
  currentFilters.value = filters;
  
  try {
    const data = await settlementApi.getMonthlySettlement(filters.storeId, filters.yearMonth);
    settlementData.value = data;
  } catch (err) {
    error.value = `정산 데이터를 불러올 수 없습니다: ${err.message}`;
    settlementData.value = null;
  } finally {
    loading.value = false;
  }
};

// Filter data by status on frontend
const filteredSettlementData = computed(() => {
  if (!settlementData.value) return null;
  if (currentFilters.value.status === 'ALL') return settlementData.value;
  
  // If status doesn't match, return null (show empty state)
  if (settlementData.value.status !== currentFilters.value.status) {
    return null;
  }
  
  return settlementData.value;
});

const handleFilterChange = (filters) => {
  loadMonthlySettlement(filters);
};

const handleDownloadPdf = async () => {
  try {
    loading.value = true;
    await settlementApi.downloadMonthlyPdf(currentFilters.value.storeId, currentFilters.value.yearMonth);
    alert('🌶️ PDF가 성공적으로 다운로드되었습니다!');
  } catch (err) {
    alert(`PDF 다운로드 중 오류가 발생했습니다: ${err.message}`);
  } finally {
    loading.value = false;
  }
};

const handleLogout = () => {
  if (confirm('정말 주방에서 퇴근하시겠습니까? 👩‍🍳')) {
    alert('로그아웃되었습니다. 다음에 또 만나요!');
    // router.push('/login') // Later: redirect to login
  }
};
</script>

<template>
  <div class="settlement-app">
    <nav class="tteok-nav">
      <div class="nav-container">
        <div class="brand">
          <span class="brand-icon">🌶️</span>
          <h1 class="brand-name">SPICY</h1>
        </div>
        <div class="nav-links">
          <span class="user-info">어서오세요, 셰프님! 👩‍🍳</span>
          <button class="nav-btn" @click="router.push('/inventory')">재고 관리</button>
          <button class="nav-btn" @click="router.push('/orders')">주문 현황</button>
          <button class="nav-btn" @click="router.push('/settlements/daily')">일별 정산</button>
          <button class="nav-btn active" @click="router.push('/settlements/monthly')">월별 정산</button>
          <button class="logout-btn" @click="handleLogout">퇴근하기</button>
        </div>
      </div>
    </nav>

    <header class="tteok-header">
      <div class="header-card premium-card">
        <h2>월별 정산 조회</h2>
        <p>특정 월의 주문 금액 합계, 수수료, 최종 정산 금액, 상태, 지급 예정일을 확인하세요.</p>
        <div class="header-decor">📈</div>
      </div>
    </header>

    <main class="main-content">
      <div v-if="error" class="error-alert">
        <span class="icon">🌶️</span> {{ error }}
      </div>

      <SettlementFilter 
        mode="monthly" 
        @filter-change="handleFilterChange"
      />

      <MonthlyTable 
        :data="filteredSettlementData" 
        :loading="loading"
        @download-pdf="handleDownloadPdf"
      />
    </main>
  </div>
</template>

<style scoped>
.settlement-app {
  min-height: 100vh;
  background-color: var(--rice-cream);
}

.tteok-nav {
  background-color: #ffffff;
  border-bottom: 4px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
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
  gap: 0;
  flex-wrap: wrap;
}

.user-info {
  font-size: 0.95rem;
  color: var(--text-muted);
  font-weight: 700;
  background: var(--rice-cream);
  padding: 0.4rem 1.2rem;
  border-radius: 99px 0 0 99px;
  border: 1px solid var(--border-color);
  border-right: none;
}

.nav-btn {
  font-size: 0.9rem;
  font-weight: 800;
  color: var(--deep-brown);
  background: var(--border-color);
  padding: 0.4rem 1.2rem;
  border: 1px solid var(--border-color);
  border-left: 1px solid rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.2s;
}

.nav-btn:hover {
  background: #fde68a;
}

.nav-btn.active {
  background: var(--spicy-red);
  color: white;
  border-color: var(--spicy-red);
}

.logout-btn {
  font-size: 0.9rem;
  font-weight: 800;
  color: #ffffff;
  background: var(--text-muted);
  padding: 0.4rem 1.2rem;
  border-top: 1px solid var(--text-muted);
  border-bottom: 1px solid var(--text-muted);
  border-left: none;
  border-right: 1px solid var(--text-muted);
  border-radius: 0 99px 99px 0;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: var(--deep-brown);
}

.tteok-header {
  max-width: 1200px;
  margin: 2rem auto 0;
  padding: 0 2rem;
}

.header-card {
  padding: 2.5rem;
  text-align: left;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #ffffff, #fdf2f8);
}

.header-card h2 {
  font-size: 2.25rem;
  font-weight: 900;
  color: var(--deep-brown);
  margin-bottom: 0.5rem;
}

.header-card p {
  color: var(--text-muted);
  font-size: 1.1rem;
  font-weight: 600;
}

.header-decor {
  position: absolute;
  right: 2rem;
  bottom: 0.5rem;
  font-size: 5rem;
  opacity: 0.15;
  transform: rotate(15deg);
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.error-alert {
  background-color: #fff1f2;
  border: 2px solid #fecaca;
  color: #e11d48;
  padding: 1.25rem;
  border-radius: 20px;
  margin-bottom: 2rem;
  text-align: center;
  font-weight: 700;
}

@media (max-width: 768px) {
  .nav-container {
    flex-direction: column;
    align-items: stretch;
  }

  .nav-links {
    width: 100%;
    justify-content: stretch;
  }

  .user-info,
  .nav-btn,
  .logout-btn {
    flex: 1;
    text-align: center;
    border-radius: 0;
    border: 1px solid var(--border-color);
  }
}
</style>