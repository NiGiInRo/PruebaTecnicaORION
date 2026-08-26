export type Rol = 'SUPERVISOR' | 'COORDINADOR' | 'TECNICO'

export interface UsuarioResumen {
  id: number
  nombre: string
  email: string
}

export interface LoginResponse {
  token: string
  nombre: string
  email: string
  rol: Rol
}

export type TipoActivo = 'PMV' | 'CCTV' | 'ESTACION_METEOROLOGICA' | 'SENSOR_TRAFICO' | 'AFORADOR'
export type EstadoActivo = 'OPERATIVO' | 'FUERA_DE_SERVICIO' | 'EN_MANTENIMIENTO'

export interface CorredorVial {
  id: number
  codigo: string
  nombre: string
  descripcion: string | null
}

export interface Activo {
  id: number
  codigo: string
  nombre: string
  tipo: TipoActivo
  corredor: CorredorVial
  pkKilometraje: number | null
  fabricante: string | null
  modelo: string | null
  fechaInstalacion: string | null
  estado: EstadoActivo
}

export type EspecialidadCuadrilla = 'ELECTRICA' | 'REDES_COMUNICACIONES' | 'MECANICA' | 'CIVIL' | 'SENALIZACION'
export type EstadoCuadrilla = 'DISPONIBLE' | 'EN_MISION' | 'INACTIVA'

export interface Cuadrilla {
  id: number
  codigo: string
  nombre: string
  especialidad: EspecialidadCuadrilla
  estado: EstadoCuadrilla
  lider: UsuarioResumen | null
  tecnicos: UsuarioResumen[]
}

export type TipoOrdenTrabajo = 'PREVENTIVO' | 'CORRECTIVO'
export type PrioridadOrdenTrabajo = 'BAJA' | 'MEDIA' | 'ALTA' | 'CRITICA'
export type EstadoOrdenTrabajo = 'ABIERTA' | 'ASIGNADA' | 'EN_EJECUCION' | 'CERRADA' | 'CANCELADA'
export type OrigenOrdenTrabajo = 'MANUAL' | 'AUTOMATICA'
export type RolCuadrillaEnOT = 'EJECUCION_TECNICA' | 'SENALIZACION' | 'APOYO_LOGISTICO'

export interface OrdenTrabajoCuadrillaAsignacion {
  id: number
  cuadrilla: Cuadrilla
  rol: RolCuadrillaEnOT
  fechaAsignacion: string
}

export interface OrdenTrabajo {
  id: number
  activo: Activo
  tipo: TipoOrdenTrabajo
  prioridad: PrioridadOrdenTrabajo
  estado: EstadoOrdenTrabajo
  descripcion: string | null
  fechaCreacion: string
  fechaProgramada: string | null
  fechaInicioEjecucion: string | null
  fechaCierre: string | null
  observacionesCierre: string | null
  origen: OrigenOrdenTrabajo
  creadoPor: UsuarioResumen | null
  cuadrillasAsignadas: OrdenTrabajoCuadrillaAsignacion[]
}

export interface DashboardIndicadores {
  otsPorEstado: Partial<Record<EstadoOrdenTrabajo, number>>
  otsPorTipo: Partial<Record<TipoOrdenTrabajo, number>>
  activosOperativos: number
  activosFueraDeServicio: number
  activosEnMantenimiento: number
  porcentajeDisponibilidadActivos: number
  otsPorCorredor: Record<string, number>
  cargaPorCuadrilla: Record<string, number>
  tiempoPromedioResolucionHoras: number | null
}

export interface ApiError {
  error: string
}
