package com.cineplus.presentacion;

import com.cineplus.dto.ActualizarPerfilDTO;
import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.PlanDTO;
import com.cineplus.presentacion.formularios.FrmMembresia;
import com.cineplus.presentacion.formularios.FrmPagoMembresia;
import com.cineplus.presentacion.formularios.FrmPerfil;

import java.util.List;

/**
 * Coordinador del modulo Cuenta: perfil, membresia CinePLUS Pro y acceso a la vista
 * de administrador. Usa app.cambiar(), app.ejecutar(), app.fachada() y app.confirmar().
 *
 * @author Luis Guillermo Franco Murrieta
 */
public final class CoordinadorCuenta {

    private static final String MENSAJE_EXITO = "¡Se realizó la operación con éxito!";
    private static final String MENSAJE_PRO = "¡Ya eres Pro! Bienvenido";

    private final CoordinadorAplicacion app;

    public CoordinadorCuenta(CoordinadorAplicacion app) {
        this.app = app;
    }

    // ---------- Perfil ----------

    /** Cambia a FrmPerfil y llama a cargarPerfil. */
    public void abrirPerfil() {
        FrmPerfil vista = new FrmPerfil(app);
        app.cambiar(vista);
        cargarPerfil(vista);
    }

    /** En segundo plano fachada.obtenerPerfil() y lo entrega a la vista. */
    public void cargarPerfil(FrmPerfil vista) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().obtenerPerfil(),
                perfil -> {
                    vista.ocupado(false);
                    vista.mostrarPerfil(perfil);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    /** Guarda el perfil; si sale bien muestra la confirmacion y regresa al perfil. */
    public void actualizarPerfil(FrmPerfil vista, ActualizarPerfilDTO datos) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().actualizarPerfil(datos),
                usuario -> app.confirmar(MENSAJE_EXITO, this::abrirPerfil),
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    // ---------- Membresia ----------

    /** Cambia a FrmMembresia y llama a cargarPlanes. */
    public void abrirMembresia() {
        FrmMembresia vista = new FrmMembresia(app);
        app.cambiar(vista);
        cargarPlanes(vista);
    }

    /** fachada.listarPlanes() y fachada.obtenerPerfil() → vista.mostrarPlanes(planes, pro). */
    public void cargarPlanes(FrmMembresia vista) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> new Planes(app.fachada().listarPlanes(), app.fachada().obtenerPerfil().pro()),
                planes -> {
                    vista.mostrarPlanes(planes.planes(), planes.pro());
                    vista.ocupado(false);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    /** Si el plan es gratuito no hace nada (ya lo tiene); si es Pro cambia a FrmPagoMembresia. */
    public void abrirPagoMembresia(PlanDTO plan) {
        if (plan == null || plan.gratuito()) {
            return;
        }
        app.cambiar(new FrmPagoMembresia(app, plan));
    }

    /**
     * Cobra y contrata Pro. Si sale bien muestra "¡Ya eres Pro!" y regresa al inicio;
     * si falla muestra el mensaje. Siempre limpia los datos de tarjeta.
     */
    public void contratarPro(FrmPagoMembresia vista, DatosPagoDTO pago) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().contratarPro(pago),
                membresia -> {
                    vista.limpiarTarjeta();
                    app.confirmar(MENSAJE_PRO, app::inicio);
                },
                mensaje -> {
                    // Mientras no se integre Dev 3 llega "Pendiente: FabricaPago.para"
                    vista.limpiarTarjeta();
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    // ---------- Administrador ----------

    /** Abre el panel del administrador. */
    public void abrirVistaAdmin() {
        // TODO: Conectar cuando se integre Dev 5 (CoordinadorAdmin.abrirPanel muestra "proximamente" hasta entonces)
        app.admin().abrirPanel();
    }

    /** Planes y si el usuario ya es Pro, cargados juntos en segundo plano. */
    private record Planes(List<PlanDTO> planes, boolean pro) {
    }
}
