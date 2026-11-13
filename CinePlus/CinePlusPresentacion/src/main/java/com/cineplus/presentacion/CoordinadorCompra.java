package com.cineplus.presentacion;

import com.cineplus.dto.AsientoDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.FuncionDTO;

import java.util.List;

/**
 * Coordinador del modulo. Pago de boletos y confirmacion de la compra.
 * Esqueleto del Flujo 0 con los metodos de entrada que llaman otros modulos. Mientras
 * su duenio no los implementa muestran "proximamente". Usa app.cambiar(), app.ejecutar(),
 * app.fachada() y app.confirmar().
 *
 * @author luisf
 */
public final class CoordinadorCompra {

    private final CoordinadorAplicacion app;

    public CoordinadorCompra(CoordinadorAplicacion app) {
        this.app = app;
    }

    /** Cambia a FrmPago y pide la cotizacion. */
    public void abrirPago(FuncionDTO funcion, List<AsientoDTO> asientos) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Pago de boletos");
    }

    /** Cambia a FrmConfirmacionCompra. */
    public void abrirConfirmacion(CompraDTO compra) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Confirmación de compra");
    }

    /** Regresa a la seleccion de asientos (por ejemplo, si el asiento ya se vendio). */
    public void volverAAsientos(FuncionDTO funcion) {
        app.cartelera().abrirAsientos(funcion);
    }
}
