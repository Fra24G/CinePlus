
package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Pelicula;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO encargado de consultar y guardar peliculas en la base de dato
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
        try {
            // merge actualiza una pelicula que ya existe
            Pelicula actualizada = em.merge(pelicula);
            em.flush();
            return actualizada;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
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
        try {
            // Busca una pelicula utilizando su identificador de TMDB
            List<Pelicula> resultados = em.createQuery(
                    "SELECT p FROM Pelicula p WHERE p.idExterno = :id",
                    Pelicula.class)
                    .setParameter("id", idExterno)
                    .getResultList();

            return resultados.stream().findFirst();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Pelicula> listarEnCartelera(LocalDate fecha) throws Exception {
        try {
            // Solo muestra peliculas disponibles dentro de su periodo de cartelera
            return em.createQuery(
                    "SELECT p FROM Pelicula p "
                    + "WHERE p.disponible = true "
                    + "AND (p.inicioCartelera IS NULL OR p.inicioCartelera <= :fecha) "
                    + "AND (p.finCartelera IS NULL OR p.finCartelera >= :fecha) "
                    + "ORDER BY p.titulo",
                    Pelicula.class)
                    .setParameter("fecha", fecha)
                    .getResultList();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Pelicula> listarTodas() throws Exception {
        try {
            // devuelve todo el catalogo ordenado alfabeticamente
            return em.createQuery(
                    "SELECT p FROM Pelicula p ORDER BY p.titulo",
                    Pelicula.class)
                    .getResultList();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Pelicula> buscarPorTitulo(String texto) throws Exception {
        try {
            // el lower permite buscar sin importar mayusculas o minusculas
            return em.createQuery(
                    "SELECT p FROM Pelicula p "
                    + "WHERE LOWER(p.titulo) LIKE :texto "
                    + "ORDER BY p.titulo",
                    Pelicula.class)
                    .setParameter("texto", "%" + texto.toLowerCase().trim() + "%")
                    .getResultList();

        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }
}
