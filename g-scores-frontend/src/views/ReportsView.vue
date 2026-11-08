<script setup lang="ts">
import { reactive, onMounted, watch } from 'vue'
import { getGroups, getGroupTop } from '@/api/gscores'
import { useApi } from '@/composables/useApi'
import { formatScore } from '@/lib/format'
import type { TopGroupResponse, GroupSummaryResponse } from '@/types/api'

const groups = useApi(getGroups)

function retryAll() {
  if (groups.error) groups.reload()
}

// Mo tung nhom: state loading/data/loi rieng, dung reactive de
// mutate tuong bien (tops[code].data = ...) van trigger re-render.
interface TopState {
  data: TopGroupResponse | null
  loading: boolean
  error: string | null
}
const tops = reactive<Record<string, TopState>>({})

async function loadAllTops() {
  const list = groupsSafe()
  if (list.length === 0) return
  await Promise.all(
    list.map(async (g) => {
      const code = g.groupCode
      tops[code] = { data: null, loading: true, error: null }
      try {
        tops[code]!.data = await getGroupTop(code)
      } catch (e) {
        tops[code]!.error = e instanceof Error ? e.message : 'Đã có lỗi'
      } finally {
        tops[code]!.loading = false
      }
    }),
  )
}

onMounted(loadAllTops)
watch(
  () => groups.data,
  (v) => {
    if (v && v.length > 0) loadAllTops()
  },
)

function groupsSafe(): GroupSummaryResponse[] {
  return groups.data ?? []
}
</script>

<template>
  <div>
    <h1 class="page-title">Báo cáo</h1>
    <p class="page-lede">
      Bảng top 10 học sinh theo từng nhóm môn (A, B, C).
    </p>
    <hr class="page-divider" />

    <div v-if="groups.loading" class="card" aria-live="polite">
      <div v-for="n in 2" :key="n" class="skeleton" style="height: 72px" />
      <p class="loading-message">Đang tải báo cáo…</p>
    </div>

    <div v-else-if="groups.error" class="banner" role="alert">
      <span>{{ groups.error }}</span>
      <button class="banner__retry" type="button" @click="retryAll">Thử lại</button>
    </div>

    <template v-else-if="groups.data">
        <section class="reports-content">
          <h2 class="reports-content__title">Top 10 học sinh theo nhóm</h2>
          <div class="top-grid">
            <div
              v-for="g in groupsSafe()"
              :key="g.groupCode"
              class="top-col"
            >
              <div class="top-col__head">
                <span class="top-col__name">{{ g.name }}:</span>
                <span class="top-col__subs">
                  {{ g.subjects.map((s) => s.subjectName).join(', ') }}
                </span>
              </div>

              <div v-if="tops[g.groupCode]?.loading" class="skeleton" style="height: 220px" />

              <div v-else-if="tops[g.groupCode]?.error" class="top-col__error">
                {{ tops[g.groupCode]?.error }}
              </div>

              <table v-else-if="tops[g.groupCode]?.data" class="top-table">
                <thead>
                  <tr>
                    <th>Hạng</th>
                    <th class="num">SBD</th>
                    <th class="num" style="text-align: right">Tổng</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="s in tops[g.groupCode]?.data?.students ?? []" :key="s.sbd">
                    <td class="num">{{ s.rank }}</td>
                    <td class="num">{{ s.sbd }}</td>
                    <td class="num top-table__score">{{ formatScore(s.total) }}</td>
                  </tr>
                </tbody>
              </table>

              <p v-else class="setting-hint">Chưa có dữ liệu của nhóm này.</p>
            </div>
          </div>
        </section>
    </template>

    <p v-else class="setting-hint">Chưa có dữ liệu báo cáo.</p>
  </div>
</template>

<style scoped>
.top-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-4);
}

.reports-content {
  margin-top: var(--space-5);
}

.reports-content__title {
  margin-bottom: var(--space-4);
  text-align: center;
  font-size: var(--text-lg);
  font-weight: 600;
}

.loading-message {
  margin-top: var(--space-3);
  color: var(--text-2);
  font-size: var(--text-sm);
}

.top-col {
  border: 1px solid color-mix(in srgb, #000 18%, var(--border));
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  background: var(--panel);
}

.top-col__head {
  display: flex;
  align-items: baseline;
  flex-wrap: wrap;
  gap: var(--space-2) var(--space-3);
  margin-bottom: var(--space-3);
}

.top-col__name {
  font-weight: 600;
}

.top-col__subs {
  color: var(--text);
  font-size: var(--text-sm);
  font-weight: 600;
}

.top-col__error {
  color: var(--danger);
  font-size: var(--text-sm);
}

.top-table {
  width: 100%;
  border-collapse: collapse;
  font-size: var(--text-sm);
}

.top-table th {
  color: var(--text);
  font-weight: 700;
  text-align: left;
  padding: var(--space-1) var(--space-2);
  border-bottom: 1px solid var(--border);
}

.top-table td {
  padding: var(--space-1) var(--space-2);
  border-bottom: 1px solid var(--border);
}

.top-table__score {
  font-weight: 700;
  text-align: right;
}

.top-table tr:last-child td {
  border-bottom: 0;
}

</style>
