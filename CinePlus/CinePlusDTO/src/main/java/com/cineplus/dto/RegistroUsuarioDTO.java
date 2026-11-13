package com.cineplus.dto;

import java.time.LocalDate;

/**
 * Datos del formulario de registro. Obligatorios: nombre, correo, contrasena y fecha de nacimiento.
 * La contrasena va como char[] para poder borrarla de memoria despues de usarla.
 *
 * @author luisf
 */
public record RegistroUsuarioDTO(
        String nombre,
        String correo,
        char[] contrasena,
        LocalDate fechaNacimiento,
        String telefono,
        String nombreUsuario) {
}