ALTER TABLE cuadrilla ADD COLUMN lider_id BIGINT REFERENCES usuario(id);

CREATE TABLE cuadrilla_tecnico (
    id BIGSERIAL PRIMARY KEY,
    cuadrilla_id BIGINT NOT NULL REFERENCES cuadrilla(id),
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuario(id),
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_cuadrilla_tecnico_cuadrilla ON cuadrilla_tecnico(cuadrilla_id);
