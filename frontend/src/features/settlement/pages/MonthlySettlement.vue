<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { settlementApi } from '../api/settlementApi';
import SettlementFilter from '../components/SettlementFilter.vue';
import MonthlyTable from '../components/MonthlyTable.vue';

const router = useRouter();

// State
const settlementData = ref(null);
const loading = ref(false);
const error = ref(null);
const isCreating = ref(false);
const creationProgress = ref({ current: 0, total: 0, successCount: 0 });
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
    console.error('Error loading monthly settlement:', err);
    if (err.response?.status === 404) {
      error.value = '해당 월의 정산 데이터가 없습니다. 정산을 먼저 생성해주세요.';
    } else {
      error.value = `정산 데이터를 불러올 수 없습니다: ${err.response?.data?.message || err.message}`;
    }
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
    console.error('Error downloading PDF:', err);
    alert(`PDF 다운로드 중 오류가 발생했습니다: ${err.response?.data?.message || err.message}`);
  } finally {
    loading.value = false;
  }
};

const handleCreateMonthlySettlement = async () => {
  const [year, month] = currentFilters.value.yearMonth.split('-');
  const monthStr = `${year}년 ${month}월`;
  
  if (!confirm(`${monthStr}의 정산을 생성하시겠습니까?\n\n해당 월의 모든 배송 완료된 주문에 대해 일별 정산이 생성됩니다.`)) {
    return;
  }

  try {
    isCreating.value = true;
    loading.value = true;
    error.value = null;
    
    // 해당 월의 모든 날짜에 대해 정산 생성 시도
    const daysInMonth = new Date(year, month, 0).getDate();
    creationProgress.value = { current: 0, total: daysInMonth, successCount: 0 };
    
    let successCount = 0;
    let errorCount = 0;
    const errors = [];
    
    for (let day = 1; day <= daysInMonth; day++) {
      const date = `${currentFilters.value.yearMonth}-${String(day).padStart(2, '0')}`;
      creationProgress.value.current = day;
      
      try {
        await settlementApi.createSettlement(currentFilters.value.storeId, currentFilters.value.productId, date);
        successCount++;
        creationProgress.value.successCount = successCount;
      } catch (err) {
        // 이미 정산이 존재하거나 주문이 없는 경우는 무시
        errorCount++;
        if (!err.response?.data?.message?.includes('이미 존재') && !err.response?.data?.message?.includes('주문이 없')) {
          errors.push(`${day}일: ${err.response?.data?.message || err.message}`);
        }
      }
    }
    
    // Reload the data
    await loadMonthlySettlement(currentFilters.value);
    
    if (successCount > 0) {
      alert(`🌶️ ${monthStr} 정산 생성 완료!\n\n생성된 일수: ${successCount}일\n스킵된 일수: ${errorCount}일`);
    } else if (errors.length > 0) {
      alert(`❌ 정산 생성 중 오류가 발생했습니다:\n\n${errors.join('\n')}`);
    } else {
      alert(`⚠️ 생성할 수 있는 정산이 없습니다.\n\n해당 월에 배송 완료된 주문이 없거나 이미 정산이 생성되었습니다.`);
    }
  } catch (err) {
    console.error('Error creating monthly settlement:', err);
    alert(`정산 생성 중 오류가 발생했습니다: ${err.response?.data?.message || err.message}`);
  } finally {
    isCreating.value = false;
    loading.value = false;
    creationProgress.value = { current: 0, total: 0, successCount: 0 };
  }
};

// 초기 데이터 로드
onMounted(() => {
  loadMonthlySettlement(currentFilters.value);
});
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

      <!-- 정산 생성 진행 상황 표시 -->
      <div v-if="isCreating" class="progress-alert premium-card">
        <div class="progress-content">
          <div class="progress-icon">⏳</div>
          <div class="progress-info">
            <h4>정산 생성 중...</h4>
            <p>{{ creationProgress.current }} / {{ creationProgress.total }}일 처리 중 (성공: {{ creationProgress.successCount }}일)</p>
            <div class="progress-bar-container">
              <div 
                class="progress-bar-fill" 
                :style="{ width: (creationProgress.current / creationProgress.total * 100) + '%' }"
              ></div>
            </div>
          </div>
        </div>
      </div>

      <SettlementFilter 
        mode="monthly" 
        @filter-change="handleFilterChange"
      />

      <MonthlyTable 
        :data="filteredSettlementData" 
        :loading="loading"
        :is-creating="isCreating"
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

.progress-alert {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: linear-gradient(135deg, #fef3c7, #fff7ed);
  border: 2px solid var(--sauce-orange);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.9; }
}

.progress-content {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.progress-icon {
  font-size: 3rem;
  animation: rotate 2s infinite linear;
}

.progress-info {
  flex: 1;
}

.progress-info h4 {
  font-size: 1.25rem;
  font-weight: 900;
  color: var(--deep-brown);
  margin: 0 0 0.5rem 0;
}

.progress-info p {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-muted);
  margin: 0 0 1rem 0;
}

.progress-bar-container {
  height: 12px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(to right, var(--sauce-orange), var(--spicy-red));
  transition: width 0.3s ease;
  border-radius: 10px;
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