package com.cineplus.negocio.control;

import com.cineplus.dto.RegistroUsuarioDTO;
import com.cineplus.dto.UsuarioDTO;
import com.cineplus.negocio.bo.UsuarioBO;

/**
 * Control del caso de uso "Registrar cuenta".
 *
 * @author luisf
 */
public class ControlRegistro {

    private final UsuarioBO usuarioBO;

    public ControlRegistro(UsuarioBO usuarioBO) {
        this.usuarioBO = usuarioBO;
    }

    public UsuarioDTO registrarCuenta(RegistroUsuarioDTO datos) throws Exception {
        return usuarioBO.registrar(datos);
    }
}