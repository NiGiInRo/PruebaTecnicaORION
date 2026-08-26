package com.orion.maintenance.application.exception;

/** Regla de negocio que cruza agregados y no encaja en una entidad de dominio única
 * (ej. técnico que ya pertenece a otra cuadrilla, cuadrilla no disponible para asignar). */
public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
