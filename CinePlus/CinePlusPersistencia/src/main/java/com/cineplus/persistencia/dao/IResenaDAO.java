package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Resena;
import java.util.List;

/**
 * PATRON DAO: operaciones de base de datos para la entidad Resena.
 *
 * @author luisf
 */
public interface IResenaDAO {

    /** Guarda una resena nueva. */
    Resena insertar(Resena resena) throws Exception;

    /** Todas las resenas, de la mas reciente a la mas antigua, paginadas. */
    List<Resena> listarRecientes(int pagina, int tamano) throws Exception;

    /** Total de resenas. */
    long contarTodas() throws Exception;

    /** Resenas de una pelicula, paginadas. */
    List<Resena> listarPorPelicula(Integer idPelicula, int pagina, int tamano) throws Exception;

    /** Total de resenas de una pelicula. */
    long contarPorPelicula(Integer idPelicula) throws Exception;

    /** true si el usuario ya reseno esa pelicula. */
    boolean existeDeUsuario(Integer idUsuario, Integer idPelicula) throws Exception;
}
