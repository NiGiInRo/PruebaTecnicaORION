const ESTADO_OT_STYLE: Record<string, string> = {
  ABIERTA: 'badge-warning',
  ASIGNADA: 'badge-primary',
  EN_EJECUCION: 'badge-primary',
  CERRADA: 'badge-success',
  CANCELADA: 'badge-neutral',
}

const ESTADO_ACTIVO_STYLE: Record<string, string> = {
  OPERATIVO: 'badge-success',
  FUERA_DE_SERVICIO: 'badge-danger',
  EN_MANTENIMIENTO: 'badge-warning',
}

const ESTADO_CUADRILLA_STYLE: Record<string, string> = {
  DISPONIBLE: 'badge-success',
  EN_MISION: 'badge-warning',
  INACTIVA: 'badge-neutral',
}

const PRIORIDAD_STYLE: Record<string, string> = {
  BAJA: 'badge-neutral',
  MEDIA: 'badge-primary',
  ALTA: 'badge-warning',
  CRITICA: 'badge-danger',
}

function humanize(value: string): string {
  return value.replaceAll('_', ' ')
}

export function Badge({ value, styleMap }: { value: string; styleMap: Record<string, string> }) {
  const cls = styleMap[value] ?? 'badge-neutral'
  return <span className={`badge ${cls}`}>{humanize(value)}</span>
}

export const EstadoOtBadge = ({ value }: { value: string }) => (
  <Badge value={value} styleMap={ESTADO_OT_STYLE} />
)
export const EstadoActivoBadge = ({ value }: { value: string }) => (
  <Badge value={value} styleMap={ESTADO_ACTIVO_STYLE} />
)
export const EstadoCuadrillaBadge = ({ value }: { value: string }) => (
  <Badge value={value} styleMap={ESTADO_CUADRILLA_STYLE} />
)
export const PrioridadBadge = ({ value }: { value: string }) => (
  <Badge value={value} styleMap={PRIORIDAD_STYLE} />
)
