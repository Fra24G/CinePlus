package com.cineplus.persistencia.dao;

import com.cineplus.entidades.Compra;
import com.cineplus.entidades.enums.EstadoPago;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JPA del DAO de compras.
 *
 * @author Mario Perez
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
        try {
            Compra actualizada = em.merge(compra);
            em.flush();
            return actualizada;
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
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
        try {
            Filtro filtro = new Filtro(idUsuario, null, desde, hasta, estado);
            TypedQuery<Compra> consulta = em.createQuery(
                    "SELECT c FROM Compra c" + filtro.where() + " ORDER BY c.fechaCompra DESC, c.idCompra DESC",
                    Compra.class);
            filtro.aplicar(consulta);
            return consulta
                    .setFirstResult(pagina * tamano)
                    .setMaxResults(tamano)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public long contarPorUsuario(Integer idUsuario, LocalDate desde, LocalDate hasta, EstadoPago estado) throws Exception {
        try {
            Filtro filtro = new Filtro(idUsuario, null, desde, hasta, estado);
            TypedQuery<Long> consulta = em.createQuery(
                    "SELECT COUNT(c) FROM Compra c" + filtro.where(), Long.class);
            filtro.aplicar(consulta);
            return consulta.getSingleResult();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public List<Compra> buscarTodas(String texto, LocalDate desde, LocalDate hasta,
            EstadoPago estado, int pagina, int tamano) throws Exception {
        try {
            Filtro filtro = new Filtro(null, texto, desde, hasta, estado);
            TypedQuery<Compra> consulta = em.createQuery(
                    "SELECT c FROM Compra c" + filtro.where() + " ORDER BY c.fechaCompra DESC, c.idCompra DESC",
                    Compra.class);
            filtro.aplicar(consulta);
            return consulta
                    .setFirstResult(pagina * tamano)
                    .setMaxResults(tamano)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public long contarTodas(String texto, LocalDate desde, LocalDate hasta, EstadoPago estado) throws Exception {
        try {
            Filtro filtro = new Filtro(null, texto, desde, hasta, estado);
            TypedQuery<Long> consulta = em.createQuery(
                    "SELECT COUNT(c) FROM Compra c" + filtro.where(), Long.class);
            filtro.aplicar(consulta);
            return consulta.getSingleResult();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    @Override
    public long contarBoletosVendidosEntre(LocalDateTime desde, LocalDateTime hasta) throws Exception {
        try {
            return em.createQuery(
                    "SELECT COUNT(b) FROM Boleto b "
                    + "WHERE b.compra.estadoPago = :aprobado "
                    + "AND b.compra.fechaCompra >= :desde "
                    + "AND b.compra.fechaCompra < :hasta", Long.class)
                    .setParameter("aprobado", EstadoPago.APROBADO)
                    .setParameter("desde", desde)
                    .setParameter("hasta", hasta)
                    .getSingleResult();
        } catch (RuntimeException e) {
            throw new Exception(MENSAJE_ERROR, e);
        }
    }

    /**
     * Arma el WHERE de las consultas del historial y de transacciones.
     * Cada filtro en null (o texto vacio) se ignora. Asi la consulta de la lista
     * y la de COUNT usan exactamente las mismas condiciones.
     */
    private static final class Filtro {

        private final Integer idUsuario;
        private final String texto;
        private final Integer idCompraBuscada;
        private final LocalDateTime desde;
        private final LocalDateTime hasta;
        private final EstadoPago estado;

        Filtro(Integer idUsuario, String texto, LocalDate desde, LocalDate hasta, EstadoPago estado) {
            this.idUsuario = idUsuario;
            this.texto = (texto == null || texto.isBlank()) ? null : texto.strip().toLowerCase();
            this.idCompraBuscada = this.texto == null ? null : numeroDeCompra(this.texto);
            this.desde = desde == null ? null : desde.atStartOfDay();
            this.hasta = hasta == null ? null : hasta.plusDays(1).atStartOfDay();
            this.estado = estado;
        }

        String where() {
            List<String> condiciones = new ArrayList<>();
            if (idUsuario != null) {
                condiciones.add("c.usuario.idUsuario = :idUsuario");
            }
            if (texto != null) {
                String busqueda = "LOWER(c.usuario.nombre) LIKE :texto"
                        + " OR LOWER(c.funcion.pelicula.titulo) LIKE :texto";
                if (idCompraBuscada != null) {
                    busqueda += " OR c.idCompra = :idCompra";
                }
                condiciones.add("(" + busqueda + ")");
            }
            if (desde != null) {
                condiciones.add("c.fechaCompra >= :desde");
            }
            if (hasta != null) {
                condiciones.add("c.fechaCompra < :hasta");
            }
            if (estado != null) {
                condiciones.add("c.estadoPago = :estado");
            }
            return condiciones.isEmpty() ? "" : " WHERE " + String.join(" AND ", condiciones);
        }

        void aplicar(Query consulta) {
            if (idUsuario != null) {
                consulta.setParameter("idUsuario", idUsuario);
            }
            if (texto != null) {
                consulta.setParameter("texto", "%" + texto + "%");
                if (idCompraBuscada != null) {
                    consulta.setParameter("idCompra", idCompraBuscada);
                }
            }
            if (desde != null) {
                consulta.setParameter("desde", desde);
            }
            if (hasta != null) {
                consulta.setParameter("hasta", hasta);
            }
            if (estado != null) {
                consulta.setParameter("estado", estado);
            }
        }

        private static Integer numeroDeCompra(String texto) {
            String limpio = texto.startsWith("#") ? texto.substring(1) : texto;
            if (limpio.isEmpty() || limpio.length() > 9 || !limpio.chars().allMatch(Character::isDigit)) {
                return null;
            }
            return Integer.valueOf(limpio);
        }
    }
}
