<script setup lang="ts">
import { computed } from 'vue'
import type { DistributionResponse, ScoreBand } from '@/types/api'
import { BAND_COLOR, BAND_LABEL, BAND_ORDER } from '@/lib/bands'

const props = defineProps<{ distribution: DistributionResponse }>()

const bands = computed(() =>
  BAND_ORDER.map((band) => {
    const share = props.distribution.bands.find((item) => item.band === band)
    return {
      band,
      label: BAND_LABEL[band],
      studentCount: share?.studentCount ?? 0,
      percentage: Number(share?.percentage ?? 0),
    }
  }),
)

const maxPercentage = computed(() =>
  Math.max(...bands.value.map((band) => band.percentage), 0),
)

function barStyle(band: ScoreBand, percentage: number) {
  return {
    height: `${maxPercentage.value ? (percentage / maxPercentage.value) * 100 : 0}%`,
    background: BAND_COLOR[band],
  }
}
</script>

<template>
  <div class="band-chart" role="img" :aria-label="`Phân bố điểm môn ${distribution.subjectName}`">
    <div v-for="item in bands" :key="item.band" class="band-chart__item">
      <span class="band-chart__percentage">{{ item.percentage.toFixed(1) }}%</span>
      <div class="band-chart__track">
        <div class="band-chart__bar" :style="barStyle(item.band, item.percentage)" />
      </div>
      <span class="band-chart__label">{{ item.label }}</span>
      <span class="band-chart__count num">{{ item.studentCount.toLocaleString('vi-VN') }}</span>
    </div>
  </div>
</template>

<style scoped>
.band-chart {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  align-items: end;
  gap: var(--space-3);
  min-height: 250px;
}

.band-chart__item {
  display: grid;
  grid-template-rows: auto minmax(100px, 1fr) auto auto;
  justify-items: center;
  gap: var(--space-2);
  height: 100%;
  min-width: 0;
  text-align: center;
}

.band-chart__percentage {
  font-size: var(--text-sm);
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

.band-chart__track {
  display: flex;
  align-items: end;
  justify-content: center;
  width: min(100%, 64px);
  height: 100%;
  overflow: hidden;
  border-radius: 6px 6px 0 0;
  background: var(--panel);
}

.band-chart__bar {
  width: 100%;
  min-height: 2px;
  border-radius: 6px 6px 0 0;
}

.band-chart__label {
  color: var(--text-2);
  font-size: var(--text-sm);
  line-height: 1.3;
}

.band-chart__count {
  color: var(--text-2);
  font-size: var(--text-xs);
}

@container (max-width: 420px) {
  .band-chart {
    gap: var(--space-2);
    min-height: 200px;
  }

  .band-chart__label {
    font-size: 11px;
  }

  .band-chart__count {
    font-size: 11px;
  }
}
</style>
