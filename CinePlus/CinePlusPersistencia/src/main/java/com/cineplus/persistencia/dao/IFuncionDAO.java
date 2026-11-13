package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Funcion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * PATRON DAO: operaciones de base de datos para la entidad Funcion.
 *
 * @author luisf
 */
public interface IFuncionDAO {

    /** Guarda una funcion nueva. */
    Funcion insertar(Funcion funcion) throws Exception;

    /** Guarda los cambios de una funcion. */
    Funcion actualizar(Funcion funcion) throws Exception;

    /** Elimina una funcion. */
    void eliminar(Funcion funcion) throws Exception;

    /** Busca una funcion por su id. */
    Optional<Funcion> buscarPorId(Integer idFuncion) throws Exception;

    /** Funciones de una pelicula en un cine a partir de una fecha y hora. */
    List<Funcion> listarPorPelicula(Integer idPelicula, Integer idCine, LocalDateTime desde) throws Exception;

    /** Todas las funciones de una pelicula (vista del admin). */
    List<Funcion> listarPorPeliculaAdmin(Integer idPelicula) throws Exception;

    /** Funciones de una sala en un rango, para revisar empalmes. */
    List<Funcion> listarPorSalaEntre(Integer idSala, LocalDateTime desde, LocalDateTime hasta) throws Exception;

    /** Funciones publicadas en un rango. */
    long contarEntre(LocalDateTime desde, LocalDateTime hasta) throws Exception;

    /** Funciones futuras de una pelicula. */
    long contarFuturasPorPelicula(Integer idPelicula, LocalDateTime desde) throws Exception;
}
