
package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Asiento;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

/**
 * DAO encargado de consultar los asientos de las salas
 */
public class AsientoDAO implements IAsientoDAO {

    private final EntityManager em;

    public AsientoDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Asiento> listarPorSala(Integer idSala) throws Exception {
        try {
            // Consulta los asientos activos de una sala
            // Se ordenan primero por fila y despues por numero
            return em.createQuery(
                    "SELECT a FROM Asiento a "
                    + "WHERE a.sala.idSala = :idSala "
                    + "AND a.estado = true "
                    + "ORDER BY a.fila, a.numero",
                    Asiento.class)
                    .setParameter("idSala", idSala)
                    .getResultList();

        } catch (RuntimeException e) {
            throw new Exception("Error al consultar los asientos de la sala.", e);
        }
    }

    @Override
    public List<Asiento> buscarPorIds(List<Integer> idsAsientos) throws Exception {
        try {
            // Si no se recibieron identificadores, devolvemos una lista vacia
            if (idsAsientos == null || idsAsientos.isEmpty()) {
                return Collections.emptyList();
            }

            // Busca todos los asientos cuyos identificadores esten en la lista
            return em.createQuery(
                    "SELECT a FROM Asiento a "
                    + "WHERE a.idAsiento IN :ids",
                    Asiento.class)
                    .setParameter("ids", idsAsientos)
                    .getResultList();

        } catch (RuntimeException e) {
            throw new Exception("Error al buscar los asientos seleccionados.", e);
        }
    }
}
