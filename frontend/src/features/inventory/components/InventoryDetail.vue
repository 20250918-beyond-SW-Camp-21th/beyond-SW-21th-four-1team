<script setup>
import { defineProps, defineEmits, ref, watch, onMounted } from 'vue';
import { inventoryApi } from '../api/inventoryApi';

const props = defineProps({ product: { type: Object, required: true } });
const emit = defineEmits(['back', 'request-inbound']);

const demandInfo = ref(null);
const demandError = ref(null);
const demandLoading = ref(false);

const checkSafetyStock = async () => {
  if (!props.product?.productId) return;
  demandLoading.value = true;
  demandError.value = null;
  console.log('Checking safety stock for product: ' + props.product.productId);
  try {
    const data = await inventoryApi.checkDemand(props.product.productId);
    console.log('Safety Stock Data:', data);
    demandInfo.value = data;
    if (!data) demandError.value = "Data is null (API failed?)";
  } catch (e) {
    console.error('Safety stock check failed:', e);
    demandError.value = e.message;
  } finally {
    demandLoading.value = false;
  }
};

onMounted(checkSafetyStock);
watch(() => props.product, checkSafetyStock);
</script>

<template>
  <div class="detail-container">
    <div class="top-nav">
      <button class="back-link" @click="$emit('back')">◀ 주방 목록으로 돌아가기</button>
      <button class="btn-spicy refill-btn" @click="$emit('request-inbound', product.productName)">
        🌶️ 재고 채우기
      </button>
    </div>

    <!-- Debug Section -->
    <div v-if="demandError" class="debug-box error">⚠️ 에러: {{ demandError }}</div>
    <div v-if="demandLoading" class="debug-box">⏳ 안전재고 확인 중...</div>

    <!-- Alert Section -->
    <div v-if="demandInfo && demandInfo.isOrderRequired" class="spicy-alert">
      <div class="alert-icon">🚨</div>
      <div class="alert-content">
        <h3>{{ demandInfo.message }}</h3>
        <p>지금 바로 입고가 필요합니다! 셰프님, 재료가 떨어져가요!</p>
      </div>
      <button class="btn-alert-action" @click="$emit('request-inbound', product.productName)">
        바로 주문하기 📦
      </button>
    </div>

    <div class="product-hero premium-card">
      <div class="p-brand-tag">Premium Ingredient</div>
      <div class="p-main-info">
         <h1>{{ product.productName }}</h1>
         <div class="id-badge">ID-#{{ product.productId }}</div>
      </div>
      
      <div class="stats-rack">
        <div class="stat-pill">
          <label>현재 보유량</label>
          <div class="val" :class="{ 'warning': product.totalQuantity < product.minimumQuantity }">
            {{ product.totalQuantity }}<span>개</span>
          </div>
        </div>
        <div class="stat-pill">
          <label>적정 보관량</label>
          <div class="val">{{ product.minimumQuantity }}<span>개</span></div>
        </div>
        <div class="stat-pill price-pill">
          <label>개당 가격</label>
          <div class="val">₩{{ product.price?.toLocaleString() || '0' }}</div>
        </div>
      </div>
    </div>

    <div class="lot-area">
      <h3 class="area-title">🥘 세부 보관 내역 (Lot)</h3>
      <div class="lot-grid">
        <div v-for="lot in product.products" :key="lot.productCode" class="lot-card premium-card">
          <div class="lot-type" :class="lot.status">{{ lot.status }}</div>
          <div class="lot-code">{{ lot.productCode }}</div>
          <div class="lot-details">
            <div class="row">
              <label>보유수량</label>
              <strong>{{ lot.quantity }}개</strong>
            </div>
            <div class="row">
              <label>유통기한</label>
              <strong class="expiry">{{ lot.expirationDate }}</strong>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!product.products || product.products.length === 0" class="empty-lots">
        보관 중인 로트 정보가 없습니다. 🌶️
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-container {
  animation: pop 0.5s ease-out;
}

