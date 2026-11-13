package com.cineplus.dto;

import java.time.Instant;

/**
 * Sesion abierta: quien inicio sesion y cuando.
 *
 */
public record SesionDTO(
        String idSesion,
        UsuarioDTO usuario,
        Instant creadaEn) {
}