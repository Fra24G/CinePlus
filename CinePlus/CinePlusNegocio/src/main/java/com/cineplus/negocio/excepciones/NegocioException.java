package com.cineplus.negocio.excepciones;

/**
 * Error de la capa de Negocio. 
 *
 * @author luisf
 */
public class NegocioException extends Exception {

    private static final long serialVersionUID = 1L;

    public NegocioException(String mensaje) {
        super(mensaje);
    }

    public NegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}