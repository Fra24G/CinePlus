package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Usuario;
import java.util.Optional;

/**
 * PATRON DAO: operaciones de base de datos para la entidad Usuario.
 * Negocio solo conoce esta interfaz, no el SQL.
 *
 */
public interface IUsuarioDAO {

    /** Guarda un usuario nuevo y lo devuelve con su id ya asignado. */
    Usuario insertar(Usuario usuario) throws Exception;

    /** Busca un usuario por su correo. Devuelve vacio si no existe. */
    Optional<Usuario> buscarPorCorreo(String correo) throws Exception;
}