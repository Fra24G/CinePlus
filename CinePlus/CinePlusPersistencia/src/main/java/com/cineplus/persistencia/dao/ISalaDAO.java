package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Sala;
import java.util.List;
import java.util.Optional;

/**
 * PATRON DAO: operaciones de base de datos para la entidad Sala.
 *
 * @author luisf
 */
public interface ISalaDAO {

    /** Guarda una sala nueva junto con sus asientos (cascada). */
    Sala insertar(Sala sala) throws Exception;

    /** Salas de un cine ordenadas por numero. */
    List<Sala> listarPorCine(Integer idCine) throws Exception;

    /** Busca una sala por su id. */
    Optional<Sala> buscarPorId(Integer idSala) throws Exception;
}
