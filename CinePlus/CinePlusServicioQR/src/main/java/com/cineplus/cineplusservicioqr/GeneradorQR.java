package com.cineplus.cineplusservicioqr;

/**
 * Patron Adapter: traduce la libreria de QR a la interfaz IGeneradorQR.
 *
 * @author Mario Perez
 */
public class GeneradorQR implements IGeneradorQR {

    @Override
    public byte[] generarPng(String contenido, int tamanoPx) throws Exception {
        throw new Exception("Pendiente: GeneradorQR.generarPng");
    }
}
