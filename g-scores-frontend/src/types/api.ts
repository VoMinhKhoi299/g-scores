export type ScoreBand = 'VERY_GOOD' | 'GOOD' | 'FAIR' | 'POOR'

export interface StudentSubjectScore {
  subjectCode: string
  subjectName: string
  score: number
  band: ScoreBand
}

export interface StudentGroupSummary {
  groupCode: string
  groupName: string
  completed: boolean
  subjectCount: number
  total: number | null
  average: number | null
}

export interface StudentDetailResponse {
  sbd: string
  foreignLanguage: string | null
  subjectCount: number
  scores: StudentSubjectScore[]
  groups: StudentGroupSummary[]
}

export interface BandShare {
  band: ScoreBand
  label: string
  studentCount: number
  percentage: number
}

export interface DistributionResponse {
  subjectCode: string
  subjectName: string
  totalStudents: number
  averageScore: number
  bands: BandShare[]
}

export interface GroupSubjectInfo {
  subjectCode: string
  subjectName: string
}

export interface GroupSummaryResponse {
  groupCode: string
  name: string
  subjectCount: number
  subjects: GroupSubjectInfo[]
}

export interface TopStudentResponse {
  rank: number
  sbd: string
  total: number
  average: number
}

export interface TopGroupResponse {
  groupCode: string
  name: string
  subjectCount: number
  students: TopStudentResponse[]
}

export interface DashboardSummaryResponse {
  sourceFile: string
  totalStudents: number
  totalScores: number
  subjectCount: number
  groupCount: number
  seededAt: string
}

/** Dinh dang loi chung cua backend (ApiErrorResponse). */
export interface ApiErrorResponse {
  status: number
  error: string
  message: string
  timestamp: string
  fieldErrors: Record<string, string>
}
