
package com.cineplus.negocio.bo;

import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.GuardarFuncionDTO;
import com.cineplus.entidades.enums.Clasificacion;
import com.cineplus.entidades.enums.TipoFuncion;
import com.cineplus.entidades.enums.FormatoFuncion;

/**
 * Contiene las validaciones del catalogo de peliculas y funciones
 */
final class ValidacionesCatalogo {

    // Constructor privado porque esta clase solo contiene metodos estaticos.
    private ValidacionesCatalogo() {
    }

    /**
     * Valida los datos de una peli antes de guardarla
     */
    static void pelicula(PeliculaDTO datos) throws Exception {

        if (datos == null) {
            throw new Exception("Los datos de la película son obligatorios.");
        }

        // Validar el titulo
        if (datos.titulo() == null
                || datos.titulo().trim().isEmpty()
                || datos.titulo().trim().length() > 200) {
            throw new Exception("El título debe tener entre 1 y 200 caracteres.");
        }

        // Validar la duracion
        if (datos.duracionMinutos() < 1 || datos.duracionMinutos() > 600) {
            throw new Exception("La duración debe estar entre 1 y 600 minutos.");
        }

        // Verificar que la clasificacion exista
        if (Clasificacion.porEtiqueta(datos.clasificacion()) == null) {
            throw new Exception("La clasificación de la película no es válida.");
        }

        // La sinopsis puede estar vacia, pero no superar 2000 caracteres
        if (datos.sinopsis() != null && datos.sinopsis().length() > 2000) {
            throw new Exception("La sinopsis no puede superar los 2000 caracteres.");
        }

        // La fecha inicial no puede ser posterior a la fecha final
        if (datos.inicioCartelera() != null
                && datos.finCartelera() != null
                && datos.inicioCartelera().isAfter(datos.finCartelera())) {
            throw new Exception("La fecha inicial no puede ser posterior a la fecha final.");
        }
    }

    /**
     * Valida los datos de una funcion antes de guardarla
     */
    static void funcion(GuardarFuncionDTO datos) throws Exception {

        if (datos == null) {
            throw new Exception("Los datos de la función son obligatorios.");
        }

        // Verificar que se haya seleccionado una pelicula
        if (datos.idPelicula() == null || datos.idPelicula() <= 0) {
            throw new Exception("Debes seleccionar una película.");
        }

        // Verificar que se haya seleccionado una sala
        if (datos.idSala() == null || datos.idSala() <= 0) {
            throw new Exception("Debes seleccionar una sala.");
        }

        // La funcion debe programarse para una fecha futura
        if (datos.fechaHora() == null
                || !datos.fechaHora().isAfter(java.time.LocalDateTime.now())) {
            throw new Exception("La fecha y hora de la función deben ser futuras.");
        }

        // Comprobar que el tipo de funcion exista en el enum
        try {
            TipoFuncion.valueOf(datos.tipo());
        } catch (Exception e) {
            throw new Exception("El tipo de función no es válido.");
        }

        // Comprobar que el formato exista en el enum
        try {
            FormatoFuncion.valueOf(datos.formato());
        } catch (Exception e) {
            throw new Exception("El formato de la función no es válido.");
        }
    }
}
