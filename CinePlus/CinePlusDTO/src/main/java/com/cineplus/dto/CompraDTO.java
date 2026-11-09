package com.cineplus.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Una compra para el historial y el detalle. El estado puede ser Aprobado, Denegado o Cancelado.
 *
 * @author luisf
 */
public record CompraDTO(
        Integer idCompra,
        LocalDateTime fechaCompra,
        String tituloPelicula,
        String cine,
        int numeroSala,
        String tipoFuncion,
        LocalDateTime fechaHoraFuncion,
        double subtotal,
        double cargoServicio,
        double montoTotal,
        int puntosUsados,
        double descuentoPuntos,
        double montoCobrado,
        int puntosGanados,
        String metodoPago,
        String ultimosDigitos,
        String estado,
        String motivoCancelacion,
        List<String> productos,
        List<BoletoDTO> boletos,
        boolean cancelable) {
}