package com.orion.maintenance.domain.exception;

/** Se lanza cuando una entidad de dominio recibe una transición de estado que viola sus reglas. */
public class TransicionInvalidaException extends RuntimeException {

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
