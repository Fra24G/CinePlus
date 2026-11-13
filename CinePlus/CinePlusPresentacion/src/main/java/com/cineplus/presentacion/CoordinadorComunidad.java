package com.cineplus.presentacion;

import com.cineplus.dto.PeliculaDTO;

/**
 * Coordinador del modulo. Comunidad, resenas de una pelicula y publicar resena.
 * Esqueleto del Flujo 0 con los metodos de entrada que llaman otros modulos. Mientras
 * su duenio no los implementa muestran "proximamente". Usa app.cambiar(), app.ejecutar(),
 * app.fachada() y app.confirmar().
 *
 * @author luisf
 */
public final class CoordinadorComunidad {

    private final CoordinadorAplicacion app;

    public CoordinadorComunidad(CoordinadorAplicacion app) {
        this.app = app;
    }

    /** Cambia a FrmComunidad y carga la pagina 0. */
    public void abrir() {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Comunidad");
    }

    /** Cambia a FrmResenasPelicula(pelicula). */
    public void abrirResenas(PeliculaDTO pelicula) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Reseñas de " + pelicula.titulo());
    }

    /** Requiere sesion (si no, login); cambia a FrmPublicarResena. */
    public void abrirPublicar(PeliculaDTO pelicula) {
        // Pendiente (lo implementa su duenio)
        app.proximamente("Publicar reseña");
    }
}
