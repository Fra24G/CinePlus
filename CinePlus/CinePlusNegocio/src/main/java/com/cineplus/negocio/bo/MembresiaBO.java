package com.cineplus.negocio.bo;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.MembresiaDTO;
import com.cineplus.dto.PlanDTO;
import java.util.List;
import java.util.Optional;

/**
 * Objeto de negocio de las membresias CinePLUS+ y CinePLUS Pro.
 *
 * @author luisf
 */
public class MembresiaBO {

    /** Los dos planes fijos del Figma (no consulta la BD). */
    public List<PlanDTO> listarPlanes() {
        return List.of(
                new PlanDTO("CinePLUS+", 0.0,
                        "Encuentra tus boletos de forma sencilla. Acumula puntos en todas tus compras. "
                        + "Accede a la comunidad CinePlus: lee y publica reseñas.", true),
                new PlanDTO("CinePLUS Pro", 129.0,
                        "Todo lo de CinePLUS+ y además: más puntos por tus compras, sin comisión por uso "
                        + "del servicio, preventas anticipadas y funciones exclusivas.", false));
    }

    /** Membresia vigente del usuario, si tiene. */
    public Optional<MembresiaDTO> obtenerActiva(Integer idUsuario) throws Exception {
        // Lo implementa el Desarrollador 1
        throw new Exception("Pendiente: MembresiaBO.obtenerActiva");
    }

    /** true si el usuario tiene CinePLUS Pro activa. */
    public boolean esPro(Integer idUsuario) throws Exception {
        // Lo implementa el Desarrollador 1. Mientras tanto nadie es Pro,
        // asi la compra funciona cobrando como usuario normal.
        return false;
    }

    /** Cobra $129 y activa CinePLUS Pro por un mes. */
    public MembresiaDTO contratarPro(Integer idUsuario, DatosPagoDTO pago) throws Exception {
        // Lo implementa el Desarrollador 1
        throw new Exception("Pendiente: MembresiaBO.contratarPro");
    }
}
