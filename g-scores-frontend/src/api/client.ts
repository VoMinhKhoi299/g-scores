import type { ApiErrorResponse } from '@/types/api'

/**
 * Loi tra ve tu API, giu nguyen thong diep cua backend de hien thi.
 */
export class ApiError extends Error {
  readonly status: number
  readonly fieldErrors: Record<string, string>

  constructor(status: number, message: string, fieldErrors: Record<string, string> = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''
// Dataset lớn có thể mất thời gian nạp khi backend vừa khởi động.
const TIMEOUT_MS = 90_000

export async function request<T>(path: string): Promise<T> {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), TIMEOUT_MS)
  let response: Response
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      headers: { Accept: 'application/json' },
      signal: controller.signal,
    })
  } catch {
    if (controller.signal.aborted) {
      throw new ApiError(0, 'Yêu cầu mất quá nhiều thời gian. Vui lòng thử lại.')
    }
    throw new ApiError(0, 'Không kết nối được tới máy chủ. Kiểm tra backend có đang chạy không?')
  } finally {
    clearTimeout(timer)
  }

  if (!response.ok) {
    throw new ApiError(response.status, await readErrorMessage(response))
  }

  return (await response.json()) as T
}

/**
 * Doc message tu response loi cua backend. Neu body khong phai JSON
 * (vi du backend chua chay) thi dung status text lam fallback.
 */
async function readErrorMessage(response: Response): Promise<string> {
  try {
    const body = (await response.json()) as Partial<ApiErrorResponse>
    if (body.message) return body.message
  } catch {
    // Response khong phai JSON — bo qua, dung fallback ben duoi.
  }
  return response.statusText || `Lỗi ${response.status}`
}
