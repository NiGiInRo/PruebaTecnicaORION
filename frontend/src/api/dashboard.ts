import { apiClient } from './client'
import type { DashboardIndicadores } from './types'

export async function getIndicadores(): Promise<DashboardIndicadores> {
  const { data } = await apiClient.get<DashboardIndicadores>('/dashboard/indicadores')
  return data
}
