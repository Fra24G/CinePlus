package com.cineplus.presentacion;

import com.cineplus.dto.CompraDTO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Modelo de la tabla "Historial de compras".
 * Columnas: ID, Fecha, Hora, Concepto, Metodos, Total, Estado.
 *
 * @author Mario Perez
 */
public class ModeloTablaCompras extends AbstractTableModel {

    private static final String[] COLUMNAS = {"ID", "Fecha", "Hora", "Concepto", "Métodos", "Total", "Estado"};
    /** Columna del estado, para ponerle RendererEstado. */
    public static final int COLUMNA_ESTADO = 6;

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
            case 0 -> FormatoCompra.numeroCompra(compra.idCompra());
            case 1 -> FormatoCompra.fecha(compra.fechaCompra());
            case 2 -> FormatoCompra.hora(compra.fechaCompra());
            case 3 -> compra.tituloPelicula();
            case 4 -> FormatoCompra.metodo(compra.metodoPago(), compra.ultimosDigitos());
            case 5 -> FormatoCompra.dinero(compra.montoCobrado());
            case 6 -> compra.estado();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    /**
     * Estilo comun de las tablas de historial y transacciones: filas altas, sin
     * reordenar columnas, una sola seleccion y el estado con color.
     */
    public static void configurarTabla(JTable tabla, int columnaEstado) {
        tabla.setRowHeight(36);
        tabla.setFont(Tema.fuente(Font.PLAIN, 14));
        tabla.getTableHeader().setFont(Tema.fuente(Font.BOLD, 14));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setBackground(Tema.NAV_FONDO);
        tabla.getTableHeader().setForeground(Tema.TEXTO);
        tabla.getTableHeader().putClientProperty("FlatLaf.style",
                "background: " + Tema.hex(Tema.NAV_FONDO) + "; separatorColor: " + Tema.hex(Tema.NAV_BORDE)
                + "; bottomSeparatorColor: " + Tema.hex(Tema.NAV_BORDE) + "; height: 38");
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowVerticalLines(false);
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(Tema.TARJETA_MORADA);
        tabla.setForeground(Tema.TEXTO);
        tabla.setGridColor(Tema.BORDE_TARJETA);
        tabla.setSelectionBackground(Tema.NAV_ACTIVO);
        tabla.setSelectionForeground(Tema.TEXTO);
        tabla.getColumnModel().getColumn(columnaEstado).setCellRenderer(new RendererEstado());
    }

    /**
     * Pinta el estado: Aprobado en verde, Denegado en rojo y Cancelado en gris.
     * Lo reutiliza la tabla de transacciones.
     */
    public static final class RendererEstado extends DefaultTableCellRenderer {

        // TODO: pedir a Desarrollador 1 que agregue este verde a Tema
        private static final Color VERDE = new Color(0x4cc38a);

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                boolean foco, int fila, int columna) {
            JLabel celda = (JLabel) super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            String estado = valor == null ? "" : valor.toString();
            celda.setFont(celda.getFont().deriveFont(Font.BOLD));
            if (!seleccionada) {
                celda.setForeground(switch (estado) {
                    case "Aprobado" -> VERDE;
                    case "Denegado" -> Tema.ERROR;
                    default -> Tema.MUTED;
                });
            }
            return celda;
        }
    }
}
