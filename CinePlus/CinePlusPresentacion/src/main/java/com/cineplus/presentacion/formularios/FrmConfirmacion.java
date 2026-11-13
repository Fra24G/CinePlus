package com.cineplus.presentacion.formularios;

import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.Tema;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Pantalla generica "Se realizo la operacion con exito" con cuenta regresiva. La
 * reutilizan compra, cancelacion, membresia, resenas y admin. Se abre con
 * coordinador.confirmar(mensaje, alTerminar).
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
        Tema.preparar(this);
        setTitle("CinePlus");
    }

    public FrmConfirmacion(CoordinadorAplicacion coordinador, String mensaje, Runnable alTerminar) {
        this();
        this.alTerminar = alTerminar;
        if (mensaje != null && !mensaje.isBlank()) {
            lblMensaje.setText("<html><div style='text-align:center'>" + Tema.escapar(mensaje) + "</div></html>");
        }
        lblCuenta.setText("Regresando en " + restantes + "…");
        timer = new Timer(1000, e -> contar());
        timer.start();
    }

    /** Cada segundo baja el numero; al llegar a 0 detiene el timer y ejecuta alTerminar. */
    private void contar() {
        restantes--;
        if (restantes > 0) {
            lblCuenta.setText("Regresando en " + restantes + "…");
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        lblIcono = new javax.swing.JLabel();
        lblMensaje = new javax.swing.JLabel();
        lblCuenta = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(22, 15, 23));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        lblIcono.setFont(new java.awt.Font("Segoe UI", 1, 72)); // NOI18N
        lblIcono.setForeground(new java.awt.Color(158, 51, 70));
        lblIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIcono.setText("✓");
        pnlFondo.add(lblIcono, java.awt.BorderLayout.NORTH);

        lblMensaje.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(246, 241, 244));
        lblMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMensaje.setText("¡Se realizó la operación con éxito!");
        pnlFondo.add(lblMensaje, java.awt.BorderLayout.CENTER);

        lblCuenta.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lblCuenta.setForeground(new java.awt.Color(179, 164, 175));
        lblCuenta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCuenta.setText("Regresando en 5…");
        pnlFondo.add(lblCuenta, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblCuenta;
    private javax.swing.JLabel lblIcono;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JPanel pnlFondo;
    // End of variables declaration//GEN-END:variables
}
