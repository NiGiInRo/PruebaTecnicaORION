import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import { LoginPage } from './auth/LoginPage'
import { ProtectedRoute } from './auth/ProtectedRoute'
import { AppLayout } from './layout/AppLayout'
import { DashboardPage } from './features/dashboard/DashboardPage'
import { ActivosPage } from './features/activos/ActivosPage'
import { CuadrillasPage } from './features/cuadrillas/CuadrillasPage'
import { OrdenesTrabajoPage } from './features/ordenes-trabajo/OrdenesTrabajoPage'
import { MaterialesPage } from './features/materiales/MaterialesPage'

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/"
            element={
              <ProtectedRoute roles={['SUPERVISOR', 'COORDINADOR']}>
                <AppLayout>
                  <DashboardPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/activos"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <ActivosPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/cuadrillas"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <CuadrillasPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/ordenes-trabajo"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <OrdenesTrabajoPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/materiales"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <MaterialesPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
