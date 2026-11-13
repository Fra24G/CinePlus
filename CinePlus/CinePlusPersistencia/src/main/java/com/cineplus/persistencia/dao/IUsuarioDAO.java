package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Usuario;
import com.cineplus.entidades.enums.Rol;
import java.util.Optional;

/**
 * PATRON DAO: operaciones de base de datos para la entidad Usuario.
 *
 * @author luisf
 */
public interface IUsuarioDAO {

    /**
     * Guarda un usuario nuevo y lo devuelve con su id ya asignado.
     */
    Usuario insertar(Usuario usuario) throws Exception;

    /**
     * Busca un usuario por su correo. Devuelve vacio si no existe.
     */
    Optional<Usuario> buscarPorCorreo(String correo) throws Exception;

    /**
     * Busca un usuario por su id. Devuelve vacio si no existe.
     */
    Optional<Usuario> buscarPorId(Integer idUsuario) throws Exception;

    /**
     * Guarda los cambios de un usuario (puntos, perfil). Requiere transaccion.
     */
    Usuario actualizar(Usuario usuario) throws Exception;

    /**
     * Cuenta cuantos usuarios tienen ese rol.
     */
    long contarPorRol(Rol rol) throws Exception;
}
