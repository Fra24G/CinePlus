package com.cineplus.presentacion.formularios;

import com.cineplus.dto.PlanDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla "Seleccion de Membresia" (F13): encabezado con logo CinePLUS centrado y
 * botones de campana y mas opciones; dos tarjetas grandes lado a lado con fondo oscuro
 * #171022 (CinePLUS+ con borde blanco sutil y boton gris deshabilitado "Ya eres miembro",
 * y CinePLUS Pro con borde rojo, precio gigante "$129 AL MES" y boton vino "CONTINUAR");
 * y barra de navegacion inferior con CinePLUS activo.
 *
 * @author Luis Guillermo Franco Murrieta
 */
public class FrmMembresia extends JFrame {

    private CoordinadorAplicacion coordinador;
    private PlanDTO planPro;
    /** Solo se habilita CONTINUAR cuando ya cargaron los planes y el usuario no es Pro. */
    private boolean puedeContratar;

    /** Constructor para el editor visual de NetBeans. */
    public FrmMembresia() {
        initComponents();
        configurarVista();
    }

    /** Arma la vista con la barra de navegacion; los planes los carga CoordinadorCuenta. */
    public FrmMembresia(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        pnlNavegacion.add(new PanelNavegacion(coordinador, Seccion.CINEPLUS), BorderLayout.CENTER);
        btnPro.addActionListener(e -> {
            if (planPro != null) {
                coordinador.cuenta().abrirPagoMembresia(planPro);
            }
        });
    }

    /** Estilos con Tema y propiedades de FlatLaf. */
    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Membresía");

        pnlCabecera.add(PanelEncabezado.centrado(), BorderLayout.CENTER);
        pnlPlanes.setBorder(new EmptyBorder(4, 52, 8, 52));

        Tema.tarjetaConBorde(
                pnlPlanGratis,
                Tema.TARJETA_MORADA,
                new Color(0x8c8692),
                14,
                new Insets(22, 22, 24, 22));
        Tema.tarjetaConBorde(
                pnlPlanPro,
                Tema.TARJETA_MORADA,
                Tema.ROJO_PRO,
                14,
                new Insets(22, 22, 24, 22));

        lblNombreGratis.setFont(Tema.fuente(Font.BOLD, 30));
        lblNombreGratis.setForeground(Color.WHITE);
        lblNombreGratis.setText("<html>CinePLUS<font color='#a8a3ad'>+</font></html>");

        lblPrecioGratis.setFont(Tema.fuente(Font.BOLD, 14));
        lblPrecioGratis.setForeground(new Color(0xe0dae2));
        lblPrecioGratis.setText("¡Ya lo tienes!");

        lblNombrePro.setFont(Tema.fuente(Font.BOLD, 30));
        lblNombrePro.setForeground(Color.WHITE);
        lblNombrePro.setText("<html>CinePLUS <font color='#bc0d0d'>Pro</font></html>");

        lblSubtitulo.setFont(Tema.fuente(Font.BOLD, 14));
        lblSubtitulo.setForeground(new Color(0xe0dae2));
        lblSubtitulo.setText("Todo lo de un usuario CinePLUS+ y además..");

        lblBeneficiosGratis.setFont(Tema.fuente(Font.BOLD, 17));
        lblBeneficiosGratis.setForeground(new Color(0xdad4dc));
        lblBeneficiosGratis.setText(beneficios(
                "Beneficios únicos por registrarte en CinePlus\n"
                + "Encuentra tus boletos de forma sencilla\n"
                + "Acumula puntos en todas tus compras\n"
                + "Accede a nuestra comunidad CinePlus, lee, publica reseñas."));

        lblBeneficiosPro.setFont(Tema.fuente(Font.BOLD, 17));
        lblBeneficiosPro.setForeground(new Color(0xdad4dc));
        lblBeneficiosPro.setText(beneficios(
                "Recibe más puntos por tus compras\n"
                + "Sin comisión por uso del servicio\n"
                + "Preventas anticipadas\n"
                + "Funciones exclusivas\n"
                + "¡Mucho más!"));

        lblPrecioPro.setFont(Tema.fuenteLogo(58));
        lblPrecioPro.setForeground(Color.WHITE);
        lblAlMes.setFont(Tema.fuente(Font.BOLD, 24));
        lblAlMes.setForeground(Color.WHITE);

        Tema.botonFigma(btnGratis, Tema.GRIS_BOTON, 16);
        btnGratis.setFont(Tema.fuente(Font.BOLD, 16));
        btnGratis.setForeground(new Color(0xe2dce0));
        btnGratis.putClientProperty("FlatLaf.style",
                btnGratis.getClientProperty("FlatLaf.style") + "; disabledText: #e2dce0");
        btnGratis.setPreferredSize(new Dimension(256, 40));
        btnGratis.setEnabled(false);

