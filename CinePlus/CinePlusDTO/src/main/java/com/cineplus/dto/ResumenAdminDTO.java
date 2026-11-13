package com.cineplus.dto;

import java.util.List;

/**
 * Panel de control del admin (semana actual, de lunes a domingo).
 *
 * @author luisf
 */
public record ResumenAdminDTO(
        long boletosSemana,
        long funcionesSemana,
        List<PeliculaAdminDTO> peliculas) {

    public ResumenAdminDTO {
        peliculas = List.copyOf(peliculas);
    }
}