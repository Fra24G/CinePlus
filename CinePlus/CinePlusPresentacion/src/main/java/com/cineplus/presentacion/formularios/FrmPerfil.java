package com.cineplus.presentacion.formularios;

import com.cineplus.dto.ActualizarPerfilDTO;
import com.cineplus.dto.MembresiaDTO;
import com.cineplus.dto.PerfilDTO;
import com.cineplus.dto.UsuarioDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Pantalla "Perfil CinePLUS": datos del usuario, saldo de puntos, plan actual,
 * edicion del perfil y acceso a la Vista ADMIN (solo administradores).
 *
 * @author Luis Guillermo Franco Murrieta
 */
public class FrmPerfil extends JFrame {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CoordinadorAplicacion coordinador;
    private PanelNavegacion navegacion;

    /** Constructor para el editor visual de NetBeans. */
    public FrmPerfil() {
        initComponents();
        configurarVista();
    }

    /** Arma la vista con la barra de navegacion; los datos los carga CoordinadorCuenta. */
    public FrmPerfil(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        navegacion = new PanelNavegacion(coordinador, Seccion.PERFIL);
        pnlNavegacion.add(navegacion, BorderLayout.CENTER);
        conectarEventos();
    }

    /** Estilos con Tema y propiedades de FlatLaf. */
    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Perfil");
        Tema.titulo(lblTitulo);

