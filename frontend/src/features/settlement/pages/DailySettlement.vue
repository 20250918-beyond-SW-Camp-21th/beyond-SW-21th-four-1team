<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { settlementApi } from '../api/settlementApi';
import SettlementFilter from '../components/SettlementFilter.vue';
import DailySummary from '../components/DailySummary.vue';
import SettlementChart from '../components/SettlementChart.vue';
import BarChart from '../components/BarChart.vue';

const router = useRouter();

// State
const settlementData = ref(null);
const loading = ref(false);
const error = ref(null);
const currentFilters = ref({ storeId: 1, productId: 1, date: new Date().toISOString().split('T')[0] });
const isCreating = ref(false);
const statsData = ref([]);
const statsLoading = ref(false);

const loadDailySettlement = async (filters) => {
  loading.value = true;
  error.value = null;
  currentFilters.value = filters;
  
  try {
    const data = await settlementApi.getDailySettlement(filters.storeId, filters.productId, filters.date);
    settlementData.value = data;
  } catch (err) {
    console.error('Error loading daily settlement:', err);
    if (err.response?.status === 404) {
      error.value = '해당 날짜의 정산 데이터가 없습니다. 정산을 먼저 생성해주세요.';
    } else {
      error.value = `정산 데이터를 불러올 수 없습니다: ${err.response?.data?.message || err.message}`;
    }
    settlementData.value = null;
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = (filters) => {
  loadDailySettlement(filters);
};

const handleCreateSettlement = async () => {
  const dateStr = new Date(currentFilters.value.date).toLocaleDateString('ko-KR');
  if (!confirm(`${dateStr}의 정산을 생성하시겠습니까?\n\n배송 완료된 주문을 기반으로 정산이 생성됩니다.`)) {
    return;
  }

  try {
    isCreating.value = true;
    loading.value = true;
    error.value = null;
    
    await settlementApi.createSettlement(currentFilters.value.storeId, currentFilters.value.productId, currentFilters.value.date);
    
    // Reload the data
    await loadDailySettlement(currentFilters.value);
    
    alert(`🌶️ 정산이 성공적으로 생성되었습니다!\n\n날짜: ${dateStr}\n가맹점 ID: ${currentFilters.value.storeId}`);
  } catch (err) {
    console.error('Error creating settlement:', err);
    const errorMsg = err.response?.data?.message || err.message;
    
    if (errorMsg.includes('이미 존재')) {
      alert(`⚠️ 해당 날짜의 정산이 이미 존재합니다.\n\n날짜: ${dateStr}`);
    } else if (errorMsg.includes('주문이 없')) {
      alert(`⚠️ 해당 날짜에 배송 완료된 주문이 없습니다.\n\n날짜: ${dateStr}`);
    } else {
      alert(`❌ 정산 생성 중 오류가 발생했습니다:\n${errorMsg}`);
    }
  } finally {
    isCreating.value = false;
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
    console.error('Error downloading PDF:', err);
    alert(`PDF 다운로드 중 오류가 발생했습니다: ${err.response?.data?.message || err.message}`);
  } finally {
    loading.value = false;
  }
};

// 통계 데이터 로드 (샘플 데이터)
const loadWeeklyStats = async () => {
  statsLoading.value = true;
  try {
    // 샘플 데이터 생성 (최근 7일)
    const endDate = new Date(currentFilters.value.date);
    const statsArray = [];
    
    for (let i = 6; i >= 0; i--) {
      const date = new Date(endDate);
      date.setDate(date.getDate() - i);
      
      statsArray.push({
        date: date.toISOString().split('T')[0],
        settlementDate: date.toISOString().split('T')[0],
        orderCount: Math.floor(Math.random() * 20) + 5, // 5-25 건
        dailyAmount: Math.floor(Math.random() * 500000) + 100000 // 100,000-600,000원
      });
    }
    
    console.log('📊 Sample weekly stats generated:', statsArray);
    statsData.value = statsArray;
  } catch (err) {
    console.error('Error loading weekly stats:', err);
    statsData.value = [];
  } finally {
    statsLoading.value = false;
  }
};

// 매출 추이 차트 데이터
const salesChartData = computed(() => {
  if (!statsData.value || statsData.value.length === 0) {
    return {
      labels: [],
      datasets: []
    };
  }
  
  console.log('📊 Creating chart from stats:', statsData.value);
  
  const labels = statsData.value.map(item => {
    // 다양한 날짜 필드명 시도
    const dateStr = item.settlementDate || item.date || item.createdAt;
    if (!dateStr) {
      console.warn('⚠️ No date field found in item:', item);
      return 'N/A';
    }
    
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) {
      console.warn('⚠️ Invalid date:', dateStr);
      return 'N/A';
    }
    
    return `${date.getMonth() + 1}/${date.getDate()}`;
  });
  
  const amounts = statsData.value.map(item => Number(item.dailyAmount) || 0);
  
  console.log('📊 Chart labels:', labels);
  console.log('📊 Chart amounts:', amounts);
  
  return {
    labels,
    datasets: [
      {
        label: '주간 매출 추이',
        data: amounts,
        borderColor: '#fb7185',
        backgroundColor: 'rgba(251, 113, 133, 0.1)',
        tension: 0.4,
        fill: true,
        pointRadius: 4,
        pointHoverRadius: 6
      }
    ]
  };
});

// 주문 건수 차트 데이터
const ordersChartData = computed(() => {
  if (!statsData.value || statsData.value.length === 0) {
    return {
      labels: [],
      datasets: []
    };
  }
  
  const labels = statsData.value.map(item => {
    const dateStr = item.settlementDate || item.date || item.createdAt;
    if (!dateStr) return 'N/A';
    
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return 'N/A';
    
    return `${date.getMonth() + 1}/${date.getDate()}`;
  });
  
  const counts = statsData.value.map(item => Number(item.orderCount) || 0);
  
  return {
    labels,
    datasets: [
      {
        label: '주문 건수',
        data: counts,
        backgroundColor: '#fb923c',
        borderColor: '#f97316',
        borderWidth: 2,
        borderRadius: 8
      }
    ]
  };
});

// 초기 데이터 로드
onMounted(() => {
  loadDailySettlement(currentFilters.value);
  // loadWeeklyStats(); // 자동 로드 제거 - 버튼 클릭 시에만 로드
});
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

      <!-- 정산 생성 진행 상황 표시 -->
      <div v-if="isCreating" class="progress-alert premium-card">
        <div class="progress-content">
          <div class="progress-icon">⏳</div>
          <div class="progress-info">
            <h4>정산 생성 중...</h4>
            <p>잠시만 기다려주세요...</p>
          </div>
        </div>
      </div>

      <SettlementFilter 
        mode="daily" 
        @filter-change="handleFilterChange"
      />

      <div class="action-bar">
        <button 
          class="btn-spicy create-btn" 
          @click="handleCreateSettlement"
          :disabled="isCreating || loading"
        >
          <span v-if="isCreating">⏳ 생성 중...</span>
          <span v-else>➕ 정산 생성하기</span>
        </button>
      </div>

      <DailySummary 
        :data="settlementData" 
        :loading="loading"
        @download-pdf="handleDownloadPdf"
      />

      <!-- 그래프 섹션 -->
      <div class="charts-section">
        <div class="section-header">
          <h3 class="section-title">📊 주간 매출 추이</h3>
          <button class="btn-spicy chart-load-btn" @click="loadWeeklyStats" :disabled="statsLoading">
            <span v-if="statsLoading">⏳ 조회 중...</span>
            <span v-else>📊 그래프 조회하기</span>
          </button>
        </div>
        
        <div v-if="statsLoading" class="chart-loading premium-card">
          <div class="tteok-spinner">🥘</div>
          <p>통계 데이터를 불러오는 중...</p>
        </div>
        
        <div v-else-if="statsData.length > 0" class="charts-grid">
          <div class="chart-card premium-card">
            <h4 class="chart-title">💰 주간 매출</h4>
            <SettlementChart :chart-data="salesChartData" />
          </div>
          
          <div class="chart-card premium-card">
            <h4 class="chart-title">📦 주간 주문 건수</h4>
            <BarChart :chart-data="ordersChartData" />
          </div>
        </div>
        
        <div v-else class="chart-empty premium-card">
          <span class="empty-icon">📊</span>
          <p>통계 데이터가 없습니다.</p>
          <p class="hint">위의 "그래프 조회하기" 버튼을 눌러주세요.</p>
        </div>
      </div>
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

@keyframes rotate {
  from { transform: rotate(0); }
  to { transform: rotate(360deg); }
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
  margin: 0;
}

.charts-section {
  margin-top: 3rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-title {
  font-size: 1.75rem;
  font-weight: 900;
  color: var(--deep-brown);
  margin: 0;
}

.chart-load-btn {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 2rem;
}

.chart-card {
  padding: 2rem;
  animation: pop 0.4s ease-out;
}

.chart-title {
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--deep-brown);
  margin: 0 0 1.5rem 0;
}

.chart-loading,
.chart-empty {
  padding: 3rem;
  text-align: center;
}

.chart-loading .tteok-spinner {
  font-size: 3rem;
  animation: rotate 1.5s infinite linear;
  margin-bottom: 1rem;
}

.chart-empty .empty-icon {
  font-size: 4rem;
  opacity: 0.2;
  margin-bottom: 1rem;
  display: block;
}

.chart-loading p,
.chart-empty p {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-muted);
  margin: 0;
}

.chart-empty .hint {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--sauce-orange);
  margin-top: 0.5rem;
}

@media (max-width: 768px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
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