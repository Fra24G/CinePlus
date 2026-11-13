package com.cineplus.negocio.compra.pago;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.entidades.enums.MetodoPago;

/**
 * PATRON STRATEGY. Pago simulado con Apple / Google Pay.
 *
 * @author luisf
 */
public class PagoBilletera implements IEstrategiaPago {

    @Override
    public ResultadoPago cobrar(double monto, DatosPagoDTO datos) throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: PagoBilletera.cobrar");
    }

    @Override
    public MetodoPago metodo() {
        return MetodoPago.APPLE_GOOGLE_PAY;
    }
}
