package com.cineplus.presentacion.formularios;

import com.cineplus.dto.ActualizarPerfilDTO;
import com.cineplus.dto.PerfilDTO;
import com.cineplus.dto.UsuarioDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla "Perfil CinePLUS" (F17): encabezado con logo CinePLUS, pildora de busqueda y
 * botones de campana y mas opciones; 3 columnas al centro (gran circulo avatar color vino
 * con iniciales gigantes y lapiz de edicion; "Detalles de la cuenta:" con campos centrados
 * para nombre de usuario con icono de lapiz y nombre completo, mas el boton vino
 * "Ver historial de compras" y edicion expandible de telefono/contrasena; y tarjeta morada
 * oscura a la derecha con "Eres CinePLUS Pro", "Saldo Actual:", puntos gigantes y barra de
 * progreso roja); boton centrado "Vista ADMIN" encima de la barra de navegacion (solo si
 * el usuario es ADMIN); y barra inferior PanelNavegacion.
 *
 * @author Luis Guillermo Franco Murrieta
 */
public class FrmPerfil extends JFrame {

    private CoordinadorAplicacion coordinador;
    private PanelNavegacion navegacion;
    private final AvatarGrande lienzoAvatar = new AvatarGrande();
    private final BarraPuntos barraPuntos = new BarraPuntos();

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

        pnlCabecera.add(new PanelEncabezado(null), BorderLayout.CENTER);
        pnlAvatarContenedor.add(lienzoAvatar, BorderLayout.CENTER);
        pnlBarraContenedor.add(barraPuntos, BorderLayout.CENTER);

        lblTitulo.setFont(Tema.fuente(Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);

        // Campo superior: "INGRESA TU NOMBRE DE USUARIO" con texto centrado e icono de lapiz a la derecha
        Tema.campoFigma(txtNombreUsuario, "INGRESA TU NOMBRE DE USUARIO", 12, 48);
        txtNombreUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        txtNombreUsuario.setFont(Tema.fuente(Font.BOLD, 15));
        JButton btnLapizCampo = crearBotonLapiz("Editar o guardar nombre de usuario");
        btnLapizCampo.addActionListener(e -> {
            if (!txtNombreUsuario.hasFocus()) {
                txtNombreUsuario.requestFocusInWindow();
                txtNombreUsuario.selectAll();
            } else if (coordinador != null) {
                guardar();
            }
        });
        txtNombreUsuario.putClientProperty("JTextField.trailingComponent", btnLapizCampo);

        // Campo inferior: nombre completo del usuario centrado
        Tema.campoFigma(txtNombre, "NOMBRE COMPLETO", 12, 48);
        txtNombre.setHorizontalAlignment(SwingConstants.CENTER);
        txtNombre.setFont(Tema.fuente(Font.BOLD, 16));
        txtNombre.setForeground(new Color(0xe4dee2));

        // Campos adicionales compactos (telefono y nueva contrasena, desplegables con el lapiz)
        Tema.campoFigma(txtTelefono, "TELÉFONO (OPCIONAL, 7 A 20 DÍGITOS)", 12, 38);
        txtTelefono.setHorizontalAlignment(SwingConstants.CENTER);
        Tema.campoFigma(pwdNuevaContrasena, "NUEVA CONTRASEÑA (VACÍA = NO CAMBIAR)", 12, 38);
        pwdNuevaContrasena.setHorizontalAlignment(SwingConstants.CENTER);
        pwdNuevaContrasena.putClientProperty("FlatLaf.style",
                pwdNuevaContrasena.getClientProperty("FlatLaf.style") + "; showRevealButton: true");
        pnlExtras.setVisible(false);

        // Boton vino redondeado "Ver historial de compras"
        Tema.botonFigma(btnVerHistorial, new Color(0x461217), 20);
        btnVerHistorial.setFont(Tema.fuente(Font.BOLD, 15));
        btnVerHistorial.setForeground(new Color(0xdad2d6));
        btnVerHistorial.setPreferredSize(new Dimension(190, 50));

        // Enlaces discretos para desplegar telefono/contrasena, guardar y cerrar sesion
        Tema.enlace(btnMasDatos, new Color(0x887e84), false);
        btnMasDatos.setFont(Tema.fuente(Font.BOLD, 12));
        Tema.enlace(btnGuardar, new Color(0xb58d92), false);
        btnGuardar.setFont(Tema.fuente(Font.BOLD, 12));
        Tema.enlace(btnCerrarSesion, new Color(0x887e84), false);
        btnCerrarSesion.setFont(Tema.fuente(Font.BOLD, 12));

        // Tarjeta derecha: "Eres CinePLUS Pro", "Saldo Actual:", puntos gigantes y barra roja
        Tema.tarjetaConBorde(
                pnlResumen,
                new Color(0x161024),
                new Color(0x281e3a),
                12,
                new Insets(16, 22, 16, 22));
        lblPlan.setFont(Tema.fuente(Font.BOLD, 27));
        lblPlan.setForeground(Color.WHITE);
        lblPlan.setText("<html>Eres CinePLUS <font color='#bc0d0d'>Pro</font></html>");

        lblSaldoActual.setFont(Tema.fuente(Font.BOLD, 16));
        lblSaldoActual.setForeground(Color.WHITE);

        lblPuntos.setFont(Tema.fuente(Font.BOLD, 62));
        lblPuntos.setForeground(Color.WHITE);

        // Boton centrado "Vista ADMIN" encima de la barra inferior
        Tema.botonFigma(btnVistaAdmin, new Color(0x461217), 20);
        btnVistaAdmin.setFont(Tema.fuente(Font.BOLD, 19));
        btnVistaAdmin.setForeground(new Color(0xb5adb1));
        btnVistaAdmin.setPreferredSize(new Dimension(190, 50));
        btnVistaAdmin.setVisible(false);

        // Etiquetas heredadas ocultas para mantener compatibilidad sin ensuciar el layout
        for (JComponent oculto : new JComponent[]{
            lblNombre, lblCorreoTitulo, lblCorreo, lblNombreTitulo,
            lblNombreUsuarioTitulo, lblTelefonoTitulo, lblContrasenaTitulo}) {
            oculto.setVisible(false);
        }

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
    }

