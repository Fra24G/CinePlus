package com.cineplus.presentacion;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.CredencialesDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.RegistroUsuarioDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.negocio.FachadaNegocio;
import com.cineplus.presentacion.formularios.FrmConfirmacion;
import com.cineplus.presentacion.formularios.FrmInicio;
import com.cineplus.presentacion.formularios.FrmLogin;
import com.cineplus.presentacion.formularios.FrmPrincipal;
import com.cineplus.presentacion.formularios.FrmRegistro;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Nucleo de la presentacion: arranque, cambio de ventana, tareas en segundo plano y
 * navegacion entre secciones. Cada modulo tiene su propio coordinador (cartelera, compra,
 * boletos, comunidad, admin y cuenta) para que los integrantes no se pisen.
 *
 * @author luisf
 */
public final class CoordinadorAplicacion {

    /** Tarea que puede lanzar Exception (un Supplier normal no puede). */
    @FunctionalInterface
    interface Tarea<T> {
        T ejecutar() throws Exception;
    }

    private static final String MENSAJE_GENERICO =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final FachadaNegocio fachada;
    private final CoordinadorCartelera cartelera;
    private final CoordinadorCompra compra;
    private final CoordinadorBoletos boletos;
    private final CoordinadorComunidad comunidad;
    private final CoordinadorAdmin admin;
    private final CoordinadorCuenta cuenta;

    private JFrame ventana;
    private volatile CarteleraDTO carteleraCache;
    private boolean datosInicializados;

    public CoordinadorAplicacion(FachadaNegocio fachada) {
        this.fachada = fachada;
        this.cartelera = new CoordinadorCartelera(this);
        this.compra = new CoordinadorCompra(this);
        this.boletos = new CoordinadorBoletos(this);
        this.comunidad = new CoordinadorComunidad(this);
        this.admin = new CoordinadorAdmin(this);
        this.cuenta = new CoordinadorCuenta(this);
    }

    // ---------- Arranque y navegacion ----------

    /**
     * La primera vez carga los datos semilla en segundo plano (si la BD esta vacia) y
     * despues muestra la pantalla de inicio. Las siguientes veces solo cambia de pantalla.
     */
    public void inicio() {
        if (datosInicializados) {
            mostrarInicio();
            return;
        }
        datosInicializados = true;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                fachada.inicializarDatos();
                return null;
            }

