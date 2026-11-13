package com.cineplus.negocio.control;

import com.cineplus.dto.CredencialesDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.dto.UsuarioDTO;
import com.cineplus.negocio.bo.UsuarioBO;
import java.time.Instant;
import java.util.UUID;

/**
 * Control del acceso: inicia sesion, la consulta, la exige y la cierra.
 * La sesion vive en memoria mientras la aplicacion esta abierta.
 *
 * @author luisf
 */
public class ControlUsuario {

    private final UsuarioBO usuarioBO;
    private volatile SesionDTO sesion;

    public ControlUsuario(UsuarioBO usuarioBO) {
        this.usuarioBO = usuarioBO;
    }

    public SesionDTO autenticar(CredencialesDTO datos) throws Exception {
        cerrarSesion();
        return abrirSesion(usuarioBO.autenticar(datos));
    }

    public SesionDTO abrirSesion(UsuarioDTO usuario) {
        sesion = new SesionDTO(UUID.randomUUID().toString(), usuario, Instant.now());
        return sesion;
    }

    /** Devuelve la sesion abierta, o null si nadie ha iniciado sesion. */
    public SesionDTO obtenerSesion() {
        return sesion;
    }

    public void cerrarSesion() {
        sesion = null;
    }

    /** Devuelve el usuario de la sesion o lanza una Exception si nadie ha iniciado sesion. */
    public UsuarioDTO exigirSesion() throws Exception {
        SesionDTO actual = sesion;
        if (actual == null) {
            throw new Exception("Inicia sesión para continuar.");
        }
        return actual.usuario();
    }

    /** Exige que haya sesion y que el usuario sea administrador. */
    public void exigirAdmin() throws Exception {
        if (!exigirSesion().esAdmin()) {
            throw new Exception("Solo un administrador puede hacer esto.");
        }
    }

    /** Reemplaza el usuario de la sesion actual (mismo idSesion) con datos nuevos. */
    public void refrescar(UsuarioDTO usuario) {
        SesionDTO actual = sesion;
        if (actual != null && usuario != null) {
            sesion = new SesionDTO(actual.idSesion(), usuario, actual.creadaEn());
        }
    }
}
