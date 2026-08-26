CREATE TABLE orden_trabajo (
    id BIGSERIAL PRIMARY KEY,
    activo_id BIGINT NOT NULL REFERENCES activo(id),
    tipo VARCHAR(20) NOT NULL,
    prioridad VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA',
    descripcion VARCHAR(1000),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT now(),
    fecha_programada DATE,
    fecha_inicio_ejecucion TIMESTAMP,
    fecha_cierre TIMESTAMP,
    observaciones_cierre VARCHAR(1000),
    origen VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    creado_por_id BIGINT REFERENCES usuario(id)
);

CREATE INDEX idx_orden_trabajo_activo ON orden_trabajo(activo_id);
CREATE INDEX idx_orden_trabajo_estado ON orden_trabajo(estado);

CREATE TABLE orden_trabajo_cuadrilla (
    id BIGSERIAL PRIMARY KEY,
    orden_trabajo_id BIGINT NOT NULL REFERENCES orden_trabajo(id),
    cuadrilla_id BIGINT NOT NULL REFERENCES cuadrilla(id),
    rol VARCHAR(30) NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_otc_orden_trabajo ON orden_trabajo_cuadrilla(orden_trabajo_id);
