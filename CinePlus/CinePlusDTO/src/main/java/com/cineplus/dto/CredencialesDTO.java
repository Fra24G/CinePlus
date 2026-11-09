package com.cineplus.dto;

/**
 * Correo y contrasena que se escriben al iniciar sesion.
 *
 * @author luisf
 */
public record CredencialesDTO(
        String correo,
        String contrasena) {
}