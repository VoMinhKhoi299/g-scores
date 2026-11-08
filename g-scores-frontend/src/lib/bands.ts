import type { ScoreBand } from '@/types/api'

/** Màu cho 4 band điểm, theo nghĩa đạt/khá/trung bình/yếu. */
export const BAND_COLOR: Record<ScoreBand, string> = {
  VERY_GOOD: 'var(--chart-bar)',
  GOOD: 'var(--chart-bar)',
  FAIR: 'var(--chart-bar)',
  POOR: 'var(--chart-bar)',
}

export const BAND_LABEL: Record<ScoreBand, string> = {
  VERY_GOOD: 'Tốt',
  GOOD: 'Khá',
  FAIR: 'Trung bình',
  POOR: 'Yếu',
}

/** Thứ tự hiển thị band, từ cao xuống thấp. */
export const BAND_ORDER: ScoreBand[] = ['VERY_GOOD', 'GOOD', 'FAIR', 'POOR']
