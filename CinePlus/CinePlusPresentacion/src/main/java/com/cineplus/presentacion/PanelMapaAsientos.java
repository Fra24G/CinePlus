package com.cineplus.presentacion;

import com.cineplus.dto.AsientoDTO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Mapa interactivo de asientos de una sala
 *
 * @author Juan Ramon Acuña Gutierrez
 */
public class PanelMapaAsientos extends JPanel {

    private static final int MAXIMO_ASIENTOS = 10;

    private static final Color COLOR_OCUPADO = new Color(0x55515A);
    private static final Color COLOR_LIBRE = new Color(0x302632);
    private static final Color COLOR_SELECCIONADO = Tema.VINO;

    private final Consumer<List<AsientoDTO>> alCambiar;

    private final JPanel pnlFilas = new JPanel();
    private final Map<Integer, AsientoDTO> seleccion =
            new LinkedHashMap<>();

    private final Map<Integer, JButton> botones =
            new LinkedHashMap<>();

    private final JLabel lblEstado = new JLabel(
            "Selecciona tus asientos.",
            SwingConstants.CENTER);

    public PanelMapaAsientos(
            Consumer<List<AsientoDTO>> alCambiar) {

        this.alCambiar = alCambiar;

        configurarPanel();
    }

    /** Configura los elementos generales del map*/
    private void configurarPanel() {

        setLayout(new BorderLayout(0, 15));
        setOpaque(false);

        // Indicacion de la parte frontal de la sala
        JPanel pnlPantalla = new JPanel(new BorderLayout());
        pnlPantalla.setOpaque(true);
        pnlPantalla.setBackground(Tema.CAMPO);
        pnlPantalla.setBorder(new EmptyBorder(12, 10, 12, 10));

        JLabel lblPantalla = new JLabel(
                "P A N T A L L A",
                SwingConstants.CENTER);

        lblPantalla.setForeground(Tema.TEXTO);
        lblPantalla.setFont(
                new Font("Segoe UI", Font.BOLD, 14));

        pnlPantalla.add(lblPantalla, BorderLayout.CENTER);

        // Contenedor de filas
        pnlFilas.setOpaque(false);
        pnlFilas.setLayout(
                new BoxLayout(pnlFilas, BoxLayout.Y_AXIS));
        pnlFilas.setBorder(new EmptyBorder(15, 5, 15, 5));

        JScrollPane scroll = new JScrollPane(pnlFilas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // Leyenda de los estados de los asientos
        JPanel pnlInferior = new JPanel(
                new BorderLayout(0, 10));
        pnlInferior.setOpaque(false);

        JPanel pnlLeyenda = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 3));
        pnlLeyenda.setOpaque(false);

        pnlLeyenda.add(crearLeyenda(
                COLOR_LIBRE, "Disponible"));

        pnlLeyenda.add(crearLeyenda(
                COLOR_SELECCIONADO, "Seleccionado"));

        pnlLeyenda.add(crearLeyenda(
                COLOR_OCUPADO, "Ocupado"));

        lblEstado.setForeground(Tema.MUTED);

        pnlInferior.add(pnlLeyenda, BorderLayout.NORTH);
        pnlInferior.add(lblEstado, BorderLayout.SOUTH);

        add(pnlPantalla, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(pnlInferior, BorderLayout.SOUTH);
    }

    /** Crea un elemento de la leyenda */
    private JPanel crearLeyenda(Color color, String texto) {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);

        JLabel indicador = new JLabel("●");
        indicador.setForeground(color);

        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(Tema.MUTED);

        panel.add(indicador);
        panel.add(etiqueta);

