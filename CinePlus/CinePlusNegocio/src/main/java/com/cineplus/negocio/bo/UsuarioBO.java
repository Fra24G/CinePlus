package com.cineplus.negocio.bo;

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
 * Objeto de negocio de usuarios: valida los datos, protege la contrasena
 * y usa el DAO a traves de un UnitOfWork. Los errores se lanzan como
 * Exception con un mensaje claro para el usuario.
 *
 * @author luisf
 */
public class UsuarioBO {

    private final HashContrasena hash = new HashContrasena();

    /** Crea una cuenta nueva de cliente. */
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
            // El correo es unico: se revisa antes de guardar
            if (uow.usuarioDAO().buscarPorCorreo(usuario.getCorreo()).isPresent()) {
                throw new Exception("Ese correo ya tiene una cuenta. Inicia sesión para continuar.");
            }
            uow.usuarioDAO().insertar(usuario);
            uow.commit();
            return aDTO(usuario);
        }
    }

    /** Comprueba correo y contrasena. Si son correctos devuelve el usuario. */
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
                // Mismo mensaje para correo o contrasena incorrectos: no revela cual fallo
                throw new Exception("Correo o contraseña incorrectos.");
            }
            return aDTO(encontrado.get());
        }
    }

    /** Convierte la entidad en DTO. El DTO nunca lleva la contrasena. */
    private UsuarioDTO aDTO(Usuario usuario) {
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