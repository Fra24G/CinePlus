package com.cineplus.presentacion;

import com.cineplus.dto.CineDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.GuardarFuncionDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.presentacion.formularios.admin.FrmFuncion;
import com.cineplus.presentacion.formularios.admin.FrmPanelAdmin;
import com.cineplus.presentacion.formularios.admin.FrmPelicula;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Coordinador del modulo de administracion de cartelera y funciones.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public final class CoordinadorAdmin {

    private final CoordinadorAplicacion app;

    public CoordinadorAdmin(CoordinadorAplicacion app) {
        this.app = app;
    }

    /** Valida rol ADMIN y abre FrmPanelAdmin. */
    public void abrirPanel() {
        SesionDTO sesion = app.sesion();
        if (sesion == null || !sesion.usuario().esAdmin()) {
            app.avisar("Solo un administrador puede acceder a esta sección.");
            return;
        }
        app.cambiar(new FrmPanelAdmin(app));
    }

    /** Carga las metricas semanales y el catalogo del panel. */
    public void cargarResumen(FrmPanelAdmin vista) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().obtenerResumenAdmin(),
                resumen -> {
                    vista.ocupado(false);
                    vista.mostrarResumen(resumen);
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Busca peliculas en TMDB para importarlas a cartelera. */
    public void buscarEnTmdb(FrmPanelAdmin vista, String texto) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().buscarPeliculas(texto),
                resultados -> {
                    vista.ocupado(false);
                    vista.mostrarResultadosTmdb(resultados);
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Importa una pelicula de TMDB y la abre en FrmPelicula para ajustar su periodo. */
    public void importar(FrmPanelAdmin vista, PeliculaDTO externa) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().importarPelicula(externa.idExterno()),
                importada -> {
                    vista.ocupado(false);
                    abrirEditarPelicula(importada);
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Activa o desactiva la disponibilidad en cartelera de una pelicula y recarga el panel. */
    public void cambiarDisponibilidad(FrmPanelAdmin vista, Integer idPelicula, boolean disponible) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> {
                    app.fachada().cambiarDisponibilidad(idPelicula, disponible);
                    return app.fachada().obtenerResumenAdmin();
                },
                resumen -> {
                    vista.ocupado(false);
                    vista.mostrarResumen(resumen);
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Abre FrmPelicula en modo creacion. */
    public void abrirAgregarPelicula() {
        app.cambiar(new FrmPelicula(app, null));
    }

    /** Abre FrmPelicula en modo edicion. */
    public void abrirEditarPelicula(PeliculaDTO pelicula) {
        app.cambiar(new FrmPelicula(app, pelicula));
    }

    /** Sube una imagen local como poster y coloca la ruta resultante en el formulario. */
    public void subirPoster(FrmPelicula vista, File archivo) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> {
                    byte[] bytes = Files.readAllBytes(archivo.toPath());
                    return app.fachada().subirPoster(bytes, archivo.getName());
                },
                ruta -> {
                    vista.ocupado(false);
                    vista.ponerImagen(ruta);
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Crea o actualiza una pelicula y muestra pantalla de confirmacion. */
    public void guardarPelicula(FrmPelicula vista, PeliculaDTO datos) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> app.fachada().guardarPelicula(datos),
                guardada -> app.confirmar("¡Se realizó la operación con éxito!", this::abrirPanel),
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Abre FrmFuncion para gestionar las funciones de una pelicula. */
    public void abrirFunciones(PeliculaDTO pelicula, FuncionDTO funcion) {
        app.cambiar(new FrmFuncion(app, pelicula, funcion));
    }

    /** Carga la lista de cines y las funciones actuales de la pelicula. */
    public void cargarDatosFuncion(FrmFuncion vista, Integer idPelicula) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> new DatosInicialesFuncion(
                        app.fachada().listarCines(),
                        app.fachada().listarFuncionesAdmin(idPelicula)),
                datos -> {
                    vista.ocupado(false);
                    vista.mostrarCines(datos.cines());
                    vista.mostrarFunciones(datos.funciones());
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Carga las salas de un cine seleccionado. */
    public void cargarSalas(FrmFuncion vista, Integer idCine) {
        if (idCine == null) {
            vista.mostrarSalas(List.of());
            return;
        }
        app.ejecutar(
                vista,
                () -> app.fachada().listarSalas(idCine),
                vista::mostrarSalas,
                vista::mostrarError);
    }

    /** Guarda una funcion nueva o editada y recarga la lista en pantalla. */
    public void guardarFuncion(FrmFuncion vista, GuardarFuncionDTO datos) {
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> {
                    app.fachada().guardarFuncion(datos);
                    return app.fachada().listarFuncionesAdmin(datos.idPelicula());
                },
                funciones -> {
                    vista.ocupado(false);
                    vista.limpiarFormulario();
                    vista.mostrarFunciones(funciones);
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Confirma con el usuario y elimina una funcion si no tiene boletos vendidos. */
    public void eliminarFuncion(FrmFuncion vista, Integer idFuncion) {
        if (idFuncion == null) {
            vista.mostrarError("Selecciona primero una función de la lista para eliminar.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(
                vista,
                "¿Seguro que deseas eliminar esta función?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        vista.ocupado(true);
        app.ejecutar(
                vista,
                () -> {
                    app.fachada().eliminarFuncion(idFuncion);
                    return app.fachada().listarFuncionesAdmin(vista.idPelicula());
                },
                funciones -> {
                    vista.ocupado(false);
                    vista.limpiarFormulario();
                    vista.mostrarFunciones(funciones);
                },
                error -> {
                    vista.ocupado(false);
                    vista.mostrarError(error);
                });
    }

    /** Navega al historial global de transacciones (modulo del Desarrollador 4). */
    public void abrirTransacciones() {
        app.boletos().abrirTransacciones();
    }

    private record DatosInicialesFuncion(List<CineDTO> cines, List<FuncionDTO> funciones) {
    }
}
