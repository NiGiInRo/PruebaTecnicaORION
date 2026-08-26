# Comandos — ORION Maintenance Lite

Referencia rápida de comandos para levantar, probar y depurar el proyecto. Requiere Docker Desktop abierto para todo lo que use `docker compose`.

## Arranque completo (lo único que necesita el evaluador)

```bash
docker compose up
```

En segundo plano (no bloquea la terminal):

```bash
docker compose up -d
```

Forzando reconstrucción de imágenes (necesario después de cambiar código):

```bash
docker compose up --build -d
```

Reconstruir solo un servicio (más rápido que todo):

```bash
docker compose up --build -d backend
docker compose up --build -d frontend
```

- Frontend: [http://localhost:5173](http://localhost:5173)
- Backend: no publicado directamente al host, se accede vía el proxy del frontend en `/api/*` (ej. `http://localhost:5173/api/actuator/health`)

## Apagar

```bash
docker compose down
```

Apagar y **borrar también los datos de la base** (vuelve a arrancar con el seed limpio):

```bash
docker compose down -v
```

## Diagnóstico

```bash
docker compose ps
docker compose logs backend -f
docker compose logs frontend -f
docker compose logs backend --tail 50
```

Entrar a un contenedor (el del backend trae `curl` instalado, útil para probar la API desde adentro sin depender del puerto publicado):

```bash
docker compose exec backend sh
docker compose exec backend curl -s http://localhost:8080/actuator/health
```

## Backend sin Docker (desarrollo local)

Requiere Java 21 y una base Postgres accesible (o usar `docker compose up -d db` para levantar solo la base).

```bash
cd maintenance
./gradlew bootRun
```

Correr los tests:

```bash
./gradlew test
```

Solo compilar (verificación rápida sin correr tests):

```bash
./gradlew compileJava
```

Build completo (genera el jar):

```bash
./gradlew build
```

## Frontend sin Docker (desarrollo local, con hot-reload)

Requiere Node 22+. El backend debe estar corriendo (vía Docker o `bootRun`) para que el proxy de `/api` funcione.

```bash
cd frontend
npm install
npm run dev
```

Build de producción (el mismo que corre dentro del contenedor):

```bash
npm run build
```

## Usuarios de demostración

Se crean automáticamente al primer arranque (ver `UsuarioSeeder`), no hay registro público:

| Rol | Email | Contraseña |
|---|---|---|
| Supervisor | `supervisor@orion.com` | `Supervisor123!` |
| Coordinador | `coordinador@orion.com` | `Coordinador123!` |
| Técnico | `tecnico@orion.com` | `Tecnico123!` |

## Probar la API directo (sin frontend)

```bash
curl -X POST http://localhost:5173/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"supervisor@orion.com","password":"Supervisor123!"}'
```

Con el token de la respuesta anterior:

```bash
curl http://localhost:5173/api/activos \
  -H "Authorization: Bearer <token>"
```

## Verificación "desde cero" (lo que corre el evaluador)

```bash
docker compose down -v
docker compose up --build -d
docker compose ps
curl -s http://localhost:5173/
```

Todos los servicios deben quedar en estado `healthy` sin ningún paso manual adicional.
