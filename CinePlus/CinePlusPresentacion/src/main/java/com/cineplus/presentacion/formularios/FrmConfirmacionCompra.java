package com.cineplus.presentacion.formularios;

import com.cineplus.dto.BoletoDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.TarjetaPelicula;
import com.cineplus.presentacion.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla de confirmacion despues de una compra aprobada
 *
 * @author yakor
 */
public class FrmConfirmacionCompra extends JFrame {

    private static final DateTimeFormatter FORMATO_FUNCION =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM · HH:mm", Locale.forLanguageTag("es-MX"));

    private CoordinadorAplicacion coordinador;
    private CompraDTO compra;

    /**
     * Constructor 
     */
    public FrmConfirmacionCompra() {
        initComponents();
        configurarVista();
    }

    /**
     * funcion, los asientos, el cine y los puntos ganados
     */
    public FrmConfirmacionCompra(CoordinadorAplicacion coordinador, CompraDTO compra) {
        this();
        this.coordinador = coordinador;
        this.compra = compra;
        pnlNavegacion.add(new PanelNavegacion(coordinador, Seccion.CARTELERA), BorderLayout.CENTER);

        lblPelicula.setText(compra.tituloPelicula());
        lblFuncion.setText(capitalizar(FORMATO_FUNCION.format(compra.fechaHoraFuncion()))
                + " · " + compra.tipoFuncion());
        lblSala.setText(compra.cine() + "   SALA " + compra.numeroSala());
        lblAsientos.setText("Asientos: " + compra.boletos().stream()
                .map(BoletoDTO::asiento)
                .collect(Collectors.joining(", ")));
        lblPago.setText("Pagado con " + compra.metodoPago()
                + (compra.ultimosDigitos() == null ? "" : " •••• " + compra.ultimosDigitos())
                + " - $" + precio(compra.montoCobrado()));
        lblPuntos.setText("~ Sumaste " + compra.puntosGanados() + " puntos CinePLUS ~");

        btnIrBoletos.addActionListener(e -> coordinador.navegar(Seccion.BOLETOS));
    }

    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Confirmación de compra");

        pnlCabecera.add(PanelEncabezado.centrado(), BorderLayout.CENTER);
        pnlContenido.setBorder(new EmptyBorder(0, 14, 2, 14));
        Tema.tarjetaConBorde(pnlTarjeta, Tema.TARJETA_MORADA, Tema.ROJO_PRO, 14, new Insets(20, 24, 20, 24));

        lblTitulo.setFont(Tema.fuente(Font.BOLD, 32));
        lblTitulo.setForeground(Color.WHITE);
        lblPelicula.setFont(Tema.fuente(Font.BOLD, 22));
        lblPelicula.setForeground(Color.WHITE);
        for (javax.swing.JLabel etiqueta : new javax.swing.JLabel[]{lblFuncion, lblSala, lblAsientos, lblPago}) {
            etiqueta.setFont(Tema.fuente(Font.BOLD, 16));
            etiqueta.setForeground(Tema.TEXTO_SUAVE);
        }
        lblPuntos.setFont(Tema.fuente(Font.BOLD, 20));
        lblPuntos.setForeground(Tema.ROJO_CONFIRMACION);

        Tema.botonFigma(btnIrBoletos, Tema.VINO_BRILLANTE, 14);
        btnIrBoletos.setFont(Tema.fuente(Font.BOLD, 16));
        btnIrBoletos.setForeground(Color.WHITE);
        btnIrBoletos.setPreferredSize(new Dimension(240, 40));
    }

    /**
     * Muestra el poster de la pelicula comprada.
     */
    public void mostrarPelicula(PeliculaDTO pelicula) {
        pnlPoster.removeAll();
        pnlPoster.add(new TarjetaPelicula(pelicula, coordinador, null), BorderLayout.CENTER);
        pnlPoster.revalidate();
        pnlPoster.repaint();
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
        pnlTarjeta = new javax.swing.JPanel();
        pnlPoster = new javax.swing.JPanel();
        pnlDetalles = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblPelicula = new javax.swing.JLabel();
        lblFuncion = new javax.swing.JLabel();
        lblSala = new javax.swing.JLabel();
        lblAsientos = new javax.swing.JLabel();
        lblPago = new javax.swing.JLabel();
        lblPuntos = new javax.swing.JLabel();
        pnlBotones = new javax.swing.JPanel();
        btnIrBoletos = new javax.swing.JButton();
        pnlQr = new javax.swing.JPanel();
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

        pnlTarjeta.setLayout(new java.awt.BorderLayout(24, 12));

        pnlPoster.setOpaque(false);
        pnlPoster.setPreferredSize(new java.awt.Dimension(220, 330));
        pnlPoster.setLayout(new java.awt.BorderLayout());
        pnlTarjeta.add(pnlPoster, java.awt.BorderLayout.WEST);

        pnlDetalles.setOpaque(false);
        pnlDetalles.setLayout(new java.awt.GridBagLayout());

        lblTitulo.setText("¡Gracias por tu Compra!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 14, 0);
        pnlDetalles.add(lblTitulo, gridBagConstraints);

        lblPelicula.setText("Película");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        pnlDetalles.add(lblPelicula, gridBagConstraints);

        lblFuncion.setText("Función");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlDetalles.add(lblFuncion, gridBagConstraints);

        lblSala.setText("Cine - Sala");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlDetalles.add(lblSala, gridBagConstraints);

        lblAsientos.setText("Asientos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlDetalles.add(lblAsientos, gridBagConstraints);

        lblPago.setText("Pago");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 18, 0);
        pnlDetalles.add(lblPago, gridBagConstraints);

        lblPuntos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPuntos.setText("~ Sumaste 0 puntos CinePLUS ~");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 18, 0);
        pnlDetalles.add(lblPuntos, gridBagConstraints);

        pnlBotones.setOpaque(false);
        pnlBotones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnIrBoletos.setText("Ir a Boletos");
        pnlBotones.add(btnIrBoletos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        pnlDetalles.add(pnlBotones, gridBagConstraints);

        pnlTarjeta.add(pnlDetalles, java.awt.BorderLayout.CENTER);

        pnlQr.setOpaque(false);
        pnlQr.setPreferredSize(new java.awt.Dimension(220, 220));
        pnlQr.setLayout(new java.awt.BorderLayout());
        pnlTarjeta.add(pnlQr, java.awt.BorderLayout.EAST);

        pnlContenido.add(pnlTarjeta, java.awt.BorderLayout.CENTER);

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
    private javax.swing.JButton btnIrBoletos;
    private javax.swing.JLabel lblAsientos;
    private javax.swing.JLabel lblFuncion;
    private javax.swing.JLabel lblPago;
    private javax.swing.JLabel lblPelicula;
    private javax.swing.JLabel lblPuntos;
    private javax.swing.JLabel lblSala;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlDetalles;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlInferior;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPoster;
    private javax.swing.JPanel pnlQr;
    private javax.swing.JPanel pnlTarjeta;
    // End of variables declaration//GEN-END:variables
}
