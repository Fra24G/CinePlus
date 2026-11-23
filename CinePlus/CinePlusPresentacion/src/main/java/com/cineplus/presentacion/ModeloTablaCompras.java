package com.cineplus.presentacion;

import com.cineplus.dto.CompraDTO;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo de la tabla "Historial de compras".
 * Columnas: ID, Fecha, Hora, Concepto, Metodos, Total, Estado.
 *
 * @author Mario Perez
 */
public class ModeloTablaCompras extends AbstractTableModel {

    private static final String[] COLUMNAS = {"ID", "Fecha", "Hora", "Concepto", "Métodos", "Total", "Estado"};
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final List<CompraDTO> compras = new ArrayList<>();

    /** Reemplaza las filas y avisa a la tabla. */
    public void mostrar(List<CompraDTO> nuevas) {
        compras.clear();
        if (nuevas != null) {
            compras.addAll(nuevas);
        }
        fireTableDataChanged();
    }

    /** Compra de la fila seleccionada, o null si la fila no existe. */
    public CompraDTO compraEn(int fila) {
        return fila >= 0 && fila < compras.size() ? compras.get(fila) : null;
    }

    @Override
    public int getRowCount() {
        return compras.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        CompraDTO compra = compras.get(fila);
        return switch (columna) {
            case 0 -> compra.idCompra() == null ? "" : "#" + compra.idCompra();
            case 1 -> formato(compra.fechaCompra(), FECHA);
            case 2 -> formato(compra.fechaCompra(), HORA);
            case 3 -> compra.tituloPelicula();
            case 4 -> metodo(compra.metodoPago(), compra.ultimosDigitos());
            case 5 -> dinero(compra.montoCobrado());
            case 6 -> compra.estado();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    private static String formato(LocalDateTime fecha, DateTimeFormatter formato) {
        return fecha == null ? "" : formato.format(fecha);
    }

    /** "... 3845" si se pago con tarjeta; si no, el nombre del metodo. */
    private static String metodo(String metodo, String ultimosDigitos) {
        if (ultimosDigitos != null && !ultimosDigitos.isBlank()) {
            return "... " + ultimosDigitos;
        }
        return metodo == null ? "" : metodo;
    }

    /** "$300" o "$179.50". */
    private static String dinero(double monto) {
        DecimalFormat formato = new DecimalFormat(monto == Math.floor(monto) ? "#,##0" : "#,##0.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return "$" + formato.format(monto);
    }
}
