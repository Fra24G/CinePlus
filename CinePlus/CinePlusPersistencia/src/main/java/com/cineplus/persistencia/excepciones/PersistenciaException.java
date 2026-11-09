package com.cineplus.persistencia.excepciones;

/**
 * Error de la capa de Persistencia.
 *
 * @author luisf
 */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}