package com.cineplus.negocio.compra;

import com.cineplus.dto.CotizacionDTO;
import com.cineplus.entidades.Asiento;
import com.cineplus.entidades.Compra;
import com.cineplus.entidades.Funcion;
import com.cineplus.entidades.Usuario;
import com.cineplus.negocio.compra.pago.ResultadoPago;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRON BUILDER. Arma paso a paso una Compra con todos sus datos.
 * Usa ReglasPrecio para todos los montos.
 *
 * @author luisf
 */
public class CompraBuilder {

    private final Usuario usuario;
    private final Funcion funcion;
    private List<Asiento> asientos = new ArrayList<>();
    private List<String> productos = new ArrayList<>();
    private boolean pro;
    private int puntosUsados;
    private ResultadoPago pago;

    /** Datos obligatorios de cualquier compra. */
    public CompraBuilder(Usuario usuario, Funcion funcion) {
        this.usuario = usuario;
        this.funcion = funcion;
    }

    public CompraBuilder conAsientos(List<Asiento> asientos) {
        this.asientos = new ArrayList<>(asientos);
        return this;
    }

    public CompraBuilder conProductos(List<String> productos) {
        this.productos = productos == null ? new ArrayList<>() : new ArrayList<>(productos);
        return this;
    }

    public CompraBuilder comoMiembroPro(boolean pro) {
        this.pro = pro;
        return this;
    }

    public CompraBuilder usandoPuntos(int puntos) {
        this.puntosUsados = Math.max(0, puntos);
        return this;
    }

    public CompraBuilder conPago(ResultadoPago pago) {
        this.pago = pago;
        return this;
    }

    /** Calcula montos y puntos sin crear nada. */
    public CotizacionDTO cotizacion(int puntosDisponibles) throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: CompraBuilder.cotizacion");
    }

    /** Total menos el valor de los puntos usados (nunca menor a 0). */
    public double montoACobrar() throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: CompraBuilder.montoACobrar");
    }

    /** Crea la Compra con un Boleto por asiento. Exige asientos y pago aprobado. */
    public Compra construir() throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: CompraBuilder.construir");
    }
}
