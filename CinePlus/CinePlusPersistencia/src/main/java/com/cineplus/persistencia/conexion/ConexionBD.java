package com.cineplus.persistencia.conexion;

import com.cineplus.persistencia.excepciones.PersistenciaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

/**
 * PATRON SINGLETON.
 * Existe una sola EntityManagerFactory la parte pesada de JPA para toda la aplicacion.
 * Se crea la primera vez que alguien la pide y despues siempre se reutiliza.
 *
 * @author luisf
 */
public final class ConexionBD {

    // Nombre de la unidad de persistencia definida en persistence.xml
    private static final String UNIDAD_PERSISTENCIA = "CinePlusPU";

    private EntityManagerFactory fabrica;

    // Constructor privado: nadie puede hacer "new ConexionBD()"
    private ConexionBD() {
    }

    // La unica instancia se crea la primera vez que se usa esta clase interna
    private static final class Holder {
        private static final ConexionBD INSTANCIA = new ConexionBD();
    }

    /** Devuelve la unica instancia de ConexionBD. */
    public static ConexionBD getInstancia() {
        return Holder.INSTANCIA;
    }

    /**
     * Devuelve un EntityManager nuevo. Quien lo pida es responsable de cerrarlo
     * (de eso se encarga el UnitOfWork).
     *
     * @throws PersistenciaException si no se puede conectar con la base de datos
     */
    public synchronized EntityManager crearEntityManager() throws PersistenciaException {
        if (fabrica == null || !fabrica.isOpen()) {
            fabrica = crearFabrica();
        }
        try {
            return fabrica.createEntityManager();
        } catch (PersistenceException | IllegalStateException e) {
            throw new PersistenciaException("No se pudo abrir la conexion con la base de datos", e);
        }
    }

    /** Cierra la fabrica. Se llama al cerrar la aplicacion. */
    public synchronized void cerrar() {
        if (fabrica != null && fabrica.isOpen()) {
            fabrica.close();
        }
        fabrica = null;
    }

    private EntityManagerFactory crearFabrica() throws PersistenciaException {
        try {
            return Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA);
        } catch (PersistenceException e) {
            throw new PersistenciaException(
                    "No se pudo conectar con la base de datos. Revisa MySQL.", e);
        }
    }
}