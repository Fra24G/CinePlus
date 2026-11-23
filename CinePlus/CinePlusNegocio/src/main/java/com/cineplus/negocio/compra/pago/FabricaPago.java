package com.cineplus.negocio.compra.pago;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.negocio.compra.ReglasPrecio;

/**
 * elige la estrategia de pago segun el metodo elegido y el monto a cobrar
 *
 * @author yakor
 */
public final class FabricaPago {

    private FabricaPago() {
    }

    public static IEstrategiaPago para(DatosPagoDTO datos, double monto) throws Exception {
        if (datos == null) {
            throw new Exception("Elige un método de pago.");
        }
        if (ReglasPrecio.redondear(monto) <= 0) {
            return new PagoPuntos();
        }
        if (datos.metodo() == null) {
            throw new Exception("Elige un método de pago.");
        }
        return switch (datos.metodo()) {
            case TARJETA -> new PagoTarjeta();
            case APPLE_GOOGLE_PAY -> new PagoBilletera();
        };
    }
}
