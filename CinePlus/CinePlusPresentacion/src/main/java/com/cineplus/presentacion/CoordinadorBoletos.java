package com.cineplus.presentacion;

import com.cineplus.dto.BoletoDTO;
import com.cineplus.dto.CancelacionDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.FiltroComprasDTO;
import com.cineplus.presentacion.formularios.DlgCancelacion;
import com.cineplus.presentacion.formularios.FrmBoletos;
import com.cineplus.presentacion.formularios.FrmDetalleCompra;
import com.cineplus.presentacion.formularios.FrmHistorial;
import com.cineplus.presentacion.formularios.admin.FrmTransacciones;

/**
 * Coordinador del modulo. Boletos con QR, historial de compras, detalle, cancelacion y transacciones.
 * Usa app.cambiar(), app.ejecutar(), app.fachada() y app.confirmar().
 *
 * @author luisf
 * @author Mario Perez
 */
public final class CoordinadorBoletos {

    /** Boletos por pagina en "Mis boletos". */
    private static final int BOLETOS_POR_PAGINA = 4;
    private static final String MENSAJE_EXITO = "¡Se realizó la operación con éxito!";

    private final CoordinadorAplicacion app;

    public CoordinadorBoletos(CoordinadorAplicacion app) {
        this.app = app;
    }

    // ---------- Boletos ----------

    /** Cambia a FrmBoletos; la vista pide la pagina 0. */
    public void abrirBoletos() {
        app.cambiar(new FrmBoletos(app));
    }

    /** fachada.listarBoletos(pagina, 4) -> vista.mostrarBoletos. */
    public void cargarBoletos(FrmBoletos vista, int pagina) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().listarBoletos(pagina, BOLETOS_POR_PAGINA),
                boletos -> {
                    vista.ocupado(false);
                    vista.mostrarBoletos(boletos);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    /**
     * Imagen PNG del QR del boleto. Lo llama TarjetaBoleto en segundo plano.
     * Si falla devuelve un arreglo vacio para no romper la pantalla.
     */
    public byte[] qr(BoletoDTO boleto) {
        try {
            return app.fachada().generarQR(boleto);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    // ---------- Historial ----------

    /** Cambia a FrmHistorial; la vista carga con su filtro inicial. */
    public void abrirHistorial() {
        app.cambiar(new FrmHistorial(app));
    }

    /** fachada.consultarHistorial -> vista.mostrarCompras. */
    public void cargarHistorial(FrmHistorial vista, FiltroComprasDTO filtro) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().consultarHistorial(filtro),
                compras -> {
                    vista.ocupado(false);
                    vista.mostrarCompras(compras);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    // ---------- Detalle y cancelacion ----------

    /** Cambia a FrmDetalleCompra(idCompra); la vista pide cargarCompra. */
    public void abrirDetalle(Integer idCompra) {
        if (idCompra == null) {
            return;
        }
        app.cambiar(new FrmDetalleCompra(app, idCompra));
    }

    /** fachada.obtenerCompra -> vista.mostrarCompra. */
    public void cargarCompra(FrmDetalleCompra vista, Integer idCompra) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().obtenerCompra(idCompra),
                compra -> {
                    vista.ocupado(false);
                    vista.mostrarCompra(compra);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    /**
     * Abre DlgCancelacion; si el usuario escribe el motivo y confirma, pide la
     * cancelacion con cancelarCompra.
     */
    public void pedirCancelacion(FrmDetalleCompra vista, CompraDTO compra) {
        if (compra == null) {
            return;
        }
        new DlgCancelacion(vista, compra).mostrarYLeerMotivo().ifPresent(motivo ->
                cancelarCompra(vista, new CancelacionDTO(compra.idCompra(), motivo)));
    }

    /**
     * fachada.cancelarCompra -> pantalla de exito y despues el historial.
     */
    public void cancelarCompra(FrmDetalleCompra vista, CancelacionDTO datos) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().cancelarCompra(datos),
                compra -> app.confirmar(MENSAJE_EXITO, this::abrirHistorial),
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    // ---------- Transacciones (admin) ----------

    /** Cambia a FrmTransacciones (lo llama la navegacion del admin). */
    public void abrirTransacciones() {
        app.cambiar(new FrmTransacciones(app));
    }

    /** fachada.listarTransacciones -> vista.mostrarTransacciones. */
    public void cargarTransacciones(FrmTransacciones vista, FiltroComprasDTO filtro) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().listarTransacciones(filtro),
                transacciones -> {
                    vista.ocupado(false);
                    vista.mostrarTransacciones(transacciones);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }
}
