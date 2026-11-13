package com.cineplus.negocio.compra.pago;

import com.cineplus.entidades.enums.MetodoPago;

/**
 * Resultado de intentar un cobro. ultimosDigitos solo aplica a tarjeta.
 *
 * @author luisf
 */
public record ResultadoPago(
        boolean aprobado,
        MetodoPago metodo,
        String ultimosDigitos,
        String mensaje) {
}
