package com.cineplus.negocio.compra.pago;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.entidades.enums.MetodoPago;

/**
 * Simulación de pago con apple o google pay, siempre se aprueba porque es una prueba
 * 
 * @author yakor
 */
public class PagoBilletera implements IEstrategiaPago {

    @Override
    public ResultadoPago cobrar(double monto, DatosPagoDTO datos) throws Exception {
        return new ResultadoPago(true, MetodoPago.APPLE_GOOGLE_PAY, null, "Pago aprobado");
    }

    @Override
    public MetodoPago metodo() {
        return MetodoPago.APPLE_GOOGLE_PAY;
    }
}
