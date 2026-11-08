import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import BandDistributionChart from './BandDistributionChart.vue'
import type { DistributionResponse } from '@/types/api'

describe('BandDistributionChart', () => {
  it('shows accented labels in score order with vertical bars', () => {
    const distribution: DistributionResponse = {
      subjectCode: 'test',
      subjectName: 'Test',
      totalStudents: 100,
      averageScore: 5,
      bands: [
        { band: 'POOR', label: 'Yeu', studentCount: 40, percentage: 40 },
        { band: 'FAIR', label: 'Trung binh', studentCount: 30, percentage: 30 },
        { band: 'GOOD', label: 'Tot', studentCount: 20, percentage: 20 },
        { band: 'VERY_GOOD', label: 'Tuong doi tot', studentCount: 10, percentage: 10 },
      ],
    }

    const wrapper = mount(BandDistributionChart, { props: { distribution } })
    expect(wrapper.findAll('.band-chart__bar')).toHaveLength(4)
    expect(wrapper.findAll('.band-chart__label').map((label) => label.text())).toEqual([
      'Tốt',
      'Khá',
      'Trung bình',
      'Yếu',
    ])
    const bars = wrapper.findAll('.band-chart__bar')
    expect(bars.at(0)?.attributes('style')).toContain('height: 25%')
    expect(bars.at(-1)?.attributes('style')).toContain('height: 100%')
    expect(wrapper.text()).toContain('10.0%')
  })
})
