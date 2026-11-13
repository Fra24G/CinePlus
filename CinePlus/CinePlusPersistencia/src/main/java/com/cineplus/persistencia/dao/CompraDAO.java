package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Compra;
import com.cineplus.entidades.enums.EstadoPago;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JPA del DAO de compras.
 *
 * @author luisf
 */
public class CompraDAO implements ICompraDAO {

    private static final String MENSAJE_ERROR =
            "No fue posible completar la operación. Inténtalo nuevamente.";

    private final EntityManager em;

    public CompraDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Compra insertar(Compra compra) throws Exception {
        try {
            // Los boletos de la compra se guardan solos por la cascada de Compra.boletos
            em.persist(compra);
            em.flush();
            return compra;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public Compra actualizar(Compra compra) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: CompraDAO.actualizar");
    }

    @Override
    public Optional<Compra> buscarPorId(Integer idCompra) throws Exception {
        try {
            return Optional.ofNullable(em.find(Compra.class, idCompra));
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Compra> buscarPorUsuario(Integer idUsuario, LocalDate desde, LocalDate hasta,
            EstadoPago estado, int pagina, int tamano) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: CompraDAO.buscarPorUsuario");
    }

    @Override
    public long contarPorUsuario(Integer idUsuario, LocalDate desde, LocalDate hasta, EstadoPago estado) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: CompraDAO.contarPorUsuario");
    }

    @Override
    public List<Compra> buscarTodas(String texto, LocalDate desde, LocalDate hasta,
            EstadoPago estado, int pagina, int tamano) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: CompraDAO.buscarTodas");
    }

    @Override
    public long contarTodas(String texto, LocalDate desde, LocalDate hasta, EstadoPago estado) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: CompraDAO.contarTodas");
    }

    @Override
    public long contarBoletosVendidosEntre(LocalDateTime desde, LocalDateTime hasta) throws Exception {
        // Lo implementa el Desarrollador 4
        throw new Exception("Pendiente: CompraDAO.contarBoletosVendidosEntre");
    }
}
