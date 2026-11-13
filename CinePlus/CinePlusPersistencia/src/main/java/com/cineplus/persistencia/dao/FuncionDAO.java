package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Funcion;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JPA del DAO de funciones.
 *
 * @author luisf
 */
public class FuncionDAO implements IFuncionDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final EntityManager em;

    public FuncionDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Funcion insertar(Funcion funcion) throws Exception {
        try {
            em.persist(funcion);
            em.flush();
            return funcion;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public Funcion actualizar(Funcion funcion) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionDAO.actualizar");
    }

    @Override
    public void eliminar(Funcion funcion) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionDAO.eliminar");
    }

    @Override
    public Optional<Funcion> buscarPorId(Integer idFuncion) throws Exception {
        try {
            return Optional.ofNullable(em.find(Funcion.class, idFuncion));
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Funcion> listarPorPelicula(Integer idPelicula, Integer idCine, LocalDateTime desde) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionDAO.listarPorPelicula");
    }

    @Override
    public List<Funcion> listarPorPeliculaAdmin(Integer idPelicula) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionDAO.listarPorPeliculaAdmin");
    }

    @Override
    public List<Funcion> listarPorSalaEntre(Integer idSala, LocalDateTime desde, LocalDateTime hasta) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionDAO.listarPorSalaEntre");
    }

    @Override
    public long contarEntre(LocalDateTime desde, LocalDateTime hasta) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionDAO.contarEntre");
    }

    @Override
    public long contarFuturasPorPelicula(Integer idPelicula, LocalDateTime desde) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionDAO.contarFuturasPorPelicula");
    }
}
