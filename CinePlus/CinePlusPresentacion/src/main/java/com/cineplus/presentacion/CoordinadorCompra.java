package com.cineplus.presentacion;

import com.cineplus.dto.AsientoDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.SolicitudCompraDTO;
import com.cineplus.presentacion.formularios.FrmConfirmacionCompra;
import com.cineplus.presentacion.formularios.FrmPago;
import java.util.List;
import java.util.Objects;

/**
 * coordinador del pago de boletos y la confirmacion de la compra
 *
 * @author yakor
 */
public final class CoordinadorCompra {

    private final CoordinadorAplicacion app;
    private PeliculaDTO pelicula;

    public CoordinadorCompra(CoordinadorAplicacion app) {
        this.app = app;
    }

    /**
     * Cambia a FrmPago, que pide la cotizacion al abrirse.
     */
    public void abrirPago(FuncionDTO funcion, List<AsientoDTO> asientos) {
        if (funcion == null || asientos == null || asientos.isEmpty()) {
            return;
        }
        pelicula = null;
        FrmPago vista = new FrmPago(app, funcion, asientos);
        app.cambiar(vista);
        cargarPelicula(vista, funcion.idPelicula());
    }

    public void cotizar(FrmPago vista, SolicitudCompraDTO solicitud) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().cotizar(solicitud),
                cotizacion -> {
                    vista.ocupado(false);
                    vista.mostrarCotizacion(cotizacion);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    public void comprar(FrmPago vista, SolicitudCompraDTO solicitud) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().comprar(solicitud),
                compra -> {
                    vista.limpiarTarjeta();
                    abrirConfirmacion(compra);
                },
                mensaje -> {
                    vista.limpiarTarjeta();
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    /**
     * Cambia a FrmConfirmacionCompra.
     */
    public void abrirConfirmacion(CompraDTO compra) {
        FrmConfirmacionCompra vista = new FrmConfirmacionCompra(app, compra);
        app.cambiar(vista);
        if (pelicula != null) {
            vista.mostrarPelicula(pelicula);
        }
    }

    /**
     * Regresa a la seleccion de asientos, por ejemplo si el asiento ya se vendio.
     */
    public void volverAAsientos(FuncionDTO funcion) {
        app.cartelera().abrirAsientos(funcion);
    }

    /**
     * busca la pelicula de la funcion para mostrar su poster, si falla la pantalla se queda sin el poster
     */
    private void cargarPelicula(FrmPago vista, Integer idPelicula) {
        app.ejecutar(
                vista,
                () -> app.fachada().consultarCarteleraLocal().stream()
                        .filter(p -> Objects.equals(p.idPelicula(), idPelicula))
                        .findFirst()
                        .orElse(null),
                encontrada -> {
                    pelicula = encontrada;
                    if (encontrada != null) {
                        vista.mostrarPelicula(encontrada);
                    }
                },
                mensaje -> pelicula = null);
    }
}
