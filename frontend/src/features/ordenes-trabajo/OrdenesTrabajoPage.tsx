import { useEffect, useState } from 'react'
import { useAuth } from '../../auth/AuthContext'
import { getApiErrorMessage } from '../../api/client'
import { listActivos } from '../../api/activos'
import { listCuadrillas } from '../../api/cuadrillas'
import {
  asignarCuadrillas,
  cancelarOrdenTrabajo,
  cerrarOrdenTrabajo,
  createOrdenTrabajo,
  iniciarEjecucion,
  listOrdenesTrabajo,
} from '../../api/ordenesTrabajo'
import type {
  Activo,
  Cuadrilla,
  EstadoOrdenTrabajo,
  OrdenTrabajo,
  PrioridadOrdenTrabajo,
  RolCuadrillaEnOT,
  TipoOrdenTrabajo,
} from '../../api/types'
import { EstadoOtBadge, PrioridadBadge } from '../../components/Badge'

const ESTADOS: EstadoOrdenTrabajo[] = ['ABIERTA', 'ASIGNADA', 'EN_EJECUCION', 'CERRADA', 'CANCELADA']
const TIPOS: TipoOrdenTrabajo[] = ['PREVENTIVO', 'CORRECTIVO']
const PRIORIDADES: PrioridadOrdenTrabajo[] = ['BAJA', 'MEDIA', 'ALTA', 'CRITICA']
const ROLES_CUADRILLA: RolCuadrillaEnOT[] = ['EJECUCION_TECNICA', 'SENALIZACION', 'APOYO_LOGISTICO']

