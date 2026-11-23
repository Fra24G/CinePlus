package com.cineplus.negocio.bo;

import com.cineplus.dto.CancelacionDTO;
import com.cineplus.dto.FiltroComprasDTO;

/**
 * Reglas de validacion del modulo. Los mensajes se muestran tal cual en la pantalla.
 *
 * @author Mario Perez
 */
final class ValidacionesHistorial {

    private static final int TAMANO_MAXIMO_PAGINA = 50;
    private static final int MOTIVO_MINIMO = 5;
    private static final int MOTIVO_MAXIMO = 300;

    private ValidacionesHistorial() {
    }

    /** Valida filtros y paginacion. El estado lo convierte y valida HistorialBO. */
    static void filtro(FiltroComprasDTO filtro) throws Exception {
        if (filtro == null) {
            throw new Exception("Indica los filtros de búsqueda.");
        }
        if (filtro.desde() != null && filtro.hasta() != null && filtro.desde().isAfter(filtro.hasta())) {
            throw new Exception("La fecha \"desde\" no puede ser posterior a la fecha \"hasta\".");
        }
        if (filtro.pagina() < 0) {
            throw new Exception("La página no es válida.");
        }
        if (filtro.tamano() < 1 || filtro.tamano() > TAMANO_MAXIMO_PAGINA) {
            throw new Exception("El tamaño de página debe ser de 1 a " + TAMANO_MAXIMO_PAGINA + ".");
        }
    }

    /** Valida la peticion de cancelacion. */
    static void cancelacion(CancelacionDTO datos) throws Exception {
        if (datos == null || datos.idCompra() == null) {
            throw new Exception("Selecciona la compra que quieres cancelar.");
        }
        String motivo = datos.motivo() == null ? "" : datos.motivo().strip();
        if (motivo.length() < MOTIVO_MINIMO || motivo.length() > MOTIVO_MAXIMO) {
            throw new Exception("El motivo de cancelación debe tener de "
                    + MOTIVO_MINIMO + " a " + MOTIVO_MAXIMO + " caracteres.");
        }
    }
}
