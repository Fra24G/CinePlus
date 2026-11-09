package com.cineplus.servicioPeliculas;

/**
 * Error al consultar el catalogo externo de peliculas.
 *
 * @author luisf
 */
public class ServicioPeliculasException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServicioPeliculasException(String mensaje) {
        super(mensaje);
    }

    public ServicioPeliculasException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}