package com.cineplus.presentacion;

/**
 * Coordinador del modulo. Boletos con QR, historial de compras, detalle, cancelacion y transacciones.
 * Esqueleto del Flujo 0 con los metodos de entrada que llaman otros modulos. Mientras
 * su duenio no los implementa muestran "proximamente". Usa app.cambiar(), app.ejecutar(),
 * app.fachada() y app.confirmar().
 *
 * @author luisf
 */
public final class CoordinadorBoletos {

    private final CoordinadorAplicacion app;

    public CoordinadorBoletos(CoordinadorAplicacion app) {
        this.app = app;
    }

    /** Cambia a FrmBoletos y carga la pagina 0. */
    public void abrirBoletos() {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Mis boletos");
    }

    /** Cambia a FrmHistorial y carga con el filtro inicial. */
    public void abrirHistorial() {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Historial de compras");
    }

    /** Cambia a FrmDetalleCompra(idCompra). */
    public void abrirDetalle(Integer idCompra) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Detalle de compra");
    }

    /** Cambia a FrmTransacciones (lo llama la navegacion del admin). */
    public void abrirTransacciones() {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Transacciones");
    }
}
