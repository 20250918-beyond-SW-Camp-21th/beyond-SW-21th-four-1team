<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { inventoryApi } from '../api/inventoryApi';
import InventoryList from '../components/InventoryList.vue';
import InventoryDetail from '../components/InventoryDetail.vue';
import InboundModal from '../components/InboundModal.vue';
import OutboundModal from '../components/OutboundModal.vue';

const router = useRouter();

// State
const viewMode = ref('list'); 
const products = ref([]);
const selectedProduct = ref(null);
const loading = ref(false);
const error = ref(null);

// Modal State
const showInboundModal = ref(false);
const showOutboundModal = ref(false);
const prefilledProductName = ref('');
const prefilledProductId = ref(null);

const loadProducts = async () => {
  loading.value = true;
  error.value = null;
  try {
    const data = await inventoryApi.getAllProducts();
    console.log('Loaded products:', data);
    products.value = data || [];
  } catch (err) {
    error.value = `데이터를 불러올 수 없습니다: ${err.message}`;
  } finally {
    loading.value = false;
  }
};

const handleSearch = async (query) => {
  if (!query) {
    loadProducts();
    return;
  }
  loading.value = true;
  try {
    const result = await inventoryApi.searchByName(query);
    products.value = result ? [result] : [];
  } catch (err) {
    products.value = [];
  } finally {
    loading.value = false;
  }
};

const openDetail = async (id) => {
  loading.value = true;
  try {
    const detail = await inventoryApi.searchProduct(id);
    selectedProduct.value = detail;
    viewMode.value = 'detail';
  } catch (err) {
    console.error(err);
    alert(`상품 상세 정보를 가져오는데 실패했습니다: ${err.message}`);
  } finally {
    loading.value = false;
  }
};

const goBackToList = () => {
  viewMode.value = 'list';
  selectedProduct.value = null;
  loadProducts();
};

const openInboundRequest = (name = '') => {
  prefilledProductName.value = name;
  showInboundModal.value = true;
};

const handleInboundConfirm = async (data) => {
  try {
    await inventoryApi.inbound(data);
    alert('🌶️ 맛있게 입고되었습니다!');
    showInboundModal.value = false;
    if (viewMode.value === 'detail' && selectedProduct.value) {
      await openDetail(selectedProduct.value.productId);
    } else {
      await loadProducts();
    }
  } catch (err) {
    alert('입구 처리 중 불맛이 좀 강했나요? 오류가 발생했습니다.');
  }
};

const openOutboundRequest = (id = null) => {
   prefilledProductId.value = id || (selectedProduct.value?.productId);
   showOutboundModal.value = true;
};

const handleOutboundConfirm = async (data) => {
  try {
    await inventoryApi.outbound(data);
    alert('🔥 화끈하게 출고되었습니다!');
    showOutboundModal.value = false;
    if (viewMode.value === 'detail' && selectedProduct.value) {
      await openDetail(selectedProduct.value.productId);
    } else {
      await loadProducts();
    }
  } catch (err) {
    alert('출고 처리 중 재고가 부족해 김이 빠졌네요.');
  }
};

const goToProductOrder = () => {
  router.push('/products');
};

const handleLogout = () => {
  // Placeholder for future logout logic (e.g., clearing tokens)
  if (confirm('정말 주방에서 퇴근하시겠습니까? 👩‍🍳')) {
    alert('로그아웃되었습니다. 다음에 또 만나요!');
    // router.push('/login') // Later: redirect to login
  }
};

onMounted(loadProducts);
</script>

<template>
  <div class="tteokbokki-app">
    <nav class="tteok-nav">
      <div class="nav-container">
        <div class="brand">
          <span class="brand-icon">🌶️</span>
          <h1 class="brand-name">SPICY</h1>
        </div>
        <div class="nav-links">
          <span class="user-info">어서오세요, 셰프님! 👩‍🍳</span>
          <button class="order-status-btn" @click="router.push('/orders')">주문 현황 확인</button>
          <button class="cart-btn" @click="router.push('/cart')">장바구니 확인</button>
          <button class="logout-btn" @click="handleLogout">퇴근하기</button>
        </div>
      </div>
    </nav>

    <header class="tteok-header">
      <div class="header-card premium-card">
        <h2>실시간 주방 현황</h2>
        <p>오늘의 매콤한 재고 현황을 한눈에 확인하세요.</p>
        <div class="header-decor">🥘</div>
      </div>
    </header>

    <main class="main-content">
      <div v-if="error" class="error-alert">
        <span class="icon">🌶️</span> {{ error }}
      </div>

      <transition name="pop-in" mode="out-in">
        <InventoryList 
          v-if="viewMode === 'list'"
          :products="products" 
          :loading="loading"
          @view-detail="openDetail"
          @request-inbound="goToProductOrder"
          @request-outbound="openOutboundRequest"
          @search="handleSearch"
        />
        
        <InventoryDetail 
          v-else-if="viewMode === 'detail' && selectedProduct"
          :product="selectedProduct"
          @back="goBackToList"
          @request-inbound="openInboundRequest"
        />
      </transition>
    </main>

    <!-- Modals -->
    <InboundModal 
      :is-open="showInboundModal"
      :prefilled-product-name="prefilledProductName"
      @close="showInboundModal = false"
      @confirm="handleInboundConfirm"
    />

    <OutboundModal 
      :is-open="showOutboundModal"
      :prefilled-product-id="prefilledProductId"
      @close="showOutboundModal = false"
      @confirm="handleOutboundConfirm"
    />
  </div>
</template>

<style scoped>
.tteokbokki-app {
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

.brand-name span {
  color: var(--spicy-red);
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

.order-status-btn {
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

.order-status-btn:hover {
  background: #fde68a;
}

.cart-btn {
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

.cart-btn:hover {
  background: #fde68a;
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

/* Pop-in Transitions */
.pop-in-enter-active,
.pop-in-leave-active {
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.pop-in-enter-from {
  opacity: 0;
  transform: scale(0.9) translateY(20px);
}

.pop-in-leave-to {
  opacity: 0;
  transform: scale(0.9) translateY(-20px);
}
</style>
