# Backlog Refinado — ORION Maintenance Lite

## Convenciones
- Estimación: XS (1-2h) · S (2-4h) · M (4-8h) · L (1-2 días) · XL (2-3+ días) — relativas, no compromisos de horas.
- Roles: `SUPERVISOR`, `COORDINADOR`, `TECNICO`

---

## Requisito Transversal — Autenticación y Roles
*(No existía como historia en el backlog original; se incorpora porque las reglas de autorización de cada HU dependen de ella)*

### Análisis
- **Ambigüedad**: el backlog original no menciona login ni roles.
- **Riesgo**: sin esto, cualquier regla de "quién puede cerrar una OT / asignar cuadrillas / simular avería" queda sin forma de aplicarse.
- **Supuesto**: no hay registro público de usuarios; se crean por seed/administración. No se maneja recuperación de contraseña (fuera de alcance).

### Refinamiento
- Entidad `Usuario` (id, nombre, email, passwordHash, rol).
- Roles: `SUPERVISOR` (gestiona activos, corredores, OTs, materiales), `COORDINADOR` (asigna cuadrillas), `TECNICO` (ejecuta OTs, registra consumo).
- Login vía `POST /auth/login` → JWT con rol embebido.

### Descomposición técnica
| Actividad | Estimación |
|---|---|
| Backend: entidad Usuario + Spring Security + JWT (login, filtro de autorización por rol) | L |
| Persistencia: tabla `usuario`, seed de usuarios iniciales | S |
| Frontend: pantalla de login, guardas de ruta por rol | M |
| Testing: unit tests de generación/validación de JWT, tests de acceso denegado por rol | S |

**Priorización**: MVP (bloquea todo lo demás).
**Justificación**: agregada por pedido explícito en el refinamiento; se decidió Spring Security + JWT por ser el estándar de facto en Spring Boot.

---

## HU-001 Gestión de Activos

### Análisis
- **Ambigüedades**: no define atributos por tipo de activo, ni si se permite borrado físico.
- **Dependencias**: requiere `CorredorVial` existente antes de crear un activo.
- **Riesgos**: modelar un atributo técnico distinto por cada tipo (PMV, CCTV, etc.) generaría explosión de tablas sin necesidad real en este alcance.
- **Supuestos**: un activo pertenece a un único corredor; no se contempla baja definitiva (decisión explícita, ver justificación); no hay borrado físico, solo edición de estado.

### Refinamiento
- Entidades: `Activo` (código único, nombre, tipo, corredor FK, pk/kilometraje, fabricante, modelo, fecha instalación, estado), `CorredorVial` (código, nombre, descripción).
- Estados: `OPERATIVO ⇄ FUERA_DE_SERVICIO ⇄ EN_MANTENIMIENTO` (sin estado terminal "dado de baja").
- Regla: el estado del activo se deriva de sus OTs activas (no se edita manualmente cuando hay una OT abierta/asignada/en ejecución).
- Relación: `CorredorVial` 1─N `Activo`; `Activo` 1─N `OrdenTrabajo`.

### Descomposición técnica
| Actividad | Estimación |
|---|---|
| Backend: entidades dominio Activo/CorredorVial, casos de uso CRUD, puertos + adaptadores JPA | M |
| Persistencia: migraciones `activo`, `corredor_vial` | S |
| Frontend: listado con filtros (corredor/tipo/estado), CRUD activo y corredor, detalle con historial de OTs | M |
| Seguridad: escritura solo `SUPERVISOR`, lectura para todos los roles | S |
| Testing: unit tests de dominio, tests de repositorio, tests de controller | S |

**Priorización**: MVP.
**Justificación**: se agregó `CorredorVial` (no estaba en el backlog) porque el contexto de negocio lo exige explícitamente ("red distribuida en corredores viales"). Se descartó "Dado de Baja" por decisión del usuario para no ampliar el alcance del MVP.

---

## HU-002 Gestión de Órdenes de Trabajo

### Análisis
- **Ambigüedades**: no define tipos, prioridades, estados, ni cuántas cuadrillas puede requerir una OT.
- **Dependencias**: requiere `Activo` (HU-001) y `Cuadrilla` (HU-003).
- **Riesgos**: sin máquina de estados explícita, se pueden ejecutar transiciones inválidas (ej. cerrar sin cuadrilla asignada).
- **Supuestos**: una OT siempre referencia exactamente un `Activo`; puede tener 0..N cuadrillas asignadas.

