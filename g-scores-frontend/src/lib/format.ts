/** Điểm: 1 chữ số thập phân (8.0 thay vì 8.00), bỏ số 0 vô nghĩa. */
export function formatScore(value: number | null | undefined): string {
  if (value == null) return '—'
  return Number.isInteger(value) ? value.toFixed(1) : String(value)
}

/** Số lượng kiểu Việt Nam: 1.045.613 */
export function formatCount(value: number | null | undefined): string {
  return value == null ? '—' : value.toLocaleString('vi-VN')
}

export function formatPercent(value: number | null | undefined): string {
  return value == null ? '—' : `${value.toFixed(2)}%`
}

export function formatTimestamp(iso: string | null | undefined): string {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
