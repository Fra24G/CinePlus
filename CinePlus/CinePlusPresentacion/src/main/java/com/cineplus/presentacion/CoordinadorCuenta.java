package com.cineplus.presentacion;

import com.cineplus.dto.PlanDTO;

/**
 * Coordinador del modulo. Perfil, membresia CinePLUS Pro y acceso a la vista de administrador.
 * Esqueleto del Flujo 0 con los metodos de entrada que llaman otros modulos. Mientras
 * su duenio no los implementa muestran "proximamente". Usa app.cambiar(), app.ejecutar(),
 * app.fachada() y app.confirmar().
 *
 * @author luisf
 */
public final class CoordinadorCuenta {

    private final CoordinadorAplicacion app;

    public CoordinadorCuenta(CoordinadorAplicacion app) {
        this.app = app;
    }

    /** Cambia a FrmPerfil y llama a cargarPerfil. */
    public void abrirPerfil() {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Mi perfil");
    }

    /** Cambia a FrmMembresia y llama a cargarPlanes. */
    public void abrirMembresia() {
        // Pendiente (lo implementa su duenio)
        app.proximamente("CinePLUS Pro");
    }

    /** Si el plan es gratuito no hace nada; si es Pro cambia a FrmPagoMembresia. */
    public void abrirPagoMembresia(PlanDTO plan) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Pago de membresía");
    }

    /** Abre el panel del administrador. */
    public void abrirVistaAdmin() {
        app.admin().abrirPanel();
    }
}
