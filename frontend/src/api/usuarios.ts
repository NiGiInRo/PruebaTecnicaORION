import { apiClient } from './client'
import type { Rol, UsuarioResumen } from './types'

export async function listUsuarios(rol?: Rol): Promise<UsuarioResumen[]> {
  const { data } = await apiClient.get<UsuarioResumen[]>('/usuarios', { params: { rol } })
  return data
}
