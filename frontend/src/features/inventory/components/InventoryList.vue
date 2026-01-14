<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  products: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false }
});

const emit = defineEmits(['view-detail', 'request-inbound', 'request-outbound', 'search']);

const handleSearch = (e) => {
  emit('search', e.target.value);
};
</script>

<template>
  <div class="list-container">
    <div class="search-actions">
      <div class="search-pill premium-card">
        <span class="icon">🔍</span>
        <input type="text" placeholder="어떤 상품을 찾으시나요?" @input="handleSearch" />
      </div>
      <div class="main-btns">
        <button class="btn-order action-btn" @click="$emit('request-inbound')">
          <span>📦 주문하기</span>
        </button>
        <button class="btn-spicy action-btn" @click="$emit('request-inbound')">
          <span>🌶️ 입고하기</span>
        </button>
        <button class="btn-outbound" @click="$emit('request-outbound')">
          <span>🔥 출고하기</span>
        </button>
      </div>
    </div>

    <div class="inventory-grid">
      <div v-if="loading" class="loading-state">
        <div class="tteok-spinner">🥘</div>
        <p>주방에서 데이터를 요리 중입니다...</p>
      </div>

      <template v-else>
        <div v-for="p in products" :key="p.productId" class="p-card premium-card" @click="$emit('view-detail', p.productId)">
          <div class="card-head">
            <span class="p-id">No. {{ p.productId }}</span>
            <div class="status-indicator" :class="{ 'warning': p.totalQuantity < p.minimumQuantity }">
               {{ p.totalQuantity < p.minimumQuantity ? '⚠️ 품절위기' : '✅ 든든함' }}
            </div>
          </div>
          
          <div class="card-body">
            <h3 class="p-name">{{ p.productName }}</h3>
            <div class="stock-display">
              <label>현재 재고</label>
              <div class="amount">
                <span class="val">{{ p.totalQuantity }}</span>
                <span class="unit">개</span>
              </div>
            </div>
            <div class="stock-bar">
               <div class="fill" :style="{ width: Math.min((p.totalQuantity / (p.minimumQuantity || 1)) * 100, 100) + '%' }"></div>
            </div>
          </div>

          <div class="card-footer">
            <div class="p-price">₩{{ p.price?.toLocaleString() || '0' }}</div>
            <div class="card-footer-actions">
              <button class="order-link" @click.stop="$emit('request-inbound', p.productName)">📦 주문</button>
              <button class="detail-link">🔍 상세보기</button>
            </div>
          </div>
        </div>

        <div v-if="products.length === 0" class="empty-state premium-card">
          <span class="empty-icon">🥣</span>
          <p>등록된 상품이 비어있습니다.</p>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.list-container {
  animation: pop 0.4s ease-out;
}

.search-actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 2.5rem;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.search-pill {
  flex: 1;
  max-width: 450px;
  display: flex;
  align-items: center;
  padding: 0 1.5rem;
  height: 60px;
  background: white !important;
}

.search-pill input {
  background: transparent;
  border: none;
  color: var(--deep-brown);
  font-size: 1.1rem;
  font-weight: 600;
  margin-left: 1rem;
  width: 100%;
}

.search-pill input::placeholder { color: #d0885e; }
.search-pill input:focus { outline: none; }

.main-btns { display: flex; gap: 1rem; }

.action-btn {
  padding: 0 1.75rem;
  height: 60px;
  font-size: 1.05rem;
}

.btn-outbound {
  padding: 0 1.75rem;
  height: 60px;
  border-radius: 16px;
  border: 2px solid var(--sauce-orange);
  background: white;
  color: var(--sauce-orange);
  font-weight: 800;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outbound:hover {
  background: #fff7ed;
  transform: translateY(-2px);
}

.inventory-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
}

.p-card {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  cursor: pointer;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.p-id { font-weight: 800; color: var(--sauce-orange); font-size: 0.9rem; }
.status-indicator {
  font-size: 0.8rem;
  font-weight: 800;
  padding: 0.25rem 0.6rem;
  background: #f0fdf4;
  color: #166534;
  border-radius: 8px;
}
.status-indicator.warning {
  background: #fff1f2;
  color: #be123c;
}

.p-name {
  font-size: 1.5rem;
  font-weight: 900;
  color: var(--deep-brown);
  margin: 0;
  word-break: keep-all;
}

.stock-display {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-top: 0.5rem;
}

.stock-display label { font-size: 0.85rem; font-weight: 700; color: var(--text-muted); }
.stock-display .val { font-size: 1.75rem; font-weight: 900; color: var(--deep-brown); }
.stock-display .unit { font-size: 1rem; color: var(--text-muted); margin-left: 0.25rem; }

.stock-bar {
  height: 10px;
  background: #fdf2f2;
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid #fee2e2;
}

.fill {
  height: 100%;
  background: linear-gradient(to right, var(--sauce-orange), var(--spicy-red));
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px dashed var(--border-color);
  padding-top: 1rem;
  margin-top: auto;
}

.p-price { font-weight: 900; color: var(--spicy-red); font-size: 1.15rem; }
.card-footer-actions { display: flex; gap: 0.75rem; align-items: center; }
.order-link { background: #fdf2f2; border: 1.5px solid var(--spicy-red); color: var(--spicy-red); padding: 0.35rem 0.75rem; border-radius: 10px; font-weight: 800; cursor: pointer; transition: all 0.2s; }
.order-link:hover { background: var(--spicy-red); color: white; }
.detail-link { background: none; border: none; font-weight: 700; color: var(--text-muted); cursor: pointer; }

.btn-order {
  padding: 0 1.75rem;
  height: 60px;
  border-radius: 16px;
  border: 2px solid var(--spicy-red);
  background: white;
  color: var(--spicy-red);
  font-weight: 800;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-order:hover {
  background: #fdf2f2;
  transform: translateY(-2px);
}

.loading-state, .empty-state {
  grid-column: 1 / -1;
  padding: 6rem;
  text-align: center;
}

.tteok-spinner { font-size: 3rem; animation: rotate 1.5s infinite linear; }
@keyframes rotate { from { transform: rotate(0); } to { transform: rotate(360deg); } }

.empty-icon { font-size: 4rem; opacity: 0.5; margin-bottom: 1rem; display: block; }
.empty-state p { font-size: 1.25rem; font-weight: 700; color: var(--text-muted); }
</style>
