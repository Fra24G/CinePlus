package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Sala;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JPA del DAO de salas.
 *
 * @author luisf
 */
public class SalaDAO implements ISalaDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final EntityManager em;

    public SalaDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Sala insertar(Sala sala) throws Exception {
        try {
            // Los asientos de la sala se guardan solos por la cascada de Sala.asientos
            em.persist(sala);
            em.flush();
            return sala;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Sala> listarPorCine(Integer idCine) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: SalaDAO.listarPorCine");
    }

    @Override
    public Optional<Sala> buscarPorId(Integer idSala) throws Exception {
        try {
            return Optional.ofNullable(em.find(Sala.class, idSala));
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }
}
