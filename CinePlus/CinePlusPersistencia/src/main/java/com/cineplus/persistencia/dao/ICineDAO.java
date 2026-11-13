package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Cine;
import java.util.List;
import java.util.Optional;

/**
 * PATRON DAO: operaciones de base de datos para la entidad Cine.
 *
 * @author luisf
 */
public interface ICineDAO {

    /** Guarda un cine nuevo y lo devuelve con su id. */
    Cine insertar(Cine cine) throws Exception;

    /** Todos los cines ordenados por nombre. */
    List<Cine> listarTodos() throws Exception;

    /** Busca un cine por su id. */
    Optional<Cine> buscarPorId(Integer idCine) throws Exception;

    /** Cuantos cines hay (la semilla lo usa para saber si la BD esta vacia). */
    long contar() throws Exception;
}
