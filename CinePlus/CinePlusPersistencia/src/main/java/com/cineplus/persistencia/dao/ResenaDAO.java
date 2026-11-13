package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Resena;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Implementacion JPA del DAO de resenas.
 *
 * @author luisf
 */
public class ResenaDAO implements IResenaDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final EntityManager em;

    public ResenaDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Resena insertar(Resena resena) throws Exception {
        try {
            em.persist(resena);
            em.flush();
            return resena;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Resena> listarRecientes(int pagina, int tamano) throws Exception {
        // Lo implementa el Desarrollador 5
        throw new Exception("Pendiente: ResenaDAO.listarRecientes");
    }

    @Override
    public long contarTodas() throws Exception {
        // Lo implementa el Desarrollador 5
        throw new Exception("Pendiente: ResenaDAO.contarTodas");
    }

    @Override
    public List<Resena> listarPorPelicula(Integer idPelicula, int pagina, int tamano) throws Exception {
        // Lo implementa el Desarrollador 5
        throw new Exception("Pendiente: ResenaDAO.listarPorPelicula");
    }

    @Override
    public long contarPorPelicula(Integer idPelicula) throws Exception {
        // Lo implementa el Desarrollador 5
        throw new Exception("Pendiente: ResenaDAO.contarPorPelicula");
    }

    @Override
    public boolean existeDeUsuario(Integer idUsuario, Integer idPelicula) throws Exception {
        // Lo implementa el Desarrollador 5
        throw new Exception("Pendiente: ResenaDAO.existeDeUsuario");
    }
}
