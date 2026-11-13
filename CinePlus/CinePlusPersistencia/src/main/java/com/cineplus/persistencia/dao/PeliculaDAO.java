package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Pelicula;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JPA del DAO de peliculas.
 *
 * @author luisf
 */
public class PeliculaDAO implements IPeliculaDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final EntityManager em;

    public PeliculaDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Pelicula insertar(Pelicula pelicula) throws Exception {
        try {
            em.persist(pelicula);
            em.flush();
            return pelicula;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public Pelicula actualizar(Pelicula pelicula) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaDAO.actualizar");
    }

    @Override
    public Optional<Pelicula> buscarPorId(Integer idPelicula) throws Exception {
        try {
            return Optional.ofNullable(em.find(Pelicula.class, idPelicula));
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public Optional<Pelicula> buscarPorIdExterno(String idExterno) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaDAO.buscarPorIdExterno");
    }

    @Override
    public List<Pelicula> listarEnCartelera(LocalDate fecha) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaDAO.listarEnCartelera");
    }

    @Override
    public List<Pelicula> listarTodas() throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaDAO.listarTodas");
    }

    @Override
    public List<Pelicula> buscarPorTitulo(String texto) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaDAO.buscarPorTitulo");
    }
}
