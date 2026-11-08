import { ref, shallowRef, reactive } from 'vue'

export interface ApiState<T> {
  data: T | null
  loading: boolean
  error: string | null
  reload: () => Promise<void>
}

/**
 * Lấy dữ liệu API kèm trạng thái loading/error, dùng chung cho các view.
 *
 * `shallowRef` cho `data`: dữ liệu API chỉ đọc, không cần phản ứng sâu,
 * và không phải đi qua UnwrapRef nên kiểu giữ nguyên là T.
 * `e instanceof Error` giữ được message do ApiError mang từ backend.
 */
export function useApi<T>(fetcher: () => Promise<T>): ApiState<T> {
  const data = shallowRef<T | null>(null)
  const loading = ref(true)
  const error = ref<string | null>(null)

  async function reload() {
    loading.value = true
    error.value = null
    try {
      data.value = await fetcher()
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Đã có lỗi xảy ra'
    } finally {
      loading.value = false
    }
  }

  // Keep the refs inside a reactive object so views stay updated on access.
  const state = reactive({ data, loading, error, reload })
  void reload()
  return state as unknown as ApiState<T>
}
