package com.cineplus.dto;

/**
 * Peticion para cancelar una compra. El motivo es obligatorio.
 *
 * @author luisf
 */
public record CancelacionDTO(
        Integer idCompra,
        String motivo) {
}