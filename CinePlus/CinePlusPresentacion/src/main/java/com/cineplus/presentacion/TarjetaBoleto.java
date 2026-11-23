package com.cineplus.presentacion;

import com.cineplus.dto.BoletoDTO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Tarjeta de un boleto en "Mis boletos": pelicula, fecha y hora, tipo, asiento,
 * cine y sala, codigo y el enlace "Ver detalles de compra...". A la izquierda va
 * el espacio del codigo QR.
 *
 * @author Mario Perez
 */
public class TarjetaBoleto extends JPanel {

    /** Tamano del QR (igual al PNG que genera BoletoBO). */
    private static final int TAMANO_QR = 220;
    private static final DateTimeFormatter FECHA_LARGA =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.forLanguageTag("es-MX"));
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final BoletoDTO boleto;
    private final CoordinadorAplicacion coordinador;
    private final JLabel lblQR = new JLabel("", SwingConstants.CENTER);

    public TarjetaBoleto(BoletoDTO boleto, CoordinadorAplicacion coordinador) {
        super(new BorderLayout(18, 0));
        this.boleto = boleto;
        this.coordinador = coordinador;
        Tema.tarjetaConBorde(this, Tema.TARJETA_MORADA, Tema.BORDE_TARJETA, 16, new Insets(16, 16, 16, 20));
        add(crearZonaQR(), BorderLayout.WEST);
        add(crearDatos(), BorderLayout.CENTER);
    }

    /** Recuadro del codigo QR. */
    private JComponent crearZonaQR() {
        lblQR.setPreferredSize(new Dimension(TAMANO_QR, TAMANO_QR));
        lblQR.setMinimumSize(new Dimension(TAMANO_QR, TAMANO_QR));
        lblQR.setOpaque(true);
        lblQR.setBackground(Tema.SUBPANEL_PAGO);
        lblQR.setForeground(Tema.MUTED);
        lblQR.setFont(Tema.fuente(Font.BOLD, 13));
        lblQR.setBorder(BorderFactory.createLineBorder(Tema.BORDE_TARJETA));
        lblQR.setText("Código QR");
        return lblQR;
    }

    private JComponent crearDatos() {
        JPanel datos = new JPanel();
        datos.setOpaque(false);
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));

        JLabel lblPelicula = etiqueta(boleto.tituloPelicula(), Font.BOLD, 22, Tema.TEXTO);
        JLabel lblFecha = etiqueta(fechaLarga(boleto.fechaHoraFuncion()), Font.BOLD, 16, Tema.TEXTO_SUAVE);
        JLabel lblTipo = etiqueta(hora(boleto.fechaHoraFuncion()) + " · " + boleto.tipoFuncion(),
                Font.PLAIN, 15, Tema.MUTED);
        JLabel lblAsiento = etiqueta("Asiento " + boleto.asiento(), Font.BOLD, 18, Tema.TEXTO);
        JLabel lblSala = etiqueta(boleto.cine() + " SALA " + boleto.numeroSala(), Font.PLAIN, 15, Tema.MUTED);
        JLabel lblCodigo = etiqueta(boleto.codigo(), Font.PLAIN, 13, Tema.PLACEHOLDER);

        JButton btnDetalles = new JButton("Ver detalles de compra...");
        Tema.enlace(btnDetalles, Tema.ENLACE, true);
        btnDetalles.setFont(Tema.fuente(Font.BOLD, 14));
        btnDetalles.setAlignmentX(LEFT_ALIGNMENT);
        btnDetalles.addActionListener(e -> coordinador.boletos().abrirDetalle(boleto.idCompra()));

        datos.add(lblPelicula);
        datos.add(Box.createVerticalStrut(6));
        datos.add(lblFecha);
        datos.add(lblTipo);
        datos.add(Box.createVerticalStrut(12));
        datos.add(lblAsiento);
        datos.add(lblSala);
        datos.add(Box.createVerticalGlue());
        datos.add(lblCodigo);
        datos.add(btnDetalles);
        return datos;
    }

   
    private static String fechaLarga(LocalDateTime fecha) {
        if (fecha == null) {
            return "";
        }
        String texto = FECHA_LARGA.format(fecha);
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

 
    private static String hora(LocalDateTime fecha) {
        return fecha == null ? "" : HORA.format(fecha);
    }

    private static JLabel etiqueta(String texto, int estilo, int tamano, java.awt.Color color) {
        JLabel etiqueta = new JLabel(texto == null ? "" : texto);
        etiqueta.setFont(Tema.fuente(estilo, tamano));
        etiqueta.setForeground(color);
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);
        return etiqueta;
    }
}
