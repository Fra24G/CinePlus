package com.cineplus.negocio.compra.pago;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.entidades.enums.MetodoPago;

/**
 * PATRON STRATEGY. Una forma de cobrar por clase: tarjeta, billetera o puntos.
 *
 * @author luisf
 */
public interface IEstrategiaPago {

    /** Intenta cobrar el monto con los datos recibidos y devuelve si se aprobo. */
    ResultadoPago cobrar(double monto, DatosPagoDTO datos) throws Exception;

    /** Metodo de pago que representa esta estrategia. */
    MetodoPago metodo();
}
