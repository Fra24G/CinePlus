package com.cineplus.dto;

import java.util.List;

/**
 * Una pagina de resultados. pagina empieza en 0.
 *
 * @author luisf
 */
public record PaginaDTO<T>(
        List<T> elementos,
        int pagina,
        int tamano,
        long total) {

    public int totalPaginas() {
        return tamano <= 0 ? 0 : (int) ((total + tamano - 1) / tamano);
    }
}