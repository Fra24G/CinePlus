package com.cineplus.presentacion;

import com.cineplus.dto.TransaccionDTO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo de la tabla "Historial de Transacciones" del admin.
 * Columnas: ID, Hora, Cliente, Concepto, Metodos, Total, Estado. El estado se pinta
 * con ModeloTablaCompras.RendererEstado (Aprobado en verde y Denegado en rojo).
 *
 * @author Mario Perez
 */
public class ModeloTablaTransacciones extends AbstractTableModel {

    private static final String[] COLUMNAS = {"ID", "Hora", "Cliente", "Concepto", "Métodos", "Total", "Estado"};
    /** Columna del estado, para ponerle el renderer. */
    public static final int COLUMNA_ESTADO = 6;

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
            case 0 -> FormatoCompra.numeroCompra(transaccion.idCompra());
            case 1 -> FormatoCompra.fechaHora(transaccion.fechaHora());
            case 2 -> transaccion.cliente();
            case 3 -> transaccion.concepto();
            case 4 -> FormatoCompra.metodo(transaccion.metodo(), transaccion.ultimosDigitos());
            case 5 -> FormatoCompra.dinero(transaccion.total());
            case 6 -> transaccion.estado();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }
}
