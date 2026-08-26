import { useEffect, useState } from 'react'
import { getIndicadores } from '../../api/dashboard'
import { getApiErrorMessage } from '../../api/client'
import type { DashboardIndicadores } from '../../api/types'

export function DashboardPage() {
  const [data, setData] = useState<DashboardIndicadores | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    getIndicadores()
      .then(setData)
      .catch((err) => setError(getApiErrorMessage(err)))
  }, [])

  if (error) return <div className="error-box">{error}</div>
  if (!data) return <p className="text-muted">Cargando…</p>

  const totalOts = Object.values(data.otsPorEstado).reduce((a, b) => a + (b ?? 0), 0)

  return (
    <div>
      <div className="page-header">
        <h1>Dashboard Operacional</h1>
      </div>

      <div className="stat-grid">
        <div className="stat-card">
          <div className="stat-label">Órdenes de trabajo</div>
          <div className="stat-value">{totalOts}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Disponibilidad de activos</div>
          <div className="stat-value">{data.porcentajeDisponibilidadActivos.toFixed(0)}%</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Activos fuera de servicio</div>
          <div className="stat-value">{data.activosFueraDeServicio}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">MTTR (horas)</div>
          <div className="stat-value">
            {data.tiempoPromedioResolucionHoras != null
              ? data.tiempoPromedioResolucionHoras.toFixed(1)
              : '—'}
          </div>
        </div>
      </div>

      <div className="grid-2">
        <div className="card">
          <h3>OTs por estado</h3>
          <SimpleBarList data={data.otsPorEstado} />
        </div>
        <div className="card">
          <h3>OTs por tipo</h3>
          <SimpleBarList data={data.otsPorTipo} />
        </div>
        <div className="card">
          <h3>OTs por corredor</h3>
          <SimpleBarList data={data.otsPorCorredor} />
        </div>
        <div className="card">
          <h3>Carga por cuadrilla (OTs activas)</h3>
          <SimpleBarList data={data.cargaPorCuadrilla} empty="Sin cuadrillas con carga activa" />
        </div>
      </div>
    </div>
  )
}

function SimpleBarList({
  data,
  empty = 'Sin datos',
}: {
  data: Record<string, number | undefined>
  empty?: string
}) {
  const entries = Object.entries(data).filter(([, v]) => v != null) as [string, number][]
  const max = Math.max(1, ...entries.map(([, v]) => v))

  if (entries.length === 0) {
    return <p className="text-muted">{empty}</p>
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 10, marginTop: 12 }}>
      {entries.map(([label, value]) => (
        <div key={label}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12.5, marginBottom: 4 }}>
            <span>{label.replaceAll('_', ' ')}</span>
            <strong>{value}</strong>
          </div>
          <div style={{ background: 'var(--surface-2)', borderRadius: 999, height: 6 }}>
            <div
              style={{
                width: `${(value / max) * 100}%`,
                background: 'var(--primary)',
                height: 6,
                borderRadius: 999,
              }}
            />
          </div>
        </div>
      ))}
    </div>
  )
}
