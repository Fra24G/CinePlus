
package com.cineplus.presentacion;

import com.cineplus.dto.AsientoDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.presentacion.formularios.FrmCartelera;
import com.cineplus.presentacion.formularios.FrmSeleccionFuncion;
import com.cineplus.presentacion.formularios.FrmSeleccionAsientos;

import java.util.List;

/**
 * Coordinador del modulo de cartelera, funciones y asientos
 *
 * @author Juan Ramon Acuña Gutierrez
 */
public final class CoordinadorCartelera {

    private final CoordinadorAplicacion app;

    public CoordinadorCartelera(CoordinadorAplicacion app) {
        this.app = app;
    }

    /** Abre la cartelera y carga las peliculas disponibles */
    public void abrir() {
        FrmCartelera vista = new FrmCartelera(app);
        app.cambiar(vista);
        cargarCartelera(vista);
    }

    /** Consulta las peliculas locales de la cartelera */
    public void cargarCartelera(FrmCartelera vista) {
        vista.ocupado(true);

        app.ejecutar(
                vista,
                () -> app.fachada().consultarCarteleraLocal(),
                peliculas -> {
                    vista.ocupado(false);
                    vista.mostrarPeliculas(peliculas);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                }
        );
    }

    /** Abre la seleccion de funcion de la pelicula elegida*/
    public void abrirFunciones(PeliculaDTO pelicula) {
        if (pelicula == null) {
            app.avisar("No se seleccionó ninguna película.");
            return;
        }

        FrmSeleccionFuncion vista =
                new FrmSeleccionFuncion(app, pelicula);

        app.cambiar(vista);
        cargarCines(vista);
    }

    /** Consulta los cines disponibles */
    public void cargarCines(FrmSeleccionFuncion vista) {
        vista.ocupado(true);

        app.ejecutar(
                vista,
                () -> app.fachada().listarCines(),
                cines -> {
                    vista.ocupado(false);
                    vista.mostrarCines(cines);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                }
        );
    }

    /** Consulta las funciones de una pelicula en el cine elegido */
    public void cargarFunciones(
            FrmSeleccionFuncion vista,
            Integer idPelicula,
            Integer idCine) {

        if (idPelicula == null || idCine == null) {
            vista.mostrarError("Selecciona un cine para consultar sus funciones.");
            return;
        }

        vista.ocupado(true);

        app.ejecutar(
                vista,
                () -> app.fachada().listarFunciones(idPelicula, idCine),
                funciones -> {
                    vista.ocupado(false);
                    vista.mostrarFunciones(funciones);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                }
        );
    }

    /** Abre la seleccion de asientos de la funcion elegida */
    public void abrirAsientos(FuncionDTO funcion) {
        if (funcion == null) {
            app.avisar("No se seleccionó ninguna función.");
            return;
        }

        FrmSeleccionAsientos vista =
                new FrmSeleccionAsientos(app, funcion);

        app.cambiar(vista);
        cargarAsientos(vista, funcion.idFuncion());
    }

    /** Consulta los asientos disponibles de una funcion */
    public void cargarAsientos(
            FrmSeleccionAsientos vista,
            Integer idFuncion) {

        vista.ocupado(true);

        app.ejecutar(
                vista,
                () -> app.fachada().listarAsientos(idFuncion),
                asientos -> {
                    vista.ocupado(false);
                    vista.mostrarAsientos(asientos);
                },
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                }
        );
    }

    /** Envia la seleccion al modulo de compra */
    public void continuarAPago(
            FrmSeleccionAsientos vista,
            FuncionDTO funcion,
            List<AsientoDTO> seleccion) {

        if (seleccion == null || seleccion.isEmpty()) {
            vista.mostrarError("Elige al menos un asiento.");
            return;
        }

        app.compra().abrirPago(funcion, seleccion);
    }
}
