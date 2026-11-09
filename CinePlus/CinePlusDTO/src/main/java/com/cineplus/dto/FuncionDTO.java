package com.cineplus.dto;

import java.time.LocalDateTime;

/**
 * Una funcion de una pelicula. tipo es el idioma y formato es 2D o 3D.
 *
 * @author luisf
 */
public record FuncionDTO(
        Integer idFuncion,
        Integer idPelicula,
        String tituloPelicula,
        Integer idSala,
        int numeroSala,
        String cine,
        String tipo,
        String formato,
        int aforo,
        LocalDateTime fechaHora) {
}