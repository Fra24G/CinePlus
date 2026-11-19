package com.cineplus.negocio.control;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.MembresiaDTO;
import com.cineplus.dto.PerfilDTO;
import com.cineplus.dto.UsuarioDTO;
import com.cineplus.negocio.bo.MembresiaBO;
import com.cineplus.negocio.bo.UsuarioBO;
import java.util.Optional;

/**
 * Caso de uso de membresia y perfil. Toma al usuario de la sesion, nunca de la pantalla.
 *
 * @author Luis Guillermo Franco Murrieta
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

    /**
     * Contrata CinePLUS Pro para el usuario de la sesion.
     */
    public MembresiaDTO contratarPro(DatosPagoDTO pago) throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        return membresiaBO.contratarPro(actual.idUsuario(), pago);
    }

    /**
     * Lee el usuario fresco (puntos al dia), refresca la sesion y arma el perfil
     * con la membresia activa (o null) y si es Pro.
     */
    public PerfilDTO obtenerPerfil() throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        UsuarioDTO fresco = usuarioBO.obtener(actual.idUsuario());
        usuario.refrescar(fresco);

        Optional<MembresiaDTO> activa = membresiaBO.obtenerActiva(fresco.idUsuario());
        boolean pro = activa.map(MembresiaBO::esPlanPro).orElse(false);
        return new PerfilDTO(fresco, activa.orElse(null), pro);
    }
}
