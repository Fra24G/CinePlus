package com.cineplus.negocio.compra.pago;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.entidades.enums.MetodoPago;
import com.cineplus.negocio.compra.ReglasPrecio;

/**
 * Pago cuando los puntos cubren todo el total.
 *
 * @author yakor
 */
public class PagoPuntos implements IEstrategiaPago {

    @Override
    public ResultadoPago cobrar(double monto, DatosPagoDTO datos) throws Exception {
        if (ReglasPrecio.redondear(monto) > 0) {
            throw new Exception("Tus puntos no cubren el total.");
        }
        return new ResultadoPago(true, MetodoPago.PUNTOS, null, "Pago aprobado");
    }

    @Override
    public MetodoPago metodo() {
        return MetodoPago.PUNTOS;
    }
}
