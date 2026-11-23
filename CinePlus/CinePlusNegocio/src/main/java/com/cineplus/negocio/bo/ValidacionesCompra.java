package com.cineplus.negocio.bo;

import com.cineplus.dto.SolicitudCompraDTO;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Reglas de validación
 *
 * @author yakor
 */
final class ValidacionesCompra {

    /** 
     * Maximo de asientos en una sola compra. 
     */
    static final int MAX_ASIENTOS = 10;

    private ValidacionesCompra() {
    }

    /**
     * datos de pago y 1 a 10 asientos que no se repiten
     */
    static void solicitud(SolicitudCompraDTO solicitud) throws Exception {
        if (solicitud == null) {
            throw new Exception("Faltan los datos de la compra.");
        }
        if (solicitud.idFuncion() == null) {
            throw new Exception("Elige una función.");
        }

        List<Integer> asientos = solicitud.idsAsientos();
        if (asientos == null || asientos.isEmpty()) {
            throw new Exception("Elige al menos un asiento.");
        }
        if (asientos.size() > MAX_ASIENTOS) {
            throw new Exception("Puedes comprar máximo " + MAX_ASIENTOS + " asientos por compra.");
        }
        if (asientos.stream().anyMatch(Objects::isNull)
                || new HashSet<>(asientos).size() != asientos.size()) {
            throw new Exception("No puedes elegir el mismo asiento dos veces.");
        }

        if (solicitud.pago() == null) {
            throw new Exception("Elige un método de pago.");
        }
    }
}
