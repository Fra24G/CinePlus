package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Boleto;
import com.cineplus.entidades.enums.EstadoPago;
import java.util.List;

/**
 * PATRON DAO: consultas de la entidad Boleto.
 * Los boletos se guardan en cascada con su Compra, por eso no hay insertar.
 *
 * @author luisf
 */
public interface IBoletoDAO {

    /** Ids de los asientos ya vendidos en una funcion (sin contar compras canceladas ni denegadas). */
    List<Integer> listarAsientosOcupados(Integer idFuncion) throws Exception;

    /** Boletos vigentes de un usuario, paginados. */
    List<Boleto> listarPorUsuario(Integer idUsuario, int pagina, int tamano) throws Exception;

    /** Cuantos boletos vigentes tiene un usuario. */
    long contarPorUsuario(Integer idUsuario) throws Exception;

    /** Cuantos boletos vendidos (compras aprobadas) tiene una funcion. */
    long contarPorFuncion(Integer idFuncion) throws Exception;
}
