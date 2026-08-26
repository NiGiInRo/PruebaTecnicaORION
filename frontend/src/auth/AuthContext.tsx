import { createContext, useContext, useMemo, useState, type ReactNode } from 'react'
import { login as loginRequest } from '../api/auth'
import type { Rol } from '../api/types'

interface AuthUser {
  nombre: string
  email: string
  rol: Rol
}

interface AuthContextValue {
  user: AuthUser | null
  login: (email: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

function readStoredUser(): AuthUser | null {
  const raw = localStorage.getItem('orion_user')
  if (!raw) return null
  try {
    return JSON.parse(raw) as AuthUser
  } catch {
    return null
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(readStoredUser)

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      login: async (email, password) => {
        const response = await loginRequest(email, password)
        const authUser: AuthUser = { nombre: response.nombre, email: response.email, rol: response.rol }
        localStorage.setItem('orion_token', response.token)
        localStorage.setItem('orion_user', JSON.stringify(authUser))
        setUser(authUser)
      },
      logout: () => {
        localStorage.removeItem('orion_token')
        localStorage.removeItem('orion_user')
        setUser(null)
      },
    }),
    [user],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider')
  return ctx
}
