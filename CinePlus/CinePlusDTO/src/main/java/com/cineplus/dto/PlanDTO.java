package com.cineplus.dto;

/**
 * Un plan que se muestra en la pantalla de membresias.
 *
 * @author luisf
 */
public record PlanDTO(
        String nombre,
        double precioMensual,
        String beneficios,
        boolean gratuito) {
}