package com.cineplus.presentacion.formularios;

import com.cineplus.dto.BoletoDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.FormatoCompra;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Pantalla "Detalles compra" (F11): pelicula, funcion, asientos, cine y sala,
 * desglose del cobro, fecha de compra y metodo de pago. El boton Cancelar solo se
 * ve si la compra es cancelable; Facturar, Enviar al correo y ¿Necesitas ayuda?
 * son solo visuales.
 *
 * @author Mario Perez
 */
public class FrmDetalleCompra extends JFrame {

    private CoordinadorAplicacion coordinador;
    private Integer idCompra;
    private CompraDTO compra;

    private final JPanel pnlFuncion = new JPanel();
    private final JPanel pnlDesglose = new JPanel();
    private final JButton btnVolver = new JButton("Volver");
    private final JButton btnFacturar = new JButton("Facturar");
    private final JButton btnCorreo = new JButton("Enviar al correo");
    private final JButton btnAyuda = new JButton("¿Necesitas ayuda?");
    private final JButton btnCancelar = new JButton("Cancelar compra");

    /** Constructor para el editor visual de NetBeans. */
    public FrmDetalleCompra() {
        initComponents();
        configurarVista();
    }

    /** Arma la vista y pide la compra al coordinador. */
    public FrmDetalleCompra(CoordinadorAplicacion coordinador, Integer idCompra) {
        this();
        this.coordinador = coordinador;
        this.idCompra = idCompra;
        pnlNavegacion.add(new PanelNavegacion(coordinador, Seccion.HISTORIAL), BorderLayout.CENTER);
        conectarEventos();
        coordinador.boletos().cargarCompra(this, idCompra);
    }

    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Detalles de compra");
        pnlCabecera.add(new PanelEncabezado(null), BorderLayout.CENTER);
        Tema.titulo(lblTitulo);

        Tema.tarjetaConBorde(pnlFuncion, Tema.TARJETA_MORADA, Tema.BORDE_TARJETA, 16, new Insets(18, 22, 18, 22));
        Tema.tarjetaConBorde(pnlDesglose, Tema.TARJETA_MORADA, Tema.BORDE_TARJETA, 16, new Insets(18, 22, 18, 22));
        pnlFuncion.setLayout(new BoxLayout(pnlFuncion, BoxLayout.Y_AXIS));
        pnlDesglose.setLayout(new BoxLayout(pnlDesglose, BoxLayout.Y_AXIS));

        JPanel columnas = new JPanel(new GridLayout(1, 2, 18, 0));
        columnas.setOpaque(false);
        columnas.add(pnlFuncion);
        columnas.add(pnlDesglose);
        pnlCuerpo.add(columnas, BorderLayout.CENTER);

