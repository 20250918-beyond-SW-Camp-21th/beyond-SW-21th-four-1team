<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  mode: {
    type: String,
    default: 'daily', // 'daily' or 'monthly'
    validator: (value) => ['daily', 'monthly'].includes(value)
  }
});

const emit = defineEmits(['filter-change']);

// Filter state
const storeId = ref(1);
const selectedDate = ref(new Date().toISOString().split('T')[0]); // YYYY-MM-DD
const selectedMonth = ref(new Date().toISOString().slice(0, 7)); // YYYY-MM
const selectedStatus = ref('ALL'); // ALL, WAITING, CONFIRMED, COMPLETED

const handleFilter = () => {
  if (props.mode === 'daily') {
    emit('filter-change', {
      storeId: storeId.value,
      date: selectedDate.value
    });
  } else {
    emit('filter-change', {
      storeId: storeId.value,
      yearMonth: selectedMonth.value,
      status: selectedStatus.value
    });
  }
};

// Auto-trigger on mount
watch([storeId, selectedDate, selectedMonth, selectedStatus], () => {
  handleFilter();
}, { immediate: true });
</script>

<template>
  <div class="filter-container premium-card">
    <div class="filter-header">
      <span class="icon">🔍</span>
      <h3>정산 조회</h3>
    </div>
    
    <div class="filter-body">
      <div class="filter-group">
        <label for="storeId">가맹점 ID</label>
        <input 
          id="storeId"
          v-model.number="storeId" 
          type="number" 
          min="1"
          placeholder="가맹점 번호를 입력하세요"
        />
      </div>

      <div v-if="mode === 'daily'" class="filter-group">
        <label for="date">조회 날짜</label>
        <input 
          id="date"
          v-model="selectedDate" 
          type="date"
          :max="new Date().toISOString().split('T')[0]"
        />
      </div>

      <template v-else>
        <div class="filter-group">
          <label for="month">조회 월</label>
          <input 
            id="month"
            v-model="selectedMonth" 
            type="month"
            :max="new Date().toISOString().slice(0, 7)"
          />
        </div>

        <div class="filter-group">
          <label for="status">정산 상태</label>
          <select 
            id="status"
            v-model="selectedStatus"
            class="status-select"
          >
            <option value="ALL">전체</option>
            <option value="WAITING">대기</option>
            <option value="CONFIRMED">확정</option>
            <option value="COMPLETED">지급 완료</option>
          </select>
        </div>
      </template>

      <button class="btn-spicy filter-btn" @click="handleFilter">
        <span>🌶️ 조회하기</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.filter-container {
  padding: 1.5rem;
  margin-bottom: 2rem;
  animation: pop 0.4s ease-out;
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.filter-header .icon {
  font-size: 1.5rem;
}

.filter-header h3 {
  font-size: 1.25rem;
  font-weight: 900;
  color: var(--deep-brown);
  margin: 0;
}

.filter-body {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.25rem;
  align-items: end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--text-muted);
}

.filter-group input {
  padding: 0.75rem 1rem;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  color: var(--deep-brown);
  background: white;
  transition: all 0.2s;
}

.filter-group input:focus {
  outline: none;
  border-color: var(--spicy-red);
  box-shadow: 0 0 0 3px rgba(251, 113, 133, 0.1);
}

.status-select {
  padding: 0.75rem 1rem;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  color: var(--deep-brown);
  background: white;
  transition: all 0.2s;
  cursor: pointer;
}

.status-select:focus {
  outline: none;
  border-color: var(--spicy-red);
  box-shadow: 0 0 0 3px rgba(251, 113, 133, 0.1);
}

.status-select:hover {
  border-color: var(--sauce-orange);
}

.filter-btn {
  padding: 0.75rem 1.5rem;
  height: auto;
  font-size: 1rem;
}

@media (max-width: 768px) {
  .filter-body {
    grid-template-columns: 1fr;
  }
}
</style>