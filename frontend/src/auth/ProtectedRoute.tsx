import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import type { Rol } from '../api/types'
import { useAuth } from './AuthContext'

export function ProtectedRoute({
  children,
  roles,
  fallback = '/activos',
}: {
  children: ReactNode
  roles?: Rol[]
  fallback?: string
}) {
  const { user } = useAuth()

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (roles && !roles.includes(user.rol)) {
    return <Navigate to={fallback} replace />
  }

  return <>{children}</>
}
