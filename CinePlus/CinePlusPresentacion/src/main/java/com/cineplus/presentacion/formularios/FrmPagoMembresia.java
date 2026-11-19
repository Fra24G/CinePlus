package com.cineplus.presentacion.formularios;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.DatosTarjetaDTO;
import com.cineplus.dto.PlanDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.dto.TipoMetodoPago;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
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
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla "Pago de Membresia" (F14): encabezado con logo CinePLUS centrado y botones de
 * campana y mas opciones; gran tarjeta morada con borde rojo que muestra "¡Estas muy cerca
 * de ser Pro!", "Detalles de Compra:", "Membresia CinePLUS Pro Mensual - $129", la pildora
 * vino "TOTAL - $129 AL MES" y el subpanel de pago con "<Nombre> - Miembro ¿No eres tu?",
 * los 4 campos de tarjeta en 2 filas a la izquierda y los 3 botones apilados (TARJETA,
 * APPLE / GOOGLE PAY y CONTINUAR) a la derecha.
 * <p>
 * TODO: Reemplazar pnlPago por PanelFormularioPago cuando se integre Dev 3. Mientras tanto
 * este formulario arma el DatosPagoDTO por su cuenta y el cobro responde
 * "Pendiente: FabricaPago.para" en la etiqueta de error.
 *
 * @author Luis Guillermo Franco Murrieta
 */
public class FrmPagoMembresia extends JFrame {

    private CoordinadorAplicacion coordinador;
    private PlanDTO plan;
    private TipoMetodoPago metodo = TipoMetodoPago.TARJETA;

    /** Constructor para el editor visual de NetBeans. */
    public FrmPagoMembresia() {
        initComponents();
        configurarVista();
    }

    /**
     * Muestra "¡Estas muy cerca de ser Pro!", el usuario de la sesion ("¿No eres tu?"
     * cierra sesion) y el detalle "Membresia CinePLUS Pro Mensual - $129".
     */
    public FrmPagoMembresia(CoordinadorAplicacion coordinador, PlanDTO plan) {
        this();
        this.coordinador = coordinador;
        this.plan = plan;
        pnlNavegacion.add(new PanelNavegacion(coordinador, Seccion.CINEPLUS), BorderLayout.CENTER);

        SesionDTO sesion = coordinador.sesion();
        lblUsuario.setText(sesion == null
                ? "Invitado - Miembro"
                : nombreCorto(sesion.usuario().nombre()) + " - Miembro");
        if (plan != null) {
            String monto = precio(plan.precioMensual());
            lblDetalle.setText("Membresía " + plan.nombre()
                    + " Mensual             - $" + monto);
            lblTotal.setText("TOTAL - $" + monto + " AL MES");
        }
        conectarEventos();
    }

