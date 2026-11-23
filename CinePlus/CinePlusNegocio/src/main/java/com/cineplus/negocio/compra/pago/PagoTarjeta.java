package com.cineplus.negocio.compra.pago;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.DatosTarjetaDTO;
import com.cineplus.entidades.enums.MetodoPago;
import java.time.YearMonth;

/**
 * Pago simulado con tarjeta. Nunca se guarda el numero completo ni el CVV.
 *
 * @author yakor
 */
public class PagoTarjeta implements IEstrategiaPago {

    /**
     * Se rechazan tarjetas con terminación en 0000.
     */
    private static final String TERMINACION_DENEGADA = "0000";

    @Override
    public ResultadoPago cobrar(double monto, DatosPagoDTO datos) throws Exception {
        DatosTarjetaDTO tarjeta = datos == null ? null : datos.tarjeta();
        if (tarjeta == null) {
            throw new Exception("Captura los datos de tu tarjeta.");
        }

        String titular = tarjeta.nombreTitular() == null ? "" : tarjeta.nombreTitular().trim();
        if (titular.isEmpty()) {
            throw new Exception("Escribe el nombre del titular de la tarjeta.");
        }

        String numero = tarjeta.numero() == null ? "" : tarjeta.numero().replaceAll("[\\s-]", "");
        if (!numero.matches("\\d{13,19}") || !luhn(numero)) {
            throw new Exception("El número de tarjeta no es válido.");
        }

        if (!vigente(tarjeta.vencimiento())) {
            throw new Exception("La fecha de vencimiento no es válida o la tarjeta ya venció.");
        }

        String cvv = tarjeta.cvv() == null ? "" : tarjeta.cvv().trim();
        if (!cvv.matches("\\d{3,4}")) {
            throw new Exception("El CVV debe tener 3 o 4 dígitos.");
        }

        String ultimos = numero.substring(numero.length() - 4);
        if (TERMINACION_DENEGADA.equals(ultimos)) {
            return new ResultadoPago(false, MetodoPago.TARJETA, ultimos,
                    "Tu banco rechazó el pago. Intenta con otra tarjeta.");
        }
        return new ResultadoPago(true, MetodoPago.TARJETA, ultimos, "Pago aprobado");
    }

    @Override
    public MetodoPago metodo() {
        return MetodoPago.TARJETA;
    }

    /** utilizamos el algoritmo de luhn para comprobar el digito verificador de la tarjeta */
    private static boolean luhn(String numero) {
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }

    /** true si el vencimiento tiene formato MM/YY y el mes todavia no termina. */
    private static boolean vigente(String mmYY) {
        if (mmYY == null || !mmYY.trim().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return false;
        }
        String[] partes = mmYY.trim().split("/");
        int mes = Integer.parseInt(partes[0]);
        int anio = 2000 + Integer.parseInt(partes[1]);
        return !YearMonth.of(anio, mes).isBefore(YearMonth.now());
    }
}
