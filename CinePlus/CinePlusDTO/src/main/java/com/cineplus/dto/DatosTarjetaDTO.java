package com.cineplus.dto;

/**
 * Datos de la tarjeta. Solo viajan para cobrar y no se guardan. vencimiento tiene formato MM/YY.
 *
 * @author luisf
 */
public record DatosTarjetaDTO(
        String nombreTitular,
        String numero,
        String vencimiento,
        String cvv) {

    /** Evita que la tarjeta se imprima por accidente en logs. */
    @Override
    public String toString() {
        return "DatosTarjetaDTO[****]";
    }
}