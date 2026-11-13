package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Boleto;
import com.cineplus.entidades.enums.EstadoPago;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Implementacion JPA del DAO de boletos.
 *
 * @author luisf
 */
public class BoletoDAO implements IBoletoDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

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
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: BoletoDAO.listarPorUsuario");
    }

    @Override
    public long contarPorUsuario(Integer idUsuario) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: BoletoDAO.contarPorUsuario");
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
