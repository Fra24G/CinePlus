package com.cineplus.negocio.seguridad;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Convierte una contrasena en un hash seguro con BCrypt y comprueba
 * si una contrasena coincide con un hash guardado.
 * Nunca se guarda la contrasena en claro.
 *
 * @author luisf
 */
public final class HashContrasena {

    private static final int COSTO = 12;

    /** Devuelve el texto que se guarda en la base de datos (60 caracteres). */
    public String crear(char[] clave) {
        return BCrypt.hashpw(new String(clave), BCrypt.gensalt(COSTO));
    }

    /** Comprueba si la contrasena escrita corresponde al hash guardado. */
    public boolean coincide(char[] clave, String guardado) {
        try {
            return guardado != null && BCrypt.checkpw(new String(clave), guardado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}