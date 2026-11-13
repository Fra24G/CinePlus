package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Asiento;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Implementacion JPA del DAO de asientos.
 *
 * @author luisf
 */
public class AsientoDAO implements IAsientoDAO {

    private final EntityManager em;

    public AsientoDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Asiento> listarPorSala(Integer idSala) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: AsientoDAO.listarPorSala");
    }

    @Override
    public List<Asiento> buscarPorIds(List<Integer> idsAsientos) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: AsientoDAO.buscarPorIds");
    }
}
