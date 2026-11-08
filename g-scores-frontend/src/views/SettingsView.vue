<script setup lang="ts">
import { getDashboardSummary } from '@/api/gscores'
import { useTheme } from '@/composables/useTheme'
import { useApi } from '@/composables/useApi'
import { formatTimestamp, formatCount } from '@/lib/format'
import { computed } from 'vue'

const { theme, toggle } = useTheme()
const isDark = computed(() => theme.value === 'dark')

const source = useApi(getDashboardSummary)
</script>

<template>
  <div>
    <h1 class="page-title">Cài đặt</h1>
    <hr class="page-divider" />

    <section class="settings-section">
      <h2 class="settings-section__title">Giao diện</h2>
      <div class="setting-row">
        <div>
          <p class="setting-label">Chủ đề</p>
          <p class="setting-hint">
            Hiện đang: <strong>{{ isDark ? 'Tối' : 'Sáng' }}</strong>
          </p>
        </div>
        <button class="setting-btn" type="button" @click="toggle">
          {{ isDark ? 'Đổi sang sáng' : 'Đổi sang tối' }}
        </button>
      </div>
    </section>

    <section class="settings-section">
      <h2 class="settings-section__title">Nguồn dữ liệu</h2>

      <p v-if="source.loading && !source.data" class="setting-hint">Đang tải…</p>

      <template v-if="source.data">
        <div class="setting-row">
          <span class="setting-label">Tệp CSV</span>
          <span>{{ source.data.sourceFile }}</span>
        </div>
        <div class="setting-row">
          <span class="setting-label">Thời gian nạp</span>
          <span>{{ formatTimestamp(source.data.seededAt) }}</span>
        </div>
        <div class="setting-row">
          <span class="setting-label">Tổng số điểm</span>
          <span>{{ formatCount(source.data.totalScores) }}</span>
        </div>
      </template>

      <div v-if="source.error" class="setting-row">
        <span class="setting-hint" style="color: var(--danger)">{{ source.error }}</span>
        <button class="setting-btn" type="button" @click="source.reload">Thử lại</button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.settings-section {
  margin-top: var(--space-5);
}

.settings-section + .settings-section {
  border-top: 1px solid var(--border);
  padding-top: var(--space-5);
}

.settings-section__title {
  font-size: var(--text-lg);
  font-weight: 600;
  margin: 0 0 var(--space-3);
}

.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  padding: var(--space-3) 0;
  border-top: 1px solid var(--border);
}

.setting-row:first-of-type {
  border-top: 0;
}

.setting-label {
  color: var(--text-2);
  font-size: var(--text-sm);
}

.setting-hint {
  color: var(--text-2);
  font-size: var(--text-sm);
}

.setting-btn {
  border: 1px solid var(--border-strong);
  background: var(--panel);
  border-radius: var(--radius-sm);
  padding: var(--space-2) var(--space-3);
  font-size: var(--text-sm);
  color: var(--text);
  cursor: pointer;
}

.setting-btn:hover {
  border-color: var(--text-3);
}

</style>
