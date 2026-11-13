package com.cineplus.negocio.bo;

import com.cineplus.dto.CancelacionDTO;
import com.cineplus.dto.FiltroComprasDTO;

/**
 * Reglas de validacion del modulo. Los mensajes se muestran tal cual en la pantalla.
 *
 * @author luisf
 */
final class ValidacionesHistorial {
    private ValidacionesHistorial() {
    }

    /** Valida filtros y paginacion. */
    static void filtro(FiltroComprasDTO filtro) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: ValidacionesHistorial.filtro");
    }

    /** Valida la peticion de cancelacion. */
    static void cancelacion(CancelacionDTO datos) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: ValidacionesHistorial.cancelacion");
    }
}
