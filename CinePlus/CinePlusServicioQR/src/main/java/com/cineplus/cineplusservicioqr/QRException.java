package com.cineplus.cineplusservicioqr;

/**
 * Error al generar un codigo QR. .
 *
 * @author luisf
 */
public class QRException extends Exception {

    private static final long serialVersionUID = 1L;

    public QRException(String mensaje) {
        super(mensaje);
    }

    public QRException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}