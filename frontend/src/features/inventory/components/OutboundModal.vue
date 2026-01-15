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
  monthsUntilExpiration: 3
});

const handleSubmit = () => {
  emit('confirm', { ...formData.value });
};
</script>

<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>출고 처리</h2>
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
          <label>출고 수량</label>
          <input 
            type="number" 
            v-model="formData.quantity" 
            min="1" 
            required
          />
        </div>

        <div class="form-group">
          <label>최소 잔여 유통기한 (개월)</label>
          <div class="input-hint">입력한 개월 수 이상 남은 재고에서 차감됩니다.</div>
          <input 
            type="number" 
            v-model="formData.monthsUntilExpiration" 
            min="1" 
            required
          />
        </div>

        <div class="alert-box">
           ⚠️ 유통기한이 임박한 상품부터 선입선출(FIFO) 원칙에 따라 출고됩니다.
        </div>

        <div class="modal-footer">
          <button type="submit" class="btn-confirm">출고 처리 완료</button>
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
  background: linear-gradient(135deg, #f97316, #ea580c);
  box-shadow: 0 4px 0px #9a3412;
  border: none;
  border-radius: 14px;
  color: white;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-confirm:active {
  box-shadow: 0 2px 0px #9a3412;
  transform: translateY(2px);
}

.input-hint {
  font-size: 0.8rem;
  color: #92400e;
  margin-bottom: 0.5rem;
}

.alert-box {
  background-color: #fff1f2;
  border: 1px solid #ffe4e6;
  color: #be123c;
  padding: 1rem;
  border-radius: 10px;
  font-size: 0.9rem;
  margin-bottom: 2rem;
  line-height: 1.5;
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
