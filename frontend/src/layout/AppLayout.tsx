import type { ReactNode } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const NAV_ITEMS: { to: string; label: string; roles?: string[] }[] = [
  { to: '/', label: 'Dashboard', roles: ['SUPERVISOR', 'COORDINADOR'] },
  { to: '/activos', label: 'Activos' },
  { to: '/cuadrillas', label: 'Cuadrillas' },
  { to: '/ordenes-trabajo', label: 'Órdenes de Trabajo' },
]

export function AppLayout({ children }: { children: ReactNode }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-brand">
          ORION <span>Maintenance</span>
        </div>
        <nav>
          {NAV_ITEMS.filter((item) => !item.roles || (user && item.roles.includes(user.rol))).map(
            (item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === '/'}
                className={({ isActive }) => 'sidebar-link' + (isActive ? ' active' : '')}
              >
                {item.label}
              </NavLink>
            ),
          )}
        </nav>
        <div className="sidebar-footer">
          <div className="sidebar-user">
            <strong>{user?.nombre}</strong>
            {user?.rol}
          </div>
          <button type="button" className="btn btn-sm" onClick={handleLogout} style={{ width: '100%' }}>
            Cerrar sesión
          </button>
        </div>
      </aside>
      <main className="main-content">{children}</main>
    </div>
  )
}
