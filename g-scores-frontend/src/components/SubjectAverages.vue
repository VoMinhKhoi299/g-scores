<script setup lang="ts">
import type { DistributionResponse } from '@/types/api'

/**
 * So sanh diem trung binh cua 9 mon: bang thanh ngang thuần CSS.
 * Diem luon trong [0, 10] nen thang doc co dinh = 10.0.
 */
const props = defineProps<{
  distributions: DistributionResponse[]
}>()

const rows = props.distributions.slice()

const MAX_SCALE = 10
</script>

<template>
  <div class="subj">
    <div
      v-for="d in rows"
      :key="d.subjectCode"
      class="subj__row"
    >
      <span class="subj__name">{{ d.subjectName }}</span>

      <div class="bar-wrapper">
        <div
          class="bar-fill"
          :style="{ width: `${(Number(d.averageScore) / MAX_SCALE) * 100}%` }"
        />
      </div>

      <span class="num subj__value">{{ Number(d.averageScore).toFixed(2) }}</span>
    </div>
  </div>
</template>

<style scoped>
.subj {
  display: grid;
}

.subj__row {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
}

.subj__row:last-child {
  margin-bottom: 0;
}

.subj__name {
  width: 80px;
  flex-shrink: 0;
  text-align: left;
  font-size: 14.5px;
  color: var(--text-2);
}

.bar-wrapper {
  flex: 1;
  height: 8px;
  background-color: var(--panel);
  border-radius: 9999px;
  overflow: hidden;
  margin: 0 12px;
}

.bar-fill {
  height: 100%;
  background-color: var(--chart-bar);
  border-radius: 9999px;
  transition: width 0.4s ease;
}

.subj__value {
  width: 40px;
  flex-shrink: 0;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}
</style>
