import { flushPromises } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import { useApi } from './useApi'

describe('useApi', () => {
  it('fetches data on initialization', async () => {
    const fetcher = vi.fn<() => Promise<string>>().mockResolvedValue('loaded')

    const state = useApi(fetcher)

    expect(fetcher).toHaveBeenCalledOnce()
    expect(state.loading).toBe(true)

    await flushPromises()

    expect(state.data).toBe('loaded')
    expect(state.loading).toBe(false)
  })
})
