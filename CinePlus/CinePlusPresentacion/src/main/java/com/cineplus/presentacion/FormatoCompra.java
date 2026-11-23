package com.cineplus.presentacion;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Formatos de fechas, horas, montos y metodo de pago que comparten las pantallas
 * de boletos, historial, detalle y transacciones.
 *
 * @author Mario Perez
 */
public final class FormatoCompra {

    private static final Locale ES = Locale.forLanguageTag("es-MX");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", ES);
    private static final DateTimeFormatter FECHA_CAPTURA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu", ES).withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm", ES);
    private static final DateTimeFormatter FECHA_LARGA = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ES);

    private FormatoCompra() {
    }

    /** "05/11/2026". */
    public static String fecha(LocalDateTime fecha) {
        return fecha == null ? "" : FECHA.format(fecha);
    }

    /** "18:30". */
    public static String hora(LocalDateTime fecha) {
        return fecha == null ? "" : HORA.format(fecha);
    }

    /** "05/11/2026 18:30". */
    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "" : fecha(fecha) + " " + hora(fecha);
    }

    /** "Jueves 5 de noviembre". */
    public static String fechaLarga(LocalDateTime fecha) {
        if (fecha == null) {
            return "";
        }
        String texto = FECHA_LARGA.format(fecha);
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    /** "$300" o "$179.50". */
    public static String dinero(double monto) {
        DecimalFormat formato = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));
        if (monto != Math.floor(monto)) {
            formato.applyPattern("#,##0.00");
        }
        return "$" + formato.format(monto);
    }

    /** "... 3845" si se pago con tarjeta; si no, el nombre del metodo. */
    public static String metodo(String metodo, String ultimosDigitos) {
        if (ultimosDigitos != null && !ultimosDigitos.isBlank()) {
            return "... " + ultimosDigitos;
        }
        return metodo == null ? "" : metodo;
    }

    /** "#9081". */
    public static String numeroCompra(Integer idCompra) {
        return idCompra == null ? "" : "#" + idCompra;
    }

    /** Lee una fecha "dd/mm/aaaa". Vacio = null. Si no es valida lanza Exception con el mensaje. */
    public static LocalDate leerFecha(String texto, String campo) throws Exception {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.strip(), FECHA_CAPTURA);
        } catch (DateTimeParseException e) {
            throw new Exception("La fecha \"" + campo + "\" debe tener el formato dd/mm/aaaa.");
        }
    }
}
