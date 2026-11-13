package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

/**
 * Implementacion JPA del DAO de usuarios.
 * Recibe el EntityManager del UnitOfWork, asi comparte la transaccion con otros DAOs.
 * No abre ni cierra nada.
 *
 * @author luisf
 */
public class UsuarioDAO implements IUsuarioDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final EntityManager em;

    public UsuarioDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Usuario insertar(Usuario usuario) throws Exception {
        try {
            em.persist(usuario);
            // flush envia el INSERT ya, para que un error salga aqui y no hasta el commit
            em.flush();
            return usuario;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) throws Exception {
        try {
            TypedQuery<Usuario> consulta = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class);
            consulta.setParameter("correo", correo);
            return consulta.getResultList().stream().findFirst();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }
}