package com.cineplus.negocio.control;

import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.CotizacionDTO;
import com.cineplus.dto.SolicitudCompraDTO;
import com.cineplus.negocio.bo.CompraBO;
import com.cineplus.negocio.bo.MembresiaBO;
import com.cineplus.negocio.bo.UsuarioBO;

/**
 * Control del caso de uso "Comprar boletos".
 *
 * @author luisf
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

    /** Cotiza la compra del usuario de la sesion. */
    public CotizacionDTO cotizar(SolicitudCompraDTO solicitud) throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: ControlCompra.cotizar");
    }

    /** Compra y refresca los puntos de la sesion. */
    public CompraDTO confirmar(SolicitudCompraDTO solicitud) throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: ControlCompra.confirmar");
    }
}
