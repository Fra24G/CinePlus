package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Compra;
import com.cineplus.entidades.enums.EstadoPago;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * PATRON DAO: operaciones de base de datos para la entidad Compra.
 * Un filtro en null significa "sin filtro".
 *
 * @author luisf
 */
public interface ICompraDAO {

    /** Guarda una compra nueva con sus boletos (cascada). */
    Compra insertar(Compra compra) throws Exception;

    /** Guarda los cambios de una compra (cancelacion). */
    Compra actualizar(Compra compra) throws Exception;

    /** Busca una compra por su id. */
    Optional<Compra> buscarPorId(Integer idCompra) throws Exception;

    /** Compras de un usuario con filtros y paginacion (historial). */
    List<Compra> buscarPorUsuario(Integer idUsuario, LocalDate desde, LocalDate hasta,
            EstadoPago estado, int pagina, int tamano) throws Exception;

    /** Total de compras de un usuario con esos filtros. */
    long contarPorUsuario(Integer idUsuario, LocalDate desde, LocalDate hasta, EstadoPago estado) throws Exception;

    /** Compras de todos los usuarios con filtros y paginacion (transacciones del admin). */
    List<Compra> buscarTodas(String texto, LocalDate desde, LocalDate hasta,
            EstadoPago estado, int pagina, int tamano) throws Exception;

    /** Total de compras con esos filtros. */
    long contarTodas(String texto, LocalDate desde, LocalDate hasta, EstadoPago estado) throws Exception;

    /** Boletos de compras aprobadas en un rango de fechas. */
    long contarBoletosVendidosEntre(LocalDateTime desde, LocalDateTime hasta) throws Exception;
}
