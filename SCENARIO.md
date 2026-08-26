# SCENARIO.md — Documento de Arquitectura
## ORION Maintenance Lite

## 1. Resumen
Sistema de gestión de mantenimiento de infraestructura ITS para una concesión vial. Monorepo con 3 servicios contenedorizados: backend (API REST), frontend (SPA) y base de datos.

## 2. Restricción relevante
Desarrollado en un plazo de 24h (prueba técnica). Las decisiones de alcance y arquitectura priorizan **simplicidad defendible** sobre cobertura exhaustiva — se prefiere un MVP sólido, testeado y documentado, a un sistema amplio pero frágil.

## 3. Decisiones Arquitectónicas (ADR resumidos)

### ADR-1: Monolito modular, no microservicios
**Decisión**: un único servicio backend, no servicios separados por dominio ni un servicio de auth aparte.
**Razón**: microservicios resuelven problemas de escalado independiente y equipos grandes trabajando en paralelo — ninguno aplica aquí (un desarrollador, alcance acotado, `docker compose up` único). Separar auth añadiría validación de JWT entre servicios, un contenedor más y latencia de red sin beneficio real a este alcance.
**Alternativa descartada**: 3 servicios (front, back de negocio, auth) — descartada por sobre-ingeniería para el tamaño del problema.

### ADR-2: Arquitectura hexagonal ligera dentro del backend
**Decisión**: capas `domain` (entidades + reglas de negocio, sin dependencias de framework) / `application` (casos de uso, puertos como interfaces) / `infrastructure` (adaptadores: JPA, controllers REST, seguridad).
**Razón**: desacopla la lógica de negocio de Spring/JPA, permite testear el dominio (ej. máquina de estados de OT, regla de stock) sin levantar contexto de Spring ni base de datos. No se aplica el ceremonial completo de DDD/hexagonal puro (no hay un puerto por cada detalle mínimo) para no perder tiempo en el plazo disponible.

### ADR-3: `CorredorVial` como entidad propia
**Decisión**: entidad independiente, no un campo de texto libre en `Activo`.
**Razón**: el propio contexto de negocio describe la infraestructura como "distribuida en corredores viales" — es una unidad organizativa real del dominio, y habilita indicadores del dashboard por corredor.

### ADR-4: `OrdenTrabajo` admite múltiples `Cuadrilla` mediante entidad de asignación con rol
**Decisión**: `OrdenTrabajoCuadrilla` (OT + cuadrilla + rol en la OT + fecha) en vez de N:M plano.
**Razón**: en mantenimiento vial una intervención puede requerir cuadrilla técnica + cuadrilla de señalización/control de tráfico simultáneamente (normativa de seguridad vial) — son roles distintos, no cuadrillas intercambiables. El rol da trazabilidad real de quién hizo qué.

### ADR-5: Autenticación con Spring Security + JWT, dentro del mismo backend
**Decisión**: módulo `auth` desacoplado internamente (paquete propio, sin lógica de negocio de OTs/inventario mezclada), pero no un servicio separado.
**Razón**: frontera clara para una futura extracción si el sistema creciera a multi-concesión/SSO real, sin pagar el costo de un microservicio ahora.

### ADR-6: Generación de averías (HU-006) — trigger manual primero, scheduler opcional reutilizando el mismo servicio
**Decisión**: `POST /averias/simular` invoca un caso de uso `GenerarAveriaSimulada`; un `@Scheduled` opcional (activable por variable de entorno) invoca el mismo caso de uso.
**Razón**: control total en demo (manual) sin duplicar reglas si luego se agrega el scheduler.
**Estado**: diseñado y justificado, **no implementado** — priorizado fuera del alcance de las 24h (ver tabla de priorización en `BACKLOG_REFINED.md`).

### ADR-7: Bloqueo de consumo de inventario sin stock suficiente
**Decisión**: `RegistrarConsumo` valida `cantidad <= stockActual` antes de persistir, rechaza si no alcanza.
**Razón**: decisión de negocio confirmada — se prioriza integridad del inventario sobre flexibilidad operativa.
**Estado**: implementado (HU-005). Las entradas no requieren OT; las salidas siempre quedan asociadas a la orden de trabajo durante la cual se consumió.

### ADR-8: Entidades de dominio anotadas directamente con JPA (sin modelo de persistencia separado)
**Decisión**: las entidades en `domain.model` llevan anotaciones JPA, y los puertos de repositorio (`application.port`) extienden `JpaRepository`/`JpaSpecificationExecutor` directamente, en vez de una interfaz 100% agnóstica de framework con adaptador propio en `infrastructure`.
**Razón**: pragmatismo de tiempo — evita duplicar cada entidad en un modelo de persistencia + mapper. El dominio conserva toda la lógica de negocio (máquinas de estado, invariantes) y los puertos siguen siendo mockeables en tests de casos de uso (ver `OrdenTrabajoServiceTest`, `ActivoServiceTest`, etc.). No es hexagonal 100% puro, es una concesión explícita y documentada.

## 4. Vista de Componentes

```
[ React SPA ] ---- HTTP/JSON (JWT) ----> [ Spring Boot API ] ----> [ PostgreSQL ]
                                                |
                                    domain / application / infrastructure
```

Tres contenedores vía `docker-compose.yml`: `frontend`, `backend`, `db`. `docker compose up` sin pasos manuales adicionales (migraciones automáticas al arrancar el backend, seed de usuarios iniciales incluido).

## 5. Modelo de Datos
Ver detalle completo de entidades, estados y reglas en `BACKLOG_REFINED.md`.
**Implementadas**: `Usuario`, `CorredorVial`, `Activo`, `OrdenTrabajo`, `OrdenTrabajoCuadrilla`, `Cuadrilla`, `CuadrillaTecnico`, `Material`, `MovimientoInventario`.
**Diseñada, no implementada** (HU-006, ver ADR-6): generación automática de averías — no introduce entidades nuevas, reutiliza `Activo`/`OrdenTrabajo`.

## 6. Stack Tecnológico
- Backend: Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA
- Frontend: React + TypeScript + Vite, sin librería de componentes (CSS propio)
- Base de datos: PostgreSQL
- Migraciones: Flyway
- Testing: JUnit 5 + Mockito (backend) — 55 tests. Sin tests automatizados de frontend por restricción de tiempo (ver riesgo abajo).
- Contenedores: Docker + Docker Compose

## 7. Estrategia de Testing
Foco en lógica de dominio con mayor riesgo de bugs: máquina de estados de OT, sincronización de estado Activo↔OT y Cuadrilla↔OT, validaciones de disponibilidad/duplicados. Tests de dominio sin dependencias externas (puro Java), complementados con tests de casos de uso vía mocks de los puertos de repositorio. El frontend se validó manualmente end-to-end (los 3 roles, flujo completo de una OT) contra el `docker compose up` real, pero no quedaron pruebas automatizadas — riesgo aceptado explícitamente por la restricción de 24h.

## 8. Cómo ejecutar
```bash
docker compose up
```
Backend expone la API, frontend queda servido, migraciones y seed corren automáticamente al levantar el backend.

## 9. Supuestos y Riesgos
Ver secciones "Supuestos Generales" y "Riesgos Generales" en `BACKLOG_REFINED.md`.
