package com.cineplus.negocio.bo;

import com.cineplus.dto.RegistroUsuarioDTO;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Reglas de validacion de los datos de usuario.
 * Los mensajes se muestran tal cual en la pantalla.
 *
 */
final class Validaciones {

    private Validaciones() {
    }

    /** Valida el correo y lo devuelve normalizado (sin espacios y en minusculas). */
    static String correo(String correo) throws Exception {
        String normalizado = correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
        if (normalizado.length() > 120 || !normalizado.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new Exception("Escribe un correo válido, por ejemplo nombre@correo.com.");
        }
        return normalizado;
    }

    /** Valida todos los datos del formulario de registro. */
    static void registro(RegistroUsuarioDTO datos) throws Exception {
        if (datos == null || datos.nombre() == null
                || datos.nombre().strip().length() < 2
                || datos.nombre().strip().length() > 100) {
            throw new Exception("Escribe tu nombre, de 2 a 100 caracteres.");
        }
        correo(datos.correo());
        if (datos.contrasena() == null
                || datos.contrasena().length < 8
                || datos.contrasena().length > 128) {
            throw new Exception("Tu contraseña debe tener entre 8 y 128 caracteres.");
        }
        if (datos.fechaNacimiento() == null
                || datos.fechaNacimiento().isAfter(LocalDate.now())
                || datos.fechaNacimiento().isBefore(LocalDate.of(1900, 1, 1))) {
            throw new Exception("Revisa tu fecha de nacimiento.");
        }
        if (datos.nombreUsuario() != null && datos.nombreUsuario().strip().length() > 50) {
            throw new Exception("El nombre de usuario puede tener hasta 50 caracteres.");
        }
        if (datos.telefono() != null && !datos.telefono().isBlank()
                && !datos.telefono().strip().matches("[0-9+() -]{7,20}")) {
            throw new Exception("Revisa tu teléfono: solo números, de 7 a 20 caracteres.");
        }
    }

    /** Texto opcional: si esta vacio devuelve null, si no, sin espacios en los extremos. */
    static String textoOpcional(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.strip();
    }
}