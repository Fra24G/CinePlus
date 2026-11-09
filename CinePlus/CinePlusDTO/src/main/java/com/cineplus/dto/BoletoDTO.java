package com.cineplus.dto;

import java.time.LocalDateTime;

/**
 * Un boleto, con lo necesario para mostrarlo y generar su QR (el QR lleva el codigo).
 *
 * @author luisf
 */
public record BoletoDTO(
        Integer idBoleto,
        Integer idCompra,
        String codigo,
        double precio,
        String tituloPelicula,
        String cine,
        int numeroSala,
        String tipoFuncion,
        LocalDateTime fechaHoraFuncion,
        String asiento) {
}