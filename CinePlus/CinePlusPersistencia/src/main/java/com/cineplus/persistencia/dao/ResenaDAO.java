package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Resena;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Implementacion JPA del DAO de resenas.
 *
 * @author Alec Enrique Navarro Gonzalez
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
        try {
            return em.createQuery(
                    "SELECT r FROM Resena r ORDER BY r.fecha DESC",
                    Resena.class)
                    .setFirstResult(pagina * tamano)
                    .setMaxResults(tamano)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public long contarTodas() throws Exception {
        try {
            return em.createQuery(
                    "SELECT COUNT(r) FROM Resena r",
                    Long.class)
                    .getSingleResult();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Resena> listarPorPelicula(Integer idPelicula, int pagina, int tamano) throws Exception {
        try {
            return em.createQuery(
                    "SELECT r FROM Resena r WHERE r.pelicula.idPelicula = :idPelicula ORDER BY r.fecha DESC",
                    Resena.class)
                    .setParameter("idPelicula", idPelicula)
                    .setFirstResult(pagina * tamano)
                    .setMaxResults(tamano)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public long contarPorPelicula(Integer idPelicula) throws Exception {
        try {
            return em.createQuery(
                    "SELECT COUNT(r) FROM Resena r WHERE r.pelicula.idPelicula = :idPelicula",
                    Long.class)
                    .setParameter("idPelicula", idPelicula)
                    .getSingleResult();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public boolean existeDeUsuario(Integer idUsuario, Integer idPelicula) throws Exception {
        try {
            Long total = em.createQuery(
                    "SELECT COUNT(r) FROM Resena r WHERE r.usuario.idUsuario = :idUsuario AND r.pelicula.idPelicula = :idPelicula",
                    Long.class)
                    .setParameter("idUsuario", idUsuario)
                    .setParameter("idPelicula", idPelicula)
                    .getSingleResult();
            return total != null && total > 0;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }
}
