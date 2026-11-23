package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Boleto;
import com.cineplus.entidades.enums.EstadoPago;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementacion JPA del DAO de boletos.
 *
 * @author Mario Perez
 */
public class BoletoDAO implements IBoletoDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    /** Un boleto se sigue viendo hasta 3 horas despues de que empezo su funcion. */
    private static final long HORAS_VISIBLE_DESPUES_DE_INICIO = 3;

    /** Boletos vigentes: compra aprobada del usuario y funcion futura o que empezo hace menos de 3 h. */
    private static final String CONDICION_VIGENTES =
            " WHERE b.compra.usuario.idUsuario = :idUsuario"
            + " AND b.compra.estadoPago = :aprobado"
            + " AND b.funcion.fechaHora > :limite";

    private final EntityManager em;

    public BoletoDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Integer> listarAsientosOcupados(Integer idFuncion) throws Exception {
        try {
            // Las compras canceladas o denegadas no ocupan asiento
            return em.createQuery(
                    "SELECT b.asiento.idAsiento FROM Boleto b "
                    + "WHERE b.funcion.idFuncion = :idFuncion "
                    + "AND b.compra.estadoPago NOT IN :excluidos", Integer.class)
                    .setParameter("idFuncion", idFuncion)
                    .setParameter("excluidos", List.of(EstadoPago.CANCELADO, EstadoPago.DENEGADO))
                    .getResultList();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Boleto> listarPorUsuario(Integer idUsuario, int pagina, int tamano) throws Exception {
        try {
            return em.createQuery(
                    "SELECT b FROM Boleto b" + CONDICION_VIGENTES
                    + " ORDER BY b.funcion.fechaHora ASC, b.idBoleto ASC", Boleto.class)
                    .setParameter("idUsuario", idUsuario)
                    .setParameter("aprobado", EstadoPago.APROBADO)
                    .setParameter("limite", limiteVigencia())
                    .setFirstResult(pagina * tamano)
                    .setMaxResults(tamano)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public long contarPorUsuario(Integer idUsuario) throws Exception {
        try {
            return em.createQuery(
                    "SELECT COUNT(b) FROM Boleto b" + CONDICION_VIGENTES, Long.class)
                    .setParameter("idUsuario", idUsuario)
                    .setParameter("aprobado", EstadoPago.APROBADO)
                    .setParameter("limite", limiteVigencia())
                    .getSingleResult();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    /** Funciones que empezaron antes de este momento ya no se muestran en Mis boletos. */
    private static LocalDateTime limiteVigencia() {
        return LocalDateTime.now().minusHours(HORAS_VISIBLE_DESPUES_DE_INICIO);
    }

    @Override
    public long contarPorFuncion(Integer idFuncion) throws Exception {
        try {
            return em.createQuery(
                    "SELECT COUNT(b) FROM Boleto b "
                    + "WHERE b.funcion.idFuncion = :idFuncion "
                    + "AND b.compra.estadoPago = :aprobado", Long.class)
                    .setParameter("idFuncion", idFuncion)
                    .setParameter("aprobado", EstadoPago.APROBADO)
                    .getSingleResult();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }
}
