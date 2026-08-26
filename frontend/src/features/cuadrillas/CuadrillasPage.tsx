import { useEffect, useState } from 'react'
import { useAuth } from '../../auth/AuthContext'
import { getApiErrorMessage } from '../../api/client'
import {
  activarCuadrilla,
  agregarTecnico,
  createCuadrilla,
  desactivarCuadrilla,
  designarLider,
  listCuadrillas,
  quitarTecnico,
} from '../../api/cuadrillas'
import { listUsuarios } from '../../api/usuarios'
import type { Cuadrilla, EspecialidadCuadrilla, UsuarioResumen } from '../../api/types'
import { EstadoCuadrillaBadge } from '../../components/Badge'

const ESPECIALIDADES: EspecialidadCuadrilla[] = [
  'ELECTRICA',
  'REDES_COMUNICACIONES',
  'MECANICA',
  'CIVIL',
  'SENALIZACION',
]

export function CuadrillasPage() {
  const { user } = useAuth()
  const canWrite = user?.rol === 'COORDINADOR'

  const [cuadrillas, setCuadrillas] = useState<Cuadrilla[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [detalle, setDetalle] = useState<Cuadrilla | null>(null)

  async function load() {
    setLoading(true)
    setError(null)
    try {
      setCuadrillas(await listCuadrillas())
    } catch (err) {
      setError(getApiErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  return (
    <div>
      <div className="page-header">
        <h1>Cuadrillas</h1>
        {canWrite && (
          <button className="btn btn-primary" onClick={() => setShowForm(true)}>
            + Nueva cuadrilla
          </button>
        )}
      </div>

      {error && <div className="error-box">{error}</div>}

      <div className="card">
        {loading ? (
          <p className="text-muted">Cargando…</p>
        ) : cuadrillas.length === 0 ? (
          <div className="empty-state">No hay cuadrillas registradas.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Código</th>
                <th>Nombre</th>
                <th>Especialidad</th>
                <th>Líder</th>
                <th>Técnicos</th>
                <th>Estado</th>
                {canWrite && <th />}
              </tr>
            </thead>
            <tbody>
              {cuadrillas.map((c) => (
                <tr key={c.id}>
                  <td>{c.codigo}</td>
                  <td>{c.nombre}</td>
                  <td>{c.especialidad}</td>
                  <td>{c.lider?.nombre ?? '—'}</td>
                  <td>{c.tecnicos.length}</td>
                  <td>
                    <EstadoCuadrillaBadge value={c.estado} />
                  </td>
                  {canWrite && (
                    <td>
                      <button className="btn btn-sm" onClick={() => setDetalle(c)}>
                        Gestionar
                      </button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showForm && (
        <CuadrillaFormModal
          onClose={() => setShowForm(false)}
          onSaved={() => {
            setShowForm(false)
            load()
          }}
        />
      )}

      {detalle && (
        <CuadrillaDetalleModal
          cuadrilla={detalle}
          onClose={() => setDetalle(null)}
          onChanged={async () => {
            await load()
            const actualizadas = await listCuadrillas()
            setDetalle(actualizadas.find((c) => c.id === detalle.id) ?? null)
          }}
        />
      )}
    </div>
  )
}

function CuadrillaFormModal({ onClose, onSaved }: { onClose: () => void; onSaved: () => void }) {
  const [codigo, setCodigo] = useState('')
  const [nombre, setNombre] = useState('')
  const [especialidad, setEspecialidad] = useState<EspecialidadCuadrilla>('ELECTRICA')
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  async function handleSubmit() {
    setSaving(true)
    setError(null)
    try {
      await createCuadrilla({ codigo, nombre, especialidad })
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
        <h2>Nueva cuadrilla</h2>
        {error && <div className="error-box">{error}</div>}
        <div className="form-group">
          <label>Código</label>
          <input value={codigo} onChange={(e) => setCodigo(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Nombre</label>
          <input value={nombre} onChange={(e) => setNombre(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Especialidad</label>
          <select value={especialidad} onChange={(e) => setEspecialidad(e.target.value as EspecialidadCuadrilla)}>
            {ESPECIALIDADES.map((e) => (
              <option key={e} value={e}>
                {e}
              </option>
            ))}
          </select>
        </div>
        <div className="form-actions">
          <button className="btn btn-primary" disabled={saving} onClick={handleSubmit}>
            Guardar
          </button>
          <button className="btn" onClick={onClose}>
            Cancelar
          </button>
        </div>
      </div>
    </div>
  )
}

function CuadrillaDetalleModal({
  cuadrilla,
  onClose,
  onChanged,
}: {
  cuadrilla: Cuadrilla
  onClose: () => void
  onChanged: () => void
}) {
  const [tecnicosDisponibles, setTecnicosDisponibles] = useState<UsuarioResumen[]>([])
  const [seleccionado, setSeleccionado] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    listUsuarios('TECNICO').then(setTecnicosDisponibles).catch(() => {})
  }, [])

  const miembrosIds = new Set(cuadrilla.tecnicos.map((t) => t.id))
  const candidatos = tecnicosDisponibles.filter((t) => !miembrosIds.has(t.id))

  async function withBusy(fn: () => Promise<unknown>) {
    setBusy(true)
    setError(null)
    try {
      await fn()
      onChanged()
    } catch (err) {
      setError(getApiErrorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>{cuadrilla.nombre}</h2>
        <p className="text-muted">
          {cuadrilla.codigo} · {cuadrilla.especialidad}
        </p>
        {error && <div className="error-box">{error}</div>}

        <div style={{ margin: '16px 0' }}>
          <h3>Estado</h3>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginTop: 8 }}>
            <EstadoCuadrillaBadge value={cuadrilla.estado} />
            {cuadrilla.estado !== 'EN_MISION' &&
              (cuadrilla.estado === 'INACTIVA' ? (
                <button
                  className="btn btn-sm"
                  disabled={busy}
                  onClick={() => withBusy(() => activarCuadrilla(cuadrilla.id))}
                >
                  Activar
                </button>
              ) : (
                <button
                  className="btn btn-sm"
                  disabled={busy}
                  onClick={() => withBusy(() => desactivarCuadrilla(cuadrilla.id))}
                >
                  Desactivar
                </button>
              ))}
          </div>
        </div>

        <div style={{ margin: '16px 0' }}>
          <h3>Líder</h3>
          <p className="text-muted" style={{ marginBottom: 8 }}>
            {cuadrilla.lider ? cuadrilla.lider.nombre : 'Sin líder designado'}
          </p>
        </div>

        <div style={{ margin: '16px 0' }}>
          <h3>Técnicos</h3>
          <div className="chip-list" style={{ marginTop: 8, marginBottom: 12 }}>
            {cuadrilla.tecnicos.length === 0 && <span className="text-muted">Sin técnicos asignados</span>}
            {cuadrilla.tecnicos.map((t) => (
              <span className="chip" key={t.id}>
                {t.nombre}
                {cuadrilla.lider?.id !== t.id && (
                  <button
                    title="Designar líder"
                    disabled={busy}
                    onClick={() => withBusy(() => designarLider(cuadrilla.id, t.id))}
                  >
                    ★
                  </button>
                )}
                <button title="Quitar" disabled={busy} onClick={() => withBusy(() => quitarTecnico(cuadrilla.id, t.id))}>
                  ×
                </button>
              </span>
            ))}
          </div>

          <div style={{ display: 'flex', gap: 8 }}>
            <select value={seleccionado} onChange={(e) => setSeleccionado(e.target.value)} style={{ flex: 1 }}>
              <option value="">Seleccionar técnico…</option>
              {candidatos.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.nombre} ({t.email})
                </option>
              ))}
            </select>
            <button
              className="btn btn-sm"
              disabled={!seleccionado || busy}
              onClick={() => withBusy(() => agregarTecnico(cuadrilla.id, Number(seleccionado)))}
            >
              Agregar
            </button>
          </div>
        </div>

        <div className="form-actions">
          <button className="btn" onClick={onClose}>
            Cerrar
          </button>
        </div>
      </div>
    </div>
  )
}
