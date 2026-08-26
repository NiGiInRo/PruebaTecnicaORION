import { apiClient } from './client'
import type { Material, MovimientoInventario } from './types'

export async function listMateriales(): Promise<Material[]> {
  const { data } = await apiClient.get<Material[]>('/materiales')
  return data
}

export async function getMaterial(id: number): Promise<Material> {
  const { data } = await apiClient.get<Material>(`/materiales/${id}`)
  return data
}

export async function createMaterial(input: {
  codigo: string
  nombre: string
  unidadMedida: string
  stockMinimo: number
}): Promise<Material> {
  const { data } = await apiClient.post<Material>('/materiales', input)
  return data
}

export async function listMovimientos(materialId: number): Promise<MovimientoInventario[]> {
  const { data } = await apiClient.get<MovimientoInventario[]>(`/materiales/${materialId}/movimientos`)
  return data
}

export async function registrarEntrada(materialId: number, cantidad: number): Promise<MovimientoInventario> {
  const { data } = await apiClient.post<MovimientoInventario>(`/materiales/${materialId}/entradas`, { cantidad })
  return data
}

export async function registrarConsumo(
  materialId: number,
  ordenTrabajoId: number,
  cantidad: number,
): Promise<MovimientoInventario> {
  const { data } = await apiClient.post<MovimientoInventario>(`/materiales/${materialId}/consumos`, {
    ordenTrabajoId,
    cantidad,
  })
  return data
}
