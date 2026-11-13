package com.cineplus.negocio.bo;

import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.CotizacionDTO;
import com.cineplus.dto.SolicitudCompraDTO;

/**
 * Objeto de negocio de la compra: cotizar y comprar en un solo UnitOfWork.
 *
 * @author luisf
 */
public class CompraBO {

    /** Calcula montos y puntos sin guardar nada. */
    public CotizacionDTO cotizar(Integer idUsuario, SolicitudCompraDTO solicitud, boolean pro) throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: CompraBO.cotizar");
    }

    /** Valida, cobra y guarda la compra con sus boletos y puntos. */
    public CompraDTO comprar(Integer idUsuario, SolicitudCompraDTO solicitud, boolean pro) throws Exception {
        // Lo implementa el Desarrollador 3
        throw new Exception("Pendiente: CompraBO.comprar");
    }
}
