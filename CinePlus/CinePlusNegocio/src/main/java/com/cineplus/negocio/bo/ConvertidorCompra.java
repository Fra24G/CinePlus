package com.cineplus.negocio.bo;

import com.cineplus.dto.BoletoDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.TransaccionDTO;
import com.cineplus.entidades.Asiento;
import com.cineplus.entidades.Boleto;
import com.cineplus.entidades.Cine;
import com.cineplus.entidades.Compra;
import com.cineplus.entidades.Funcion;
import com.cineplus.entidades.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 * Convierte compras y boletos a DTO. Lo usan CompraBO, BoletoBO e HistorialBO,
 * asi nadie duplica la conversion.
 *
 * @author luisf
 */
final class ConvertidorCompra {

    private ConvertidorCompra() {
    }

    /** Compra completa con sus boletos. */
    static CompraDTO aDTO(Compra compra, boolean cancelable) {
        Funcion funcion = compra.getFuncion();
        List<BoletoDTO> boletos = new ArrayList<>();
        for (Boleto boleto : compra.getBoletos()) {
            boletos.add(boletoADTO(boleto));
        }
        return new CompraDTO(
                compra.getIdCompra(),
                compra.getFechaCompra(),
                funcion.getPelicula().getTitulo(),
                nombreCine(funcion.getSala().getCine()),
                funcion.getSala().getNumeroSala(),
                funcion.getTipo() != null ? funcion.getTipo().getEtiqueta() : "",
                funcion.getFechaHora(),
                valor(compra.getSubtotalBoletos()),
                valor(compra.getCargoServicio()),
                valor(compra.getMontoTotal()),
                entero(compra.getPuntosUsados()),
                valor(compra.getDescuentoPuntos()),
                valor(compra.getMontoCobrado()),
                entero(compra.getPuntosGanados()),
                compra.getMetodoPago() != null ? compra.getMetodoPago().getEtiqueta() : "",
                compra.getUltimosDigitos(),
                compra.getEstadoPago() != null ? compra.getEstadoPago().getEtiqueta() : "",
                compra.getMotivoCancelacion(),
                List.copyOf(compra.getListaDeProductos()),
                boletos,
                cancelable);
    }

    /** Boleto con los datos de su funcion y asiento. */
    static BoletoDTO boletoADTO(Boleto boleto) {
        Funcion funcion = boleto.getFuncion();
        Asiento asiento = boleto.getAsiento();
        return new BoletoDTO(
                boleto.getIdBoleto(),
                boleto.getCompra() != null ? boleto.getCompra().getIdCompra() : null,
                boleto.getCodigo(),
                valor(boleto.getPrecio()),
                funcion.getPelicula().getTitulo(),
                nombreCine(funcion.getSala().getCine()),
                funcion.getSala().getNumeroSala(),
                funcion.getTipo() != null ? funcion.getTipo().getEtiqueta() : "",
                funcion.getFechaHora(),
                asiento.getFila() + asiento.getNumero());
    }

    /** Fila de la tabla de transacciones del admin. */
    static TransaccionDTO transaccionADTO(Compra compra) {
        return new TransaccionDTO(
                compra.getIdCompra(),
                compra.getFechaCompra(),
                nombreCorto(compra.getUsuario()),
                compra.getFuncion().getPelicula().getTitulo(),
                compra.getMetodoPago() != null ? compra.getMetodoPago().getEtiqueta() : "",
                compra.getUltimosDigitos(),
                valor(compra.getMontoCobrado()),
                compra.getEstadoPago() != null ? compra.getEstadoPago().getEtiqueta() : "");
    }

    /** Ejemplo: "Cd. Obregon - Cine 1". */
    static String nombreCine(Cine cine) {
        return cine.getCiudad() + " - " + cine.getNombre();
    }

    /** Ejemplo: "Yazid E." (primer nombre e inicial de la siguiente palabra). */
    static String nombreCorto(Usuario usuario) {
        String[] partes = usuario.getNombre().strip().split("\\s+");
        return partes.length > 1 ? partes[0] + " " + partes[1].charAt(0) + "." : partes[0];
    }

    private static double valor(Double numero) {
        return numero != null ? numero : 0.0;
    }

    private static int entero(Integer numero) {
        return numero != null ? numero : 0;
    }
}
