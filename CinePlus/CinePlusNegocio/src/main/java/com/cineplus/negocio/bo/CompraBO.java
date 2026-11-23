package com.cineplus.negocio.bo;

import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.CotizacionDTO;
import com.cineplus.dto.SolicitudCompraDTO;
import com.cineplus.entidades.Asiento;
import com.cineplus.entidades.Compra;
import com.cineplus.entidades.Funcion;
import com.cineplus.entidades.Usuario;
import com.cineplus.entidades.enums.Clasificacion;
import com.cineplus.entidades.enums.EstadoPago;
import com.cineplus.negocio.compra.CompraBuilder;
import com.cineplus.negocio.compra.ReglasPrecio;
import com.cineplus.negocio.compra.pago.FabricaPago;
import com.cineplus.negocio.compra.pago.IEstrategiaPago;
import com.cineplus.negocio.compra.pago.ResultadoPago;
import com.cineplus.persistencia.uow.UnitOfWork;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

/**
 * bo de compra, cotización y compra
 *
 * @author yakor
 */
public class CompraBO {

    /** 
     * Calcula los montos y los puntos sin guardar nada. 
     */
    public CotizacionDTO cotizar(Integer idUsuario, SolicitudCompraDTO solicitud, boolean pro) throws Exception {
        ValidacionesCompra.solicitud(solicitud);

        try (UnitOfWork uow = new UnitOfWork()) {
            Usuario usuario = buscarUsuario(uow, idUsuario);
            Funcion funcion = buscarFuncionVendible(uow, solicitud.idFuncion());
            List<Asiento> asientos = validarAsientos(uow, funcion, solicitud.idsAsientos());
            int puntos = puntosDe(usuario);

            return new CompraBuilder(usuario, funcion)
                    .conAsientos(asientos)
                    .conProductos(solicitud.productos())
                    .comoMiembroPro(pro)
                    .usandoPuntos(solicitud.pago().usarPuntos() ? puntos : 0)
                    .cotizacion(puntos);
        }
    }

    /** 
     * Valida, cobra y guarda la compra con los boletos y los puntos
     */
    public CompraDTO comprar(Integer idUsuario, SolicitudCompraDTO solicitud, boolean pro) throws Exception {
        ValidacionesCompra.solicitud(solicitud);

        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();

            Usuario usuario = buscarUsuario(uow, idUsuario);
            Funcion funcion = buscarFuncionVendible(uow, solicitud.idFuncion());
            List<Asiento> asientos = validarAsientos(uow, funcion, solicitud.idsAsientos());
            validarEdad(usuario, funcion);

            int puntos = puntosDe(usuario);
            CompraBuilder builder = new CompraBuilder(usuario, funcion)
                    .conAsientos(asientos)
                    .conProductos(solicitud.productos())
                    .comoMiembroPro(pro)
                    .usandoPuntos(solicitud.pago().usarPuntos() ? puntos : 0);

            double monto = builder.montoACobrar();
            IEstrategiaPago estrategia = FabricaPago.para(solicitud.pago(), monto);
            ResultadoPago resultado = estrategia.cobrar(monto, solicitud.pago());

            if (resultado == null || !resultado.aprobado()) {
                uow.compraDAO().insertar(compraDenegada(builder, usuario, funcion, monto, resultado));
                uow.commit();
                throw new Exception(resultado != null && resultado.mensaje() != null
                        ? resultado.mensaje()
                        : "El pago no fue aprobado. Comprueba tus datos e inténtalo de nuevo.");
            }

            Compra compra = builder.conPago(resultado).construir();
            uow.compraDAO().insertar(compra);

            usuario.setPuntosAcumulados(Math.max(0, puntos - compra.getPuntosUsados() + compra.getPuntosGanados()));
            uow.usuarioDAO().actualizar(usuario);

            uow.commit();
            return ConvertidorCompra.aDTO(compra,
                    ReglasPrecio.esCancelable(funcion.getFechaHora(), LocalDateTime.now()));
        }
    }

    private List<Asiento> validarAsientos(UnitOfWork uow, Funcion funcion, List<Integer> ids) throws Exception {
        List<Asiento> asientos = uow.asientoDAO().buscarPorIds(ids);
        if (asientos.size() != ids.size()) {
            throw new Exception("Uno de los asientos elegidos ya no existe. Vuelve a elegir tus asientos.");
        }

        Integer idSala = funcion.getSala().getIdSala();
        List<Integer> ocupados = uow.boletoDAO().listarAsientosOcupados(funcion.getIdFuncion());
        for (Asiento asiento : asientos) {
            if (!idSala.equals(asiento.getSala().getIdSala())) {
                throw new Exception("El asiento " + nombreAsiento(asiento) + " no pertenece a la sala de esta función.");
            }
            if (ocupados.contains(asiento.getIdAsiento())) {
                throw new Exception("El asiento " + nombreAsiento(asiento) + " fue vendido. Selecciona otro.");
            }
        }
        return asientos;
    }

    private Usuario buscarUsuario(UnitOfWork uow, Integer idUsuario) throws Exception {
        if (idUsuario == null) {
            throw new Exception("No se encontró el usuario.");
        }
        return uow.usuarioDAO().buscarPorId(idUsuario)
                .orElseThrow(() -> new Exception("No se encontró el usuario."));
    }

    private Funcion buscarFuncionVendible(UnitOfWork uow, Integer idFuncion) throws Exception {
        Funcion funcion = uow.funcionDAO().buscarPorId(idFuncion)
                .orElseThrow(() -> new Exception("La función ya no existe."));
        if (!funcion.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new Exception("Esta función ya comenzó. Elige otro horario.");
        }
        return funcion;
    }

    private void validarEdad(Usuario usuario, Funcion funcion) throws Exception {
        Clasificacion clasificacion = funcion.getPelicula().getClasificacion();
        if (clasificacion == null || clasificacion.getEdadMinima() <= 0) {
            return;
        }
        LocalDate nacimiento = usuario.getFechaNacimiento();
        if (nacimiento == null
                || Period.between(nacimiento, LocalDate.now()).getYears() < clasificacion.getEdadMinima()) {
            throw new Exception("Esta película es para mayores de " + clasificacion.getEdadMinima() + " años.");
        }
    }

    private Compra compraDenegada(CompraBuilder builder, Usuario usuario, Funcion funcion, double monto,
            ResultadoPago resultado) throws Exception {
        CotizacionDTO montos = builder.cotizacion(0);

        Compra compra = new Compra();
        compra.setUsuario(usuario);
        compra.setFuncion(funcion);
        compra.setFechaCompra(LocalDateTime.now());
        compra.setSubtotalBoletos(montos.subtotal());
        compra.setCargoServicio(montos.cargoServicio());
        compra.setMontoTotal(montos.montoTotal());
        compra.setMontoCobrado(monto);
        compra.setPuntosUsados(0);
        compra.setDescuentoPuntos(0.0);
        compra.setPuntosGanados(0);
        if (resultado != null) {
            compra.setMetodoPago(resultado.metodo());
            compra.setUltimosDigitos(resultado.ultimosDigitos());
        }
        compra.setEstadoPago(EstadoPago.DENEGADO);
        return compra;
    }

    private static int puntosDe(Usuario usuario) {
        return usuario.getPuntosAcumulados() == null ? 0 : usuario.getPuntosAcumulados();
    }

    private static String nombreAsiento(Asiento asiento) {
        return asiento.getFila() + asiento.getNumero();
    }
}
