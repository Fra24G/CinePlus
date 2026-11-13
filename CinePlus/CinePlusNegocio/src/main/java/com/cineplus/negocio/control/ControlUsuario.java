package com.cineplus.negocio.control;

import com.cineplus.dto.CredencialesDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.dto.UsuarioDTO;
import com.cineplus.negocio.bo.UsuarioBO;
import java.time.Instant;
import java.util.UUID;

/**
 * Control del acceso: inicia sesion, la consulta y la cierra.
 * La sesion vive en memoria mientras la aplicacion esta abierta.
 *
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
}