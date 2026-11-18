package com.cineplus.presentacion;

import com.cineplus.dto.NuevaResenaDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.presentacion.formularios.FrmComunidad;
import com.cineplus.presentacion.formularios.FrmPublicarResena;
import com.cineplus.presentacion.formularios.FrmResenasPelicula;
import java.util.List;

/**
 * Coordinador del modulo de Comunidad y resenas.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public final class CoordinadorComunidad {

    private static final int TAMANO_COMUNIDAD = 6;
    private static final int TAMANO_PELICULA = 10;

    private final CoordinadorAplicacion app;

    public CoordinadorComunidad(CoordinadorAplicacion app) {
        this.app = app;
    }

    /** Cambia a FrmComunidad y carga la pagina 0. */
    public void abrir() {
        app.cambiar(new FrmComunidad(app));
    }

    /** Carga las resenas mas recientes paginadas. */
    public void cargarRecientes(FrmComunidad vista, int pagina) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().listarResenasRecientes(pagina, TAMANO_COMUNIDAD),
                resultado -> {
                    vista.ocupado(false);
                    vista.mostrarResenas(resultado);
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Cambia a FrmResenasPelicula(pelicula). */
    public void abrirResenas(PeliculaDTO pelicula) {
        app.cambiar(new FrmResenasPelicula(app, pelicula));
    }

    /** Carga las resenas de una pelicula especifica. */
    public void cargarResenas(FrmResenasPelicula vista, PeliculaDTO pelicula, int pagina) {
        if (pelicula == null || pelicula.idPelicula() == null) {
            vista.mostrarResenas(new PaginaDTO<>(List.of(), 0, TAMANO_PELICULA, 0));
            return;
        }
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().listarResenas(pelicula.idPelicula(), pagina, TAMANO_PELICULA),
                resultado -> {
                    vista.ocupado(false);
                    vista.mostrarResenas(resultado);
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Requiere sesion (si no, login); cambia a FrmPublicarResena. */
    public void abrirPublicar(PeliculaDTO pelicula) {
        if (app.sesion() == null) {
            app.login();
            return;
        }
        app.cambiar(new FrmPublicarResena(app, pelicula));
    }

    /** Publica una nueva resena y muestra confirmacion. */
    public void publicar(FrmPublicarResena vista, NuevaResenaDTO datos) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().publicarResena(datos),
                resena -> app.confirmar("¡Se realizó la operación con éxito!", this::abrir),
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }
}
