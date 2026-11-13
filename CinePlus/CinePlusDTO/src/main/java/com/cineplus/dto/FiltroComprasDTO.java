package com.cineplus.dto;

import java.time.LocalDate;

/**
 * Filtros y paginacion del historial y de transacciones.
 * Un filtro en null se ignora. pagina empieza en 0.
 *
 * @author luisf
 */
public record FiltroComprasDTO(
        String texto,
        LocalDate desde,
        LocalDate hasta,
        String estado,
        int pagina,
        int tamano) {
}