    private JButton crearBotonLapiz(String ayuda) {
        JButton boton = new JButton(Tema.iconoLapiz(22, new Color(0xb8b2b6)));
        boton.setToolTipText(ayuda);
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(36, 36));
        return boton;
    }

    private void conectarEventos() {
        btnGuardar.addActionListener(e -> guardar());
        txtNombreUsuario.addActionListener(e -> guardar());
        txtNombre.addActionListener(e -> guardar());
        txtTelefono.addActionListener(e -> guardar());
        pwdNuevaContrasena.addActionListener(e -> guardar());
        btnMasDatos.addActionListener(e -> alternarExtras());
        lienzoAvatar.alClicLapiz = this::alternarExtras;
        // TODO: Conectar cuando se integre Dev 4 (mientras tanto CoordinadorBoletos muestra "proximamente")
        btnVerHistorial.addActionListener(e -> coordinador.navegar(Seccion.HISTORIAL));
        // TODO: Conectar cuando se integre Dev 5 (mientras tanto CoordinadorAdmin muestra "proximamente")
        btnVistaAdmin.addActionListener(e -> coordinador.cuenta().abrirVistaAdmin());
        btnCerrarSesion.addActionListener(e -> coordinador.cerrarSesion());
    }

    private void alternarExtras() {
        pnlExtras.setVisible(!pnlExtras.isVisible());
        btnMasDatos.setText(pnlExtras.isVisible()
                ? "<html><u>Ocultar teléfono / contraseña</u></html>"
                : "<html><u>Editar teléfono / contraseña</u></html>");
        pnlDetalles.revalidate();
        pnlDetalles.repaint();
    }

    private void guardar() {
        if (coordinador == null) {
            return;
        }
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
        lienzoAvatar.setIniciales(inicialesDe(usuario.nombre()));

        int puntos = usuario.puntosAcumulados() == null ? 0 : usuario.puntosAcumulados();
        lblPuntos.setText(String.valueOf(puntos));
        barraPuntos.setPuntos(puntos);

        if (perfil.pro()) {
            lblPlan.setText("<html>Eres CinePLUS <font color='#bc0d0d'>Pro</font></html>");
        } else {
            lblPlan.setText("<html>Eres CinePLUS<font color='#a8a3ad'>+</font></html>");
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
        pnlInferior.revalidate();
        pnlInferior.repaint();
    }

    private static String inicialesDe(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "YR";
        }
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        int segundo = partes.length >= 3 ? 2 : 1;
        return ("" + Character.toUpperCase(partes[0].charAt(0))
                + Character.toUpperCase(partes[segundo].charAt(0)));
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
        btnMasDatos.setEnabled(!ocupado);
        lblEstado.setText(ocupado ? "Cargando…" : "");
        if (ocupado) {
            mostrarError(null);
        }
    }

    /** Muestra el mensaje de la Exception en la etiqueta roja (null la limpia). */
    public void mostrarError(String mensaje) {
        lblError.setText(mensaje == null || mensaje.isBlank()
                ? ""
                : "<html>" + Tema.escapar(mensaje) + "</html>");
    }

    /** Gran circulo color vino con las iniciales gigantes ("YR") y el icono de lapiz abajo a la derecha. */
    private static final class AvatarGrande extends JComponent {

        private final Icon lapiz = Tema.iconoLapiz(26, new Color(0xb8b2b6));
        private String iniciales = "YR";
        private Runnable alClicLapiz;

        AvatarGrande() {
            setPreferredSize(new Dimension(228, 228));
            setMinimumSize(new Dimension(190, 190));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Editar detalles adicionales del perfil");
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (alClicLapiz != null) {
                        alClicLapiz.run();
                    }
                }
            });
        }

        void setIniciales(String iniciales) {
            this.iniciales = iniciales == null || iniciales.isBlank() ? "YR" : iniciales;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int diametro = Math.min(getWidth(), getHeight()) - 14;
                int x = (getWidth() - diametro) / 2;
                int y = (getHeight() - diametro) / 2;
                g2.setColor(new Color(0x54181e));
                g2.fillOval(x, y, diametro, diametro);

                g2.setFont(Tema.fuente(Font.BOLD, Math.round(diametro * 0.35f)));
                g2.setColor(new Color(0xdad3d7));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (diametro - fm.stringWidth(iniciales)) / 2;
                int ty = y + (diametro - fm.getHeight()) / 2 + fm.getAscent() - 2;
                g2.drawString(iniciales, tx, ty);

                int lx = x + diametro - lapiz.getIconWidth() + 4;
                int ly = y + diametro - lapiz.getIconHeight() - 4;
                lapiz.paintIcon(this, g2, lx, ly);
            } finally {
                g2.dispose();
            }
        }
    }

    /** Barra horizontal de progreso de puntos (fondo gris #303235 y relleno rojo #ea2c2c). */
    private static final class BarraPuntos extends JComponent {

        private int puntos = 999;

        BarraPuntos() {
            setPreferredSize(new Dimension(320, 10));
            setMinimumSize(new Dimension(200, 10));
        }

        void setPuntos(int puntos) {
            this.puntos = Math.max(0, puntos);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(Tema.BARRA_FONDO);
                g2.fillRoundRect(0, 0, w, h, h, h);

                double fraccion = puntos <= 0 ? 0.0 : Math.min(1.0, Math.max(0.08, puntos / 1350.0));
                int relleno = (int) Math.round(w * fraccion);
                if (relleno > 0) {
                    g2.setColor(Tema.ROJO);
                    g2.fillRoundRect(0, 0, relleno, h, h, h);
                }
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
        pnlCabecera = new javax.swing.JPanel();
        pnlContenido = new javax.swing.JPanel();
        pnlAvatarContenedor = new javax.swing.JPanel();
        pnlDetalles = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        txtNombreUsuario = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        pnlExtras = new javax.swing.JPanel();
        txtTelefono = new javax.swing.JTextField();
        pwdNuevaContrasena = new javax.swing.JPasswordField();
        pnlFilaHistorial = new javax.swing.JPanel();
        btnVerHistorial = new javax.swing.JButton();
        pnlBotones = new javax.swing.JPanel();
        btnMasDatos = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        pnlMensajes = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();
        pnlColumnaDerecha = new javax.swing.JPanel();
        pnlResumen = new javax.swing.JPanel();
        lblPlan = new javax.swing.JLabel();
        pnlSaldo = new javax.swing.JPanel();
        lblSaldoActual = new javax.swing.JLabel();
        lblPuntos = new javax.swing.JLabel();
        pnlBarraContenedor = new javax.swing.JPanel();
        pnlInferior = new javax.swing.JPanel();
        pnlFilaAdmin = new javax.swing.JPanel();
        btnVistaAdmin = new javax.swing.JButton();
        pnlNavegacion = new javax.swing.JPanel();
        pnlFormulario = new javax.swing.JPanel();
        pnlDatos = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        lblCorreoTitulo = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        lblNombreTitulo = new javax.swing.JLabel();
        lblNombreUsuarioTitulo = new javax.swing.JLabel();
        lblTelefonoTitulo = new javax.swing.JLabel();
        lblContrasenaTitulo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(12, 10));

        pnlCabecera.setOpaque(false);
        pnlCabecera.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlCabecera, java.awt.BorderLayout.NORTH);

        pnlContenido.setOpaque(false);
        pnlContenido.setLayout(new java.awt.GridBagLayout());

        pnlAvatarContenedor.setOpaque(false);
        pnlAvatarContenedor.setLayout(new java.awt.BorderLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(6, 16, 0, 26);
        pnlContenido.add(pnlAvatarContenedor, gbc);

        pnlDetalles.setOpaque(false);
        pnlDetalles.setLayout(new java.awt.GridBagLayout());

        lblTitulo.setText("Detalles de la cuenta:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(18, 2, 12, 0);
        pnlDetalles.add(lblTitulo, gbc);

        txtNombreUsuario.setPreferredSize(new java.awt.Dimension(380, 48));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlDetalles.add(txtNombreUsuario, gbc);

        txtNombre.setPreferredSize(new java.awt.Dimension(380, 48));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlDetalles.add(txtNombre, gbc);

        pnlExtras.setOpaque(false);
        pnlExtras.setLayout(new java.awt.GridLayout(2, 1, 0, 8));
        pnlExtras.add(txtTelefono);
        pnlExtras.add(pwdNuevaContrasena);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlDetalles.add(pnlExtras, gbc);

        pnlFilaHistorial.setOpaque(false);
        pnlFilaHistorial.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 28, 4));

        btnVerHistorial.setText("<html><center>Ver historial de<br>compras</center></html>");
        pnlFilaHistorial.add(btnVerHistorial);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 4, 0);
        pnlDetalles.add(pnlFilaHistorial, gbc);

        pnlBotones.setOpaque(false);
        pnlBotones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));

        btnMasDatos.setText("Editar teléfono / contraseña");
        pnlBotones.add(btnMasDatos);

        btnGuardar.setText("Guardar cambios");
        pnlBotones.add(btnGuardar);

        btnCerrarSesion.setText("Cerrar sesión");
        pnlBotones.add(btnCerrarSesion);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlDetalles.add(pnlBotones, gbc);

        pnlMensajes.setOpaque(false);
        pnlMensajes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));

        lblEstado.setText("");
        pnlMensajes.add(lblEstado);

        lblError.setText("");
        pnlMensajes.add(lblError);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlDetalles.add(pnlMensajes, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.55;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(8, 0, 0, 22);
        pnlContenido.add(pnlDetalles, gbc);

        pnlColumnaDerecha.setOpaque(false);
        pnlColumnaDerecha.setLayout(new java.awt.BorderLayout());

        pnlResumen.setPreferredSize(new java.awt.Dimension(365, 172));
        pnlResumen.setLayout(new java.awt.BorderLayout(0, 8));

        lblPlan.setText("Eres CinePLUS Pro");
        pnlResumen.add(lblPlan, java.awt.BorderLayout.NORTH);

        pnlSaldo.setOpaque(false);
        pnlSaldo.setLayout(new java.awt.GridBagLayout());

        lblSaldoActual.setText("Saldo Actual:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 12, 14);
        pnlSaldo.add(lblSaldoActual, gbc);

        lblPuntos.setText("999");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 8);
        pnlSaldo.add(lblPuntos, gbc);

        pnlResumen.add(pnlSaldo, java.awt.BorderLayout.CENTER);

        pnlBarraContenedor.setOpaque(false);
        pnlBarraContenedor.setBorder(new EmptyBorder(4, 0, 2, 0));
        pnlBarraContenedor.setLayout(new java.awt.BorderLayout());
        pnlResumen.add(pnlBarraContenedor, java.awt.BorderLayout.SOUTH);

        pnlColumnaDerecha.add(pnlResumen, java.awt.BorderLayout.NORTH);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.45;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(22, 0, 0, 8);
        pnlContenido.add(pnlColumnaDerecha, gbc);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlInferior.setOpaque(false);
        pnlInferior.setLayout(new java.awt.BorderLayout(0, 14));

        pnlFilaAdmin.setOpaque(false);
        pnlFilaAdmin.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        btnVistaAdmin.setText("<html><center>Vista<br>ADMIN</center></html>");
        pnlFilaAdmin.add(btnVistaAdmin);

        pnlInferior.add(pnlFilaAdmin, java.awt.BorderLayout.NORTH);

        pnlNavegacion.setOpaque(false);
        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlInferior.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlInferior, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnMasDatos;
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
    private javax.swing.JLabel lblSaldoActual;
    private javax.swing.JLabel lblTelefonoTitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlAvatarContenedor;
    private javax.swing.JPanel pnlBarraContenedor;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlColumnaDerecha;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlDatos;
    private javax.swing.JPanel pnlDetalles;
    private javax.swing.JPanel pnlExtras;
    private javax.swing.JPanel pnlFilaAdmin;
    private javax.swing.JPanel pnlFilaHistorial;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlInferior;
    private javax.swing.JPanel pnlMensajes;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlResumen;
    private javax.swing.JPanel pnlSaldo;
    private javax.swing.JPasswordField pwdNuevaContrasena;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNombreUsuario;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
