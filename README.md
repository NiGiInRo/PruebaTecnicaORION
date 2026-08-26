#Candidato-FullStack

# ORION Maintenance Lite

## Cómo ejecutar

```bash
docker compose up
```

Sin pasos manuales adicionales: las migraciones de base de datos y el seed de usuarios de demo corren automáticamente al levantar el backend.

- Frontend: [http://localhost:5173](http://localhost:5173)
- API backend: expuesta internamente en la red de Docker y accesible desde el frontend vía proxy en `/api/*` (no publicada directamente al host).
- Documentación interactiva de la API (Swagger UI): [http://localhost:5173/api/swagger-ui.html](http://localhost:5173/api/swagger-ui.html) — inicia sesión con `POST /auth/login`, copia el `token` de la respuesta y úsalo en el botón *Authorize* (sin el prefijo `Bearer `).

### Usuarios de demostración

No hay registro público (ver supuestos en `BACKLOG_REFINED.md`); se crean automáticamente al primer arranque:

| Rol | Email | Contraseña |
|---|---|---|
| Supervisor | `supervisor@orion.com` | `Supervisor123!` |
| Coordinador | `coordinador@orion.com` | `Coordinador123!` |
| Técnico | `tecnico@orion.com` | `Tecnico123!` |

## Resumen de la solución

Sistema de gestión de mantenimiento de infraestructura ITS (activos, órdenes de trabajo, cuadrillas, dashboard operacional) para una concesión vial. Monorepo con 3 servicios: backend (Spring Boot, monolito modular con arquitectura hexagonal ligera), frontend (React + TypeScript) y PostgreSQL.

**Documentación del proceso:**
- [`BACKLOG_REFINED.md`](BACKLOG_REFINED.md) — análisis funcional y refinamiento de cada historia de usuario, entidades, reglas de negocio, estimación y priorización.
- [`SCENARIO.md`](SCENARIO.md) — decisiones de arquitectura (ADRs) y justificación técnica.

**Alcance implementado:** autenticación con roles (Supervisor/Coordinador/Técnico), gestión de activos y corredores viales, órdenes de trabajo con máquina de estados completa, cuadrillas (técnicos, líder, disponibilidad derivada), dashboard operacional, e inventario (materiales, entradas/salidas, bloqueo por stock insuficiente). HU-006 (averías automáticas) quedó diseñada y documentada en `BACKLOG_REFINED.md` pero no implementada, por restricción de tiempo de la prueba (24h) — priorización explícita, no un olvido.

**Tests:** 55 pruebas unitarias en el backend (dominio y casos de uso), ejecutables con:
```bash
cd maintenance && ./gradlew test
```

---

## Introducción
Bienvenido a la prueba técnica para el cargo de Desarrollador Full Stack.
El objetivo de esta evaluación es validar tus capacidades de análisis, diseño, desarrollo y documentación mediante la construcción de una solución orientada a la gestión de mantenimiento de infraestructura ITS (Intelligent Transportation Systems).

Además de la implementación técnica, esta prueba busca evaluar tu capacidad para interpretar requerimientos, refinar historias de usuario y transformar necesidades de negocio en una solución funcional y mantenible.
---

# Contexto de Negocio

La concesión vial **Autopistas Inteligentes S.A.** opera una red de infraestructura ITS distribuida en diferentes corredores viales.
Dentro de esta infraestructura se encuentran activos como:
- Paneles de Mensajería Variable (PMV)
- Cámaras CCTV
- Estaciones Meteorológicas
- Sensores de Tráfico
- Aforadores
Actualmente el mantenimiento de dichos activos se realiza mediante herramientas dispersas y hojas de cálculo, dificultando el control operativo, la planificación y el seguimiento de actividades.
La organización requiere una solución que permita gestionar sus procesos de mantenimiento de forma centralizada.
---

# Objetivo
Desarrollar una solución que permita gestionar el ciclo de vida de actividades de mantenimiento asociadas a los activos ITS.
La solución deberá permitir como mínimo:
- Gestión de activos.
- Gestión de órdenes de trabajo.
- Gestión de cuadrillas.
- Visualización de indicadores operativos.
---
# Alcance
El backlog inicial del producto se encuentra documentado en:
```text
BACKLOG.md
```
Antes de iniciar el desarrollo deberá realizarse un ejercicio de refinamiento descrito en:
```text
REFINEMENT.md
```
---
# Tecnologías
Puede utilizar cualquier stack tecnológico.
Se valorará especialmente:
- Buenas prácticas de arquitectura.
- Calidad del código.
- Documentación.
- Pruebas automatizadas unit test.
- Dockerización.
---
# Restricciones
La solución deberá:
- Persistir información.
- Ser ejecutable mediante contenedores Docker.
- Incluir documentación técnica.
- Incluir pruebas automatizadas.
---
# Entregables
La entrega deberá incluir como mínimo:
```text
Código fuente completo
Dockerfile(s)
docker-compose.yml
README.md actualizado
BACKLOG_REFINED.md
SCENARIO.md
Pruebas automatizadas
```

---
# Docker
La aplicación deberá ejecutarse mediante:
```bash
docker compose up
```
No deberán existir pasos manuales adicionales para iniciar la solución.
---

# Documento de Arquitectura
Crear el archivo:
```text
SCENARIO.md
```
Donde deberá documentar las principales decisiones arquitectónicas adoptadas.
---

# Evaluación
La evaluación tendrá en cuenta:
- Análisis funcional.
- Refinamiento del backlog.
- Diseño de la solución.
- Calidad de código.
- Uso de git y ramas de impelementación
- Arquitectura.
- Dockerización.
- Testing.
- Documentación.
- Criterio técnico.
---

# Consideraciones Finales
En caso de realizar supuestos funcionales o técnicos, estos deberán ser documentados de manera explícita.
No existe una única solución válida. Se valorará especialmente la capacidad de justificar las decisiones tomadas.