### Refinamiento
- Entidades: `OrdenTrabajo` (activo FK, tipo [PREVENTIVO/CORRECTIVO], prioridad [BAJA/MEDIA/ALTA/CRITICA], estado, fechas, descripción, observaciones de cierre, origen [MANUAL/AUTOMATICA]), `OrdenTrabajoCuadrilla` (OT + cuadrilla + **rol en la OT** [EJECUCION_TECNICA/SEÑALIZACION/APOYO_LOGISTICO] + fecha asignación).
- Estados: `ABIERTA → ASIGNADA → EN_EJECUCION → CERRADA`, con `CANCELADA` como alterno desde cualquier estado no terminal.
- Reglas: no pasa a `EN_EJECUCION` sin al menos una cuadrilla asignada; no se `CIERRA` sin fecha y observaciones de cierre; el estado del `Activo` se sincroniza con el estado de la OT (Abierta→Fuera de Servicio, Asignada/En Ejecución→En Mantenimiento, Cerrada→Operativo).

### Descomposición técnica
| Actividad | Estimación |
|---|---|
| Backend: entidad + máquina de estados en dominio, casos de uso (crear/asignar/iniciar/cerrar/cancelar), integración con estado de Activo | L |
| Persistencia: tablas `orden_trabajo`, `orden_trabajo_cuadrilla` | S |
| Frontend: listado con filtros, formulario de creación, detalle con acciones condicionadas al estado, timeline de cambios | L |
| Seguridad: `SUPERVISOR` crea/cierra/cancela, `COORDINADOR` asigna cuadrillas, `TECNICO` inicia ejecución de las suyas | S |
| Testing: unit tests de la máquina de estados (transiciones válidas/inválidas), test de integración de flujo completo | M |

**Priorización**: MVP (núcleo del sistema).
**Justificación**: se definió explícitamente la máquina de estados (no estaba en el backlog). Se modeló `OrdenTrabajoCuadrilla` como entidad con rol en vez de N:M plano, para soportar el caso real de múltiples cuadrillas por especialidad (técnica + señalización) con trazabilidad de quién hizo qué.

---

## HU-003 Gestión de Cuadrillas

### Análisis
- **Ambigüedades**: no define qué información necesita una cuadrilla ni su relación con técnicos.
- **Dependencias**: los técnicos deben existir como `Usuario` (rol `TECNICO`).
- **Riesgos**: sin control de disponibilidad, se puede asignar una cuadrilla ya ocupada, generando sobrecarga irreal.
- **Supuestos**: un técnico pertenece a una sola cuadrilla activa a la vez (simplificación de MVP, sin historial de membresías).

### Refinamiento
- Entidades: `Cuadrilla` (código, nombre, especialidad, estado, líder), `CuadrillaTecnico` (relación N:M con `Usuario`).
- Estados: `DISPONIBLE / EN_MISION / INACTIVA` — se deriva de si tiene OTs activas asignadas.
- Relación: `Cuadrilla` N─M `Usuario` (técnicos); `Cuadrilla` N─M `OrdenTrabajo` vía `OrdenTrabajoCuadrilla`.

### Descomposición técnica
| Actividad | Estimación |
|---|---|
| Backend: entidad Cuadrilla, casos de uso CRUD + asignar técnicos, lógica de disponibilidad derivada | M |
| Persistencia: tablas `cuadrilla`, `cuadrilla_tecnico` | S |
| Frontend: CRUD cuadrillas, asignación de técnicos, indicador de disponibilidad | M |
| Seguridad: gestión por `COORDINADOR`, lectura para `SUPERVISOR` | XS |
| Testing: unit tests de reglas de disponibilidad | S |

**Priorización**: MVP.
**Justificación**: se agregó la relación cuadrilla-técnico explícita porque sin ella no se puede operar el sistema; se simplificó a "una cuadrilla activa por técnico" para no complicar el MVP con historial.

---

## HU-004 Dashboard Operacional

### Análisis
- **Ambigüedad**: no especifica qué indicadores mostrar (pregunta de reflexión explícita del enunciado).
- **Dependencias**: requiere datos reales de HU-001/002/003.
- **Riesgos**: ninguno relevante al volumen de datos esperado en este alcance (se descarta necesidad de cache/vistas materializadas).

### Refinamiento
- No introduce entidades nuevas; es una vista agregada.
- Indicadores definidos: OTs por estado, % preventivo vs. correctivo, % disponibilidad de activos (operativos / total), OTs por corredor, carga de trabajo por cuadrilla, tiempo promedio de resolución (MTTR) de OTs cerradas.

### Descomposición técnica
| Actividad | Estimación |
|---|---|
| Backend: casos de uso de consulta agregada, endpoint `/dashboard/indicadores` | M |
| Frontend: tarjetas de indicadores + gráficos simples | M |
| Testing: unit tests de cálculo de cada indicador | S |

**Priorización**: MVP (depende de datos de 001-003).
**Justificación**: se definieron indicadores concretos porque el backlog dejaba la pregunta abierta a propósito; se priorizaron los calculables solo con entidades del MVP core.

