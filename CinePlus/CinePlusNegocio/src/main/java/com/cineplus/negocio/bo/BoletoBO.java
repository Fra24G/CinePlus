package com.cineplus.negocio.bo;

import com.cineplus.cineplusservicioqr.IGeneradorQR;
import com.cineplus.dto.BoletoDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.entidades.Boleto;
import com.cineplus.persistencia.dao.IBoletoDAO;
import com.cineplus.persistencia.uow.UnitOfWork;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de negocio de boletos y su codigo QR.
 *
 * @author luisf
 * @author Mario Perez
 */
public class BoletoBO {

    private static final int TAMANO_MAXIMO_PAGINA = 50;

    private final IGeneradorQR generadorQR;

    public BoletoBO(IGeneradorQR generadorQR) {
        this.generadorQR = generadorQR;
    }

    /** Boletos vigentes del usuario, paginados. */
    public PaginaDTO<BoletoDTO> listarBoletos(Integer idUsuario, int pagina, int tamano) throws Exception {
        if (idUsuario == null) {
            throw new Exception("No se encontró el usuario.");
        }
        if (pagina < 0 || tamano < 1 || tamano > TAMANO_MAXIMO_PAGINA) {
            throw new Exception("La página no es válida.");
        }
        try (UnitOfWork uow = new UnitOfWork()) {
            IBoletoDAO dao = uow.boletoDAO();
            List<Boleto> boletos = dao.listarPorUsuario(idUsuario, pagina, tamano);
            long total = dao.contarPorUsuario(idUsuario);

            List<BoletoDTO> elementos = new ArrayList<>();
            for (Boleto boleto : boletos) {
                elementos.add(ConvertidorCompra.boletoADTO(boleto));
            }
            return new PaginaDTO<>(elementos, pagina, tamano, total);
        }
    }

    /** Imagen PNG del QR del boleto. */
    public byte[] generarQR(BoletoDTO boleto) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: BoletoBO.generarQR");
    }
}
