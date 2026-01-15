<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { orderApi } from '../../api/orderApi';

const router = useRouter();
const loading = ref(false);
const orders = ref([]);
const currentStatus = ref('PENDING');
const storeId = 1; // Simulation
const expandedOrderId = ref(null);
const orderItemsMap = ref({});
const fetchingDetails = ref(false);

const statuses = [
  { key: 'PENDING', label: '접수 대기', icon: '📝' },
  { key: 'PROCESSING', label: '처리중', icon: '👨‍🍳' },
  { key: 'SHIPPED', label: '배송중', icon: '🚚' },
  { key: 'DELIVERED', label: '도착 완료', icon: '🏠' },
  { key: 'CANCELLED', label: '취소됨', icon: '❌' }
];

const fetchOrders = async () => {
  loading.value = true;
  expandedOrderId.value = null; // Reset expansion when tab changes
  try {
    const response = await orderApi.getOrders(storeId, currentStatus.value);
    orders.value = response.data || [];
  } catch (err) {
    console.error('Failed to fetch orders:', err);
  } finally {
    loading.value = false;
  }
};

const toggleDetails = async (orderId) => {
  if (expandedOrderId.value === orderId) {
    expandedOrderId.value = null;
    return;
  }

  expandedOrderId.value = orderId;

  // Fetch details if not already in map
  if (!orderItemsMap.value[orderId]) {
    fetchingDetails.value = true;
    try {
      const response = await orderApi.getOrderDetails(storeId, orderId);
      orderItemsMap.value[orderId] = response.data || [];
    } catch (err) {
      console.error('Failed to fetch order details:', err);
      alert('상세 정보를 가져오는데 실패했습니다.');
    } finally {
      fetchingDetails.value = false;
    }
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const cancelOrder = async (orderId) => {
  if (!confirm('🛑 정말 주문을 취소하시겠습니까?')) return;
  
  loading.value = true;
  try {
    await orderApi.cancelOrder(storeId, orderId);
    alert('✅ 주문이 취소되었습니다.');
    await fetchOrders(); // Refresh list
  } catch (err) {
    console.error('Failed to cancel order:', err);
    alert('🔥 주문 취소에 실패했습니다.');
  } finally {
    loading.value = false;
  }
};

watch(currentStatus, fetchOrders);

onMounted(fetchOrders);
</script>

<template>
  <div class="order-list-page">
    <header class="tteok-header">
      <div class="header-card premium-card">
        <h2>주문 현황 확인 📦</h2>
        <p>요청하신 주문들의 진행 상황을 실시간으로 확인하세요.</p>
        <div class="header-decor">🚚</div>
      </div>
    </header>

    <main class="main-content">
      <!-- Status Tabs -->
      <div class="status-tabs premium-card">
        <button 
          v-for="s in statuses" 
          :key="s.key"
          class="tab-btn"
          :class="{ active: currentStatus === s.key }"
          @click="currentStatus = s.key"
        >
          <span class="tab-icon">{{ s.icon }}</span>
          <span class="tab-label">{{ s.label }}</span>
        </button>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="tteok-spinner">🥘</div>
        <p>주문 내역을 불러오고 있습니다...</p>
      </div>

      <div v-else-if="orders.length === 0" class="empty-state premium-card">
        <span class="empty-icon">📂</span>
        <p>해당 상태의 주문 내역이 없습니다.</p>
      </div>

      <div v-else class="orders-container">
        <div v-for="order in orders" :key="order.orderId" class="order-card premium-card">
          <div class="order-header">
            <div class="order-meta">
              <span class="order-id">주문번호: {{ order.orderNumber }}</span>
              <span class="order-date">{{ formatDate(order.createdAt) }}</span>
            </div>
            <div v-if="order.status !== 'PENDING'" class="order-status-badge" :class="order.status.toLowerCase()">
              {{ statuses.find(s => s.key === order.status)?.label }}
            </div>
            <button 
              v-if="order.status === 'PENDING'" 
              class="cancel-btn" 
              @click.stop="cancelOrder(order.orderId)"
            >
              접수 취소
            </button>
          </div>

          <div class="order-body">
            <div class="info-row">
              <span class="label">수령인</span>
              <span class="value">{{ order.receiverName }} ({{ order.receiverPhone }})</span>
            </div>
            <div class="info-row">
              <span class="label">배송지</span>
              <span class="value">{{ order.address }}</span>
            </div>
            <div class="info-row" v-if="order.memo">
              <span class="label">요청사항</span>
              <span class="value memo">{{ order.memo }}</span>
            </div>
            <div class="info-row">
              <span class="label">희망 배송일</span>
              <span class="value delivery-date">📅 {{ order.deliveryDate }}</span>
            </div>
          </div>

          <div class="order-footer">
            <button class="detail-toggle-btn" @click="toggleDetails(order.orderId)">
              {{ expandedOrderId === order.orderId ? '닫기 ▲' : '주문 상품 보기 ▼' }}
            </button>
            <div class="total-amount">
              <span>총 주문 금액</span>
              <strong>₩{{ order.totalAmount.toLocaleString() }}</strong>
            </div>
          </div>

          <!-- Expanded Details Section -->
          <div v-if="expandedOrderId === order.orderId" class="order-details-pane">
            <div v-if="fetchingDetails" class="mini-loading">
              <span class="mini-spinner">🌶️</span> 불러오는 중...
            </div>
            <div v-else-if="orderItemsMap[order.orderId]?.length > 0" class="items-list">
              <div v-for="item in orderItemsMap[order.orderId]" :key="item.productName" class="order-item-row">
                <span class="item-name">{{ item.productName }}</span>
                <span class="item-qty">{{ item.quantity }}개</span>
                <span class="item-price">₩{{ item.totalPrice.toLocaleString() }}</span>
              </div>
            </div>
            <div v-else class="no-items">상품 정보가 없습니다.</div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.order-list-page {
  min-height: 100vh;
  background-color: #fffbeb;
}

.tteok-header {
  max-width: 1000px;
  margin: 2rem auto 0;
  padding: 0 2rem;
}

.header-card {
  padding: 2.5rem;
  background: white;
  border-radius: 32px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 20px 40px -10px rgba(0,0,0,0.05);
  border: 3px solid #fde68a;
}

.header-card h2 { font-size: 2.25rem; font-weight: 950; color: #451a03; margin: 0 0 0.5rem 0; }
.header-card p { color: #92400e; font-weight: 700; font-size: 1.1rem; }
.header-decor { position: absolute; right: 2rem; bottom: 0.5rem; font-size: 5rem; opacity: 0.1; }

.main-content {
  max-width: 1000px;
  margin: 2rem auto;
  padding: 0 2rem;
}

/* Status Tabs */
.status-tabs {
  background: white;
  padding: 0.75rem;
  border-radius: 20px;
  display: flex;
  justify-content: space-between;
  gap: 0.5rem;
  margin-bottom: 2.5rem;
  border: 3px solid #fde68a;
}

.tab-btn {
  flex: 1;
  padding: 1rem 0.5rem;
  border: none;
  background: none;
  border-radius: 14px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.2s;
}

.tab-icon { font-size: 1.5rem; }
.tab-label { font-size: 0.9rem; font-weight: 800; color: #92400e; }

.tab-btn:hover { background: #fffbeb; }
.tab-btn.active {
  background: #e11d48;
}
.tab-btn.active .tab-label { color: white; }
.tab-btn.active .tab-icon { filter: brightness(1.2); }

/* Orders Container */
.orders-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.order-card {
  background: white;
  border-radius: 24px;
  padding: 1.5rem 2rem;
  border: 3px solid #fde68a;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 1.5rem;
  border-bottom: 2px dashed #fde68a;
  margin-bottom: 1.5rem;
}

.order-meta { display: flex; flex-direction: column; gap: 0.25rem; }
.order-id { font-size: 1.1rem; font-weight: 900; color: #451a03; }
.order-date { font-size: 0.85rem; color: #92400e; font-weight: 700; }

.order-status-badge {
  padding: 0.4rem 1rem;
  border-radius: 99px;
  font-size: 0.85rem;
  font-weight: 900;
  color: white;
}

.order-status-badge.pending { background: #92400e; }
.order-status-badge.processing { background: #3b82f6; }
.order-status-badge.shipped { background: #8b5cf6; }
.order-status-badge.delivered { background: #10b981; }
.order-status-badge.cancelled { background: #ef4444; }

.cancel-btn {
  margin-left: 1rem;
  background: white;
  border: 1.5px solid #ef4444;
  color: #ef4444;
  padding: 0.3rem 0.8rem;
  border-radius: 99px;
  font-size: 0.8rem;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background: #ef4444;
  color: white;
}

.order-body {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.info-row {
  display: flex;
  gap: 1.5rem;
}

.label {
  min-width: 80px;
  font-size: 0.95rem;
  font-weight: 800;
  color: #d0885e;
}

.value {
  font-size: 0.95rem;
  font-weight: 700;
  color: #451a03;
}

.memo { color: #92400e; font-style: italic; }
.delivery-date { color: #e11d48; }

.order-footer {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 2px solid #fffbeb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-toggle-btn {
  background: #fffbeb;
  border: 1px solid #fde68a;
  color: #92400e;
  padding: 0.5rem 1rem;
  border-radius: 12px;
  font-weight: 800;
  cursor: pointer;
  font-size: 0.9rem;
}

.detail-toggle-btn:hover { background: #fde68a; }

.order-details-pane {
  margin-top: 1.5rem;
  background: #fffafa;
  border-radius: 16px;
  padding: 1rem;
  border: 1.5px solid #fde68a;
}

.items-list { display: flex; flex-direction: column; gap: 0.75rem; }
.order-item-row { display: flex; justify-content: space-between; align-items: center; padding-bottom: 0.5rem; border-bottom: 1px solid #fff1f2; }
.item-name { flex: 1; font-weight: 800; color: #451a03; }
.item-qty { margin: 0 1.5rem; color: #92400e; font-weight: 700; background: white; padding: 0.2rem 0.6rem; border-radius: 8px; font-size: 0.85rem; }
.item-price { font-weight: 900; color: #e11d48; }

.total-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.25rem;
}

.total-amount span { font-size: 0.9rem; font-weight: 700; color: #92400e; }
.total-amount strong { font-size: 1.5rem; font-weight: 950; color: #e11d48; }

.mini-loading { text-align: center; color: #92400e; font-weight: 700; padding: 1rem; }
.mini-spinner { animation: spin 1s infinite linear; display: inline-block; }
.no-items { text-align: center; padding: 1rem; color: #92400e; font-weight: 600; }

.empty-state {
  text-align: center;
  padding: 5rem;
  background: white;
  border-radius: 32px;
  border: 3px solid #fde68a;
}

.empty-icon { font-size: 4rem; opacity: 0.2; margin-bottom: 1rem; display: block; }
.empty-state p { font-size: 1.25rem; font-weight: 800; color: #92400e; }

.tteok-spinner { font-size: 3rem; animation: spin 1s infinite linear; text-align: center; margin-bottom: 1rem; }
@keyframes spin { from { transform: rotate(0); } to { transform: rotate(360deg); } }
.loading-state { text-align: center; padding: 5rem; }
</style>