            @Override
            protected void done() {
                String error = null;
                try {
                    get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (CancellationException e) {
                    // No hay nada que mostrar
                } catch (ExecutionException e) {
                    System.err.println("No se pudieron cargar los datos iniciales: " + e.getCause());
                    error = mensajeDe(e.getCause());
                }
                mostrarInicio();
                if (error != null) {
                    JOptionPane.showMessageDialog(ventana,
                            "No se pudieron preparar los datos iniciales.\n" + error,
                            "CinePlus", JOptionPane.WARNING_MESSAGE);
                }
            }
        }.execute();
    }

    private void mostrarInicio() {
        cambiar(sesion() == null ? new FrmInicio(this) : new FrmPrincipal(this));
    }

    /** Lleva a la seccion de la barra de navegacion. Si pide sesion y no hay, abre el login. */
    public void navegar(Seccion seccion) {
        if (seccion.requiereSesion() && sesion() == null) {
            login();
            return;
        }
        switch (seccion) {
            case INICIO -> inicio();
            case CARTELERA -> cartelera.abrir();
            case BOLETOS -> boletos.abrirBoletos();
            case HISTORIAL -> boletos.abrirHistorial();
            case COMUNIDAD -> comunidad.abrir();
            case CINEPLUS -> cuenta.abrirMembresia();
            case PERFIL -> cuenta.abrirPerfil();
        }
    }

    /**
     * Clic en una pelicula del carrusel. Sin sesion abre el login. Con sesion: si esta en
     * cartelera abre sus funciones; si solo esta en el catalogo abre sus resenas; si no
     * existe en la BD abre las resenas con la pelicula de TMDB.
     */
    public void seleccionarPelicula(PeliculaDTO pelicula) {
        if (sesion() == null) {
            login();
            return;
        }
        if (pelicula.idExterno() == null) {
            comunidad.abrirResenas(pelicula);
            return;
        }
        JFrame destino = ventana;
        ejecutar(
                destino,
                () -> {
                    Optional<PeliculaDTO> enCartelera = fachada.buscarEnCartelera(pelicula.idExterno());
                    if (enCartelera.isPresent()) {
                        return new Seleccion(enCartelera.get(), true);
                    }
                    PeliculaDTO enCatalogo = fachada.buscarEnCatalogo(pelicula.idExterno()).orElse(pelicula);
                    return new Seleccion(enCatalogo, false);
                },
                seleccion -> {
                    if (seleccion.enCartelera()) {
                        cartelera.abrirFunciones(seleccion.pelicula());
                    } else {
                        comunidad.abrirResenas(seleccion.pelicula());
                    }
                },
                this::avisar);
    }

    /** Resultado de buscar la pelicula seleccionada en la BD. */
    private record Seleccion(PeliculaDTO pelicula, boolean enCartelera) {
    }

    public void login() {
        cambiar(new FrmLogin(this));
    }

    public void registro() {
        cambiar(new FrmRegistro(this));
    }

    /** Pantalla generica de exito con cuenta regresiva; al terminar ejecuta alTerminar. */
    public void confirmar(String mensaje, Runnable alTerminar) {
        cambiar(new FrmConfirmacion(this, mensaje, alTerminar));
    }

    public SesionDTO sesion() {
        return fachada.obtenerSesion();
    }

    public void cerrarSesion() {
        fachada.cerrarSesion();
        cambiar(new FrmInicio(this));
    }

    // ---------- Coordinadores de modulo ----------

    public CoordinadorCartelera cartelera() {
        return cartelera;
    }

    public CoordinadorCompra compra() {
        return compra;
    }

    public CoordinadorBoletos boletos() {
        return boletos;
    }

    public CoordinadorComunidad comunidad() {
        return comunidad;
    }

    public CoordinadorAdmin admin() {
        return admin;
    }

    public CoordinadorCuenta cuenta() {
        return cuenta;
    }

    // ---------- Herramientas para los coordinadores de modulo ----------

    /** La Fachada, para que los coordinadores de modulo la usen. */
    FachadaNegocio fachada() {
        return fachada;
    }

    /** La ventana que se esta mostrando. */
    JFrame ventana() {
        return ventana;
    }

    /** Muestra la ventana nueva y cierra la anterior. */
    void cambiar(JFrame nueva) {
        JFrame anterior = ventana;
        ventana = nueva;
        nueva.setVisible(true);
        if (anterior != null && anterior != nueva) {
            anterior.dispose();
        }
    }

    /** Muestra un aviso sobre la ventana actual. */
    void avisar(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "CinePlus", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Aviso de las pantallas que su integrante todavia no termina. */
    void proximamente(String pantalla) {
        avisar(pantalla + ": próximamente.");
    }

    // ---------- Cuenta ----------

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

    // ---------- Cartelera de inicio ----------

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
                mensaje -> recibir.accept(new CarteleraDTO(List.of(), false, mensaje)));
    }

    public byte[] poster(PeliculaDTO pelicula) {
        return fachada.obtenerPoster(pelicula);
    }

    /**
     * Peliculas para el mosaico decorativo de login y registro. Usa la copia guardada; si no
     * hay, consulta una sola vez y la guarda. Se llama desde un hilo de fondo.
     */
    public CarteleraDTO catalogoDecorativo() {
        CarteleraDTO catalogo = carteleraCache;
        if (catalogo == null) {
            catalogo = fachada.consultarCartelera();
            carteleraCache = catalogo;
        }
        return catalogo;
    }

    // ---------- Segundo plano ----------

    /**
     * Ejecuta una tarea fuera del hilo de Swing. Si sale bien llama a exito; si falla llama
     * a error con el mensaje de la Exception (los errores internos muestran un mensaje generico).
     * Si destino ya se cerro no llama a nada.
     */
    <T> void ejecutar(JFrame destino, Tarea<T> tarea, Consumer<T> exito, Consumer<String> error) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.ejecutar();
            }

            @Override
            protected void done() {
                boolean visible = destino == null || destino.isDisplayable();
                try {
                    T valor = get();
                    if (visible) {
                        exito.accept(valor);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (CancellationException ignored) {
                    // La tarea se cancelo: no hay nada que mostrar
                } catch (ExecutionException e) {
                    if (visible) {
                        error.accept(mensajeDe(e.getCause()));
                    }
                }
            }
        }.execute();
    }

    /** Solo se muestra el mensaje de nuestras Exception; un error interno muestra el generico. */
    private static String mensajeDe(Throwable causa) {
        if (causa == null || causa instanceof RuntimeException || causa.getMessage() == null) {
            if (causa != null) {
                causa.printStackTrace();
            }
            return MENSAJE_GENERICO;
        }
        return causa.getMessage();
    }
}
