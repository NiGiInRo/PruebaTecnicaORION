import { apiClient } from './client'
import type {
  EstadoOrdenTrabajo,
  OrdenTrabajo,
  PrioridadOrdenTrabajo,
  RolCuadrillaEnOT,
  TipoOrdenTrabajo,
} from './types'

export interface OrdenTrabajoInput {
  activoId: number
  tipo: TipoOrdenTrabajo
  prioridad: PrioridadOrdenTrabajo
  descripcion?: string
  fechaProgramada?: string | null
}

export async function listOrdenesTrabajo(filtros?: {
  activoId?: number
  estado?: EstadoOrdenTrabajo
  tipo?: TipoOrdenTrabajo
  prioridad?: PrioridadOrdenTrabajo
}): Promise<OrdenTrabajo[]> {
  const { data } = await apiClient.get<OrdenTrabajo[]>('/ordenes-trabajo', { params: filtros })
  return data
}

export async function getOrdenTrabajo(id: number): Promise<OrdenTrabajo> {
  const { data } = await apiClient.get<OrdenTrabajo>(`/ordenes-trabajo/${id}`)
  return data
}

export async function createOrdenTrabajo(input: OrdenTrabajoInput): Promise<OrdenTrabajo> {
  const { data } = await apiClient.post<OrdenTrabajo>('/ordenes-trabajo', input)
  return data
}

export async function asignarCuadrillas(
  otId: number,
  asignaciones: { cuadrillaId: number; rol: RolCuadrillaEnOT }[],
): Promise<OrdenTrabajo> {
  const { data } = await apiClient.post<OrdenTrabajo>(`/ordenes-trabajo/${otId}/asignar-cuadrillas`, {
    asignaciones,
  })
  return data
}

export async function iniciarEjecucion(otId: number): Promise<OrdenTrabajo> {
  const { data } = await apiClient.post<OrdenTrabajo>(`/ordenes-trabajo/${otId}/iniciar-ejecucion`)
  return data
}

export async function cerrarOrdenTrabajo(otId: number, observaciones: string): Promise<OrdenTrabajo> {
  const { data } = await apiClient.post<OrdenTrabajo>(`/ordenes-trabajo/${otId}/cerrar`, { observaciones })
  return data
}

export async function cancelarOrdenTrabajo(otId: number, motivo: string): Promise<OrdenTrabajo> {
  const { data } = await apiClient.post<OrdenTrabajo>(`/ordenes-trabajo/${otId}/cancelar`, { motivo })
  return data
}
