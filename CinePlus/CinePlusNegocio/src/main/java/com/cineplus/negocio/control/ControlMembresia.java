package com.cineplus.negocio.control;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.MembresiaDTO;
import com.cineplus.dto.PerfilDTO;
import com.cineplus.negocio.bo.MembresiaBO;
import com.cineplus.negocio.bo.UsuarioBO;

/**
 * Control de membresia y perfil.
 *
 * @author luisf
 */
public class ControlMembresia {
    private final MembresiaBO membresiaBO;
    private final UsuarioBO usuarioBO;
    private final ControlUsuario usuario;

    public ControlMembresia(MembresiaBO membresiaBO, UsuarioBO usuarioBO, ControlUsuario usuario) {
        this.membresiaBO = membresiaBO;
        this.usuarioBO = usuarioBO;
        this.usuario = usuario;
    }

    /** Contrata CinePLUS Pro para el usuario de la sesion. */
    public MembresiaDTO contratarPro(DatosPagoDTO pago) throws Exception {
        // Lo implementa el Desarrollador 1
        throw new Exception("Pendiente: ControlMembresia.contratarPro");
    }

    /** Datos del perfil con puntos al dia y membresia. */
    public PerfilDTO obtenerPerfil() throws Exception {
        // Lo implementa el Desarrollador 1
        throw new Exception("Pendiente: ControlMembresia.obtenerPerfil");
    }
}
