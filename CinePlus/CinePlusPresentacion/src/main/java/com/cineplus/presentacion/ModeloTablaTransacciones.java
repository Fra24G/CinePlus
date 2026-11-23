package com.cineplus.presentacion;

import com.cineplus.dto.TransaccionDTO;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo de la tabla "Historial de Transacciones" del admin.
 * Columnas: ID, Hora, Cliente, Concepto, Metodos, Total, Estado. Usa el mismo
 * formato que la tabla de compras.
 *
 * @author Mario Perez
 */
public class ModeloTablaTransacciones extends AbstractTableModel {

    private static final String[] COLUMNAS = {"ID", "Hora", "Cliente", "Concepto", "Métodos", "Total", "Estado"};
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final List<TransaccionDTO> transacciones = new ArrayList<>();

    /** Reemplaza las filas y avisa a la tabla. */
    public void mostrar(List<TransaccionDTO> nuevas) {
        transacciones.clear();
        if (nuevas != null) {
            transacciones.addAll(nuevas);
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return transacciones.size();
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
        TransaccionDTO transaccion = transacciones.get(fila);
        return switch (columna) {
            case 0 -> transaccion.idCompra() == null ? "" : "#" + transaccion.idCompra();
            case 1 -> transaccion.fechaHora() == null ? "" : FECHA_HORA.format(transaccion.fechaHora());
            case 2 -> transaccion.cliente();
            case 3 -> transaccion.concepto();
            case 4 -> metodo(transaccion.metodo(), transaccion.ultimosDigitos());
            case 5 -> dinero(transaccion.total());
            case 6 -> transaccion.estado();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    private static String metodo(String metodo, String ultimosDigitos) {
        if (ultimosDigitos != null && !ultimosDigitos.isBlank()) {
            return "... " + ultimosDigitos;
        }
        return metodo == null ? "" : metodo;
    }

    private static String dinero(double monto) {
        DecimalFormat formato = new DecimalFormat(monto == Math.floor(monto) ? "#,##0" : "#,##0.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return "$" + formato.format(monto);
    }
}
