package com.cineplus.presentacion.formularios;

import com.cineplus.dto.PlanDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Pantalla "Seleccion de Membresia": CinePLUS+ (gratis) y CinePLUS Pro ($129 al mes).
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
        Tema.titulo(lblTitulo);
        lblSubtitulo.setForeground(Tema.MUTED);

        Tema.tarjeta(pnlPlanGratis);
        Tema.tarjeta(pnlPlanPro);
        // Los paneles internos dejan ver el fondo de la tarjeta
        pnlGratisEncabezado.setOpaque(false);
        pnlProEncabezado.setOpaque(false);
        estiloPlan(lblNombreGratis, lblPrecioGratis, lblBeneficiosGratis);
        estiloPlan(lblNombrePro, lblPrecioPro, lblBeneficiosPro);

        Tema.botonSecundario(btnGratis);
        btnGratis.setEnabled(false);
        Tema.botonPrimario(btnPro);
        btnPro.setEnabled(false);

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
    }

    private static void estiloPlan(JLabel nombre, JLabel precio, JLabel beneficios) {
        nombre.setFont(nombre.getFont().deriveFont(Font.BOLD, 22f));
        nombre.setForeground(Tema.TEXTO);
        precio.setFont(precio.getFont().deriveFont(Font.BOLD, 16f));
        precio.setForeground(Tema.TEXTO);
        beneficios.setForeground(Tema.MUTED);
    }

    /**
     * Dos tarjetas: CinePLUS+ con "¡Ya lo tienes!" y Pro con su precio y CONTINUAR,
     * o "Ya eres miembro" deshabilitado si yaEsPro.
     */
    public void mostrarPlanes(List<PlanDTO> planes, boolean yaEsPro) {
        for (PlanDTO plan : planes) {
            if (plan.gratuito()) {
                lblNombreGratis.setText(plan.nombre());
                lblPrecioGratis.setText("GRATIS");
                lblBeneficiosGratis.setText(beneficios(plan.beneficios()));
            } else {
                planPro = plan;
                lblNombrePro.setText(plan.nombre());
                lblPrecioPro.setText("$" + precio(plan.precioMensual()) + " AL MES");
                lblBeneficiosPro.setText(beneficios(plan.beneficios()));
            }
        }
        btnGratis.setText("¡Ya lo tienes!");
        puedeContratar = planPro != null && !yaEsPro;
        btnPro.setText(yaEsPro ? "Ya eres miembro" : "CONTINUAR");
        btnPro.setEnabled(puedeContratar);
    }

    /** Texto con ajuste de linea y una vineta por enunciado. */
    private static String beneficios(String texto) {
        if (texto == null || texto.isBlank()) {
            return " ";
        }
        StringBuilder html = new StringBuilder("<html><body style='width: 300px'>");
        for (String parte : texto.split("\\.\\s*")) {
            if (!parte.isBlank()) {
                html.append("• ").append(Tema.escapar(parte.strip())).append(".<br><br>");
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
        pnlEncabezado = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        pnlMensajes = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();
        pnlPlanes = new javax.swing.JPanel();
        pnlPlanGratis = new javax.swing.JPanel();
        pnlGratisEncabezado = new javax.swing.JPanel();
        lblNombreGratis = new javax.swing.JLabel();
        lblPrecioGratis = new javax.swing.JLabel();
        lblBeneficiosGratis = new javax.swing.JLabel();
        btnGratis = new javax.swing.JButton();
        pnlPlanPro = new javax.swing.JPanel();
        pnlProEncabezado = new javax.swing.JPanel();
        lblNombrePro = new javax.swing.JLabel();
        lblPrecioPro = new javax.swing.JLabel();
        lblBeneficiosPro = new javax.swing.JLabel();
        btnPro = new javax.swing.JButton();
        pnlNavegacion = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        pnlEncabezado.setLayout(new java.awt.BorderLayout(0, 6));

        lblTitulo.setText("Selección de Membresía");
        pnlEncabezado.add(lblTitulo, java.awt.BorderLayout.NORTH);
        lblSubtitulo.setText("Elige el plan que más te convenga.");
        pnlEncabezado.add(lblSubtitulo, java.awt.BorderLayout.CENTER);
        pnlMensajes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));

        lblEstado.setText(" ");
        pnlMensajes.add(lblEstado);
        lblError.setText(" ");
        pnlMensajes.add(lblError);
        pnlEncabezado.add(pnlMensajes, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlEncabezado, java.awt.BorderLayout.NORTH);

        pnlPlanes.setLayout(new java.awt.GridLayout(1, 2, 24, 0));

        pnlPlanGratis.setLayout(new java.awt.BorderLayout(0, 12));

        pnlGratisEncabezado.setLayout(new java.awt.GridLayout(2, 1, 0, 4));

        lblNombreGratis.setText("CinePLUS+");
        pnlGratisEncabezado.add(lblNombreGratis);
        lblPrecioGratis.setText("GRATIS");
        pnlGratisEncabezado.add(lblPrecioGratis);
        pnlPlanGratis.add(pnlGratisEncabezado, java.awt.BorderLayout.NORTH);

        lblBeneficiosGratis.setText("Beneficios");
        lblBeneficiosGratis.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        pnlPlanGratis.add(lblBeneficiosGratis, java.awt.BorderLayout.CENTER);
        btnGratis.setText("¡Ya lo tienes!");
        pnlPlanGratis.add(btnGratis, java.awt.BorderLayout.SOUTH);
        pnlPlanes.add(pnlPlanGratis);

        pnlPlanPro.setLayout(new java.awt.BorderLayout(0, 12));

        pnlProEncabezado.setLayout(new java.awt.GridLayout(2, 1, 0, 4));

        lblNombrePro.setText("CinePLUS Pro");
        pnlProEncabezado.add(lblNombrePro);
        lblPrecioPro.setText("$129 AL MES");
        pnlProEncabezado.add(lblPrecioPro);
        pnlPlanPro.add(pnlProEncabezado, java.awt.BorderLayout.NORTH);

        lblBeneficiosPro.setText("Beneficios");
        lblBeneficiosPro.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        pnlPlanPro.add(lblBeneficiosPro, java.awt.BorderLayout.CENTER);
        btnPro.setText("CONTINUAR");
        pnlPlanPro.add(btnPro, java.awt.BorderLayout.SOUTH);
        pnlPlanes.add(pnlPlanPro);

        pnlFondo.add(pnlPlanes, java.awt.BorderLayout.CENTER);

        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);


        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGratis;
    private javax.swing.JButton btnPro;
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
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlGratisEncabezado;
    private javax.swing.JPanel pnlMensajes;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPlanGratis;
    private javax.swing.JPanel pnlPlanPro;
    private javax.swing.JPanel pnlPlanes;
    private javax.swing.JPanel pnlProEncabezado;
    // End of variables declaration//GEN-END:variables
}