.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.back-link {
  background: none;
  border: none;
  font-weight: 800;
  color: var(--text-muted);
  cursor: pointer;
}

.refill-btn {
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
}

.product-hero {
  padding: 3rem;
  background: white;
  margin-bottom: 3rem;
  position: relative;
}

.p-brand-tag {
  font-size: 0.8rem;
  font-weight: 900;
  color: var(--spicy-red);
  text-transform: uppercase;
  letter-spacing: 0.1rem;
  margin-bottom: 0.75rem;
}

.p-main-info {
  display: flex;
  align-items: baseline;
  gap: 1.5rem;
  margin-bottom: 3rem;
  flex-wrap: wrap;
}

h1 { font-size: 3.5rem; font-weight: 950; color: var(--deep-brown); margin: 0; letter-spacing: -0.05em; }
.id-badge { background: var(--rice-cream); color: var(--sauce-orange); padding: 0.4rem 1rem; border-radius: 99px; font-weight: 800; border: 2px solid var(--border-color); }

.stats-rack {
  display: flex;
  gap: 5rem;
  flex-wrap: wrap;
}

.stat-pill { display: flex; flex-direction: column; gap: 0.5rem; }
.stat-pill label { font-size: 0.95rem; font-weight: 800; color: var(--text-muted); }
.stat-pill .val { font-size: 3rem; font-weight: 950; color: var(--deep-brown); }
.stat-pill .val span { font-size: 1.25rem; color: var(--text-muted); margin-left: 0.25rem; }
.stat-pill .val.warning { color: var(--spicy-red); animation: pulse 2s infinite; }

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.price-pill .val { color: var(--sauce-orange); }

.area-title { font-size: 1.75rem; font-weight: 900; color: var(--deep-brown); margin-bottom: 2rem; }

.lot-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.lot-card {
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.lot-type {
  align-self: flex-start;
  font-size: 0.75rem;
  font-weight: 900;
  padding: 0.3rem 0.8rem;
  background: var(--rice-cream);
  border-radius: 99px;
  color: var(--text-muted);
  border: 1px solid var(--border-color);
}

.lot-code { font-family: monospace; font-size: 1.25rem; font-weight: 900; color: var(--spicy-red); }

.lot-details { display: flex; flex-direction: column; gap: 1rem; }
.row { display: flex; justify-content: space-between; }
.row label { color: var(--text-muted); font-weight: 700; font-size: 0.9rem; }
.row strong { color: var(--deep-brown); font-weight: 800; }
.expiry { color: var(--sauce-orange); }

.empty-lots { text-align: center; padding: 5rem; background: white; border-radius: 24px; color: var(--text-muted); font-weight: 700; border: 2px dashed var(--border-color); }

.spicy-alert {
  background: #fff1f2;
  border: 3px solid #e11d48;
  border-radius: 24px;
  padding: 1.5rem 2rem;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
}

.alert-icon { font-size: 2.5rem; }

.alert-content { flex: 1; }
.alert-content h3 { color: #e11d48; font-size: 1.25rem; font-weight: 900; margin: 0 0 0.25rem 0; }
.alert-content p { color: #9f1239; font-weight: 700; margin: 0; }

.btn-alert-action {
  background: #e11d48;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 14px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 4px 0 #9f1239;
  transition: all 0.1s;
}

.btn-alert-action:active {
  transform: translateY(2px);
  box-shadow: 0 2px 0 #9f1239;
}

@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
  40%, 60% { transform: translate3d(4px, 0, 0); }
}


.debug-box {
  background: #f3f4f6;
  padding: 0.5rem;
  margin-bottom: 1rem;
  font-size: 0.8rem;
  border: 1px dashed #ccc;
  border-radius: 8px;
}
.debug-box.error {
  color: red;
  background: #fee2e2;
  border-color: red;
}
</style>
