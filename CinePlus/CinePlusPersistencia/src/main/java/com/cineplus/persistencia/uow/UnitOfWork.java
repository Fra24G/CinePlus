package com.cineplus.persistencia.uow;

import com.cineplus.persistencia.conexion.ConexionBD;
import com.cineplus.persistencia.dao.AsientoDAO;
import com.cineplus.persistencia.dao.BoletoDAO;
import com.cineplus.persistencia.dao.CineDAO;
import com.cineplus.persistencia.dao.CompraDAO;
import com.cineplus.persistencia.dao.FuncionDAO;
import com.cineplus.persistencia.dao.IAsientoDAO;
import com.cineplus.persistencia.dao.IBoletoDAO;
import com.cineplus.persistencia.dao.ICineDAO;
import com.cineplus.persistencia.dao.ICompraDAO;
import com.cineplus.persistencia.dao.IFuncionDAO;
import com.cineplus.persistencia.dao.IMembresiaDAO;
import com.cineplus.persistencia.dao.IPeliculaDAO;
import com.cineplus.persistencia.dao.IResenaDAO;
import com.cineplus.persistencia.dao.ISalaDAO;
import com.cineplus.persistencia.dao.IUsuarioDAO;
import com.cineplus.persistencia.dao.MembresiaDAO;
import com.cineplus.persistencia.dao.PeliculaDAO;
import com.cineplus.persistencia.dao.ResenaDAO;
import com.cineplus.persistencia.dao.SalaDAO;
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

    // Cada DAO se crea la primera vez que se pide (creacion perezosa)
    private IUsuarioDAO usuarioDAO;
    private ICineDAO cineDAO;
    private ISalaDAO salaDAO;
    private IAsientoDAO asientoDAO;
    private IPeliculaDAO peliculaDAO;
    private IFuncionDAO funcionDAO;
    private IBoletoDAO boletoDAO;
    private ICompraDAO compraDAO;
    private IMembresiaDAO membresiaDAO;
    private IResenaDAO resenaDAO;

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

    public IUsuarioDAO usuarioDAO() {
        if (usuarioDAO == null) {
            usuarioDAO = new UsuarioDAO(em);
        }
        return usuarioDAO;
    }

    public ICineDAO cineDAO() {
        if (cineDAO == null) {
            cineDAO = new CineDAO(em);
        }
        return cineDAO;
    }

    public ISalaDAO salaDAO() {
        if (salaDAO == null) {
            salaDAO = new SalaDAO(em);
        }
        return salaDAO;
    }

    public IAsientoDAO asientoDAO() {
        if (asientoDAO == null) {
            asientoDAO = new AsientoDAO(em);
        }
        return asientoDAO;
    }

    public IPeliculaDAO peliculaDAO() {
        if (peliculaDAO == null) {
            peliculaDAO = new PeliculaDAO(em);
        }
        return peliculaDAO;
    }

    public IFuncionDAO funcionDAO() {
        if (funcionDAO == null) {
            funcionDAO = new FuncionDAO(em);
        }
        return funcionDAO;
    }

    public IBoletoDAO boletoDAO() {
        if (boletoDAO == null) {
            boletoDAO = new BoletoDAO(em);
        }
        return boletoDAO;
    }

    public ICompraDAO compraDAO() {
        if (compraDAO == null) {
            compraDAO = new CompraDAO(em);
        }
        return compraDAO;
    }

    public IMembresiaDAO membresiaDAO() {
        if (membresiaDAO == null) {
            membresiaDAO = new MembresiaDAO(em);
        }
        return membresiaDAO;
    }

    public IResenaDAO resenaDAO() {
        if (resenaDAO == null) {
            resenaDAO = new ResenaDAO(em);
        }
        return resenaDAO;
    }
}
