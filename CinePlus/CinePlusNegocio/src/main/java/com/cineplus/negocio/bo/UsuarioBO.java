package com.cineplus.negocio.bo;

import com.cineplus.dto.ActualizarPerfilDTO;
import com.cineplus.dto.CredencialesDTO;
import com.cineplus.dto.RegistroUsuarioDTO;
import com.cineplus.dto.RolDTO;
import com.cineplus.dto.UsuarioDTO;
import com.cineplus.entidades.Usuario;
import com.cineplus.entidades.enums.Rol;
import com.cineplus.negocio.seguridad.HashContrasena;
import com.cineplus.persistencia.uow.UnitOfWork;
import java.util.Optional;

/**
 * Reglas de negocio del usuario: registro, inicio de sesion, consulta y perfil.
 *
 * @author Luis Guillermo Franco Murrieta
 */
public class UsuarioBO {

    private final HashContrasena hash = new HashContrasena();

    public UsuarioDTO registrar(RegistroUsuarioDTO datos) throws Exception {
        Validaciones.registro(datos);

        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre().strip());
        usuario.setNombreUsuario(Validaciones.textoOpcional(datos.nombreUsuario()));
        usuario.setCorreo(Validaciones.correo(datos.correo()));
        usuario.setTelefono(Validaciones.textoOpcional(datos.telefono()));
        usuario.setFechaNacimiento(datos.fechaNacimiento());
        usuario.setRol(Rol.CLIENTE);
        usuario.setContrasena(hash.crear(datos.contrasena()));

        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();
            if (uow.usuarioDAO().buscarPorCorreo(usuario.getCorreo()).isPresent()) {
                throw new Exception("Ese correo ya tiene una cuenta. Inicia sesión para continuar.");
            }
            uow.usuarioDAO().insertar(usuario);
            uow.commit();
            return aDTO(usuario);
        }
    }

    public UsuarioDTO autenticar(CredencialesDTO datos) throws Exception {
        if (datos == null) {
            throw new Exception("Captura tu correo y contraseña.");
        }
        String correo = Validaciones.correo(datos.correo());
        if (datos.contrasena() == null || datos.contrasena().length == 0
                || datos.contrasena().length > 72) {
            throw new Exception("Captura tu contraseña.");
        }
        try (UnitOfWork uow = new UnitOfWork()) {
            Optional<Usuario> encontrado = uow.usuarioDAO().buscarPorCorreo(correo);
            if (encontrado.isEmpty()
                    || !hash.coincide(datos.contrasena(), encontrado.get().getContrasena())) {
                throw new Exception("Correo o contraseña incorrectos.");
            }
            return aDTO(encontrado.get());
        }
    }

    /**
     * Lee el usuario de la BD (puntos al dia) y lo devuelve como DTO, sin contrasena.
     */
    public UsuarioDTO obtener(Integer idUsuario) throws Exception {
        if (idUsuario == null) {
            throw new Exception("No se encontró el usuario.");
        }
        try (UnitOfWork uow = new UnitOfWork()) {
            Usuario usuario = uow.usuarioDAO().buscarPorId(idUsuario)
                    .orElseThrow(() -> new Exception("No se encontró el usuario."));
            return aDTO(usuario);
        }
    }

    /**
     * Actualiza nombre, nombre de usuario y telefono. Si viene una contrasena
     * nueva, la guarda con BCrypt. Correo, rol y puntos no se modifican aqui.
     */
    public UsuarioDTO actualizarPerfil(Integer idUsuario, ActualizarPerfilDTO datos) throws Exception {
        ValidacionesCuenta.perfil(datos);
        if (idUsuario == null) {
            throw new Exception("No se encontró el usuario.");
        }
        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();
            Usuario usuario = uow.usuarioDAO().buscarPorId(idUsuario)
                    .orElseThrow(() -> new Exception("No se encontró el usuario."));

            usuario.setNombre(datos.nombre().strip());
            usuario.setNombreUsuario(Validaciones.textoOpcional(datos.nombreUsuario()));
            usuario.setTelefono(Validaciones.textoOpcional(datos.telefono()));
            if (ValidacionesCuenta.cambiaContrasena(datos)) {
                usuario.setContrasena(hash.crear(datos.nuevaContrasena().toCharArray()));
            }

            Usuario actualizado = uow.usuarioDAO().actualizar(usuario);
            uow.commit();
            return aDTO(actualizado);
        }
    }

    static UsuarioDTO aDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getFechaNacimiento(),
                usuario.getPuntosAcumulados(),
                RolDTO.valueOf(usuario.getRol().name()));
    }
}
