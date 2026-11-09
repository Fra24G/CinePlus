package com.cineplus.dto;

/**
 * Un asiento de la sala, con su tipo, su precio y si esta ocupado en esa funcion.
 *
 * @author luisf
 */
public record AsientoDTO(
        Integer idAsiento,
        String fila,
        int numero,
        String tipo,
        double precio,
        boolean ocupado) {

    /** Ejemplo: "B3". */
    public String etiqueta() {
        return fila + numero;
    }
}