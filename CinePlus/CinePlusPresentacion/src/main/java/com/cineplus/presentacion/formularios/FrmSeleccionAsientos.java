
package com.cineplus.presentacion.formularios;

import com.cineplus.dto.AsientoDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelMapaAsientos;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla para seleccionar los asientos de una funcion
 *
 * @author Juan Ramon Acuña Gutierrez
 */
public class FrmSeleccionAsientos extends JFrame {

    private CoordinadorAplicacion coordinador;
    private FuncionDTO funcion;

    private PanelMapaAsientos mapa;
    private PanelNavegacion navegacion;

    private JLabel lblInformacionFuncion;
    private JLabel lblEstado;
    private JLabel lblAsientos;
    private JLabel lblSubtotal;

    private JButton btnLimpiar;
    private JButton btnContinuar;

    private final NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(
                    Locale.forLanguageTag("es-MX"));

    public FrmSeleccionAsientos() {
        initComponents();
        configurarVentana();
    }

    public FrmSeleccionAsientos(
            CoordinadorAplicacion coordinador,
            FuncionDTO funcion) {

        this();

        this.coordinador = coordinador;
        this.funcion = funcion;

        construirPantalla();
    }

    /** Configura las propiedades generales de la ventana */
    private void configurarVentana() {

        setTitle("CinePlus · Selección de asientos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Tema.preparar(this);
    }

    /** Construye la pantalla y sus componentes */
    private void construirPantalla() {

        pnlFondo.removeAll();
        pnlFondo.setLayout(new BorderLayout(20, 18));
        pnlFondo.setBackground(Tema.FONDO);

        // Encabezado.
        JPanel pnlEncabezado = new JPanel(
                new BorderLayout(0, 8));
        pnlEncabezado.setOpaque(false);

        JLabel lblTitulo = new JLabel("Elige tu(s) asiento(s)");
        Tema.titulo(lblTitulo);

        lblInformacionFuncion = new JLabel(
                obtenerInformacionFuncion());
        lblInformacionFuncion.setForeground(Tema.MUTED);

        pnlEncabezado.add(lblTitulo, BorderLayout.NORTH);
        pnlEncabezado.add(
                lblInformacionFuncion, BorderLayout.SOUTH);

        // Mapa de asientos
        mapa = new PanelMapaAsientos(
                this::actualizarResumen);

        JPanel pnlMapa = new JPanel(new BorderLayout());
        Tema.tarjeta(pnlMapa);

        JLabel lblSubtituloMapa = new JLabel(
                "Selecciona tus lugares");
        lblSubtituloMapa.setFont(
                new java.awt.Font(
                        "Segoe UI", java.awt.Font.BOLD, 17));
        lblSubtituloMapa.setForeground(Tema.TEXTO);
        lblSubtituloMapa.setBorder(
                new EmptyBorder(0, 0, 12, 0));

        pnlMapa.add(lblSubtituloMapa, BorderLayout.NORTH);
        pnlMapa.add(mapa, BorderLayout.CENTER);

        // Resumen de la seleccion
        JPanel pnlResumen = new JPanel(
                new BorderLayout(0, 14));
        Tema.tarjeta(pnlResumen);

        JLabel lblTituloResumen = new JLabel("Resumen de selección");
        lblTituloResumen.setFont(
                new java.awt.Font(
                        "Segoe UI", java.awt.Font.BOLD, 17));
        lblTituloResumen.setForeground(Tema.TEXTO);

        JPanel pnlDatosResumen = new JPanel(
                new GridLayout(2, 1, 0, 10));
        pnlDatosResumen.setOpaque(false);

        lblAsientos = new JLabel(
                "Asientos seleccionados: Ninguno");
        lblAsientos.setForeground(Tema.MUTED);

        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblSubtotal.setForeground(Tema.TEXTO);
        lblSubtotal.setFont(
                new java.awt.Font(
                        "Segoe UI", java.awt.Font.BOLD, 18));

        pnlDatosResumen.add(lblAsientos);
        pnlDatosResumen.add(lblSubtotal);

        JPanel pnlBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBotones.setOpaque(false);

        btnLimpiar = new JButton("Limpiar selección");
        Tema.botonSecundario(btnLimpiar);
        btnLimpiar.addActionListener(e -> mapa.limpiar());

        btnContinuar = new JButton("CONTINUAR");
        Tema.botonPrimario(btnContinuar);
        btnContinuar.addActionListener(e -> continuar());

        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnContinuar);

        lblEstado = new JLabel(" ");
        lblEstado.setForeground(Tema.ERROR);
        lblEstado.setVisible(false);

        JPanel pnlInferiorResumen = new JPanel(
                new BorderLayout(0, 10));
        pnlInferiorResumen.setOpaque(false);

        pnlInferiorResumen.add(
                lblEstado, BorderLayout.NORTH);
        pnlInferiorResumen.add(
                pnlBotones, BorderLayout.SOUTH);

        pnlResumen.add(
                lblTituloResumen, BorderLayout.NORTH);
        pnlResumen.add(
                pnlDatosResumen, BorderLayout.CENTER);
        pnlResumen.add(
                pnlInferiorResumen, BorderLayout.SOUTH);

        // Contenido central
        JPanel pnlContenido = new JPanel(
                new BorderLayout(0, 16));
        pnlContenido.setOpaque(false);

        pnlContenido.add(pnlMapa, BorderLayout.CENTER);
        pnlContenido.add(pnlResumen, BorderLayout.SOUTH);

        // Navegacion inferior compartida
        navegacion = new PanelNavegacion(
                coordinador, Seccion.CARTELERA);

        pnlFondo.add(
                pnlEncabezado, BorderLayout.NORTH);
        pnlFondo.add(
                pnlContenido, BorderLayout.CENTER);
        pnlFondo.add(
                navegacion, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /** Prepara los datos de la funcion para mostrarlos */
    private String obtenerInformacionFuncion() {

        if (funcion == null) {
            return "No se recibió una función válida.";
        }

        String fecha = funcion.fechaHora().format(
                java.time.format.DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"));

        return funcion.tituloPelicula()
                + " · " + funcion.cine()
                + " · Sala " + funcion.numeroSala()
                + " · " + fecha
                + " · " + funcion.formato()
                + " " + funcion.tipo();
    }

    /** Recibe los asientos consultados para la funcion */
    public void mostrarAsientos(List<AsientoDTO> asientos) {

        mapa.mostrar(asientos);
        actualizarResumen(mapa.seleccionados());
    }

    /** Actualiza los asientos seleccionados y el subtotal */
    private void actualizarResumen(List<AsientoDTO> seleccion) {

        if (seleccion == null || seleccion.isEmpty()) {

            lblAsientos.setText(
                    "Asientos seleccionados: Ninguno");

            lblSubtotal.setText("Subtotal: $0.00");

            return;
        }

        List<String> etiquetas = seleccion.stream()
                .map(AsientoDTO::etiqueta)
                .toList();

        double subtotal = seleccion.stream()
                .mapToDouble(AsientoDTO::precio)
                .sum();

        lblAsientos.setText(
                "Asientos seleccionados: "
                + String.join(", ", etiquetas));

        lblSubtotal.setText(
                "Subtotal: " + formatoMoneda.format(subtotal));

        lblEstado.setText(" ");
        lblEstado.setVisible(false);
    }

    /** Envia los asientos elegidos al modulo de compra */
    private void continuar() {

        List<AsientoDTO> seleccion = mapa.seleccionados();

        if (seleccion.isEmpty()) {
            mostrarError("Elige al menos un asiento.");
            return;
        }

        coordinador.cartelera().continuarAPago(
                this, funcion, seleccion);
    }

    /** Muestra el estado de carga y deshabilita los botones */
    public void ocupado(boolean ocupado) {

        btnLimpiar.setEnabled(!ocupado);
        btnContinuar.setEnabled(!ocupado);

        if (navegacion != null) {
            if (!ocupado) {
                navegacion.actualizar();
            } else {
                navegacion.setEnabled(false);
            }
        }

        if (ocupado) {

            lblEstado.setText("Cargando asientos...");
            lblEstado.setForeground(Tema.MUTED);
            lblEstado.setVisible(true);

            setCursor(java.awt.Cursor.getPredefinedCursor(
                    java.awt.Cursor.WAIT_CURSOR));

        } else {

            setCursor(java.awt.Cursor.getDefaultCursor());

            if ("Cargando asientos...".equals(lblEstado.getText())) {
                lblEstado.setText(" ");
                lblEstado.setVisible(false);
            }
        }
    }

    /** Muestra los errores recibidos del coordinador */
    public void mostrarError(String mensaje) {

        lblEstado.setText(
                mensaje == null || mensaje.isBlank()
                ? "Ocurrió un error al consultar los asientos."
                : mensaje
        );

        lblEstado.setForeground(Tema.ERROR);
        lblEstado.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout());

        pnlFondo.setBackground(Tema.FONDO);
        pnlFondo.setLayout(new java.awt.BorderLayout());

        getContentPane().add(
                pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlFondo;
    // End of variables declaration//GEN-END:variables
}
