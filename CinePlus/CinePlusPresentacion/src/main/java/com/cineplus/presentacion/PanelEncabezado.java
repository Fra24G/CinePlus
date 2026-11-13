package com.cineplus.presentacion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Encabezado superior: logo CinePLUS, buscador y los botones de notificaciones y mas
 * opciones (solo decorativos). Uso en una pantalla nueva:
 * <pre>pnlCabecera.add(new PanelEncabezado(texto -> filtrar(texto)));</pre>
 *
 * @author luisf
 */
public final class PanelEncabezado extends JPanel {

    private final JTextField buscador = new JTextField();
    private final Consumer<String> alBuscar;
    private boolean silencio;

    public PanelEncabezado(Consumer<String> alBuscar) {
        super(new BorderLayout(16, 0));
        this.alBuscar = alBuscar;
        setOpaque(false);

        JLabel logo = new JLabel("CinePLUS");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(Tema.TEXTO);
        add(logo, BorderLayout.WEST);

        Tema.campo(buscador, "Buscar una película…");
        buscador.setPreferredSize(new Dimension(290, 36));
        buscador.getAccessibleContext().setAccessibleName("Buscar");
        buscador.addActionListener(e -> avisar());
        buscador.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                avisar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                avisar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                avisar();
            }
        });
        add(buscador, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(decorativo("🔔", "Notificaciones"));
        acciones.add(decorativo("...", "Más opciones"));
        add(acciones, BorderLayout.EAST);
    }

    /** Borra el texto del buscador sin disparar la busqueda. */
    public void limpiarBusqueda() {
        silencio = true;
        try {
            buscador.setText("");
        } finally {
            silencio = false;
        }
    }

    /** Texto actual del buscador. */
    public String texto() {
        return buscador.getText();
    }

    private void avisar() {
        if (!silencio && alBuscar != null) {
            alBuscar.accept(buscador.getText());
        }
    }

    private static JButton decorativo(String texto, String ayuda) {
        JButton boton = new JButton(texto);
        boton.setToolTipText(ayuda);
        boton.setFocusable(false);
        Tema.botonSecundario(boton);
        return boton;
    }
}
