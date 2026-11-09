package com.cineplus.dto;

/**
 * Como se quiere pagar. usarPuntos = true gasta todos los puntos posibles ("Usar saldo CinePLUS").
 *
 * @author luisf
 */
public record DatosPagoDTO(
        TipoMetodoPago metodo,
        boolean usarPuntos,
        DatosTarjetaDTO tarjeta) {
}