        return panel;
    }

    /** Dibuja los asientos agrupados por fila */
    public void mostrar(List<AsientoDTO> asientos) {

        seleccion.clear();
        botones.clear();
        pnlFilas.removeAll();

        if (asientos == null || asientos.isEmpty()) {
            lblEstado.setText("No hay asientos disponibles.");
            actualizarPanel();
            notificarCambio();
            return;
        }

        // Agrupa los asientos por letra de fila
        Map<String, List<AsientoDTO>> filas = new TreeMap<>();

        for (AsientoDTO asiento : asientos) {

            if (asiento == null || asiento.idAsiento() == null) {
                continue;
            }

            String fila = asiento.fila() == null
                    ? "" : asiento.fila();

            filas.computeIfAbsent(
                    fila, clave -> new ArrayList<>())
                    .add(asiento);
        }

        // Ordena los asientos por número dentro de cada fila
        for (Map.Entry<String, List<AsientoDTO>> entrada
                : filas.entrySet()) {

            entrada.getValue().sort(
                    Comparator.comparingInt(AsientoDTO::numero));

            agregarFila(entrada.getKey(), entrada.getValue());
        }

        if (botones.isEmpty()) {
            lblEstado.setText("No hay asientos para mostrar.");
        } else {
            lblEstado.setText("Selecciona de 1 a 10 asientos.");
        }

        actualizarPanel();
        notificarCambio();
    }

    /** Construye una fila completa de asientos */
    private void agregarFila(
            String nombreFila,
            List<AsientoDTO> asientos) {

        JPanel pnlFila = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 6, 5));
        pnlFila.setOpaque(false);

        JLabel lblFila = new JLabel(
                nombreFila.isBlank() ? "-" : nombreFila);

        lblFila.setForeground(Tema.TEXTO);
        lblFila.setFont(
                new Font("Segoe UI", Font.BOLD, 14));
        lblFila.setPreferredSize(new Dimension(25, 34));

        pnlFila.add(lblFila);

        for (AsientoDTO asiento : asientos) {

            JButton boton = crearBoton(asiento);

            botones.put(asiento.idAsiento(), boton);
            pnlFila.add(boton);
        }

        pnlFila.setAlignmentX(CENTER_ALIGNMENT);

        pnlFilas.add(pnlFila);
    }

    /** Crea el boton correspondiente a un asiento */
    private JButton crearBoton(AsientoDTO asiento) {

        JButton boton = new JButton(
                String.valueOf(asiento.numero()));

        boton.setPreferredSize(new Dimension(42, 34));
        boton.setMinimumSize(new Dimension(42, 34));
        boton.setMaximumSize(new Dimension(42, 34));
        boton.setFont(
                new Font("Segoe UI", Font.BOLD, 12));

        boton.setFocusable(false);
        boton.setBorderPainted(false);
        boton.putClientProperty("JButton.buttonType", "roundRect");

        boton.setToolTipText(
                asiento.etiqueta()
                + " · " + asiento.tipo()
                + " · $" + asiento.precio());

        if (asiento.ocupado()) {

            boton.setEnabled(false);
            boton.setBackground(COLOR_OCUPADO);
            boton.setForeground(Color.WHITE);

        } else {

            boton.setEnabled(true);
            actualizarEstilo(boton, false);

            boton.addActionListener(
                    e -> alternar(asiento));
        }

        return boton;
    }

    /** Selecciona o quita un asiento */
    private void alternar(AsientoDTO asiento) {

        Integer id = asiento.idAsiento();

        if (seleccion.containsKey(id)) {

            seleccion.remove(id);

        } else {

            if (seleccion.size() >= MAXIMO_ASIENTOS) {
                lblEstado.setForeground(Tema.ERROR);
                lblEstado.setText(
                        "Solo puedes seleccionar 10 asientos por compra.");
                return;
            }

            seleccion.put(id, asiento);
        }

        actualizarBotones();

        lblEstado.setForeground(Tema.MUTED);
        lblEstado.setText(
                seleccion.size() + " de " + MAXIMO_ASIENTOS
                + " asientos seleccionados.");

        notificarCambio();
    }

    /** Actualiza los colores de los botones */
    private void actualizarBotones() {

        for (Map.Entry<Integer, JButton> entrada
                : botones.entrySet()) {

            boolean seleccionado =
                    seleccion.containsKey(entrada.getKey());

            actualizarEstilo(
                    entrada.getValue(), seleccionado);
        }
    }

    /** Aplica el estilo correspondiente a cada estado */
    private void actualizarEstilo(
            JButton boton, boolean seleccionado) {

        if (seleccionado) {

            boton.setBackground(COLOR_SELECCIONADO);
            boton.setForeground(Color.WHITE);

        } else {

            boton.setBackground(COLOR_LIBRE);
            boton.setForeground(Tema.TEXTO);
        }
    }

    /** Devuelve una copia de los asientos seleccionados */
    public List<AsientoDTO> seleccionados() {

        List<AsientoDTO> resultado =
                new ArrayList<>(seleccion.values());

        resultado.sort(
                Comparator.comparing(
                        AsientoDTO::fila,
                        Comparator.nullsFirst(String::compareTo))
                .thenComparingInt(AsientoDTO::numero));

        return resultado;
    }

    /** Quita todos los asientos seleccionados */
    public void limpiar() {

        if (seleccion.isEmpty()) {
            return;
        }

        seleccion.clear();

        actualizarBotones();

        lblEstado.setForeground(Tema.MUTED);
        lblEstado.setText("Selecciona tus asientos.");

        notificarCambio();
    }

    /** Notifica al formulario los cambios en la seleccion*/
    private void notificarCambio() {

        if (alCambiar != null) {
            alCambiar.accept(seleccionados());
        }
    }

    /** Actualiza el dibujo del mapa */
    private void actualizarPanel() {

        pnlFilas.revalidate();
        pnlFilas.repaint();

        revalidate();
        repaint();
    }
}
