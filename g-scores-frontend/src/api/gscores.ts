import { request } from '@/api/client'
import type {
  DashboardSummaryResponse,
  DistributionResponse,
  GroupSummaryResponse,
  StudentDetailResponse,
  TopGroupResponse,
} from '@/types/api'

export function getDashboardSummary(): Promise<DashboardSummaryResponse> {
  return request('/api/dashboard/summary')
}

/**
 * @param subjectCode ma mon (toan, ngu_van, ...). De trong lay tat ca mon.
 */
export function getDistribution(subjectCode?: string): Promise<DistributionResponse[]> {
  const query = subjectCode ? `?subjectCode=${encodeURIComponent(subjectCode)}` : ''
  return request(`/api/reports/distribution${query}`)
}

export function getGroups(): Promise<GroupSummaryResponse[]> {
  return request('/api/reports/groups')
}

export function getGroupTop(groupCode: string): Promise<TopGroupResponse> {
  return request(`/api/reports/groups/${encodeURIComponent(groupCode)}/top`)
}

export function getStudentScores(sbd: string): Promise<StudentDetailResponse> {
  return request(`/api/scores/${encodeURIComponent(sbd)}`)
}