        Tema.tarjeta(pnlResumen);
        lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD, 20f));
        lblNombre.setForeground(Tema.TEXTO);
        lblPuntos.setForeground(Tema.TEXTO);
        lblPlan.setFont(lblPlan.getFont().deriveFont(Font.BOLD));
        lblPlan.setForeground(Tema.MUTED);

        for (JLabel etiqueta : new JLabel[]{lblCorreoTitulo, lblNombreTitulo,
            lblNombreUsuarioTitulo, lblTelefonoTitulo, lblContrasenaTitulo}) {
            etiqueta.setForeground(Tema.MUTED);
        }
        lblCorreo.setForeground(Tema.TEXTO);

        Tema.campo(txtNombre, "Tu nombre completo");
        Tema.campo(txtNombreUsuario, "Como quieres que te vean (opcional)");
        Tema.campo(txtTelefono, "Opcional, de 7 a 20 dígitos");
        Tema.campo(pwdNuevaContrasena, "Déjala vacía para no cambiarla");
        pwdNuevaContrasena.putClientProperty("FlatLaf.style", "showRevealButton: true");

        Tema.botonPrimario(btnGuardar);
        Tema.botonSecundario(btnVerHistorial);
        Tema.botonSecundario(btnVistaAdmin);
        Tema.botonSecundario(btnCerrarSesion);
        btnVistaAdmin.setVisible(false);

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
    }

    private void conectarEventos() {
        btnGuardar.addActionListener(e -> guardar());
        // TODO: Conectar cuando se integre Dev 4 (mientras tanto CoordinadorBoletos muestra "proximamente")
        btnVerHistorial.addActionListener(e -> coordinador.navegar(Seccion.HISTORIAL));
        // TODO: Conectar cuando se integre Dev 5 (mientras tanto CoordinadorAdmin muestra "proximamente")
        btnVistaAdmin.addActionListener(e -> coordinador.cuenta().abrirVistaAdmin());
        btnCerrarSesion.addActionListener(e -> coordinador.cerrarSesion());
    }

    private void guardar() {
        mostrarError(null);
        coordinador.cuenta().actualizarPerfil(this, leerDatos());
    }

    /**
     * Nombre completo, saldo de puntos, plan y datos editables. "Vista ADMIN"
     * solo es visible si el rol es ADMIN.
     */
    public void mostrarPerfil(PerfilDTO perfil) {
        UsuarioDTO usuario = perfil.usuario();
        lblNombre.setText(usuario.nombre());
        int puntos = usuario.puntosAcumulados() == null ? 0 : usuario.puntosAcumulados();
        lblPuntos.setText("Saldo: " + puntos + " puntos CinePLUS");

        if (perfil.pro()) {
            MembresiaDTO membresia = perfil.membresia();
            String vence = (membresia != null && membresia.fechaVencimiento() != null)
                    ? " · vence el " + FECHA.format(membresia.fechaVencimiento())
                    : "";
            lblPlan.setText("Eres CinePLUS Pro" + vence);
            lblPlan.setForeground(Tema.TEXTO);
        } else {
            lblPlan.setText("Eres CinePLUS+");
            lblPlan.setForeground(Tema.MUTED);
        }

        lblCorreo.setText(usuario.correo());
        txtNombre.setText(usuario.nombre());
        txtNombreUsuario.setText(usuario.nombreUsuario() == null ? "" : usuario.nombreUsuario());
        txtTelefono.setText(usuario.telefono() == null ? "" : usuario.telefono());
        pwdNuevaContrasena.setText("");

        btnVistaAdmin.setVisible(usuario.esAdmin());
        if (navegacion != null) {
            navegacion.actualizar();
        }
        pnlBotones.revalidate();
    }

    /** Arma el DTO con los campos de la pantalla. La contrasena vacia significa "no cambiar". */
    public ActualizarPerfilDTO leerDatos() {
        char[] clave = pwdNuevaContrasena.getPassword();
        String nueva = clave.length == 0 ? null : new String(clave);
        Arrays.fill(clave, '\0');
        pwdNuevaContrasena.setText("");
        return new ActualizarPerfilDTO(
                txtNombre.getText(),
                txtNombreUsuario.getText(),
                txtTelefono.getText(),
                nueva);
    }

    /** Deshabilita los botones y muestra "Cargando…" mientras corre la tarea. */
    public void ocupado(boolean ocupado) {
        btnGuardar.setEnabled(!ocupado);
        btnVerHistorial.setEnabled(!ocupado);
        btnVistaAdmin.setEnabled(!ocupado);
        btnCerrarSesion.setEnabled(!ocupado);
        lblEstado.setText(ocupado ? "Cargando…" : " ");
        if (ocupado) {
            mostrarError(null);
        }
    }

    /** Muestra el mensaje de la Exception en la etiqueta roja (null la limpia). */
    public void mostrarError(String mensaje) {
        lblError.setText(mensaje == null || mensaje.isBlank()
                ? " "
                : "<html>" + Tema.escapar(mensaje) + "</html>");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlContenido = new javax.swing.JPanel();
        pnlFormulario = new javax.swing.JPanel();
        pnlResumen = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        lblPuntos = new javax.swing.JLabel();
        lblPlan = new javax.swing.JLabel();
        pnlDatos = new javax.swing.JPanel();
        lblCorreoTitulo = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        lblNombreTitulo = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblNombreUsuarioTitulo = new javax.swing.JLabel();
        txtNombreUsuario = new javax.swing.JTextField();
        lblTelefonoTitulo = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblContrasenaTitulo = new javax.swing.JLabel();
        pwdNuevaContrasena = new javax.swing.JPasswordField();
        pnlInferior = new javax.swing.JPanel();
        lblError = new javax.swing.JLabel();
        pnlBotones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnVerHistorial = new javax.swing.JButton();
        btnVistaAdmin = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();
        pnlNavegacion = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        lblTitulo.setText("Perfil CinePLUS");
        pnlFondo.add(lblTitulo, java.awt.BorderLayout.NORTH);
        pnlContenido.setLayout(new java.awt.BorderLayout());

        pnlFormulario.setLayout(new java.awt.BorderLayout(0, 16));

        pnlResumen.setLayout(new java.awt.GridLayout(3, 1, 0, 4));

        lblNombre.setText("Nombre del usuario");
        pnlResumen.add(lblNombre);
        lblPuntos.setText("Saldo: 0 puntos CinePLUS");
        pnlResumen.add(lblPuntos);
        lblPlan.setText("Eres CinePLUS+");
        pnlResumen.add(lblPlan);
        pnlFormulario.add(pnlResumen, java.awt.BorderLayout.NORTH);

        pnlDatos.setLayout(new java.awt.GridLayout(5, 2, 12, 10));

        lblCorreoTitulo.setText("CORREO");
        pnlDatos.add(lblCorreoTitulo);
        lblCorreo.setText("correo@ejemplo.com");
        pnlDatos.add(lblCorreo);
        lblNombreTitulo.setText("NOMBRE COMPLETO");
        pnlDatos.add(lblNombreTitulo);
        pnlDatos.add(txtNombre);
        lblNombreUsuarioTitulo.setText("NOMBRE DE USUARIO");
        pnlDatos.add(lblNombreUsuarioTitulo);
        pnlDatos.add(txtNombreUsuario);
        lblTelefonoTitulo.setText("TELÉFONO");
        pnlDatos.add(lblTelefonoTitulo);
        pnlDatos.add(txtTelefono);
        lblContrasenaTitulo.setText("NUEVA CONTRASEÑA");
        pnlDatos.add(lblContrasenaTitulo);
        pnlDatos.add(pwdNuevaContrasena);
        pnlFormulario.add(pnlDatos, java.awt.BorderLayout.CENTER);

        pnlInferior.setLayout(new java.awt.BorderLayout(0, 8));

        lblError.setText(" ");
        pnlInferior.add(lblError, java.awt.BorderLayout.NORTH);
        pnlBotones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));

        btnGuardar.setText("Guardar");
        pnlBotones.add(btnGuardar);
        btnVerHistorial.setText("Ver historial");
        pnlBotones.add(btnVerHistorial);
        btnVistaAdmin.setText("Vista ADMIN");
        pnlBotones.add(btnVistaAdmin);
        btnCerrarSesion.setText("Cerrar sesión");
        pnlBotones.add(btnCerrarSesion);
        lblEstado.setText(" ");
        pnlBotones.add(lblEstado);
        pnlInferior.add(pnlBotones, java.awt.BorderLayout.CENTER);

        pnlFormulario.add(pnlInferior, java.awt.BorderLayout.SOUTH);

        pnlContenido.add(pnlFormulario, java.awt.BorderLayout.NORTH);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);


        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnVerHistorial;
    private javax.swing.JButton btnVistaAdmin;
    private javax.swing.JLabel lblContrasenaTitulo;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblCorreoTitulo;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNombreTitulo;
    private javax.swing.JLabel lblNombreUsuarioTitulo;
    private javax.swing.JLabel lblPlan;
    private javax.swing.JLabel lblPuntos;
    private javax.swing.JLabel lblTelefonoTitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlInferior;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlResumen;
    private javax.swing.JPasswordField pwdNuevaContrasena;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNombreUsuario;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
