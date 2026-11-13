package com.cineplus.negocio.compra.pago;

import com.cineplus.dto.DatosPagoDTO;

/**
 * Elige la estrategia de pago segun el metodo elegido y el monto a cobrar.
 *
 * @author luisf
 */
public final class FabricaPago {

    private FabricaPago() {
    }

    /** monto 0 = PagoPuntos; TARJETA = PagoTarjeta; APPLE_GOOGLE_PAY = PagoBilletera. */
    public static IEstrategiaPago para(DatosPagoDTO datos, double monto) throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: FabricaPago.para");
    }
}
