package com.cineplus.negocio.control;

import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.CotizacionDTO;
import com.cineplus.dto.SolicitudCompraDTO;
import com.cineplus.dto.UsuarioDTO;
import com.cineplus.negocio.bo.CompraBO;
import com.cineplus.negocio.bo.MembresiaBO;
import com.cineplus.negocio.bo.UsuarioBO;

/**
 * 
 * @author yakor
 */
public class ControlCompra {

    private final CompraBO compraBO;
    private final MembresiaBO membresiaBO;
    private final UsuarioBO usuarioBO;
    private final ControlUsuario usuario;

    public ControlCompra(CompraBO compraBO, MembresiaBO membresiaBO, UsuarioBO usuarioBO,
            ControlUsuario usuario) {
        this.compraBO = compraBO;
        this.membresiaBO = membresiaBO;
        this.usuarioBO = usuarioBO;
        this.usuario = usuario;
    }

    /** 
     * Cotiza una compra del usuario 
     */
    public CotizacionDTO cotizar(SolicitudCompraDTO solicitud) throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        boolean pro = membresiaBO.esPro(actual.idUsuario());
        return compraBO.cotizar(actual.idUsuario(), solicitud, pro);
    }

    /** 
     * compra y refresca los puntos 
     */
    public CompraDTO confirmar(SolicitudCompraDTO solicitud) throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        boolean pro = membresiaBO.esPro(actual.idUsuario());
        CompraDTO compra = compraBO.comprar(actual.idUsuario(), solicitud, pro);
        usuario.refrescar(usuarioBO.obtener(actual.idUsuario()));
        return compra;
    }
}
