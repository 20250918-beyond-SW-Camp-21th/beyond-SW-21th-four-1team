<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { settlementApi } from '../api/settlementApi';
import SettlementFilter from '../components/SettlementFilter.vue';
import DailySummary from '../components/DailySummary.vue';

const router = useRouter();

// State
const settlementData = ref(null);
const loading = ref(false);
const error = ref(null);
const currentFilters = ref({ storeId: 1, productId: 1, date: new Date().toISOString().split('T')[0] });

const loadDailySettlement = async (filters) => {
  loading.value = true;
  error.value = null;
  currentFilters.value = filters;
  
  try {
    const data = await settlementApi.getDailySettlement(filters.storeId, filters.productId, filters.date);
    settlementData.value = data;
  } catch (err) {
    error.value = `정산 데이터를 불러올 수 없습니다: ${err.message}`;
    settlementData.value = null;
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = (filters) => {
  loadDailySettlement(filters);
};

const handleCreateSettlement = async () => {
  if (!confirm('현재 선택한 날짜의 정산을 생성하시겠습니까?')) {
    return;
  }

  try {
    loading.value = true;
    await settlementApi.createSettlement(currentFilters.value.storeId, currentFilters.value.productId, currentFilters.value.date);
    alert('🌶️ 정산이 성공적으로 생성되었습니다!');
    // Reload the data
    await loadDailySettlement(currentFilters.value);
  } catch (err) {
    alert(`정산 생성 중 오류가 발생했습니다: ${err.message}`);
  } finally {
    loading.value = false;
  }
};

const handleDownloadPdf = async () => {
  if (!settlementData.value) {
    alert('다운로드할 정산 데이터가 없습니다.');
    return;
  }

  try {
    loading.value = true;
    await settlementApi.downloadDailyPdf(
      currentFilters.value.storeId, 
      currentFilters.value.productId,
      currentFilters.value.date
    );
    alert('🌶️ PDF가 성공적으로 다운로드되었습니다!');
  } catch (err) {
    alert(`PDF 다운로드 중 오류가 발생했습니다: ${err.message}`);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="settlement-app">
    <header class="tteok-header">
      <div class="header-card premium-card">
        <h2>일별 정산 조회</h2>
        <p>특정 날짜의 주문 건수, 일 주문 금액, 월 누적 금액을 확인하세요.</p>
        <div class="header-decor">📊</div>
      </div>
    </header>

    <main class="main-content">
      <div v-if="error" class="error-alert">
        <span class="icon">🌶️</span> {{ error }}
      </div>

      <SettlementFilter 
        mode="daily" 
        @filter-change="handleFilterChange"
      />

      <div class="action-bar">
        <button class="btn-spicy create-btn" @click="handleCreateSettlement">
          <span>➕ 정산 생성하기</span>
        </button>
      </div>

      <DailySummary 
        :data="settlementData" 
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
  background: linear-gradient(135deg, #ffffff, #fff7ed);
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

.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 2rem;
}

.create-btn {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
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