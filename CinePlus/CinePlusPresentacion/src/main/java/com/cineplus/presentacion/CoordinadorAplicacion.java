package com.cineplus.presentacion;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.CredencialesDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.RegistroUsuarioDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.negocio.FachadaNegocio;
import com.cineplus.presentacion.formularios.FrmInicio;
import com.cineplus.presentacion.formularios.FrmLogin;
import com.cineplus.presentacion.formularios.FrmPrincipal;
import com.cineplus.presentacion.formularios.FrmRegistro;

import java.util.Arrays;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Coordina las pantallas: decide cual ventana se muestra y habla con la FachadaNegocio.
 * Las operaciones lentas (login, registro, cartelera) se ejecutan en un SwingWorker
 * para no congelar la ventana.
 *
 * @author luisf
 */
public final class CoordinadorAplicacion {

    /** Tarea que puede lanzar Exception (un Supplier normal no puede). */
    @FunctionalInterface
    private interface Tarea<T> {
        T ejecutar() throws Exception;
    }

    private static final String MENSAJE_GENERICO =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final FachadaNegocio fachada;
    private JFrame ventana;
    private volatile CarteleraDTO carteleraCache;

    public CoordinadorAplicacion(FachadaNegocio fachada) {
        this.fachada = fachada;
    }

    public void inicio() {
        cambiar(sesion() == null ? new FrmInicio(this) : new FrmPrincipal(this));
    }

    public void login() {
        cambiar(new FrmLogin(this));
    }

    public void registro() {
        cambiar(new FrmRegistro(this));
    }

    public SesionDTO sesion() {
        return fachada.obtenerSesion();
    }

    public void cerrarSesion() {
        fachada.cerrarSesion();
        cambiar(new FrmInicio(this));
    }

    /** Muestra la ventana nueva y cierra la anterior. */
    private void cambiar(JFrame nueva) {
        JFrame anterior = ventana;
        ventana = nueva;
        nueva.setVisible(true);
        if (anterior != null) {
            anterior.dispose();
        }
    }

    public void registrar(FrmRegistro vista, RegistroUsuarioDTO datos) {
        vista.ocupado(true);
        ejecutar(
                vista,
                () -> {
                    try {
                        return fachada.registrarCuenta(datos);
                    } finally {
                        // Borra la contrasena de la memoria despues de usarla
                        Arrays.fill(datos.contrasena(), '\0');
                    }
                },
                sesion -> cambiar(new FrmPrincipal(this)),
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    public void autenticar(FrmLogin vista, CredencialesDTO datos) {
        vista.ocupado(true);
        ejecutar(
                vista,
                () -> {
                    try {
                        return fachada.autenticar(datos);
                    } finally {
                        // Borra la contrasena de la memoria despues de usarla
                        Arrays.fill(datos.contrasena(), '\0');
                    }
                },
                sesion -> cambiar(new FrmPrincipal(this)),
                mensaje -> {
                    vista.ocupado(false);
                    vista.mostrarError(mensaje);
                });
    }

    /** Carga la cartelera una sola vez y despues reutiliza la copia guardada. */
    public void cargarCartelera(JFrame destino, Consumer<CarteleraDTO> recibir) {
        if (carteleraCache != null) {
            SwingUtilities.invokeLater(
                    () -> {
                        if (destino.isDisplayable()) {
                            recibir.accept(carteleraCache);
                        }
                    });
            return;
        }
        ejecutar(
                destino,
                () -> {
                    CarteleraDTO catalogo = fachada.consultarCartelera();
                    carteleraCache = catalogo;
                    return catalogo;
                },
                recibir,
                mensaje -> recibir.accept(new CarteleraDTO(java.util.List.of(), false, mensaje)));
    }

    public byte[] poster(PeliculaDTO pelicula) {
        return fachada.obtenerPoster(pelicula);
    }

    /** Peliculas para el mosaico decorativo de login y registro. */
    public CarteleraDTO catalogoDecorativo() {
        CarteleraDTO catalogo = carteleraCache;
        return catalogo == null ? fachada.consultarCartelera() : catalogo;
    }

    /**
     * Ejecuta una tarea fuera del hilo de Swing. Si sale bien llama a exito; si falla llama
     * a error con el mensaje de la Exception (los errores internos muestran un mensaje generico).
     */
    private <T> void ejecutar(
            JFrame destino, Tarea<T> tarea, Consumer<T> exito, Consumer<String> error) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.ejecutar();
            }

            @Override
            protected void done() {
                try {
                    T valor = get();
                    if (destino.isDisplayable()) {
                        exito.accept(valor);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (CancellationException ignored) {
                    // La tarea se cancelo: no hay nada que mostrar
                } catch (ExecutionException e) {
                    if (destino.isDisplayable()) {
                        error.accept(mensajeDe(e.getCause()));
                    }
                }
            }
        }.execute();
    }

    /** Solo se muestra el mensaje de nuestras Exception; un error interno muestra el generico. */
    private static String mensajeDe(Throwable causa) {
        if (causa == null || causa instanceof RuntimeException || causa.getMessage() == null) {
            return MENSAJE_GENERICO;
        }
        return causa.getMessage();
    }
}