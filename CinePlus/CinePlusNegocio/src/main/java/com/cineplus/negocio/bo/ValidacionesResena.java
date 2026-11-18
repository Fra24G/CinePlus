package com.cineplus.negocio.bo;

import com.cineplus.dto.NuevaResenaDTO;

/**
 * Reglas de validacion del modulo de resenas.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
final class ValidacionesResena {

    private ValidacionesResena() {
    }

    /** Valida una resena nueva antes de persistir. */
    static void resena(NuevaResenaDTO datos) throws Exception {
        if (datos == null) {
            throw new Exception("Los datos de la reseña son obligatorios.");
        }
        boolean tieneIdLocal = datos.idPelicula() != null;
        boolean tieneIdExterno = datos.idExterno() != null && !datos.idExterno().isBlank();
        if (!tieneIdLocal && !tieneIdExterno) {
            throw new Exception("Selecciona una película para reseñar.");
        }
        if (datos.calificacion() < 1 || datos.calificacion() > 5) {
            throw new Exception("La calificación debe estar entre 1 y 5 estrellas.");
        }
        String comentario = datos.comentario() == null ? "" : datos.comentario().trim();
        if (comentario.length() < 10 || comentario.length() > 500) {
            throw new Exception("El comentario debe tener entre 10 y 500 caracteres.");
        }
    }
}
