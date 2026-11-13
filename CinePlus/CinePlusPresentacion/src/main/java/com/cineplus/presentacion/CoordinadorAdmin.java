package com.cineplus.presentacion;

import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.PeliculaDTO;

/**
 * Coordinador del modulo. Panel de administrador: peliculas, funciones y transacciones.
 * Esqueleto del Flujo 0 con los metodos de entrada que llaman otros modulos. Mientras
 * su duenio no los implementa muestran "proximamente". Usa app.cambiar(), app.ejecutar(),
 * app.fachada() y app.confirmar().
 *
 * @author luisf
 */
public final class CoordinadorAdmin {

    private final CoordinadorAplicacion app;

    public CoordinadorAdmin(CoordinadorAplicacion app) {
        this.app = app;
    }

    /** Si la sesion no es ADMIN muestra un aviso; si lo es, cambia a FrmPanelAdmin y carga el resumen. */
    public void abrirPanel() {
        if (app.sesion() == null || !app.sesion().usuario().esAdmin()) {
            app.avisar("Solo un administrador puede entrar a esta sección.");
            return;
        }
        app.proximamente("Panel de administrador");
    }

    /** Cambia a FrmPelicula en modo agregar. */
    public void abrirAgregarPelicula() {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Agregar película");
    }

    /** Cambia a FrmPelicula en modo editar. */
    public void abrirEditarPelicula(PeliculaDTO pelicula) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Editar película");
    }

    /** Cambia a FrmFuncion (funcion null = nueva). */
    public void abrirFunciones(PeliculaDTO pelicula, FuncionDTO funcion) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Funciones");
    }

    /** Las transacciones las muestra el modulo de boletos. */
    public void abrirTransacciones() {
        app.boletos().abrirTransacciones();
    }
}
