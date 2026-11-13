package com.cineplus.dto;

/**
 * Datos de la pantalla de perfil. membresia es null si no tiene una activa.
 *
 * @author luisf
 */
public record PerfilDTO(
        UsuarioDTO usuario,
        MembresiaDTO membresia,
        boolean pro) {
}