<script setup lang="ts">
import { getDashboardSummary, getDistribution } from '@/api/gscores'
import { ref, computed } from 'vue'
import { useApi } from '@/composables/useApi'
import { formatCount, formatTimestamp } from '@/lib/format'
import SubjectAverages from '@/components/SubjectAverages.vue'
import type { DistributionResponse } from '@/types/api'
import BandDistributionChart from '@/components/BandDistributionChart.vue'

const summary = useApi(getDashboardSummary)
const dists = useApi(() => getDistribution())

const totalStudents = computed(() => summary.data?.totalStudents ?? null)
const subjectCount = computed(() => summary.data?.subjectCount ?? null)
const seededAt = computed(() => summary.data?.seededAt ?? null)

function distsSafe(): DistributionResponse[] {
  const v = dists.data
  if (!v) return []
  return v
}

const overallAverage = computed(() => {
  const list = distsSafe()
  const totalScores = list.reduce((sum, item) => sum + item.totalStudents, 0)
  if (totalScores === 0) return null
  const weightedTotal = list.reduce(
    (sum, item) => sum + Number(item.averageScore) * item.totalStudents,
    0,
  )
  return weightedTotal / totalScores
})

const highScoreRate = computed(() => {
  const list = distsSafe()
  const totalScores = list.reduce((sum, item) => sum + item.totalStudents, 0)
  if (totalScores === 0) return null
  const highScores = list.reduce(
    (sum, item) =>
      sum + (item.bands.find((band) => band.band === 'VERY_GOOD')?.studentCount ?? 0),
    0,
  )
  return (highScores / totalScores) * 100
})

const selectedSubject = ref('')

const selected = computed((): DistributionResponse | null => {
  const list = distsSafe()
  return list.find((d) => d.subjectCode === selectedSubject.value) ?? list[0] ?? null
})

function selectSubject(code: string) {
  selectedSubject.value = code
}

function retryAll() {
  if (summary.error) summary.reload()
  if (dists.error) dists.reload()
}
</script>

<template>
  <div class="overview">
    <div class="page-heading">
      <h1 class="page-title">Tổng quan</h1>
      <p class="page-lede">
        Tổng hợp toàn bộ dữ liệu điểm thi THPT Quốc gia 2024: số học sinh, số môn
        thi và mức điểm của từng môn
      </p>
    </div>
    <hr class="page-divider" />

    <div v-if="summary.error || dists.error" class="banner" role="alert">
      <span>{{ summary.error ?? dists.error }}</span>
      <button class="banner__retry" type="button" @click="retryAll">Thử lại</button>
    </div>

    <div v-else-if="summary.loading || dists.loading" class="card" aria-live="polite">
      <div v-for="n in 3" :key="n" class="skeleton" style="height: 96px" />
      <p class="loading-message">Đang tải dữ liệu…</p>
    </div>

    <template v-else>
        <div class="stat-grid">
          <div class="stat">
            <p class="stat__label">Số học sinh</p>
            <p class="stat__value">{{ formatCount(totalStudents) }}</p>
          </div>
          <div class="stat">
            <p class="stat__label">Số môn thi</p>
            <p class="stat__value">{{ subjectCount }}</p>
          </div>
          <div class="stat">
            <p class="stat__label">Điểm trung bình chung</p>
            <p class="stat__value">
              {{ overallAverage?.toFixed(2) ?? '—' }}<span class="stat__unit">/ 10</span>
            </p>
          </div>
          <div class="stat">
            <p class="stat__label">Tỷ lệ lượt thi đạt từ 8 điểm</p>
            <p class="stat__value">
              {{ highScoreRate?.toFixed(1) ?? '—' }}<span class="stat__unit">%</span>
            </p>
          </div>
        </div>

        <div class="chart-row">
          <div class="card chart-row__item">
            <h2 class="card__title">Phân bố điểm theo môn</h2>
            <div class="subject-tabs">
              <button
                v-for="d in distsSafe()"
                :key="d.subjectCode"
                class="subject-tab"
                :class="{ 'subject-tab--active': selected?.subjectCode === d.subjectCode }"
                type="button"
                @click="selectSubject(d.subjectCode)"
              >
                {{ d.subjectName }}
              </button>
            </div>
            <div class="chart-row__body">
              <BandDistributionChart v-if="selected" :distribution="selected" />
              <p v-else class="setting-hint">Chưa có dữ liệu phân bố.</p>
            </div>
          </div>

          <div class="card chart-row__item">
            <h2 class="card__title">So sánh điểm trung bình theo môn</h2>
            <p class="card__subtitle">Thang điểm 10</p>
            <div class="chart-row__body">
              <SubjectAverages :distributions="distsSafe()" />
            </div>
          </div>
        </div>

        <div class="card">
          <h2 class="card__title">Thông tin nguồn dữ liệu</h2>
          <p class="setting-hint">
            Thời điểm nạp:
            <span class="num">{{ seededAt ? formatTimestamp(seededAt) : '—' }}</span>
          </p>
        </div>
    </template>
  </div>
</template>

<style scoped>
.chart-row {
  display: grid;
  grid-template-columns: minmax(0, 0.65fr) minmax(0, 1fr);
  gap: var(--space-4);
  margin: 16px 0;
}

.overview {
  margin-block: -17px;
}

.overview .page-heading {
  margin-bottom: 0;
}

.overview > .page-divider {
  margin: 8px 0 12px;
}

.overview .stat-grid {
  margin-bottom: 16px;
}

.overview .stat-grid :deep(.stat) {
  padding: 16px;
}

.page-heading {
  display: block;
}

.page-heading .page-lede {
  margin-top: var(--space-2);
}

.chart-row__item {
  display: flex;
  flex-direction: column;
  margin-top: 0;
  container-type: inline-size;
}

.card.chart-row__item {
  padding: 16px;
}

.overview > .card {
  padding: 12px 16px;
}

.chart-row__item .card__subtitle {
  text-align: center;
}

.chart-row__body {
  display: flex;
  flex: 1;
  flex-direction: column;
  margin-top: var(--space-3);
}

.chart-row__body :deep(.band-chart) {
  flex: 1;
  width: 100%;
}

.chart-row__body :deep(.subj) {
  flex: 1;
  align-content: space-between;
}

.loading-message {
  margin-top: var(--space-3);
  color: var(--text-2);
  font-size: var(--text-sm);
}

@media (max-width: 1100px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
}

.subject-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.subject-tab {
  border: 1px solid var(--border);
  background: var(--panel);
  color: var(--text);
  border-radius: var(--radius-sm);
  padding: var(--space-1) var(--space-3);
  font-size: var(--text-sm);
  cursor: pointer;
}

.subject-tab--active {
  background: var(--control-selected);
  border-color: var(--border-strong);
  color: var(--control-selected-text);
  font-weight: 500;
}

</style>
