package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Membresia;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * PATRON DAO: operaciones de base de datos para la entidad Membresia.
 *
 * @author luisf
 */
public interface IMembresiaDAO {

    /** Guarda una membresia nueva. */
    Membresia insertar(Membresia membresia) throws Exception;

    /** Guarda los cambios de una membresia. */
    Membresia actualizar(Membresia membresia) throws Exception;

    /** Busca una membresia por su id. */
    Optional<Membresia> buscarPorId(Integer idMembresia) throws Exception;

    /** Membresia activa (vigente) de un usuario, si tiene. */
    Optional<Membresia> buscarActiva(Integer idUsuario, LocalDateTime ahora) throws Exception;
}