        Tema.botonFigma(btnPro, new Color(0x6e1920), 16);
        btnPro.setFont(Tema.fuente(Font.BOLD, 16));
        btnPro.setForeground(Color.WHITE);
        btnPro.setPreferredSize(new Dimension(256, 40));
        btnPro.setEnabled(false);

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
    }

    /**
     * Dos tarjetas: CinePLUS+ con "Ya eres miembro" y Pro con su precio y CONTINUAR,
     * o "Ya eres miembro" deshabilitado si yaEsPro.
     */
    public void mostrarPlanes(List<PlanDTO> planes, boolean yaEsPro) {
        for (PlanDTO plan : planes) {
            if (plan.gratuito()) {
                lblNombreGratis.setText(formatearTituloPlan(plan.nombre(), false));
                lblPrecioGratis.setText("¡Ya lo tienes!");
                lblBeneficiosGratis.setText(beneficios(plan.beneficios()));
            } else {
                planPro = plan;
                lblNombrePro.setText(formatearTituloPlan(plan.nombre(), true));
                lblPrecioPro.setText("$" + precio(plan.precioMensual()));
                lblAlMes.setText("AL MES");
                lblBeneficiosPro.setText(beneficios(plan.beneficios()));
            }
        }
        btnGratis.setText("Ya eres miembro");
        puedeContratar = planPro != null && !yaEsPro;
        btnPro.setText(yaEsPro ? "Ya eres miembro" : "CONTINUAR");
        if (yaEsPro) {
            Tema.botonFigma(btnPro, Tema.GRIS_BOTON, 16);
            btnPro.putClientProperty("FlatLaf.style",
                    btnPro.getClientProperty("FlatLaf.style") + "; disabledText: #e2dce0");
        } else {
            Tema.botonFigma(btnPro, new Color(0x6e1920), 16);
        }
        btnPro.setFont(Tema.fuente(Font.BOLD, 16));
        btnPro.setEnabled(puedeContratar);
    }

    private static String formatearTituloPlan(String nombre, boolean pro) {
        if (nombre == null) {
            return "";
        }
        String seguro = Tema.escapar(nombre);
        if (pro) {
            return "<html>" + seguro.replaceAll("\\bPro\\b",
                    "<font color='#bc0d0d'>Pro</font>") + "</html>";
        }
        return "<html>" + seguro.replace("+", "<font color='#a8a3ad'>+</font>") + "</html>";
    }

    /** Convierte las lineas de beneficios en la lista de vinetas espaciadas del Figma. */
    private static String beneficios(String texto) {
        if (texto == null || texto.isBlank()) {
            return " ";
        }
        String[] lineas = texto.contains("\n")
                ? texto.split("\\r?\\n")
                : texto.split("\\.\\s+");
        StringBuilder html = new StringBuilder(
                "<html><body style='width: 255px; line-height: 1.25; margin-top: 6px; padding-right: 10px'>");
        for (int i = 0; i < lineas.length; i++) {
            String parte = lineas[i].strip();
            if (!parte.isEmpty()) {
                html.append("<div style='margin-bottom: 14px; text-indent: -14px; margin-left: 14px'>"
                        + "•&nbsp;&nbsp;")
                        .append(Tema.escapar(parte))
                        .append("</div>");
            }
        }
        return html.append("</body></html>").toString();
    }

    private static String precio(double valor) {
        return valor == Math.floor(valor)
                ? String.format("%.0f", valor)
                : String.format("%.2f", valor);
    }

    /** Deshabilita CONTINUAR y muestra "Cargando…" mientras corre la tarea. */
    public void ocupado(boolean ocupado) {
        btnPro.setEnabled(!ocupado && puedeContratar);
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
        GridBagConstraints gbc;

        pnlFondo = new javax.swing.JPanel();
        pnlEncabezado = new javax.swing.JPanel();
        pnlCabecera = new javax.swing.JPanel();
        pnlMensajes = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();
        pnlPlanes = new javax.swing.JPanel();
        pnlPlanGratis = new javax.swing.JPanel();
        pnlGratisEncabezado = new javax.swing.JPanel();
        lblNombreGratis = new javax.swing.JLabel();
        lblPrecioGratis = new javax.swing.JLabel();
        lblBeneficiosGratis = new javax.swing.JLabel();
        pnlGratisPie = new javax.swing.JPanel();
        btnGratis = new javax.swing.JButton();
        pnlPlanPro = new javax.swing.JPanel();
        pnlProEncabezado = new javax.swing.JPanel();
        lblNombrePro = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblBeneficiosPro = new javax.swing.JLabel();
        pnlProPie = new javax.swing.JPanel();
        pnlPrecioPro = new javax.swing.JPanel();
        lblPrecioPro = new javax.swing.JLabel();
        lblAlMes = new javax.swing.JLabel();
        pnlBotonPro = new javax.swing.JPanel();
        btnPro = new javax.swing.JButton();
        pnlNavegacion = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(12, 8));

        pnlEncabezado.setOpaque(false);
        pnlEncabezado.setLayout(new java.awt.BorderLayout(0, 2));

        pnlCabecera.setOpaque(false);
        pnlCabecera.setLayout(new java.awt.BorderLayout());
        pnlEncabezado.add(pnlCabecera, java.awt.BorderLayout.CENTER);

        pnlMensajes.setOpaque(false);
        pnlMensajes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 0));

        lblTitulo.setText("");
        lblTitulo.setVisible(false);
        pnlMensajes.add(lblTitulo);

        lblEstado.setText("");
        pnlMensajes.add(lblEstado);

        lblError.setText("");
        pnlMensajes.add(lblError);

        pnlEncabezado.add(pnlMensajes, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlEncabezado, java.awt.BorderLayout.NORTH);

        pnlPlanes.setOpaque(false);
        pnlPlanes.setLayout(new java.awt.GridLayout(1, 2, 46, 0));

        pnlPlanGratis.setLayout(new java.awt.BorderLayout(0, 18));

        pnlGratisEncabezado.setOpaque(false);
        pnlGratisEncabezado.setLayout(new java.awt.GridBagLayout());

        lblNombreGratis.setHorizontalAlignment(SwingConstants.CENTER);
        lblNombreGratis.setText("CinePLUS+");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        pnlGratisEncabezado.add(lblNombreGratis, gbc);

        lblPrecioGratis.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrecioGratis.setText("¡Ya lo tienes!");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlGratisEncabezado.add(lblPrecioGratis, gbc);

        pnlPlanGratis.add(pnlGratisEncabezado, java.awt.BorderLayout.NORTH);

        lblBeneficiosGratis.setText("Beneficios");
        lblBeneficiosGratis.setVerticalAlignment(SwingConstants.TOP);
        lblBeneficiosGratis.setHorizontalAlignment(SwingConstants.CENTER);
        pnlPlanGratis.add(lblBeneficiosGratis, java.awt.BorderLayout.CENTER);

        pnlGratisPie.setOpaque(false);
        pnlGratisPie.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 4));

        btnGratis.setText("Ya eres miembro");
        pnlGratisPie.add(btnGratis);

        pnlPlanGratis.add(pnlGratisPie, java.awt.BorderLayout.SOUTH);

        pnlPlanes.add(pnlPlanGratis);

        pnlPlanPro.setLayout(new java.awt.BorderLayout(0, 14));

        pnlProEncabezado.setOpaque(false);
        pnlProEncabezado.setLayout(new java.awt.GridBagLayout());

        lblNombrePro.setHorizontalAlignment(SwingConstants.CENTER);
        lblNombrePro.setText("CinePLUS Pro");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 4, 0);
        pnlProEncabezado.add(lblNombrePro, gbc);

        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitulo.setText("Todo lo de un usuario CinePLUS+ y además..");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        pnlProEncabezado.add(lblSubtitulo, gbc);

        pnlPlanPro.add(pnlProEncabezado, java.awt.BorderLayout.NORTH);

        lblBeneficiosPro.setText("Beneficios");
        lblBeneficiosPro.setVerticalAlignment(SwingConstants.TOP);
        lblBeneficiosPro.setHorizontalAlignment(SwingConstants.CENTER);
        pnlPlanPro.add(lblBeneficiosPro, java.awt.BorderLayout.CENTER);

        pnlProPie.setOpaque(false);
        pnlProPie.setLayout(new java.awt.BorderLayout(0, 6));

        pnlPrecioPro.setOpaque(false);
        pnlPrecioPro.setLayout(new java.awt.GridBagLayout());

        lblPrecioPro.setText("$129");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.BASELINE;
        gbc.insets = new Insets(0, 0, 0, 10);
        pnlPrecioPro.add(lblPrecioPro, gbc);

        lblAlMes.setText("AL MES");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.BASELINE;
        pnlPrecioPro.add(lblAlMes, gbc);

        pnlProPie.add(pnlPrecioPro, java.awt.BorderLayout.NORTH);

        pnlBotonPro.setOpaque(false);
        pnlBotonPro.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 4));

        btnPro.setText("CONTINUAR");
        pnlBotonPro.add(btnPro);

        pnlProPie.add(pnlBotonPro, java.awt.BorderLayout.SOUTH);

        pnlPlanPro.add(pnlProPie, java.awt.BorderLayout.SOUTH);

        pnlPlanes.add(pnlPlanPro);

        pnlFondo.add(pnlPlanes, java.awt.BorderLayout.CENTER);

        pnlNavegacion.setOpaque(false);
        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGratis;
    private javax.swing.JButton btnPro;
    private javax.swing.JLabel lblAlMes;
    private javax.swing.JLabel lblBeneficiosGratis;
    private javax.swing.JLabel lblBeneficiosPro;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblNombreGratis;
    private javax.swing.JLabel lblNombrePro;
    private javax.swing.JLabel lblPrecioGratis;
    private javax.swing.JLabel lblPrecioPro;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlBotonPro;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlGratisEncabezado;
    private javax.swing.JPanel pnlGratisPie;
    private javax.swing.JPanel pnlMensajes;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPlanGratis;
    private javax.swing.JPanel pnlPlanPro;
    private javax.swing.JPanel pnlPlanes;
    private javax.swing.JPanel pnlPrecioPro;
    private javax.swing.JPanel pnlProEncabezado;
    private javax.swing.JPanel pnlProPie;
    // End of variables declaration//GEN-END:variables
}
