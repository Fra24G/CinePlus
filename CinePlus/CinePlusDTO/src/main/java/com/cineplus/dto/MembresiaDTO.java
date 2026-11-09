package com.cineplus.dto;

import java.time.LocalDateTime;

/**
 * Membresia contratada por el usuario.
 *
 * @author luisf
 */
public record MembresiaDTO(
        Integer idMembresia,
        String nombrePlan,
        double precioMensual,
        String beneficios,
        LocalDateTime fechaInicio,
        LocalDateTime fechaVencimiento,
        boolean estado) {
}