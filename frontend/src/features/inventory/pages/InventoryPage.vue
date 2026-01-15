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

onMounted(loadProducts);
</script>

<template>
  <div class="tteokbokki-app">
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
