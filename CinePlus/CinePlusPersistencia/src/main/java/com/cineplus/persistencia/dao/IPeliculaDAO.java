package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Pelicula;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * PATRON DAO: operaciones de base de datos para la entidad Pelicula (catalogo local).
 *
 * @author luisf
 */
public interface IPeliculaDAO {

    /** Guarda una pelicula nueva. */
    Pelicula insertar(Pelicula pelicula) throws Exception;

    /** Guarda los cambios de una pelicula. */
    Pelicula actualizar(Pelicula pelicula) throws Exception;

    /** Busca una pelicula por su id. */
    Optional<Pelicula> buscarPorId(Integer idPelicula) throws Exception;

    /** Busca por el id de TMDB, para no importar la misma pelicula dos veces. */
    Optional<Pelicula> buscarPorIdExterno(String idExterno) throws Exception;

    /** Peliculas disponibles cuyo periodo de cartelera incluye esa fecha. */
    List<Pelicula> listarEnCartelera(LocalDate fecha) throws Exception;

    /** Todo el catalogo, ordenado por titulo. */
    List<Pelicula> listarTodas() throws Exception;

    /** Peliculas cuyo titulo contiene el texto. */
    List<Pelicula> buscarPorTitulo(String texto) throws Exception;
}
