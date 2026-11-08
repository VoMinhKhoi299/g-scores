<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getStudentScores } from '@/api/gscores'
import { formatScore } from '@/lib/format'
import type { StudentDetailResponse } from '@/types/api'

// SBD phai dung 8 chu so (khop regex backend).
const sbd = ref('')
const searched = ref('')
const lastError = ref<string | null>(null)
// Phan biet reactive doi voi object thuong: object thuong gan
// vao bien ref thi phan 'detail.data = ...' bi mat voi Vue,
// UI khong re-render va de luon o skeleton.
const detail = reactive<{ data: StudentDetailResponse | null; loading: boolean; error: string | null }>({
  data: null,
  loading: false,
  error: null,
})

async function search() {
  const raw = sbd.value.trim()
  if (!/^\d{8}$/.test(raw)) {
    lastError.value = 'Số báo danh phải là đúng 8 chữ số.'
    return
  }
  searched.value = raw
  lastError.value = null
  detail.loading = true
  detail.data = null
  detail.error = null
  try {
    detail.data = await getStudentScores(raw)
  } catch (e) {
    detail.error = e instanceof Error ? e.message : 'Đã có lỗi xảy ra'
  } finally {
    detail.loading = false
  }
}
</script>

<template>
  <div>
    <h1 class="page-title">Tra cứu điểm</h1>
    <p class="page-lede">
      Nhập số báo danh (8 chữ số) để xem chi tiết điểm từng môn của một học sinh,
      kèm bảng tổng hợp điểm theo nhóm.
    </p>
    <hr class="page-divider" />

    <div class="card">
      <form class="search-form" @submit.prevent="search">
        <label for="sbd" class="search-label">Số báo danh</label>
        <div class="search-row">
          <input
            id="sbd"
            v-model="sbd"
            type="text"
            inputmode="numeric"
            maxlength="8"
            placeholder="VD: 01000001"
            class="search-input num"
          />
          <button class="btn btn--primary" type="submit" :disabled="detail.loading">
            {{ detail.loading ? 'Đang tìm…' : 'Tìm kiếm' }}
          </button>
        </div>
        <p v-if="lastError" class="search-error">{{ lastError }}</p>
      </form>
    </div>

    <template v-if="searched">
      <div v-if="detail.loading" class="card" aria-live="polite">
        <div v-for="n in 4" :key="n" class="skeleton" style="height: 72px" />
        <p class="loading-message">Đang tìm kết quả…</p>
      </div>

      <div v-else-if="detail.error" class="banner" role="alert">
        <span>{{ detail.error }}</span>
        <button class="banner__retry" type="button" @click="search">Thử lại</button>
      </div>

      <div v-else-if="detail.data">
        <div class="lookup-result">
          <section class="scores">
            <h2 class="scores__title">Điểm các môn đã thi</h2>
            <div class="score-list">
              <div v-for="s in detail.data.scores" :key="s.subjectCode" class="score-item">
                <strong class="score-item__name">{{ s.subjectName }}</strong>
                <span>:</span>
                <strong class="score-item__value">{{ formatScore(s.score) }}</strong>
              </div>
            </div>
          </section>
        </div>

        <div class="card">
          <h2 class="card__title">Tổng hợp theo nhóm môn</h2>
          <div class="group-cards">
            <div v-for="g in detail.data.groups" :key="g.groupCode" class="group-card">
              <div class="group-card__head">
                <span class="group-card__name">{{ g.groupName }}</span>
                <span
                  class="group-card__status"
                  :class="{ 'group-card__status--ok': g.completed }"
                >
                  {{ g.completed ? 'Đủ môn' : 'Thiếu môn' }}
                </span>
              </div>
              <div class="group-card__total">
                <span v-if="g.completed" class="num group-card__total-num">
                  {{ formatScore(g.total) }}
                </span>
                <span v-else class="num">—</span>
                <span class="group-card__total-label">/ {{ g.subjectCount * 10 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.page-lede {
  max-width: none;
}

.loading-message {
  color: var(--text-3);
  font-size: var(--text-sm);
  margin-top: var(--space-3);
}
.search-form {
  display: grid;
  gap: var(--space-2);
}

.search-label {
  font-size: var(--text-sm);
  color: var(--text-2);
}

.search-row {
  display: flex;
  gap: var(--space-3);
  align-items: center;
}

.search-input {
  flex: 1;
  border: 1px solid var(--border);
  background: var(--control-selected);
  border-radius: var(--radius-sm);
  padding: var(--space-3) var(--space-4);
  font-size: var(--text-base);
  color: var(--text);
}

.search-input:focus {
  border-color: var(--accent);
  outline: none;
}

.search-error {
  color: var(--danger);
  font-size: var(--text-sm);
}

.lookup-result {
  margin: 24px 0;
}

.scores__title {
  margin: 0 0 var(--space-3);
  font-size: var(--text-base);
  font-weight: 400;
}

.scores {
  padding: 0 var(--space-3);
}

.score-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3) var(--space-5);
}

.score-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.score-item__name {
  font-size: var(--text-lg);
  color: var(--text);
}

.score-item__value {
  font-size: var(--text-lg);
}

.group-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--space-4);
}

.group-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  background: var(--panel-2);
}

.group-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.group-card__name {
  font-weight: 600;
  font-size: var(--text-base);
}

.group-card__status {
  font-size: var(--text-xs);
  color: var(--text-3);
}

.group-card__status--ok {
  color: var(--ok);
}

.group-card__total {
  display: flex;
  align-items: baseline;
  gap: var(--space-2);
}

.group-card__total-num {
  font-size: var(--text-2xl);
  font-weight: 600;
}

.group-card__total-label {
  font-size: var(--text-sm);
  color: var(--text-3);
}
</style>
