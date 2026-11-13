package com.cineplus.negocio.bo;

import com.cineplus.dto.CancelacionDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.FiltroComprasDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.dto.TransaccionDTO;

/**
 * Objeto de negocio del historial, detalle, cancelacion y transacciones.
 *
 * @author luisf
 */
public class HistorialBO {

    /** Compras del usuario con filtros y paginacion. */
    public PaginaDTO<CompraDTO> consultarHistorial(Integer idUsuario, FiltroComprasDTO filtro) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: HistorialBO.consultarHistorial");
    }

    /** Detalle de una compra del usuario. */
    public CompraDTO obtener(Integer idUsuario, Integer idCompra) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: HistorialBO.obtener");
    }

    /** Cancela una compra (hasta 2 h antes) y ajusta los puntos. */
    public CompraDTO cancelar(Integer idUsuario, CancelacionDTO datos) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: HistorialBO.cancelar");
    }

    /** Todas las compras para el admin. */
    public PaginaDTO<TransaccionDTO> listarTransacciones(FiltroComprasDTO filtro) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: HistorialBO.listarTransacciones");
    }
}
