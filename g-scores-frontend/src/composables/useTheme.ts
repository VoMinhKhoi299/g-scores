import { ref, watch } from 'vue'
import type { Ref } from 'vue'

type Theme = 'light' | 'dark'

const STORAGE_KEY = 'g-scores-theme'

function initialTheme(): Theme {
  const saved = localStorage.getItem(STORAGE_KEY)
  if (saved === 'light' || saved === 'dark') return saved
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

// Một ref duy nhất cho cả app (module-level), tránh mỗi component giữ bản riêng.
const theme: Ref<Theme> = ref(initialTheme())

watch(
  theme,
  (value) => {
    document.documentElement.dataset.theme = value
    localStorage.setItem(STORAGE_KEY, value)
  },
  { immediate: true },
)

export function useTheme() {
  function toggle() {
    theme.value = theme.value === 'dark' ? 'light' : 'dark'
  }
  return { theme, toggle }
}
