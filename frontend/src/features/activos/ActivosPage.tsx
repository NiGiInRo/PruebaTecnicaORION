import { useEffect, useState } from 'react'
import { useAuth } from '../../auth/AuthContext'
import { getApiErrorMessage } from '../../api/client'
import {
  createActivo,
  createCorredor,
  listActivos,
  listCorredores,
  updateActivo,
  type ActivoInput,
} from '../../api/activos'
import type { Activo, CorredorVial, EstadoActivo, TipoActivo } from '../../api/types'
import { EstadoActivoBadge } from '../../components/Badge'

const TIPOS: TipoActivo[] = ['PMV', 'CCTV', 'ESTACION_METEOROLOGICA', 'SENSOR_TRAFICO', 'AFORADOR']
const ESTADOS: EstadoActivo[] = ['OPERATIVO', 'FUERA_DE_SERVICIO', 'EN_MANTENIMIENTO']

export function ActivosPage() {
  const { user } = useAuth()
  const canWrite = user?.rol === 'SUPERVISOR'

  const [activos, setActivos] = useState<Activo[]>([])
  const [corredores, setCorredores] = useState<CorredorVial[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [filtroTipo, setFiltroTipo] = useState('')
  const [filtroEstado, setFiltroEstado] = useState('')
  const [filtroCorredor, setFiltroCorredor] = useState('')

  const [showActivoForm, setShowActivoForm] = useState(false)
  const [editingActivo, setEditingActivo] = useState<Activo | null>(null)
  const [showCorredorForm, setShowCorredorForm] = useState(false)

  async function load() {
    setLoading(true)
    setError(null)
    try {
      const [a, c] = await Promise.all([
        listActivos({
          tipo: (filtroTipo || undefined) as TipoActivo | undefined,
          estado: (filtroEstado || undefined) as EstadoActivo | undefined,
          corredorId: filtroCorredor ? Number(filtroCorredor) : undefined,
        }),
        listCorredores(),
      ])
      setActivos(a)
      setCorredores(c)
    } catch (err) {
      setError(getApiErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filtroTipo, filtroEstado, filtroCorredor])

  return (
    <div>
      <div className="page-header">
        <h1>Activos ITS</h1>
        {canWrite && (
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn" onClick={() => setShowCorredorForm(true)}>
              + Corredor vial
            </button>
            <button className="btn btn-primary" onClick={() => setShowActivoForm(true)}>
              + Nuevo activo
            </button>
          </div>
        )}
      </div>

      {error && <div className="error-box">{error}</div>}

      <div className="card" style={{ marginBottom: 16 }}>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          <select value={filtroTipo} onChange={(e) => setFiltroTipo(e.target.value)}>
            <option value="">Todos los tipos</option>
            {TIPOS.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
          <select value={filtroEstado} onChange={(e) => setFiltroEstado(e.target.value)}>
            <option value="">Todos los estados</option>
            {ESTADOS.map((e) => (
              <option key={e} value={e}>
                {e}
              </option>
            ))}
          </select>
          <select value={filtroCorredor} onChange={(e) => setFiltroCorredor(e.target.value)}>
            <option value="">Todos los corredores</option>
            {corredores.map((c) => (
              <option key={c.id} value={c.id}>
                {c.codigo} — {c.nombre}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="card">
        {loading ? (
          <p className="text-muted">Cargando…</p>
        ) : activos.length === 0 ? (
          <div className="empty-state">No hay activos con estos filtros.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Código</th>
                <th>Nombre</th>
                <th>Tipo</th>
                <th>Corredor</th>
                <th>PK</th>
                <th>Estado</th>
                {canWrite && <th />}
              </tr>
            </thead>
            <tbody>
              {activos.map((a) => (
                <tr key={a.id}>
                  <td>{a.codigo}</td>
                  <td>{a.nombre}</td>
                  <td>{a.tipo}</td>
                  <td>{a.corredor.codigo}</td>
                  <td>{a.pkKilometraje ?? '—'}</td>
                  <td>
                    <EstadoActivoBadge value={a.estado} />
                  </td>
                  {canWrite && (
                    <td>
                      <button className="btn btn-sm" onClick={() => setEditingActivo(a)}>
                        Editar
                      </button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showCorredorForm && (
        <CorredorFormModal
          onClose={() => setShowCorredorForm(false)}
          onSaved={() => {
            setShowCorredorForm(false)
            load()
          }}
        />
      )}

      {(showActivoForm || editingActivo) && (
        <ActivoFormModal
          corredores={corredores}
          activo={editingActivo}
          onClose={() => {
            setShowActivoForm(false)
            setEditingActivo(null)
          }}
          onSaved={() => {
            setShowActivoForm(false)
            setEditingActivo(null)
            load()
          }}
        />
      )}
    </div>
  )
}

function CorredorFormModal({ onClose, onSaved }: { onClose: () => void; onSaved: () => void }) {
  const [codigo, setCodigo] = useState('')
  const [nombre, setNombre] = useState('')
  const [descripcion, setDescripcion] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  async function handleSubmit() {
    setSaving(true)
    setError(null)
    try {
      await createCorredor({ codigo, nombre, descripcion: descripcion || undefined })
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
        <h2>Nuevo corredor vial</h2>
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
          <label>Descripción</label>
          <input value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
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

function ActivoFormModal({
  corredores,
  activo,
  onClose,
  onSaved,
}: {
  corredores: CorredorVial[]
  activo: Activo | null
  onClose: () => void
  onSaved: () => void
}) {
  const isEdit = !!activo
  const [codigo, setCodigo] = useState(activo?.codigo ?? '')
  const [nombre, setNombre] = useState(activo?.nombre ?? '')
  const [tipo, setTipo] = useState<TipoActivo>(activo?.tipo ?? 'PMV')
  const [corredorId, setCorredorId] = useState<string>(
    String(activo?.corredor.id ?? corredores[0]?.id ?? ''),
  )
  const [pk, setPk] = useState(activo?.pkKilometraje?.toString() ?? '')
  const [fabricante, setFabricante] = useState(activo?.fabricante ?? '')
  const [modelo, setModelo] = useState(activo?.modelo ?? '')
  const [fechaInstalacion, setFechaInstalacion] = useState(activo?.fechaInstalacion ?? '')
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  async function handleSubmit() {
    setSaving(true)
    setError(null)
    try {
      const base = {
        nombre,
        tipo,
        corredorId: Number(corredorId),
        pkKilometraje: pk ? Number(pk) : null,
        fabricante: fabricante || null,
        modelo: modelo || null,
        fechaInstalacion: fechaInstalacion || null,
      }
      if (isEdit && activo) {
        await updateActivo(activo.id, base)
      } else {
        await createActivo({ ...base, codigo } as ActivoInput)
      }
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
        <h2>{isEdit ? 'Editar activo' : 'Nuevo activo'}</h2>
        {error && <div className="error-box">{error}</div>}
        <div className="form-grid">
          <div className="form-group">
            <label>Código</label>
            <input value={codigo} onChange={(e) => setCodigo(e.target.value)} disabled={isEdit} />
          </div>
          <div className="form-group">
            <label>Nombre</label>
            <input value={nombre} onChange={(e) => setNombre(e.target.value)} />
          </div>
          <div className="form-group">
            <label>Tipo</label>
            <select value={tipo} onChange={(e) => setTipo(e.target.value as TipoActivo)}>
              {TIPOS.map((t) => (
                <option key={t} value={t}>
                  {t}
                </option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Corredor vial</label>
            <select value={corredorId} onChange={(e) => setCorredorId(e.target.value)}>
              {corredores.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.codigo} — {c.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>PK / Kilometraje</label>
            <input value={pk} onChange={(e) => setPk(e.target.value)} placeholder="34.500" />
          </div>
          <div className="form-group">
            <label>Fecha de instalación</label>
            <input type="date" value={fechaInstalacion} onChange={(e) => setFechaInstalacion(e.target.value)} />
          </div>
          <div className="form-group">
            <label>Fabricante</label>
            <input value={fabricante} onChange={(e) => setFabricante(e.target.value)} />
          </div>
          <div className="form-group">
            <label>Modelo</label>
            <input value={modelo} onChange={(e) => setModelo(e.target.value)} />
          </div>
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
