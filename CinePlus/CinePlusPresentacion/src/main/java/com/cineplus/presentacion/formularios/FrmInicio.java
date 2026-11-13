package com.cineplus.presentacion.formularios;

import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PresentadorCartelera;
import com.cineplus.presentacion.Tema;

import javax.swing.JButton;
import javax.swing.JFrame;

public class FrmInicio extends JFrame {

    private CoordinadorAplicacion coordinador;
    private PresentadorCartelera cartelera;

    public FrmInicio() {
        initComponents();
        configurarVista();
    }

    public FrmInicio(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        cargar();
    }

    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Inicio");
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar una película…");
        txtBuscar.getAccessibleContext().setAccessibleName("Buscar película");
        for (JButton b : new JButton[] {btnBoletos, btnHistorial, btnComunidad, btnCinePlus}) {
            b.setEnabled(false);
        }
        Tema.botonSecundario(btnInicio);
        Tema.botonSecundario(btnCartelera);
    }

    private void cargar() {
        cartelera =
                new PresentadorCartelera(
                        coordinador, pnlPeliculas, txtBuscar, lblEstado, lblSinResultados);
        cartelera.cargar(this);
    }

    private void btnInicioActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnInicioActionPerformed
        txtBuscar.setText("");
    }//GEN-LAST:event_btnInicioActionPerformed

    private void btnCarteleraActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnCarteleraActionPerformed
        txtBuscar.setText("");
        txtBuscar.requestFocusInWindow();
    }//GEN-LAST:event_btnCarteleraActionPerformed

    private void btnAccesoActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnAccesoActionPerformed
        if (coordinador != null) {
            coordinador.login();
        }
    }//GEN-LAST:event_btnAccesoActionPerformed

    private void btnRegistroActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnRegistroActionPerformed
        if (coordinador != null) {
            coordinador.registro();
        }
    }//GEN-LAST:event_btnRegistroActionPerformed

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        pnlCabecera = new javax.swing.JPanel();
        lblMarca = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        pnlAcciones = new javax.swing.JPanel();
        btnAcceso = new javax.swing.JButton();
        btnRegistro = new javax.swing.JButton();
        pnlContenido = new javax.swing.JPanel();
        pnlEncabezado = new javax.swing.JPanel();
        lblPeliculasRecientes = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        pnlPeliculas = new javax.swing.JPanel();
        lblSinResultados = new javax.swing.JLabel();
        pnlPie = new javax.swing.JPanel();
        pnlNavegacion = new javax.swing.JPanel();
        btnInicio = new javax.swing.JButton();
        btnCartelera = new javax.swing.JButton();
        btnBoletos = new javax.swing.JButton();
        btnHistorial = new javax.swing.JButton();
        btnComunidad = new javax.swing.JButton();
        btnCinePlus = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout(16, 16));

        pnlFondo.setBackground(new java.awt.Color(22, 15, 23));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        pnlCabecera.setBackground(new java.awt.Color(22, 15, 23));
        pnlCabecera.setLayout(new java.awt.BorderLayout(16, 16));

        lblMarca.setText("CinePLUS");
        lblMarca.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblMarca.setForeground(new java.awt.Color(246, 241, 244));
        pnlCabecera.add(lblMarca, java.awt.BorderLayout.WEST);

        txtBuscar.setPreferredSize(new java.awt.Dimension(290, 36));
        pnlCabecera.add(txtBuscar, java.awt.BorderLayout.CENTER);

        pnlAcciones.setBackground(new java.awt.Color(22, 15, 23));
        pnlAcciones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 5));

        btnAcceso.setText("Iniciar sesión");
        btnAcceso.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAcceso.setForeground(new java.awt.Color(246, 241, 244));
        btnAcceso.setBackground(new java.awt.Color(158, 51, 70));
        btnAcceso.addActionListener(this::btnAccesoActionPerformed);
        pnlAcciones.add(btnAcceso);

        btnRegistro.setText("Crear cuenta");
        btnRegistro.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRegistro.setForeground(new java.awt.Color(246, 241, 244));
        btnRegistro.setBackground(new java.awt.Color(158, 51, 70));
        btnRegistro.addActionListener(this::btnRegistroActionPerformed);
        pnlAcciones.add(btnRegistro);

        pnlCabecera.add(pnlAcciones, java.awt.BorderLayout.EAST);

        pnlFondo.add(pnlCabecera, java.awt.BorderLayout.NORTH);

        pnlContenido.setBackground(new java.awt.Color(22, 15, 23));
        pnlContenido.setLayout(new java.awt.BorderLayout(16, 16));

        pnlEncabezado.setOpaque(false);
        pnlEncabezado.setLayout(new java.awt.BorderLayout(0, 8));

        lblPeliculasRecientes.setText("Películas recientes");
        lblPeliculasRecientes.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblPeliculasRecientes.setForeground(new java.awt.Color(246, 241, 244));
        pnlEncabezado.add(lblPeliculasRecientes, java.awt.BorderLayout.NORTH);

        lblEstado.setText("Cargando películas…");
        lblEstado.setForeground(new java.awt.Color(179, 164, 175));
        pnlEncabezado.add(lblEstado, java.awt.BorderLayout.SOUTH);

        pnlContenido.add(pnlEncabezado, java.awt.BorderLayout.NORTH);

        pnlPeliculas.setBackground(new java.awt.Color(22, 15, 23));
        pnlPeliculas.setPreferredSize(new java.awt.Dimension(1000, 350));
        pnlPeliculas.setLayout(new java.awt.GridLayout(1, 6, 18, 0));
        pnlContenido.add(pnlPeliculas, java.awt.BorderLayout.CENTER);

        lblSinResultados.setText(" ");
        lblSinResultados.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSinResultados.setForeground(new java.awt.Color(179, 164, 175));
        pnlContenido.add(lblSinResultados, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlPie.setBackground(new java.awt.Color(22, 15, 23));
        pnlPie.setLayout(new java.awt.BorderLayout(16, 16));

        pnlNavegacion.setBackground(new java.awt.Color(22, 15, 23));
        pnlNavegacion.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 5));

        btnInicio.setText("Inicio");
        btnInicio.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnInicio.setForeground(new java.awt.Color(246, 241, 244));
        btnInicio.setBackground(new java.awt.Color(158, 51, 70));
        btnInicio.addActionListener(this::btnInicioActionPerformed);
        pnlNavegacion.add(btnInicio);

        btnCartelera.setText("Cartelera");
        btnCartelera.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCartelera.setForeground(new java.awt.Color(246, 241, 244));
        btnCartelera.setBackground(new java.awt.Color(158, 51, 70));
        btnCartelera.addActionListener(this::btnCarteleraActionPerformed);
        pnlNavegacion.add(btnCartelera);

        btnBoletos.setText("Boletos");
        btnBoletos.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBoletos.setForeground(new java.awt.Color(246, 241, 244));
        btnBoletos.setBackground(new java.awt.Color(158, 51, 70));
        pnlNavegacion.add(btnBoletos);

        btnHistorial.setText("Historial");
        btnHistorial.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnHistorial.setForeground(new java.awt.Color(246, 241, 244));
        btnHistorial.setBackground(new java.awt.Color(158, 51, 70));
        pnlNavegacion.add(btnHistorial);

        btnComunidad.setText("Comunidad");
        btnComunidad.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnComunidad.setForeground(new java.awt.Color(246, 241, 244));
        btnComunidad.setBackground(new java.awt.Color(158, 51, 70));
        pnlNavegacion.add(btnComunidad);

        btnCinePlus.setText("CinePLUS");
        btnCinePlus.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCinePlus.setForeground(new java.awt.Color(246, 241, 244));
        btnCinePlus.setBackground(new java.awt.Color(158, 51, 70));
        pnlNavegacion.add(btnCinePlus);

        pnlPie.add(pnlNavegacion, java.awt.BorderLayout.NORTH);

        pnlFondo.add(pnlPie, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcceso;
    private javax.swing.JButton btnBoletos;
    private javax.swing.JButton btnCartelera;
    private javax.swing.JButton btnCinePlus;
    private javax.swing.JButton btnComunidad;
    private javax.swing.JButton btnHistorial;
    private javax.swing.JButton btnInicio;
    private javax.swing.JButton btnRegistro;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblPeliculasRecientes;
    private javax.swing.JLabel lblSinResultados;
    private javax.swing.JPanel pnlAcciones;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPeliculas;
    private javax.swing.JPanel pnlPie;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
