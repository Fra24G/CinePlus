package com.cineplus.negocio.bo;

import com.cineplus.dto.GuardarFuncionDTO;
import com.cineplus.dto.PeliculaDTO;

/**
 * Reglas de validacion del modulo. Los mensajes se muestran tal cual en la pantalla.
 *
 * @author luisf
 */
final class ValidacionesCatalogo {
    private ValidacionesCatalogo() {
    }

    /** Valida los datos de una pelicula. */
    static void pelicula(PeliculaDTO datos) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: ValidacionesCatalogo.pelicula");
    }

    /** Valida los datos de una funcion. */
    static void funcion(GuardarFuncionDTO datos) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: ValidacionesCatalogo.funcion");
    }
}
