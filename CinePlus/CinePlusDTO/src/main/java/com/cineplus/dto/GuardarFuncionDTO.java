package com.cineplus.dto;

import java.time.LocalDateTime;

/**
 * Datos del formulario de funcion del admin. idFuncion null = funcion nueva.
 * tipo y formato son los nombres de los enums TipoFuncion y FormatoFuncion.
 *
 * @author luisf
 */
public record GuardarFuncionDTO(
        Integer idFuncion,
        Integer idPelicula,
        Integer idSala,
        LocalDateTime fechaHora,
        String tipo,
        String formato) {
}