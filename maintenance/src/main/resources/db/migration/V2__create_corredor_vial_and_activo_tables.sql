CREATE TABLE corredor_vial (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500)
);

CREATE TABLE activo (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    corredor_id BIGINT NOT NULL REFERENCES corredor_vial(id),
    pk_kilometraje NUMERIC(10,3),
    fabricante VARCHAR(100),
    modelo VARCHAR(100),
    fecha_instalacion DATE,
    estado VARCHAR(30) NOT NULL DEFAULT 'OPERATIVO'
);

CREATE INDEX idx_activo_corredor ON activo(corredor_id);
