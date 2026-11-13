package com.cineplus.negocio.compra.pago;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.entidades.enums.MetodoPago;

/**
 * PATRON STRATEGY. Pago cuando los puntos cubren todo el total.
 *
 * @author luisf
 */
public class PagoPuntos implements IEstrategiaPago {

    @Override
    public ResultadoPago cobrar(double monto, DatosPagoDTO datos) throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: PagoPuntos.cobrar");
    }

    @Override
    public MetodoPago metodo() {
        return MetodoPago.PUNTOS;
    }
}
