package com.cineplus.dto;

/**
 * Cine para combos y encabezados. Ejemplo: Cine 1, Cd. Obregon.
 *
 * @author luisf
 */
public record CineDTO(
        Integer idCine,
        String nombre,
        String ciudad) {
}