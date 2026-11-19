package com.cineplus.presentacion.formularios;

import com.cineplus.dto.RegistroUsuarioDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelPosters;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Pantalla de registro (F2): logo CinePLUS grande monoespaciado arriba a la izquierda,
 * subtitulo "Registrate para acceder a nuestros beneficios:", campos grandes redondeados
 * NOMBRES, CORREO, CONTRASEÑA y FECHA DE NACIMIENTO (mas corto), enlace subrayado
 * "¿Ya tienes cuenta?" centrado sobre el boton "Registrar", abanico de 3 posters a la
 * derecha y capsula inferior con el boton "CinePLUS" para regresar al inicio.
 *
 * @author luisf
 */
public class FrmRegistro extends JFrame {

    private static final DateTimeFormatter[] FORMATOS_FECHA = {
        DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT)
    };

    private CoordinadorAplicacion coordinador;

    public FrmRegistro() {
        initComponents();
        configurarVista();
    }

    public FrmRegistro(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        pnlPosters.add(new PanelPosters(coordinador), BorderLayout.CENTER);
    }

    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Registro");

        Tema.estiloLogo(lblMarca, 62);
        lblTitulo.setFont(Tema.fuente(Font.BOLD, 21));
        lblTitulo.setForeground(Color.WHITE);

        Tema.campoFigma(txtNombre, "NOMBRES", 14, 52);
        Tema.campoFigma(txtCorreo, "CORREO", 14, 52);
        Tema.campoFigma(txtContrasena, "CONTRASEÑA", 14, 52);
        txtContrasena.setEchoChar('●');
        Tema.campoFigma(txtFecha, "FECHA DE NACIMIENTO", 14, 52);
        txtFecha.setPreferredSize(new Dimension(215, 52));
        txtFecha.setMinimumSize(new Dimension(215, 52));
        txtFecha.setToolTipText("Formato dd/mm/aaaa");

        txtNombre.getAccessibleContext().setAccessibleName("Nombres");
        txtCorreo.getAccessibleContext().setAccessibleName("Correo");
        txtContrasena.getAccessibleContext().setAccessibleName("Contraseña");
        txtFecha.getAccessibleContext().setAccessibleName("Fecha de nacimiento");

        Tema.enlace(btnCambiar, new Color(0xb7afb4), true);
        btnCambiar.setFont(Tema.fuente(Font.BOLD, 14));

        Tema.botonContorno(btnEnviar, new Color(0x3f0e13), new Color(0x140406), 16);
        btnEnviar.setFont(Tema.fuente(Font.BOLD, 15));
        btnEnviar.setPreferredSize(new Dimension(132, 38));

        Tema.botonContorno(btnVolver, new Color(0x410f14), new Color(0x160507), 14);
        btnVolver.setFont(Tema.fuente(Font.BOLD, 15));
        btnVolver.setPreferredSize(new Dimension(122, 34));

        lblMensaje.setForeground(Tema.ERROR);
        getRootPane().setDefaultButton(btnEnviar);
    }

    private void btnVolverActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnVolverActionPerformed
        if (coordinador != null) {
            coordinador.inicio();
        }
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnCambiarActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnCambiarActionPerformed
        if (coordinador != null) {
            coordinador.login();
        }
    }//GEN-LAST:event_btnCambiarActionPerformed

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnEnviarActionPerformed
        if (coordinador == null) {
            return;
        }
        LocalDate fecha = parsearFecha(txtFecha.getText());
        if (fecha == null) {
            mostrarError("Escribe una fecha válida con formato dd/mm/aaaa.");
            txtFecha.requestFocusInWindow();
            return;
        }
        char[] clave = txtContrasena.getPassword();
        coordinador.registrar(
                this,
                new RegistroUsuarioDTO(txtNombre.getText(), txtCorreo.getText(), clave, fecha, null, null));
    }//GEN-LAST:event_btnEnviarActionPerformed

    private static LocalDate parsearFecha(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String limpio = texto.strip();
        for (DateTimeFormatter formato : FORMATOS_FECHA) {
            try {
                return LocalDate.parse(limpio, formato);
            } catch (DateTimeParseException ignorada) {
                // Prueba el siguiente formato
            }
        }
        return null;
    }

    public void ocupado(boolean ocupado) {
        btnEnviar.setEnabled(!ocupado);
        btnVolver.setEnabled(!ocupado);
        btnCambiar.setEnabled(!ocupado);
        txtCorreo.setEnabled(!ocupado);
        txtContrasena.setEnabled(!ocupado);
        txtNombre.setEnabled(!ocupado);
        txtFecha.setEnabled(!ocupado);
        btnEnviar.setText(ocupado ? "Un momento…" : "Registrar");
        if (ocupado) {
            lblMensaje.setText(" ");
        }
    }

    public void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje == null || mensaje.isBlank()
                ? " "
                : "<html>" + Tema.escapar(mensaje) + "</html>");
        lblMensaje.setToolTipText(mensaje);
        txtContrasena.setText("");
    }

    /** Capsula inferior que envuelve al boton "CinePLUS" como en el Figma. */
    private static final class CapsulaPie extends JPanel {

        CapsulaPie() {
            super(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 7));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.NAV_FONDO);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.setColor(Tema.NAV_BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            } finally {
                g2.dispose();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gbc;

        pnlFondo = new javax.swing.JPanel();
        pnlContenido = new javax.swing.JPanel();
        pnlFormulario = new javax.swing.JPanel();
        lblMarca = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtCorreo = new javax.swing.JTextField();
        txtContrasena = new javax.swing.JPasswordField();
        txtFecha = new javax.swing.JTextField();
        lblMensaje = new javax.swing.JLabel();
        pnlAcciones = new javax.swing.JPanel();
        btnCambiar = new javax.swing.JButton();
        btnEnviar = new javax.swing.JButton();
        pnlPosters = new javax.swing.JPanel();
        pnlPie = new javax.swing.JPanel();
        pnlCapsula = new CapsulaPie();
        btnVolver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 10));

        pnlContenido.setOpaque(false);
        pnlContenido.setLayout(new java.awt.GridLayout(1, 2, 48, 0));

        pnlFormulario.setOpaque(false);
        pnlFormulario.setLayout(new java.awt.GridBagLayout());

        lblMarca.setFont(new java.awt.Font("Monospaced", 1, 62)); // NOI18N
        lblMarca.setForeground(new java.awt.Color(255, 255, 255));
        lblMarca.setText("CinePLUS");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlFormulario.add(lblMarca, gbc);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 21)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Registrate para acceder a nuestros beneficios:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 18, 0);
        pnlFormulario.add(lblTitulo, gbc);

        txtNombre.setPreferredSize(new java.awt.Dimension(460, 52));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 16, 0);
        pnlFormulario.add(txtNombre, gbc);

        txtCorreo.setPreferredSize(new java.awt.Dimension(460, 52));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 16, 0);
        pnlFormulario.add(txtCorreo, gbc);

        txtContrasena.setPreferredSize(new java.awt.Dimension(460, 52));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 16, 0);
        pnlFormulario.add(txtContrasena, gbc);

        txtFecha.setPreferredSize(new java.awt.Dimension(215, 52));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlFormulario.add(txtFecha, gbc);

        lblMensaje.setForeground(new java.awt.Color(224, 96, 111));
        lblMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMensaje.setText(" ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 6, 0);
        pnlFormulario.add(lblMensaje, gbc);

        pnlAcciones.setOpaque(false);
        pnlAcciones.setLayout(new java.awt.GridBagLayout());

        btnCambiar.setText("¿Ya tienes cuenta?");
        btnCambiar.addActionListener(this::btnCambiarActionPerformed);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlAcciones.add(btnCambiar, gbc);

        btnEnviar.setText("Registrar");
        btnEnviar.addActionListener(this::btnEnviarActionPerformed);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlAcciones.add(btnEnviar, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        pnlFormulario.add(pnlAcciones, gbc);

        pnlContenido.add(pnlFormulario);

        pnlPosters.setOpaque(false);
        pnlPosters.setLayout(new java.awt.BorderLayout());
        pnlContenido.add(pnlPosters);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlPie.setOpaque(false);
        pnlPie.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnVolver.setText("CinePLUS");
        btnVolver.addActionListener(this::btnVolverActionPerformed);
        pnlCapsula.add(btnVolver);
        pnlPie.add(pnlCapsula);

        pnlFondo.add(pnlPie, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCambiar;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlAcciones;
    private javax.swing.JPanel pnlCapsula;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlPie;
    private javax.swing.JPanel pnlPosters;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
