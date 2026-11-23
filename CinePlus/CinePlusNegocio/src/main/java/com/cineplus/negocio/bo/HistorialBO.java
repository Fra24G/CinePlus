package com.cineplus.negocio.bo;

import com.cineplus.dto.CancelacionDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.FiltroComprasDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.dto.TransaccionDTO;
import com.cineplus.entidades.Compra;
import com.cineplus.entidades.Usuario;
import com.cineplus.entidades.enums.EstadoPago;
import com.cineplus.negocio.compra.ReglasPrecio;
import com.cineplus.persistencia.dao.ICompraDAO;
import com.cineplus.persistencia.uow.UnitOfWork;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de negocio del historial, detalle, cancelacion y transacciones.
 *
 * @author luisf
 * @author Mario Perez
 */
public class HistorialBO {

    private static final String NO_ENCONTRADA = "No se encontró la compra.";
    private static final String NO_CANCELABLE = "Esta compra ya no se puede cancelar.";

    /** Compras del usuario con filtros y paginacion. */
    public PaginaDTO<CompraDTO> consultarHistorial(Integer idUsuario, FiltroComprasDTO filtro) throws Exception {
        ValidacionesHistorial.filtro(filtro);
        EstadoPago estado = estado(filtro.estado());

        try (UnitOfWork uow = new UnitOfWork()) {
            ICompraDAO dao = uow.compraDAO();
            List<Compra> compras = dao.buscarPorUsuario(idUsuario, filtro.desde(), filtro.hasta(),
                    estado, filtro.pagina(), filtro.tamano());
            long total = dao.contarPorUsuario(idUsuario, filtro.desde(), filtro.hasta(), estado);

            // La conversion se hace dentro del UnitOfWork porque los boletos se cargan al pedirlos
            LocalDateTime ahora = LocalDateTime.now();
            List<CompraDTO> elementos = new ArrayList<>();
            for (Compra compra : compras) {
                elementos.add(ConvertidorCompra.aDTO(compra, esCancelable(compra, ahora)));
            }
            return new PaginaDTO<>(elementos, filtro.pagina(), filtro.tamano(), total);
        }
    }

    /** Detalle de una compra del usuario. */
    public CompraDTO obtener(Integer idUsuario, Integer idCompra) throws Exception {
        if (idCompra == null) {
            throw new Exception(NO_ENCONTRADA);
        }
        try (UnitOfWork uow = new UnitOfWork()) {
            Compra compra = buscarDelUsuario(uow, idUsuario, idCompra);
            return ConvertidorCompra.aDTO(compra, esCancelable(compra, LocalDateTime.now()));
        }
    }

    /** Cancela una compra (hasta 2 h antes) y ajusta los puntos. */
    public CompraDTO cancelar(Integer idUsuario, CancelacionDTO datos) throws Exception {
        ValidacionesHistorial.cancelacion(datos);

        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();
            LocalDateTime ahora = LocalDateTime.now();

            Compra compra = buscarDelUsuario(uow, idUsuario, datos.idCompra());
            if (!esCancelable(compra, ahora)) {
                uow.rollback();
                throw new Exception(NO_CANCELABLE);
            }

            compra.setEstadoPago(EstadoPago.CANCELADO);
            compra.setMotivoCancelacion(datos.motivo().strip());
            compra.setFechaCancelacion(ahora);

            // Se devuelven los puntos usados y se quitan los ganados, sin quedar en negativo.
            // El usuario se cargo con la compra en el mismo UnitOfWork, asi que sus puntos
            // se guardan junto con la compra al hacer actualizar/commit.
            Usuario usuario = compra.getUsuario();
            int puntos = valor(usuario.getPuntosAcumulados())
                    + valor(compra.getPuntosUsados())
                    - valor(compra.getPuntosGanados());
            usuario.setPuntosAcumulados(Math.max(0, puntos));

            Compra cancelada = uow.compraDAO().actualizar(compra);
            // Los asientos quedan libres solos: las compras canceladas no cuentan como ocupadas
            CompraDTO resultado = ConvertidorCompra.aDTO(cancelada, false);
            uow.commit();
            return resultado;
        }
    }

    /** Todas las compras para el admin. */
    public PaginaDTO<TransaccionDTO> listarTransacciones(FiltroComprasDTO filtro) throws Exception {
        ValidacionesHistorial.filtro(filtro);
        EstadoPago estado = estado(filtro.estado());

        try (UnitOfWork uow = new UnitOfWork()) {
            ICompraDAO dao = uow.compraDAO();
            List<Compra> compras = dao.buscarTodas(filtro.texto(), filtro.desde(), filtro.hasta(),
                    estado, filtro.pagina(), filtro.tamano());
            long total = dao.contarTodas(filtro.texto(), filtro.desde(), filtro.hasta(), estado);

            List<TransaccionDTO> elementos = new ArrayList<>();
            for (Compra compra : compras) {
                elementos.add(ConvertidorCompra.transaccionADTO(compra));
            }
            return new PaginaDTO<>(elementos, filtro.pagina(), filtro.tamano(), total);
        }
    }

    /** Busca la compra y revisa que sea del usuario; si no, responde como si no existiera. */
    private Compra buscarDelUsuario(UnitOfWork uow, Integer idUsuario, Integer idCompra) throws Exception {
        Compra compra = uow.compraDAO().buscarPorId(idCompra)
                .orElseThrow(() -> new Exception(NO_ENCONTRADA));
        if (idUsuario == null || compra.getUsuario() == null
                || !idUsuario.equals(compra.getUsuario().getIdUsuario())) {
            throw new Exception(NO_ENCONTRADA);
        }
        return compra;
    }

    /** Solo las compras aprobadas y con 2 h o mas antes de la funcion. */
    private static boolean esCancelable(Compra compra, LocalDateTime ahora) {
        return compra.getEstadoPago() == EstadoPago.APROBADO
                && compra.getFuncion() != null
                && ReglasPrecio.esCancelable(compra.getFuncion().getFechaHora(), ahora);
    }

    private static int valor(Integer numero) {
        return numero != null ? numero : 0;
    }

    /**
     * Convierte el texto del filtro de estado a EstadoPago. Acepta el nombre
     * ("APROBADO") o la etiqueta ("Aprobado"). Vacio o null = sin filtro.
     */
    private static EstadoPago estado(String texto) throws Exception {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String buscado = texto.strip();
        for (EstadoPago estado : EstadoPago.values()) {
            if (estado.name().equalsIgnoreCase(buscado) || estado.getEtiqueta().equalsIgnoreCase(buscado)) {
                return estado;
            }
        }
        throw new Exception("El estado de la compra no es válido.");
    }
}
