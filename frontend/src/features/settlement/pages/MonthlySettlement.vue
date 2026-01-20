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
  productId: 1,
  yearMonth: new Date().toISOString().slice(0, 7),
  status: 'ALL'
});

const loadMonthlySettlement = async (filters) => {
  loading.value = true;
  error.value = null;
  currentFilters.value = filters;
  
  try {
    const data = await settlementApi.getMonthlySettlement(filters.storeId, filters.productId, filters.yearMonth);
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
    await settlementApi.downloadMonthlyPdf(currentFilters.value.storeId, currentFilters.value.productId, currentFilters.value.yearMonth);
    alert('🌶️ PDF가 성공적으로 다운로드되었습니다!');
  } catch (err) {
    alert(`PDF 다운로드 중 오류가 발생했습니다: ${err.message}`);
  } finally {
    loading.value = false;
  }
};

const handleCreateMonthlySettlement = async () => {
  if (!confirm(`${currentFilters.value.yearMonth}월의 정산을 생성하시겠습니까?\n해당 월의 모든 배송 완료된 주문에 대해 일별 정산이 생성됩니다.`)) {
    return;
  }

  try {
    loading.value = true;
    
    // 해당 월의 모든 날짜에 대해 정산 생성 시도
    const [year, month] = currentFilters.value.yearMonth.split('-');
    const daysInMonth = new Date(year, month, 0).getDate();
    
    let successCount = 0;
    let errorCount = 0;
    
    for (let day = 1; day <= daysInMonth; day++) {
      const date = `${currentFilters.value.yearMonth}-${String(day).padStart(2, '0')}`;
      try {
        await settlementApi.createSettlement(currentFilters.value.storeId, currentFilters.value.productId, date);
        successCount++;
      } catch (err) {
        // 이미 정산이 존재하거나 주문이 없는 경우는 무시
        errorCount++;
      }
    }
    
    if (successCount > 0) {
      alert(`🌶️ ${successCount}일의 정산이 성공적으로 생성되었습니다!`);
      // Reload the data
      await loadMonthlySettlement(currentFilters.value);
    } else {
      alert('⚠️ 생성할 수 있는 정산이 없습니다.\n해당 월에 배송 완료된 주문이 없거나 이미 정산이 생성되었습니다.');
    }
  } catch (err) {
    alert(`정산 생성 중 오류가 발생했습니다: ${err.message}`);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="settlement-app">
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
        @create-settlement="handleCreateMonthlySettlement"
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