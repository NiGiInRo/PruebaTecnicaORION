package com.orion.maintenance.domain.exception;

/** Se lanza al intentar registrar un consumo de material mayor al stock disponible
 * (regla de negocio confirmada: se bloquea, no se permite stock negativo). */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
