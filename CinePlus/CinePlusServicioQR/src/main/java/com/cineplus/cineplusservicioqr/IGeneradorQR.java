package com.cineplus.cineplusservicioqr;

/**
 * PATRON ADAPTER (objetivo). Contrato del generador de codigos QR.
 * Negocio solo conoce esta interfaz, no la libreria que genera el QR.
 *
 * @author luisf
 */
public interface IGeneradorQR {

    /** Genera el QR del contenido como imagen PNG de tamanoPx x tamanoPx pixeles. */
    byte[] generarPng(String contenido, int tamanoPx) throws Exception;
}
