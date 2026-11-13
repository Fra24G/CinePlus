package com.cineplus.negocio;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.CredencialesDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.RegistroUsuarioDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.negocio.bo.CarteleraBO;
import com.cineplus.negocio.bo.UsuarioBO;
import com.cineplus.negocio.control.ControlRegistro;
import com.cineplus.negocio.control.ControlUsuario;
import com.cineplus.servicio.IServicioPeliculas;
import com.cineplus.servicio.ServicioPeliculasTmdb;

/**
 * PATRON FACHADA.
 * Unica puerta de entrada a la logica de negocio: Presentacion solo conoce esta clase
 * y los DTOs. Por dentro coordina los controles y los objetos de negocio.
 * El servicio de peliculas se puede cambiar al construirla (por ejemplo, para pruebas).
 *
 */
public class FachadaNegocio {

    private final ControlRegistro registro;
    private final ControlUsuario usuario;
    private final CarteleraBO cartelera;

    /** Fachada normal: usa TMDB como servicio de peliculas. */
    public FachadaNegocio() {
        this(new ServicioPeliculasTmdb());
    }

    public FachadaNegocio(IServicioPeliculas servicioPeliculas) {
        UsuarioBO usuarioBO = new UsuarioBO();
        this.registro = new ControlRegistro(usuarioBO);
        this.usuario = new ControlUsuario(usuarioBO);
        this.cartelera = new CarteleraBO(servicioPeliculas);
    }

    /** Crea la cuenta y deja la sesion abierta. */
    public SesionDTO registrarCuenta(RegistroUsuarioDTO datos) throws Exception {
        usuario.cerrarSesion();
        return usuario.abrirSesion(registro.registrarCuenta(datos));
    }

    public SesionDTO autenticar(CredencialesDTO datos) throws Exception {
        return usuario.autenticar(datos);
    }

    public SesionDTO obtenerSesion() {
        return usuario.obtenerSesion();
    }

    public void cerrarSesion() {
        usuario.cerrarSesion();
    }

    public CarteleraDTO consultarCartelera() {
        return cartelera.consultar();
    }

    public byte[] obtenerPoster(PeliculaDTO pelicula) {
        return cartelera.obtenerPoster(pelicula);
    }
}