        Tema.botonSecundario(btnVolver);
        for (JButton visual : new JButton[]{btnFacturar, btnCorreo, btnAyuda}) {
            Tema.botonSecundario(visual);
            visual.setToolTipText("Próximamente");
        }
        Tema.botonFigma(btnCancelar, Tema.VINO_BRILLANTE, 14);
        btnCancelar.setFont(btnCancelar.getFont().deriveFont(Font.BOLD));
        btnCancelar.setVisible(false);

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izquierda.setOpaque(false);
        izquierda.add(btnVolver);
        izquierda.add(btnFacturar);
        izquierda.add(btnCorreo);
        izquierda.add(btnAyuda);
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(btnCancelar);
        pnlBotones.add(izquierda, BorderLayout.CENTER);
        pnlBotones.add(derecha, BorderLayout.EAST);

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
    }

    private void conectarEventos() {
        btnVolver.addActionListener(e -> coordinador.navegar(Seccion.HISTORIAL));
        for (JButton visual : new JButton[]{btnFacturar, btnCorreo, btnAyuda}) {
            visual.addActionListener(e -> lblEstado.setText(visual.getText() + ": próximamente."));
        }
        btnCancelar.addActionListener(e -> {
            if (compra != null) {
                coordinador.boletos().pedirCancelacion(this, compra);
            }
        });
    }

    /** Muestra la compra. El boton Cancelar solo aparece si compra.cancelable(). */
    public void mostrarCompra(CompraDTO compra) {
        this.compra = compra;
        lblTitulo.setText("Detalles compra " + FormatoCompra.numeroCompra(compra.idCompra()));

        pnlFuncion.removeAll();
        agregar(pnlFuncion, compra.tituloPelicula(), Font.BOLD, 26, Tema.TEXTO);
        agregar(pnlFuncion, FormatoCompra.fechaLarga(compra.fechaHoraFuncion())
                + " · " + FormatoCompra.hora(compra.fechaHoraFuncion()), Font.BOLD, 17, Tema.TEXTO_SUAVE);
        agregar(pnlFuncion, compra.tipoFuncion(), Font.PLAIN, 15, Tema.MUTED);
        pnlFuncion.add(Box.createVerticalStrut(14));
        agregar(pnlFuncion, "Asientos: " + asientos(compra), Font.BOLD, 17, Tema.TEXTO);
        agregar(pnlFuncion, compra.cine() + " SALA " + compra.numeroSala(), Font.PLAIN, 15, Tema.MUTED);
        if (!compra.productos().isEmpty()) {
            pnlFuncion.add(Box.createVerticalStrut(10));
            agregar(pnlFuncion, "Productos: " + String.join(", ", compra.productos()), Font.PLAIN, 14, Tema.MUTED);
        }
        pnlFuncion.add(Box.createVerticalGlue());
        agregar(pnlFuncion, "Estado: " + compra.estado(), Font.BOLD, 15, colorEstado(compra.estado()));
        if (compra.motivoCancelacion() != null && !compra.motivoCancelacion().isBlank()) {
            agregar(pnlFuncion, "<html>Motivo: " + Tema.escapar(compra.motivoCancelacion()) + "</html>",
                    Font.PLAIN, 14, Tema.MUTED);
        }

        pnlDesglose.removeAll();
        agregar(pnlDesglose, "Desglose", Font.BOLD, 20, Tema.TEXTO);
        pnlDesglose.add(Box.createVerticalStrut(10));
        fila(pnlDesglose, "Boletos (" + compra.boletos().size() + ")", FormatoCompra.dinero(compra.subtotal()));
        fila(pnlDesglose, "Cargo por servicio", FormatoCompra.dinero(compra.cargoServicio()));
        if (compra.puntosUsados() > 0) {
            fila(pnlDesglose, "Puntos usados (" + compra.puntosUsados() + ")",
                    "-" + FormatoCompra.dinero(compra.descuentoPuntos()));
        }
        fila(pnlDesglose, "Total", FormatoCompra.dinero(compra.montoCobrado()));
        if (compra.puntosGanados() > 0) {
            fila(pnlDesglose, "Puntos ganados", "+" + compra.puntosGanados());
        }
        pnlDesglose.add(Box.createVerticalStrut(14));
        agregar(pnlDesglose, "Compra: " + FormatoCompra.fechaHora(compra.fechaCompra()), Font.PLAIN, 14, Tema.MUTED);
        agregar(pnlDesglose, textoPago(compra), Font.PLAIN, 14, Tema.MUTED);

        btnCancelar.setVisible(compra.cancelable());
        pnlCuerpo.revalidate();
        pnlCuerpo.repaint();
    }

    /** "B7, B8, B9". */
    private static String asientos(CompraDTO compra) {
        List<String> lista = new ArrayList<>();
        for (BoletoDTO boleto : compra.boletos()) {
            lista.add(boleto.asiento());
        }
        return lista.isEmpty() ? "—" : String.join(", ", lista);
    }

    /** "Pago con Tarjeta terminación ****2928" o "Pago con Puntos". */
    private static String textoPago(CompraDTO compra) {
        String texto = "Pago con " + (compra.metodoPago() == null || compra.metodoPago().isBlank()
                ? "—" : compra.metodoPago());
        if (compra.ultimosDigitos() != null && !compra.ultimosDigitos().isBlank()) {
            texto += " terminación ****" + compra.ultimosDigitos();
        }
        return texto;
    }

    private static Color colorEstado(String estado) {
        return "Denegado".equals(estado) ? Tema.ERROR : Tema.TEXTO_SUAVE;
    }

    private static void agregar(JPanel panel, String texto, int estilo, int tamano, Color color) {
        JLabel etiqueta = new JLabel(texto == null ? "" : texto);
        etiqueta.setFont(Tema.fuente(estilo, tamano));
        etiqueta.setForeground(color);
        etiqueta.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        panel.add(etiqueta);
    }

    /** Renglon del desglose: concepto a la izquierda y monto a la derecha. */
    private static void fila(JPanel panel, String concepto, String monto) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        fila.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
        JLabel lblConcepto = new JLabel(concepto);
        lblConcepto.setFont(Tema.fuente(Font.PLAIN, 16));
        lblConcepto.setForeground(Tema.TEXTO_SUAVE);
        JLabel lblMonto = new JLabel(monto);
        lblMonto.setFont(Tema.fuente(Font.BOLD, 16));
        lblMonto.setForeground(Tema.TEXTO);
        fila.add(lblConcepto, BorderLayout.WEST);
        fila.add(lblMonto, BorderLayout.EAST);
        panel.add(fila);
    }

    /** Deshabilita botones y muestra "Cargando…" mientras corre la tarea. */
    public void ocupado(boolean ocupado) {
        btnVolver.setEnabled(!ocupado);
        btnCancelar.setEnabled(!ocupado);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        pnlCabecera = new javax.swing.JPanel();
        pnlContenido = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlCuerpo = new javax.swing.JPanel();
        pnlPie = new javax.swing.JPanel();
        lblError = new javax.swing.JLabel();
        pnlBotones = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();
        pnlNavegacion = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        pnlCabecera.setOpaque(false);
        pnlCabecera.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlCabecera, java.awt.BorderLayout.NORTH);

        pnlContenido.setOpaque(false);
        pnlContenido.setLayout(new java.awt.BorderLayout(12, 12));

        lblTitulo.setText("Detalles compra");
        pnlContenido.add(lblTitulo, java.awt.BorderLayout.NORTH);

        pnlCuerpo.setOpaque(false);
        pnlCuerpo.setLayout(new java.awt.BorderLayout());
        pnlContenido.add(pnlCuerpo, java.awt.BorderLayout.CENTER);

        pnlPie.setOpaque(false);
        pnlPie.setLayout(new java.awt.BorderLayout(8, 8));

        lblError.setText("");
        pnlPie.add(lblError, java.awt.BorderLayout.NORTH);

        pnlBotones.setOpaque(false);
        pnlBotones.setLayout(new java.awt.BorderLayout());
        pnlPie.add(pnlBotones, java.awt.BorderLayout.CENTER);

        lblEstado.setText("");
        pnlPie.add(lblEstado, java.awt.BorderLayout.SOUTH);

        pnlContenido.add(pnlPie, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlNavegacion.setOpaque(false);
        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlCuerpo;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPie;
    // End of variables declaration//GEN-END:variables
}
