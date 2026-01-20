<script setup>
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

const emit = defineEmits(['download-pdf', 'create-settlement']);

const formatCurrency = (value) => {
  if (!value) return '₩0';
  return `₩${Number(value).toLocaleString()}`;
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' });
};

const getStatusBadge = (status) => {
  const badges = {
    ORDERED: { text: '발주 완료', class: 'status-ordered', icon: '📦' },
    PAID: { text: '결제 완료', class: 'status-paid', icon: '✅' },
    COMPLETED: { text: '영수증 발행 완료', class: 'status-completed', icon: '🎉' }
  };
  return badges[status] || { text: status, class: '', icon: '📋' };
};

const handleDownload = () => {
  emit('download-pdf');
};
</script>

<template>
  <div class="monthly-table">
    <div v-if="loading" class="loading-state premium-card">
      <div class="tteok-spinner">🥘</div>
      <p>월별 정산 데이터를 불러오는 중...</p>
    </div>

    <template v-else-if="data">
      <!-- Summary Cards -->
      <div class="summary-grid">
        <div class="summary-card premium-card">
          <div class="card-icon">💵</div>
          <div class="card-content">
            <label>주문 금액 합계</label>
            <div class="value">{{ formatCurrency(data.totalAmount) }}</div>
          </div>
        </div>

        <div class="summary-card premium-card">
          <div class="card-icon">💳</div>
          <div class="card-content">
            <label>공급가액</label>
            <div class="value supply">{{ formatCurrency(data.supplyAmount) }}</div>
          </div>
        </div>

        <div class="summary-card premium-card">
          <div class="card-icon">📄</div>
          <div class="card-content">
            <label>부가세 (10%)</label>
            <div class="value tax">{{ formatCurrency(data.taxAmount) }}</div>
          </div>
        </div>

        <div class="summary-card premium-card highlight">
          <div class="card-icon">🎯</div>
          <div class="card-content">
            <label>최종 매입 금액</label>
            <div class="value final">{{ formatCurrency(data.totalAmount) }}</div>
          </div>
        </div>
      </div>

      <!-- Details Table -->
      <div class="details-section premium-card">
        <div class="details-header">
          <h3>📊 정산 상세 정보</h3>
          <button class="btn-download" @click="handleDownload">
            <span>📄 PDF 다운로드</span>
          </button>
        </div>

        <div class="details-table">
          <div class="table-row">
            <div class="table-label">정산 상태</div>
            <div class="table-value">
              <span 
                v-if="data.status" 
                class="status-badge" 
                :class="getStatusBadge(data.status).class"
              >
                {{ getStatusBadge(data.status).icon }} {{ getStatusBadge(data.status).text }}
              </span>
              <span v-else class="text-muted">-</span>
            </div>
          </div>

          <div class="table-row">
            <div class="table-label">지급 예정일</div>
            <div class="table-value">
              <span class="payout-date">{{ formatDate(data.payoutDate) }}</span>
            </div>
          </div>

          <div class="table-row">
            <div class="table-label">공급가액</div>
            <div class="table-value">{{ formatCurrency(data.supplyAmount) }}</div>
          </div>

          <div class="table-row">
            <div class="table-label">부가세 (10%)</div>
            <div class="table-value tax">+ {{ formatCurrency(data.taxAmount) }}</div>
          </div>

          <div class="table-row total-row">
            <div class="table-label">최종 매입 금액</div>
            <div class="table-value final">{{ formatCurrency(data.totalAmount) }}</div>
          </div>
        </div>
      </div>
    </template>

    <div v-else class="empty-state premium-card">
      <span class="empty-icon">📭</span>
      <p>해당 월의 정산 데이터가 없습니다.</p>
      <button class="btn-spicy create-btn" @click="$emit('create-settlement')">
        <span>➕ 정산 생성하기</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.monthly-table {
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
  background: linear-gradient(135deg, #fdf2f8, #ffffff);
  border-color: var(--spicy-red);
  border-width: 3px;
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
  font-size: 0.85rem;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.value {
  font-size: 1.75rem;
  font-weight: 900;
  color: var(--deep-brown);
}

.value.supply {
  color: var(--deep-brown);
}

.value.tax {
  color: var(--sauce-orange);
}

.value.final {
  color: var(--spicy-red);
}

.details-section {
  padding: 2rem;
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.details-header h3 {
  font-size: 1.5rem;
  font-weight: 900;
  color: var(--deep-brown);
  margin: 0;
}

.btn-download {
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, var(--spicy-red), var(--sauce-orange));
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 4px 0px #e11d48;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-download:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 0px #e11d48;
}

.btn-download:active {
  transform: translateY(2px);
  box-shadow: 0 2px 0px #e11d48;
}

.details-table {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.table-row {
  display: grid;
  grid-template-columns: 200px 1fr;
  padding: 1rem 1.5rem;
  background: #fffbeb;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  transition: all 0.2s;
}

.table-row:hover {
  background: #fef3c7;
}

.table-row.highlight-row {
  background: #fff7ed;
  border-color: var(--sauce-orange);
}

.table-row.total-row {
  background: linear-gradient(135deg, #fdf2f8, #fff7ed);
  border: 2px solid var(--spicy-red);
  border-width: 2px;
  margin-top: 0.5rem;
}

.table-label {
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-muted);
}

.table-value {
  font-size: 1.1rem;
  font-weight: 800;
  color: var(--deep-brown);
  text-align: right;
}

.table-value.tax {
  color: var(--sauce-orange);
}

.table-value.final {
  color: var(--spicy-red);
  font-size: 1.5rem;
}

.status-badge {
  display: inline-block;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.95rem;
  font-weight: 800;
}

.status-ordered {
  background: #fef3c7;
  color: #92400e;
  border: 2px solid #fde68a;
}

.status-paid {
  background: #d1fae5;
  color: #065f46;
  border: 2px solid #6ee7b7;
}

.status-completed {
  background: #ddd6fe;
  color: #5b21b6;
  border: 2px solid #c4b5fd;
}

.payout-date {
  color: var(--spicy-red);
  font-weight: 900;
}

.text-muted {
  color: var(--text-muted);
  font-weight: 600;
}

.loading-state,
.empty-state {
  padding: 4rem;
  text-align: center;
}

.empty-state {
  text-align: center;
  padding: 5rem 2rem;
  background: white;
  border-radius: 32px;
  border: 3px solid var(--border-color);
  animation: pop 0.4s ease-out;
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
  opacity: 0.2;
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

.empty-state p {
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--text-muted);
  margin-bottom: 2rem;
}

.create-btn {
  margin-top: 1.5rem;
  padding: 1rem 2rem;
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .table-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }

  .table-value {
    text-align: left;
  }

  .details-header {
    flex-direction: column;
    align-items: stretch;
  }

  .btn-download {
    width: 100%;
    justify-content: center;
  }
}
</style>