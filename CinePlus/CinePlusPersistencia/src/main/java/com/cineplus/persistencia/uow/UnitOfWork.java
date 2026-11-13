package com.cineplus.persistencia.uow;

import com.cineplus.persistencia.conexion.ConexionBD;
import com.cineplus.persistencia.dao.IUsuarioDAO;
import com.cineplus.persistencia.dao.UsuarioDAO;
import jakarta.persistence.EntityManager;

/**
 * PATRON UNIT OF WORK. Agrupa varias operaciones sobre la base de datos en UNA
 * sola transaccion. Todos los DAOs que se obtienen de aqui comparten el mismo
 * EntityManager, asi que o se guardan todas las operaciones juntas (commit) o
 * ninguna (rollback). Ejemplo: al pagar, guardar la compra, sus boletos y
 * actualizar los puntos del usuario tienen que ocurrir completos o no ocurrir.
 *
 * Uso tipico en un BO de Negocio:
 * try (UnitOfWork uow = new UnitOfWork()) {
 *     uow.iniciar();
 *     ... usar los DAOs de uow ...
 *     uow.commit();
 * } // close() hace rollback si no se llego al commit y cierra el EntityManager
 * Para solo consultar no hace falta llamar a iniciar().
 *
 * @author luisf
 */
public class UnitOfWork implements AutoCloseable {

    private final EntityManager em;
    private IUsuarioDAO usuarioDAO;

    /** Abre un EntityManager nuevo. */
    public UnitOfWork() throws Exception {
        this.em = ConexionBD.getInstancia().crearEntityManager();
    }

    /** Inicia la transaccion: desde aqui nada se guarda hasta hacer commit. */
    public void iniciar() throws Exception {
        try {
            em.getTransaction().begin();
        } catch (RuntimeException e) {
            throw new Exception("No se pudo iniciar la transacción.", e);
        }
    }

    /** Confirma todos los cambios. Si falla, los deshace y lanza la excepcion. */
    public void commit() throws Exception {
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            rollback();
            throw new Exception("No se pudieron guardar los cambios.", e);
        }
    }

    /** Deshace todos los cambios desde iniciar(). */
    public void rollback() {
        try {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (RuntimeException e) {
            // Si el rollback falla no hay mas que hacer: close() cerrara el EntityManager
        }
    }

    /** EntityManager compartido que se le pasa a cada DAO. */
    public EntityManager getEntityManager() {
        return em;
    }

    /** Si no se llego al commit, deshace todo; despues cierra el EntityManager. */
    @Override
    public void close() {
        try {
            rollback();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /** DAO de usuarios. Comparte el EntityManager (y la transaccion) de este UnitOfWork. */
    public IUsuarioDAO usuarioDAO() {
        if (usuarioDAO == null) {
            usuarioDAO = new UsuarioDAO(em);
        }
        return usuarioDAO;
    }
}