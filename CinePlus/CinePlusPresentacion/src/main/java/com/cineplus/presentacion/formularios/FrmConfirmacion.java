package com.cineplus.presentacion.formularios;

import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Pantalla generica de confirmacion ("¡Se realizo la operacion con exito!" o
 * "¡Ya eres Pro! Bienvenido") con cuenta regresiva, encabezado CinePLUS centrado y
 * barra de navegacion inferior tal como en el Figma. La reutilizan compra, cancelacion,
 * membresia, resenas y admin. Se abre con coordinador.confirmar(mensaje, alTerminar).
 *
 * @author luisf
 */
public class FrmConfirmacion extends JFrame {

    private static final int SEGUNDOS = 5;

    private Runnable alTerminar;
    private Timer timer;
    private int restantes = SEGUNDOS;

    public FrmConfirmacion() {
        initComponents();
        configurarVista(null);
    }

    public FrmConfirmacion(CoordinadorAplicacion coordinador, String mensaje, Runnable alTerminar) {
        initComponents();
        this.alTerminar = alTerminar;
        configurarVista(coordinador);
        if (mensaje != null && !mensaje.isBlank()) {
            lblMensaje.setText(formatearMensaje(mensaje));
        }
        lblCuenta.setText("Regresando en " + restantes + "...");
        timer = new Timer(1000, e -> contar());
        timer.start();
    }

    private void configurarVista(CoordinadorAplicacion coordinador) {
        Tema.preparar(this);
        setTitle("CinePlus");
        pnlCabecera.add(PanelEncabezado.centrado(), BorderLayout.CENTER);
        pnlNavegacion.add(new PanelNavegacion(coordinador, null), BorderLayout.CENTER);
        lblMensaje.setFont(Tema.fuente(Font.BOLD, 42));
        lblMensaje.setForeground(new Color(0xd9d3d7));
        lblCuenta.setFont(Tema.fuente(Font.BOLD, 20));
        lblCuenta.setForeground(new Color(0xd9d3d7));
    }

    /** Si el mensaje menciona "Pro", pinta esa palabra en rojo como en el Figma. */
    private static String formatearMensaje(String mensaje) {
        String seguro = Tema.escapar(mensaje);
        if (mensaje.contains("Pro")) {
            String conPro = seguro.replaceAll("\\bPro\\b",
                    "<font color='" + Tema.hex(Tema.ROJO_CONFIRMACION) + "'>Pro</font>");
            return "<html><div style='text-align:center;color:#ffffff;padding:0 12px'>" + conPro + "</div></html>";
        }
        return "<html><div style='text-align:center;padding:0 12px'>" + seguro + "</div></html>";
    }

    /** Cada segundo baja el numero; al llegar a 0 detiene el timer y ejecuta alTerminar. */
    private void contar() {
        restantes--;
        if (restantes > 0) {
            lblCuenta.setText("Regresando en " + restantes + "...");
            return;
        }
        timer.stop();
        if (alTerminar != null && isDisplayable()) {
            alTerminar.run();
        }
    }

    @Override
    public void dispose() {
        if (timer != null) {
            timer.stop();
        }
        super.dispose();
    }

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gbc;

        pnlFondo = new javax.swing.JPanel();
        pnlCabecera = new javax.swing.JPanel();
        pnlCentro = new javax.swing.JPanel();
        lblMensaje = new javax.swing.JLabel();
        lblCuenta = new javax.swing.JLabel();
        pnlNavegacion = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        pnlCabecera.setOpaque(false);
        pnlCabecera.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlCabecera, java.awt.BorderLayout.NORTH);

        pnlCentro.setOpaque(false);
        pnlCentro.setLayout(new java.awt.GridBagLayout());

        lblMensaje.setFont(new java.awt.Font("Segoe UI", 1, 40)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(246, 241, 244));
        lblMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMensaje.setText("¡Se realizó la operación con éxito!");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 16, 12, 16);
        pnlCentro.add(lblMensaje, gbc);

        lblCuenta.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblCuenta.setForeground(new java.awt.Color(217, 211, 215));
        lblCuenta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCuenta.setText("Regresando en 5...");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlCentro.add(lblCuenta, gbc);

        pnlFondo.add(pnlCentro, java.awt.BorderLayout.CENTER);

        pnlNavegacion.setOpaque(false);
        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblCuenta;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlCentro;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlNavegacion;
    // End of variables declaration//GEN-END:variables
}