    /** Si el nombre tiene 4 palabras ("Yazid Enrique Revilla Bernal"), muestra "Yazid Revilla". */
    private static String nombreCorto(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Usuario";
        }
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 3) {
            return partes[0] + " " + partes[2];
        }
        return nombre.trim();
    }

    /** Estilos con Tema y propiedades de FlatLaf. */
    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Pago de membresía");

        pnlCabecera.add(PanelEncabezado.centrado(), BorderLayout.CENTER);
        pnlContenido.setBorder(new EmptyBorder(0, 14, 2, 14));

        Tema.tarjetaConBorde(
                pnlTarjetaCompra,
                Tema.TARJETA_MORADA,
                Tema.ROJO_PRO,
                14,
                new Insets(16, 30, 18, 30));

        lblTitulo.setFont(Tema.fuente(Font.BOLD, 30));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setText("<html>¡Estás muy cerca de ser <font color='#bc0d0d'>Pro</font>!</html>");

        lblSubtituloDetalles.setFont(Tema.fuente(Font.BOLD, 24));
        lblSubtituloDetalles.setForeground(Color.WHITE);

        lblDetalle.setFont(Tema.fuente(Font.BOLD, 16));
        lblDetalle.setForeground(new Color(0xa6a0aa));

        lblTotal.setFont(Tema.fuente(Font.BOLD, 20));
        lblTotal.setForeground(Color.WHITE);
        pnlPildoraTotal.setPreferredSize(new Dimension(330, 46));

        // Subpanel de pago redondeado oscuro
        Tema.tarjetaConBorde(
                pnlPago,
                Tema.SUBPANEL_PAGO,
                null,
                22,
                new Insets(12, 22, 14, 22));

        lblUsuario.setFont(Tema.fuente(Font.BOLD, 18));
        lblUsuario.setForeground(Color.WHITE);

        Tema.enlace(btnNoEresTu, Tema.ENLACE, false);
        btnNoEresTu.setFont(Tema.fuente(Font.BOLD, 18));

        Tema.campoFigma(txtTitular, "NOMBRE DEL TITULAR", 12, 42);
        Tema.campoFigma(txtVencimiento, "MM/YY", 12, 42);
        txtVencimiento.setHorizontalAlignment(SwingConstants.CENTER);
        txtVencimiento.setPreferredSize(new Dimension(108, 42));
        txtVencimiento.setMinimumSize(new Dimension(108, 42));

        Tema.campoFigma(txtNumero, "NÚMERO DE TARJETA", 12, 42);
        Tema.campoFigma(pwdCvv, "CVV", 12, 42);
        pwdCvv.setHorizontalAlignment(SwingConstants.CENTER);
        pwdCvv.setPreferredSize(new Dimension(108, 42));
        pwdCvv.setMinimumSize(new Dimension(108, 42));

        lblTitular.setVisible(false);
        lblNumero.setVisible(false);
        lblVencimiento.setVisible(false);
        lblCvv.setVisible(false);
        btnVolver.setVisible(false);

        lblAvisoBilletera.setFont(Tema.fuente(Font.BOLD, 15));
        lblAvisoBilletera.setForeground(Tema.TEXTO_SUAVE);

        btnTarjeta.setFont(Tema.fuente(Font.BOLD, 16));
        btnTarjeta.setPreferredSize(new Dimension(280, 38));
        btnBilletera.setFont(Tema.fuente(Font.BOLD, 16));
        btnBilletera.setPreferredSize(new Dimension(280, 38));
        Tema.botonFigma(btnContinuar, Tema.VINO_BRILLANTE, 14);
        btnContinuar.setFont(Tema.fuente(Font.BOLD, 16));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setPreferredSize(new Dimension(280, 38));

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
        seleccionarMetodo(TipoMetodoPago.TARJETA);
    }

    private void conectarEventos() {
        btnTarjeta.addActionListener(e -> seleccionarMetodo(TipoMetodoPago.TARJETA));
        btnBilletera.addActionListener(e -> seleccionarMetodo(TipoMetodoPago.APPLE_GOOGLE_PAY));
        btnNoEresTu.addActionListener(e -> coordinador.cerrarSesion());
        btnVolver.addActionListener(e -> coordinador.cuenta().abrirMembresia());
        btnContinuar.addActionListener(e -> continuar());
    }

    /** Marca el metodo elegido; con billetera se ocultan los campos de tarjeta. */
    private void seleccionarMetodo(TipoMetodoPago elegido) {
        metodo = elegido;
        boolean tarjeta = elegido == TipoMetodoPago.TARJETA;
        Tema.botonFigma(btnTarjeta, tarjeta ? new Color(0x521417) : Tema.VINO_METODO, 14);
        Tema.botonFigma(btnBilletera, !tarjeta ? new Color(0x521417) : Tema.VINO_METODO, 14);
        btnTarjeta.setFont(Tema.fuente(Font.BOLD, 16));
        btnBilletera.setFont(Tema.fuente(Font.BOLD, 16));
        btnTarjeta.setForeground(Color.WHITE);
        btnBilletera.setForeground(Color.WHITE);

        pnlCamposTarjeta.setVisible(tarjeta);
        lblAvisoBilletera.setVisible(!tarjeta);
        pnlPago.revalidate();
        pnlPago.repaint();
    }

    /** Lee el pago (sin puntos) y pide al coordinador contratar Pro. */
    private void continuar() {
        mostrarError(null);
        try {
            DatosPagoDTO datos = leerDatosPago();
            coordinador.cuenta().contratarPro(this, datos);
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Arma el DatosPagoDTO con el metodo elegido y usarPuntos = false (la membresia
     * no se paga con puntos). Con tarjeta solo revisa que no haya campos vacios;
     * el formato lo valida la estrategia de pago.
     * TODO: Reemplazar por panelPago.leerDatos(false) cuando se integre Dev 3
     */
    private DatosPagoDTO leerDatosPago() throws Exception {
        if (metodo != TipoMetodoPago.TARJETA) {
            return new DatosPagoDTO(metodo, false, null);
        }
        boolean faltaTitular = vacio(txtTitular);
        boolean faltaNumero = vacio(txtNumero);
        boolean faltaVencimiento = vacio(txtVencimiento);
        boolean faltaCvv = pwdCvv.getPassword().length == 0;
        Tema.marcarError(txtTitular, faltaTitular);
        Tema.marcarError(txtNumero, faltaNumero);
        Tema.marcarError(txtVencimiento, faltaVencimiento);
        Tema.marcarError(pwdCvv, faltaCvv);
        if (faltaTitular || faltaNumero || faltaVencimiento || faltaCvv) {
            throw new Exception("Completa los datos de la tarjeta.");
        }
        char[] cvv = pwdCvv.getPassword();
        DatosTarjetaDTO tarjeta = new DatosTarjetaDTO(
                txtTitular.getText().strip(),
                txtNumero.getText().replace(" ", ""),
                txtVencimiento.getText().strip(),
                new String(cvv));
        Arrays.fill(cvv, '\0');
        return new DatosPagoDTO(TipoMetodoPago.TARJETA, false, tarjeta);
    }

    private static boolean vacio(JTextField campo) {
        return campo.getText() == null || campo.getText().isBlank();
    }

    private static String precio(double valor) {
        return valor == Math.floor(valor)
                ? String.format("%.0f", valor)
                : String.format("%.2f", valor);
    }

    /** Borra el numero y el CVV de la pantalla. */
    public void limpiarTarjeta() {
        txtNumero.setText("");
        pwdCvv.setText("");
    }

    /** Deshabilita los botones y muestra "Procesando pago…" mientras corre la tarea. */
    public void ocupado(boolean ocupado) {
        btnContinuar.setEnabled(!ocupado);
        btnVolver.setEnabled(!ocupado);
        btnTarjeta.setEnabled(!ocupado);
        btnBilletera.setEnabled(!ocupado);
        btnNoEresTu.setEnabled(!ocupado);
        lblEstado.setText(ocupado ? "Procesando pago…" : "");
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

    /** Pildora vino "TOTAL - $129 AL MES" del centro de la tarjeta. */
    private static final class PildoraTotal extends JPanel {

        PildoraTotal() {
            super(new BorderLayout());
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.VINO_TOTAL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
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
        pnlTarjetaCompra = new javax.swing.JPanel();
        pnlEncabezado = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtituloDetalles = new javax.swing.JLabel();
        lblDetalle = new javax.swing.JLabel();
        pnlFilaTotal = new javax.swing.JPanel();
        pnlPildoraTotal = new PildoraTotal();
        lblTotal = new javax.swing.JLabel();
        pnlPago = new javax.swing.JPanel();
        pnlUsuario = new javax.swing.JPanel();
        lblUsuario = new javax.swing.JLabel();
        btnNoEresTu = new javax.swing.JButton();
        pnlCuerpoPago = new javax.swing.JPanel();
        pnlIzquierdaPago = new javax.swing.JPanel();
        pnlCamposTarjeta = new javax.swing.JPanel();
        lblTitular = new javax.swing.JLabel();
        txtTitular = new javax.swing.JTextField();
        lblVencimiento = new javax.swing.JLabel();
        txtVencimiento = new javax.swing.JTextField();
        lblNumero = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        lblCvv = new javax.swing.JLabel();
        pwdCvv = new javax.swing.JPasswordField();
        lblAvisoBilletera = new javax.swing.JLabel();
        pnlMensajes = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();
        pnlMetodos = new javax.swing.JPanel();
        btnTarjeta = new javax.swing.JButton();
        btnBilletera = new javax.swing.JButton();
        btnContinuar = new javax.swing.JButton();
        btnVolver = new javax.swing.JButton();
        pnlInferior = new javax.swing.JPanel();
        pnlBotones = new javax.swing.JPanel();
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

        pnlTarjetaCompra.setLayout(new java.awt.BorderLayout(0, 12));

        pnlEncabezado.setOpaque(false);
        pnlEncabezado.setLayout(new java.awt.GridBagLayout());

        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setText("¡Estás muy cerca de ser Pro!");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 14, 0);
        pnlEncabezado.add(lblTitulo, gbc);

        lblSubtituloDetalles.setText("Detalles de Compra:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 8, 8);
        pnlEncabezado.add(lblSubtituloDetalles, gbc);

        lblDetalle.setHorizontalAlignment(SwingConstants.CENTER);
        lblDetalle.setText("Membresía CinePLUS Pro Mensual             - $129");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);
        pnlEncabezado.add(lblDetalle, gbc);

        pnlFilaTotal.setOpaque(false);
        pnlFilaTotal.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotal.setText("TOTAL - $129 AL MES");
        pnlPildoraTotal.add(lblTotal, java.awt.BorderLayout.CENTER);

        pnlFilaTotal.add(pnlPildoraTotal);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        pnlEncabezado.add(pnlFilaTotal, gbc);

        pnlTarjetaCompra.add(pnlEncabezado, java.awt.BorderLayout.NORTH);

        pnlPago.setLayout(new java.awt.BorderLayout(0, 8));

        pnlUsuario.setOpaque(false);
        pnlUsuario.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));

        lblUsuario.setText("Yazid Revilla - Miembro");
        pnlUsuario.add(lblUsuario);

        btnNoEresTu.setText("¿No eres tú?");
        pnlUsuario.add(btnNoEresTu);

        pnlPago.add(pnlUsuario, java.awt.BorderLayout.NORTH);

        pnlCuerpoPago.setOpaque(false);
        pnlCuerpoPago.setLayout(new java.awt.GridBagLayout());

        pnlIzquierdaPago.setOpaque(false);
        pnlIzquierdaPago.setLayout(new java.awt.BorderLayout(0, 6));

        pnlCamposTarjeta.setOpaque(false);
        pnlCamposTarjeta.setLayout(new java.awt.GridBagLayout());

        lblTitular.setText("NOMBRE DEL TITULAR");
        pnlCamposTarjeta.add(lblTitular);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 14, 14);
        pnlCamposTarjeta.add(txtTitular, gbc);

        lblVencimiento.setText("MM/YY");
        pnlCamposTarjeta.add(lblVencimiento);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 14, 0);
        pnlCamposTarjeta.add(txtVencimiento, gbc);

        lblNumero.setText("NÚMERO DE TARJETA");
        pnlCamposTarjeta.add(lblNumero);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 14);
        pnlCamposTarjeta.add(txtNumero, gbc);

        lblCvv.setText("CVV");
        pnlCamposTarjeta.add(lblCvv);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        pnlCamposTarjeta.add(pwdCvv, gbc);

        pnlIzquierdaPago.add(pnlCamposTarjeta, java.awt.BorderLayout.CENTER);

        lblAvisoBilletera.setText("Se cobrará con Apple / Google Pay al presionar CONTINUAR.");
        pnlIzquierdaPago.add(lblAvisoBilletera, java.awt.BorderLayout.NORTH);

        pnlMensajes.setOpaque(false);
        pnlMensajes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        lblEstado.setText("");
        pnlMensajes.add(lblEstado);

        lblError.setText("");
        pnlMensajes.add(lblError);

        pnlIzquierdaPago.add(pnlMensajes, java.awt.BorderLayout.SOUTH);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.62;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 4, 0, 28);
        pnlCuerpoPago.add(pnlIzquierdaPago, gbc);

        pnlMetodos.setOpaque(false);
        pnlMetodos.setLayout(new java.awt.GridLayout(3, 1, 0, 10));

        btnTarjeta.setText("TARJETA");
        pnlMetodos.add(btnTarjeta);

        btnBilletera.setText("APPLE / GOOGLE PAY");
        pnlMetodos.add(btnBilletera);

        btnContinuar.setText("CONTINUAR");
        pnlMetodos.add(btnContinuar);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.38;
        gbc.anchor = GridBagConstraints.CENTER;
        pnlCuerpoPago.add(pnlMetodos, gbc);

        pnlPago.add(pnlCuerpoPago, java.awt.BorderLayout.CENTER);

        pnlTarjetaCompra.add(pnlPago, java.awt.BorderLayout.SOUTH);

        pnlContenido.add(pnlTarjetaCompra, java.awt.BorderLayout.CENTER);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlInferior.setOpaque(false);
        pnlInferior.setLayout(new java.awt.BorderLayout());

        pnlBotones.setOpaque(false);
        pnlBotones.setVisible(false);
        btnVolver.setText("VOLVER");
        pnlBotones.add(btnVolver);
        pnlInferior.add(pnlBotones, java.awt.BorderLayout.NORTH);

        pnlNavegacion.setOpaque(false);
        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlInferior.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlInferior, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBilletera;
    private javax.swing.JButton btnContinuar;
    private javax.swing.JButton btnNoEresTu;
    private javax.swing.JButton btnTarjeta;
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel lblAvisoBilletera;
    private javax.swing.JLabel lblCvv;
    private javax.swing.JLabel lblDetalle;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblSubtituloDetalles;
    private javax.swing.JLabel lblTitular;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JLabel lblVencimiento;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlCamposTarjeta;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlCuerpoPago;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlFilaTotal;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlInferior;
    private javax.swing.JPanel pnlIzquierdaPago;
    private javax.swing.JPanel pnlMensajes;
    private javax.swing.JPanel pnlMetodos;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPago;
    private javax.swing.JPanel pnlPildoraTotal;
    private javax.swing.JPanel pnlTarjetaCompra;
    private javax.swing.JPanel pnlUsuario;
    private javax.swing.JPasswordField pwdCvv;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtTitular;
    private javax.swing.JTextField txtVencimiento;
    // End of variables declaration//GEN-END:variables
}
