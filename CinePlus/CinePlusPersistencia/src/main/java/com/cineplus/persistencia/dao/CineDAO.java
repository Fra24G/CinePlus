package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Cine;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JPA del DAO de cines.
 *
 * @author luisf
 */
public class CineDAO implements ICineDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final EntityManager em;

    public CineDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Cine insertar(Cine cine) throws Exception {
        try {
            em.persist(cine);
            em.flush();
            return cine;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Cine> listarTodos() throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: CineDAO.listarTodos");
    }

    @Override
    public Optional<Cine> buscarPorId(Integer idCine) throws Exception {
        try {
            return Optional.ofNullable(em.find(Cine.class, idCine));
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public long contar() throws Exception {
        try {
            return em.createQuery("SELECT COUNT(c) FROM Cine c", Long.class).getSingleResult();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }
}
