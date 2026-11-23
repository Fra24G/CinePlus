package com.cineplus.presentacion.formularios;

import com.cineplus.dto.AsientoDTO;
import com.cineplus.dto.CotizacionDTO;
import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.dto.SolicitudCompraDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelFormularioPago;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.TarjetaPelicula;
import com.cineplus.presentacion.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla de pago de boletos con el desglose, los puntos y el formulario de pago
 *
 * @author yakor
 */
public class FrmPago extends JFrame {

    private static final DateTimeFormatter FORMATO_FUNCION =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM · HH:mm", Locale.forLanguageTag("es-MX"));

    private CoordinadorAplicacion coordinador;
    private FuncionDTO funcion;
    private List<AsientoDTO> asientos = List.of();
    private CotizacionDTO cotizacion;
    private final PanelFormularioPago panelPago = new PanelFormularioPago();
    private final JLabel lblPuntosCubren = new JLabel("Tus puntos CinePLUS cubren el total de tu compra.");

    /**
     * Constructor 
     */
    public FrmPago() {
        initComponents();
        configurarVista();
    }

    /**
     * Muestra la funcion, los asientos y el usuario, al abrirse pide la cotizacion
     */
    public FrmPago(CoordinadorAplicacion coordinador, FuncionDTO funcion, List<AsientoDTO> asientos) {
        this();
        this.coordinador = coordinador;
        this.funcion = funcion;
        this.asientos = List.copyOf(asientos);
        pnlNavegacion.add(new PanelNavegacion(coordinador, Seccion.CARTELERA), BorderLayout.CENTER);

        lblPelicula.setText(funcion.tituloPelicula());
        lblFuncion.setText(capitalizar(FORMATO_FUNCION.format(funcion.fechaHora()))
                + " · " + funcion.tipo() + (funcion.formato() == null ? "" : " · " + funcion.formato()));
        lblSala.setText(funcion.cine() + "   SALA " + funcion.numeroSala());
        lblUsuario.setText(nombreUsuario(coordinador.sesion()) + " - Miembro");
        mostrarDesglose();
        conectarEventos();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                coordinador.compra().cotizar(FrmPago.this, solicitudCotizacion());
            }
        });
    }

    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Pago de boletos");

        pnlCabecera.add(PanelEncabezado.centrado(), BorderLayout.CENTER);
        pnlContenido.setBorder(new EmptyBorder(0, 14, 2, 14));
        Tema.tarjetaConBorde(pnlTarjetaCompra, Tema.TARJETA_MORADA, Tema.ROJO_PRO, 14, new Insets(16, 24, 18, 24));
        Tema.tarjetaConBorde(pnlPago, Tema.SUBPANEL_PAGO, null, 22, new Insets(12, 22, 14, 22));
        Tema.tarjetaConBorde(pnlPildoraTotal, Tema.VINO_TOTAL, null, 22, new Insets(8, 26, 8, 26));

        lblPelicula.setFont(Tema.fuente(Font.BOLD, 26));
        lblPelicula.setForeground(Color.WHITE);
        lblFuncion.setFont(Tema.fuente(Font.BOLD, 15));
        lblFuncion.setForeground(Tema.TEXTO_SUAVE);
        lblSala.setFont(Tema.fuente(Font.BOLD, 15));
        lblSala.setForeground(Tema.TEXTO_SUAVE);
        lblSubtituloDetalles.setFont(Tema.fuente(Font.BOLD, 22));
        lblSubtituloDetalles.setForeground(Color.WHITE);
        lblDesglose.setFont(Tema.fuente(Font.BOLD, 15));
        lblDesglose.setForeground(new Color(0xa6a0aa));
        lblTotal.setFont(Tema.fuente(Font.BOLD, 20));
        lblTotal.setForeground(Color.WHITE);

        chkUsarPuntos.setFont(Tema.fuente(Font.BOLD, 14));
        chkUsarPuntos.setForeground(Tema.TEXTO);
        chkUsarPuntos.setOpaque(false);
        chkUsarPuntos.setEnabled(false);

        lblUsuario.setFont(Tema.fuente(Font.BOLD, 18));
        lblUsuario.setForeground(Color.WHITE);
        Tema.enlace(btnNoEresTu, Tema.ENLACE, false);
        btnNoEresTu.setFont(Tema.fuente(Font.BOLD, 18));

        lblPuntosCubren.setFont(Tema.fuente(Font.BOLD, 15));
        lblPuntosCubren.setForeground(Tema.TEXTO_SUAVE);
        pnlFormulario.add(panelPago, BorderLayout.CENTER);

        Tema.botonFigma(btnContinuar, Tema.VINO_BRILLANTE, 14);
        btnContinuar.setFont(Tema.fuente(Font.BOLD, 16));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setPreferredSize(new Dimension(280, 38));
        Tema.botonSecundario(btnVolver);
        btnVolver.setPreferredSize(new Dimension(140, 38));

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
    }

    private void conectarEventos() {
        btnNoEresTu.addActionListener(e -> coordinador.cerrarSesion());
        btnVolver.addActionListener(e -> coordinador.compra().volverAAsientos(funcion));
        btnContinuar.addActionListener(e -> continuar());
        chkUsarPuntos.addActionListener(e -> mostrarTotal());
    }

    /**
     * Muestra el poster de la película de la función.
     */
    public void mostrarPelicula(PeliculaDTO pelicula) {
        pnlPoster.removeAll();
        pnlPoster.add(new TarjetaPelicula(pelicula, coordinador, null), BorderLayout.CENTER);
        pnlPoster.revalidate();
        pnlPoster.repaint();
    }

    /**
     * asiento, servicio, total y la casilla de puntos
     */
    public void mostrarCotizacion(CotizacionDTO cotizacion) {
        this.cotizacion = cotizacion;
        mostrarDesglose();
        lblUsuario.setText(nombreUsuario(coordinador.sesion())
                + (cotizacion.miembroPro() ? " - Miembro Pro" : " - Miembro"));

        chkUsarPuntos.setText("USAR SALDO CinePLUS: " + cotizacion.puntosDisponibles() + " puntos"
                + (cotizacion.puntosUsables() > 0 ? " (-$" + precio(cotizacion.descuentoPuntos()) + ")" : ""));
        chkUsarPuntos.setEnabled(cotizacion.puntosUsables() > 0);
        if (!chkUsarPuntos.isEnabled()) {
            chkUsarPuntos.setSelected(false);
        }
        mostrarTotal();
    }

    /**
     * Borra el numero y el CVV
     */
    public void limpiarTarjeta() {
        panelPago.limpiar();
    }

    /**
     * muestra Procesando…" mientras corre
     */
    public void ocupado(boolean ocupado) {
        btnContinuar.setEnabled(!ocupado);
        btnVolver.setEnabled(!ocupado);
        btnNoEresTu.setEnabled(!ocupado);
        chkUsarPuntos.setEnabled(!ocupado && cotizacion != null && cotizacion.puntosUsables() > 0);
        panelPago.setEnabled(!ocupado);
        lblEstado.setText(ocupado ? "Procesando…" : "");
        if (ocupado) {
            mostrarError(null);
        }
    }
    
    public void mostrarError(String mensaje) {
        lblError.setText(mensaje == null || mensaje.isBlank()
                ? ""
                : "<html>" + Tema.escapar(mensaje) + "</html>");
    }

    private void continuar() {
        mostrarError(null);
        try {
            coordinador.compra().comprar(this, armarSolicitud());
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * si los puntos cubren todo no se piden datos de tarjeta, el cobro lo hace PagoPuntos
     */
    private SolicitudCompraDTO armarSolicitud() throws Exception {
        if (cotizacion == null) {
            throw new Exception("Espera a que se calcule el total de tu compra.");
        }
        boolean usarPuntos = chkUsarPuntos.isSelected();
        DatosPagoDTO pago = usarPuntos && cotizacion.montoConPuntos() <= 0
                ? new DatosPagoDTO(null, true, null)
                : panelPago.leerDatos(usarPuntos);
        return new SolicitudCompraDTO(funcion.idFuncion(), idsAsientos(), List.of(), pago);
    }

    private SolicitudCompraDTO solicitudCotizacion() {
        return new SolicitudCompraDTO(funcion.idFuncion(), idsAsientos(), List.of(),
                new DatosPagoDTO(null, false, null));
    }

    private List<Integer> idsAsientos() {
        return asientos.stream().map(AsientoDTO::idAsiento).collect(Collectors.toList());
    }

    private void mostrarDesglose() {
        StringBuilder html = new StringBuilder("<html>");
        for (AsientoDTO asiento : asientos) {
            html.append("Asiento ").append(Tema.escapar(asiento.tipo())).append(' ')
                    .append(asiento.etiqueta()).append(" - $").append(precio(asiento.precio())).append("<br>");
        }
        if (cotizacion != null) {
            html.append("Servicio - $").append(precio(cotizacion.cargoServicio()));
            if (cotizacion.miembroPro()) {
                html.append(" (CinePLUS Pro)");
            }
        }
        lblDesglose.setText(html.append("</html>").toString());
    }

    private void mostrarTotal() {
        if (cotizacion == null) {
            lblTotal.setText("TOTAL - calculando…");
            return;
        }
        boolean usarPuntos = chkUsarPuntos.isSelected();
        double total = usarPuntos ? cotizacion.montoConPuntos() : cotizacion.montoTotal();
        lblTotal.setText("TOTAL - $" + precio(total));

        boolean cubren = usarPuntos && cotizacion.montoConPuntos() <= 0;
        pnlFormulario.removeAll();
        pnlFormulario.add(cubren ? lblPuntosCubren : panelPago, BorderLayout.CENTER);
        pnlFormulario.revalidate();
        pnlFormulario.repaint();
    }

    private static String nombreUsuario(SesionDTO sesion) {
        return sesion == null ? "Invitado" : sesion.usuario().nombreMostrado();
    }

    private static String capitalizar(String texto) {
        return texto.isEmpty() ? texto : Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private static String precio(double valor) {
        return valor == Math.floor(valor)
                ? String.format("%.0f", valor)
                : String.format("%.2f", valor);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlFondo = new javax.swing.JPanel();
        pnlCabecera = new javax.swing.JPanel();
        pnlContenido = new javax.swing.JPanel();
        pnlTarjetaCompra = new javax.swing.JPanel();
        pnlPoster = new javax.swing.JPanel();
        pnlDetalles = new javax.swing.JPanel();
        lblPelicula = new javax.swing.JLabel();
        lblFuncion = new javax.swing.JLabel();
        lblSala = new javax.swing.JLabel();
        lblSubtituloDetalles = new javax.swing.JLabel();
        lblDesglose = new javax.swing.JLabel();
        chkUsarPuntos = new javax.swing.JCheckBox();
        pnlFilaTotal = new javax.swing.JPanel();
        pnlPildoraTotal = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        pnlPago = new javax.swing.JPanel();
        pnlUsuario = new javax.swing.JPanel();
        lblUsuario = new javax.swing.JLabel();
        btnNoEresTu = new javax.swing.JButton();
        pnlFormulario = new javax.swing.JPanel();
        pnlAcciones = new javax.swing.JPanel();
        pnlMensajes = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();
        pnlBotones = new javax.swing.JPanel();
        btnVolver = new javax.swing.JButton();
        btnContinuar = new javax.swing.JButton();
        pnlInferior = new javax.swing.JPanel();
        pnlNavegacion = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(12, 8));

        pnlCabecera.setOpaque(false);
        pnlCabecera.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlCabecera, java.awt.BorderLayout.NORTH);

        pnlContenido.setOpaque(false);
        pnlContenido.setLayout(new java.awt.BorderLayout());

        pnlTarjetaCompra.setLayout(new java.awt.BorderLayout(24, 12));

        pnlPoster.setOpaque(false);
        pnlPoster.setPreferredSize(new java.awt.Dimension(220, 330));
        pnlPoster.setLayout(new java.awt.BorderLayout());
        pnlTarjetaCompra.add(pnlPoster, java.awt.BorderLayout.WEST);

        pnlDetalles.setOpaque(false);
        pnlDetalles.setLayout(new java.awt.GridBagLayout());

        lblPelicula.setText("Película");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlDetalles.add(lblPelicula, gridBagConstraints);

        lblFuncion.setText("Función");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlDetalles.add(lblFuncion, gridBagConstraints);

        lblSala.setText("Cine - Sala");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        pnlDetalles.add(lblSala, gridBagConstraints);

        lblSubtituloDetalles.setText("Detalles de Compra:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        pnlDetalles.add(lblSubtituloDetalles, gridBagConstraints);

        lblDesglose.setText("Asientos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 0);
        pnlDetalles.add(lblDesglose, gridBagConstraints);

        pnlFilaTotal.setOpaque(false);
        pnlFilaTotal.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 18, 0));

        pnlPildoraTotal.setLayout(new java.awt.BorderLayout());

        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setText("TOTAL - calculando…");
        pnlPildoraTotal.add(lblTotal, java.awt.BorderLayout.CENTER);

        pnlFilaTotal.add(pnlPildoraTotal);

        chkUsarPuntos.setText("USAR SALDO CinePLUS: 0 puntos");
        pnlFilaTotal.add(chkUsarPuntos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlDetalles.add(pnlFilaTotal, gridBagConstraints);

        pnlTarjetaCompra.add(pnlDetalles, java.awt.BorderLayout.CENTER);

        pnlPago.setLayout(new java.awt.BorderLayout(0, 8));

        pnlUsuario.setOpaque(false);
        pnlUsuario.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));

        lblUsuario.setText("Usuario - Miembro");
        pnlUsuario.add(lblUsuario);

        btnNoEresTu.setText("¿No eres tú?");
        pnlUsuario.add(btnNoEresTu);

        pnlPago.add(pnlUsuario, java.awt.BorderLayout.NORTH);

        pnlFormulario.setOpaque(false);
        pnlFormulario.setLayout(new java.awt.BorderLayout());
        pnlPago.add(pnlFormulario, java.awt.BorderLayout.CENTER);

        pnlAcciones.setOpaque(false);
        pnlAcciones.setLayout(new java.awt.BorderLayout(12, 0));

        pnlMensajes.setOpaque(false);
        pnlMensajes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        lblEstado.setText("");
        pnlMensajes.add(lblEstado);

        lblError.setText("");
        pnlMensajes.add(lblError);

        pnlAcciones.add(pnlMensajes, java.awt.BorderLayout.CENTER);

        pnlBotones.setOpaque(false);
        pnlBotones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));

        btnVolver.setText("VOLVER");
        pnlBotones.add(btnVolver);

        btnContinuar.setText("CONTINUAR");
        pnlBotones.add(btnContinuar);

        pnlAcciones.add(pnlBotones, java.awt.BorderLayout.EAST);

        pnlPago.add(pnlAcciones, java.awt.BorderLayout.SOUTH);

        pnlTarjetaCompra.add(pnlPago, java.awt.BorderLayout.SOUTH);

        pnlContenido.add(pnlTarjetaCompra, java.awt.BorderLayout.CENTER);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlInferior.setOpaque(false);
        pnlInferior.setLayout(new java.awt.BorderLayout());

        pnlNavegacion.setOpaque(false);
        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlInferior.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlInferior, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContinuar;
    private javax.swing.JButton btnNoEresTu;
    private javax.swing.JButton btnVolver;
    private javax.swing.JCheckBox chkUsarPuntos;
    private javax.swing.JLabel lblDesglose;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFuncion;
    private javax.swing.JLabel lblPelicula;
    private javax.swing.JLabel lblSala;
    private javax.swing.JLabel lblSubtituloDetalles;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlAcciones;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlDetalles;
    private javax.swing.JPanel pnlFilaTotal;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlInferior;
    private javax.swing.JPanel pnlMensajes;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPago;
    private javax.swing.JPanel pnlPildoraTotal;
    private javax.swing.JPanel pnlPoster;
    private javax.swing.JPanel pnlTarjetaCompra;
    private javax.swing.JPanel pnlUsuario;
    // End of variables declaration//GEN-END:variables
}