---

## HU-005 Gestión de Inventario *(opcional → incluida por decisión del usuario)*

### Análisis
- **Ambigüedad**: no define cómo se controla el consumo (pregunta de reflexión explícita).
- **Dependencias**: requiere `OrdenTrabajo` (HU-002) para asociar consumo.
- **Riesgos**: decrementar stock sin historial pierde trazabilidad/auditoría.
- **Supuestos**: solo se registra consumo durante ejecución/cierre de una OT; el reabastecimiento es un movimiento manual simple, sin flujo de compras/proveedores.

### Refinamiento
- Entidades: `Material` (código, nombre, unidad, stock actual, stock mínimo), `MovimientoInventario` (material FK, OT FK opcional, tipo [ENTRADA/SALIDA], cantidad, fecha, usuario).
- **Regla confirmada**: se **bloquea** el registro de consumo si `cantidad > stockActual` (no se permite stock negativo).
- Alerta de stock bajo cuando `stockActual <= stockMinimo`.

### Descomposición técnica
| Actividad | Estimación |
|---|---|
| Backend: entidades + regla de bloqueo por stock, casos de uso (CRUD material, registrar entrada/consumo) | M |
| Persistencia: tablas `material`, `movimiento_inventario` | S |
| Frontend: CRUD materiales, registro de consumo desde detalle de OT, alerta de stock bajo | S |
| Testing: unit tests de la regla de bloqueo (casos límite: stock exacto, stock cero) | S |

**Priorización**: incluida en el alcance, pero **después** del MVP core — no bloquea el flujo principal de OTs.
**Justificación**: se modeló como movimientos (no solo un contador) para trazabilidad; se confirmó bloquear consumo sin stock suficiente en vez de permitir negativo.

---

## HU-006 Generación Automática de Averías *(opcional → incluida, versión simple + mejora híbrida)*

### Análisis
- **Ambigüedad**: no especifica si es un proceso en background o disparado manualmente.
- **Dependencias**: requiere `Activo` en estado `OPERATIVO` (HU-001); genera una `OrdenTrabajo` (HU-002).
- **Riesgos**: un job automático sin control podría duplicar OTs correctivas sobre el mismo activo; se mitiga excluyendo activos con OT correctiva ya abierta.
- **Supuestos**: es una herramienta de simulación/demo, no reemplaza monitoreo IoT real.

### Refinamiento
- No introduce entidades nuevas. Servicio `GenerarAveriaSimulada`: selecciona un `Activo` `OPERATIVO` al azar (excluyendo los que ya tengan OT correctiva abierta), lo marca `FUERA_DE_SERVICIO`, crea una `OrdenTrabajo` tipo `CORRECTIVO` con `origen = AUTOMATICA`.

### Descomposición técnica
| Actividad | Estimación |
|---|---|
| Backend: caso de uso (reutiliza el servicio de creación de OT de HU-002), endpoint `POST /averias/simular` | S |
| Frontend: botón "Simular Avería" (rol `SUPERVISOR`) | XS |
| Testing: test de exclusión de activos con OT ya abierta | XS |
| *(Mejora opcional)* `@Scheduled` activable por variable de entorno, reutilizando el mismo servicio | XS |

**Priorización**: mejora post-MVP — trigger manual primero (confirmado); scheduler solo si sobra tiempo.
**Justificación**: se reutiliza el mismo servicio de creación de OT del trigger manual y del futuro scheduler para que ambos compartan las mismas reglas y no diverjan.

---

## Priorización Global

| Fase | Historias |
|---|---|
| **MVP (bloqueante)** | Auth/Roles, HU-001, HU-002, HU-003, HU-004 |
| **Incluido, no bloqueante** | HU-005 (inventario), HU-006 versión manual |
| **Mejora futura (si sobra tiempo)** | HU-006 scheduler automático |
| **Fuera de alcance (explícitamente descartado)** | Estado "Dado de Baja" de Activo, historial de membresías de cuadrilla, flujo de compras/proveedores, recuperación de contraseña, registro público de usuarios |

## Supuestos Generales del Proyecto
- Sistema de un solo tenant/concesión (no multi-empresa).
- Sin integración real con sensores IoT; HU-006 es simulación.
- Usuarios se crean por seed, no hay auto-registro.
- Desarrollado bajo restricción de tiempo de 24h (prueba técnica): si el alcance completo no se logra, prevalece la priorización de esta tabla.

## Riesgos Generales
- Alcance amplio (6 HU + auth) para el tiempo disponible de una prueba técnica de 24h → mitigado priorizando MVP primero y dejando HU-005/006 como incrementos independientes que no bloquean el core.
- Consistencia Activo↔OT (estado derivado) es lógica sensible → mitigado con tests unitarios de dominio dedicados.
