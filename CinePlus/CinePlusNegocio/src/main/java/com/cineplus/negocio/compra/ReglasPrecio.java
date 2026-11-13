package com.cineplus.negocio.compra;

import com.cineplus.entidades.enums.TipoAsiento;
import java.time.LocalDateTime;

/**
 * Reglas de precios y puntos de CinePlus en un solo lugar.
 * Nadie escribe estos numeros en otra parte: los montos y puntos siempre salen de aqui.
 *
 * @author luisf
 */
public final class ReglasPrecio {

    public static final double PRECIO_CLASICO = 79.0;
    // Pendiente de definir por el equipo: mientras tanto cuesta lo mismo que el Clasico
    public static final double PRECIO_VIP = 79.0;
    public static final double CARGO_SERVICIO = 21.0;
    public static final double PESOS_POR_PUNTO = 10.0;
    public static final double VALOR_PUNTO = 0.10;
    public static final int MULTIPLICADOR_PRO = 3;
    public static final long HORAS_LIMITE_CANCELACION = 2;

    private ReglasPrecio() {
    }

    /** Precio de un asiento segun su tipo. */
    public static double precioAsiento(TipoAsiento tipo) {
        return tipo == TipoAsiento.VIP ? PRECIO_VIP : PRECIO_CLASICO;
    }

    /** Cargo por servicio de una compra: $21, o $0 si el usuario es CinePLUS Pro. */
    public static double cargoServicio(boolean pro) {
        return pro ? 0.0 : CARGO_SERVICIO;
    }

    /** 1 punto por cada $10 cobrados (redondeo hacia abajo); el triple si es Pro. */
    public static int puntosGanados(double montoCobrado, boolean pro) {
        if (montoCobrado <= 0) {
            return 0;
        }
        int base = (int) Math.floor(redondear(montoCobrado) / PESOS_POR_PUNTO);
        return pro ? base * MULTIPLICADOR_PRO : base;
    }

    /** Valor en pesos de una cantidad de puntos (cada punto vale $0.10). */
    public static double valorPuntos(int puntos) {
        return redondear(Math.max(0, puntos) * VALOR_PUNTO);
    }

    /** Maximo de puntos que se pueden usar sin pasarse del total. */
    public static int puntosUsables(int disponibles, double total) {
        if (disponibles <= 0 || total <= 0) {
            return 0;
        }
        // Se redondea antes del floor para evitar errores de decimales (179 / 0.10 = 1789.999...)
        int maximo = (int) Math.floor(redondear(total / VALOR_PUNTO));
        return Math.min(disponibles, maximo);
    }

    /** true si faltan 2 horas o mas para la funcion. */
    public static boolean esCancelable(LocalDateTime funcion, LocalDateTime ahora) {
        return funcion != null && ahora != null
                && !ahora.plusHours(HORAS_LIMITE_CANCELACION).isAfter(funcion);
    }

    /** Redondea un monto a 2 decimales. */
    public static double redondear(double monto) {
        return Math.round(monto * 100.0) / 100.0;
    }
}
