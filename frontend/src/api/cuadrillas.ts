import { apiClient } from './client'
import type { Cuadrilla, EspecialidadCuadrilla } from './types'

export async function listCuadrillas(): Promise<Cuadrilla[]> {
  const { data } = await apiClient.get<Cuadrilla[]>('/cuadrillas')
  return data
}

export async function getCuadrilla(id: number): Promise<Cuadrilla> {
  const { data } = await apiClient.get<Cuadrilla>(`/cuadrillas/${id}`)
  return data
}

export async function createCuadrilla(input: {
  codigo: string
  nombre: string
  especialidad: EspecialidadCuadrilla
}): Promise<Cuadrilla> {
  const { data } = await apiClient.post<Cuadrilla>('/cuadrillas', input)
  return data
}

export async function agregarTecnico(cuadrillaId: number, usuarioId: number): Promise<Cuadrilla> {
  const { data } = await apiClient.post<Cuadrilla>(`/cuadrillas/${cuadrillaId}/tecnicos`, { usuarioId })
  return data
}

export async function quitarTecnico(cuadrillaId: number, usuarioId: number): Promise<Cuadrilla> {
  const { data } = await apiClient.delete<Cuadrilla>(`/cuadrillas/${cuadrillaId}/tecnicos/${usuarioId}`)
  return data
}

export async function designarLider(cuadrillaId: number, usuarioId: number): Promise<Cuadrilla> {
  const { data } = await apiClient.post<Cuadrilla>(`/cuadrillas/${cuadrillaId}/lider`, { usuarioId })
  return data
}

export async function activarCuadrilla(cuadrillaId: number): Promise<Cuadrilla> {
  const { data } = await apiClient.post<Cuadrilla>(`/cuadrillas/${cuadrillaId}/activar`)
  return data
}

export async function desactivarCuadrilla(cuadrillaId: number): Promise<Cuadrilla> {
  const { data } = await apiClient.post<Cuadrilla>(`/cuadrillas/${cuadrillaId}/desactivar`)
  return data
}
