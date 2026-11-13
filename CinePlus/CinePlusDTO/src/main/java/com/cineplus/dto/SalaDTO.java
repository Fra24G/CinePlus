package com.cineplus.dto;

/**
 * Sala de un cine. capacidadTotal se muestra como AFORO.
 *
 * @author luisf
 */
public record SalaDTO(
        Integer idSala,
        Integer idCine,
        int numeroSala,
        int capacidadTotal,
        String tipoSala) {
}