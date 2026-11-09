package com.cineplus.dto;

import java.time.LocalDate;

/**
 * Datos del formulario de registro. Obligatorios: nombre, correo, contrasena y fecha de nacimiento.
 *
 * @author luisf
 */
public record RegistroUsuarioDTO(
        String nombre,
        String correo,
        String contrasena,
        LocalDate fechaNacimiento,
        String telefono,
        String nombreUsuario) {
}