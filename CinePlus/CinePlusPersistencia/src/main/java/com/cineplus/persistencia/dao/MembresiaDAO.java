package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Membresia;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Acceso a datos de las membresias. Recibe el EntityManager del UnitOfWork
 * y nunca abre ni cierra la conexion ni la transaccion.
 *
 * @author Luis Guillermo Franco Murrieta
 */
public class MembresiaDAO implements IMembresiaDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final EntityManager em;

    public MembresiaDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Membresia insertar(Membresia membresia) throws Exception {
        try {
            em.persist(membresia);
            em.flush();
            return membresia;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    /**
     * Guarda los cambios de una membresia (vencerla o renovarla).
     * Requiere una transaccion iniciada en el UnitOfWork.
     */
    @Override
    public Membresia actualizar(Membresia membresia) throws Exception {
        if (membresia == null) {
            throw new Exception("No se recibió la membresía a actualizar.");
        }
        try {
            Membresia actualizada = em.merge(membresia);
            em.flush();
            return actualizada;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public Optional<Membresia> buscarPorId(Integer idMembresia) throws Exception {
        try {
            return Optional.ofNullable(em.find(Membresia.class, idMembresia));
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    /**
     * Membresia vigente del usuario: estado = true y sin vencer
     * (fechaVencimiento posterior a "ahora" o null). Si hay varias,
     * devuelve la que empezo mas recientemente.
     */
    @Override
    public Optional<Membresia> buscarActiva(Integer idUsuario, LocalDateTime ahora) throws Exception {
        if (idUsuario == null) {
            return Optional.empty();
        }
        LocalDateTime referencia = (ahora != null) ? ahora : LocalDateTime.now();
        try {
            return em.createQuery(
                    "SELECT m FROM Membresia m "
                    + "WHERE m.usuario.idUsuario = :idUsuario "
                    + "AND m.estado = true "
                    + "AND (m.fechaVencimiento IS NULL OR m.fechaVencimiento > :ahora) "
                    + "ORDER BY m.fechaInicio DESC", Membresia.class)
                    .setParameter("idUsuario", idUsuario)
                    .setParameter("ahora", referencia)
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }
}
