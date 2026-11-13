package com.cineplus.presentacion;

import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.PeliculaDTO;

/**
 * Coordinador del modulo. Cartelera, seleccion de funcion y de asientos.
 * Esqueleto del Flujo 0 con los metodos de entrada que llaman otros modulos. Mientras
 * su duenio no los implementa muestran "proximamente". Usa app.cambiar(), app.ejecutar(),
 * app.fachada() y app.confirmar().
 *
 * @author luisf
 */
public final class CoordinadorCartelera {

    private final CoordinadorAplicacion app;

    public CoordinadorCartelera(CoordinadorAplicacion app) {
        this.app = app;
    }

    /** Cambia a FrmCartelera y llama a cargarCartelera. */
    public void abrir() {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Cartelera");
    }

    /** Cambia a FrmSeleccionFuncion(pelicula) y llama a cargarCines. */
    public void abrirFunciones(PeliculaDTO pelicula) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Funciones de " + pelicula.titulo());
    }

    /** Cambia a FrmSeleccionAsientos(funcion) y llama a cargarAsientos. */
    public void abrirAsientos(FuncionDTO funcion) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Selección de asientos");
    }
}
