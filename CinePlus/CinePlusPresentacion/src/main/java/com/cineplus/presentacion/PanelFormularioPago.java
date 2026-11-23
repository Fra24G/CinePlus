package com.cineplus.presentacion;

import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.DatosTarjetaDTO;
import com.cineplus.dto.TipoMetodoPago;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * formulario de pago que se reutiliza en boletos y membresia
 *
 * @author yakor
 */
public class PanelFormularioPago extends JPanel {

    private static final Color METODO_ELEGIDO = new Color(0x521417);

    private final JTextField txtTitular = new JTextField();
    private final JTextField txtNumero = new JTextField();
    private final JTextField txtVencimiento = new JTextField();
    private final JPasswordField txtCvv = new JPasswordField();
    private final JButton btnTarjeta = new JButton("TARJETA");
    private final JButton btnBilletera = new JButton("APPLE o GOOGLE PAY");
    private final JPanel pnlCampos = new JPanel(new GridBagLayout());
    private final JLabel lblAvisoBilletera = new JLabel("Se cobrará con Apple o Google Pay al presionar CONTINUAR.");

    private TipoMetodoPago metodo = TipoMetodoPago.TARJETA;

    /**
     * Botones de TARJETA y APPLE o GOOGLE PAY con los campos de la tarjeta.
     */
    public PanelFormularioPago() {
        super(new GridBagLayout());
        setOpaque(false);

        Tema.campoFigma(txtTitular, "NOMBRE DEL TITULAR", 12, 42);
        Tema.campoFigma(txtNumero, "NÚMERO DE TARJETA", 12, 42);
        Tema.campoFigma(txtVencimiento, "MM/YY", 12, 42);
        Tema.campoFigma(txtCvv, "CVV", 12, 42);
        campoCorto(txtVencimiento);
        campoCorto(txtCvv);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 14, 14);
        pnlCampos.add(txtTitular, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 14, 0);
        pnlCampos.add(txtVencimiento, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 14);
        pnlCampos.add(txtNumero, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlCampos.add(txtCvv, gbc);
        pnlCampos.setOpaque(false);

        lblAvisoBilletera.setFont(Tema.fuente(Font.BOLD, 15));
        lblAvisoBilletera.setForeground(Tema.TEXTO_SUAVE);

        JPanel pnlIzquierda = new JPanel(new BorderLayout(0, 6));
        pnlIzquierda.setOpaque(false);
        pnlIzquierda.add(lblAvisoBilletera, BorderLayout.NORTH);
        pnlIzquierda.add(pnlCampos, BorderLayout.CENTER);

        JPanel pnlMetodos = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlMetodos.setOpaque(false);
        for (JButton boton : new JButton[]{btnTarjeta, btnBilletera}) {
            boton.setPreferredSize(new Dimension(280, 38));
            pnlMetodos.add(boton);
        }

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 0.62;
        gbc.insets = new Insets(0, 4, 0, 28);
        add(pnlIzquierda, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.38;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(pnlMetodos, gbc);

        btnTarjeta.addActionListener(e -> seleccionarMetodo(TipoMetodoPago.TARJETA));
        btnBilletera.addActionListener(e -> seleccionarMetodo(TipoMetodoPago.APPLE_GOOGLE_PAY));
        seleccionarMetodo(TipoMetodoPago.TARJETA);
    }

    /**
     * arma los datos de pago con el metodo elegido, el formato lo revisa PagoTarjeta
     */
    public DatosPagoDTO leerDatos(boolean usarPuntos) throws Exception {
        if (metodo != TipoMetodoPago.TARJETA) {
            return new DatosPagoDTO(metodo, usarPuntos, null);
        }
        boolean faltaTitular = txtTitular.getText().isBlank();
        boolean faltaNumero = txtNumero.getText().isBlank();
        boolean faltaVencimiento = txtVencimiento.getText().isBlank();
        boolean faltaCvv = txtCvv.getPassword().length == 0;
        Tema.marcarError(txtTitular, faltaTitular);
        Tema.marcarError(txtNumero, faltaNumero);
        Tema.marcarError(txtVencimiento, faltaVencimiento);
        Tema.marcarError(txtCvv, faltaCvv);
        if (faltaTitular || faltaNumero || faltaVencimiento || faltaCvv) {
            throw new Exception("Completa los datos de la tarjeta.");
        }

        char[] cvv = txtCvv.getPassword();
        DatosTarjetaDTO tarjeta = new DatosTarjetaDTO(
                txtTitular.getText().strip(),
                txtNumero.getText().strip(),
                txtVencimiento.getText().strip(),
                new String(cvv));
        Arrays.fill(cvv, '\0');
        return new DatosPagoDTO(TipoMetodoPago.TARJETA, usarPuntos, tarjeta);
    }

    /**
     * Borra el numero y el CVV de la pantalla.
     */
    public void limpiar() {
        txtNumero.setText("");
        txtCvv.setText("");
    }

    @Override
    public void setEnabled(boolean habilitado) {
        super.setEnabled(habilitado);
        btnTarjeta.setEnabled(habilitado);
        btnBilletera.setEnabled(habilitado);
        txtTitular.setEnabled(habilitado);
        txtNumero.setEnabled(habilitado);
        txtVencimiento.setEnabled(habilitado);
        txtCvv.setEnabled(habilitado);
    }

    private void seleccionarMetodo(TipoMetodoPago elegido) {
        metodo = elegido;
        boolean tarjeta = elegido == TipoMetodoPago.TARJETA;
        estiloMetodo(btnTarjeta, tarjeta);
        estiloMetodo(btnBilletera, !tarjeta);
        pnlCampos.setVisible(tarjeta);
        lblAvisoBilletera.setVisible(!tarjeta);
        revalidate();
        repaint();
    }

    private static void estiloMetodo(JButton boton, boolean elegido) {
        Tema.botonFigma(boton, elegido ? METODO_ELEGIDO : Tema.VINO_METODO, 14);
        boton.setFont(Tema.fuente(Font.BOLD, 16));
        boton.setForeground(Color.WHITE);
    }

    private static void campoCorto(JTextField campo) {
        campo.setHorizontalAlignment(SwingConstants.CENTER);
        campo.setPreferredSize(new Dimension(108, 42));
        campo.setMinimumSize(new Dimension(108, 42));
    }
}
