<script setup>
import { ref, defineProps, defineEmits } from 'vue';

const props = defineProps({
  isOpen: Boolean,
  prefilledProductName: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['close', 'confirm']);

const formData = ref({
  name: props.prefilledProductName || '',
  quantity: 1,
  expirationDate: '',
  productCode: ''
});

const handleSubmit = () => {
  emit('confirm', { ...formData.value });
};
</script>

<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>입고 등록</h2>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-body">
        <div class="form-group">
          <label>상품 이름</label>
          <input 
            type="text" 
            v-model="formData.name" 
            placeholder="상품 이름 입력"
            :disabled="!!prefilledProductName"
            required
          />
        </div>

        <div class="form-group">
          <label>수량</label>
          <input 
            type="number" 
            v-model="formData.quantity" 
            min="1" 
            required
          />
        </div>

        <div class="form-group">
          <label>유통기한</label>
          <input 
            type="date" 
            v-model="formData.expirationDate" 
            required
          />
        </div>

        <div class="form-group">
          <label>일련번호 (Lot Code)</label>
          <input 
            type="text" 
            v-model="formData.productCode" 
            placeholder="LOT-2024001"
            required
          />
        </div>

        <div class="modal-footer">
          <button type="submit" class="btn-spicy btn-confirm">입고 등록 완료</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(69, 26, 3, 0.4);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

.modal-content {
  background: white;
  width: 100%;
  max-width: 500px;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  border: 3px solid #fde68a;
  animation: pop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.modal-header {
  padding: 2rem 2rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px dashed #fde68a;
}

.modal-header h2 {
  font-size: 1.5rem;
  font-weight: 900;
  color: #451a03;
  margin: 0;
}

.close-btn {
  background: #fffbeb;
  border: none;
  font-size: 1.5rem;
  color: #92400e;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-body {
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.form-group label {
  font-size: 0.95rem;
  font-weight: 800;
  color: #92400e;
}

.form-group input {
  padding: 0.85rem 1.25rem;
  border-radius: 14px;
  border: 2px solid #fde68a;
  background: #fffbeb;
  font-size: 1rem;
  font-weight: 700;
  color: #451a03;
}

.modal-footer {
  padding: 0 2rem 2rem;
}

.btn-confirm {
  width: 100%;
  height: 56px;
  font-size: 1.1rem;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes pop {
  0% { transform: scale(0.9); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}
</style>
