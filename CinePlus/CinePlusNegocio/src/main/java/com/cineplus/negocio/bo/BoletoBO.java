package com.cineplus.negocio.bo;

import com.cineplus.cineplusservicioqr.IGeneradorQR;
import com.cineplus.dto.BoletoDTO;
import com.cineplus.dto.PaginaDTO;

/**
 * Objeto de negocio de boletos y su codigo QR.
 *
 * @author luisf
 */
public class BoletoBO {
    private final IGeneradorQR generadorQR;

    public BoletoBO(IGeneradorQR generadorQR) {
        this.generadorQR = generadorQR;
    }

    /** Boletos vigentes del usuario, paginados. */
    public PaginaDTO<BoletoDTO> listarBoletos(Integer idUsuario, int pagina, int tamano) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: BoletoBO.listarBoletos");
    }

    /** Imagen PNG del QR del boleto. */
    public byte[] generarQR(BoletoDTO boleto) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: BoletoBO.generarQR");
    }
}
