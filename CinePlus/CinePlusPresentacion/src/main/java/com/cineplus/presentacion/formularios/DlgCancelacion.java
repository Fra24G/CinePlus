package com.cineplus.presentacion.formularios;

import com.cineplus.dto.BoletoDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Dialogo modal "Panel de cancelacion" (F12): muestra el detalle de la compra y pide
 * el motivo. mostrarYLeerMotivo espera a que el usuario pulse "Pedir cancelacion"
 * o cierre el dialogo.
 *
 * @author Mario Perez
 */
public class DlgCancelacion extends javax.swing.JDialog {

    private static final int MOTIVO_MINIMO = 5;
    private static final int MOTIVO_MAXIMO = 300;
    private static final DateTimeFormatter FECHA_LARGA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.forLanguageTag("es-MX"));
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private CompraDTO compra;
    private String motivo;

    private final JTextArea txtMotivo = new JTextArea(5, 30);
    private final JLabel lblContador = new JLabel("0/" + MOTIVO_MAXIMO);
    private final JButton btnVolver = new JButton("Volver");
    private final JButton btnPedir = new JButton("Pedir cancelación");

    /** Constructor para el editor visual de NetBeans. */
    public DlgCancelacion() {
        super((JFrame) null, true);
        initComponents();
        configurarVista();
    }

    public DlgCancelacion(JFrame padre, CompraDTO compra) {
        super(padre, true);
        this.compra = compra;
        initComponents();
        configurarVista();
        mostrarCompra();
        conectarEventos();
        setLocationRelativeTo(padre);
    }

    private void configurarVista() {
        setTitle("CinePlus · Cancelar compra");
        pnlFondo.setBorder(new EmptyBorder(22, 26, 18, 26));
        Tema.titulo(lblTitulo);

        Tema.botonSecundario(btnVolver);
        Tema.botonFigma(btnPedir, Tema.VINO_BRILLANTE, 14);
        btnPedir.setFont(btnPedir.getFont().deriveFont(Font.BOLD));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.add(btnVolver);
        botones.add(btnPedir);
        pnlBotones.add(botones, BorderLayout.CENTER);

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
        lblContador.setForeground(Tema.MUTED);
        getRootPane().setDefaultButton(btnPedir);
    }

    /** Resumen de la compra y el area "Escribe tu motivo de cancelacion...". */
    private void mostrarCompra() {
        JPanel resumen = new JPanel();
        Tema.tarjetaConBorde(resumen, Tema.TARJETA_MORADA, Tema.BORDE_TARJETA, 14, new Insets(14, 18, 14, 18));
        resumen.setLayout(new BoxLayout(resumen, BoxLayout.Y_AXIS));
        agregar(resumen, compra.tituloPelicula(), Font.BOLD, 20);
        agregar(resumen, fechaYHora(compra.fechaHoraFuncion()), Font.PLAIN, 15);
        agregar(resumen, compra.cine() + " SALA " + compra.numeroSala(), Font.PLAIN, 15);
        agregar(resumen, "Asientos: " + asientos(), Font.PLAIN, 15);
        agregar(resumen, "Compra #" + compra.idCompra()
                + " · Total " + dinero(compra.montoCobrado()), Font.BOLD, 15);
        if (compra.puntosUsados() > 0 || compra.puntosGanados() > 0) {
            agregar(resumen, "<html>Se te devolverán " + compra.puntosUsados()
                    + " puntos usados y se quitarán " + compra.puntosGanados() + " puntos ganados.</html>",
                    Font.PLAIN, 13);
        }

        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setFont(Tema.fuente(Font.PLAIN, 15));
        txtMotivo.setBackground(Tema.CAMPO_FIGMA);
        txtMotivo.setForeground(Tema.TEXTO);
        txtMotivo.setCaretColor(Tema.TEXTO);
        txtMotivo.setBorder(new EmptyBorder(10, 12, 10, 12));
        txtMotivo.putClientProperty("JTextField.placeholderText", "Escribe tu motivo de cancelación...");
        JScrollPane scroll = new JScrollPane(txtMotivo);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JLabel lblMotivo = new JLabel("Motivo de cancelación (5 a " + MOTIVO_MAXIMO + " caracteres):");
        lblMotivo.setFont(Tema.fuente(Font.BOLD, 14));
        lblMotivo.setForeground(Tema.TEXTO_SUAVE);

        JPanel pnlMotivo = new JPanel(new BorderLayout(0, 6));
        pnlMotivo.setOpaque(false);
        pnlMotivo.add(lblMotivo, BorderLayout.NORTH);
        pnlMotivo.add(scroll, BorderLayout.CENTER);
        pnlMotivo.add(lblContador, BorderLayout.SOUTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setOpaque(false);
        contenido.add(resumen, BorderLayout.NORTH);
        contenido.add(pnlMotivo, BorderLayout.CENTER);
        pnlCuerpo.add(contenido, BorderLayout.CENTER);
    }

    private void conectarEventos() {
        btnVolver.addActionListener(e -> dispose());
        btnPedir.addActionListener(e -> pedir());
        txtMotivo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                contar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                contar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                contar();
            }
        });
    }

    private void contar() {
        int largo = txtMotivo.getText().strip().length();
        lblContador.setText(largo + "/" + MOTIVO_MAXIMO);
        lblContador.setForeground(largo > MOTIVO_MAXIMO ? Tema.ERROR : Tema.MUTED);
    }

    /** Revisa el largo del motivo; si esta bien guarda el motivo y cierra. */
    private void pedir() {
        String texto = txtMotivo.getText().strip();
        if (texto.length() < MOTIVO_MINIMO || texto.length() > MOTIVO_MAXIMO) {
            lblError.setText("El motivo de cancelación debe tener de "
                    + MOTIVO_MINIMO + " a " + MOTIVO_MAXIMO + " caracteres.");
            Tema.marcarError(txtMotivo, true);
            return;
        }
        motivo = texto;
        dispose();
    }

    /**
     * Muestra el dialogo y espera. Devuelve el motivo si se pulso "Pedir cancelacion"
     * o vacio si se cerro.
     */
    public Optional<String> mostrarYLeerMotivo() {
        motivo = null;
        setVisible(true);
        return Optional.ofNullable(motivo);
    }

    private String asientos() {
        List<String> lista = new ArrayList<>();
        for (BoletoDTO boleto : compra.boletos()) {
            lista.add(boleto.asiento());
        }
        return lista.isEmpty() ? "—" : String.join(", ", lista);
    }

    private static String fechaYHora(LocalDateTime fecha) {
        if (fecha == null) {
            return "";
        }
        String texto = FECHA_LARGA.format(fecha);
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1) + " · " + HORA.format(fecha);
    }

    private static String dinero(double monto) {
        DecimalFormat formato = new DecimalFormat(monto == Math.floor(monto) ? "#,##0" : "#,##0.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return "$" + formato.format(monto);
    }

    private static void agregar(JPanel panel, String texto, int estilo, int tamano) {
        JLabel etiqueta = new JLabel(texto == null ? "" : texto);
        etiqueta.setFont(Tema.fuente(estilo, tamano));
        etiqueta.setForeground(estilo == Font.BOLD ? Tema.TEXTO : Tema.TEXTO_SUAVE);
        etiqueta.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(2));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlCuerpo = new javax.swing.JPanel();
        pnlPie = new javax.swing.JPanel();
        lblError = new javax.swing.JLabel();
        pnlBotones = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(560, 520));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        lblTitulo.setText("Panel de cancelación");
        pnlFondo.add(lblTitulo, java.awt.BorderLayout.NORTH);

        pnlCuerpo.setOpaque(false);
        pnlCuerpo.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlCuerpo, java.awt.BorderLayout.CENTER);

        pnlPie.setOpaque(false);
        pnlPie.setLayout(new java.awt.BorderLayout(8, 8));

        lblError.setText("");
        pnlPie.add(lblError, java.awt.BorderLayout.NORTH);

        pnlBotones.setOpaque(false);
        pnlBotones.setLayout(new java.awt.BorderLayout());
        pnlPie.add(pnlBotones, java.awt.BorderLayout.CENTER);

        lblEstado.setText("");
        pnlPie.add(lblEstado, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlPie, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlCuerpo;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlPie;
    // End of variables declaration//GEN-END:variables
}
