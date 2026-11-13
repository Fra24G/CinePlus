package com.cineplus.negocio.compra.pago;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.entidades.enums.MetodoPago;

/**
 * PATRON STRATEGY. Pago simulado con tarjeta. Nunca guarda el numero completo ni el CVV.
 *
 * @author luisf
 */
public class PagoTarjeta implements IEstrategiaPago {

    @Override
    public ResultadoPago cobrar(double monto, DatosPagoDTO datos) throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: PagoTarjeta.cobrar");
    }

    @Override
    public MetodoPago metodo() {
        return MetodoPago.TARJETA;
    }
}
