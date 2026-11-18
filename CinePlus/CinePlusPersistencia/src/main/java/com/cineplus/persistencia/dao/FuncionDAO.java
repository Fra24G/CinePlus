
package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Funcion;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DAO encargado de las operaciones de base de datos
 * relacionadas con las funciones del cine
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
        try {
            // Actualiza una funcion existente
            Funcion actualizada = em.merge(funcion);
            em.flush();
            return actualizada;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public void eliminar(Funcion funcion) throws Exception {
        try {
            // Primero verificamos que la funcion este administrada por JPA
            Funcion funcionEliminar = em.contains(funcion)
                    ? funcion
                    : em.merge(funcion);

            em.remove(funcionEliminar);
            em.flush();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
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
    public List<Funcion> listarPorPelicula(
            Integer idPelicula,
            Integer idCine,
            LocalDateTime desde) throws Exception {

        try {
            // Busca funciones futuras de una pelicula en un cine especifico
            return em.createQuery(
                    "SELECT f FROM Funcion f "
                    + "WHERE f.pelicula.idPelicula = :idPelicula "
                    + "AND f.sala.cine.idCine = :idCine "
                    + "AND f.fechaHora >= :desde "
                    + "ORDER BY f.fechaHora",
                    Funcion.class)
                    .setParameter("idPelicula", idPelicula)
                    .setParameter("idCine", idCine)
                    .setParameter("desde", desde)
                    .getResultList();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Funcion> listarPorPeliculaAdmin(Integer idPelicula) throws Exception {
        try {
            // El administrador puede consultar todas las funciones de la pelicula
            return em.createQuery(
                    "SELECT f FROM Funcion f "
                    + "WHERE f.pelicula.idPelicula = :idPelicula "
                    + "ORDER BY f.fechaHora",
                    Funcion.class)
                    .setParameter("idPelicula", idPelicula)
                    .getResultList();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Funcion> listarPorSalaEntre(
            Integer idSala,
            LocalDateTime desde,
            LocalDateTime hasta) throws Exception {

        try {
            // Consulta las funciones que pertenecen a una sala dentro del rango
            return em.createQuery(
                    "SELECT f FROM Funcion f "
                    + "WHERE f.sala.idSala = :idSala "
                    + "AND f.fechaHora >= :desde "
                    + "AND f.fechaHora <= :hasta "
                    + "ORDER BY f.fechaHora",
                    Funcion.class)
                    .setParameter("idSala", idSala)
                    .setParameter("desde", desde)
                    .setParameter("hasta", hasta)
                    .getResultList();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public long contarEntre(LocalDateTime desde, LocalDateTime hasta) throws Exception {
        try {
            // Cuenta las funciones registradas dentro del periodo indicado
            return em.createQuery(
                    "SELECT COUNT(f) FROM Funcion f "
                    + "WHERE f.fechaHora >= :desde "
                    + "AND f.fechaHora <= :hasta",
                    Long.class)
                    .setParameter("desde", desde)
                    .setParameter("hasta", hasta)
                    .getSingleResult();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public long contarFuturasPorPelicula(
            Integer idPelicula,
            LocalDateTime desde) throws Exception {

        try {
            // Cuenta las funciones futuras de una pelicula
            return em.createQuery(
                    "SELECT COUNT(f) FROM Funcion f "
                    + "WHERE f.pelicula.idPelicula = :idPelicula "
                    + "AND f.fechaHora >= :desde",
                    Long.class)
                    .setParameter("idPelicula", idPelicula)
                    .setParameter("desde", desde)
                    .getSingleResult();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }
}
