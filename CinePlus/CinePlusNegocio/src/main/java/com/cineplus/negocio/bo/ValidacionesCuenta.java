package com.cineplus.negocio.bo;

import com.cineplus.dto.ActualizarPerfilDTO;
import java.nio.charset.StandardCharsets;

/**
 * Validaciones del modulo Cuenta (perfil del usuario).
 * Clase final de paquete: solo la usan los BO de com.cineplus.negocio.bo.
 *
 * @author Luis Guillermo Franco Murrieta
 */
final class ValidacionesCuenta {

    /** BCrypt solo toma en cuenta los primeros 72 bytes de la contrasena. */
    private static final int MAX_CONTRASENA = 72;
    private static final int MIN_CONTRASENA = 8;

    private ValidacionesCuenta() {
    }

    /**
     * Nombre de 2 a 100; nombre de usuario hasta 50; telefono opcional de 7 a 20
     * (mismo formato que el registro); contrasena nueva opcional de 8 a 72.
     */
    static void perfil(ActualizarPerfilDTO datos) throws Exception {
        if (datos == null) {
            throw new Exception("Captura los datos de tu perfil.");
        }
        if (datos.nombre() == null
                || datos.nombre().strip().length() < 2
                || datos.nombre().strip().length() > 100) {
            throw new Exception("Escribe tu nombre, de 2 a 100 caracteres.");
        }
        if (datos.nombreUsuario() != null && datos.nombreUsuario().strip().length() > 50) {
            throw new Exception("El nombre de usuario puede tener hasta 50 caracteres.");
        }
        // Mismo patron que Validaciones.registro para no rechazar un telefono ya registrado
        if (datos.telefono() != null && !datos.telefono().isBlank()
                && !datos.telefono().strip().matches("[0-9+() -]{7,20}")) {
            throw new Exception("Revisa tu teléfono: solo números, de 7 a 20 caracteres.");
        }
        if (cambiaContrasena(datos)) {
            String nueva = datos.nuevaContrasena();
            if (nueva.length() < MIN_CONTRASENA
                    || nueva.getBytes(StandardCharsets.UTF_8).length > MAX_CONTRASENA) {
                throw new Exception("Tu nueva contraseña debe tener entre 8 y 72 caracteres.");
            }
        }
    }

    /** true si el usuario escribio una contrasena nueva (vacia = no se cambia). */
    static boolean cambiaContrasena(ActualizarPerfilDTO datos) {
        return datos != null
                && datos.nuevaContrasena() != null
                && !datos.nuevaContrasena().isEmpty();
    }
}
