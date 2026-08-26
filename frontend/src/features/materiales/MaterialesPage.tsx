import { useEffect, useState } from 'react'
import { useAuth } from '../../auth/AuthContext'
import { getApiErrorMessage } from '../../api/client'
import {
  createMaterial,
  listMateriales,
  listMovimientos,
  registrarConsumo,
  registrarEntrada,
} from '../../api/materiales'
import { listOrdenesTrabajo } from '../../api/ordenesTrabajo'
import type { Material, MovimientoInventario, OrdenTrabajo } from '../../api/types'

export function MaterialesPage() {
  const { user } = useAuth()
  const [materiales, setMateriales] = useState<Material[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [showForm, setShowForm] = useState(false)
  const [entradaMaterial, setEntradaMaterial] = useState<Material | null>(null)
  const [consumoMaterial, setConsumoMaterial] = useState<Material | null>(null)
  const [historialMaterial, setHistorialMaterial] = useState<Material | null>(null)

  async function load() {
    setLoading(true)
    setError(null)
    try {
      setMateriales(await listMateriales())
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
        <h1>Inventario</h1>
        {user?.rol === 'SUPERVISOR' && (
          <button className="btn btn-primary" onClick={() => setShowForm(true)}>
            + Nuevo material
          </button>
        )}
      </div>

      {error && <div className="error-box">{error}</div>}

      <div className="card">
        {loading ? (
          <p className="text-muted">Cargando…</p>
        ) : materiales.length === 0 ? (
          <div className="empty-state">No hay materiales registrados.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Código</th>
                <th>Nombre</th>
                <th>Unidad</th>
                <th>Stock actual</th>
                <th>Stock mínimo</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {materiales.map((m) => (
                <tr key={m.id}>
                  <td>{m.codigo}</td>
                  <td>{m.nombre}</td>
                  <td>{m.unidadMedida}</td>
                  <td>
                    {m.stockActual}
                    {m.stockBajo && (
                      <span className="badge badge-danger" style={{ marginLeft: 8 }}>
                        stock bajo
                      </span>
                    )}
                  </td>
                  <td>{m.stockMinimo}</td>
                  <td style={{ display: 'flex', gap: 6, justifyContent: 'flex-end' }}>
                    <button className="btn btn-sm" onClick={() => setHistorialMaterial(m)}>
                      Movimientos
                    </button>
                    {user?.rol === 'SUPERVISOR' && (
                      <button className="btn btn-sm" onClick={() => setEntradaMaterial(m)}>
                        + Entrada
                      </button>
                    )}
                    {user?.rol === 'TECNICO' && (
                      <button className="btn btn-sm" onClick={() => setConsumoMaterial(m)}>
                        − Consumo
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showForm && (
        <MaterialFormModal
          onClose={() => setShowForm(false)}
          onSaved={() => {
            setShowForm(false)
            load()
          }}
        />
      )}

      {entradaMaterial && (
        <EntradaModal
          material={entradaMaterial}
          onClose={() => setEntradaMaterial(null)}
          onSaved={() => {
            setEntradaMaterial(null)
            load()
          }}
        />
      )}

      {consumoMaterial && (
        <ConsumoModal
          material={consumoMaterial}
          onClose={() => setConsumoMaterial(null)}
          onSaved={() => {
            setConsumoMaterial(null)
            load()
          }}
        />
      )}

      {historialMaterial && (
        <HistorialModal material={historialMaterial} onClose={() => setHistorialMaterial(null)} />
      )}
    </div>
  )
}

function MaterialFormModal({ onClose, onSaved }: { onClose: () => void; onSaved: () => void }) {
  const [codigo, setCodigo] = useState('')
  const [nombre, setNombre] = useState('')
  const [unidadMedida, setUnidadMedida] = useState('')
  const [stockMinimo, setStockMinimo] = useState('0')
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  async function handleSubmit() {
    setSaving(true)
    setError(null)
    try {
      await createMaterial({ codigo, nombre, unidadMedida, stockMinimo: Number(stockMinimo) })
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
        <h2>Nuevo material</h2>
        {error && <div className="error-box">{error}</div>}
        <div className="form-group">
          <label>Código</label>
          <input value={codigo} onChange={(e) => setCodigo(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Nombre</label>
          <input value={nombre} onChange={(e) => setNombre(e.target.value)} />
        </div>
        <div className="form-grid">
          <div className="form-group">
            <label>Unidad de medida</label>
            <input
              value={unidadMedida}
              onChange={(e) => setUnidadMedida(e.target.value)}
              placeholder="unidad, metro, litro…"
            />
          </div>
          <div className="form-group">
            <label>Stock mínimo</label>
            <input type="number" min="0" step="0.001" value={stockMinimo} onChange={(e) => setStockMinimo(e.target.value)} />
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

function EntradaModal({
  material,
  onClose,
  onSaved,
}: {
  material: Material
  onClose: () => void
  onSaved: () => void
}) {
  const [cantidad, setCantidad] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  async function handleSubmit() {
    setSaving(true)
    setError(null)
    try {
      await registrarEntrada(material.id, Number(cantidad))
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
        <h2>Registrar entrada — {material.nombre}</h2>
        <p className="text-muted">Stock actual: {material.stockActual} {material.unidadMedida}</p>
        {error && <div className="error-box">{error}</div>}
        <div className="form-group">
          <label>Cantidad a ingresar</label>
          <input type="number" min="0.001" step="0.001" value={cantidad} onChange={(e) => setCantidad(e.target.value)} autoFocus />
        </div>
        <div className="form-actions">
          <button className="btn btn-primary" disabled={saving || !cantidad} onClick={handleSubmit}>
            Registrar
          </button>
          <button className="btn" onClick={onClose}>
            Cancelar
          </button>
        </div>
      </div>
    </div>
  )
}

function ConsumoModal({
  material,
  onClose,
  onSaved,
}: {
  material: Material
  onClose: () => void
  onSaved: () => void
}) {
  const [ordenes, setOrdenes] = useState<OrdenTrabajo[]>([])
  const [otId, setOtId] = useState('')
  const [cantidad, setCantidad] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    listOrdenesTrabajo()
      .then((ots) => setOrdenes(ots.filter((o) => o.estado !== 'CERRADA' && o.estado !== 'CANCELADA')))
      .catch(() => {})
  }, [])

  async function handleSubmit() {
    setSaving(true)
    setError(null)
    try {
      await registrarConsumo(material.id, Number(otId), Number(cantidad))
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
        <h2>Registrar consumo — {material.nombre}</h2>
        <p className="text-muted">Stock actual: {material.stockActual} {material.unidadMedida}</p>
        {error && <div className="error-box">{error}</div>}
        <div className="form-group">
          <label>Orden de trabajo</label>
          <select value={otId} onChange={(e) => setOtId(e.target.value)}>
            <option value="">Seleccionar OT…</option>
            {ordenes.map((o) => (
              <option key={o.id} value={o.id}>
                #{o.id} — {o.activo.codigo} ({o.estado})
              </option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label>Cantidad consumida</label>
          <input type="number" min="0.001" step="0.001" value={cantidad} onChange={(e) => setCantidad(e.target.value)} />
        </div>
        <div className="form-actions">
          <button className="btn btn-primary" disabled={saving || !otId || !cantidad} onClick={handleSubmit}>
            Registrar
          </button>
          <button className="btn" onClick={onClose}>
            Cancelar
          </button>
        </div>
      </div>
    </div>
  )
}

function HistorialModal({ material, onClose }: { material: Material; onClose: () => void }) {
  const [movimientos, setMovimientos] = useState<MovimientoInventario[] | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    listMovimientos(material.id)
      .then(setMovimientos)
      .catch((err) => setError(getApiErrorMessage(err)))
  }, [material.id])

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: 560 }}>
        <h2>Movimientos — {material.nombre}</h2>
        {error && <div className="error-box">{error}</div>}
        {!movimientos ? (
          <p className="text-muted">Cargando…</p>
        ) : movimientos.length === 0 ? (
          <p className="text-muted">Sin movimientos registrados.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Tipo</th>
                <th>Cantidad</th>
                <th>OT</th>
                <th>Usuario</th>
              </tr>
            </thead>
            <tbody>
              {movimientos.map((m) => (
                <tr key={m.id}>
                  <td className="text-muted">{new Date(m.fecha).toLocaleString()}</td>
                  <td>
                    <span className={`badge ${m.tipo === 'ENTRADA' ? 'badge-success' : 'badge-warning'}`}>
                      {m.tipo}
                    </span>
                  </td>
                  <td>{m.cantidad}</td>
                  <td>{m.ordenTrabajoId ?? '—'}</td>
                  <td>{m.usuario.nombre}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <div className="form-actions">
          <button className="btn" onClick={onClose}>
            Cerrar
          </button>
        </div>
      </div>
    </div>
  )
}
