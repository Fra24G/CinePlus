package com.cineplus.negocio.bo;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.MembresiaDTO;
import com.cineplus.dto.PlanDTO;
import com.cineplus.entidades.Membresia;
import com.cineplus.entidades.Usuario;
import com.cineplus.negocio.compra.pago.FabricaPago;
import com.cineplus.negocio.compra.pago.IEstrategiaPago;
import com.cineplus.negocio.compra.pago.ResultadoPago;
import com.cineplus.persistencia.uow.UnitOfWork;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Reglas de negocio de la membresia: planes, membresia activa y contratacion de Pro.
 *
 * @author Luis Guillermo Franco Murrieta
 */
public class MembresiaBO {

    /** Nombre del plan gratuito. */
    public static final String PLAN_GRATIS = "CinePLUS+";
    /** Nombre del plan de pago; debe coincidir con el que guarda CargaInicial. */
    public static final String PLAN_PRO = "CinePLUS Pro";
    /** Precio mensual de CinePLUS Pro. */
    public static final double PRECIO_PRO = 129.0;

    /** Beneficios que se guardan en la membresia contratada (mismo texto que la semilla). */
    private static final String BENEFICIOS_PRO = "Más puntos por tus compras, sin comisión por uso del servicio, "
            + "preventas anticipadas y funciones exclusivas.";

    /**
     * Los dos planes fijos del Figma con sus vinetas exactas. No consulta la BD.
     */
    public List<PlanDTO> listarPlanes() {
        return List.of(
                new PlanDTO(PLAN_GRATIS, 0.0,
                        "Beneficios únicos por registrarte en CinePlus\n"
                        + "Encuentra tus boletos de forma sencilla\n"
                        + "Acumula puntos en todas tus compras\n"
                        + "Accede a nuestra comunidad CinePlus, lee, publica reseñas.", true),
                new PlanDTO(PLAN_PRO, PRECIO_PRO,
                        "Recibe más puntos por tus compras\n"
                        + "Sin comisión por uso del servicio\n"
                        + "Preventas anticipadas\n"
                        + "Funciones exclusivas\n"
                        + "¡Mucho más!", false));
    }

    /**
     * Membresia vigente del usuario, o vacio si no tiene.
     */
    public Optional<MembresiaDTO> obtenerActiva(Integer idUsuario) throws Exception {
        if (idUsuario == null) {
            return Optional.empty();
        }
        try (UnitOfWork uow = new UnitOfWork()) {
            return uow.membresiaDAO()
                    .buscarActiva(idUsuario, LocalDateTime.now())
                    .map(this::aDTO);
        }
    }

    /**
     * true si el usuario tiene CinePLUS Pro vigente. La usa ControlCompra
     * para quitar el cargo por servicio y triplicar los puntos.
     */
    public boolean esPro(Integer idUsuario) throws Exception {
        return obtenerActiva(idUsuario)
                .map(MembresiaBO::esPlanPro)
                .orElse(false);
    }

    /**
     * Cobra $129 y crea la membresia Pro por 1 mes, todo en un UnitOfWork.
     * No se puede pagar con puntos ni contratar si ya hay una Pro activa.
     */
    public MembresiaDTO contratarPro(Integer idUsuario, DatosPagoDTO pago) throws Exception {
        if (idUsuario == null) {
            throw new Exception("No se encontró el usuario.");
        }
        if (pago == null) {
            throw new Exception("Elige un método de pago.");
        }
        if (pago.usarPuntos()) {
            throw new Exception("La membresía CinePLUS Pro no se puede pagar con puntos.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();
            LocalDateTime ahora = LocalDateTime.now();

            Usuario usuario = uow.usuarioDAO().buscarPorId(idUsuario)
                    .orElseThrow(() -> new Exception("No se encontró el usuario."));

            Optional<Membresia> activa = uow.membresiaDAO().buscarActiva(idUsuario, ahora);
            if (activa.isPresent() && PLAN_PRO.equals(activa.get().getNombrePlan())) {
                throw new Exception("Ya eres miembro CinePLUS Pro.");
            }

            // TODO: Funciona de punta a punta cuando se integre Dev 3 (FabricaPago y estrategias).
            // Mientras tanto FabricaPago.para lanza "Pendiente: FabricaPago.para" y el mensaje
            // llega a la vista por mostrarError.
            IEstrategiaPago estrategia = FabricaPago.para(pago, PRECIO_PRO);
            ResultadoPago resultado = estrategia.cobrar(PRECIO_PRO, pago);
            if (resultado == null || !resultado.aprobado()) {
                String mensaje = (resultado != null && resultado.mensaje() != null
                        && !resultado.mensaje().isBlank())
                        ? resultado.mensaje()
                        : "El pago no fue aprobado. Revisa tus datos e inténtalo de nuevo.";
                throw new Exception(mensaje);
            }

            Membresia membresia = new Membresia();
            membresia.setUsuario(usuario);
            membresia.setFechaInicio(ahora);
            membresia.setFechaVencimiento(ahora.plusMonths(1));
            membresia.setEstado(true);
            membresia.setNombrePlan(PLAN_PRO);
            membresia.setPrecioMensual(PRECIO_PRO);
            membresia.setBeneficios(BENEFICIOS_PRO);

            uow.membresiaDAO().insertar(membresia);
            uow.commit();
            return aDTO(membresia);
        }
    }

    /** true si la membresia es del plan CinePLUS Pro. */
    public static boolean esPlanPro(MembresiaDTO membresia) {
        return membresia != null && PLAN_PRO.equals(membresia.nombrePlan());
    }

    private MembresiaDTO aDTO(Membresia membresia) {
        return new MembresiaDTO(
                membresia.getIdMembresia(),
                membresia.getNombrePlan(),
                membresia.getPrecioMensual() != null ? membresia.getPrecioMensual() : 0.0,
                membresia.getBeneficios(),
                membresia.getFechaInicio(),
                membresia.getFechaVencimiento(),
                Boolean.TRUE.equals(membresia.getEstado()));
    }
}
