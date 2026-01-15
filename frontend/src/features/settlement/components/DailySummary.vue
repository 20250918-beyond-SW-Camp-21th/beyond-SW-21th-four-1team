<script setup>
import { computed } from 'vue';

const props = defineProps({
  data: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const formatCurrency = (value) => {
  if (!value) return '₩0';
  return `₩${Number(value).toLocaleString()}`;
};

const progressPercentage = computed(() => {
  if (!props.data || !props.data.dailyAmount || !props.data.monthlyAccumulatedAmount) return 0;
  const percentage = (Number(props.data.dailyAmount) / Number(props.data.monthlyAccumulatedAmount)) * 100;
  return Math.min(percentage, 100);
});
</script>

<template>
  <div class="daily-summary">
    <div v-if="loading" class="loading-state premium-card">
      <div class="tteok-spinner">🥘</div>
      <p>정산 데이터를 불러오는 중...</p>
    </div>

    <template v-else-if="data">
      <div class="summary-grid">
        <!-- Order Count Card -->
        <div class="summary-card premium-card">
          <div class="card-icon">📦</div>
          <div class="card-content">
            <label>주문 건수</label>
            <div class="value">
              <span class="number">{{ data.orderCount || 0 }}</span>
              <span class="unit">건</span>
            </div>
          </div>
        </div>

        <!-- Daily Amount Card -->
        <div class="summary-card premium-card highlight">
          <div class="card-icon">💰</div>
          <div class="card-content">
            <label>일 주문 금액</label>
            <div class="value">
              <span class="number">{{ formatCurrency(data.dailyAmount) }}</span>
            </div>
          </div>
        </div>

        <!-- Monthly Accumulated Card -->
        <div class="summary-card premium-card accent">
          <div class="card-icon">📊</div>
          <div class="card-content">
            <label>월 누적 금액</label>
            <div class="value">
              <span class="number">{{ formatCurrency(data.monthlyAccumulatedAmount) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Progress Bar -->
      <div class="progress-section premium-card">
        <div class="progress-header">
          <span class="icon">🔥</span>
          <h3>이번 달 진행률</h3>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
        </div>
        <p class="progress-text">
          오늘 매출이 이번 달 누적 매출의 <strong>{{ progressPercentage.toFixed(1) }}%</strong>를 차지합니다
        </p>
      </div>
    </template>

    <div v-else class="empty-state premium-card">
      <span class="empty-icon">📭</span>
      <p>해당 날짜의 정산 데이터가 없습니다.</p>
    </div>
  </div>
</template>

<style scoped>
.daily-summary {
  animation: pop 0.4s ease-out;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.summary-card {
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: all 0.3s;
}

.summary-card:hover {
  transform: translateY(-4px);
}

.summary-card.highlight {
  background: linear-gradient(135deg, #fff7ed, #ffffff);
  border-color: var(--sauce-orange);
}

.summary-card.accent {
  background: linear-gradient(135deg, #fdf2f8, #ffffff);
  border-color: var(--spicy-red);
}

.card-icon {
  font-size: 3rem;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.card-content label {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.value {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.value .number {
  font-size: 2rem;
  font-weight: 900;
  color: var(--deep-brown);
}

.value .unit {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-muted);
}

.progress-section {
  padding: 2rem;
}

.progress-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
}

.progress-header .icon {
  font-size: 1.5rem;
}

.progress-header h3 {
  font-size: 1.25rem;
  font-weight: 900;
  color: var(--deep-brown);
  margin: 0;
}

.progress-bar {
  height: 20px;
  background: #fef3c7;
  border-radius: 20px;
  overflow: hidden;
  border: 2px solid var(--border-color);
  margin-bottom: 1rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(to right, var(--sauce-orange), var(--spicy-red));
  transition: width 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border-radius: 20px;
}

.progress-text {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-muted);
  text-align: center;
  margin: 0;
}

.progress-text strong {
  color: var(--spicy-red);
  font-weight: 900;
}

.loading-state,
.empty-state {
  padding: 4rem;
  text-align: center;
}

.tteok-spinner {
  font-size: 3rem;
  animation: rotate 1.5s infinite linear;
  margin-bottom: 1rem;
}

@keyframes rotate {
  from { transform: rotate(0); }
  to { transform: rotate(360deg); }
}

.empty-icon {
  font-size: 4rem;
  opacity: 0.5;
  margin-bottom: 1rem;
  display: block;
}

.empty-state p,
.loading-state p {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-muted);
  margin: 0;
}
</style>