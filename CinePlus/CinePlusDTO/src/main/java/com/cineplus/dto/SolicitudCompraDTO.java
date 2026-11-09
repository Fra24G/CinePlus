package com.cineplus.dto;

import java.util.List;

/**
 * Lo que la pantalla manda para comprar: quien, que funcion, que asientos y como paga.
 *
 * @author luisf
 */
public record SolicitudCompraDTO(
        Integer idUsuario,
        Integer idFuncion,
        List<Integer> idsAsientos,
        List<String> productos,
        DatosPagoDTO pago) {
}