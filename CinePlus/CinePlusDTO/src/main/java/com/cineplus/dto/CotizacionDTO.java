package com.cineplus.dto;

/**
 * Cuanto costaria una compra antes de pagarla (pantalla de resumen).
 *
 * @author luisf
 */
public record CotizacionDTO(
        double subtotal,
        double cargoServicio,
        double montoTotal,
        int puntosDisponibles,
        int puntosUsables,
        double descuentoPuntos,
        double montoConPuntos,
        int puntosAGanar,
        boolean miembroPro) {
}