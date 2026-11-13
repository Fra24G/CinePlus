package com.cineplus.presentacion.formularios;

import com.cineplus.dto.CredencialesDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelPosters;
import com.cineplus.presentacion.Tema;

import javax.swing.JFrame;

public class FrmLogin extends JFrame {

    private CoordinadorAplicacion coordinador;

    public FrmLogin() {
        initComponents();
        configurarVista();
    }

    public FrmLogin(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        pnlPosters.add(new PanelPosters(coordinador), java.awt.BorderLayout.CENTER);
    }

    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Iniciar sesión");
        Tema.botonSecundario(btnVolver);
        Tema.botonSecundario(btnCambiar);
        txtCorreo.putClientProperty("JTextField.placeholderText", "nombre@correo.com");
        txtContrasena.putClientProperty("JTextField.placeholderText", "Tu contraseña");
        lblCorreo.setLabelFor(txtCorreo);
        lblContrasena.setLabelFor(txtContrasena);
        txtContrasena.setEchoChar('●');

        getRootPane().setDefaultButton(btnEnviar);
    }

    private void chkMostrarActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_chkMostrarActionPerformed
        txtContrasena.setEchoChar(chkMostrar.isSelected() ? '\0' : '●');
    }//GEN-LAST:event_chkMostrarActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnVolverActionPerformed
        if (coordinador != null) {
            coordinador.inicio();
        }
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnCambiarActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnCambiarActionPerformed
        if (coordinador != null) {
            coordinador.registro();
        }
    }//GEN-LAST:event_btnCambiarActionPerformed

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent e) {//GEN-FIRST:event_btnEnviarActionPerformed

        if (coordinador == null) {
            return;
        }

        char[] clave = txtContrasena.getPassword();
        coordinador.autenticar(this, new CredencialesDTO(txtCorreo.getText(), clave));
    }//GEN-LAST:event_btnEnviarActionPerformed

    public void ocupado(boolean ocupado) {
        btnEnviar.setEnabled(!ocupado);
        btnVolver.setEnabled(!ocupado);
        btnCambiar.setEnabled(!ocupado);
        txtCorreo.setEnabled(!ocupado);
        txtContrasena.setEnabled(!ocupado);
        chkMostrar.setEnabled(!ocupado);
        btnEnviar.setText(ocupado ? "Un momento…" : "Entrar a CinePlus");
        if (ocupado) {
            lblMensaje.setText(" ");
        }
    }

    public void mostrarError(String mensaje) {
        lblMensaje.setText("<html>" + Tema.escapar(mensaje) + "</html>");
        lblMensaje.setToolTipText(mensaje);
        txtContrasena.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        pnlCabecera = new javax.swing.JPanel();
        lblMarca = new javax.swing.JLabel();
        btnVolver = new javax.swing.JButton();
        pnlContenido = new javax.swing.JPanel();
        pnlFormulario = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        txtCorreo = new javax.swing.JTextField();
        lblContrasena = new javax.swing.JLabel();
        txtContrasena = new javax.swing.JPasswordField();
        chkMostrar = new javax.swing.JCheckBox();
        lblMensaje = new javax.swing.JLabel();
        btnEnviar = new javax.swing.JButton();
        btnCambiar = new javax.swing.JButton();
        pnlPosters = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout(16, 16));

        pnlFondo.setBackground(new java.awt.Color(22, 15, 23));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1000, 720));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        pnlCabecera.setBackground(new java.awt.Color(22, 15, 23));
        pnlCabecera.setLayout(new java.awt.BorderLayout(16, 16));

        lblMarca.setText("CinePLUS");
        lblMarca.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblMarca.setForeground(new java.awt.Color(246, 241, 244));
        pnlCabecera.add(lblMarca, java.awt.BorderLayout.WEST);

        btnVolver.setText("← Volver al inicio");
        btnVolver.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnVolver.setForeground(new java.awt.Color(246, 241, 244));
        btnVolver.setBackground(new java.awt.Color(158, 51, 70));
        btnVolver.addActionListener(this::btnVolverActionPerformed);
        pnlCabecera.add(btnVolver, java.awt.BorderLayout.EAST);

        pnlFondo.add(pnlCabecera, java.awt.BorderLayout.NORTH);

        pnlContenido.setBackground(new java.awt.Color(22, 15, 23));
        pnlContenido.setLayout(new java.awt.GridLayout(1, 2, 72, 0));

        pnlFormulario.setBackground(new java.awt.Color(22, 15, 23));

        lblTitulo.setText("Iniciar sesión");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 29)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(246, 241, 244));

        lblCorreo.setText("CORREO ELECTRÓNICO");
        lblCorreo.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblCorreo.setForeground(new java.awt.Color(246, 241, 244));

        lblContrasena.setText("CONTRASEÑA");
        lblContrasena.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblContrasena.setForeground(new java.awt.Color(246, 241, 244));

        chkMostrar.setText("Mostrar contraseña");
        chkMostrar.setForeground(new java.awt.Color(179, 164, 175));
        chkMostrar.setBackground(new java.awt.Color(22, 15, 23));
        chkMostrar.addActionListener(this::chkMostrarActionPerformed);

        lblMensaje.setText(" ");
        lblMensaje.setForeground(new java.awt.Color(255, 173, 184));

        btnEnviar.setText("Entrar a CinePlus");
        btnEnviar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnEnviar.setForeground(new java.awt.Color(246, 241, 244));
        btnEnviar.setBackground(new java.awt.Color(158, 51, 70));
        btnEnviar.addActionListener(this::btnEnviarActionPerformed);

        btnCambiar.setText("¿Aún no tienes cuenta? Regístrate");
        btnCambiar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCambiar.setForeground(new java.awt.Color(246, 241, 244));
        btnCambiar.setBackground(new java.awt.Color(158, 51, 70));
        btnCambiar.addActionListener(this::btnCambiarActionPerformed);

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(chkMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnCambiar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGap(12, 12, 12)
                .addComponent(lblCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(chkMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnCambiar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlContenido.add(pnlFormulario);

        pnlPosters.setBackground(new java.awt.Color(22, 15, 23));
        pnlPosters.setLayout(new java.awt.BorderLayout(16, 16));
        pnlContenido.add(pnlPosters);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCambiar;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JCheckBox chkMostrar;
    private javax.swing.JLabel lblContrasena;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlPosters;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JTextField txtCorreo;
    // End of variables declaration//GEN-END:variables
}
