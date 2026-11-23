package com.cineplus.negocio.compra;

import com.cineplus.dto.CotizacionDTO;
import com.cineplus.entidades.Asiento;
import com.cineplus.entidades.Boleto;
import com.cineplus.entidades.Compra;
import com.cineplus.entidades.Funcion;
import com.cineplus.entidades.Usuario;
import com.cineplus.entidades.enums.EstadoPago;
import com.cineplus.negocio.compra.pago.ResultadoPago;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * se arma una compra con los datos necesarios usando un builder y ReglasPrecio
 * 
 * @author yakor
 */
public class CompraBuilder {

    private final Usuario usuario;
    private final Funcion funcion;
    private List<Asiento> asientos = new ArrayList<>();
    private List<String> productos = new ArrayList<>();
    private boolean pro;
    private int puntosUsados;
    private ResultadoPago pago;

    /** 
     * Datos que debe tener una compra.
     */
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

    /** 
     * calculo de montos y puntos sin crear nada 
     */
    public CotizacionDTO cotizacion(int puntosDisponibles) throws Exception {
        double subtotal = subtotal();
        double cargo = ReglasPrecio.cargoServicio(pro);
        double total = ReglasPrecio.redondear(subtotal + cargo);

        int usables = ReglasPrecio.puntosUsables(puntosDisponibles, total);
        double descuento = ReglasPrecio.valorPuntos(usables);
        double conPuntos = ReglasPrecio.redondear(Math.max(0, total - descuento));

        // Los puntos a ganar dependen de si en esta solicitud se pidio usar el saldo
        int aGanar = ReglasPrecio.puntosGanados(montoACobrar(), pro);

        return new CotizacionDTO(subtotal, cargo, total, Math.max(0, puntosDisponibles),
                usables, descuento, conPuntos, aGanar, pro);
    }

    /** 
     * El total menos el valor de los puntos que fueron utilizados que es nunca menor de 0.
     */
    public double montoACobrar() throws Exception {
        exigirFuncionYAsientos();
        double total = total();
        return ReglasPrecio.redondear(Math.max(0, total - ReglasPrecio.valorPuntos(puntosAplicados(total))));
    }

    /** 
     * se crea la compra con un boleto por asiento pidiendo esos asientos y el pago aprobado. 
     */
    public Compra construir() throws Exception {
        exigirFuncionYAsientos();
        if (pago == null || !pago.aprobado()) {
            throw new Exception("No se puede registrar la compra sin un pago aprobado.");
        }

        Compra compra = new Compra();
        compra.setUsuario(usuario);
        compra.setFuncion(funcion);
        compra.setFechaCompra(LocalDateTime.now());

        for (Asiento asiento : asientos) {
            Boleto boleto = new Boleto();
            boleto.setFuncion(funcion);
            boleto.setAsiento(asiento);
            boleto.setPrecio(ReglasPrecio.precioAsiento(asiento.getTipo()));
            boleto.setCodigo("CP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            compra.agregarBoleto(boleto);
        }

        double total = total();
        int puntos = puntosAplicados(total);
        double cobrado = montoACobrar();

        compra.setSubtotalBoletos(subtotal());
        compra.setCargoServicio(ReglasPrecio.cargoServicio(pro));
        compra.setMontoTotal(total);
        compra.setPuntosUsados(puntos);
        compra.setDescuentoPuntos(ReglasPrecio.valorPuntos(puntos));
        compra.setMontoCobrado(cobrado);
        compra.setPuntosGanados(ReglasPrecio.puntosGanados(cobrado, pro));
        compra.setMetodoPago(pago.metodo());
        compra.setUltimosDigitos(pago.ultimosDigitos());
        compra.setEstadoPago(EstadoPago.APROBADO);
        compra.setListaDeProductos(new ArrayList<>(productos));
        return compra;
    }

    private double subtotal() {
        double subtotal = 0;
        for (Asiento asiento : asientos) {
            subtotal += ReglasPrecio.precioAsiento(asiento.getTipo());
        }
        return ReglasPrecio.redondear(subtotal);
    }

    private double total() {
        return ReglasPrecio.redondear(subtotal() + ReglasPrecio.cargoServicio(pro));
    }

    /** 
     * Puntos pedidos, recortados para que no sean mayores que el total. 
     */
    private int puntosAplicados(double total) {
        return ReglasPrecio.puntosUsables(puntosUsados, total);
    }

    private void exigirFuncionYAsientos() throws Exception {
        if (funcion == null || asientos.isEmpty()) {
            throw new Exception("La compra necesita una función y al menos un asiento.");
        }
    }
}
