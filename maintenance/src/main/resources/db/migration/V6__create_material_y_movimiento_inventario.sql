CREATE TABLE material (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    unidad_medida VARCHAR(20) NOT NULL,
    stock_actual NUMERIC(12,3) NOT NULL DEFAULT 0,
    stock_minimo NUMERIC(12,3) NOT NULL DEFAULT 0
);

CREATE TABLE movimiento_inventario (
    id BIGSERIAL PRIMARY KEY,
    material_id BIGINT NOT NULL REFERENCES material(id),
    orden_trabajo_id BIGINT REFERENCES orden_trabajo(id),
    tipo VARCHAR(10) NOT NULL,
    cantidad NUMERIC(12,3) NOT NULL,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    fecha TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_movimiento_material ON movimiento_inventario(material_id);
