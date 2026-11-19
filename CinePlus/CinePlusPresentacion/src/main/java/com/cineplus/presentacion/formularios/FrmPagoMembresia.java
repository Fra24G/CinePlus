package com.cineplus.presentacion.formularios;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.DatosTarjetaDTO;
import com.cineplus.dto.PlanDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.dto.TipoMetodoPago;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Pantalla "Pago de Membresia".
 * <p>
 * TODO: Reemplazar pnlPago (botones de metodo y campos de tarjeta) por PanelFormularioPago
 * cuando se integre Dev 3. Mientras tanto este formulario arma el DatosPagoDTO por su cuenta
 * y el cobro responde "Pendiente: FabricaPago.para" en la etiqueta de error.
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
                ? "Comprando como: invitado"
                : "Comprando como: " + sesion.usuario().nombre() + " (" + sesion.usuario().correo() + ")");
        if (plan != null) {
            lblDetalle.setText("Detalles de compra: Membresía " + plan.nombre()
                    + " Mensual - $" + precio(plan.precioMensual()));
        }
        conectarEventos();
    }

    /** Estilos con Tema y propiedades de FlatLaf. */
    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Pago de membresía");
        Tema.titulo(lblTitulo);
        lblUsuario.setForeground(Tema.MUTED);
        lblDetalle.setForeground(Tema.TEXTO);
        btnNoEresTu.putClientProperty("JButton.buttonType", "borderless");

        // TODO: Reemplazar por PanelFormularioPago cuando se integre Dev 3
        Tema.tarjeta(pnlPago);
        // Los paneles internos dejan ver el fondo de la tarjeta
        pnlMetodos.setOpaque(false);
        pnlCamposTarjeta.setOpaque(false);
        for (JLabel etiqueta : new JLabel[]{lblTitular, lblNumero, lblVencimiento, lblCvv}) {
            etiqueta.setForeground(Tema.MUTED);
        }
        Tema.campo(txtTitular, "Como aparece en la tarjeta");
        Tema.campo(txtNumero, "1234 5678 9012 3456");
        Tema.campo(txtVencimiento, "MM/YY");
        Tema.campo(pwdCvv, "123");
        lblAvisoBilletera.setForeground(Tema.MUTED);

        Tema.botonPrimario(btnContinuar);
        Tema.botonSecundario(btnVolver);
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
        if (tarjeta) {
            Tema.botonPrimario(btnTarjeta);
            Tema.botonSecundario(btnBilletera);
        } else {
            Tema.botonPrimario(btnBilletera);
            Tema.botonSecundario(btnTarjeta);
        }
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

    /** Deshabilita los botones y muestra "Cargando…" mientras corre la tarea. */
    public void ocupado(boolean ocupado) {
        btnContinuar.setEnabled(!ocupado);
        btnVolver.setEnabled(!ocupado);
        btnTarjeta.setEnabled(!ocupado);
        btnBilletera.setEnabled(!ocupado);
        btnNoEresTu.setEnabled(!ocupado);
        lblEstado.setText(ocupado ? "Procesando pago…" : " ");
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
        pnlEncabezado = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlUsuario = new javax.swing.JPanel();
        lblUsuario = new javax.swing.JLabel();
        btnNoEresTu = new javax.swing.JButton();
        lblDetalle = new javax.swing.JLabel();
        pnlContenido = new javax.swing.JPanel();
        pnlPago = new javax.swing.JPanel();
        pnlMetodos = new javax.swing.JPanel();
        btnTarjeta = new javax.swing.JButton();
        btnBilletera = new javax.swing.JButton();
        pnlCamposTarjeta = new javax.swing.JPanel();
        lblTitular = new javax.swing.JLabel();
        txtTitular = new javax.swing.JTextField();
        lblNumero = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        lblVencimiento = new javax.swing.JLabel();
        txtVencimiento = new javax.swing.JTextField();
        lblCvv = new javax.swing.JLabel();
        pwdCvv = new javax.swing.JPasswordField();
        lblAvisoBilletera = new javax.swing.JLabel();
        pnlInferior = new javax.swing.JPanel();
        pnlMensajes = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();
        pnlBotones = new javax.swing.JPanel();
        btnVolver = new javax.swing.JButton();
        btnContinuar = new javax.swing.JButton();
        pnlNavegacion = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        pnlEncabezado.setLayout(new java.awt.BorderLayout(0, 6));

        lblTitulo.setText("¡Estás muy cerca de ser Pro!");
        pnlEncabezado.add(lblTitulo, java.awt.BorderLayout.NORTH);
        pnlUsuario.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        lblUsuario.setText("Comprando como:");
        pnlUsuario.add(lblUsuario);
        btnNoEresTu.setText("¿No eres tú?");
        pnlUsuario.add(btnNoEresTu);
        pnlEncabezado.add(pnlUsuario, java.awt.BorderLayout.CENTER);

        lblDetalle.setText("Detalles de compra: Membresía CinePLUS Pro Mensual - $129");
        pnlEncabezado.add(lblDetalle, java.awt.BorderLayout.SOUTH);
        pnlFondo.add(pnlEncabezado, java.awt.BorderLayout.NORTH);

        pnlContenido.setLayout(new java.awt.BorderLayout());

        pnlPago.setLayout(new java.awt.BorderLayout(0, 12));

        pnlMetodos.setLayout(new java.awt.GridLayout(1, 2, 12, 0));

        btnTarjeta.setText("TARJETA");
        pnlMetodos.add(btnTarjeta);
        btnBilletera.setText("APPLE / GOOGLE PAY");
        pnlMetodos.add(btnBilletera);
        pnlPago.add(pnlMetodos, java.awt.BorderLayout.NORTH);

        pnlCamposTarjeta.setLayout(new java.awt.GridLayout(4, 2, 12, 10));

        lblTitular.setText("NOMBRE DEL TITULAR");
        pnlCamposTarjeta.add(lblTitular);
        pnlCamposTarjeta.add(txtTitular);
        lblNumero.setText("NÚMERO DE TARJETA");
        pnlCamposTarjeta.add(lblNumero);
        pnlCamposTarjeta.add(txtNumero);
        lblVencimiento.setText("MM/YY");
        pnlCamposTarjeta.add(lblVencimiento);
        pnlCamposTarjeta.add(txtVencimiento);
        lblCvv.setText("CVV");
        pnlCamposTarjeta.add(lblCvv);
        pnlCamposTarjeta.add(pwdCvv);
        pnlPago.add(pnlCamposTarjeta, java.awt.BorderLayout.CENTER);

        lblAvisoBilletera.setText("Se cobrará con Apple / Google Pay al presionar CONTINUAR.");
        pnlPago.add(lblAvisoBilletera, java.awt.BorderLayout.SOUTH);
        pnlContenido.add(pnlPago, java.awt.BorderLayout.NORTH);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlInferior.setLayout(new java.awt.BorderLayout(0, 8));

        pnlMensajes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));

        lblEstado.setText(" ");
        pnlMensajes.add(lblEstado);
        lblError.setText(" ");
        pnlMensajes.add(lblError);
        pnlInferior.add(pnlMensajes, java.awt.BorderLayout.NORTH);

        pnlBotones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 12, 0));

        btnVolver.setText("VOLVER");
        pnlBotones.add(btnVolver);
        btnContinuar.setText("CONTINUAR");
        pnlBotones.add(btnContinuar);
        pnlInferior.add(pnlBotones, java.awt.BorderLayout.CENTER);

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
    private javax.swing.JLabel lblTitular;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JLabel lblVencimiento;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlCamposTarjeta;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlInferior;
    private javax.swing.JPanel pnlMensajes;
    private javax.swing.JPanel pnlMetodos;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPago;
    private javax.swing.JPanel pnlUsuario;
    private javax.swing.JPasswordField pwdCvv;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtTitular;
    private javax.swing.JTextField txtVencimiento;
    // End of variables declaration//GEN-END:variables
}
