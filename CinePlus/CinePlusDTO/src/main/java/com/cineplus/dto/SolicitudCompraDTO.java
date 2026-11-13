package com.cineplus.dto;

import java.util.List;

/**
 * Lo que la pantalla de pago manda para comprar. El usuario se toma de la sesion.
 *
 * @author luisf
 */
public record SolicitudCompraDTO(
        Integer idFuncion,
        List<Integer> idsAsientos,
        List<String> productos,
        DatosPagoDTO pago) {
}