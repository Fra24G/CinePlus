package com.cineplus.presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Encabezado superior del Figma:
 * <ul>
 *   <li>Con buscador ({@link #PanelEncabezado(Consumer)}): logo {@code CinePLUS} en fuente
 *       monoespaciada a la izquierda, pildora de busqueda al centro y botones {@code 🔔} y
 *       {@code ...} a la derecha.</li>
 *   <li>Sin buscador ({@link #centrado()}): logo {@code CinePLUS} centrado en grande y
 *       botones {@code 🔔} y {@code ...} a la derecha (Membresia, Pago de Membresia y
 *       Confirmacion).</li>
 * </ul>
 *
 * @author luisf
 */
public final class PanelEncabezado extends JPanel {

    public static final String PLACEHOLDER_BUSQUEDA
            = "Buscar películas, funciones o directores...";

    private final JTextField buscador = new JTextField();
    private final Consumer<String> alBuscar;
    private boolean silencio;

    public PanelEncabezado() {
        this(null, true);
    }

    public PanelEncabezado(Consumer<String> alBuscar) {
        this(alBuscar, true);
    }

    private PanelEncabezado(Consumer<String> alBuscar, boolean mostrarBuscador) {
        super(new BorderLayout(20, 0));
        this.alBuscar = alBuscar;
        setOpaque(false);

        JPanel acciones = crearAcciones();

        if (mostrarBuscador) {
            JLabel logo = Tema.logo(46);
            add(logo, BorderLayout.WEST);

            configurarBuscador(buscador);
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

            JPanel contenedorCentral = new JPanel(new GridBagLayout());
            contenedorCentral.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 24, 4, 24);
            contenedorCentral.add(buscador, gbc);
            add(contenedorCentral, BorderLayout.CENTER);
            add(acciones, BorderLayout.EAST);
        } else {
            // Espaciador izquierdo del mismo ancho que las acciones para que el logo quede al centro exacto
            Dimension tamAcciones = acciones.getPreferredSize();
            add(Box.createRigidArea(tamAcciones), BorderLayout.WEST);

            JLabel logo = Tema.logo(52);
            logo.setHorizontalAlignment(SwingConstants.CENTER);
            add(logo, BorderLayout.CENTER);
            add(acciones, BorderLayout.EAST);
        }
    }

    /**
     * Encabezado con el logo {@code CinePLUS} centrado en grande y los botones de campana y
     * mas opciones a la derecha (sin barra de busqueda), tal como en Membresia, Pago de Membresia
     * y Confirmacion del Figma.
     */
    public static PanelEncabezado centrado() {
        return new PanelEncabezado(null, false);
    }

    /** Aplica el estilo de pildora oscura del Figma a cualquier campo de busqueda. */
    public static void configurarBuscador(JTextField campo) {
        campo.putClientProperty("JTextField.placeholderText", PLACEHOLDER_BUSQUEDA);
        campo.putClientProperty("JTextField.leadingIcon",
                Tema.iconoLupa(16, new Color(0xc7c0c5)));
        campo.putClientProperty("FlatLaf.style", "arc: 999"
                + "; borderWidth: 1; focusWidth: 0; innerFocusWidth: 0"
                + "; borderColor: #3b3239; focusedBorderColor: #6a1d25"
                + "; background: " + Tema.hex(Tema.BUSCADOR)
                + "; margin: 4,18,4,18");
        campo.setBackground(Tema.BUSCADOR);
        campo.setForeground(Tema.TEXTO);
        campo.setCaretColor(Tema.TEXTO);
        campo.setHorizontalAlignment(SwingConstants.CENTER);
        campo.setFont(Tema.fuente(Font.BOLD, 14));
        campo.setPreferredSize(new Dimension(500, 40));
        campo.setMinimumSize(new Dimension(240, 40));
        campo.getAccessibleContext().setAccessibleName("Buscar");
    }

    /** Crea el par de botones decorativos (campana y "...") de la esquina superior derecha. */
    public static JPanel crearAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        acciones.setOpaque(false);

        JButton btnCampana = Tema.botonIcono(
                "", Tema.iconoCampana(18, new Color(0xdfb045)), "Notificaciones");
        btnCampana.setPreferredSize(new Dimension(54, 40));

        JButton btnMas = Tema.botonIcono("...", null, "Más opciones");
        btnMas.setPreferredSize(new Dimension(40, 40));

        acciones.add(btnCampana);
        acciones.add(btnMas);
        return acciones;
    }

    /** Devuelve el campo de texto del buscador para conectarlo con PresentadorCartelera. */
    public JTextField campoBusqueda() {
        return buscador;
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
}
