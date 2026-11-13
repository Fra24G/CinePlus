package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Asiento;
import java.util.List;

/**
 * PATRON DAO: consultas de la entidad Asiento.
 * Los asientos se guardan en cascada con su Sala, por eso no hay insertar.
 *
 * @author luisf
 */
public interface IAsientoDAO {

    /** Asientos activos de una sala, ordenados por fila y numero. */
    List<Asiento> listarPorSala(Integer idSala) throws Exception;

    /** Asientos con esos ids (los que eligio el usuario). */
    List<Asiento> buscarPorIds(List<Integer> idsAsientos) throws Exception;
}
