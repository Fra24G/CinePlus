package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Membresia;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementacion JPA del DAO de membresias.
 *
 * @author luisf
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

    @Override
    public Membresia actualizar(Membresia membresia) throws Exception {
        // Lo implementa el Desarrollador 1
        throw new Exception("Pendiente: MembresiaDAO.actualizar");
    }

    @Override
    public Optional<Membresia> buscarPorId(Integer idMembresia) throws Exception {
        try {
            return Optional.ofNullable(em.find(Membresia.class, idMembresia));
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public Optional<Membresia> buscarActiva(Integer idUsuario, LocalDateTime ahora) throws Exception {
        // Lo implementa el Desarrollador 1
        throw new Exception("Pendiente: MembresiaDAO.buscarActiva");
    }
}
