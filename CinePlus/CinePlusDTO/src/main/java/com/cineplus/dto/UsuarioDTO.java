package com.cineplus.dto;

import java.time.LocalDate;

/**
 * Datos del usuario que salen hacia la pantalla. Nunca lleva la contrasena.
 *
 * @author luisf
 */
public record UsuarioDTO(
        Integer idUsuario,
        String nombre,
        String nombreUsuario,
        String correo,
        String telefono,
        LocalDate fechaNacimiento,
        Integer puntosAcumulados,
        RolDTO rol) {

    public boolean esAdmin() {
        return rol == RolDTO.ADMIN;
    }

    public String nombreMostrado() {
        return (nombreUsuario != null && !nombreUsuario.isBlank()) ? nombreUsuario : nombre;
    }
}