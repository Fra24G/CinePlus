package com.cineplus.dto;

import java.time.LocalDateTime;

/**
 * Fila de la tabla de transacciones del admin.
 * cliente con formato "Nombre I."; concepto = titulo de la pelicula.
 *
 * @author luisf
 */
public record TransaccionDTO(
        Integer idCompra,
        LocalDateTime fechaHora,
        String cliente,
        String concepto,
        String metodo,
        String ultimosDigitos,
        double total,
        String estado) {
}