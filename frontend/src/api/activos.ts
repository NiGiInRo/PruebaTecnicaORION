import { apiClient } from './client'
import type { Activo, CorredorVial, EstadoActivo, TipoActivo } from './types'

export interface ActivoInput {
  codigo: string
  nombre: string
  tipo: TipoActivo
  corredorId: number
  pkKilometraje?: number | null
  fabricante?: string | null
  modelo?: string | null
  fechaInstalacion?: string | null
}

export interface ActivoUpdateInput {
  nombre: string
  tipo: TipoActivo
  corredorId: number
  pkKilometraje?: number | null
  fabricante?: string | null
  modelo?: string | null
  fechaInstalacion?: string | null
}

export async function listActivos(filtros?: {
  corredorId?: number
  tipo?: TipoActivo
  estado?: EstadoActivo
}): Promise<Activo[]> {
  const { data } = await apiClient.get<Activo[]>('/activos', { params: filtros })
  return data
}

export async function getActivo(id: number): Promise<Activo> {
  const { data } = await apiClient.get<Activo>(`/activos/${id}`)
  return data
}

export async function createActivo(input: ActivoInput): Promise<Activo> {
  const { data } = await apiClient.post<Activo>('/activos', input)
  return data
}

export async function updateActivo(id: number, input: ActivoUpdateInput): Promise<Activo> {
  const { data } = await apiClient.put<Activo>(`/activos/${id}`, input)
  return data
}

export async function listCorredores(): Promise<CorredorVial[]> {
  const { data } = await apiClient.get<CorredorVial[]>('/corredores')
  return data
}

export async function createCorredor(input: {
  codigo: string
  nombre: string
  descripcion?: string
}): Promise<CorredorVial> {
  const { data } = await apiClient.post<CorredorVial>('/corredores', input)
  return data
}
