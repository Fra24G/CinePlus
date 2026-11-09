package com.cineplus.dto;

/**
 * Cambios que el usuario puede hacer a su perfil. Si nuevaContrasena viene vacia, no se cambia.
 *
 * @author luisf
 */
public record ActualizarPerfilDTO(
        String nombre,
        String nombreUsuario,
        String telefono,
        String nuevaContrasena) {
}