export function OrdenesTrabajoPage() {
  const { user } = useAuth()
  const [ordenes, setOrdenes] = useState<OrdenTrabajo[]>([])
  const [activos, setActivos] = useState<Activo[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [filtroEstado, setFiltroEstado] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [detalleId, setDetalleId] = useState<number | null>(null)

  async function load() {
    setLoading(true)
    setError(null)
    try {
      const [ots, acts] = await Promise.all([
        listOrdenesTrabajo({ estado: (filtroEstado || undefined) as EstadoOrdenTrabajo | undefined }),
        listActivos(),
      ])
      setOrdenes(ots)
      setActivos(acts)
    } catch (err) {
      setError(getApiErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filtroEstado])

  const detalle = ordenes.find((o) => o.id === detalleId) ?? null

  return (
    <div>
      <div className="page-header">
        <h1>Órdenes de Trabajo</h1>
        {user?.rol === 'SUPERVISOR' && (
          <button className="btn btn-primary" onClick={() => setShowForm(true)}>
            + Nueva OT
          </button>
        )}
      </div>

      {error && <div className="error-box">{error}</div>}

      <div className="card" style={{ marginBottom: 16 }}>
        <select value={filtroEstado} onChange={(e) => setFiltroEstado(e.target.value)}>
          <option value="">Todos los estados</option>
          {ESTADOS.map((e) => (
            <option key={e} value={e}>
              {e}
            </option>
          ))}
        </select>
      </div>

      <div className="card">
        {loading ? (
          <p className="text-muted">Cargando…</p>
        ) : ordenes.length === 0 ? (
          <div className="empty-state">No hay órdenes de trabajo con este filtro.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Activo</th>
                <th>Tipo</th>
                <th>Prioridad</th>
                <th>Estado</th>
                <th>Creada</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {ordenes.map((o) => (
                <tr key={o.id}>
                  <td>{o.id}</td>
                  <td>
                    {o.activo.codigo} — {o.activo.nombre}
                  </td>
                  <td>{o.tipo}</td>
                  <td>
                    <PrioridadBadge value={o.prioridad} />
                  </td>
                  <td>
                    <EstadoOtBadge value={o.estado} />
                  </td>
                  <td className="text-muted">{new Date(o.fechaCreacion).toLocaleString()}</td>
                  <td>
                    <button className="btn btn-sm" onClick={() => setDetalleId(o.id)}>
                      Ver
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showForm && (
        <CrearOtModal
          activos={activos}
          onClose={() => setShowForm(false)}
          onSaved={() => {
            setShowForm(false)
            load()
          }}
        />
      )}

      {detalle && (
        <OtDetalleModal
          ot={detalle}
          rol={user?.rol}
          onClose={() => setDetalleId(null)}
          onChanged={load}
        />
      )}
    </div>
  )
}

function CrearOtModal({
  activos,
  onClose,
  onSaved,
}: {
  activos: Activo[]
  onClose: () => void
  onSaved: () => void
}) {
  const [activoId, setActivoId] = useState<string>(String(activos[0]?.id ?? ''))
  const [tipo, setTipo] = useState<TipoOrdenTrabajo>('CORRECTIVO')
  const [prioridad, setPrioridad] = useState<PrioridadOrdenTrabajo>('MEDIA')
  const [descripcion, setDescripcion] = useState('')
  const [fechaProgramada, setFechaProgramada] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  async function handleSubmit() {
    setSaving(true)
    setError(null)
    try {
      await createOrdenTrabajo({
        activoId: Number(activoId),
        tipo,
        prioridad,
        descripcion: descripcion || undefined,
        fechaProgramada: fechaProgramada || null,
      })
      onSaved()
    } catch (err) {
      setError(getApiErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>Nueva orden de trabajo</h2>
        {error && <div className="error-box">{error}</div>}
        <div className="form-group">
          <label>Activo</label>
          <select value={activoId} onChange={(e) => setActivoId(e.target.value)}>
            {activos.map((a) => (
              <option key={a.id} value={a.id}>
                {a.codigo} — {a.nombre}
              </option>
            ))}
          </select>
        </div>
        <div className="form-grid">
          <div className="form-group">
            <label>Tipo</label>
            <select value={tipo} onChange={(e) => setTipo(e.target.value as TipoOrdenTrabajo)}>
              {TIPOS.map((t) => (
                <option key={t} value={t}>
                  {t}
                </option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Prioridad</label>
            <select value={prioridad} onChange={(e) => setPrioridad(e.target.value as PrioridadOrdenTrabajo)}>
              {PRIORIDADES.map((p) => (
                <option key={p} value={p}>
                  {p}
                </option>
              ))}
            </select>
          </div>
        </div>
        <div className="form-group">
          <label>Descripción</label>
          <textarea rows={3} value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Fecha programada (opcional)</label>
          <input type="date" value={fechaProgramada} onChange={(e) => setFechaProgramada(e.target.value)} />
        </div>
        <div className="form-actions">
          <button className="btn btn-primary" disabled={saving || !activoId} onClick={handleSubmit}>
            Crear
          </button>
          <button className="btn" onClick={onClose}>
            Cancelar
          </button>
        </div>
      </div>
    </div>
  )
}

function OtDetalleModal({
  ot,
  rol,
  onClose,
  onChanged,
}: {
  ot: OrdenTrabajo
  rol?: string
  onClose: () => void
  onChanged: () => void
}) {
  const [cuadrillas, setCuadrillas] = useState<Cuadrilla[]>([])
  const [cuadrillaSel, setCuadrillaSel] = useState('')
  const [rolSel, setRolSel] = useState<RolCuadrillaEnOT>('EJECUCION_TECNICA')
  const [observaciones, setObservaciones] = useState('')
  const [motivoCancelacion, setMotivoCancelacion] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    listCuadrillas().then(setCuadrillas).catch(() => {})
  }, [])

  const esTerminal = ot.estado === 'CERRADA' || ot.estado === 'CANCELADA'
  const disponibles = cuadrillas.filter((c) => c.estado === 'DISPONIBLE')

  async function withBusy(fn: () => Promise<unknown>) {
    setBusy(true)
    setError(null)
    try {
      await fn()
      onChanged()
      onClose()
    } catch (err) {
      setError(getApiErrorMessage(err))
      setBusy(false)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: 620 }}>
        <div className="page-header" style={{ marginBottom: 8 }}>
          <h2>
            OT #{ot.id} · {ot.activo.codigo}
          </h2>
          <EstadoOtBadge value={ot.estado} />
        </div>
        <p className="text-muted">
          {ot.tipo} · <PrioridadBadge value={ot.prioridad} /> · creada por {ot.creadoPor?.nombre ?? '—'}
        </p>
        {ot.descripcion && <p style={{ marginTop: 10 }}>{ot.descripcion}</p>}

        {error && <div className="error-box" style={{ marginTop: 14 }}>{error}</div>}

        <div style={{ margin: '16px 0' }}>
          <h3>Cuadrillas asignadas</h3>
          {ot.cuadrillasAsignadas.length === 0 ? (
            <p className="text-muted">Ninguna todavía</p>
          ) : (
            <div className="chip-list" style={{ marginTop: 8 }}>
              {ot.cuadrillasAsignadas.map((a) => (
                <span className="chip" key={a.id}>
                  {a.cuadrilla.codigo} · {a.rol.replaceAll('_', ' ')}
                </span>
              ))}
            </div>
          )}
        </div>

        {rol === 'COORDINADOR' && (ot.estado === 'ABIERTA' || ot.estado === 'ASIGNADA') && (
          <div style={{ margin: '16px 0' }}>
            <h3>Asignar cuadrilla</h3>
            <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
              <select value={cuadrillaSel} onChange={(e) => setCuadrillaSel(e.target.value)} style={{ flex: 1 }}>
                <option value="">Cuadrilla disponible…</option>
                {disponibles.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.codigo} — {c.nombre}
                  </option>
                ))}
              </select>
              <select value={rolSel} onChange={(e) => setRolSel(e.target.value as RolCuadrillaEnOT)}>
                {ROLES_CUADRILLA.map((r) => (
                  <option key={r} value={r}>
                    {r.replaceAll('_', ' ')}
                  </option>
                ))}
              </select>
              <button
                className="btn btn-sm"
                disabled={!cuadrillaSel || busy}
                onClick={() =>
                  withBusy(() => asignarCuadrillas(ot.id, [{ cuadrillaId: Number(cuadrillaSel), rol: rolSel }]))
                }
              >
                Asignar
              </button>
            </div>
          </div>
        )}

        {rol === 'TECNICO' && ot.estado === 'ASIGNADA' && (
          <div className="form-actions">
            <button className="btn btn-primary" disabled={busy} onClick={() => withBusy(() => iniciarEjecucion(ot.id))}>
              Iniciar ejecución
            </button>
          </div>
        )}

        {rol === 'SUPERVISOR' && ot.estado === 'EN_EJECUCION' && (
          <div style={{ margin: '16px 0' }}>
            <h3>Cerrar OT</h3>
            <div className="form-group">
              <label>Observaciones de cierre</label>
              <textarea rows={2} value={observaciones} onChange={(e) => setObservaciones(e.target.value)} />
            </div>
            <button
              className="btn btn-primary"
              disabled={busy || !observaciones}
              onClick={() => withBusy(() => cerrarOrdenTrabajo(ot.id, observaciones))}
            >
              Cerrar OT
            </button>
          </div>
        )}

        {rol === 'SUPERVISOR' && !esTerminal && (
          <div style={{ margin: '16px 0' }}>
            <h3>Cancelar OT</h3>
            <div style={{ display: 'flex', gap: 8 }}>
              <input
                style={{ flex: 1 }}
                placeholder="Motivo de cancelación"
                value={motivoCancelacion}
                onChange={(e) => setMotivoCancelacion(e.target.value)}
              />
              <button
                className="btn btn-danger btn-sm"
                disabled={busy || !motivoCancelacion}
                onClick={() => withBusy(() => cancelarOrdenTrabajo(ot.id, motivoCancelacion))}
              >
                Cancelar OT
              </button>
            </div>
          </div>
        )}

        <div className="form-actions">
          <button className="btn" onClick={onClose}>
            Cerrar ventana
          </button>
        </div>
      </div>
    </div>
  )
}
